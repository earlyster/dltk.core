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

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.dltk.core.environment.IEnvironment;
import org.eclipse.swt.widgets.Shell;

public interface IProjectTemplate {

	/**
	 * @since 2.0
	 */
	void setCurrentEnvironment(IEnvironment environment);

	IProjectTemplateOperation configure(IProject project,
			IProjectTemplateOperation prevOperation, Shell parentShell);

	/**
	 * @param templateOperation
	 * @return
	 */
	IStatus validate(IProjectTemplateOperation templateOperation);

}
