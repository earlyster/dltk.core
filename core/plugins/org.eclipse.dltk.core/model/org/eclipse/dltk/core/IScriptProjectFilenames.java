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
package org.eclipse.dltk.core;

import org.eclipse.core.resources.IProjectDescription;

/**
 * @since 2.0
 */
public interface IScriptProjectFilenames {

	public static final String PROJECT_FILENAME = IProjectDescription.DESCRIPTION_FILE_NAME;

	/**
	 * Name of file containing project buildpath
	 */
	public static final String BUILDPATH_FILENAME = ".buildpath"; //$NON-NLS-1$

	/**
	 * Name of the folder containing project specific options
	 */
	public static final String SETTINGS_FOLDER_NAME = ".settings"; //$NON-NLS-1$

}
