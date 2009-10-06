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
package org.eclipse.dltk.ui.dialogs;

import java.util.List;

import org.eclipse.jface.wizard.IWizardPage;

public interface IProjectTemplate {

	/**
	 * Returns the list of pages to be added to the project wizard
	 * 
	 * @since 2.0
	 */
	List<IWizardPage> getPages();

}
