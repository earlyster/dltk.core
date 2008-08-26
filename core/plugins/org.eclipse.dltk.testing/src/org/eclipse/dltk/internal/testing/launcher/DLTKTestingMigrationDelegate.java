/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.dltk.internal.testing.launcher;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationMigrationDelegate;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.dltk.compiler.util.Util;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.IScriptModel;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.launching.ScriptLaunchConfigurationConstants;
import org.eclipse.dltk.testing.DLTKTestingConstants;

public class DLTKTestingMigrationDelegate implements ILaunchConfigurationMigrationDelegate {

	protected static final String EMPTY_STRING= ""; //$NON-NLS-1$

	public DLTKTestingMigrationDelegate() {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.debug.core.ILaunchConfigurationMigrationDelegate#isCandidate()
	 */
	public boolean isCandidate(ILaunchConfiguration candidate) throws CoreException {
		IResource[] mapped= candidate.getMappedResources();
		IResource target= getResource(candidate);
		if (target == null) {
			return mapped == null;
		} else {
			if (mapped == null) {
				return true;
			} else {
				if (mapped.length != 1) {
					return true;
				} else {
					return !target.equals(mapped[0]);
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.debug.core.ILaunchConfigurationMigrationDelegate#migrate(org.eclipse.debug.core.ILaunchConfiguration)
	 */
	public void migrate(ILaunchConfiguration candidate) throws CoreException {
		ILaunchConfigurationWorkingCopy wc= candidate.getWorkingCopy();
		mapResources(wc);
		wc.doSave();
	}

	public static ILaunchConfiguration fixMappedResources(
			ILaunchConfiguration config) throws CoreException {
		if (config.getMappedResources() == null) {
			final String projectName = config.getAttribute(
					ScriptLaunchConfigurationConstants.ATTR_PROJECT_NAME,
					Util.EMPTY_STRING);
			if (Path.ROOT.isValidSegment(projectName)) {
				final IScriptProject project = getModel().getScriptProject(
						projectName);
				if (project != null && project.exists()) {
					final IResource resource = project.getResource();
					if (resource != null) {
						final ILaunchConfigurationWorkingCopy wc;
						if (config.isWorkingCopy()) {
							wc = (ILaunchConfigurationWorkingCopy) config;
						} else {
							wc = config.getWorkingCopy();
						}
						wc.setMappedResources(new IResource[] { resource });
						return wc.doSave();
					}
				}
			}
		}
		return config;
	}

	/**
	 * Maps a resource for the given launch configuration.
	 * 
	 * @param config working copy
	 * @throws CoreException if an exception occurs mapping resource
	 */
	public static void mapResources(ILaunchConfigurationWorkingCopy config) throws CoreException {
		IResource resource= getResource(config);
		if (resource == null) {
			config.setMappedResources(null);
		} else {
			config.setMappedResources(new IResource[] { resource });
		}
	}

	/**
	 * Returns a resource mapping for the given launch configuration, or <code>null</code>
	 * if none.
	 * 
	 * @param config working copy
	 * @returns resource or <code>null</code>
	 * @throws CoreException if an exception occurs mapping resource
	 */
	private static IResource getResource(ILaunchConfiguration config) throws CoreException {
		String projName= config.getAttribute(ScriptLaunchConfigurationConstants.ATTR_PROJECT_NAME, (String) null);
		String containerHandle= config.getAttribute(DLTKTestingConstants.ATTR_TEST_CONTAINER, (String) null);
//		String typeName = config.getAttribute(ScriptLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME, (String)null);
		IModelElement element= null;
		if (projName != null && Path.ROOT.isValidSegment(projName)) {
			IScriptProject javaProject= getModel().getScriptProject(projName);
			if (javaProject.exists()) {
//				if (typeName != null) {
//					element = javaProject.findType(typeName);
//				}
				if (element == null) {
					element= javaProject;
				}
			} else {
				IProject project= javaProject.getProject();
				if (project.exists() && !project.isOpen()) {
					return project;
				}
			}
		} else if (containerHandle != null) {
			element= DLTKCore.create(containerHandle);
		}
		IResource resource= null;
		if (element != null) {
			resource= element.getResource();
		}
		return resource;
	}

	/*
	 * Convenience method to get access to the java model.
	 */
	private static IScriptModel getModel() {
		return DLTKCore.create(ResourcesPlugin.getWorkspace().getRoot());
	}

}
