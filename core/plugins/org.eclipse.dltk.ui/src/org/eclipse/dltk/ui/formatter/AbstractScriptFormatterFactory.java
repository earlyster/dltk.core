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
import org.eclipse.dltk.core.IPreferencesLookupDelegate;
import org.eclipse.dltk.core.IPreferencesSaveDelegate;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;

/**
 * Abstract base class for the {@link IScriptFormatterFactory} implementations.
 */
public abstract class AbstractScriptFormatterFactory extends
		DLTKContributedExtension implements IScriptFormatterFactory {

	public Map retrievePreferences(IPreferencesLookupDelegate delegate) {
		return new HashMap();
	}

	public void savePreferences(Map preferences,
			IPreferencesSaveDelegate delegate) {
		// empty
	}

	public String getPreferenceQualifier() {
		return null;
	}

	public String[] getPreferenceKeys() {
		return null;
	}

	public int detectIndentationLevel(IDocument document, int offset, Map prefs) {
		final int tabSize = 4;
		int indent = 0;
		if (offset > 0) {
			try {
				final int line = document.getLineOfOffset(offset);
				final int lineStart = document.getLineOffset(line);
				for (int i = lineStart, docLength = document.getLength(); i < docLength; i++) {
					final char c = document.getChar(i);
					if (c == ' ') {
						++indent;
					} else if (c == '\t') {
						indent = indent - indent % tabSize + tabSize;
					} else
						break;
				}
			} catch (BadLocationException e) {
				return 0;
			}
		}
		return indent / tabSize;
	}

	public boolean isValid() {
		return true;
	}

	public String getPreviewContent() {
		return null;
	}

	public IFormatterModifyDialog createDialog(IFormatterDialogOwner dialogOwner) {
		return null;
	}
}
