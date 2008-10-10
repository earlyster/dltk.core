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
package org.eclipse.dltk.internal.core.mixin;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.dltk.core.DLTKLanguageManager;
import org.eclipse.dltk.core.IDLTKLanguageToolkit;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.IModelElementVisitor;
import org.eclipse.dltk.core.IProjectFragment;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.search.indexing.IndexManager;
import org.eclipse.dltk.internal.core.BuiltinProjectFragment;
import org.eclipse.dltk.internal.core.ExternalProjectFragment;
import org.eclipse.dltk.internal.core.ModelManager;

class MixinProjectRequest extends MixinIndexRequest {

	private final IScriptProject project;
	private final boolean indexExternal;

	public MixinProjectRequest(IScriptProject project, boolean indexExternal) {
		this.project = project;
		this.indexExternal = indexExternal;
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
		final IProjectFragment[] fragments = project.getProjectFragments();
		final IndexManager manager = ModelManager.getModelManager()
				.getIndexManager();
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
				if (indexExternal) {
					manager
							.request(new MixinBuiltinProjectFragmentRequest(
									fragment, toolkit,
									((BuiltinProjectFragment) fragment)
											.lastModified()));
				}
			} else if (fragment instanceof ExternalProjectFragment) {
				if (indexExternal) {
					manager.request(new MixinExternalProjectFragmentRequest(
							fragment, toolkit));
				}
			} else {
				fragment.accept(moduleCollector);
			}
		}
		manager.request(new MixinSourceModulesRequest(project, toolkit,
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
		if (getClass() != obj.getClass())
			return false;
		MixinProjectRequest other = (MixinProjectRequest) obj;
		if (project == null) {
			if (other.project != null)
				return false;
		} else if (!project.equals(other.project))
			return false;
		return true;
	}
}
