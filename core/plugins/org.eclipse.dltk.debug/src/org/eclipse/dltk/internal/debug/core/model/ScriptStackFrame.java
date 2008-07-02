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
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IRegisterGroup;
import org.eclipse.debug.core.model.IThread;
import org.eclipse.debug.core.model.IVariable;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.dbgp.IDbgpProperty;
import org.eclipse.dltk.dbgp.IDbgpStackLevel;
import org.eclipse.dltk.dbgp.commands.IDbgpContextCommands;
import org.eclipse.dltk.dbgp.exceptions.DbgpDebuggingEngineException;
import org.eclipse.dltk.dbgp.exceptions.DbgpException;
import org.eclipse.dltk.debug.core.DLTKDebugPlugin;
import org.eclipse.dltk.debug.core.ScriptDebugManager;
import org.eclipse.dltk.debug.core.model.IRefreshableScriptVariable;
import org.eclipse.dltk.debug.core.model.IScriptStack;
import org.eclipse.dltk.debug.core.model.IScriptStackFrame;
import org.eclipse.dltk.debug.core.model.IScriptThread;
import org.eclipse.dltk.debug.core.model.IScriptVariable;

public class ScriptStackFrame extends ScriptDebugElement implements
		IScriptStackFrame {

	private static final String STACK_FRAME_LABEL = Messages.ScriptStackFrame_stackFrame;

	private final IScriptThread thread;
	private final IDbgpStackLevel level;
	private final IScriptStack stack;

	private ScriptVariableContainer variables = null;
	private boolean needRefreshVariables = false;

	protected static IScriptVariable[] readVariables(
			ScriptStackFrame parentFrame, int contextId,
			IDbgpContextCommands commands) throws DbgpException {

		try {
			IDbgpProperty[] properties = commands.getContextProperties(
					parentFrame.getLevel(), contextId);

			IScriptVariable[] variables = new IScriptVariable[properties.length];

			// Workaround for bug 215215
			// https://bugs.eclipse.org/bugs/show_bug.cgi?id=215215
			// Remove this code when Tcl active state debugger fixed
			Set duplicates = findDuplicateNames(properties);

			for (int i = 0; i < properties.length; ++i) {
				IDbgpProperty property = properties[i];
				String name = property.getName();
				if (duplicates.contains(name)) {
					name = property.getEvalName();
				}
				variables[i] = new ScriptVariable(parentFrame, property, name);
			}

			return variables;
		} catch (DbgpDebuggingEngineException e) {
			if (DLTKCore.DEBUG) {
				e.printStackTrace();
			}
			return new IScriptVariable[0];
		}
	}

	private static Set findDuplicateNames(IDbgpProperty[] properties) {
		final Set duplicates = new HashSet();
		final Set alreadyExsisting = new HashSet();
		for (int i = 0; i < properties.length; ++i) {
			final IDbgpProperty property = properties[i];
			final String name = property.getName();
			if (!alreadyExsisting.add(name)) {
				duplicates.add(name);
			}
		}
		return duplicates;
	}

	protected ScriptVariableContainer readAllVariables() throws DbgpException {
		final IDbgpContextCommands commands = thread.getDbgpSession()
				.getCoreCommands();
		final Map names = commands.getContextNames(getLevel());
		final ScriptVariableContainer result = new ScriptVariableContainer();
		if (thread.retrieveLocalVariables()
				&& names.containsKey(new Integer(
						IDbgpContextCommands.LOCAL_CONTEXT_ID))) {
			result.locals = readVariables(this,
					IDbgpContextCommands.LOCAL_CONTEXT_ID, commands);
		}
		if (thread.retrieveGlobalVariables()
				&& names.containsKey(new Integer(
						IDbgpContextCommands.GLOBAL_CONTEXT_ID))) {
			result.globals = readVariables(this,
					IDbgpContextCommands.GLOBAL_CONTEXT_ID, commands);
		}
		if (thread.retrieveClassVariables()
				&& names.containsKey(new Integer(
						IDbgpContextCommands.CLASS_CONTEXT_ID))) {
			result.classes = readVariables(this,
					IDbgpContextCommands.CLASS_CONTEXT_ID, commands);
		}
		return result;
	}

	private static class ScriptVariableContainer {
		IScriptVariable[] locals = null;
		IScriptVariable[] globals = null;
		IScriptVariable[] classes = null;
		ScriptVariableWrapper globalsWrapper = null;
		ScriptVariableWrapper classesWrapper = null;

		ScriptVariableContainer sort(IDebugTarget target) {
			final Comparator variableComparator = ScriptDebugManager
					.getInstance().getVariableNameComparatorByDebugModel(
							target.getModelIdentifier());
			if (locals != null) {
				Arrays.sort(locals, variableComparator);
			}
			if (globals != null) {
				Arrays.sort(globals, variableComparator);
			}
			if (classes != null) {
				Arrays.sort(classes, variableComparator);
			}
			return this;
		}

		private int size() {
			int size = 0;
			if (locals != null) {
				size += locals.length;
			}
			if (globals != null) {
				++size;
			}
			if (classes != null) {
				++size;
			}
			return size;
		}

		IScriptVariable[] toArray(IDebugTarget target) {
			final int size = size();
			final IScriptVariable[] result = new IScriptVariable[size];
			if (size != 0) {
				int index = 0;
				if (locals != null) {
					System.arraycopy(locals, index, result, 0, locals.length);
					index += locals.length;
				}
				if (globals != null) {
					if (globalsWrapper == null) {
						globalsWrapper = new ScriptVariableWrapper(target,
								Messages.ScriptStackFrame_globalVariables,
								globals);
					} else {
						globalsWrapper.refreshValue(globals);
					}
					result[index++] = globalsWrapper;
				}
				if (classes != null) {
					if (classesWrapper == null) {
						classesWrapper = new ScriptVariableWrapper(target,
								Messages.ScriptStackFrame_classVariables,
								classes);
					} else {
						classesWrapper.refreshValue(classes);
					}
					result[index++] = classesWrapper;
				}
			}
			return result;
		}

		/**
		 * @return
		 */
		public boolean hasVariables() {
			return locals != null && locals.length != 0 || classes != null
					|| globals != null;
		}

		/**
		 * @param varName
		 * @return
		 * @throws DebugException
		 */
		public IScriptVariable findVariable(String varName)
				throws DebugException {
			if (locals != null) {
				final IScriptVariable variable = findVariable(varName, locals);
				if (variable != null) {
					return variable;
				}
			}
			if (globals != null) {
				final IScriptVariable variable = findVariable(varName, globals);
				if (variable != null) {
					return variable;
				}
			}
			return null;
		}

		private static IScriptVariable findVariable(String varName,
				IScriptVariable[] vars) throws DebugException {
			for (int i = 0; i < vars.length; i++) {
				final IScriptVariable var = vars[i];
				if (var.getName().equals(varName)) {
					return var;
				}
			}
			return null;
		}
	}

	public ScriptStackFrame(IScriptStack stack, IDbgpStackLevel stackLevel) {
		this.stack = stack;
		this.thread = stack.getThread();
		this.level = stackLevel;
	}

	public void updateVariables() {
		this.variables = null;
	}

	public IScriptStack getStack() {
		return stack;
	}

	public URI getFileName() {
		return level.getFileURI();
	}

	public int getCharStart() throws DebugException {
		return -1;
	}

	public int getCharEnd() throws DebugException {
		return -1;
	}

	public int getLineNumber() throws DebugException {
		return level.getLineNumber();
	}

	public String getName() throws DebugException {
		String name = level.getWhere().trim();

		if (name == null || name.length() == 0) {
			name = toString();
		}

		name += " (" + level.getFileURI().getPath() + ")"; //$NON-NLS-1$ //$NON-NLS-2$

		return name;
	}

	public boolean hasRegisterGroups() throws DebugException {
		return false;
	}

	public IRegisterGroup[] getRegisterGroups() throws DebugException {
		return new IRegisterGroup[0];
	}

	public IThread getThread() {
		return thread;
	}

	public boolean hasVariables() throws DebugException {
		checkVariablesAvailable();
		return variables.hasVariables();
	}

	private synchronized void checkVariablesAvailable() throws DebugException {
		try {
			if (variables == null) {
				variables = readAllVariables();
				variables.sort(getDebugTarget());
			} else if (needRefreshVariables) {
				try {
					refreshVariables();
				} finally {
					needRefreshVariables = false;
				}
			}
		} catch (DbgpException e) {
			variables = new ScriptVariableContainer();
			final Status status = new Status(IStatus.ERROR,
					DLTKDebugPlugin.PLUGIN_ID,
					Messages.ScriptStackFrame_unableToLoadVariables, e);
			DLTKDebugPlugin.log(status);
			throw new DebugException(status);
		}
	}

	/**
	 * @throws DebugException
	 * @throws DbgpException
	 */
	private void refreshVariables() throws DebugException, DbgpException {
		final ScriptVariableContainer newVars = readAllVariables();
		newVars.sort(getDebugTarget());
		variables.locals = refreshVariables(newVars.locals, variables.locals);
		variables.globals = refreshVariables(newVars.globals, variables.globals);
		variables.classes = refreshVariables(newVars.classes, variables.classes);
	}

	/**
	 * @param newVars
	 * @param oldVars
	 * @return
	 * @throws DebugException
	 */
	private static IScriptVariable[] refreshVariables(
			IScriptVariable[] newVars, IScriptVariable[] oldVars)
			throws DebugException {
		if (oldVars != null) {
			final Map map = new HashMap();
			for (int i = 0; i < oldVars.length; ++i) {
				final IScriptVariable variable = oldVars[i];
				if (variable instanceof IRefreshableScriptVariable) {
					map.put(variable.getName(), variable);
				}
			}
			for (int i = 0; i < newVars.length; ++i) {
				final IScriptVariable variable = newVars[i];
				final IRefreshableScriptVariable old;
				old = (IRefreshableScriptVariable) map.get(variable.getName());
				if (old != null) {
					newVars[i] = old.refreshVariable(variable);
				}
			}
		}
		return newVars;
	}

	public IVariable[] getVariables() throws DebugException {
		checkVariablesAvailable();
		return variables.toArray(getDebugTarget());
	}

	// IStep
	public boolean canStepInto() {
		return thread.canStepInto();
	}

	public boolean canStepOver() {
		return thread.canStepOver();
	}

	public boolean canStepReturn() {
		return thread.canStepReturn();
	}

	public boolean isStepping() {
		return thread.isStepping();
	}

	public void stepInto() throws DebugException {
		thread.stepInto();
	}

	public void stepOver() throws DebugException {
		thread.stepOver();
	}

	public void stepReturn() throws DebugException {
		thread.stepReturn();
	}

	// ISuspenResume
	public boolean canResume() {
		return thread.canResume();
	}

	public boolean canSuspend() {
		return thread.canSuspend();
	}

	public boolean isSuspended() {
		return thread.isSuspended();
	}

	public void resume() throws DebugException {
		thread.resume();
	}

	public void suspend() throws DebugException {
		thread.suspend();
	}

	// ITerminate
	public boolean canTerminate() {
		return thread.canTerminate();
	}

	public boolean isTerminated() {
		return thread.isTerminated();
	}

	public void terminate() throws DebugException {
		thread.terminate();
	}

	// IDebugElement
	public IDebugTarget getDebugTarget() {
		return thread.getDebugTarget();
	}

	public IScriptVariable findVariable(String varName) throws DebugException {
		checkVariablesAvailable();
		return variables.findVariable(varName);
	}

	public int getLevel() {
		return level.getLevel();
	}

	public boolean equals(Object obj) {
		if (obj instanceof ScriptStackFrame) {
			final ScriptStackFrame other = (ScriptStackFrame) obj;
			return level.equals(other.level);
		}
		return false;
	}

	public int hashCode() {
		return level.hashCode();
	}

	public String toString() {
		return MessageFormat.format(STACK_FRAME_LABEL,
				new Object[] { new Integer(level.getLevel()) });
	}

	public String getSourceLine() {
		return level.getWhere();
	}

	public URI getSourceURI() {
		return level.getFileURI();
	}

	public IScriptThread getScriptThread() {
		return (IScriptThread) getThread();
	}

	/**
	 * @param frame
	 * @param depth
	 * @return
	 */
	public ScriptStackFrame bind(IDbgpStackLevel newLevel) {
		if (level.equals(newLevel)) {
			needRefreshVariables = true;
			return this;
		}
		return new ScriptStackFrame(stack, newLevel);
	}
}
