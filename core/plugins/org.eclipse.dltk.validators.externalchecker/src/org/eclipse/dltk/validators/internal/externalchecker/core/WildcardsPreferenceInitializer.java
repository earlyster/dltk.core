package org.eclipse.dltk.validators.internal.externalchecker.core;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;

public class WildcardsPreferenceInitializer extends
		AbstractPreferenceInitializer {

	public WildcardsPreferenceInitializer() {
	}

	public void initializeDefaultPreferences() {
		String xmlString = ExternalCheckerWildcardManager.getDefaultWildcards();
		ExternalCheckerPlugin.getDefault().getPluginPreferences().setDefault(ExternalCheckerWildcardManager.WILDCARDS, xmlString);
		ExternalCheckerPlugin.getDefault().savePluginPreferences();
	}
}
