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

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.dltk.core.DLTKLanguageManager;
import org.eclipse.dltk.core.IDLTKLanguageToolkit;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.IModelElementVisitor;
import org.eclipse.dltk.core.IProjectFragment;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.search.indexing.IProjectIndexer;
import org.eclipse.dltk.internal.core.BuiltinProjectFragment;
import org.eclipse.dltk.internal.core.ScriptProject;

public class ProjectRequest extends IndexRequest {

	private final IScriptProject project;

	// private final boolean indexExternal;

	public ProjectRequest(IProjectIndexer indexer, IScriptProject project,
			boolean indexExternal) {
		super(indexer);
		this.project = project;
		// this.indexExternal = indexExternal;
	}

	protected String getName() {
		return project.getElementName();
	}

	static class SourceModuleCollector implements IModelElementVisitor {
		final Set modules = new HashSet();

		public boolean visit(IModelElement element) {
			if (element.getElementType() == IModelElement.SOURCE_MODULE) {
				modules.add(element);
				return false;
			}
			return true;
		}
	}

	protected void run() throws CoreException {
		final IDLTKLanguageToolkit toolkit = DLTKLanguageManager
				.getLanguageToolkit(project);
		final IProjectFragment[] fragments = ((ScriptProject) project)
				.getAllProjectFragments();
		IProjectIndexer.Internal indexer = getIndexer();
		final SourceModuleCollector moduleCollector = new SourceModuleCollector();
		for (int i = 0; i < fragments.length; ++i) {
			if (isCancelled) {
				return;
			}
			final IProjectFragment fragment = fragments[i];
			if (DEBUG) {
				log(" fragment " + fragment.getPath()); //$NON-NLS-1$
			}
			if (fragment instanceof BuiltinProjectFragment) {
				// if (indexExternal) {
				indexer.request(new BuiltinProjectFragmentRequest(indexer,
						fragment, toolkit, ((BuiltinProjectFragment) fragment)
								.lastModified()));
				// }
			} else if (fragment.isExternal()) {
				// if (indexExternal) {
				indexer.request(new ExternalProjectFragmentRequest(indexer,
						fragment, toolkit));
				// }
			} else {
				fragment.accept(moduleCollector);
			}
		}
		indexer.request(new SourceModulesRequest(indexer, project, toolkit,
				moduleCollector.modules));
	}

	public boolean belongsTo(String jobFamily) {
		return jobFamily.equals(project.getProject().getName());
	}

	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((project == null) ? 0 : project.hashCode());
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
		ProjectRequest other = (ProjectRequest) obj;
		if (project == null) {
			if (other.project != null)
				return false;
		} else if (!project.equals(other.project))
			return false;
		return true;
	}
}
