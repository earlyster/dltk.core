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

import java.net.URL;
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
		final Map result = new HashMap();
		final String qualifier = getPreferenceQualifier();
		final String[] keys = getPreferenceKeys();
		if (qualifier != null && keys != null) {
			for (int i = 0; i < keys.length; ++i) {
				final String key = keys[i];
				result.put(key, delegate.getString(qualifier, key));
			}
		}
		return result;
	}

	public void savePreferences(Map preferences,
			IPreferencesSaveDelegate delegate) {
		final String qualifier = getPreferenceQualifier();
		final String[] keys = getPreferenceKeys();
		if (qualifier != null && keys != null) {
			for (int i = 0; i < keys.length; ++i) {
				final String key = keys[i];
				if (preferences.containsKey(key)) {
					final String value = (String) preferences.get(key);
					delegate.setString(qualifier, key, value);
				}
			}
		}
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

	public URL getPreviewContent() {
		return null;
	}

	public IFormatterModifyDialog createDialog(
			IFormatterModifyDialogOwner dialogOwner) {
		return null;
	}
}
