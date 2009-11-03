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

import org.eclipse.dltk.core.environment.IEnvironment;

/**
 * @since 2.0
 */
public interface IProjectWizardInitializer {

	public interface IProjectWizardState {

		String MODE_WORKSPACE = "org.eclipse.dltk.ui.projectWizard.workspace"; //$NON-NLS-1$

		String MODE_EXTERNAL = "org.eclipse.dltk.ui.projectWizard.external"; //$NON-NLS-1$

		String getScriptNature();

		String getProjectName();

		void setProjectName(String name);

		String getToolTipText(String mode);

		void setToolTipText(String mode, String tooltip);

		String getMode();

		void setMode(String mode);

		IEnvironment getEnvironment();

		void setEnvironment(IEnvironment environment);

		String getExternalLocation();

		void setExternalLocation(String path);

	}

	void initialize(IProjectWizardState state);

}
