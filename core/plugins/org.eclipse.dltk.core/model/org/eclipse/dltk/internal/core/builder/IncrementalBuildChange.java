/*******************************************************************************
 * Copyright (c) 2010 xored software, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     xored software, Inc. - initial API and Implementation (Alex Panchenko)
 *******************************************************************************/
package org.eclipse.dltk.internal.core.builder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.dltk.core.IProjectFragment;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.builder.IBuildChange;
import org.eclipse.dltk.core.builder.IProjectChange;
import org.eclipse.dltk.core.builder.IScriptBuilder;

public class IncrementalBuildChange extends IncrementalProjectChange implements
		IBuildChange {

	public IncrementalBuildChange(IResourceDelta delta,
			IProjectChange[] projectChanges, IProject project,
			IProgressMonitor monitor, List<IPath> oldExternalFolders) {
		super(delta, project, monitor);
		this.oldExternalFolders = oldExternalFolders;
		this.projectChanges = projectChanges;
	}

	private final IProjectChange[] projectChanges;
	private final List<IPath> oldExternalFolders;

	private int buildType = IScriptBuilder.INCREMENTAL_BUILD;

	public int getBuildType() {
		return buildType;
	}

	public void setBuildType(int buildType) {
		if (buildType != IScriptBuilder.FULL_BUILD
				&& buildType != IScriptBuilder.INCREMENTAL_BUILD) {
			throw new IllegalArgumentException();
		}
		this.buildType = buildType;
	}

	public boolean isDependencyBuild() {
		return false;
	}

	public IProjectChange[] getRequiredProjectChanges() {
		return projectChanges;
	}

	public boolean addChangedResource(IFile file) throws CoreException {
		return super.addChangedResource(file);
	}

	public boolean addChangedResources(Collection<IFile> files)
			throws CoreException {
		boolean result = false;
		for (IFile file : files) {
			if (addChangedResource(file)) {
				result = true;
			}
		}
		return result;
	}

	private List<IPath> externalPaths = null;
	private Collection<IProjectFragment> externalFragments = null;

	private void loadExternalPaths() throws CoreException {
		if (externalPaths == null) {
			externalPaths = new ArrayList<IPath>();
			externalFragments = new ArrayList<IProjectFragment>();
			final IProjectFragment[] allFragments = getScriptProject()
					.getAllProjectFragments();
			for (int i = 0; i < allFragments.length; i++) {
				final IProjectFragment fragment = allFragments[i];
				if (fragment.isExternal()) {
					final IPath path = fragment.getPath();
					externalPaths.add(path);
					externalFragments.add(fragment);
				}
			}
		}
	}

	public List<IPath> getExternalPaths(int options) throws CoreException {
		validateFlags(options, BEFORE);
		if (options == BEFORE) {
			return oldExternalFolders;
		} else {
			loadExternalPaths();
			return Collections.unmodifiableList(externalPaths);
		}
	}

	private List<ISourceModule> externalModules = null;

	public List<ISourceModule> getExternalModules(int options)
			throws CoreException {
		validateFlags(options, DEFAULT);
		if (externalModules == null) {
			loadExternalPaths();
			final ExternalModuleCollector moduleCollector = new ExternalModuleCollector(
					monitor);
			for (IProjectFragment fragment : externalFragments) {
				if (!oldExternalFolders.contains(fragment.getPath())) {
					fragment.accept(moduleCollector);
				}
			}
			externalModules = unmodifiableList(moduleCollector.elements);
		}
		return externalModules;
	}

}
