/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.eclipse.dltk.ui.wizards;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.internal.ui.editor.EditorUtility;
import org.eclipse.dltk.ui.DLTKUIPlugin;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PartInitException;

public abstract class NewSourceModuleWizard extends NewElementWizard {

	private NewSourceModulePage page;

	private ISourceModule module;

	protected abstract NewSourceModulePage createNewSourceModulePage();

	@Override
	public void addPages() {
		super.addPages();

		page = createNewSourceModulePage();
		page.init(getSelection());
		addPage(page);
	}

	@Override
	public IModelElement getCreatedElement() {
		return module;
	}

	@Override
	protected void finishPage(IProgressMonitor monitor)
			throws InterruptedException, CoreException {
		module = page.createFile(monitor);
	}

	@Override
	public boolean performFinish() {
		final boolean result = super.performFinish();
		if (result && module != null) {
			openSourceModule(module);
		}
		return result;
	}

	protected void openSourceModule(final ISourceModule module) {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				try {
					EditorUtility.openInEditor(module);
				} catch (PartInitException e) {
					final String msg = NLS.bind(
							Messages.NewSourceModuleWizard_errorInOpenInEditor,
							module.getElementName());
					DLTKUIPlugin.logErrorMessage(msg, e);
				} catch (ModelException e) {
					final String msg = NLS.bind(
							Messages.NewSourceModuleWizard_errorInOpenInEditor,
							module.getElementName());
					DLTKUIPlugin.logErrorMessage(msg, e);
				}
			}
		});
	}
}
