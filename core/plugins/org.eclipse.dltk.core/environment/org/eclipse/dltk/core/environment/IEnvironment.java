/*******************************************************************************
 * Copyright (c) 2005, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.dltk.core.environment;

import java.net.URI;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;

public interface IEnvironment extends IAdaptable {
	/**
	 * Returns {@link IFileHandle} for the specified path. The path should be
	 * local to this environment. If you have full path (with the environment
	 * id) - you should use {@link EnvironmentPathUtils}
	 * 
	 * @param path
	 * 		environment-local path
	 * @return
	 */
	IFileHandle getFile(IPath path);

	String getId();

	String getSeparator();

	char getSeparatorChar();

	String getPathsSeparator();

	char getPathsSeparatorChar();

	String getName();

	boolean hasProject(IProject project);

	String convertPathToString(IPath path);

	URI getURI(IPath location);

	IFileHandle getFile(URI locationURI);
}
