/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.eclipse.dltk.ui;

import java.util.List;

import org.eclipse.dltk.core.DLTKLanguageManager;
import org.eclipse.dltk.core.IDLTKLanguageToolkit;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.PriorityClassDLTKExtensionManager;
import org.eclipse.dltk.ui.viewsupport.ScriptUILabelProvider;

public class DLTKUILanguageManager extends PriorityClassDLTKExtensionManager {
	private static DLTKUILanguageManager instance = new DLTKUILanguageManager();
	private final static String LANGUAGE_EXTPOINT = DLTKUIPlugin.PLUGIN_ID
			+ ".language"; //$NON-NLS-1$

	private DLTKUILanguageManager() {
		super(LANGUAGE_EXTPOINT);
	}

	public static IDLTKUILanguageToolkit[] getLanguageToolkits() {
		@SuppressWarnings("unchecked")
		List<IDLTKUILanguageToolkit> toolkits = instance.getObjectList();
		return toolkits.toArray(new IDLTKUILanguageToolkit[toolkits.size()]);
	}

	/**
	 * @since 2.0
	 */
	public static IDLTKUILanguageToolkit getLanguageToolkit(
			IDLTKLanguageToolkit toolkit) {
		return (IDLTKUILanguageToolkit) instance.getObject(toolkit
				.getNatureId());
	}

	public static IDLTKUILanguageToolkit getLanguageToolkit(String natureId) {
		return (IDLTKUILanguageToolkit) instance.getObject(natureId);
	}

	public static IDLTKUILanguageToolkit getLanguageToolkit(
			IModelElement element) {
		IDLTKLanguageToolkit coreToolkit = DLTKLanguageManager
				.getLanguageToolkit(element);
		if (coreToolkit != null) {
			return (IDLTKUILanguageToolkit) instance.getObject(coreToolkit
					.getNatureId());
		}
		return null;
	}

	public static ScriptUILabelProvider createLabelProvider(
			IModelElement element) {
		IDLTKUILanguageToolkit languageToolkit = getLanguageToolkit(element);
		if (languageToolkit != null) {
			ScriptUILabelProvider provider = languageToolkit
					.createScriptUILabelProvider();
			if (provider != null) {
				return provider;
			}
		}
		return new ScriptUILabelProvider();
	}

	public static ScriptUILabelProvider createLabelProvider(String nature) {
		IDLTKUILanguageToolkit languageToolkit = getLanguageToolkit(nature);
		if (languageToolkit != null) {
			ScriptUILabelProvider provider = languageToolkit
					.createScriptUILabelProvider();
			if (provider != null) {
				return provider;
			}
		}
		return new ScriptUILabelProvider();
	}

	public static IDLTKUILanguageToolkit getLanguageToolkitLower(String natureId) {
		return (IDLTKUILanguageToolkit) instance.getObjectLower(natureId);
	}
}
