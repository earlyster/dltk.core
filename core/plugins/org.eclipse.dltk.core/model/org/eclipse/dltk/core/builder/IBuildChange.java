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

import java.util.Collection;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.dltk.core.ISourceModule;

/**
 * The changed caused the build
 */
public interface IBuildChange extends IProjectChange {

	/**
	 * Returns the type of build operation
	 * 
	 * @see IScriptBuilder#INCREMENTAL_BUILD
	 * @see IScriptBuilder#FULL_BUILD
	 * @return
	 */
	int getBuildType();

	/**
	 * @param buildType
	 *            <ul>
	 *            <li>IScriptBuilder#INCREMENTAL_BUILD
	 *            <li>IScriptBuilder#FULL_BUILD
	 *            </ul>
	 * @throws IllegalArgumentException
	 */
	void setBuildType(int buildType);

	boolean isDependencyBuild();

	/**
	 * Returns changes in required projects
	 * 
	 * @return
	 */
	IProjectChange[] getRequiredProjectChanges();

	/**
	 * Adds the specified resource to this change. Returns <code>true</code> if
	 * resource was added successfully or <code>false</code> if it is already in
	 * the this change.
	 * 
	 * @param file
	 * @return
	 */
	boolean addChangedResource(IFile file) throws CoreException;

	boolean addChangedResources(Collection<IFile> files) throws CoreException;

	/**
	 * @param options
	 *            <ul>
	 *            <li>{@link IProjectChange#DEFAULT} returns new state
	 *            <li>{@link IProjectChange#BEFORE} returns state of the
	 *            previous build
	 *            </ul>
	 * @return
	 * @throws CoreException
	 */
	List<IPath> getExternalPaths(int options) throws CoreException;

	/**
	 * @return
	 * @throws CoreException
	 */
	List<ISourceModule> getExternalModules(int options) throws CoreException;

	boolean isOnBuildpath(IResource resource);

}
