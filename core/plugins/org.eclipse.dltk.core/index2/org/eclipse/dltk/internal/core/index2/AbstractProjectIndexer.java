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

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.DLTKLanguageManager;
import org.eclipse.dltk.core.IDLTKLanguageToolkit;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.IProjectFragment;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.core.PriorityClassDLTKExtensionManager;
import org.eclipse.dltk.core.index2.IIndexer;
import org.eclipse.dltk.core.search.indexing.IProjectIndexer;
import org.eclipse.dltk.internal.core.search.processing.JobManager;
import org.eclipse.dltk.internal.core.util.Util;
import org.eclipse.osgi.util.NLS;

public class AbstractProjectIndexer implements IProjectIndexer {

	private final static String INDEXER_EXTPOINT = DLTKCore.PLUGIN_ID
			+ ".index2"; //$NON-NLS-1$

	private static PriorityClassDLTKExtensionManager indexerManager = new PriorityClassDLTKExtensionManager(
			INDEXER_EXTPOINT);

	private final IndexJobManager jobManager = new IndexJobManager();

	JobManager getJobManager() {
		return jobManager;
	}

	IIndexer getIndexer(String natureId) {
		IIndexer indexer = (IIndexer) indexerManager.getObject(natureId);
		if (indexer == null) {
			indexer = (IIndexer) indexerManager.getObject("#");
		}
		return indexer;
	}

	IIndexer getIndexer(IModelElement modelElement) {
		IIndexer indexer = (IIndexer) indexerManager.getObject(modelElement);
		if (indexer == null) {
			indexer = (IIndexer) indexerManager.getObject("#");
		}
		return indexer;
	}

	public void indexLibrary(IScriptProject project, IPath path) {
		try {
			IProjectFragment fragment = project.findProjectFragment(path);
			if (fragment != null) {
				AbstractIndexRequest request = new ExternalProjectFragmentRequest(
						this, fragment);
				jobManager.requestIfNotWaiting(request);
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
		final ProjectRequest request = new ProjectRequest(this, project);
		jobManager.requestIfNotWaiting(request);
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
			jobManager.requestIfNotWaiting(new ProjectRequest(this, project));
			return;
		}
		jobManager.requestIfNotWaiting(new ExternalProjectFragmentRequest(this,
				fragmentToIndex));
	}

	public void indexSourceModule(ISourceModule module,
			IDLTKLanguageToolkit toolkit) {
		jobManager.request(new AddSourceModuleRequest(this, module));
	}

	public void reconciled(ISourceModule workingCopy,
			IDLTKLanguageToolkit toolkit) {
		jobManager.request(new ReconcileSourceModuleRequest(this, workingCopy));
	}

	public void removeLibrary(IScriptProject project, IPath path) {
		jobManager.requestIfNotWaiting(new RemoveContainerRequest(this, path,
				DLTKLanguageManager.getLanguageToolkit(project)));
	}

	public void removeProject(IPath projectPath) {

		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(
				projectPath.toString());
		IScriptProject scriptProject = DLTKCore.create(project);
		IDLTKLanguageToolkit toolkit = DLTKLanguageManager
				.getLanguageToolkit(scriptProject);

		jobManager.requestIfNotWaiting(new RemoveContainerRequest(this,
				projectPath, toolkit));
	}

	public void removeProjectFragment(IScriptProject project, IPath path) {
		jobManager.requestIfNotWaiting(new RemoveContainerRequest(this, path,
				DLTKLanguageManager.getLanguageToolkit(project)));
	}

	public void removeSourceModule(IScriptProject project, String path) {
		IIndexer indexer = getIndexer(project);
		if (indexer == null) {
			return;
		}
		indexer.removeDocument(project.getPath(), path);
	}

	public void startIndexing() {
		jobManager.reset();

		final IWorkspace workspace = ResourcesPlugin.getWorkspace();
		try {
			IScriptProject[] projects = DLTKCore.create(workspace.getRoot())
					.getScriptProjects();

			for (int i = 0; i < projects.length; ++i) {
				jobManager.requestIfNotWaiting(new ProjectRequest(this,
						projects[i]));
			}
		} catch (Exception e) {
			DLTKCore
					.error("An exception is thrown while indexing workspace", e);
		}
	}

	void removeSourceModule(ISourceModule sourceModule) {
		IModelElement projectFragment = sourceModule
				.getAncestor(IModelElement.PROJECT_FRAGMENT);
		IPath containerPath = projectFragment.getPath();
		String relativePath = Util.relativePath(sourceModule.getPath(),
				containerPath.segmentCount());

		IIndexer indexer = getIndexer(projectFragment);
		if (indexer == null) {
			return;
		}
		indexer.removeDocument(containerPath, relativePath);
	}
}
