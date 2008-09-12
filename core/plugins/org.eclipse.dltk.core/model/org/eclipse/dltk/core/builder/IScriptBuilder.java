/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.eclipse.dltk.core.builder;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.dltk.core.IScriptProject;

/**
 * Interface called from script builder to build the selected resource.
 * 
 * @author Haiodo
 * 
 */
public interface IScriptBuilder {
	public static final int INCREMENTAL_BUILD = 0;
	public static final int FULL_BUILD = 1;

	/**
	 * Initialize before a build session
	 * 
	 * @param project
	 */
	void initialize(IScriptProject project);

	/**
	 * Estimate number of elements will be build from given set.
	 * 
	 * @return
	 */
	int estimateElementsToBuild(IScriptProject project, List elements);

	/**
	 * Called for each resource required to build. Only resources with specified
	 * project nature are here.
	 * 
	 * @return
	 */
	IStatus buildResources(IScriptProject project, List resources,
			IProgressMonitor monitor, int status);

	/**
	 * Called for each resource required to build. Only resources with specified
	 * project nature are here.
	 * 
	 * @return
	 */
	IStatus buildModelElements(IScriptProject project, List elements,
			IProgressMonitor monitor, int status);

	public static class DependencyResponse {
		public boolean isFullBuild() {
			return false;
		}

		public Set getDependencies() {
			return Collections.EMPTY_SET;
		}

		public static final DependencyResponse FULL_BUILD = new DependencyResponse() {
			public boolean isFullBuild() {
				return true;
			}
		};

		public static DependencyResponse create(final Set dependencies) {
			return new DependencyResponse() {
				public Set getDependencies() {
					return dependencies;
				}
			};
		}
	}

	/**
	 * Return all dependencies for selected resource. Should also return all
	 * dependencies of returned elements.
	 * 
	 * For example if c depends on b and b depends on a, and we ask for
	 * dependencies or a, then b and c should be returned.
	 * 
	 * Resources should be checked for type. Because different kind of resource
	 * could be here.
	 * 
	 * @param buildType
	 *            build type {@link #FULL_BUILD} or {@link #INCREMENTAL_BUILD}
	 * @param localElements
	 *            changed source modules
	 * @param externalElements
	 *            newly added external source modules
	 * @param oldExternalFolders
	 *            old external fragments
	 * @param externalFolders
	 *            new external fragments
	 * @return <code>null</code> if there are no dependencies found,
	 *         {@link DependencyResponse#FULL_BUILD} to promote to the full
	 *         build, or the result of {@link DependencyResponse#create(Set)}
	 */
	DependencyResponse getDependencies(IScriptProject project, int buildType,
			Set localElements, Set externalElements, Set oldExternalFolders,
			Set externalFolders);

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
	void reset(IScriptProject project);
}
