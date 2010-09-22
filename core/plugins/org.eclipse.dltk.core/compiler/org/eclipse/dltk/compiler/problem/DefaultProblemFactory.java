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

public class DefaultProblemFactory implements IProblemFactory {

	public String getProblemMarker() {
		return DefaultProblem.MARKER_TYPE_PROBLEM;
	}

	public String getTaskMarker() {
		return DefaultProblem.MARKER_TYPE_TASK;
	}

	public IMarker createMarker(IResource resource, IProblem problem)
			throws CoreException {
		final String markerType = problem.isTask() ? getTaskMarker()
				: getProblemMarker();
		return resource.createMarker(markerType);
	}

}
