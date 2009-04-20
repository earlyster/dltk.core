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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.dltk.core.DLTKContributedExtension;
import org.eclipse.dltk.core.DLTKLanguageManager;
import org.eclipse.dltk.core.IDLTKLanguageToolkit;
import org.eclipse.dltk.core.IPreferencesLookupDelegate;
import org.eclipse.dltk.core.IPreferencesSaveDelegate;
import org.eclipse.dltk.internal.ui.formatter.profiles.BuiltInProfile;
import org.eclipse.dltk.ui.preferences.PreferenceKey;

/**
 * Abstract base class for the {@link IScriptFormatterFactory} implementations.
 */
public abstract class AbstractScriptFormatterFactory extends
		DLTKContributedExtension implements IScriptFormatterFactory {

	protected IProfileVersioner versioner;

	protected String getDefaultProfileID() {
		StringBuffer buffer = new StringBuffer();
		String lang = getLanguage();
		if (lang != null && lang.length() > 0) {
			buffer.append("org.eclipse.dltk."); //$NON-NLS-1$
			buffer.append(lang.toLowerCase());
		} else {
			buffer.append(getClass().getName());
		}

		buffer.append(".formatter.profiles.default"); //$NON-NLS-1$
		return buffer.toString();
	}

	protected String getDefaultProfileName() {
		return FormatterMessages.AbstractScriptFormatterFactory_defaultProfileName;
	}

	public List getBuiltInProfiles() {
		List profiles = new ArrayList();

		IProfileVersioner versioner = getProfileVersioner();
		BuiltInProfile profile = new BuiltInProfile(getDefaultProfileID(),
				getDefaultProfileName(), loadDefaultSettings(), 1, getId(),
				versioner.getCurrentVersion());

		profiles.add(profile);
		return profiles;
	}

	public Map loadDefaultSettings() {
		DefaultScope scope = new DefaultScope();
		Map settings = new HashMap();
		PreferenceKey[] keys = getPreferenceKeys();
		for (int i = 0; i < keys.length; i++) {
			PreferenceKey key = keys[i];
			String name = key.getName();
			IEclipsePreferences preferences = scope.getNode(key.getQualifier());
			String value = preferences.get(name, null);
			if (value != null)
				settings.put(name, value);
		}
		return settings;
	}

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
		final String profilesKey = getProfilesKey().getName();
		result.put(profilesKey, delegate.getString(getProfilesKey()
				.getQualifier(), profilesKey));

		final String activeProfileKey = getActiveProfileKey().getName();
		result.put(activeProfileKey, delegate.getString(getActiveProfileKey()
				.getQualifier(), activeProfileKey));

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

		final String profilesKey = getProfilesKey().getName();
		if (preferences.containsKey(profilesKey)) {
			final String value = (String) preferences.get(profilesKey);
			delegate.setString(getProfilesKey().getQualifier(), profilesKey,
					value);
		}

		final String activeProfileKey = getActiveProfileKey().getName();
		if (preferences.containsKey(activeProfileKey)) {
			final String value = (String) preferences.get(activeProfileKey);
			delegate.setString(getProfilesKey().getQualifier(),
					activeProfileKey, value);
		}
	}

	public IProfileVersioner getProfileVersioner() {
		if (versioner == null)
			versioner = createProfileVersioner();
		return versioner;
	}

	protected IProfileVersioner createProfileVersioner() {
		return new GeneralProfileVersioner(getId());
	}

	public boolean isValid() {
		return true;
	}

	public URL getPreviewContent() {
		return null;
	}

	public IFormatterModifyDialog createDialog(
			IFormatterModifyDialogOwner dialogOwner, IProfileManager manager) {
		return null;
	}

	private String getLanguage() {
		IDLTKLanguageToolkit toolkit = DLTKLanguageManager
				.getLanguageToolkit(getNatureId());
		if (toolkit != null)
			return toolkit.getLanguageName();
		return null;
	}
}
