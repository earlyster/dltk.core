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

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.validators.core.IResourceValidator;
import org.eclipse.dltk.validators.core.ISourceModuleValidator;
import org.eclipse.dltk.validators.core.IValidator;
import org.eclipse.dltk.validators.core.IValidatorOutput;
import org.eclipse.dltk.validators.internal.ui.ValidatorsUI;
import org.eclipse.dltk.validators.ui.AbstractConsoleValidateJob;
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
		final AbstractConsoleValidateJob delegate = new AbstractConsoleValidateJob(
				validator.getName()) {

			protected void invokeValidationFor(IValidatorOutput output,
					IScriptProject project, ISourceModule[] modules,
					IResource[] resources, IProgressMonitor monitor) {
				if (validator.isValidatorValid(project)) {
					// TODO create submonitors
					final ISourceModuleValidator sourceModuleValidator = (ISourceModuleValidator) validator
							.getValidator(project, ISourceModuleValidator.class);
					if (sourceModuleValidator != null) {
						sourceModuleValidator
								.validate(modules, output, monitor);
					}
					final IResourceValidator resourceValidator = (IResourceValidator) validator
							.getValidator(project, IResourceValidator.class);
					if (resourceValidator != null) {
						resourceValidator.validate(resources, output, monitor);
					}
				}
			}
		};
		delegate.run(selection.toArray());
	}
}
