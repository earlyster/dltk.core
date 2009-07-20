/*******************************************************************************
 * Copyright (c) 2009 xored software, Inc.  
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html  
 *
 * Contributors:
 *     xored software, Inc. - initial API and Implementation (Alex Panchenko)
 *******************************************************************************/
package org.eclipse.dltk.internal.core;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.IProjectFragment;
import org.eclipse.dltk.core.IScriptFolder;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.ModelException;

public class ProjectRefreshOperation extends ModelOperation {

	public ProjectRefreshOperation(IScriptProject[] projects) {
		super(projects, new IModelElement[] { DLTKCore.create(ResourcesPlugin
				.getWorkspace().getRoot()) }, false);
	}

	private ModelElementDelta delta = null;

	@Override
	protected void executeOperation() throws ModelException {
		final IModelElement[] projects = elementsToProcess;
		beginTask(Messages.ProjectRefreshOperation_0,
				(projects != null ? projects.length * 3 : 0) + 1);
		if (projects != null) {
			for (IModelElement project : projects) {
				if (project.getElementType() == IModelElement.SCRIPT_PROJECT) {
					refreshProject((IScriptProject) project);
				}
			}
		}
		// report delta
		if (delta != null) {
			addDelta(delta);
			delta = null;
		}
		worked(1);
		done();
	}

	private void refreshProject(IScriptProject project) throws ModelException {
		Set<ISourceModule> modules = new HashSet<ISourceModule>();
		for (IProjectFragment fragment : project.getProjectFragments()) {
			if (!fragment.isExternal()
					&& fragment.getKind() == IProjectFragment.K_SOURCE) {
				for (IModelElement element : fragment.getChildren()) {
					if (element.getElementType() == IModelElement.SCRIPT_FOLDER) {
						for (ISourceModule module : ((IScriptFolder) element)
								.getSourceModules()) {
							modules.add(module);
						}
					}
				}
			}
		}
		worked(1);
		project.close();
		for (IProjectFragment fragment : project.getProjectFragments()) {
			if (!fragment.isExternal()
					&& fragment.getKind() == IProjectFragment.K_SOURCE) {
				for (IModelElement element : fragment.getChildren()) {
					if (element.getElementType() == IModelElement.SCRIPT_FOLDER) {
						for (ISourceModule module : ((IScriptFolder) element)
								.getSourceModules()) {
							if (!modules.remove(module)) {
								if (delta == null) {
									delta = newModelElementDelta();
								}
								delta.added(module);
							}
						}
					}
				}
			}
		}
		worked(1);
		for (ISourceModule module : modules) {
			if (delta == null) {
				delta = newModelElementDelta();
			}
			delta.removed(module);
			final IResource resource = module.getResource();
			if (resource != null) {
				delta.find(project).addResourceDelta(resource);
			}
		}
		worked(1);
	}

}
