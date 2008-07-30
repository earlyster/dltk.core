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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.dltk.core.DLTKContributedExtension;
import org.eclipse.dltk.core.PreferencesLookupDelegate;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;

/**
 * Abstract base class for the {@link IScriptFormatterFactory} implementations.
 */
public abstract class AbstractScriptFormatterFactory extends
		DLTKContributedExtension implements IScriptFormatterFactory {

	public Map retrievePreferences(PreferencesLookupDelegate delegate) {
		return new HashMap();
	}

	public int detectIndentationLevel(IDocument document, int offset, Map prefs) {
		final int tabSize = 4;
		try {
			int indent = 0;
			for (int i = offset, docLength = document.getLength(); i < docLength; i++) {
				final char c = document.getChar(i);
				if (c == ' ') {
					++indent;
				} else if (c == '\t') {
					indent = indent - indent % tabSize + tabSize;
				} else
					break;
			}
			return indent / tabSize;
		} catch (BadLocationException e) {
			return 0;
		}
	}
}
