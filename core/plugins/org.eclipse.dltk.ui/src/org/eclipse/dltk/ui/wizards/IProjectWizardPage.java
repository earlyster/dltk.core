/*******************************************************************************
 * Copyright (c) 2009 xored software, Inc.  
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html  
 *
 * Contributors:
 *     xored software, Inc. - initial API and Implementation (Alex Panchenko)
 *******************************************************************************/
package org.eclipse.dltk.ui.wizards;

import org.eclipse.jface.wizard.IWizardPage;

/**
 * @since 2.0
 */
public interface IProjectWizardPage extends IWizardPage {

	/**
	 * This method is called initially to configure project create steps.
	 * 
	 * @param creator
	 */
	void configureSteps(ProjectCreator creator);

	/**
	 * This methods is called for all previous pages to update project create
	 * steps.
	 * 
	 * @param creator
	 */
	void updateSteps();

	/**
	 * This method is called when project being created was deleted as a result
	 * of project wizard cancellation or returning to the 1st page.
	 * 
	 * @param creator
	 */
	void resetPage();

}
