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
package org.eclipse.dltk.core.search.indexing;

import org.eclipse.core.runtime.IPath;
import org.eclipse.dltk.core.IDLTKLanguageToolkit;
import org.eclipse.dltk.core.IProjectFragment;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.search.index.Index;
import org.eclipse.dltk.internal.core.search.processing.IJob;

public interface IProjectIndexer {

	public interface Internal extends IProjectIndexer {

		void indexSourceModule(Index index, IDLTKLanguageToolkit toolkit,
				ISourceModule change, IPath containerPath);

		void request(IJob request);

		Index getProjectFragmentIndex(IProjectFragment fragment);

		Index getProjectIndex(IScriptProject project);

		IndexManager getIndexManager();

	}

	/**
	 * @param project
	 */
	void indexProject(IScriptProject project);

	void indexLibrary(IScriptProject project, IPath path);

	/**
	 * @param module
	 * @param toolkit
	 */
	void indexSourceModule(ISourceModule module, IDLTKLanguageToolkit toolkit);

	/**
	 * @param project
	 * @param path
	 */
	void removeSourceModule(IScriptProject project, String path);

	/**
	 * @param project
	 * @param folder
	 */
	void indexProjectFragment(IScriptProject project, IPath path);

	/**
	 * @param scriptProject
	 * @param sourceFolder
	 */
	void removeProjectFragment(IScriptProject project, IPath path);

	/**
	 * @param projectPath
	 */
	void removeProject(IPath projectPath);

	/**
	 * @param project
	 * @param path
	 */
	void removeLibrary(IScriptProject project, IPath path);

	/**
	 * @param workingCopy
	 * @param toolkit
	 */
	void reconciled(ISourceModule workingCopy, IDLTKLanguageToolkit toolkit);

	/**
	 * Is called just after initialization to verify the indexes
	 */
	void startIndexing();

	/**
	 * Disable this indexer for the given set of natures
	 * 
	 * @since 2.0
	 */
	void disableForNature(String natureId);
}
