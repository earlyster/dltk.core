package org.eclipse.dltk.launching;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.dltk.compiler.util.Util;
import org.eclipse.dltk.core.PreferencesLookupDelegate;
import org.eclipse.dltk.debug.core.IDbgpService;
import org.eclipse.dltk.debug.core.model.IScriptDebugTarget;
import org.eclipse.dltk.internal.debug.core.model.RemoteScriptDebugTarget;

public abstract class RemoteDebuggingEngineRunner extends DebuggingEngineRunner {

	public RemoteDebuggingEngineRunner(IInterpreterInstall install) {
		super(install);
	}

	protected IScriptDebugTarget createDebugTarget(ILaunch launch,
			IDbgpService dbgpService) throws CoreException {
		return new RemoteScriptDebugTarget(getDebugModelId(), dbgpService,
				getSessionId(launch.getLaunchConfiguration()), launch, null);
	}

	/*
	 * @see DebuggingEngineRunner#getSessionId(ILaunchConfiguration)
	 */
	protected String getSessionId(ILaunchConfiguration configuration)
			throws CoreException {
		return configuration.getAttribute(
				ScriptLaunchConfigurationConstants.ATTR_DLTK_DBGP_SESSION_ID,
				Util.EMPTY_STRING);
	}

	/*
	 * @see DebuggingEngineRunner#addEngineConfig(InterpreterConfig,
	 * IScriptProject,ILaunch)
	 */
	protected InterpreterConfig addEngineConfig(InterpreterConfig config,
			PreferencesLookupDelegate delegate, ILaunch launch) {
		return config;
	}

	/*
	 * @see DebuggingEngineRunner#run(InterpreterConfig, ILaunch,
	 * IProgressMonitor)
	 */
	public void run(InterpreterConfig config, ILaunch launch,
			IProgressMonitor monitor) throws CoreException {
		try {
			initializeLaunch(launch, config,
					createPreferencesLookupDelegate(launch));
			waitDebuggerConnected(null, launch, monitor);
		} catch (CoreException e) {
			launch.terminate();
			throw e;
		}
	}

	/*
	 * @see DebuggingEngineRunner#getDebuggingEngineId()
	 */
	protected String getDebuggingEngineId() {
		return null;
	}

	/*
	 * @see DebuggingEngineRunner#getDebuggingEnginePreferenceQualifier()
	 */
	protected String getDebuggingEnginePreferenceQualifier() {
		return getDebugPreferenceQualifier();
	}

	/*
	 * @see DebuggingEngineRunner#getLoggingEnabledPreferenceKey()
	 */
	protected String getLoggingEnabledPreferenceKey() {
		// not supported on the client side
		return null;
	}

	/*
	 * @see DebuggingEngineRunner#getLogFileNamePreferenceKey()
	 */
	protected String getLogFileNamePreferenceKey() {
		// not supported on the client side
		return null;
	}

	/*
	 * @see DebuggingEngineRunner#getLogFilePathPreferenceKey()
	 */
	protected String getLogFilePathPreferenceKey() {
		// not supported on the client side
		return null;
	}
}
