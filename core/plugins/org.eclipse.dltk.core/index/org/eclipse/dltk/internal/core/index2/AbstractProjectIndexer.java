/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Zend Technologies
 *******************************************************************************/
package org.eclipse.dltk.internal.core.index2;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IDLTKLanguageToolkit;
import org.eclipse.dltk.core.IProjectFragment;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.core.search.indexing.IProjectIndexer;
import org.eclipse.dltk.core.search.indexing.IndexManager;
import org.eclipse.dltk.internal.core.ModelManager;
import org.eclipse.dltk.internal.core.search.processing.JobManager;
import org.eclipse.osgi.util.NLS;

public class AbstractProjectIndexer implements IProjectIndexer {

	private final IndexManager jobManager = ModelManager.getModelManager()
			.getIndexManager();

	JobManager getJobManager() {
		return jobManager;
	}

	public void indexLibrary(IScriptProject project, IPath path) {
		try {
			IProjectFragment fragment = project.findProjectFragment(path);
			if (fragment != null) {
				AbstractIndexRequest request = new ExternalProjectFragmentRequest(
						this, fragment, new ProgressJob(jobManager));
				if (!jobManager.isJobWaiting(request)) {
					jobManager.request(request);
				}
			} else {
				DLTKCore.warn(NLS.bind("Unknown project fragment: ''{0}''",
						path));
			}
		} catch (Exception e) {
			DLTKCore.error(NLS.bind(
					"An exception is thrown while indexing: ''{0}''", path), e);
		}
	}

	public void indexProject(IScriptProject project) {
		ProjectRequest request = new ProjectRequest(this, project,
				new ProgressJob(jobManager));
		if (!jobManager.isJobWaiting(request)) {
			jobManager.request(request);
		}
	}

	public void indexProjectFragment(IScriptProject project, IPath path) {
		IProjectFragment fragmentToIndex = null;
		try {
			IProjectFragment[] fragments = project.getProjectFragments();
			for (IProjectFragment fragment : fragments) {
				if (fragment.getPath().equals(path)) {
					fragmentToIndex = fragment;
					break;
				}
			}
		} catch (ModelException e) {
			DLTKCore.error("Failed to index fragment:" + path, e);
		}
		if (fragmentToIndex == null || !fragmentToIndex.isExternal()
				|| fragmentToIndex.isBuiltin()) {
			ProjectRequest request = new ProjectRequest(this, project,
					new ProgressJob(jobManager));
			if (!jobManager.isJobWaiting(request)) {
				jobManager.request(request);
			}
			return;
		}

		ExternalProjectFragmentRequest request = new ExternalProjectFragmentRequest(
				this, fragmentToIndex, new ProgressJob(jobManager));
		if (!jobManager.isJobWaiting(request)) {
			jobManager.request(request);
		}
	}

	public void indexSourceModule(ISourceModule module,
			IDLTKLanguageToolkit toolkit) {
		jobManager.request(new AddSourceModuleRequest(this, module, null));
	}

	public void reconciled(ISourceModule workingCopy,
			IDLTKLanguageToolkit toolkit) {
		jobManager.request(new ReconcileSourceModuleRequest(this, workingCopy,
				null));
	}

	public void removeLibrary(IScriptProject project, IPath path) {
		RemoveContainerRequest request = new RemoveContainerRequest(this, path,
				null);
		if (!jobManager.isJobWaiting(request)) {
			jobManager.request(request);
		}
	}

	public void removeProject(IPath projectPath) {
		RemoveContainerRequest request = new RemoveContainerRequest(this,
				projectPath, null);
		if (!jobManager.isJobWaiting(request)) {
			jobManager.request(request);
		}
	}

	public void removeProjectFragment(IScriptProject project, IPath path) {
		RemoveContainerRequest request = new RemoveContainerRequest(this, path,
				null);
		if (!jobManager.isJobWaiting(request)) {
			jobManager.request(request);
		}
	}

	public void removeSourceModule(IScriptProject project, String path) {
		jobManager.request(new RemoveSourceModuleRequest(this, project
				.getPath(), path, null));
	}

	public void startIndexing() {
		jobManager.reset();

		final IWorkspace workspace = ResourcesPlugin.getWorkspace();
		try {
			IScriptProject[] projects = DLTKCore.create(workspace.getRoot())
					.getScriptProjects();

			for (int i = 0; i < projects.length; ++i) {
				ProjectRequest request = new ProjectRequest(this, projects[i],
						new ProgressJob(jobManager));
				if (!jobManager.isJobWaiting(request)) {
					jobManager.request(request);
				}
			}
		} catch (Exception e) {
			DLTKCore
					.error("An exception is thrown while indexing workspace", e);
		}
	}
}
