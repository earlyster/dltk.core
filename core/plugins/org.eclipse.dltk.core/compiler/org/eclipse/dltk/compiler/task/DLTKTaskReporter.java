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
package org.eclipse.dltk.compiler.task;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.dltk.compiler.problem.DefaultProblem;
import org.eclipse.dltk.core.DLTKCore;

/**
 * Default implementation of the {@link ITaskReporter}
 * 
 * @deprecated
 */
public class DLTKTaskReporter implements ITaskReporter {

	private static final String MARKER_TYPE = DefaultProblem.MARKER_TYPE_TASK;

	private final IResource resource;
	private boolean tasksCleared;

	public DLTKTaskReporter(IResource resource) {
		this.resource = resource;
		tasksCleared = false;
	}

	public void clearTasks() {
		if (!tasksCleared) {
			try {
				resource.deleteMarkers(MARKER_TYPE, true,
						IResource.DEPTH_INFINITE);
			} catch (CoreException e) {
				System.err.println(e);
			}
			tasksCleared = true;
		}
	}

	public void reportTask(String message, int lineNumber, int priority,
			int charStart, int charEnd) {
		try {
			IMarker m = resource.createMarker(MARKER_TYPE);
			m.setAttribute(IMarker.LINE_NUMBER, lineNumber + 1);
			m.setAttribute(IMarker.MESSAGE, message);
			m.setAttribute(IMarker.PRIORITY, priority);
			m.setAttribute(IMarker.CHAR_START, charStart);
			m.setAttribute(IMarker.CHAR_END, charEnd);
			m.setAttribute(IMarker.USER_EDITABLE, Boolean.FALSE);
		} catch (CoreException e) {
			DLTKCore.error("reportTask", e); //$NON-NLS-1$
		}
	}

	public Object getAdapter(Class adapter) {
		return null;
	}

}
