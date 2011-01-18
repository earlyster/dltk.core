/*******************************************************************************
 * Copyright (c) 2011 xored software, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     xored software, Inc. - initial API and Implementation (Alex Panchenko)
 *******************************************************************************/
package org.eclipse.dltk.debug.ui.display;

import org.eclipse.jface.resource.ImageDescriptor;

public interface IEvaluateConsoleFactory {

	/**
	 * Tests if console can be created at the moment of this call.
	 * 
	 * @return
	 */
	boolean isEnabled();

	/**
	 * Creates the console
	 * 
	 * @return
	 */
	IEvaluateConsole create();

	String getLabel();

	ImageDescriptor getImageDescriptor();

}
