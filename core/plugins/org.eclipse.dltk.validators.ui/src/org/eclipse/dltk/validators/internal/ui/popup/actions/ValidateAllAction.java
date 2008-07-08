/*******************************************************************************
 * Copyright (c) 2008 xored software, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     xored software, Inc. - initial API and Implementation (Alex Panchenko)
 *******************************************************************************/
package org.eclipse.dltk.validators.internal.ui.popup.actions;

import java.io.OutputStream;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.dltk.validators.core.ValidatorRuntime;
import org.eclipse.dltk.validators.internal.ui.ValidatorsUI;
import org.eclipse.dltk.validators.ui.AbstractValidateSelectionWithConsole;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.IStructuredSelection;

public class ValidateAllAction extends Action {

	private final IStructuredSelection selection;

	/**
	 * @param element
	 */
	public ValidateAllAction(IStructuredSelection selection) {
		this.selection = selection;
		setText(Messages.DLTKValidatorsEditorContextMenu_validateAll);
		setImageDescriptor(ValidatorsUI.getDefault().getImageDescriptor(
				ValidateAction.VALIDATE_IMAGE));
	}

	public void run() {
		final AbstractValidateSelectionWithConsole delegate = new AbstractValidateSelectionWithConsole() {

			protected boolean isConsoleRequired() {
				return false;
			}

			protected String getJobName() {
				return Messages.ValidateSelectionWithConsoleAction_validation;
			}

			protected void invoceValidationFor(OutputStream out, List elements,
					List resources, IProgressMonitor monitor) {
				ValidatorRuntime.executeAllValidators(out, elements, resources,
						monitor);
			}
		};
		delegate.selectionChanged(this, selection);
		delegate.run(this);
	}

}
