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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.dltk.compiler.CharOperation;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IDLTKLanguageToolkit;
import org.eclipse.dltk.core.IExternalSourceModule;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.IModelElementVisitor;
import org.eclipse.dltk.core.IProjectFragment;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.core.environment.EnvironmentManager;
import org.eclipse.dltk.core.environment.EnvironmentPathUtils;
import org.eclipse.dltk.core.environment.IEnvironment;
import org.eclipse.dltk.core.environment.IFileHandle;
import org.eclipse.dltk.core.search.index.Index;
import org.eclipse.dltk.core.search.indexing.IIndexConstants;
import org.eclipse.dltk.core.search.indexing.IProjectIndexer;
import org.eclipse.dltk.core.search.indexing.ReadWriteMonitor;
import org.eclipse.dltk.internal.core.BuiltinSourceModule;

public class ArchiveProjectFragmentRequest extends IndexRequest {

	protected final IProjectFragment fragment;
	protected final IDLTKLanguageToolkit toolkit;

	public ArchiveProjectFragmentRequest(IProjectIndexer indexer,
			IProjectFragment fragment, IDLTKLanguageToolkit toolkit) {
		super(indexer);
		this.fragment = fragment;
		this.toolkit = toolkit;
	}

	protected String getName() {
		return fragment.getElementName();
	}

	protected void run() throws CoreException, IOException {
		IEnvironment environment = EnvironmentManager.getEnvironment(fragment
				.getScriptProject());
		if (environment == null || !environment.connect()) {
			return;
		}
		final Set<ISourceModule> modules = getExternalSourceModules();
		final Index index = getIndexer().getProjectFragmentIndex(fragment);
		if (index == null) {
			return;
		}
		final IFileHandle archive = EnvironmentPathUtils.getFile(fragment
				.getPath());
		if (archive == null) {
			return;
		}
		final String signature = archive.lastModified() + "#"
				+ archive.length();
		final IPath containerPath = fragment.getPath();
		final List<Object> changes = checkChanges(index, modules,
				containerPath, signature);
		if (DEBUG) {
			log("changes.size=" + changes.size()); //$NON-NLS-1$
		}
		if (changes.isEmpty()) {
			return;
		}
		final ReadWriteMonitor imon = index.monitor;
		imon.enterWrite();
		try {
			index.separator = Index.JAR_SEPARATOR;
			index.addIndexEntry(IIndexConstants.STAMP, CharOperation.NO_CHAR,
					SIGNATURE_PREFIX + signature);
			for (Iterator<Object> i = changes.iterator(); !isCancelled
					&& i.hasNext();) {
				final Object change = i.next();
				if (change instanceof String) {
					index.remove((String) change);
				} else if (change instanceof ISourceModule) {
					ISourceModule module = (ISourceModule) change;
					getIndexer().indexSourceModule(index, toolkit, module,
							containerPath);
				}
			}

		} catch (Throwable e) {
			e.printStackTrace();
		} finally {
			try {
				index.save();
			} catch (IOException e) {
				DLTKCore.error("error saving index", e); //$NON-NLS-1$
			} finally {
				imon.exitWrite();
			}
		}
	}

	// TODO (alex) use content cache for it probably
	private static final String SIGNATURE_PREFIX = "###.LIBRARY.###.SIGNATURE.###";

	protected List<Object> checkChanges(Index index,
			Collection<ISourceModule> modules, IPath containerPath,
			String signature) throws ModelException, IOException {
		final String[] documents = queryDocumentNames(index);
		if (documents != null && documents.length != 0) {
			final List<Object> changes = new ArrayList<Object>();
			final Map<String, ISourceModule> m = collectSourceModulePaths(
					modules, containerPath);
			if (DEBUG) {
				log("documents.length=" + documents.length); //$NON-NLS-1$
				log("modules.size=" + modules.size()); //$NON-NLS-1$
				log("map.size=" + m.size()); //$NON-NLS-1$
			}
			boolean signatureOK = false;
			final List<ISourceModule> updates = new ArrayList<ISourceModule>();
			for (int i = 0; i < documents.length; ++i) {
				final String document = documents[i];
				if (document.startsWith(SIGNATURE_PREFIX)) {
					signatureOK = document.substring(SIGNATURE_PREFIX.length())
							.equals(signature);
					if (!signatureOK) {
						changes.add(document);
					}
				} else {
					final ISourceModule module = m.remove(document);
					if (module == null) {
						changes.add(document);
					} else {
						updates.add(module);
					}
				}
			}
			if (!signatureOK) {
				changes.addAll(updates);
			}
			if (!m.isEmpty()) {
				changes.addAll(m.values());
			}
			return changes;
		} else {
			return new ArrayList<Object>(modules);
		}
	}

	static class ExternalModuleVisitor implements IModelElementVisitor {
		final Set<ISourceModule> modules = new HashSet<ISourceModule>();

		public boolean visit(IModelElement element) {
			if (element.getElementType() == IModelElement.SOURCE_MODULE) {
				if (element instanceof IExternalSourceModule
						|| element instanceof BuiltinSourceModule
						|| ((ISourceModule) element).isBinary()) {
					modules.add((ISourceModule) element);
				}
				return false;
			}
			return true;
		}
	}

	private Set<ISourceModule> getExternalSourceModules() throws ModelException {
		final ExternalModuleVisitor visitor = new ExternalModuleVisitor();
		fragment.accept(visitor);
		return visitor.modules;
	}

	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((fragment == null) ? 0 : fragment.hashCode());
		return result;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		ArchiveProjectFragmentRequest other = (ArchiveProjectFragmentRequest) obj;
		if (fragment == null) {
			if (other.fragment != null)
				return false;
		} else if (!fragment.equals(other.fragment))
			return false;
		return true;
	}
}
