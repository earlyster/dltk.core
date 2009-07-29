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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.eclipse.dltk.core.IDLTKLanguageToolkit;
import org.eclipse.dltk.core.IProjectFragment;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.core.environment.IEnvironment;
import org.eclipse.dltk.core.environment.IFileHandle;
import org.eclipse.dltk.core.search.index.Index;
import org.eclipse.dltk.core.search.indexing.IProjectIndexer;

/**
 * @since 2.0
 */
public class BuiltinProjectFragmentRequest extends
		ExternalProjectFragmentRequest {

	private final long lastModified;

	/**
	 * @param fragment
	 * @param toolkit
	 */
	public BuiltinProjectFragmentRequest(IProjectIndexer indexer,
			IProjectFragment fragment, IDLTKLanguageToolkit toolkit,
			long lastModified) {
		super(indexer, fragment, toolkit);
		this.lastModified = lastModified;
	}

	protected List checkChanges(Index index, Collection modules,
			IPath containerPath, IEnvironment environment)
			throws ModelException, IOException {
		final long indexLastModified = index.getIndexFile().lastModified();
		if (lastModified > indexLastModified) {
			final List changes = new ArrayList();
			final String[] documentNames = queryDocumentNames(index);
			if (documentNames != null) {
				for (int i = 0; i < documentNames.length; ++i) {
					changes.add(documentNames[i]);
				}
			}
			changes.addAll(modules);
			return changes;
		}
		return super.checkChanges(index, modules, containerPath, environment,
				new HashSet<IFileHandle>());
	}

	protected IEnvironment getEnvironment() {
		return null;
	}

	public boolean equals(Object obj) {
		return obj instanceof BuiltinProjectFragmentRequest
				&& super.equals(obj);
	}

}
