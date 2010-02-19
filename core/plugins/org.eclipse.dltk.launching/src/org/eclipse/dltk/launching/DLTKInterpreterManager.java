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
package org.eclipse.dltk.launching;

import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.PriorityClassDLTKExtensionManager;
import org.eclipse.dltk.internal.launching.DLTKLaunchingPlugin;

/**
 * @since 2.0
 */
public class DLTKInterpreterManager {

	private final static String INTERPRETER_CONTAINER_EXTENSION_EXTPOINT = DLTKLaunchingPlugin.PLUGIN_ID
			+ ".interpreterContainerExtension";//$NON-NLS-1$

	private static PriorityClassDLTKExtensionManager interpreterContainerExtensionManager = new PriorityClassDLTKExtensionManager(
			INTERPRETER_CONTAINER_EXTENSION_EXTPOINT);

	public static IInterpreterContainerExtension getInterpreterContainerExtensions(
			IScriptProject project) {
		return (IInterpreterContainerExtension) interpreterContainerExtensionManager
				.getObject(project);
	}

	public static IInterpreterContainerExtension getInterpreterContainerExtensions(
			String natureId) {
		return (IInterpreterContainerExtension) interpreterContainerExtensionManager
				.getObject(natureId);
	}

}
