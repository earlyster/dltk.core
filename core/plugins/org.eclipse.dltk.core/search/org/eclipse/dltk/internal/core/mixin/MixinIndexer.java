/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.eclipse.dltk.internal.core.mixin;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.DLTKLanguageManager;
import org.eclipse.dltk.core.IDLTKLanguageToolkit;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.mixin.IMixinParser;
import org.eclipse.dltk.core.mixin.IMixinRequestor;
import org.eclipse.dltk.core.search.SearchDocument;
import org.eclipse.dltk.core.search.index.MixinIndex;
import org.eclipse.dltk.core.search.indexing.AbstractIndexer;

public class MixinIndexer extends AbstractIndexer {

	private final ISourceModule sourceModule;

	public MixinIndexer(SearchDocument document, ISourceModule module) {
		super(document);
		this.sourceModule = module;
	}

	public void indexDocument() {
		IDLTKLanguageToolkit toolkit = this.document.getToolkit();
		if (toolkit == null) {
			toolkit = DLTKLanguageManager.findToolkit(new Path(this.document
					.getPath()));
		}
		if (toolkit == null) {
			return;
		}
		try {
			IMixinParser parser = MixinManager.getMixinParser(toolkit
					.getNatureId());
			if (parser != null) {
				final MixinIndexRequestor requestor = new MixinIndexRequestor();
				parser.setRequirestor(requestor);
				parser.parserSourceModule(false, this.sourceModule);
				if (requestor.count == 0) {
					((MixinIndex) document.getIndex()).addDocumentName(document
							.getContainerRelativePath());
				}
			}
		} catch (CoreException e) {
			DLTKCore.error("Error in MixinIndexer", e); //$NON-NLS-1$
		}
	}

	private class MixinIndexRequestor implements IMixinRequestor {
		int count = 0;

		public void reportElement(ElementInfo info) {
			if (info.key.length() > 0) {
				addIndexEntry(MIXIN, info.key.toCharArray());
				++count;
			}
		}
	}
}
