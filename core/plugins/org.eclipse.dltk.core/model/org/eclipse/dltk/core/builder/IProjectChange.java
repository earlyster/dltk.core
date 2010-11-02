/*******************************************************************************
 * Copyright (c) 2010 xored software, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     xored software, Inc. - initial API and Implementation (Alex Panchenko)
 *******************************************************************************/
package org.eclipse.dltk.core.builder;

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.ISourceModule;

/**
 * The changes happened in the project.
 * 
 * In full build "getChanged*" methods return all project data, other methods
 * return empty lists.
 */
public interface IProjectChange {

	/**
	 * Default mode: return only non-source modules including new files after
	 * rename
	 */
	int DEFAULT = 0;

	/**
	 * Return all resources (don't exclude source modules) including new files
	 * after rename
	 */
	int ALL = 4;

	/**
	 * Exclude renamed resources from the returned lists
	 */
	int NO_RENAMES = 8;

	/**
	 * Include additions
	 */
	int ADDED = 1;

	/**
	 * Include changes
	 */
	int CHANGED = 2;

	int BEFORE = 16;

	/**
	 * Returns the project
	 * 
	 * @return
	 */
	IProject getProject();

	/**
	 * Returns the script project
	 * 
	 * @return
	 */
	IScriptProject getScriptProject();

	/**
	 * Returns the {@link IResourceDelta} for incremental build or
	 * <code>null</code> for full.
	 * 
	 * @return
	 */
	IResourceDelta getResourceDelta();

	/**
	 * Returns the list of deleted paths.
	 * 
	 * @param options
	 *            {@link IProjectChange#DEFAULT},
	 *            {@link IProjectChange#NO_RENAMES} don't include renamed files
	 * @return
	 * @throws CoreException
	 */
	List<IPath> getDeletes(int options) throws CoreException;

	/**
	 * Returns the list of renames.
	 * 
	 * @return
	 * @throws CoreException
	 */
	List<IRenameChange> getRenames() throws CoreException;

	/**
	 * Returns the list of added or changed resources. The list includes renamed
	 * resources (under new names) as well.
	 * 
	 * @param options
	 *            {@link IProjectChange#DEFAULT}, {@link IProjectChange#ALL},
	 *            {@link IProjectChange#NO_RENAMES},
	 *            {@link IProjectChange#ADDED}, {@link IProjectChange#CHANGED}
	 * @return
	 * @throws CoreException
	 */
	List<IFile> getResources(int options) throws CoreException;

	/**
	 * Returns the list of added or changed source modules. The list includes
	 * renamed source modules (under new names) as well.
	 * 
	 * @param options
	 *            <ul>
	 *            <li>{@link IProjectChange#DEFAULT}
	 *            <li>{@link IProjectChange#ADDED}
	 *            <li>{@link IProjectChange#CHANGED}
	 *            </ul>
	 * @return
	 * @throws CoreException
	 */
	List<ISourceModule> getSourceModules(int options) throws CoreException;

}
