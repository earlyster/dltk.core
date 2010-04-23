/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.eclipse.dltk.internal.core.mixin;

import java.io.IOException;
import java.io.InputStream;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IDLTKLanguageToolkit;
import org.eclipse.dltk.core.caching.IContentCache;
import org.eclipse.dltk.core.caching.MixinModelProcessor;
import org.eclipse.dltk.core.environment.EnvironmentPathUtils;
import org.eclipse.dltk.core.environment.IFileHandle;
import org.eclipse.dltk.core.mixin.IMixinParser;
import org.eclipse.dltk.core.mixin.IMixinRequestor;
import org.eclipse.dltk.core.search.index.MixinIndex;
import org.eclipse.dltk.core.search.indexing.AbstractIndexer;
import org.eclipse.dltk.core.search.indexing.IndexDocument;
import org.eclipse.dltk.internal.core.ModelManager;

public class MixinIndexer extends AbstractIndexer {

	public MixinIndexer(IndexDocument document) {
		super(document);
	}

	@Override
	public void indexDocument() {
		IDLTKLanguageToolkit toolkit = this.document.getToolkit();
		if (toolkit == null) {
			return;
		}
		boolean performed = false;
		// Try to restore index from persistent index
		IFileHandle handle = EnvironmentPathUtils.getFile(document
				.getSourceModule());
		if (handle != null) {
			// handle is null for built-in modules.
			IContentCache coreCache = ModelManager.getModelManager()
					.getCoreCache();
			InputStream stream = coreCache.getCacheEntryAttribute(handle,
					IContentCache.MIXIN_INDEX);
			if (stream != null) {
				// Found cached structure index, try to restore
				try {
					final MixinIndexRequestor requestor = new MixinIndexRequestor();
					MixinModelProcessor processor = new MixinModelProcessor(
							stream, requestor);
					processor.process();
					stream.close();
					performed = true;
					if (requestor.count == 0) {
						((MixinIndex) document.getIndex())
								.addDocumentName(document
										.getContainerRelativePath());
					}
				} catch (IOException e) {
					performed = false;
					if (DLTKCore.DEBUG) {
						e.printStackTrace();
					}
				}
			}
		}

		if (!performed) {
			try {
				IMixinParser parser = MixinManager.getMixinParser(toolkit
						.getNatureId());
				if (parser != null) {
					final MixinIndexRequestor requestor = new MixinIndexRequestor();
					parser.setRequirestor(requestor);
					parser
							.parserSourceModule(false, document
									.getSourceModule());
					if (requestor.count == 0) {
						((MixinIndex) document.getIndex())
								.addDocumentName(document
										.getContainerRelativePath());
					}
				}
			} catch (CoreException e) {
				DLTKCore.error("Error in MixinIndexer", e); //$NON-NLS-1$
			}
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
