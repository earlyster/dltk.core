/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.eclipse.dltk.internal.core.structure;

import java.io.IOException;
import java.io.InputStream;

import org.eclipse.dltk.compiler.CharOperation;
import org.eclipse.dltk.compiler.env.IModuleSource;
import org.eclipse.dltk.compiler.util.Util;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.DLTKLanguageManager;
import org.eclipse.dltk.core.IDLTKLanguageToolkit;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.ISourceElementParser;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.core.caching.IContentCache;
import org.eclipse.dltk.core.caching.StructureModelProcessor;
import org.eclipse.dltk.core.environment.EnvironmentPathUtils;
import org.eclipse.dltk.core.environment.IFileHandle;
import org.eclipse.dltk.core.model.binary.IBinaryElementParser;
import org.eclipse.dltk.core.model.binary.IBinaryModule;
import org.eclipse.dltk.core.search.indexing.AbstractIndexer;
import org.eclipse.dltk.core.search.indexing.IndexDocument;
import org.eclipse.dltk.core.search.indexing.SourceIndexerRequestor;
import org.eclipse.dltk.internal.core.ModelManager;

public class StructureIndexer extends AbstractIndexer {
	private static class ParserInput implements
			org.eclipse.dltk.compiler.env.IModuleSource {

		private ISourceModule module;

		public ParserInput(ISourceModule module) {
			this.module = module;
		}

		public char[] getContentsAsCharArray() {
			try {
				return module.getSourceAsCharArray();
			} catch (ModelException e) {
				if (DLTKCore.DEBUG) {
					e.printStackTrace();
				}
			}
			return CharOperation.NO_CHAR;
		}

		public IModelElement getModelElement() {
			return module;
		}

		public String getSourceContents() {
			if (module != null) {
				try {
					return module.getSource();
				} catch (ModelException e) {
					if (DLTKCore.DEBUG) {
						e.printStackTrace();
					}
				}
			}
			return Util.EMPTY_STRING;
		}

		public String getFileName() {
			return module.getElementName();
		}

	}

	static long maxWorkTime = 0;

	public StructureIndexer(IndexDocument document) {
		super(document);
	}

	@Override
	public void indexDocument() {
		long started = System.currentTimeMillis();
		IDLTKLanguageToolkit toolkit = this.document.getToolkit();
		if (toolkit == null) {
			return;
		}
		final ISourceModule sourceModule = document.getSourceModule();
		SourceIndexerRequestor requestor = ModelManager.getModelManager().indexManager
				.getSourceRequestor(sourceModule.getScriptProject());
		requestor.setIndexer(this);

		boolean performed = false;
		// Try to restore index from persistent cache
		IFileHandle handle = EnvironmentPathUtils.getFile(sourceModule);
		if (handle != null) {
			// handle is null for built-in modules.
			IContentCache coreCache = ModelManager.getModelManager()
					.getCoreCache();
			InputStream stream = coreCache.getCacheEntryAttribute(handle,
					IContentCache.STRUCTURE_INDEX);
			if (stream != null) {
				// Found cached structure index, try to restore
				try {
					StructureModelProcessor processor = new StructureModelProcessor(
							stream, requestor);
					processor.perform();
					performed = true;
				} catch (IOException e) {
					performed = false;
					if (DLTKCore.DEBUG) {
						e.printStackTrace();
					}
				} finally {
					try {
						stream.close();
					} catch (IOException e) {
						if (DLTKCore.DEBUG) {
							e.printStackTrace();
						}
					}
				}
			}
		}

		if (!performed) {
			if (!sourceModule.isBinary()) {
				ISourceElementParser parser = DLTKLanguageManager
						.getSourceElementParser(sourceModule);
				if (parser == null)
					return;
				parser.setRequestor(requestor);
				if (sourceModule instanceof IModuleSource) {
					parser.parseSourceModule((IModuleSource) sourceModule);
				} else {
					parser.parseSourceModule(new ParserInput(sourceModule));
				}
			} else {
				IBinaryElementParser parser = DLTKLanguageManager
						.getBinaryElementParser(sourceModule);
				if (parser == null)
					return;
				parser.setRequestor(requestor);
				parser.parseBinaryModule((IBinaryModule) sourceModule);
			}
		}

		long ended = System.currentTimeMillis();

		if (ended - started > maxWorkTime) {
			maxWorkTime = ended - started;
			if (DLTKCore.VERBOSE) {
				System.err.println("Max indexDocument() work time " //$NON-NLS-1$
						+ maxWorkTime + " on " + document.getPath()); //$NON-NLS-1$
			}
		}
	}
}
