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
	 * This method is called to customize (add or remove elements in the the
	 * specified list) the set of entries {@link IBuildpathEntry
	 * IBuildpathEntries}.
	 * 
	 * It's called for each project.
	 * 
	 * Entries are initialized with the entries constructed for the interpreter
	 * library locations.
	 */
	void processEntres(IScriptProject project, List<IBuildpathEntry> entries);

}
