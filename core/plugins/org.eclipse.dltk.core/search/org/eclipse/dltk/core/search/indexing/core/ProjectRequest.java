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
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.environment.EnvironmentManager;
import org.eclipse.dltk.core.environment.IEnvironment;
import org.eclipse.dltk.core.search.indexing.IProjectIndexer;
import org.eclipse.dltk.internal.core.BuiltinProjectFragment;
import org.eclipse.dltk.internal.core.ScriptProject;

/**
 * @since 2.0
 */
public class ProjectRequest extends IndexRequest {

	private final IScriptProject project;

	public ProjectRequest(IProjectIndexer indexer, IScriptProject project) {
		super(indexer);
		this.project = project;
	}

	@Override
	protected String getName() {
		return project.getElementName();
	}

	static class SourceModuleCollector implements IModelElementVisitor {
		final Set<ISourceModule> modules = new HashSet<ISourceModule>();

		public boolean visit(IModelElement element) {
			if (element.getElementType() == IModelElement.SOURCE_MODULE) {
				modules.add((ISourceModule) element);
				return false;
			}
			return true;
		}
	}

	@Override
	protected void run() throws CoreException {
		IEnvironment environment = EnvironmentManager.getEnvironment(project);
		if (environment == null || !environment.connect()) {
			return;
		}
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
			if (fragment.isBuiltin()) {
				indexer.requestIfNotWaiting(new BuiltinProjectFragmentRequest(
						indexer, fragment, toolkit,
						((BuiltinProjectFragment) fragment).lastModified()));
			} else if (fragment.isExternal()) {
				indexer.requestIfNotWaiting(new ExternalProjectFragmentRequest(
						indexer, fragment, toolkit));
			} else {
				fragment.accept(moduleCollector);
			}
		}
		indexer.request(new SourceModulesRequest(indexer, project, toolkit,
				moduleCollector.modules));
	}

	@Override
	public boolean belongsTo(String jobFamily) {
		return jobFamily.equals(project.getProject().getName());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((project == null) ? 0 : project.hashCode());
		return result;
	}

	@Override
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
