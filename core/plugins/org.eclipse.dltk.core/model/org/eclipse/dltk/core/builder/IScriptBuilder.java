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

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.ISourceModule;

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
	 * Called for each resource required to build. Only resources with specified
	 * project nature are here.
	 * 
	 * @return
	 */
	IStatus buildResources(IScriptProject project, List<IResource> resources,
			IProgressMonitor monitor, int status);

	/**
	 * Called for each resource required to build. Only resources with specified
	 * project nature are here.
	 * 
	 * @return
	 */
	IStatus buildModelElements(IScriptProject project,
			List<ISourceModule> elements, IProgressMonitor monitor, int status);

	public static class DependencyResponse {
		public boolean isFullLocalBuild() {
			return false;
		}

		public boolean isFullExternalBuild() {
			return false;
		}

		public Set<ISourceModule> getLocalDependencies() {
			return Collections.emptySet();
		}

		public Set<ISourceModule> getExternalDependencies() {
			return Collections.emptySet();
		}

		public static final DependencyResponse FULL_LOCAL_BUILD = new DependencyResponse() {
			@Override
			public boolean isFullLocalBuild() {
				return true;
			}
		};

		public static final DependencyResponse FULL_EXTERNAL_BUILD = new DependencyResponse() {
			@Override
			public boolean isFullLocalBuild() {
				return true;
			}

			@Override
			public boolean isFullExternalBuild() {
				return true;
			}
		};

		public static DependencyResponse createLocal(
				final Set<ISourceModule> localDependencies) {
			return new DependencyResponse() {
				@Override
				public Set<ISourceModule> getLocalDependencies() {
					return localDependencies;
				}
			};
		}

		public static DependencyResponse create(final boolean fullLocal,
				final Set<ISourceModule> localDependencies,
				final Set<ISourceModule> externalDependencies) {
			return new DependencyResponse() {

				@Override
				public boolean isFullLocalBuild() {
					return fullLocal;
				}

				@Override
				public Set<ISourceModule> getLocalDependencies() {
					return !fullLocal && localDependencies != null ? localDependencies
							: Collections.<ISourceModule> emptySet();
				}

				@Override
				public Set<ISourceModule> getExternalDependencies() {
					return externalDependencies != null ? externalDependencies
							: Collections.<ISourceModule> emptySet();
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
	 *         {@link DependencyResponse#FULL_LOCAL_BUILD} to promote to the
	 *         full build, or the result of
	 *         {@link DependencyResponse#create(Set)}
	 */
	DependencyResponse getDependencies(IScriptProject project, int buildType,
			Set<ISourceModule> localElements,
			Set<ISourceModule> externalElements, Set oldExternalFolders,
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
	void endBuild(IScriptProject project, IProgressMonitor monitor);
}
