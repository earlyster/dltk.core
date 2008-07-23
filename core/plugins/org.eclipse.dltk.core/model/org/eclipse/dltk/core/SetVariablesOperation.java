/*******************************************************************************
 * Copyright (c) 2000, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.dltk.core;

import java.util.HashMap;
import java.util.Iterator;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.dltk.internal.core.ChangeBuildpathOperation;
import org.eclipse.dltk.internal.core.ModelManager;
import org.eclipse.dltk.internal.core.ScriptProject;

public class SetVariablesOperation extends ChangeBuildpathOperation {

	String[] variableNames;
	IPath[] variablePaths;
	boolean updatePreferences;

	/*
	 * Creates a new SetVariablesOperation for the given variable values (null
	 * path meaning removal), allowing to change multiple variable values at
	 * once.
	 */
	public SetVariablesOperation(String[] variableNames, IPath[] variablePaths,
			boolean updatePreferences) {
		super(
				new IModelElement[] { ModelManager.getModelManager().getModel() },
				!ResourcesPlugin.getWorkspace().isTreeLocked());
		this.variableNames = variableNames;
		this.variablePaths = variablePaths;
		this.updatePreferences = updatePreferences;
	}

	protected void executeOperation() throws ModelException {
		checkCanceled();
		try {
			beginTask("", 1); //$NON-NLS-1$

			ModelManager manager = ModelManager.getModelManager();
			if (manager.variablePutIfInitializingWithSameValue(
					this.variableNames, this.variablePaths))
				return;

			int varLength = this.variableNames.length;

			// gather Buildpath information for updating
			final HashMap affectedProjectBuildpaths = new HashMap(5);
			IScriptModel model = getModel();

			// filter out unmodified variables
			int discardCount = 0;
			for (int i = 0; i < varLength; i++) {
				String variableName = this.variableNames[i];
				IPath oldPath = manager.variableGet(variableName); // if
				// reentering
				// will
				// provide
				// previous
				// session
				// value
				if (oldPath == ModelManager.VARIABLE_INITIALIZATION_IN_PROGRESS) {
					oldPath = null; // 33695 - cannot filter out restored
					// variable, must update affected project to
					// reset cached CP
				}
				if (oldPath != null && oldPath.equals(this.variablePaths[i])) {
					this.variableNames[i] = null;
					discardCount++;
				}
			}
			if (discardCount > 0) {
				if (discardCount == varLength)
					return;
				int changedLength = varLength - discardCount;
				String[] changedVariableNames = new String[changedLength];
				IPath[] changedVariablePaths = new IPath[changedLength];
				for (int i = 0, index = 0; i < varLength; i++) {
					if (this.variableNames[i] != null) {
						changedVariableNames[index] = this.variableNames[i];
						changedVariablePaths[index] = this.variablePaths[i];
						index++;
					}
				}
				this.variableNames = changedVariableNames;
				this.variablePaths = changedVariablePaths;
				varLength = changedLength;
			}

			if (isCanceled())
				return;

			IScriptProject[] projects = model.getScriptProjects();
			nextProject: for (int i = 0, projectLength = projects.length; i < projectLength; i++) {
				ScriptProject project = (ScriptProject) projects[i];

				// check to see if any of the modified variables is present on
				// the Buildpath
				IBuildpathEntry[] Buildpath = project.getRawBuildpath();
				for (int j = 0, cpLength = Buildpath.length; j < cpLength; j++) {

					IBuildpathEntry entry = Buildpath[j];
					for (int k = 0; k < varLength; k++) {

						String variableName = this.variableNames[k];
						if (entry.getEntryKind() == IBuildpathEntry.BPE_VARIABLE) {

							if (variableName.equals(entry.getPath().segment(0))) {
								affectedProjectBuildpaths.put(project, project
										.getResolvedBuildpath());
								continue nextProject;
							}
							IPath sourcePath, sourceRootPath;
							if (((sourcePath = entry.getPath()) != null && variableName
									.equals(sourcePath.segment(0)))
									|| ((sourceRootPath = entry.getPath()) != null && variableName
											.equals(sourceRootPath.segment(0)))) {

								affectedProjectBuildpaths.put(project, project
										.getResolvedBuildpath());
								continue nextProject;
							}
						}
					}
				}
			}

			// update variables
			for (int i = 0; i < varLength; i++) {
				manager.variablePut(this.variableNames[i],
						this.variablePaths[i]);
				if (this.updatePreferences)
					manager.variablePreferencesPut(this.variableNames[i],
							this.variablePaths[i]);
			}

			// update affected project Buildpaths
			if (!affectedProjectBuildpaths.isEmpty()) {
				try {
					// propagate Buildpath change
					Iterator projectsToUpdate = affectedProjectBuildpaths
							.keySet().iterator();
					while (projectsToUpdate.hasNext()) {

						if (this.progressMonitor != null
								&& this.progressMonitor.isCanceled())
							return;

						ScriptProject affectedProject = (ScriptProject) projectsToUpdate
								.next();

						if (this.canChangeResources) {
							// touch project to force a build if needed
							affectedProject.getProject().touch(
									this.progressMonitor);
						}
					}
				} catch (CoreException e) {
					if (ModelManager.BP_RESOLVE_VERBOSE /*
														 * ||ModelManager.
														 * CP_RESOLVE_VERBOSE_FAILURE
														 */) {
						e.printStackTrace();
					}
					if (e instanceof ModelException) {
						throw (ModelException) e;
					} else {
						throw new ModelException(e);
					}
				}
			}
		} finally {
			done();
		}
	}
}
