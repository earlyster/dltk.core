/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.eclipse.dltk.core.manipulation;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.dltk.core.DLTKLanguageManager;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.internal.core.manipulation.ScriptManipulationPlugin;

public class RefactoringEngineManager {
	private static RefactoringEngineManager instance;

	protected IRefactoringEngine doFindRefactoringEngine(String natureId)
			throws CoreException {
		IExtensionRegistry reg = Platform.getExtensionRegistry();
		IExtensionPoint ep = reg
				.getExtensionPoint(ManipulationConstants.REFACTORING_ENGINE_EP);

		IExtension[] extensions = ep.getExtensions();

		for (int i = 0; i < extensions.length; i++) {
			IExtension ext = extensions[i];
			IConfigurationElement[] ce = ext.getConfigurationElements();
			for (int j = 0; j < ce.length; j++) {
				if (natureId
						.equals(ce[j]
								.getAttribute(ManipulationConstants.REFACTORING_ENGINE_NATURE_ID))) {
					Object obj = ce[j]
							.createExecutableExtension(ManipulationConstants.REFACTORING_ENGINE_CLASS);
					if (obj instanceof IRefactoringEngine) {
						return (IRefactoringEngine) obj;
					} else {
						return null;
					}
				}
			}
		}

		return null;
	}

	public static synchronized RefactoringEngineManager getInstance() {
		if (instance == null) {
			instance = new RefactoringEngineManager();
		}

		return instance;
	}

	public IRefactoringEngine findRefactoringEngine(IModelElement element) {
		try {
			return doFindRefactoringEngine(DLTKLanguageManager.getLanguageToolkit(element).getNatureId());
		} catch (CoreException e) {
			ScriptManipulationPlugin.log(e);
		}
		return null;
	}
}
