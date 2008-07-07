/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.eclipse.dltk.ui.actions;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.search.IDLTKSearchConstants;
import org.eclipse.dltk.internal.ui.DLTKUIMessages;
import org.eclipse.dltk.internal.ui.dialogs.OpenMethodSelectionDialog2;
import org.eclipse.dltk.ui.DLTKPluginImages;
import org.eclipse.dltk.ui.DLTKUIPlugin;
import org.eclipse.dltk.ui.IDLTKUILanguageToolkit;
import org.eclipse.dltk.ui.util.ExceptionHandler;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;

public abstract class OpenMethodAction extends Action implements IWorkbenchWindowActionDelegate {

	public OpenMethodAction() {
		super();
		setText(DLTKUIMessages.OpenMethodAction_label);
		setDescription(DLTKUIMessages.OpenMethodAction_description);
		setToolTipText(DLTKUIMessages.OpenMethodAction_tooltip);
		setImageDescriptor(DLTKPluginImages.DESC_TOOL_OPENMETHOD);
		// TODO help context
		// PlatformUI.getWorkbench().getHelpSystem().setHelp(this,
		// IJavaHelpContextIds.OPEN_TYPE_ACTION);
	}

	protected abstract IDLTKUILanguageToolkit getUILanguageToolkit();

	public void run() {
		Shell parent = DLTKUIPlugin.getActiveWorkbenchShell();
		OpenMethodSelectionDialog2 dialog = new OpenMethodSelectionDialog2(parent, true, PlatformUI.getWorkbench().getProgressService(), null, IDLTKSearchConstants.METHOD, this.getUILanguageToolkit());
		dialog.setTitle(getOpenMethodDialogTitle());
		dialog.setMessage(getOpenMethodDialogMessage());

		int result = dialog.open();
		if (result != IDialogConstants.OK_ID)
			return;

		Object[] types = dialog.getResult();
		if (types != null && types.length > 0) {
			IModelElement type = null;
			for (int i = 0; i < types.length; i++) {
				type = (IModelElement) types[i];
				try {
					DLTKUIPlugin.openInEditor(type, true, true);
				} catch (CoreException x) {
					ExceptionHandler.handle(x, getOpenMethodErrorTitle(), getOpenMethodErrorMessage());
				}
			}
		}
	}

	protected String getOpenMethodErrorMessage() {
		return DLTKUIMessages.OpenMethodAction_errorMessage;
	}

	protected String getOpenMethodErrorTitle() {
		return DLTKUIMessages.OpenMethodAction_errorTitle;
	}

	protected String getOpenMethodDialogMessage() {
		return DLTKUIMessages.OpenMethodAction_dialogMessage;
	}

	protected String getOpenMethodDialogTitle() {
		return DLTKUIMessages.OpenMethodAction_dialogTitle;
	}

	// ---- IWorkbenchWindowActionDelegate
	// ------------------------------------------------

	public void run(IAction action) {
		run();
	}

	public void dispose() {
		// do nothing.
	}

	public void init(IWorkbenchWindow window) {
		// do nothing.
	}

	public void selectionChanged(IAction action, ISelection selection) {
		// do nothing. Action doesn't depend on selection.
	}
}
