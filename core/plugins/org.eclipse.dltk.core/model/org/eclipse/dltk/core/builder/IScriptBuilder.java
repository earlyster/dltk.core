/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.eclipse.dltk.core.builder;

import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.dltk.core.IScriptProject;

/**
 * Interface called from script builder to build the selected resource.
 * 
 * @author Haiodo
 */
public interface IScriptBuilder {
	public static final int INCREMENTAL_BUILD = 0;
	public static final int FULL_BUILD = 1;

	/**
	 * Initialize before a build session
	 * 
	 * @param project
	 */
	boolean initialize(IScriptProject project);

	/**
	 * Prepares the build. The builder can add dependencies or escalate build
	 * type from incremental to full.
	 * 
	 * @param change
	 * @param state
	 * @param monitor
	 */
	void prepare(IBuildChange change, IBuildState state,
			IProgressMonitor monitor) throws CoreException;

	/**
	 * Actually performs the build.
	 * 
	 * @param change
	 * @param state
	 * @param monitor
	 */
	void build(IBuildChange change, IBuildState state, IProgressMonitor monitor)
			throws CoreException;

	/**
	 * @see IncrementalProjectBuilder
	 * 
	 * @return
	 */
	void clean(IScriptProject project, IProgressMonitor monitor);

	/**
	 * Reset after a build session
	 * 
	 * @param project
	 */
	void endBuild(IScriptProject project, IBuildState state,
			IProgressMonitor monitor);
}
