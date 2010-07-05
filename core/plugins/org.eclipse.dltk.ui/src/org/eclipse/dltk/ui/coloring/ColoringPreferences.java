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

import org.eclipse.dltk.internal.ui.coloring.DefaultPreferenceRequestor;
import org.eclipse.dltk.ui.DLTKUIPlugin;
import org.eclipse.dltk.utils.NatureExtensionManager;
import org.eclipse.jface.preference.IPreferenceStore;

public class ColoringPreferences {

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

	public static IColoringPreferenceProvider[] getProviders(String natureId) {
		return (IColoringPreferenceProvider[]) new NatureExtensionManager(
				DLTKUIPlugin.PLUGIN_ID + ".coloring",
				IColoringPreferenceProvider.class).getInstances(natureId);
	}
}
