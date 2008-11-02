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
package org.eclipse.dltk.compiler.task;

import java.util.List;

import org.eclipse.dltk.core.DLTKCore;

public interface ITodoTaskPreferences {

	public static final String CASE_SENSITIVE = DLTKCore.PLUGIN_ID
			+ "tasks.case_sensitive"; //$NON-NLS-1$
	public static final String TAGS = DLTKCore.PLUGIN_ID + "tasks.tags"; //$NON-NLS-1$
	public static final String ENABLED = DLTKCore.PLUGIN_ID + "tasks.enabled"; //$NON-NLS-1$

	/**
	 * Checks if the tags are enabled
	 * 
	 * @return
	 */
	boolean isEnabled();

	/**
	 * Checks if the tags are case sensitive
	 * 
	 * @return
	 */
	boolean isCaseSensitive();

	/**
	 * returns task tags
	 * 
	 * @return list of {@link TodoTask}
	 */
	List getTaskTags();

	/**
	 * @deprecated
	 */
	void setTaskTags(List elements);

	/**
	 * @return
	 */
	String[] getTagNames();

}
