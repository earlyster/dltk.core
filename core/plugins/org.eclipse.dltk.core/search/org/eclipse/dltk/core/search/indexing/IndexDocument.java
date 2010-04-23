/*******************************************************************************
 * Copyright (c) 2010 xored software, Inc.
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
import org.eclipse.dltk.core.IExternalSourceModule;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.search.SearchDocument;
import org.eclipse.dltk.core.search.SearchParticipant;
import org.eclipse.dltk.core.search.index.Index;
import org.eclipse.dltk.core.search.indexing.core.SourceIndexUtil;

public class IndexDocument {

	private final IDLTKLanguageToolkit toolkit;
	private final ISourceModule module;

	private final Index index;
	private IPath path = null;
	private final String containerRelativePath;

	/**
	 * @param toolkit
	 * @param module
	 * @param containerPath
	 * @param index
	 */
	public IndexDocument(IDLTKLanguageToolkit toolkit, ISourceModule module,
			IPath containerPath, Index index) {
		this.toolkit = toolkit;
		this.module = module;
		this.index = index;
		this.path = module.getPath();
		this.containerRelativePath = SourceIndexUtil.containerRelativePath(
				containerPath, module, path);
	}

	public ISourceModule getSourceModule() {
		return module;
	}

	public IDLTKLanguageToolkit getToolkit() {
		return toolkit;
	}

	/**
	 * @return
	 */
	public IPath getPath() {
		return path;
	}

	/**
	 * @return
	 */
	public String getContainerRelativePath() {
		return containerRelativePath;
	}

	/**
	 * @return
	 */
	public boolean isExternal() {
		return module instanceof IExternalSourceModule;
	}

	/**
	 * Adds the given index entry (category and key) coming from this document
	 * to the index. This method must be called from
	 * {@link SearchParticipant#indexDocument(SearchDocument document, org.eclipse.core.runtime.IPath indexPath)}
	 * .
	 * 
	 * @param category
	 *            the category of the index entry
	 * @param key
	 *            the key of the index entry
	 */
	public void addIndexEntry(char[] category, char[] key) {
		index.addIndexEntry(category, key, containerRelativePath);
	}

	/**
	 * @return
	 */
	public Index getIndex() {
		return index;
	}

}
