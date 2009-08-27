package org.eclipse.dltk.launching;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.dltk.core.PreferencesLookupDelegate;
import org.eclipse.dltk.core.environment.EnvironmentPathUtils;
import org.eclipse.dltk.core.environment.IEnvironment;
import org.eclipse.dltk.core.environment.IFileHandle;
import org.eclipse.dltk.internal.launching.InterpreterMessages;
import org.eclipse.dltk.utils.PlatformFileUtils;

public abstract class ExternalDebuggingEngineRunner extends
		DebuggingEngineRunner {

	public ExternalDebuggingEngineRunner(IInterpreterInstall install) {
		super(install);
	}

	@Override
	protected final InterpreterConfig addEngineConfig(InterpreterConfig config,
			PreferencesLookupDelegate delegate, ILaunch launch)
			throws CoreException {

		final IFileHandle file = getDebuggingEnginePath(delegate);

		// Checking debugging engine path
		if (file == null || file.toString().length() == 0) {
			abort(
					InterpreterMessages.errDebuggingEnginePathNotSpecified,
					null,
					ScriptLaunchConfigurationConstants.ERR_DEBUGGING_ENGINE_NOT_CONFIGURED);
		} else if (!file.isFile()) {
			abort(
					InterpreterMessages.errDebuggingEnginePathInvalid,
					null,
					ScriptLaunchConfigurationConstants.ERR_DEBUGGING_ENGINE_NOT_CONFIGURED);
		}

		return alterConfig(config, delegate);
	}

	/**
	 * Returns the preference key used to store the external debugging engine
	 * path.
	 */
	protected abstract String getDebuggingEnginePreferenceKey();

	// /**
	// * Returns the id of the plugin whose preference store that contains the
	// * debugging engine path.
	// */
	// protected abstract String getDebuggingEnginePreferenceQualifier();

	protected IFileHandle getDebuggingEnginePath(
			PreferencesLookupDelegate delegate) {
		IEnvironment env = this.getInstall().getEnvironment();
		String key = getDebuggingEnginePreferenceKey();
		String qualifier = getDebuggingEnginePreferenceQualifier();

		String pathKeyValue = delegate.getString(qualifier, key);
		String path = EnvironmentPathUtils.decodePaths(pathKeyValue).get(env);
		if (path != null && path.length() != 0) {
			return PlatformFileUtils.findAbsoluteOrEclipseRelativeFile(env,
					new Path(path));
		}

		return null;
	}

	protected String getDebuggingPreference(PreferencesLookupDelegate delegate,
			String key) {
		String qualifier = getDebuggingEnginePreferenceQualifier();
		return delegate.getString(qualifier, key);
	}

	protected abstract InterpreterConfig alterConfig(InterpreterConfig config,
			PreferencesLookupDelegate delegate) throws CoreException;
}
