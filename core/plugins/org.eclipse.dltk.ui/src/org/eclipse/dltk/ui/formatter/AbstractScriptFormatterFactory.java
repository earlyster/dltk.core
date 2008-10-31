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
import org.eclipse.dltk.ui.preferences.PreferenceKey;

/**
 * Abstract base class for the {@link IScriptFormatterFactory} implementations.
 */
public abstract class AbstractScriptFormatterFactory extends
		DLTKContributedExtension implements IScriptFormatterFactory {

	public Map retrievePreferences(IPreferencesLookupDelegate delegate) {
		final Map result = new HashMap();
		final PreferenceKey[] keys = getPreferenceKeys();
		if (keys != null) {
			for (int i = 0; i < keys.length; ++i) {
				final PreferenceKey prefKey = keys[i];
				final String key = prefKey.getName();
				result
						.put(key, delegate.getString(prefKey.getQualifier(),
								key));
			}
		}
		return result;
	}

	public void savePreferences(Map preferences,
			IPreferencesSaveDelegate delegate) {
		final PreferenceKey[] keys = getPreferenceKeys();
		if (keys != null) {
			for (int i = 0; i < keys.length; ++i) {
				final PreferenceKey prefKey = keys[i];
				final String key = prefKey.getName();
				if (preferences.containsKey(key)) {
					final String value = (String) preferences.get(key);
					delegate.setString(prefKey.getQualifier(), key, value);
				}
			}
		}
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
