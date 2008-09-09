/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.eclipse.dltk.core.search.indexing;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.dltk.compiler.env.CompilerSourceCode;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IProjectFragment;
import org.eclipse.dltk.core.IScriptFolder;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.ISourceElementParser;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.ISourceModuleInfoCache;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.core.ISourceModuleInfoCache.ISourceModuleInfo;
import org.eclipse.dltk.core.environment.EnvironmentPathUtils;
import org.eclipse.dltk.core.search.IDLTKSearchScope;
import org.eclipse.dltk.core.search.SearchDocument;
import org.eclipse.dltk.internal.core.ModelManager;

/**
 * A SourceIndexer indexes script files using a script parser. The following
 * items are indexed:
 * <ul>
 * <li>Declarations of:
 * <ul>
 * <li>Classes;
 * <li>Interfaces;
 * <li>Methods;
 * <li>Fields;
 * </ul>
 * <li>References to:
 * <ul>
 * <li>Methods (with number of arguments);
 * <li>Fields;
 * <li>Types;
 * <li>Constructors.
 * </ul>
 * </ul>
 */
public class SourceIndexer extends AbstractIndexer {

	static long maxWorkTime = 0;

	public SourceIndexer(SearchDocument document) {
		super(document);
	}

	public void indexDocument() {

		long started = System.currentTimeMillis();
		ISourceModule module = null;
		ISourceModuleInfo info = null;

		// Create a new Parser
		SourceIndexerRequestor requestor = ((InternalSearchDocument) this.document).requestor;
		IPath path = new Path(this.document.getPath());
		ISourceElementParser parser = ((InternalSearchDocument) this.document).parser;
		if (!this.document.isExternal()) {
			IProject project = document.getProject();
			if (project == null) {
				project = ResourcesPlugin.getWorkspace().getRoot().getProject(
						path.segment(0));
			}

			IScriptProject scriptProject = DLTKCore.create(project);

			if (requestor == null) {
				requestor = ModelManager.getModelManager().indexManager
						.getSourceRequestor(scriptProject);
			}
			requestor.setIndexer(this);

			if (parser == null) {
				parser = ModelManager.getModelManager().indexManager
						.getSourceElementParser(scriptProject);
			}
			parser.setRequestor(requestor);
			String pkgName = ""; //$NON-NLS-1$
			IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
			if (file.exists()) {
				module = (ISourceModule) DLTKCore.create(file);
				if (module != null) {
					// sourceModule = module;
					IScriptFolder folder = (IScriptFolder) module.getParent();
					pkgName = folder.getElementName();
				}
			}
			requestor.setPackageName(pkgName);
			// We need to get already
			// Launch the parser
			// char[] source = null;
			// char[] name = null;
			// try {
			// source = document.getCharContents();
			// name = documentPath.toCharArray();
			// } catch (Exception e) {
			// // ignore
			// }
			// if (source == null || name == null)
			// return; // could not retrieve document info (e.g. resource was
			// discarded)

			/**
			 * Using cache to build module.
			 */
			if (module != null) {
				ISourceModuleInfoCache cache = ModelManager.getModelManager()
						.getSourceModuleInfoCache();
				info = cache.get(module);
			}

		} else { // This is for external documents
			if (parser == null || requestor == null) {
				return;
			}
			parser.setRequestor(requestor);
			requestor.setIndexer(this);
			String ppath = path.toString();
			if (DLTKCore.DEBUG) {
				System.err.println("TODO: Correct me please..."); //$NON-NLS-1$
			}
			String pkgName = (new Path(ppath.substring(ppath
					.indexOf(IDLTKSearchScope.FILE_ENTRY_SEPARATOR) + 1))
					.removeLastSegments(1)).toString();
			requestor.setPackageName(pkgName);
			// Launch the parser
			// char[] source = null;
			// char[] name = null;
			// try {
			// source = document.getCharContents();
			// name = documentPath.toCharArray();
			// } catch (Exception e) {
			// // ignore
			// }
			// if (source == null || name == null)
			// return; // could not retrieve document info (e.g. resource was
			// discarded)

			// We need to obtain ISourceModule handle to do caching. This will
			// improve parsing performance.

			if (document.getProject() != null) {
				IProject project = document.getProject();
				IScriptProject scriptProject = DLTKCore.create(project);
				try {
					IProjectFragment[] fragments = scriptProject
							.getProjectFragments();
					IProjectFragment frag = null;
					for (int i = 0; i < fragments.length; i++) {
						IPath fragmentPath = EnvironmentPathUtils
								.getLocalPath(fragments[i].getPath());
						if (fragments[i].isExternal()
								&& fragmentPath.isPrefixOf(document.fullPath)) {
							if (frag != null
									&& frag.getPath().isPrefixOf(
											fragments[i].getPath())) {
								frag = fragments[i];
							} else {
								frag = fragments[i];
							}
						}
					}
					if (frag != null) {
						IPath fragmentRelativePath = document.fullPath
								.removeFirstSegments(frag.getPath()
										.segmentCount());
						IScriptFolder folder = frag
								.getScriptFolder(fragmentRelativePath
										.removeLastSegments(1));
						module = folder.getSourceModule(document.fullPath
								.lastSegment());
						if (module.exists()) {
							info = ModelManager.getModelManager()
									.getSourceModuleInfoCache().get(module);
						}
					}
				} catch (ModelException e) {
					if (DLTKCore.DEBUG) {
						e.printStackTrace();
					}
				}
			}

		}
		if (module instanceof org.eclipse.dltk.compiler.env.ISourceModule) {
			parser.parseSourceModule(
					(org.eclipse.dltk.compiler.env.ISourceModule) module, info);
		} else {
			parser.parseSourceModule(new CompilerSourceCode(document
					.getContents()), info);
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
