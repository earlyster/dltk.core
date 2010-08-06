package org.eclipse.dltk.launching;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.dltk.core.environment.IDeployment;
import org.eclipse.dltk.core.environment.IExecutionEnvironment;
import org.eclipse.dltk.core.environment.IFileHandle;
import org.eclipse.dltk.launching.IScriptProcessHandler.ScriptResult;

/**
 * Utility class which may be used to execute a script, or perform an
 * interpreter action on a script, such as compilation.
 * 
 * @see IScriptProcessHandler
 */
public class InternalScriptExecutor {

	public interface IInternalScriptDeployer {
		/**
		 * Deploy the internal script to be executed.
		 */
		IPath deployScript(IDeployment deployment) throws IOException;
	}

	private IScriptProcessHandler handler;
	private IInterpreterInstall install;

	public InternalScriptExecutor(IInterpreterInstall install,
			IScriptProcessHandler handler) {
		Assert.isNotNull(
				install,
				Messages.InternalScriptExecutor_iInterpreterInstallMustNotBeNull);
		Assert.isNotNull(handler,
				Messages.InternalScriptExecutor_iProcessHandlerMustNotBeNull);

		this.install = install;
		this.handler = handler;
	}

	/**
	 * Execute a script.
	 * 
	 * @param deployer
	 *            implementation of <code>IInternalScriptDeployer</code> to
	 *            deploy the script being executed.
	 * 
	 * @param interpreterArgs
	 *            command line arguments for the interpreter, may be
	 *            <code>null</code>
	 * 
	 * @param scriptArgs
	 *            command line arguments for the script, may be
	 *            <code>null</code>
	 * @param stdin
	 *            stdin to pass to script, may be <code>null</code>
	 * 
	 * @throws CoreException
	 *             if there was an error handling the process
	 * @throws IOException
	 *             if there was an error deploying the script
	 */
	public ScriptResult execute(IInternalScriptDeployer deployer,
			String[] interpreterArgs, String[] scriptArgs, char[] stdin)
			throws CoreException, IOException {
		IExecutionEnvironment execEnv = install.getExecEnvironment();

		IDeployment deployment = execEnv.createDeployment();
		if (deployment == null) {
			throw new IOException(
					"Failed to deploy script. Connection to environment are not established.");
		}
		IPath deploymentPath = deployer.deployScript(deployment);

		try {
			IFileHandle interpreter = install.getInstallLocation();
			IFileHandle script = deployment.getFile(deploymentPath);

			String[] cmdLine = buildCommandLine(interpreter, interpreterArgs,
					script, scriptArgs);

			Process process = execEnv.exec(cmdLine, null, null);
			ScriptResult result = handler.handle(process, stdin);

			return result;
		} finally {
			deployment.dispose();
		}
	}

	/**
	 * Execute an interpreter action.
	 * 
	 * @param interpreterArgs
	 *            command line arguments for the interpreter, may be
	 *            <code>null</code>
	 * @param stdin
	 *            stdin to pass to script, may be <code>null</code>
	 * 
	 * @throws CoreException
	 *             if there was an error handling the process
	 * 
	 * @throws CoreException
	 */
	public ScriptResult execute(String[] interpreterArgs, char[] stdin)
			throws CoreException {
		IExecutionEnvironment execEnv = install.getExecEnvironment();
		IFileHandle interpreter = install.getInstallLocation();

		String[] cmdLine = buildCommandLine(interpreter, interpreterArgs, null,
				null);

		Process process = execEnv.exec(cmdLine, null, null);
		return handler.handle(process, stdin);
	}

	private void addArgs(List<String> list, String[] args) {
		if (args != null) {
			for (int i = 0; i < args.length; i++) {
				list.add(args[i]);
			}
		}
	}

	private String[] buildCommandLine(IFileHandle interpreter,
			String[] interpreterArgs, IFileHandle script, String[] scriptArgs) {
		List<String> cmdLine = new ArrayList<String>();

		cmdLine.add(interpreter.getCanonicalPath());
		addArgs(cmdLine, interpreterArgs);

		if (script != null) {
			cmdLine.add(script.getCanonicalPath());
		}

		addArgs(cmdLine, scriptArgs);

		return cmdLine.toArray(new String[cmdLine.size()]);
	}
}
