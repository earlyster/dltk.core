/*******************************************************************************
 * Copyright (c) 2008 xored software, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     xored software, Inc. - initial API and Implementation (Alex Panchenko)
 *******************************************************************************/
package org.eclipse.dltk.launching.process;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.ISourceLocator;

class LaunchProxy implements ILaunch {

	private final ILaunch original;

	/**
	 * @param original
	 */
	public LaunchProxy(ILaunch original) {
		this.original = original;
	}

	/*
	 * @see ILaunch#addDebugTarget(IDebugTarget)
	 */
	public void addDebugTarget(IDebugTarget target) {
		original.addDebugTarget(target);
	}

	/*
	 * @see ILaunch#addProcess(IProcess)
	 */
	public void addProcess(IProcess process) {
		original.addProcess(process);
	}

	/*
	 * @see ILaunch#getAttribute(String)
	 */
	public String getAttribute(String key) {
		if (DebugPlugin.ATTR_CAPTURE_OUTPUT.equals(key)) {
			return Boolean.TRUE.toString();
		} else {
			return original.getAttribute(key);
		}
	}

	/*
	 * @see org.eclipse.debug.core.ILaunch#getChildren()
	 */
	public Object[] getChildren() {
		return original.getChildren();
	}

	/*
	 * @see org.eclipse.debug.core.ILaunch#getDebugTarget()
	 */
	public IDebugTarget getDebugTarget() {
		return original.getDebugTarget();
	}

	/*
	 * @see org.eclipse.debug.core.ILaunch#getDebugTargets()
	 */
	public IDebugTarget[] getDebugTargets() {
		return original.getDebugTargets();
	}

	/*
	 * @see org.eclipse.debug.core.ILaunch#getLaunchConfiguration()
	 */
	public ILaunchConfiguration getLaunchConfiguration() {
		return original.getLaunchConfiguration();
	}

	/*
	 * @see org.eclipse.debug.core.ILaunch#getLaunchMode()
	 */
	public String getLaunchMode() {
		return original.getLaunchMode();
	}

	/*
	 * @see org.eclipse.debug.core.ILaunch#getProcesses()
	 */
	public IProcess[] getProcesses() {
		return original.getProcesses();
	}

	/*
	 * @see org.eclipse.debug.core.ILaunch#getSourceLocator()
	 */
	public ISourceLocator getSourceLocator() {
		return original.getSourceLocator();
	}

	/*
	 * @see org.eclipse.debug.core.ILaunch#hasChildren()
	 */
	public boolean hasChildren() {
		return original.hasChildren();
	}

	/*
	 * @see
	 * org.eclipse.debug.core.ILaunch#removeDebugTarget(org.eclipse.debug.core
	 * .model.IDebugTarget)
	 */
	public void removeDebugTarget(IDebugTarget target) {
		original.removeDebugTarget(target);
	}

	/*
	 * @see ILaunch#removeProcess(IProcess)
	 */
	public void removeProcess(IProcess process) {
		original.removeProcess(process);
	}

	/*
	 * @see ILaunch#setAttribute(String,String)
	 */
	public void setAttribute(String key, String value) {
		original.setAttribute(key, value);
	}

	/*
	 * @see ILaunch#setSourceLocator(ISourceLocator)
	 */
	public void setSourceLocator(ISourceLocator sourceLocator) {
		original.setSourceLocator(sourceLocator);
	}

	/*
	 * @see org.eclipse.debug.core.model.ITerminate#canTerminate()
	 */
	public boolean canTerminate() {
		return original.canTerminate();
	}

	/*
	 * @see org.eclipse.debug.core.model.ITerminate#isTerminated()
	 */
	public boolean isTerminated() {
		return original.isTerminated();
	}

	/*
	 * @see org.eclipse.debug.core.model.ITerminate#terminate()
	 */
	public void terminate() throws DebugException {
		original.terminate();
	}

	/*
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
	public Object getAdapter(Class adapter) {
		return original.getAdapter(adapter);
	}

}
