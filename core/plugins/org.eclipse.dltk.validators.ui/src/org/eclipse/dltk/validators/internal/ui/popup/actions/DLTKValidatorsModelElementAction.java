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

import org.eclipse.dltk.core.DLTKLanguageManager;
import org.eclipse.dltk.core.IDLTKLanguageToolkit;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.environment.EnvironmentManager;
import org.eclipse.dltk.core.environment.IEnvironment;
import org.eclipse.dltk.ui.actions.AbstractMenuCreatorObjectActionDelegate;
import org.eclipse.dltk.validators.core.IValidator;
import org.eclipse.dltk.validators.core.IValidatorType;
import org.eclipse.dltk.validators.core.ValidatorRuntime;
import org.eclipse.jface.viewers.IStructuredSelection;

public class DLTKValidatorsModelElementAction extends
		AbstractMenuCreatorObjectActionDelegate {

	protected void fillMenu(IMenuBuilder menu, IStructuredSelection selection) {
		if (selection == null || selection.isEmpty()) {
			return;
		}
		final Object input = selection.getFirstElement();
		if (!(input instanceof IModelElement)) {
			return;
		}
		final IModelElement element = (IModelElement) input;
		final IDLTKLanguageToolkit toolkit = DLTKLanguageManager
				.getLanguageToolkit(element);
		if (toolkit == null) {
			return;
		}
		final IEnvironment environment = EnvironmentManager
				.getEnvironment(element);
		if (environment == null) {
			return;
		}
		final IValidatorType[] validatorTypes = ValidatorRuntime
				.getValidatorTypes(toolkit.getNatureId());
		if (validatorTypes == null || validatorTypes.length == 0) {
			return;
		}
		int validatorCount = 0;
		for (int i = 0; i < validatorTypes.length; ++i) {
			final IValidatorType type = validatorTypes[i];
			final IValidator[] validators = type.getValidators();
			if (validators != null && validators.length != 0) {
				for (int j = 0; j < validators.length; ++j) {
					final IValidator validator = validators[j];
					menu.addAction(new ValidateAction(validator, selection));
					++validatorCount;
				}
			}
		}
		if (validatorCount != 0) {
			menu.addSeparator();
		}
		menu.addAction(new RemoveAllMarkersAction(selection));
		if (validatorCount != 0) {
			menu.addAction(new ValidateAllAction(selection));
		}
	}

}
