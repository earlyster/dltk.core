package org.eclipse.dltk.testing;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.environment.IEnvironment;
import org.eclipse.dltk.launching.InterpreterConfig;

public interface ITestingEngine extends IAdaptable {
	String getId();

	String getName();

	IStatus validateSourceModule(ISourceModule module);

	IStatus validateContainer(IModelElement element);

	void configureLaunch(InterpreterConfig config,
			ILaunchConfiguration configuration, ILaunch launch)
			throws CoreException;

	/**
	 * @param configuration
	 * @param scriptEnvironment
	 * @return
	 * @throws CoreException
	 */
	String getContainerLauncher(ILaunchConfiguration configuration,
			IEnvironment scriptEnvironment) throws CoreException;

	ITestRunnerUI getTestRunnerUI(IScriptProject project,
			ILaunchConfiguration configuration);
}
