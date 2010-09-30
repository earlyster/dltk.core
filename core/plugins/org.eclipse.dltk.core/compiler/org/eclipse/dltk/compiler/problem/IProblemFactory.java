/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
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

/*
 * Factory used from inside the compiler to build the actual problems
 * which are handed back in the compilation result.
 *
 * This allows sharing the internal problem representation with the environment.
 *
 * Note: The factory is responsible for computing and storing a localized error message.
 */
public interface IProblemFactory {

	/**
	 * @since 3.0
	 */
	String getProblemMarker();

	/**
	 * @since 3.0
	 */
	String getTaskMarker();

	/**
	 * @param resource
	 * @param problem
	 * @return
	 * @throws CoreException
	 * @since 3.0
	 */
	IMarker createMarker(IResource resource, IProblem problem)
			throws CoreException;

}
