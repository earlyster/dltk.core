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
	 * Get the path of the main script to be used in the launch configuration.
	 * Return the special launcher path for this testing engine or
	 * <code>null</code> to use the script specified in the launch
	 * configuration.
	 * 
	 * @param configuration
	 * @param scriptEnvironment
	 * @return
	 * @throws CoreException
	 */
	String getMainScriptPath(ILaunchConfiguration configuration,
			IEnvironment scriptEnvironment) throws CoreException;

	ITestRunnerUI getTestRunnerUI(IScriptProject project,
			ILaunchConfiguration configuration);
}
