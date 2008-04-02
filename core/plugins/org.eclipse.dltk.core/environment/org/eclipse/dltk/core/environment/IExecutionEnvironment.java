package org.eclipse.dltk.core.environment;

import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;

public interface IExecutionEnvironment {
	Map getEnvironmentVariables();

	IDeployment createDeployment();

	Process exec(String[] cmdLine, IPath workingDir, String[] environment)
			throws CoreException;

	IEnvironment getEnvironment();
}
