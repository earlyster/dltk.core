package org.eclipse.dltk.launching;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;

/**
 * Base class for remote launch configuration delegates.
 */
public abstract class AbstractRemoteLaunchConfigurationDelegate extends
		AbstractScriptLaunchConfigurationDelegate {

	@Override
	protected InterpreterConfig createInterpreterConfig(
			ILaunchConfiguration configuration, ILaunch launch)
			throws CoreException {
		return new InterpreterConfig();
	}

	/**
	 * Returns the remote engine runner.
	 */
	protected abstract RemoteDebuggingEngineRunner getDebuggingRunner(
			IInterpreterInstall install);

	@Override
	public IInterpreterRunner getInterpreterRunner(
			ILaunchConfiguration configuration, String mode)
			throws CoreException {
		IInterpreterInstall install = verifyInterpreterInstall(configuration);
		return getDebuggingRunner(install);
	}

	@Override
	protected void validateLaunchConfiguration(
			ILaunchConfiguration configuration, String mode, IProject project)
			throws CoreException {
		// nothing to validate
	}

}
