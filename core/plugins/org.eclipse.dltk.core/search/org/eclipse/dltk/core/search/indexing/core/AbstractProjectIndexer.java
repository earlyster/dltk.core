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
package org.eclipse.dltk.core.search.indexing.core;

import org.eclipse.core.runtime.IPath;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.DLTKLanguageManager;
import org.eclipse.dltk.core.IDLTKLanguageToolkit;
import org.eclipse.dltk.core.IExternalSourceModule;
import org.eclipse.dltk.core.IProjectFragment;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.core.search.SearchEngine;
import org.eclipse.dltk.core.search.SearchParticipant;
import org.eclipse.dltk.core.search.index.Index;
import org.eclipse.dltk.core.search.indexing.IProjectIndexer;
import org.eclipse.dltk.core.search.indexing.IndexManager;
import org.eclipse.dltk.internal.core.ModelManager;
import org.eclipse.dltk.internal.core.search.DLTKSearchDocument;
import org.eclipse.dltk.internal.core.search.LazyDLTKSearchDocument;
import org.eclipse.dltk.internal.core.search.processing.IJob;
import org.eclipse.osgi.util.NLS;

public abstract class AbstractProjectIndexer implements IProjectIndexer,
		IProjectIndexer.Internal {

	private final IndexManager manager = ModelManager.getModelManager()
			.getIndexManager();

	public void request(IJob job) {
		manager.request(job);
	}

	public void requestIfNotWaiting(IJob job) {
		manager.requestIfNotWaiting(job);
	}

	public IndexManager getIndexManager() {
		return manager;
	}

	public void indexProject(IScriptProject project) {
		final ProjectRequest request = new ProjectRequest(this, project);
		requestIfNotWaiting(request);
	}

	public void indexLibrary(IScriptProject project, IPath path) {
		try {
			final IProjectFragment fragment = project.findProjectFragment(path);
			if (fragment != null) {
				if (!path.segment(0).equals(IndexManager.SPECIAL_BUILTIN)) {
					final IndexRequest request = new ExternalProjectFragmentRequest(
							this, fragment, DLTKLanguageManager
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
			DLTKCore.error("Failed to index fragment:" + path.toString(), e);
		}
		if (fragmentToIndex == null || !fragmentToIndex.isExternal()
				|| fragmentToIndex.isBuiltin()) {
			requestIfNotWaiting(new ProjectRequest(this, project));
			return;
		}
		requestIfNotWaiting(new ExternalProjectFragmentRequest(this,
				fragmentToIndex, DLTKLanguageManager
						.getLanguageToolkit(project)));
	}

	public void indexSourceModule(ISourceModule module,
			IDLTKLanguageToolkit toolkit) {
		request(new SourceModuleRequest(this, module, toolkit));
	}

	public void reconciled(ISourceModule workingCopy,
			IDLTKLanguageToolkit toolkit) {
		request(new ReconcileSourceModuleRequest(this, workingCopy, toolkit));
	}

	public void removeProjectFragment(IScriptProject project, IPath sourceFolder) {
		// TODO optimize
		requestIfNotWaiting(new ProjectRequest(this, project));
	}

	public void removeSourceModule(IScriptProject project, String path) {
		request(new SourceModuleRemoveRequest(this, project, path));
	}

	public void removeProject(IPath projectPath) {
		requestIfNotWaiting(new RemoveIndexRequest(this, projectPath));
	}

	public void removeLibrary(IScriptProject project, IPath path) {
		requestIfNotWaiting(new RemoveIndexRequest(this, path));
	}

	public void startIndexing() {
		//
	}

	/**
	 * @since 2.0
	 */
	public boolean wantRefreshOnStart() {
		return true;
	}

	public void indexSourceModule(Index index, IDLTKLanguageToolkit toolkit,
			ISourceModule module, IPath containerPath) {
		final SearchParticipant participant = SearchEngine
				.getDefaultSearchParticipant();
		final IPath path = module.getPath();
		final DLTKSearchDocument document = new LazyDLTKSearchDocument(path
				.toString(), containerPath, null, participant,
				module instanceof IExternalSourceModule, module
						.getScriptProject().getProject());
		document.toolkit = toolkit;
		// try {
		// document.setCharContents(module.getSourceAsCharArray());
		// } catch (ModelException e) {
		// if (DLTKCore.DEBUG) {
		// e.printStackTrace();
		// }
		// }
		final String relativePath = SourceIndexUtil.containerRelativePath(
				containerPath, module, path);
		document.setContainerRelativePath(relativePath);

		index.remove(relativePath);
		document.setIndex(index);
		// new MixinIndexer(document, module).indexDocument();
		doIndexing(document, module);

	}

	public abstract void doIndexing(DLTKSearchDocument document,
			ISourceModule module);

	public Index getProjectIndex(IScriptProject project) {
		return getProjectIndex(project.getProject().getFullPath());
	}

	public Index getProjectIndex(IPath path) {
		return getIndexManager().getIndex(path, true, true);
	}

	public Index getProjectFragmentIndex(IProjectFragment fragment) {
		return getIndexManager().getIndex(fragment.getPath(), true, true);
	}

}
