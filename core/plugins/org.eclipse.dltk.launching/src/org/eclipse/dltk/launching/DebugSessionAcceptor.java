/*******************************************************************************
 * Copyright (c) 2009 xored software, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     xored software, Inc. - initial API and Implementation (Alex Panchenko)
 *******************************************************************************/
package org.eclipse.dltk.launching;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.dltk.compiler.util.Util;
import org.eclipse.dltk.dbgp.IDbgpSession;
import org.eclipse.dltk.dbgp.IDbgpThreadAcceptor;
import org.eclipse.dltk.debug.core.model.IScriptDebugTargetListener;
import org.eclipse.dltk.internal.debug.core.model.ScriptDebugTarget;

public class DebugSessionAcceptor implements IDbgpThreadAcceptor,
		IScriptDebugTargetListener {

	private final ScriptDebugTarget target;
	private IProgressMonitor parentMonitor;
	private boolean initialized = false;
	private boolean connected = false;

	public DebugSessionAcceptor(ScriptDebugTarget target,
			IProgressMonitor monitor) {
		this.target = target;
		this.parentMonitor = monitor;
		target.addListener(this);
		target.getDbgpService().registerAcceptor(target.getSessionId(), this);
	}

	/*
	 * @see IScriptDebugTargetListener#targetInitialized()
	 */
	public void targetInitialized() {
		synchronized (this) {
			initialized = true;
			notify();
		}
	}

	public void targetTerminating() {
		target.getDbgpService().unregisterAcceptor(target.getSessionId());
	}

	private static final int WAIT_CHUNK = 1000;

	public boolean waitConnection(final int timeout) {
		final SubProgressMonitor sub = new SubProgressMonitor(parentMonitor, 1);
		sub.beginTask(Util.EMPTY_STRING, timeout / WAIT_CHUNK);
		try {
			sub.setTaskName(Messages.DebugSessionAcceptor_waitConnection);
			final long start = System.currentTimeMillis();
			try {
				long waitStart = start;
				for (;;) {
					synchronized (this) {
						if (connected) {
							return true;
						}
					}
					if (target.isTerminated() || sub.isCanceled()) {
						break;
					}
					synchronized (this) {
						wait(WAIT_CHUNK);
					}
					final long now = System.currentTimeMillis();
					if (timeout != 0 && (now - start) > timeout) {
						break;
					}
					sub.worked((int) ((now - waitStart) / WAIT_CHUNK));
					waitStart = now;
				}
			} catch (InterruptedException e) {
				Thread.interrupted();
			}
			return false;
		} finally {
			sub.done();
		}
	}

	public void acceptDbgpThread(IDbgpSession session, IProgressMonitor monitor) {
		final boolean isFirst;
		synchronized (this) {
			isFirst = !connected;
			if (!connected) {
				connected = true;
				notify();
			}
		}
		if (isFirst) {
			IProgressMonitor sub = getInitializeMonitor();
			try {
				target.getDbgpThreadAcceptor().acceptDbgpThread(session, sub);
			} finally {
				sub.done();
			}
		} else {
			target.getDbgpThreadAcceptor().acceptDbgpThread(session,
					this.parentMonitor);
		}
	}

	private IProgressMonitor initializeMonitor = null;

	private synchronized IProgressMonitor getInitializeMonitor() {
		if (initializeMonitor == null) {
			initializeMonitor = new SubProgressMonitor(parentMonitor, 1);
			initializeMonitor.beginTask(Util.EMPTY_STRING, 100);
			initializeMonitor
					.setTaskName(Messages.DebugSessionAcceptor_waitInitialization);
		}
		return initializeMonitor;
	}

	public boolean waitInitialized(final int timeout) {
		final IProgressMonitor sub = getInitializeMonitor();
		try {
			final long start = System.currentTimeMillis();
			try {
				for (;;) {
					synchronized (this) {
						if (initialized) {
							return true;
						}
					}
					if (target.isTerminated() || sub.isCanceled()) {
						break;
					}
					synchronized (this) {
						wait(WAIT_CHUNK);
					}
					final long now = System.currentTimeMillis();
					if (timeout != 0 && (now - start) > timeout) {
						break;
					}
				}
			} catch (InterruptedException e) {
				Thread.interrupted();
			}
			return false;
		} finally {
			sub.done();
		}
	}

}
