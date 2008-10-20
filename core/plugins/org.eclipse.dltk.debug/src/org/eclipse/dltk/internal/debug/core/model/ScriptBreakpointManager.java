/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.dltk.internal.debug.core.model;

import java.net.URI;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IMarkerDelta;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IBreakpointListener;
import org.eclipse.debug.core.IBreakpointManager;
import org.eclipse.debug.core.IBreakpointManagerListener;
import org.eclipse.debug.core.IDebugEventSetListener;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.dbgp.IDbgpSession;
import org.eclipse.dltk.dbgp.IDbgpSpawnpoint;
import org.eclipse.dltk.dbgp.breakpoints.DbgpBreakpointConfig;
import org.eclipse.dltk.dbgp.commands.IDbgpBreakpointCommands;
import org.eclipse.dltk.dbgp.commands.IDbgpCoreCommands;
import org.eclipse.dltk.dbgp.commands.IDbgpSpawnpointCommands;
import org.eclipse.dltk.dbgp.exceptions.DbgpException;
import org.eclipse.dltk.debug.core.DLTKDebugPlugin;
import org.eclipse.dltk.debug.core.IDLTKDebugToolkit;
import org.eclipse.dltk.debug.core.ScriptDebugManager;
import org.eclipse.dltk.debug.core.model.IScriptBreakpoint;
import org.eclipse.dltk.debug.core.model.IScriptDebugTarget;
import org.eclipse.dltk.debug.core.model.IScriptExceptionBreakpoint;
import org.eclipse.dltk.debug.core.model.IScriptLineBreakpoint;
import org.eclipse.dltk.debug.core.model.IScriptMethodEntryBreakpoint;
import org.eclipse.dltk.debug.core.model.IScriptSpawnpoint;
import org.eclipse.dltk.debug.core.model.IScriptThread;
import org.eclipse.dltk.debug.core.model.IScriptWatchpoint;
import org.eclipse.osgi.util.NLS;

