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

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.dltk.core.DLTKLanguageManager;
import org.eclipse.dltk.core.IProjectFragment;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.mixin.MixinModelRegistry;
import org.eclipse.dltk.core.search.index.Index;
import org.eclipse.dltk.core.search.indexing.IndexManager;
import org.eclipse.dltk.core.search.indexing.core.AbstractProjectIndexer;
import org.eclipse.dltk.core.search.indexing.core.RemoveIndexRequest;
import org.eclipse.dltk.internal.core.search.DLTKSearchDocument;

public class MixinProjectIndexer extends AbstractProjectIndexer {

	public void doIndexing(DLTKSearchDocument document, ISourceModule module) {
		if (disabledNatures.contains(DLTKLanguageManager.getLanguageToolkit(
				module).getNatureId())) {
			return;
		}
		new MixinIndexer(document, module).indexDocument();
		MixinModelRegistry.clearKeysCache(DLTKLanguageManager
				.getLanguageToolkit(module));
	}

	public Index getProjectIndex(IPath path) {
		final String containerPath = path.getDevice() == null ? path.toString()
				: path.toOSString();
		return getIndexManager().getSpecialIndex(IndexManager.SPECIAL_MIXIN,
				path.toString(), containerPath);
	}

	public Index getProjectFragmentIndex(IProjectFragment fragment) {
		final String path = fragment.getPath().toString();
		return getIndexManager().getSpecialIndex(IndexManager.SPECIAL_MIXIN,
				path, path);
	}

	public void removeProject(IPath projectPath) {
		requestIfNotWaiting(new RemoveIndexRequest(this, new Path(
				IndexManager.SPECIAL_MIXIN + projectPath.toString())));
	}

	public void removeLibrary(IScriptProject project, IPath path) {
		requestIfNotWaiting(new RemoveIndexRequest(this, new Path(
				IndexManager.SPECIAL_MIXIN + path.toString())));
	}
}
