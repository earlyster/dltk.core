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
package org.eclipse.dltk.core.index2;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.IPath;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.DLTKLanguageManager;
import org.eclipse.dltk.core.IDLTKLanguageToolkit;
import org.eclipse.dltk.core.IProjectFragment;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.core.search.indexing.IProjectIndexer;
import org.eclipse.dltk.core.search.indexing.IndexManager;
import org.eclipse.dltk.internal.core.ModelManager;
import org.eclipse.dltk.internal.core.index2.AbstractIndexRequest;
import org.eclipse.dltk.internal.core.index2.AddSourceModuleRequest;
import org.eclipse.dltk.internal.core.index2.ExternalProjectFragmentRequest;
import org.eclipse.dltk.internal.core.index2.ProgressJob;
import org.eclipse.dltk.internal.core.index2.ProjectRequest;
import org.eclipse.dltk.internal.core.index2.ReconcileSourceModuleRequest;
import org.eclipse.dltk.internal.core.index2.RemoveContainerRequest;
import org.eclipse.dltk.internal.core.index2.RemoveSourceModuleRequest;
import org.eclipse.osgi.util.NLS;

/**
 * @since 2.0
 */
public class ProjectIndexer2 implements IProjectIndexer {

	private final IndexManager jobManager = ModelManager.getModelManager()
			.getIndexManager();

	protected final Set<String> disabledNatures = new HashSet<String>();

	public void indexLibrary(IScriptProject project, IPath path) {
		if (disabledNatures.contains(DLTKLanguageManager.getLanguageToolkit(
				project).getNatureId())) {
			return;
		}
		try {
			IProjectFragment fragment = project.findProjectFragment(path);
			if (fragment != null) {
				AbstractIndexRequest request = new ExternalProjectFragmentRequest(
						this, fragment, new ProgressJob(jobManager));
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
		if (disabledNatures.contains(DLTKLanguageManager.getLanguageToolkit(
				project).getNatureId())) {
			return;
		}
		ProjectRequest request = new ProjectRequest(this, project,
				new ProgressJob(jobManager));
		jobManager.requestIfNotWaiting(request);
	}

	public void indexProjectFragment(IScriptProject project, IPath path) {
		if (disabledNatures.contains(DLTKLanguageManager.getLanguageToolkit(
				project).getNatureId())) {
			return;
		}
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
			jobManager.requestIfNotWaiting(request);
			return;
		}

		ExternalProjectFragmentRequest request = new ExternalProjectFragmentRequest(
				this, fragmentToIndex, new ProgressJob(jobManager));
		jobManager.requestIfNotWaiting(request);
	}

	public void indexSourceModule(ISourceModule module,
			IDLTKLanguageToolkit toolkit) {
		if (disabledNatures.contains(toolkit.getNatureId())) {
			return;
		}
		jobManager.request(new AddSourceModuleRequest(this, module, null));
	}

	public void reconciled(ISourceModule workingCopy,
			IDLTKLanguageToolkit toolkit) {
		if (disabledNatures.contains(toolkit.getNatureId())) {
			return;
		}
		jobManager.request(new ReconcileSourceModuleRequest(this, workingCopy,
				null));
	}

	public void removeLibrary(IScriptProject project, IPath path) {
		RemoveContainerRequest request = new RemoveContainerRequest(this, path,
				null);
		jobManager.requestIfNotWaiting(request);
	}

	public void removeProject(IPath projectPath) {
		RemoveContainerRequest request = new RemoveContainerRequest(this,
				projectPath, null);
		jobManager.requestIfNotWaiting(request);
	}

	public void removeProjectFragment(IScriptProject project, IPath path) {
		RemoveContainerRequest request = new RemoveContainerRequest(this, path,
				null);
		jobManager.requestIfNotWaiting(request);
	}

	public void removeSourceModule(IScriptProject project, String path) {
		jobManager.request(new RemoveSourceModuleRequest(this, project
				.getPath(), path, null));
	}

	public void startIndexing() {
		//
	}

	public boolean wantRefreshOnStart() {
		return true;
	}

	/**
	 * @param request
	 */
	public void request(AbstractIndexRequest request) {
		jobManager.request(request);
	}

	public void disableForNature(String natureId) {
		disabledNatures.add(natureId);
	}
}
