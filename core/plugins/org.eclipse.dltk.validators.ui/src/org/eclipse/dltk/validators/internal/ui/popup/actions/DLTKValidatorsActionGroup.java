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
import org.eclipse.dltk.ui.DLTKUIPlugin;
import org.eclipse.dltk.validators.core.IValidator;
import org.eclipse.dltk.validators.core.IValidatorType;
import org.eclipse.dltk.validators.core.ValidatorRuntime;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.actions.ActionGroup;

public class DLTKValidatorsActionGroup extends ActionGroup {

	public void fillContextMenu(IMenuManager menu) {
		final Object input = getContext().getInput();
		if (!(input instanceof IEditorInput)) {
			return;
		}
		final IModelElement element = DLTKUIPlugin
				.getEditorInputModelElement((IEditorInput) input);
		if (element == null) {
			return;
		}
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
		final StructuredSelection selection = new StructuredSelection(element);
		int validatorCount = 0;
		final IMenuManager subMenu = new MenuManager(
				Messages.DLTKValidatorsEditorContextMenu_text);
		if (DEBUG) {
			System.out.println("validators BEGIN"); //$NON-NLS-1$
		}
		for (int i = 0; i < validatorTypes.length; ++i) {
			final IValidatorType type = validatorTypes[i];
			if (DEBUG) {
				System.out.println("validatorType " + type.getName()); //$NON-NLS-1$
			}
			final IValidator[] validators = type.getValidators();
			if (validators != null && validators.length != 0) {
				for (int j = 0; j < validators.length; ++j) {
					final IValidator validator = validators[j];
					if (DEBUG) {
						System.out.println("validator " + validator.getName()); //$NON-NLS-1$
					}
					++validatorCount;
					final ValidateAction action = new ValidateAction(validator,
							selection);
					action.setEnabled(validator.isValidatorValid(environment));
					subMenu.add(action);
					if (false) {
						subMenu
								.add(new RemoveMarkersAction(validator, element));
					}
				}
			}
		}
		if (DEBUG) {
			System.out.println("validators END"); //$NON-NLS-1$
		}
		if (validatorCount != 0) {
			subMenu.add(new Separator());
			subMenu.add(new RemoveAllMarkersAction(selection));
			subMenu.add(new ValidateAllAction(selection));
			menu.appendToGroup(IWorkbenchActionConstants.MB_ADDITIONS, subMenu);
		}
	}

	private static final boolean DEBUG = false;

}
