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
package org.eclipse.dltk.launching;

import java.util.List;

import org.eclipse.dltk.core.IBuildpathEntry;
import org.eclipse.dltk.core.IScriptProject;

public interface IInterpreterContainerExtension {

	/**
	 * <p>
	 * This method is called to customize (only add or remove elements,
	 * modification of existing objects is not allowed) the
	 * {@link IBuildpathEntry IBuildpathEntries} returned to the project.
	 * </p>
	 * 
	 * <p>
	 * It's called for each project.
	 * </p>
	 * 
	 * <p>
	 * Entries are initialized with the interpreter library locations.
	 * </p>
	 */
	void processEntres(IScriptProject project, List<IBuildpathEntry> entries);

}
