package org.eclipse.dltk.internal.debug.core.model;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.ISuspendResume;
import org.eclipse.debug.core.model.ITerminate;
import org.eclipse.dltk.dbgp.IDbgpThreadAcceptor;
import org.eclipse.dltk.debug.core.model.IScriptDebugThreadConfigurator;
import org.eclipse.dltk.debug.core.model.IScriptThread;
import org.eclipse.dltk.internal.debug.core.model.operations.DbgpDebugger;

public interface IScriptThreadManager extends IDbgpThreadAcceptor, ITerminate,
		ISuspendResume {

	// Listener
	void addListener(IScriptThreadManagerListener listener);

	void removeListener(IScriptThreadManagerListener listener);

	// Thread management
	boolean hasThreads();

	IScriptThread[] getThreads();

	void terminateThread(IScriptThread thread);

	boolean isWaitingForThreads();

	void sendTerminationRequest() throws DebugException;

	public void refreshThreads();

	/**
	 * Used to configure thread with additional DBGp features, etc.
	 */
	void configureThread(DbgpDebugger engine, ScriptThread scriptThread);

	public void setScriptThreadConfigurator(
			IScriptDebugThreadConfigurator configurator);

	void initializeBreakpoints(IScriptThread thread);
}
