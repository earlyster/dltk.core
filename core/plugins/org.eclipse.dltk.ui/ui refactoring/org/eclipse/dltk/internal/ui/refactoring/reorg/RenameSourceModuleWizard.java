/*******************************************************************************
 * Copyright (c) 2000, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 *******************************************************************************/
package org.eclipse.dltk.internal.ui.refactoring.reorg;

import org.eclipse.dltk.compiler.util.Util;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.ScriptModelUtil;
import org.eclipse.dltk.internal.corext.refactoring.rename.RenameSourceModuleProcessor;
import org.eclipse.dltk.internal.corext.refactoring.tagging.INameUpdating;
import org.eclipse.dltk.internal.ui.refactoring.RefactoringMessages;
import org.eclipse.dltk.ui.DLTKPluginImages;
import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.participants.RenameRefactoring;

public class RenameSourceModuleWizard extends RenameRefactoringWizard {

	public RenameSourceModuleWizard(Refactoring refactoring) {
		super(refactoring, RefactoringMessages.RenameCuWizard_defaultPageTitle,
				RefactoringMessages.RenameCuWizard_inputPage_description,
				DLTKPluginImages.DESC_WIZBAN_REFACTOR_CU, Util.EMPTY_STRING);
		/* IScriptHelpContextIds.RENAME_CU_WIZARD_PAGE */
	}

	@Override
	protected RefactoringStatus validateNewName(String newName) {
		final String fullName;
		if (getSourceModuleProcessor().isFileExtensionRequired()) {
			fullName = ScriptModelUtil.getRenamedCUName(getSourceModule(),
					newName);
		} else {
			fullName = newName;
		}
		return super.validateNewName(fullName);
	}

	private ISourceModule getSourceModule() {
		return (ISourceModule) getSourceModuleProcessor().getElements()[0];
	}

	@Override
	protected RenameInputWizardPage createInputPage(String message,
			String initialSetting) {
		return new RenameInputWizardPage(message, fPageContextHelpId, true,
				initialSetting) {

			@Override
			protected RefactoringStatus validateTextField(String text) {
				return validateNewName(text);
			}

			@Override
			protected String getNewName(INameUpdating nameUpdating) {
				String result = nameUpdating.getNewElementName();
				if (getSourceModuleProcessor().isFileExtensionRequired()) {
					// If renaming a CU we have to remove the file extension
					return RenameSourceModuleProcessor
							.removeFileNameExtension(result);
				} else {
					return result;
				}
			}
		};
	}

	private RenameSourceModuleProcessor getSourceModuleProcessor() {
		return ((RenameSourceModuleProcessor) ((RenameRefactoring) getRefactoring())
				.getProcessor());
	}
}
