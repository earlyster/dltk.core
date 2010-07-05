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
package org.eclipse.dltk.internal.ui.coloring;

import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.dltk.ui.DLTKUIPlugin;
import org.eclipse.dltk.ui.coloring.EnablementStyle;
import org.eclipse.dltk.ui.coloring.FontStyle;
import org.eclipse.dltk.ui.coloring.IColoringPreferenceKey;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.swt.graphics.RGB;

public class DefaultPreferenceRequestor extends
		AbstractColoringPreferenceRequestor {

	private final IPreferenceStore store;
	private final String natureId;

	public DefaultPreferenceRequestor(IPreferenceStore store, String natureId) {
		this.store = store;
		this.natureId = natureId;
	}

	private Set<String> processedKeys = new HashSet<String>();

	public void addPreference(IColoringPreferenceKey key, String name,
			RGB color, EnablementStyle enablementStyle, FontStyle... fontStyles) {
		if (!processedKeys.add(key.getColorKey())) {
			DLTKUIPlugin.warn("Duplicate color preference " + key.getColorKey()
					+ " in " + natureId);
			return;
		}
		PreferenceConverter.setDefault(store, key.getColorKey(), color);
		final EnumSet<FontStyle> fontStyleSet = EnumSet.noneOf(FontStyle.class);
		Collections.addAll(fontStyleSet, fontStyles);
		store.setDefault(key.getBoldKey(),
				fontStyleSet.contains(FontStyle.BOLD));
		store.setDefault(key.getItalicKey(),
				fontStyleSet.contains(FontStyle.ITALIC));
		store.setDefault(key.getUnderlineKey(),
				fontStyleSet.contains(FontStyle.UNDERLINE));
		store.setDefault(key.getStrikethroughKey(),
				fontStyleSet.contains(FontStyle.STRIKETHROUGH));
		if (key.getEnableKey() != null
				&& enablementStyle != EnablementStyle.ALWAYS_ON) {
			store.setDefault(key.getEnableKey(),
					enablementStyle == EnablementStyle.ON);
		}
	}
}
