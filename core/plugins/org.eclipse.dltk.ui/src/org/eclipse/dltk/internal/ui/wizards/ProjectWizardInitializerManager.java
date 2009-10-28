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
package org.eclipse.dltk.internal.ui.wizards;

import org.eclipse.dltk.ui.DLTKUIPlugin;
import org.eclipse.dltk.ui.wizards.IProjectWizardInitializer;
import org.eclipse.dltk.utils.LazyExtensionManager;

public class ProjectWizardInitializerManager extends
		LazyExtensionManager<IProjectWizardInitializer> {

	private static final String ATTR_NATURE = "nature"; //$NON-NLS-1$

	private final String selectedNature;

	/**
	 * @param extensionPoint
	 */
	public ProjectWizardInitializerManager(String nature) {
		super(DLTKUIPlugin.PLUGIN_ID + ".projectWizardInitializer"); //$NON-NLS-1$
		this.selectedNature = nature;
	}

	@Override
	protected boolean isValidDescriptor(
			Descriptor<IProjectWizardInitializer> descriptor) {
		final String natureId = descriptor.getAttribute(ATTR_NATURE);
		return natureId == null || selectedNature != null
				&& selectedNature.equals(natureId);
	}

}
