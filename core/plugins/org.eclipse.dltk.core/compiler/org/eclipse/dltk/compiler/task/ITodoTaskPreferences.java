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

public interface ITodoTaskPreferences {

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
	 * saves task tags
	 * 
	 * @param elements
	 *            list of {@link TodoTask}
	 */
	void setTaskTags(List elements);

	/**
	 * @return
	 */
	String[] getTagNames();

}
