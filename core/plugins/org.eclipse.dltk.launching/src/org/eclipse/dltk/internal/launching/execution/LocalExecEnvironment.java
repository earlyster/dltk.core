package org.eclipse.dltk.internal.launching.execution;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.Random;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.environment.IDeployment;
import org.eclipse.dltk.core.environment.IEnvironment;
import org.eclipse.dltk.core.environment.IExecutionEnvironment;
import org.eclipse.dltk.core.environment.IExecutionLogger;
import org.eclipse.dltk.core.internal.environment.LocalEnvironment;

public class LocalExecEnvironment implements IExecutionEnvironment {
	private static IPath temp;
	private static int counter = -1;

	public IDeployment createDeployment() {
		try {
			IPath rootPath = getTempDirPath().append(
					getTempName("dltk", ".tmp")); //$NON-NLS-1$ //$NON-NLS-2$
			URI rootUri = createLocalURI(rootPath);
			return new EFSDeployment(LocalEnvironment.getInstance(), rootUri);
		} catch (CoreException e) {
			if (DLTKCore.DEBUG) {
				e.printStackTrace();
			}
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

	private URI createLocalURI(IPath path) {
		return EFS.getLocalFileSystem().getStore(path).toURI();
	}

	private static IPath getTempDirPath() {
		if (temp == null) {
			try {
				File tempFile = File.createTempFile("dltk", "temp"); //$NON-NLS-1$ //$NON-NLS-2$
				tempFile.delete();
				temp = new Path(tempFile.getParent());
			} catch (IOException e) {
				throw new RuntimeException(
						Messages.LocalExecEnvironment_failedToLocateTempFolder);
			}
		}
		return temp;
	}

	public Process exec(String[] cmdLine, IPath workingDir, String[] environment)
			throws CoreException {
		File workingDirFile = null;
		if (workingDir != null) {
			workingDirFile = workingDir.toFile();
		}
		return DebugPlugin.exec(cmdLine, workingDirFile, environment);
	}

	public Process exec(String[] cmdLine, IPath workingDir,
			String[] environment, IExecutionLogger logger) throws CoreException {
		return exec(cmdLine, workingDir, environment);
	}

	public Map getEnvironmentVariables(boolean value) {
		return DebugPlugin.getDefault().getLaunchManager()
				.getNativeEnvironmentCasePreserved();
	}

	public IEnvironment getEnvironment() {
		return LocalEnvironment.getInstance();
	}

	public boolean isValidExecutableAndEquals(String possibleName, IPath path) {
		if (Platform.getOS().equals(Platform.OS_WIN32)) {
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

	/**
	 * @since 2.0
	 */
	public boolean isSafeEnvironmentVariable(String envVarName) {
		return true;
	}
}
