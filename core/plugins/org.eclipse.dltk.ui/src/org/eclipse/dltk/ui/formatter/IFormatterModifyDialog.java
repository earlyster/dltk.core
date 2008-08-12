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
package org.eclipse.dltk.ui.formatter;

import java.util.Map;

public interface IFormatterModifyDialog {

	/**
	 * Sets the preferences the dialog should use
	 * 
	 * @param prefs
	 */
	void setPreferences(Map prefs);

	/**
	 * Opens the modal dialog and returns only after the dialog was completed.
	 * The return value should be {@link org.eclipse.jface.window.Window#OK} or
	 * {@link org.eclipse.jface.window.Window#CANCEL}
	 */
	int open();

	/**
	 * Returns the preferences modified by the dialog
	 * 
	 * @return
	 */
	Map getPreferences();

	IFormatterModifyDialogOwner getOwner();

	IScriptFormatterFactory getFormatterFactory();

}
