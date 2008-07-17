/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.eclipse.dltk.compiler.problem;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.dltk.compiler.task.DLTKTaskReporter;
import org.eclipse.dltk.compiler.task.ITaskReporter;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IScriptModelMarker;
import org.eclipse.dltk.internal.core.util.Util;

/**
 * @deprecated
 */
public class DLTKProblemReporter implements IProblemReporter {

	private IResource resource;
	private IProblemFactory factory;
	private boolean cleaned = false;

	public void reportProblem(IProblem problem) {
		try {
			int severity = IMarker.SEVERITY_INFO;

			if (problem.isError()) {
				severity = IMarker.SEVERITY_ERROR;
			} else if (problem.isWarning()) {
				severity = IMarker.SEVERITY_WARNING;
			}
			IMarker m = resource
					.createMarker(DefaultProblem.MARKER_TYPE_PROBLEM);

			m.setAttribute(IMarker.LINE_NUMBER,
					problem.getSourceLineNumber() + 1);
			m.setAttribute(IMarker.MESSAGE, problem.getMessage());
			m.setAttribute(IMarker.SEVERITY, severity);
			m.setAttribute(IMarker.PRIORITY, IMarker.PRIORITY_NORMAL);
			m.setAttribute(IMarker.CHAR_START, problem.getSourceStart());
			m.setAttribute(IMarker.CHAR_END, problem.getSourceEnd());
			if (problem.getID() != 0) {
				m.setAttribute(IScriptModelMarker.ID, problem.getID());
			}
			final String[] arguments = problem.getArguments();
			if (arguments != null && arguments.length != 0) {
				m.setAttribute(IScriptModelMarker.ARGUMENTS, Util
						.getProblemArgumentsForMarker(arguments));
			}
		} catch (CoreException e) {
			DLTKCore.error("reportProblem", e); //$NON-NLS-1$
		}
	}

	protected IProblemFactory getProblemFactory() {
		return factory;
	}

	public DLTKProblemReporter(IResource resource, IProblemFactory factory) {
		if (resource == null) {
			throw new NullPointerException(
					Messages.DLTKProblemReporter_resourceCannotBeNull);
		}

		if (factory == null) {
			throw new NullPointerException(
					Messages.DLTKProblemReporter_factoryCannotBeNull);
		}

		this.resource = resource;
		this.factory = factory;
	}

	public void clearMarkers() {
		if (this.resource != null) {
			try {
				this.resource.deleteMarkers(DefaultProblem.MARKER_TYPE_PROBLEM,
						true, IResource.DEPTH_INFINITE);
			} catch (CoreException e) {
				if (DLTKCore.DEBUG) {
					e.printStackTrace();
				}
			}
			this.cleaned = true;
		}
	}

	public boolean isMarkersCleaned() {
		return this.cleaned;
	}

	public Object getAdapter(Class adapter) {
		if (ITaskReporter.class.equals(adapter)) {
			return new DLTKTaskReporter(resource);
		}
		return null;
	}
}
