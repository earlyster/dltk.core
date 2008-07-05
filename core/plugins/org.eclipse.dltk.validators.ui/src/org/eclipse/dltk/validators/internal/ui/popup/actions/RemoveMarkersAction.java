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
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.validators.core.IValidator;
import org.eclipse.dltk.validators.core.ValidatorRuntime;
import org.eclipse.dltk.validators.internal.ui.ValidatorsUI;
import org.eclipse.dltk.validators.ui.AbstractValidateSelectionWithConsole;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.osgi.util.NLS;

public class RemoveMarkersAction extends Action {

	private static final String CLEANUP_IMAGE = "icons/clear_co.gif"; //$NON-NLS-1$

	private final IValidator validator;
	private final IModelElement element;

	public RemoveMarkersAction(IValidator validator, IModelElement element) {
		this.validator = validator;
		this.element = element;
		setText(NLS.bind(
				Messages.DLTKValidatorsEditorContextMenu_validatorCleanup,
				validator.getName()));
		setImageDescriptor(ValidatorsUI.getDefault().getImageDescriptor(
				CLEANUP_IMAGE));
	}

	public void run() {
		final AbstractValidateSelectionWithConsole delegate = new AbstractValidateSelectionWithConsole() {

			protected boolean isConsoleRequired() {
				return false;
			}

			protected String getJobName() {
				final String message = Messages.DLTKValidatorsEditorContextMenu_validatorCleanup;
				return NLS.bind(message, validator.getName());
			}

			protected void invoceValidationFor(OutputStream out, List elements,
					List resources, IProgressMonitor monitor) {
				ValidatorRuntime.cleanValidator(validator, elements,
						resources, monitor);
			}
		};
		delegate.selectionChanged(this, new StructuredSelection(element));
		delegate.run(this);
	}
}
