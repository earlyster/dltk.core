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

import org.eclipse.dltk.core.IDLTKContributedExtension;
import org.eclipse.dltk.core.IPreferencesLookupDelegate;
import org.eclipse.dltk.core.IPreferencesSaveDelegate;
import org.eclipse.dltk.ui.formatter.preferences.IFormatterDialogOwner;
import org.eclipse.dltk.ui.formatter.preferences.IFormatterModifyDialog;
import org.eclipse.jface.text.IDocument;

/**
 * Script source code formatter factory interface.
 */
public interface IScriptFormatterFactory extends IDLTKContributedExtension {

	/**
	 * Retrieves the formatting options from the specified <code>delegate</code>
	 * 
	 * @param delegate
	 * @return
	 */
	Map retrievePreferences(IPreferencesLookupDelegate delegate);

	String getPreferenceQualifier();

	String[] getPreferenceKeys();

	void savePreferences(Map preferences, IPreferencesSaveDelegate delegate);

	/**
	 * Creates the {@link IScriptFormatter} with the specified preferences.
	 * 
	 * @param lineDelimiter
	 *            the line delimiter to use
	 * @param preferences
	 *            the formatting options
	 */
	IScriptFormatter createFormatter(String lineDelimiter, Map preferences);

	/**
	 * Detects the indentation level at the specified offset
	 * 
	 * @param document
	 * @param offset
	 * @param prefs
	 * @return
	 */
	int detectIndentationLevel(IDocument document, int offset, Map prefs);

	/**
	 * Validates that this formatter factory is correctly installed.
	 * 
	 * @return
	 */
	boolean isValid();

	/**
	 * Return the preview content to use with this formatter or
	 * <code>null</code> if no preview is available.
	 * 
	 * @return
	 */
	String getPreviewContent();

	/**
	 * @return
	 */
	IFormatterModifyDialog createDialog(IFormatterDialogOwner dialogOwner);

}
