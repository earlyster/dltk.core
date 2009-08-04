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
package org.eclipse.dltk.launching.sourcelookup;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.IModelElementVisitor;
import org.eclipse.dltk.core.IProjectFragment;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.core.environment.EnvironmentManager;
import org.eclipse.dltk.core.environment.EnvironmentPathUtils;
import org.eclipse.dltk.core.environment.IEnvironment;
import org.eclipse.dltk.core.environment.IFileHandle;
import org.eclipse.dltk.internal.core.ExternalSourceModule;
import org.eclipse.dltk.internal.launching.DLTKLaunchingPlugin;
import org.eclipse.dltk.internal.launching.IPathEquality;
import org.eclipse.dltk.internal.launching.PathEqualityUtils;

public class ProjectSourceLookup {

	private final IProject project;

	public ProjectSourceLookup(IProject project) {
		this.project = project;
	}

	private static final class ExternalSourceModuleFinder implements
			IModelElementVisitor {

		private final IPath fileFullPath;

		private ExternalSourceModuleFinder(IPath fileFullPath) {
			this.fileFullPath = fileFullPath;
		}

		private final IPathEquality pathEquality = PathEqualityUtils
				.getInstance();

		private final ISourceModule[] result = new ISourceModule[1];

		public boolean visit(IModelElement element) {
			if (element.getElementType() == IModelElement.PROJECT_FRAGMENT) {
				IProjectFragment fragment = (IProjectFragment) element;
				if (!fragment.isExternal()) {
					return false;
				}
			}
			if (element.getElementType() == IModelElement.SOURCE_MODULE) {
				ISourceModule module = (ISourceModule) element;
				IPath modulePath = module.getPath();
				if (module instanceof ExternalSourceModule) {
					IEnvironment environment = EnvironmentManager
							.getEnvironment(element);
					ExternalSourceModule mdl = (ExternalSourceModule) module;
					modulePath = mdl.getFullPath();
					if (!EnvironmentPathUtils.isFull(modulePath))
						modulePath = EnvironmentPathUtils.getFullPath(
								environment, modulePath);
				}
				if (pathEquality.equals(fileFullPath, modulePath)) {
					result[0] = module;
				}
				return false;
			}
			return true;
		}

		public boolean isFound() {
			return result[0] != null;
		}

		public ISourceModule[] getResult() {
			return result;
		}
	}

	private static final class LocalSourceModuleFinder implements
			IModelElementVisitor {

		private final IPath fileFullPath;

		private LocalSourceModuleFinder(IPath fileFullPath) {
			this.fileFullPath = fileFullPath;
		}

		private final IPathEquality pathEquality = PathEqualityUtils
				.getInstance();

		private final IFile[] result = new IFile[1];

		public boolean visit(IModelElement element) {
			if (element.getElementType() == IModelElement.PROJECT_FRAGMENT) {
				IProjectFragment fragment = (IProjectFragment) element;
				if (fragment.isExternal()) {
					return false;
				}
			}
			if (element.getElementType() == IModelElement.SOURCE_MODULE) {
				ISourceModule module = (ISourceModule) element;
				IEnvironment environment = EnvironmentManager
						.getEnvironment(element.getScriptProject());
				final IResource resource = module.getResource();
				if (resource != null) {
					final IFileHandle file = environment.getFile(resource
							.getLocationURI());
					if (pathEquality.equals(fileFullPath, file.getPath())) {
						result[0] = (IFile) resource;
					}
				}
				return false;
			}
			return true;
		}

		public boolean isFound() {
			return result[0] != null;
		}

		public IFile[] getResult() {
			return result;
		}
	}

	private static final class WorkspaceLookupResult implements
			IProjectLookupResult {

		private final IFile[] files;

		public WorkspaceLookupResult(IFile[] files) {
			this.files = files;
		}

		public Object[] toArray() {
			return files;
		}

		public int size() {
			return files.length;
		}

	}

	private static final class SourceModuleLookupResult implements
			IProjectLookupResult {

		private final ISourceModule[] modules;

		public SourceModuleLookupResult(ISourceModule[] modules) {
			this.modules = modules;
		}

		public Object[] toArray() {
			return modules;
		}

		public int size() {
			return modules.length;
		}

	}

	private IEnvironment environment = null;

	private IEnvironment getEnvironment() {
		if (environment == null) {
			environment = EnvironmentManager.getEnvironment(project);
		}
		return environment;
	}

	private IScriptProject scriptProject = null;

	public IScriptProject getScriptProject() {
		if (scriptProject == null) {
			scriptProject = DLTKCore.create(project);
		}
		return scriptProject;
	}

	/**
	 * Finds project elements matching to the specified environment-local path.
	 * Returns the {@link IProjectLookupResult} or <code>null</code> if nothing
	 * found.
	 * 
	 * @param path
	 * @return
	 */
	public IProjectLookupResult find(IPath path) {
		final IFileHandle file = getEnvironment().getFile(path);
		if (file.exists()) {
			// check if file is available in workspace
			final IFile[] workspaceFiles = getWorkspaceRoot()
					.findFilesForLocationURI(file.toURI());
			if (workspaceFiles.length != 0 && workspaceFiles[0].exists()) {
				return new WorkspaceLookupResult(workspaceFiles);
			}
			try {
				// Try to open external source module.
				final ExternalSourceModuleFinder finder = new ExternalSourceModuleFinder(
						file.getFullPath());
				getScriptProject().accept(finder);
				if (finder.isFound()) {
					return new SourceModuleLookupResult(finder.getResult());
				}
				// Last attempt - iterate over workspace files
				// TODO make it configurable
				final LocalSourceModuleFinder finder2 = new LocalSourceModuleFinder(
						file.getPath());
				getScriptProject().accept(finder2);
				if (finder2.isFound()) {
					return new WorkspaceLookupResult(finder2.getResult());
				}
			} catch (ModelException e) {
				DLTKLaunchingPlugin.log(e);
			}
		}
		return null;
	}

	private static IWorkspaceRoot getWorkspaceRoot() {
		return ResourcesPlugin.getWorkspace().getRoot();
	}

}
