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

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.DLTKLanguageManager;
import org.eclipse.dltk.core.IDLTKLanguageToolkit;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.IScriptFolder;
import org.eclipse.dltk.core.ISourceElementParser;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.ISourceModuleInfoCache;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.core.ISourceModuleInfoCache.ISourceModuleInfo;
import org.eclipse.dltk.core.caching.IContentCache;
import org.eclipse.dltk.core.caching.StructureModelProcessor;
import org.eclipse.dltk.core.environment.EnvironmentPathUtils;
import org.eclipse.dltk.core.environment.IFileHandle;
import org.eclipse.dltk.core.search.IDLTKSearchScope;
import org.eclipse.dltk.core.search.SearchDocument;
import org.eclipse.dltk.core.search.indexing.AbstractIndexer;
import org.eclipse.dltk.core.search.indexing.InternalSearchDocument;
import org.eclipse.dltk.core.search.indexing.SourceIndexerRequestor;
import org.eclipse.dltk.internal.core.ModelManager;

public class StructureIndexer extends AbstractIndexer {
	private static class ParserInput implements
			org.eclipse.dltk.compiler.env.ISourceModule {

		private final SearchDocument document;
		private ISourceModule module;

		public ParserInput(SearchDocument document, ISourceModule module) {
			this.document = document;
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
			return null;
		}

		public IModelElement getModelElement() {
			return module;
		}

		public IPath getScriptFolder() {
			return new Path(document.getPath()).removeLastSegments(1);
		}

		public String getSourceContents() {
			if (module != null) {
				try {
					return module.getSource();
				} catch (ModelException e) {
					e.printStackTrace();
				}
			}
			return document.getContents();
		}

		public char[] getFileName() {
			return document.getPath().toCharArray();
		}

	}

	private final ISourceModule sourceModule;
	static long maxWorkTime = 0;

	public StructureIndexer(SearchDocument document, ISourceModule module) {
		super(document);
		this.sourceModule = module;
	}

	public void indexDocument() {
		long started = System.currentTimeMillis();
		IDLTKLanguageToolkit toolkit = this.document.getToolkit();
		if (toolkit == null) {
			toolkit = DLTKLanguageManager.findToolkit(new Path(this.document
					.getPath()));
		}
		if (toolkit == null) {
			return;
		}
		SourceIndexerRequestor requestor = ((InternalSearchDocument) this.document).requestor;
		if (requestor == null) {
			requestor = ModelManager.getModelManager().indexManager
					.getSourceRequestor(sourceModule.getScriptProject());
		}
		requestor.setIndexer(this);
		if (!this.document.isExternal()) {
			String pkgName = ""; //$NON-NLS-1$
			IScriptFolder folder = (IScriptFolder) sourceModule.getParent();
			pkgName = folder.getElementName();
			requestor.setPackageName(pkgName);
		} else {
			IPath path = new Path(this.document.getPath());
			String ppath = path.toString();
			String pkgName = (new Path(ppath.substring(ppath
					.indexOf(IDLTKSearchScope.FILE_ENTRY_SEPARATOR) + 1))
					.removeLastSegments(1)).toString();
			requestor.setPackageName(pkgName);
		}

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
			ISourceElementParser parser = ((InternalSearchDocument) this.document)
					.getParser();
			if (parser == null) {
				parser = DLTKLanguageManager
						.getSourceElementParser(sourceModule);
			}
			ISourceModuleInfoCache cache = ModelManager.getModelManager()
					.getSourceModuleInfoCache();
			ISourceModuleInfo info = cache.get(sourceModule);
			parser.setRequestor(requestor);
			parser.parseSourceModule(new ParserInput(document, sourceModule),
					info);
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
