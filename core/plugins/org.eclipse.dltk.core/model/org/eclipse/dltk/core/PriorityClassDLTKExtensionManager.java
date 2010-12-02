/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.eclipse.dltk.core;

import org.eclipse.core.resources.IProject;

public class PriorityClassDLTKExtensionManager extends
		SimplePriorityClassDLTKExtensionManager {

	public PriorityClassDLTKExtensionManager(String extensionPoint) {
		super(extensionPoint, "nature"); //$NON-NLS-1$
	}

	public PriorityClassDLTKExtensionManager(String extensionPoint, String id) {
		super(extensionPoint, id);
	}

	public Object getObject(IModelElement element) {
		if (element == null
				|| element.getElementType() == IModelElement.SCRIPT_MODEL) {
			return null;
		}
		IScriptProject scriptProject = element.getScriptProject();
		IDLTKLanguageToolkit tk = scriptProject.getLanguageToolkit();
		if (tk != null) {
			return getObject(tk.getNatureId());
		}
		return getObject(scriptProject.getProject());
	}

	public Object getObject(IProject project) {
		String natureId = findScriptNature(project);
		if (natureId != null) {
			Object toolkit = getObject(natureId);
			if (toolkit != null) {
				return toolkit;
			}
		}

		return null;
	}

	public Object getObjectLower(String natureID) {
		ElementInfo ext = this.getElementInfo(natureID);
		if (ext.oldInfo == null) {
			return null;
		}
		return getInitObject(ext.oldInfo);
	}
}
