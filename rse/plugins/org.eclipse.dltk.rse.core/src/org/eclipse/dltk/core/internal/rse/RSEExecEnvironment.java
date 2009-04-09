package org.eclipse.dltk.core.internal.rse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.environment.IDeployment;
import org.eclipse.dltk.core.environment.IEnvironment;
import org.eclipse.dltk.core.environment.IExecutionEnvironment;
import org.eclipse.dltk.core.environment.IExecutionLogger;
import org.eclipse.dltk.core.internal.rse.perfomance.RSEPerfomanceStatistics;
import org.eclipse.dltk.internal.launching.execution.EFSDeployment;
import org.eclipse.osgi.util.NLS;
import org.eclipse.rse.core.model.IHost;
import org.eclipse.rse.core.subsystems.ISubSystem;
import org.eclipse.rse.internal.efs.RSEFileSystem;
import org.eclipse.rse.services.clientserver.messages.SystemMessageException;
import org.eclipse.rse.services.shells.IHostShell;
import org.eclipse.rse.services.shells.IShellService;
import org.eclipse.rse.subsystems.shells.core.subsystems.servicesubsystem.IShellServiceSubSystem;

public class RSEExecEnvironment implements IExecutionEnvironment {

	private static final String SHELL_PATH = "exec /bin/sh"; //$NON-NLS-1$

	private static final String CMD_SEPARATOR = " ;"; //$NON-NLS-1$
	private static final String EXPORT_CMD = "export "; //$NON-NLS-1$
	private final static String EXIT_CMD = "exit"; //$NON-NLS-1$
	private static final String SET_CMD = "set"; //$NON-NLS-1$

	private final RSEEnvironment environment;
	private static int counter = -1;

	private static final Map hostToEnvironment = new HashMap();

	public RSEExecEnvironment(RSEEnvironment env) {
		this.environment = env;
	}

	public IDeployment createDeployment() {
		if (RSEPerfomanceStatistics.PERFOMANCE_TRACING) {
			RSEPerfomanceStatistics
					.inc(RSEPerfomanceStatistics.DEPLOYMENTS_CREATED);
		}
		try {
			String tmpDir = getTempDir();
			if (tmpDir != null) {
				String rootPath = tmpDir + environment.getSeparator()
						+ getTempName("dltk", ".tmp"); //$NON-NLS-1$ //$NON-NLS-2$
				URI rootUri = createRemoteURI(environment.getHost(), rootPath);
				return new EFSDeployment(environment, rootUri);
			}
		} catch (CoreException e) {
			if (DLTKCore.DEBUG)
				e.printStackTrace();
		}

		return null;
	}

	private URI createRemoteURI(IHost host, String rootPath) {
		return RSEFileSystem.getURIFor(host.getHostName(), rootPath);
	}

	private IShellServiceSubSystem getShellServiceSubSystem(IHost host) {
		ISubSystem[] subsys = host.getSubSystems();
		for (int i = 0; i < subsys.length; i++) {
			if (subsys[i] instanceof IShellServiceSubSystem)
				return (IShellServiceSubSystem) subsys[i];
		}
		return null;
	}

	private String getTempName(String prefix, String suffix) {
		if (counter == -1) {
			counter = new Random().nextInt() & 0xffff;
		}
		counter++;
		return prefix + Integer.toString(counter) + suffix;
	}

	private String getTempDir() {
		IHost host = environment.getHost();
		IShellServiceSubSystem system = getShellServiceSubSystem(host);

		if (system == null) {
			DLTKRSEPlugin.logWarning(NLS.bind(
					Messages.RSEExecEnvironment_hostNotFound, host.getName()));
			return null;
		}

		String tmpDir = null;
		try {
			system.connect(new NullProgressMonitor(), false);

			tmpDir = system.getConnectorService().getTempDirectory();
			if (tmpDir.length() == 0) {
				tmpDir = "/tmp"; //$NON-NLS-1$
			}

		} catch (Exception e) {
			if (DLTKCore.DEBUG) {
				e.printStackTrace();
			}
		}

		return tmpDir;
	}

	public Process exec(String[] cmdLine, IPath workingDir, String[] environment)
			throws CoreException {
		return exec(cmdLine, workingDir, environment, null);
	}

