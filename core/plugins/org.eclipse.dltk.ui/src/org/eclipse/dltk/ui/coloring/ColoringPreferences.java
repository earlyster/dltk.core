/*******************************************************************************
 * Copyright (c) 2010 xored software, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     xored software, Inc. - initial API and Implementation (Alex Panchenko)
 *******************************************************************************/
package org.eclipse.dltk.ui.coloring;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.dltk.internal.ui.coloring.DefaultPreferenceRequestor;
import org.eclipse.dltk.ui.DLTKUIPlugin;
import org.eclipse.dltk.utils.NatureExtensionManager;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.graphics.RGB;

/**
 * @since 3.0
 */
public class ColoringPreferences {

	private static final String EXTENSION_POINT = DLTKUIPlugin.PLUGIN_ID
			+ ".coloring";

	public static void initializeDefaults(IPreferenceStore store,
			String natureId) {
		final IColoringPreferenceProvider[] providers = getProviders(natureId);
		if (providers != null) {
			final IColoringPreferenceRequestor requestor = new DefaultPreferenceRequestor(
					store, natureId);
			for (IColoringPreferenceProvider provider : providers) {
				provider.providePreferences(requestor);
			}
		}
	}

	private static NatureExtensionManager<IColoringPreferenceProvider> preferenceProviderManager = null;

	public static IColoringPreferenceProvider[] getProviders(String natureId) {
		if (preferenceProviderManager == null) {
			preferenceProviderManager = new NatureExtensionManager<IColoringPreferenceProvider>(
					EXTENSION_POINT, IColoringPreferenceProvider.class) {
				@Override
				protected boolean isValidElement(IConfigurationElement element) {
					return "coloring".equals(element.getName());
				}
			};
		}
		return preferenceProviderManager.getInstances(natureId);
	}

	private static NatureExtensionManager<IKeywordColorProvider> keywordColorProviderManager = null;

	public static IKeywordColorProvider[] getKeywordColorProviders(
			String natureId) {
		if (keywordColorProviderManager == null) {
			keywordColorProviderManager = new NatureExtensionManager<IKeywordColorProvider>(
					EXTENSION_POINT, IKeywordColorProvider.class) {
				@Override
				protected boolean isValidElement(IConfigurationElement element) {
					return "keywordColor".equals(element.getName());
				}

				@Override
				protected IKeywordColorProvider[] createEmptyResult() {
					return new IKeywordColorProvider[0];
				}
			};
		}
		return keywordColorProviderManager.getInstances(natureId);
	}

	private static NatureExtensionManager<ITextRuleProvider> textRuleProviderManager = null;

	public static ITextRuleProvider[] getTextRules(String natureId) {
		if (textRuleProviderManager == null) {
			textRuleProviderManager = new NatureExtensionManager<ITextRuleProvider>(
					EXTENSION_POINT, ITextRuleProvider.class) {
				@Override
				protected boolean isValidElement(IConfigurationElement element) {
					return "textRules".equals(element.getName());
				}
			};
		}
		return textRuleProviderManager.getInstances(natureId);
	}

	public static final RGB BLACK = new RGB(0, 0, 0);
}
