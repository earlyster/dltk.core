/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.eclipse.dltk.internal.debug.core.model;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.model.IThread;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.dbgp.IDbgpFeature;
import org.eclipse.dltk.dbgp.IDbgpSession;
import org.eclipse.dltk.dbgp.IDbgpStreamFilter;
import org.eclipse.dltk.dbgp.IDbgpStreamListener;
import org.eclipse.dltk.dbgp.breakpoints.IDbgpBreakpoint;
import org.eclipse.dltk.dbgp.breakpoints.IDbgpLineBreakpoint;
import org.eclipse.dltk.dbgp.commands.IDbgpFeatureCommands;
import org.eclipse.dltk.dbgp.exceptions.DbgpException;
import org.eclipse.dltk.debug.core.DLTKDebugPlugin;
import org.eclipse.dltk.debug.core.DebugOption;
import org.eclipse.dltk.debug.core.IDebugOptions;
import org.eclipse.dltk.debug.core.model.IScriptDebugThreadConfigurator;
import org.eclipse.dltk.debug.core.model.IScriptStackFrame;
import org.eclipse.dltk.debug.core.model.IScriptThread;
import org.eclipse.dltk.internal.debug.core.model.operations.DbgpDebugger;

public class ScriptThreadManager implements IScriptThreadManager,
		IDbgpStreamListener {
	private static final boolean DEBUG = DLTKCore.DEBUG;

	private IScriptDebugThreadConfigurator configurator = null;

	// Helper methods
	private interface IThreadBoolean {
		boolean get(IThread thread);
	}

	private boolean getThreadBoolean(IThreadBoolean b) {
		synchronized (threads) {
			IThread[] ths = getThreads();

			if (ths.length == 0) {
				return false;
			}

			for (int i = 0; i < ths.length; ++i) {
				if (!b.get(ths[i])) {
					return false;
				}
			}

			return true;
		}
	}

	private final ListenerList listeners = new ListenerList(
			ListenerList.IDENTITY);

	private final List<IScriptThread> threads = new ArrayList<IScriptThread>();

	private volatile boolean waitingForThreads = true;

	private final ScriptDebugTarget target;

	protected void fireThreadAccepted(IScriptThread thread, boolean first) {
		Object[] list = listeners.getListeners();
		for (int i = 0; i < list.length; ++i) {
			((IScriptThreadManagerListener) list[i]).threadAccepted(thread,
					first);
		}
	}

	protected void fireAllThreadsTerminated() {
		Object[] list = listeners.getListeners();
		for (int i = 0; i < list.length; ++i) {
			((IScriptThreadManagerListener) list[i]).allThreadsTerminated();
		}
	}

	public void addListener(IScriptThreadManagerListener listener) {
		listeners.add(listener);
	}

	public void removeListener(IScriptThreadManagerListener listener) {
		listeners.remove(listener);
	}

	public boolean isWaitingForThreads() {
		return waitingForThreads;
	}

	public boolean hasThreads() {
		synchronized (threads) {
			return !threads.isEmpty();
		}
	}

	public IScriptThread[] getThreads() {
		synchronized (threads) {
			return threads.toArray(new IScriptThread[threads.size()]);
		}
	}

	public ScriptThreadManager(ScriptDebugTarget target) {
		if (target == null) {
			throw new IllegalArgumentException();
		}

		this.target = target;
	}

	private IDbgpStreamFilter[] streamFilters = null;

	/**
	 * @param data
	 * @param stdout
	 * @return
	 */
	private String filter(String data, int stream) {
		if (streamFilters != null) {
			for (int i = 0; i < streamFilters.length; ++i) {
				data = streamFilters[i].filter(data, stream);
				if (data == null) {
					return null;
				}
			}
		}
		return data;
	}

	public void stdoutReceived(String data) {
		final IScriptStreamProxy proxy = target.getStreamProxy();
		if (proxy != null) {
			data = filter(data, IDbgpStreamFilter.STDOUT);
			if (data != null) {
				proxy.writeStdout(data);
			}
		}
		if (DEBUG) {
			System.out.println("Received (stdout): " + data); //$NON-NLS-1$
		}
	}

	public void stderrReceived(String data) {
		final IScriptStreamProxy proxy = target.getStreamProxy();
		if (proxy != null) {
			data = filter(data, IDbgpStreamFilter.STDERR);
			if (data != null) {
				proxy.writeStderr(data);
			}
		}
		if (DEBUG) {
			System.out.println("Received (stderr): " + data); //$NON-NLS-1$
		}
	}

	void setStreamFilters(IDbgpStreamFilter[] streamFilters) {
		this.streamFilters = streamFilters;
	}

	/**
	 * Tests if the specified thread has breakpoint at the same line
	 * 
	 * @param thread
	 * @return
	 */
	private static boolean hasBreakpointAtCurrentPosition(ScriptThread thread) {
		try {
			thread.updateStack();
			if (thread.hasStackFrames()) {
				final IStackFrame top = thread.getTopStackFrame();
				if (top instanceof IScriptStackFrame && top.getLineNumber() > 0) {
					final IScriptStackFrame frame = (IScriptStackFrame) top;
					if (frame.getSourceURI() != null) {
						final String location = frame.getSourceURI().getPath();
						final IDbgpBreakpoint[] breakpoints = thread
								.getDbgpSession().getCoreCommands()
								.getBreakpoints();
						for (int i = 0; i < breakpoints.length; ++i) {
							if (breakpoints[i] instanceof IDbgpLineBreakpoint) {
								final IDbgpLineBreakpoint bp = (IDbgpLineBreakpoint) breakpoints[i];
								if (frame.getLineNumber() == bp.getLineNumber()) {
									try {
										if (new URI(bp.getFilename()).getPath()
												.equals(location)) {
											return true;
										}
									} catch (URISyntaxException e) {
										if (DLTKCore.DEBUG) {
											e.printStackTrace();
										}
									}
								}
							}
						}
					}
				}
			}
		} catch (DebugException e) {
			if (DLTKCore.DEBUG) {
				e.printStackTrace();
			}
		} catch (DbgpException e) {
			if (DLTKCore.DEBUG) {
				e.printStackTrace();
			}
		}
		return false;
	}

	/**
	 * Tests if the specified thread has valid current stack. In some cases it
	 * is better to skip first internal location.
	 * 
	 * @param thread
	 * @return
	 */
	private static boolean isValidStack(ScriptThread thread) {
		final IDebugOptions debugOptions = thread.getDbgpSession()
				.getDebugOptions();
		if (debugOptions.get(DebugOption.ENGINE_VALIDATE_STACK)) {
			thread.updateStack();
			if (thread.hasStackFrames()) {
				return thread.isValidStack();
			}
		}
		return true;
	}

	// IDbgpThreadAcceptor
	public void acceptDbgpThread(IDbgpSession session, IProgressMonitor monitor) {
		SubMonitor sub = SubMonitor.convert(monitor, 100);
		try {
			DbgpException error = session.getInfo().getError();
			if (error != null) {
				throw error;
			}
			session.configure(target.getOptions());
			session.getStreamManager().addListener(this);

			final boolean breakOnFirstLine = target.breakOnFirstLineEnabled()
					|| isAnyThreadInStepInto();
			ScriptThread thread = new ScriptThread(target, session, this);
			thread.initialize(sub.newChild(25));
			addThread(thread);

			final boolean isFirstThread = waitingForThreads;
			if (isFirstThread) {
				waitingForThreads = false;
			}
			if (isFirstThread || !isSupportsThreads(thread)) {
				SubMonitor child = sub.newChild(25);
				target.breakpointManager.initializeSession(
						thread.getDbgpSession(), child);
				child = sub.newChild(25);
				if (configurator != null) {
					configurator.initializeBreakpoints(thread, child);
				}
			}

			DebugEventHelper.fireCreateEvent(thread);

			final boolean stopBeforeCode = thread.getDbgpSession()
					.getDebugOptions().get(DebugOption.ENGINE_STOP_BEFORE_CODE);
			boolean executed = false;
			if (!breakOnFirstLine) {
				if (stopBeforeCode || !hasBreakpointAtCurrentPosition(thread)) {
					thread.resume();
					executed = true;
				}
			} else {
				if (stopBeforeCode || !isValidStack(thread)) {
					thread.initialStepInto();
					executed = true;
				}
			}
			if (!executed) {
				if (!thread.isStackInitialized()) {
					thread.updateStack();
				}
				DebugEventHelper.fireChangeEvent(thread);
				DebugEventHelper.fireSuspendEvent(thread,
						DebugEvent.CLIENT_REQUEST);
			}
			sub.worked(25);
			fireThreadAccepted(thread, isFirstThread);
		} catch (Exception e) {
			try {
				target.terminate();
			} catch (DebugException e1) {
			}
			DLTKDebugPlugin.log(e);
		} finally {
			sub.done();
		}
	}

	private static boolean isSupportsThreads(IScriptThread thread) {
		try {
			final IDbgpFeature feature = thread.getDbgpSession()
					.getCoreCommands()
					.getFeature(IDbgpFeatureCommands.LANGUAGE_SUPPORTS_THREADS);
			return feature != null
					&& IDbgpFeature.ONE_VALUE.equals(feature.getValue());
		} catch (DbgpException e) {
			if (DLTKCore.DEBUG) {
				e.printStackTrace();
			}
			return false;
		}
	}

	private boolean isAnyThreadInStepInto() {
		synchronized (threads) {
			for (Iterator<IScriptThread> i = threads.iterator(); i.hasNext();) {
				ScriptThread thread = (ScriptThread) i.next();
				if (thread.isStepInto()) {
					return true;
				}
			}
		}
		return false;
	}

	private void addThread(ScriptThread thread) {
		synchronized (threads) {
			threads.add(thread);
		}
	}

	public void terminateThread(IScriptThread thread) {
		synchronized (threads) {
			threads.remove(thread);
		}
		DebugEventHelper.fireTerminateEvent(thread);
		final IDbgpSession session = ((ScriptThread) thread).getDbgpSession();
		session.getStreamManager().removeListener(this);
		target.breakpointManager.removeSession(thread.getDbgpSession());
		if (!hasThreads()) {
			fireAllThreadsTerminated();
		}
	}

	// ITerminate
	public boolean canTerminate() {
		synchronized (threads) {
			IThread[] ths = getThreads();

			if (ths.length == 0) {
				if (waitingForThreads) {
					return true;
				} else {
					return false;
				}
			}

			for (int i = 0; i < ths.length; ++i) {
				if (!ths[i].canTerminate()) {
					return false;
				}
			}

			return true;
		}
	}

	public boolean isTerminated() {
		if (!hasThreads()) {
			return !isWaitingForThreads();
		}

		return getThreadBoolean(new IThreadBoolean() {
			public boolean get(IThread thread) {
				return thread.isTerminated();
			}
		});
	}

	public void terminate() throws DebugException {
		target.terminate();
	}

	public void sendTerminationRequest() throws DebugException {
		synchronized (threads) {
			IScriptThread[] threads = getThreads();
			for (int i = 0; i < threads.length; ++i) {
				threads[i].sendTerminationRequest();
			}
			waitingForThreads = false;
		}
	}

	public boolean canResume() {
		return getThreadBoolean(new IThreadBoolean() {
			public boolean get(IThread thread) {
				return thread.canResume();
			}
		});
	}

	public boolean canSuspend() {
		return getThreadBoolean(new IThreadBoolean() {
			public boolean get(IThread thread) {
				return thread.canSuspend();
			}
		});
	}

	public boolean isSuspended() {
		return getThreadBoolean(new IThreadBoolean() {
			public boolean get(IThread thread) {
				return thread.isSuspended();
			}
		});
	}

	public void resume() throws DebugException {
		synchronized (threads) {
			IThread[] threads = getThreads();
			for (int i = 0; i < threads.length; ++i) {
				threads[i].resume();
			}
		}
	}

	public void suspend() throws DebugException {
		synchronized (threads) {
			IThread[] threads = getThreads();
			for (int i = 0; i < threads.length; ++i) {
				threads[i].suspend();
			}
		}
	}

	public void refreshThreads() {
		synchronized (threads) {
			IThread[] threads = getThreads();
			for (int i = 0; i < threads.length; ++i) {
				((IScriptThread) threads[i]).updateStackFrames();
			}
		}
	}

	public void setScriptThreadConfigurator(
			IScriptDebugThreadConfigurator configurator) {
		this.configurator = configurator;
	}

	public void configureThread(DbgpDebugger engine, ScriptThread scriptThread) {
		if (configurator != null) {
			configurator.configureThread(engine, scriptThread);
		}
	}

}