public class ScriptBreakpointManager implements IBreakpointListener,
		IBreakpointManagerListener {

	private final IScriptBreakpointPathMapper bpPathMapper;

	// Utility methods
	protected static IBreakpointManager getBreakpointManager() {
		return DebugPlugin.getDefault().getBreakpointManager();
	}

	protected static DbgpBreakpointConfig createBreakpointConfig(
			IScriptBreakpoint breakpoint) throws CoreException {
		// Enabled
		boolean enabled = breakpoint.isEnabled()
				&& getBreakpointManager().isEnabled();

		DbgpBreakpointConfig config = new DbgpBreakpointConfig(enabled);

		// Hit value
		config.setHitValue(breakpoint.getHitValue());

		// Hit condition
		config.setHitCondition(breakpoint.getHitCondition());

		// Expression
		if (breakpoint.getExpressionState()) {
			config.setExpression(breakpoint.getExpression());
		}

		if (breakpoint instanceof IScriptLineBreakpoint
				&& !(breakpoint instanceof IScriptMethodEntryBreakpoint)) {
			IScriptLineBreakpoint lineBreakpoint = (IScriptLineBreakpoint) breakpoint;
			config.setLineNo(lineBreakpoint.getLineNumber());
		}
		return config;
	}

	protected static String makeWatchpointExpression(
			IScriptWatchpoint watchpoint) throws CoreException {
		final IDLTKDebugToolkit debugToolkit = ScriptDebugManager.getInstance()
				.getDebugToolkitByDebugModel(watchpoint.getModelIdentifier());
		if (debugToolkit.isAccessWatchpointSupported()) {
			return watchpoint.getFieldName()
					+ (watchpoint.isAccess() ? '1' : '0')
					+ (watchpoint.isModification() ? '1' : '0');
		} else {
			return watchpoint.getFieldName();
		}
	}

	// Adding, removing, updating
	protected void addBreakpoint(IDbgpCoreCommands commands,
			IScriptBreakpoint breakpoint) throws CoreException, DbgpException {

		DbgpBreakpointConfig config = createBreakpointConfig(breakpoint);

		String id = null;
		URI bpUri = null;

		// map the outgoing uri if we're a line breakpoint
		if (breakpoint instanceof IScriptLineBreakpoint) {
			IScriptLineBreakpoint bp = (IScriptLineBreakpoint) breakpoint;
			bpUri = bpPathMapper.map(bp.getResourceURI());
		}

		// Type specific
		if (breakpoint instanceof IScriptWatchpoint) {
			IScriptWatchpoint watchpoint = (IScriptWatchpoint) breakpoint;
			config.setExpression(makeWatchpointExpression(watchpoint));

			id = commands.setWatchBreakpoint(bpUri, watchpoint.getLineNumber(),
					config);
		} else if (breakpoint instanceof IScriptMethodEntryBreakpoint) {
			IScriptMethodEntryBreakpoint entryBreakpoint = (IScriptMethodEntryBreakpoint) breakpoint;

			if (entryBreakpoint.breakOnExit()) {
				final String exitId = commands.setReturnBreakpoint(bpUri,
						entryBreakpoint.getMethodName(), config);

				entryBreakpoint.setExitBreakpointId(exitId);
			}

			if (entryBreakpoint.breakOnEntry()) {
				final String entryId = commands.setCallBreakpoint(bpUri,
						entryBreakpoint.getMethodName(), config);

				entryBreakpoint.setEntryBreakpointId(entryId);
			}
		} else if (breakpoint instanceof IScriptLineBreakpoint) {
			IScriptLineBreakpoint lineBreakpoint = (IScriptLineBreakpoint) breakpoint;

			if (ScriptBreakpointUtils.isConditional(lineBreakpoint)) {
				id = commands.setConditionalBreakpoint(bpUri, lineBreakpoint
						.getLineNumber(), config);
			} else {
				id = commands.setLineBreakpoint(bpUri, lineBreakpoint
						.getLineNumber(), config);
			}
		} else if (breakpoint instanceof IScriptExceptionBreakpoint) {
			IScriptExceptionBreakpoint lineBreakpoint = (IScriptExceptionBreakpoint) breakpoint;
			id = commands.setExceptionBreakpoint(lineBreakpoint.getTypeName(),
					config);
		}

		// Identifier
		breakpoint.setIdentifier(id);
	}

	/**
	 * @param session
	 * @param spawnpoint
	 * @throws CoreException
	 * @throws DbgpException
	 */
	private void addSpawnpoint(IDbgpSpawnpointCommands commands,
			IScriptSpawnpoint spawnpoint) throws DbgpException, CoreException {
		final IDbgpSpawnpoint p = commands.setSpawnpoint(bpPathMapper
				.map(spawnpoint.getResourceURI()), spawnpoint.getLineNumber(),
				spawnpoint.isEnabled());
		if (p != null) {
			spawnpoint.setIdentifier(p.getId());
		}
	}

	protected void changeBreakpoint(IDbgpBreakpointCommands commands,
			IScriptBreakpoint breakpoint) throws DbgpException, CoreException {

		URI bpUri = null;

		// map the outgoing uri if we're a line breakpoint
		if (breakpoint instanceof IScriptLineBreakpoint) {
			IScriptLineBreakpoint bp = (IScriptLineBreakpoint) breakpoint;
			bpUri = bpPathMapper.map(bp.getResourceURI());
		}

		if (breakpoint instanceof IScriptMethodEntryBreakpoint) {
			DbgpBreakpointConfig config = createBreakpointConfig(breakpoint);
			IScriptMethodEntryBreakpoint entryBreakpoint = (IScriptMethodEntryBreakpoint) breakpoint;

			String entryId = entryBreakpoint.getEntryBreakpointId();
			if (entryBreakpoint.breakOnEntry()) {
				if (entryId == null) {
					// Create entry breakpoint
					entryId = commands.setCallBreakpoint(bpUri, entryBreakpoint
							.getMethodName(), config);
					entryBreakpoint.setEntryBreakpointId(entryId);
				} else {
					// Update entry breakpoint
					commands.updateBreakpoint(entryId, config);
				}
			} else {
				if (entryId != null) {
					// Remove existing entry breakpoint
					commands.removeBreakpoint(entryId);
					entryBreakpoint.setEntryBreakpointId(null);
				}
			}

			String exitId = entryBreakpoint.getExitBreakpointId();
			if (entryBreakpoint.breakOnExit()) {
				if (exitId == null) {
					// Create exit breakpoint
					exitId = commands.setReturnBreakpoint(bpUri,
							entryBreakpoint.getMethodName(), config);
					entryBreakpoint.setExitBreakpointId(exitId);
				} else {
					// Update exit breakpoint
					commands.updateBreakpoint(exitId, config);
				}
			} else {
				if (exitId != null) {
					// Remove exit breakpoint
					commands.removeBreakpoint(exitId);
					entryBreakpoint.setExitBreakpointId(null);
				}
			}
		} else {
			// All other breakpoints
			final String id = breakpoint.getIdentifier();
			final DbgpBreakpointConfig config = createBreakpointConfig(breakpoint);

			if (breakpoint instanceof IScriptWatchpoint) {
				config
						.setExpression(makeWatchpointExpression((IScriptWatchpoint) breakpoint));
			}

			commands.updateBreakpoint(id, config);
		}
	}

	protected static void removeBreakpoint(IDbgpBreakpointCommands commands,
			IScriptBreakpoint breakpoint) throws DbgpException, CoreException {

		commands.removeBreakpoint(breakpoint.getIdentifier());

		if (breakpoint instanceof IScriptMethodEntryBreakpoint) {
			IScriptMethodEntryBreakpoint entryBreakpoint = (IScriptMethodEntryBreakpoint) breakpoint;

			final String entryId = entryBreakpoint.getEntryBreakpointId();
			if (entryId != null) {
				commands.removeBreakpoint(entryId);
			}

			final String exitId = entryBreakpoint.getExitBreakpointId();
			if (exitId != null) {
				commands.removeBreakpoint(exitId);
			}
		}
	}

	private static final int NO_CHANGES = 0;
	private static final int MINOR_CHANGE = 1;
	private static final int MAJOR_CHANGE = 2;

	private static int hasBreakpointChanges(IMarkerDelta delta,
			IScriptBreakpoint breakpoint) {
		final String[] attrs = breakpoint.getUpdatableAttributes();
		try {
			final IMarker marker = delta.getMarker();
			for (int i = 0; i < attrs.length; ++i) {
				final String attr = attrs[i];

				final Object oldValue = delta.getAttribute(attr);
				final Object newValue = marker.getAttribute(attr);

				if (oldValue == null) {
					if (newValue != null) {
						return classifyBreakpointChange(delta, breakpoint, attr);
					}
					continue;
				}
				if (newValue == null) {
					return classifyBreakpointChange(delta, breakpoint, attr);
				}
				if (!oldValue.equals(newValue)) {
					return classifyBreakpointChange(delta, breakpoint, attr);
				}
			}
		} catch (CoreException e) {
			DLTKDebugPlugin.log(e);
		}
		return NO_CHANGES;
	}

	private static int hasSpawnpointChanges(IMarkerDelta delta,
			IScriptBreakpoint breakpoint) {
		final String[] attrs = breakpoint.getUpdatableAttributes();
		try {
			final IMarker marker = delta.getMarker();
			for (int i = 0; i < attrs.length; ++i) {
				final String attr = attrs[i];
				if (IBreakpoint.ENABLED.equals(attr)) {
					final Object oldValue = delta.getAttribute(attr);
					final Object newValue = marker.getAttribute(attr);
					if (oldValue == null) {
						if (newValue != null) {
							return MINOR_CHANGE;
						}
						continue;
					}
					if (newValue == null) {
						return MINOR_CHANGE;
					}
					if (!oldValue.equals(newValue)) {
						return MINOR_CHANGE;
					}
				}
			}
		} catch (CoreException e) {
			DLTKDebugPlugin.log(e);
		}
		return NO_CHANGES;
	}

	private static int classifyBreakpointChange(IMarkerDelta delta,
			IScriptBreakpoint breakpoint, String attr) throws CoreException {
		final boolean conditional = ScriptBreakpointUtils
				.isConditional(breakpoint);
		if (conditional && AbstractScriptBreakpoint.EXPRESSION.equals(attr)) {
			return MAJOR_CHANGE;
		}
		final boolean oldExprState = delta.getAttribute(
				AbstractScriptBreakpoint.EXPRESSION_STATE, false);
		final String oldExpr = delta.getAttribute(
				AbstractScriptBreakpoint.EXPRESSION, null);
		if (ScriptBreakpointUtils.isConditional(oldExprState, oldExpr) != conditional) {
			return MAJOR_CHANGE;
		}
		return MINOR_CHANGE;
	}

	// DebugTarget
	private final IScriptDebugTarget target;

	// Add, remove, update to debug target
	protected void addBreakpoint(IBreakpoint breakpoint) throws CoreException,
			DbgpException {
		if (supportsBreakpoint(breakpoint)) {
			final IDbgpSession session = getSession();

			if (session != null) {
				if (breakpoint instanceof IScriptSpawnpoint) {
					addSpawnpoint((IDbgpSpawnpointCommands) session
							.get(IDbgpSpawnpointCommands.class),
							(IScriptSpawnpoint) breakpoint);
				} else {
					addBreakpoint(session.getCoreCommands(),
							(IScriptBreakpoint) breakpoint);
				}
			}
		}
	}

	protected void changeBreakpoint(IBreakpoint breakpoint)
			throws CoreException, DbgpException {
		if (supportsBreakpoint(breakpoint)) {
			final IDbgpSession session = getSession();
			if (session != null) {
				changeBreakpoint(session.getCoreCommands(),
						(IScriptBreakpoint) breakpoint);
			}
		}
	}

	private void changeSpawnpoint(IScriptSpawnpoint spawnpoint)
			throws DbgpException, CoreException {
		if (supportsBreakpoint(spawnpoint)) {
			final IDbgpSession session = getSession();
			if (session != null) {
				final IDbgpSpawnpointCommands commands = (IDbgpSpawnpointCommands) session
						.get(IDbgpSpawnpointCommands.class);
				if (commands != null) {
					commands.updateSpawnpoint(spawnpoint.getIdentifier(),
							spawnpoint.isEnabled());
				}
			}
		}
	}

	protected void removeBreakpoint(IBreakpoint breakpoint)
			throws CoreException, DbgpException {
		if (supportsBreakpoint(breakpoint)) {
			final IDbgpSession session = getSession();
			if (session != null) {
				removeBreakpoint(session.getCoreCommands(),
						(IScriptBreakpoint) breakpoint);
			}
		}
	}

	protected void removeSpawnpoint(IScriptSpawnpoint spawnpoint)
			throws DbgpException, CoreException {
		if (supportsBreakpoint(spawnpoint)) {
			final IDbgpSession session = getSession();
			if (session != null) {
				final IDbgpSpawnpointCommands commands = (IDbgpSpawnpointCommands) session
						.get(IDbgpSpawnpointCommands.class);
				if (commands != null) {
					commands.removeSpawnpoint(spawnpoint.getIdentifier());
				}
			}
		}
	}

	private IDbgpSession getSession() throws DebugException {
		final IScriptThread[] threads = (IScriptThread[]) target.getThreads();
		if (threads.length > 0) {
			return threads[0].getDbgpSession();
		} else {
			return null;
		}
	}

	public ScriptBreakpointManager(IScriptDebugTarget target,
			IScriptBreakpointPathMapper pathMapper) {
		this.target = target;
		this.bpPathMapper = pathMapper;
	}

	public boolean supportsBreakpoint(IBreakpoint breakpoint) {
		if (breakpoint instanceof IScriptBreakpoint) {
			final String modelId = target.getModelIdentifier();
			final String breakpointModelId = breakpoint.getModelIdentifier();

			return breakpointModelId.equals(modelId);
		}

		return false;
	}

	public void threadAccepted() {
		IBreakpointManager manager = DebugPlugin.getDefault()
				.getBreakpointManager();

		manager.addBreakpointListener(target);
		manager.addBreakpointManagerListener(this);
	}

	public void threadTerminated() {
		IBreakpointManager manager = DebugPlugin.getDefault()
				.getBreakpointManager();

		manager.removeBreakpointListener(target);
		manager.removeBreakpointManagerListener(this);

		bpPathMapper.clearCache();
	}

	public void setupDeferredBreakpoints() {
		IBreakpoint[] breakpoints = getBreakpointManager().getBreakpoints(
				target.getModelIdentifier());

		for (int i = 0; i < breakpoints.length; i++) {
			try {
				addBreakpoint(breakpoints[i]);
			} catch (Exception e) {
				DLTKDebugPlugin.logError(NLS.bind(
						Messages.ErrorSetupDeferredBreakpoints, e.toString()),
						e);
				if (DLTKCore.DEBUG) {
					e.printStackTrace();
				}
			}
		}
	}

	// Simple breakpoint management
	public String addBreakpoint(URI uri, int line) {
		try {
			final IDbgpSession session = getSession();
			if (session != null) {

				DbgpBreakpointConfig config = new DbgpBreakpointConfig(true);

				return session.getCoreCommands().setLineBreakpoint(uri, line,
						config);
			}

		} catch (DebugException e) {
			DLTKDebugPlugin.log(e);
		} catch (DbgpException e) {
			DLTKDebugPlugin.log(e);
		}

		return null;
	}

	public void removeBreakpoint(String id) {
		try {
			final IDbgpSession session = getSession();
			if (session != null) {
				session.getCoreCommands().removeBreakpoint(id);
			}
		} catch (DebugException e) {
			DLTKDebugPlugin.log(e);
		} catch (DbgpException e) {
			DLTKDebugPlugin.log(e);
		}
	}

	public void setBreakpointUntilFirstSuspend(URI uri, int line) {
		final String tempId = addBreakpoint(uri, line);

		DebugPlugin.getDefault().addDebugEventListener(
				new IDebugEventSetListener() {
					public void handleDebugEvents(DebugEvent[] events) {
						for (int i = 0; i < events.length; ++i) {
							DebugEvent event = events[i];
							if (event.getKind() == DebugEvent.SUSPEND) {
								removeBreakpoint(tempId);
								DebugPlugin.getDefault()
										.removeDebugEventListener(this);
							}
						}
					}
				});
	}

	// IBreakpointListener
	public void breakpointAdded(IBreakpoint breakpoint) {
		try {
			addBreakpoint(breakpoint);
		} catch (Exception e) {
			DLTKDebugPlugin.log(e);
		}
	}

	public void breakpointChanged(IBreakpoint breakpoint, IMarkerDelta delta) {
		try {
			if (breakpoint instanceof IScriptBreakpoint && delta != null) {
				if (breakpoint instanceof IScriptSpawnpoint) {
					if (hasSpawnpointChanges(delta,
							(IScriptSpawnpoint) breakpoint) == MINOR_CHANGE) {
						changeSpawnpoint((IScriptSpawnpoint) breakpoint);
					}
				} else {
					final int changes = hasBreakpointChanges(delta,
							(IScriptBreakpoint) breakpoint);
					if (changes != NO_CHANGES) {
						if (changes == MAJOR_CHANGE) {
							removeBreakpoint(breakpoint);
							addBreakpoint(breakpoint);
						} else {
							changeBreakpoint(breakpoint);
						}
					}
				}
			}
		} catch (Exception e) {
			DLTKDebugPlugin.log(e);
		}
	}

	public void breakpointRemoved(IBreakpoint breakpoint, IMarkerDelta delta) {
		try {
			if (breakpoint instanceof IScriptSpawnpoint) {
				removeSpawnpoint((IScriptSpawnpoint) breakpoint);
			} else {
				removeBreakpoint(breakpoint);
			}
		} catch (Exception e) {
			DLTKDebugPlugin.log(e);
		}
	}

	// IBreakpointManagerListener
	public void breakpointManagerEnablementChanged(boolean enabled) {
		final IBreakpointManager manager = getBreakpointManager();

		IBreakpoint[] breakpoints = manager.getBreakpoints(target
				.getModelIdentifier());

		for (int i = 0; i < breakpoints.length; ++i) {
			try {
				final IBreakpoint breakpoint = breakpoints[i];
				if (breakpoint instanceof IScriptSpawnpoint) {
					changeSpawnpoint((IScriptSpawnpoint) breakpoint);
				} else {
					changeBreakpoint(breakpoint);
				}
			} catch (Exception e) {
				DLTKDebugPlugin.log(e);
			}
		}
	}

}
