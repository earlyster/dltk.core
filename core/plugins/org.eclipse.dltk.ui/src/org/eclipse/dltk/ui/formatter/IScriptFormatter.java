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

import org.eclipse.text.edits.TextEdit;

/**
 * Script source code formatter interface.
 */
public interface IScriptFormatter {

	/**
	 * Format <code>source</code>, and returns a text edit that correspond to
	 * the difference between the given string and the formatted string.
	 * 
	 * <p>
	 * It returns null if the given string cannot be formatted.
	 * </p>
	 * 
	 * @param source
	 *            full source module content
	 * @param offset
	 *            the offset of the region to format
	 * @param length
	 *            the length of the region to format
	 * @param indentationLevel
	 *            the additional indent level
	 */
	TextEdit format(String source, int offset, int length, int indentationLevel)
			throws FormatterException;

}
