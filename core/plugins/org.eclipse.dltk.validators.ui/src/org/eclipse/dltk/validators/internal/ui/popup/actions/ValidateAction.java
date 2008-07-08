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
import org.eclipse.dltk.validators.core.IValidator;
import org.eclipse.dltk.validators.core.ValidatorRuntime;
import org.eclipse.dltk.validators.internal.ui.ValidatorsUI;
import org.eclipse.dltk.validators.ui.AbstractValidateSelectionWithConsole;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osgi.util.NLS;

public class ValidateAction extends Action {

	static final String VALIDATE_IMAGE = "icons/goto_input.gif"; //$NON-NLS-1$

	private final IValidator validator;
	private final IStructuredSelection selection;

	/**
	 * @param validator
	 */
	public ValidateAction(IValidator validator, IStructuredSelection selection) {
		this.validator = validator;
		this.selection = selection;
		final String text = NLS.bind(
				Messages.DLTKValidatorsEditorContextMenu_validateWith,
				validator.getName());
		setText(text);
		setImageDescriptor(ValidatorsUI.getDefault().getImageDescriptor(
				VALIDATE_IMAGE));
	}

	public void run() {
		final AbstractValidateSelectionWithConsole delegate = new AbstractValidateSelectionWithConsole() {

			protected String getJobName() {
				return validator.getName();
			}

			protected void invoceValidationFor(OutputStream out, List elements,
					List resources, IProgressMonitor monitor) {
				ValidatorRuntime.executeValidator(validator, out, elements,
						resources, monitor);
			}
		};
		delegate.selectionChanged(this, selection);
		delegate.run(this);
	}
}
