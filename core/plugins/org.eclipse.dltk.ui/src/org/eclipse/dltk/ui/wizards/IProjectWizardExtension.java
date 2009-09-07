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

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.dltk.core.environment.IEnvironment;
import org.eclipse.dltk.ui.dialogs.IProjectTemplate;

/**
 * @since 2.0
 */
public interface IProjectWizardExtension {
	interface IValidationRequestor {
		void validate();
	}

	void createControls(IProjectWizardExtensionContext context);

	/**
	 * @param project
	 * @param monitor
	 * @throws CoreException
	 */
	void postConfigure(IProject project, IProgressMonitor monitor)
			throws CoreException;

	IStatus validate(IProject handle, IEnvironment environment,
			IProjectTemplate template);

	void setValidationRequestor(IValidationRequestor requestor);

}
