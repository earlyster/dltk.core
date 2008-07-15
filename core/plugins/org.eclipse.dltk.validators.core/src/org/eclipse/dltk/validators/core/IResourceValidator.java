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
package org.eclipse.dltk.validators.core;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;

public interface IResourceValidator {

	/**
	 * Validate resources.
	 * 
	 * @param modules
	 * @param console
	 * @param monitor
	 * @return
	 */
	IStatus validate(IResource[] resources, IValidatorOutput output,
			IProgressMonitor monitor);

	/**
	 * Remove all reported markers.
	 * 
	 * @param module
	 */
	void clean(IResource[] resources);

}
