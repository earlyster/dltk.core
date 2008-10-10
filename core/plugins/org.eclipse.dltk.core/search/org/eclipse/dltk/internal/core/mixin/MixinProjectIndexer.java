/*******************************************************************************
 * Copyright (c) 2008 xored software, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     xored software, Inc. - initial API and Implementation (Alex Panchenko)
 *******************************************************************************/
package org.eclipse.dltk.internal.core.mixin;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.DLTKLanguageManager;
import org.eclipse.dltk.core.IDLTKLanguageToolkit;
import org.eclipse.dltk.core.IProjectFragment;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.search.indexing.IProjectIndexer;
import org.eclipse.dltk.core.search.indexing.IndexManager;
import org.eclipse.dltk.internal.core.ModelManager;
import org.eclipse.dltk.internal.core.search.processing.IJob;
import org.eclipse.osgi.util.NLS;

public class MixinProjectIndexer implements IProjectIndexer {

	private final IndexManager manager = ModelManager.getModelManager()
			.getIndexManager();

	private void request(IJob job) {
		manager.request(job);
	}

	private void requestIfNotWaiting(IJob job) {
		if (!manager.isJobWaiting(job)) {
			manager.request(job);
		}
	}

	public void indexProject(IScriptProject project) {
		final MixinProjectRequest request = new MixinProjectRequest(project,
				true);
		requestIfNotWaiting(request);
	}

	public void indexLibrary(IScriptProject project, IPath path) {
		try {
			final IProjectFragment fragment = project.findProjectFragment(path);
			if (fragment != null) {
				if (!path.segment(0).equals(IndexManager.SPECIAL_BUILTIN)) {
					final MixinIndexRequest request = new MixinExternalProjectFragmentRequest(
							fragment, DLTKLanguageManager
									.getLanguageToolkit(fragment));
					requestIfNotWaiting(request);
				}
			} else {
				DLTKCore.warn(NLS.bind(
						Messages.MixinIndexer_unknownProjectFragment, path));
			}
		} catch (Exception e) {
			DLTKCore.error(NLS.bind(Messages.MixinIndexer_indexLibraryError,
					path), e);
		}
	}

	public void indexProjectFragment(IScriptProject project, IPath path) {
		// TODO optimize
		requestIfNotWaiting(new MixinProjectRequest(project, false));
	}

	public void indexSourceModule(ISourceModule module,
			IDLTKLanguageToolkit toolkit) {
		request(new MixinSourceModuleRequest(module, toolkit));
	}

	public void reconciled(ISourceModule workingCopy,
			IDLTKLanguageToolkit toolkit) {
		request(new MixinReconcileSourceModuleRequest(workingCopy, toolkit));
	}

	public void removeProjectFragment(IScriptProject project, IPath sourceFolder) {
		// TODO optimize
		requestIfNotWaiting(new MixinProjectRequest(project, false));
	}

	public void removeSourceModule(IScriptProject project, String path) {
		request(new MixinSourceModuleRemoveRequest(project, path));
	}

	public void removeProject(IPath projectPath) {
		requestIfNotWaiting(new RemoveIndexRequest(new Path(
				IndexManager.SPECIAL_MIXIN + projectPath.toString())));
	}

	public void removeLibrary(IScriptProject project, IPath path) {
		requestIfNotWaiting(new RemoveIndexRequest(new Path(
				IndexManager.SPECIAL_MIXIN + path.toString())));
	}

	public void startIndexing() {
		final IWorkspace workspace = ResourcesPlugin.getWorkspace();
		try {
			IScriptProject[] projects = DLTKCore.create(workspace.getRoot())
					.getScriptProjects();
			for (int i = 0; i < projects.length; ++i) {
				requestIfNotWaiting(new MixinProjectRequest(projects[i], false));
			}
		} catch (Exception e) {
			DLTKCore.error(Messages.MixinIndexer_startIndexingError, e);

			if (AbstractJob.DEBUG) {
				e.printStackTrace();
			}
		}
	}

}
