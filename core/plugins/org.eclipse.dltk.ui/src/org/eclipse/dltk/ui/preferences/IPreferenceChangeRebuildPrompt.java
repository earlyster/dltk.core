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
package org.eclipse.dltk.ui.preferences;

/**
 * Returns the prompt that should be used in the popup box that indicates a
 * build needs to occur.
 */
public interface IPreferenceChangeRebuildPrompt {

	/**
	 * Returns the title
	 * 
	 * @return
	 */
	String getTitle();

	/**
	 * Returns the message
	 * 
	 * @return
	 */
	String getMessage();

}