	public Process exec(String[] cmdLine, IPath workingDir,
			String[] environment, IExecutionLogger logger) throws CoreException {
		if (RSEPerfomanceStatistics.PERFOMANCE_TRACING) {
			RSEPerfomanceStatistics
					.inc(RSEPerfomanceStatistics.EXECUTION_COUNT);
		}
		long start = System.currentTimeMillis();
		IShellServiceSubSystem shell = getShellServiceSubSystem(this.environment
				.getHost());
		try {
			shell.connect(null, false);
		} catch (Exception e) {
			if (DLTKCore.DEBUG) {
				e.printStackTrace();
			}
			return null;
		}

		if (!shell.isConnected()) {
			return null;
		}
		IShellService shellService = shell.getShellService();
		IHostShell hostShell = null;
		String workingDirectory = null;
		if (workingDir != null) {
			workingDirectory = this.environment.convertPathToString(workingDir);
		} else {
			workingDirectory = "/"; //$NON-NLS-1$
		}
		try {
			hostShell = shellService.runCommand(workingDirectory, SHELL_PATH,
					environment, new NullProgressMonitor());
		} catch (SystemMessageException e1) {
			DLTKRSEPlugin.log(e1);
			return null;
		}

		// Sometimes environment variables aren't set, so use export.
		if (environment != null) {
			hostShell.writeToShell(SHELL_PATH);
			// TODO: Skip environment variables what is already in shell.
			for (int i = 0; i < environment.length; i++) {
				hostShell.writeToShell(EXPORT_CMD
						+ toShellArguments(environment[i]));
			}
		}
		final String pattern = "DLTK_INITIAL_PREFIX_EXECUTION_STRING:" //$NON-NLS-1$
				+ String.valueOf(System.currentTimeMillis());
		final String echoPattern = "echo \"" + pattern + "\""; //$NON-NLS-1$ //$NON-NLS-2$
		hostShell.writeToShell(echoPattern + CMD_SEPARATOR
				+ buildCommand(cmdLine) + CMD_SEPARATOR + echoPattern
				+ CMD_SEPARATOR + EXIT_CMD);
		Process p = null;
		try {
			p = new MyHostShellProcessAdapter(hostShell, pattern, logger);
		} catch (Exception e) {
			if (p != null) {
				p.destroy();
			}
			throw new RuntimeException("Failed to run remote command"); //$NON-NLS-1$
		}
		if (RSEPerfomanceStatistics.PERFOMANCE_TRACING) {
			final long end = System.currentTimeMillis();
			RSEPerfomanceStatistics.inc(RSEPerfomanceStatistics.EXECUTION_TIME,
					(end - start));
		}
		return p;
	}

	private String toShellArguments(String cmd) {
		String replaceAll = cmd.replaceAll(" ", "\\\\ "); //$NON-NLS-1$ //$NON-NLS-2$
		return replaceAll;
	}

	// private String createWorkingDir(IPath workingDir) {
	// if (workingDir == null)
	//			return "."; //$NON-NLS-1$
	// return workingDir.toPortableString();
	// }

	private String buildCommand(String[] cmdLine) {
		StringBuffer cmd = new StringBuffer();
		for (int i = 0; i < cmdLine.length; i++) {
			if (i != 0) {
				cmd.append(" "); //$NON-NLS-1$
			}
			cmd.append(cmdLine[i]);
		}
		return cmd.toString();
	}

	public Map getEnvironmentVariables(boolean realyNeed) {
		if (!realyNeed) {
			return new HashMap();
		}
		final long start = System.currentTimeMillis();
		synchronized (hostToEnvironment) {
			final Map result = (Map) hostToEnvironment.get(environment
					.getHost());
			if (result != null) {
				return new HashMap(result);
			}
		}
		final Map result = new HashMap();
		try {
			Process process = this.exec(new String[] { SET_CMD }, Path.EMPTY,
					null);
			if (process != null) {
				final BufferedReader input = new BufferedReader(
						new InputStreamReader(process.getInputStream()));
				Thread t = new Thread(new Runnable() {
					public void run() {
						try {
							while (true) {
								String line = input.readLine();
								if (line == null) {
									break;
								}
								line = line.trim();
								int pos = line.indexOf("="); //$NON-NLS-1$
								if (pos != -1) {
									String varName = line.substring(0, pos);
									String varValue = line.substring(pos + 1);
									result.put(varName, varValue);
								}
							}
						} catch (IOException e) {
							DLTKRSEPlugin.log(e);
						}
					}
				});
				t.start();
				try {
					t.join(25000);// No more than 25 seconds
				} catch (InterruptedException e) {
					DLTKRSEPlugin.log(e);
				}
				process.destroy();
			}
		} catch (CoreException e) {
			DLTKRSEPlugin.log(e);
		}
		if (!result.isEmpty()) {
			synchronized (hostToEnvironment) {
				hostToEnvironment.put(this.environment.getHost(), Collections
						.unmodifiableMap(result));
			}
		}
		if (RSEPerfomanceStatistics.PERFOMANCE_TRACING) {
			final long end = System.currentTimeMillis();
			RSEPerfomanceStatistics
					.inc(RSEPerfomanceStatistics.ENVIRONMENT_RECEIVE_COUNT);
			RSEPerfomanceStatistics.inc(
					RSEPerfomanceStatistics.ENVIRONMENT_RECEIVE_TIME,
					(end - start));
		}
		return result;
	}

	public IEnvironment getEnvironment() {
		return environment;
	}

	public boolean isValidExecutableAndEquals(String possibleName, IPath path) {
		if (environment.getHost().getSystemType().isWindows()) {
			possibleName = possibleName.toLowerCase();
			String fName = path.removeFileExtension().toString().toLowerCase();
			String ext = path.getFileExtension();
			if (possibleName.equals(fName)
					&& ("exe".equalsIgnoreCase(ext) || "bat".equalsIgnoreCase(ext))) { //$NON-NLS-1$ //$NON-NLS-2$
				return true;
			}
		} else {
			String fName = path.lastSegment();
			if (fName.equals(possibleName)) {
				return true;
			}
		}
		return false;
	}
}
