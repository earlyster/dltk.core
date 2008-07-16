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
package org.eclipse.dltk.validators.internal.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.dltk.compiler.problem.AbstractProblemReporter;
import org.eclipse.dltk.compiler.problem.DefaultProblem;
import org.eclipse.dltk.compiler.problem.IProblem;
import org.eclipse.dltk.compiler.problem.IProblemReporter;
import org.eclipse.dltk.core.IScriptModelMarker;
import org.eclipse.dltk.internal.core.util.Util;

public class BuildProblemReporter extends AbstractProblemReporter implements
		IProblemReporter {

	private final IResource resource;
	private final List problems = new ArrayList();
	private boolean oldMarkersDeleted = false;

	/**
	 * @param resource
	 */
	public BuildProblemReporter(IResource resource) {
		this.resource = resource;
	}

	public void reportProblem(IProblem problem) {
		problems.add(problem);
	}

	/**
	 * @return
	 */
	public boolean isEmpty() {
		return problems.isEmpty();
	}

	public void flush() {
		final String markerType = DefaultProblem.MARKER_TYPE_PROBLEM;
		try {
			if (!oldMarkersDeleted) {
				oldMarkersDeleted = true;
				resource.deleteMarkers(markerType, true,
						IResource.DEPTH_INFINITE);
			}
			for (Iterator i = problems.iterator(); i.hasNext();) {
				final IProblem problem = (IProblem) i.next();

				int severity = IMarker.SEVERITY_INFO;
				if (problem.isError()) {
					severity = IMarker.SEVERITY_ERROR;
				} else if (problem.isWarning()) {
					severity = IMarker.SEVERITY_WARNING;
				}

				final IMarker m = resource.createMarker(markerType);
				m.setAttribute(IMarker.LINE_NUMBER, problem
						.getSourceLineNumber() + 1);
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
			}
			problems.clear();
		} catch (CoreException e) {
			ValidatorsCore.log(e.getStatus());
		}
	}

	/**
	 * @return
	 */
	public boolean hasErrors() {
		// TODO check severity
		return !problems.isEmpty();
	}

}
