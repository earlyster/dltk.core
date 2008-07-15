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
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.validators.core.IResourceValidator;
import org.eclipse.dltk.validators.core.ISourceModuleValidator;
import org.eclipse.dltk.validators.core.IValidator;
import org.eclipse.dltk.validators.core.IValidatorOutput;
import org.eclipse.dltk.validators.internal.ui.ValidatorsUI;
import org.eclipse.dltk.validators.ui.AbstractConsoleValidateJob;
import org.eclipse.jface.action.Action;
import org.eclipse.osgi.util.NLS;

public class RemoveMarkersAction extends Action {

	static final String CLEANUP_IMAGE = "icons/clear_co.gif"; //$NON-NLS-1$

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
		final String message = NLS.bind(
				Messages.DLTKValidatorsEditorContextMenu_validatorCleanup,
				validator.getName());
		final AbstractConsoleValidateJob delegate = new AbstractConsoleValidateJob(
				message) {

			protected boolean isConsoleRequired() {
				return false;
			}

			protected void invokeValidationFor(IValidatorOutput out,
					IScriptProject project, ISourceModule[] modules,
					IResource[] resources, IProgressMonitor monitor) {
				// TODO create submonitors
				final ISourceModuleValidator sourceModuleValidator = (ISourceModuleValidator) validator
						.getValidator(project, ISourceModuleValidator.class);
				if (sourceModuleValidator != null) {
					sourceModuleValidator.clean(modules);
				}
				final IResourceValidator resourceValidator = (IResourceValidator) validator
						.getValidator(project, IResourceValidator.class);
				if (resourceValidator != null) {
					resourceValidator.clean(resources);
				}
			}
		};
		delegate.run(new Object[] { element });
	}
}
