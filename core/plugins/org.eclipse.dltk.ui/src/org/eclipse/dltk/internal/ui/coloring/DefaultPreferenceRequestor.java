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

import org.eclipse.dltk.ui.coloring.EnablementStyle;
import org.eclipse.dltk.ui.coloring.FontStyle;
import org.eclipse.dltk.ui.coloring.IColoringPreferenceKey;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.swt.graphics.RGB;

public class DefaultPreferenceRequestor extends
		AbstractColoringPreferenceRequestor {

	private final IPreferenceStore store;

	public DefaultPreferenceRequestor(IPreferenceStore store) {
		this.store = store;
	}

	public void addPreference(IColoringPreferenceKey key, String name,
			RGB color, EnablementStyle enablementStyle, FontStyle... fontStyles) {
		PreferenceConverter.setDefault(store, key.getColorKey(), color);
		final EnumSet<FontStyle> fontStyleSet = EnumSet.noneOf(FontStyle.class);
		Collections.addAll(fontStyleSet, fontStyles);
		store.setDefault(key.getBoldKey(), fontStyleSet
				.contains(FontStyle.BOLD));
		store.setDefault(key.getItalicKey(), fontStyleSet
				.contains(FontStyle.ITALIC));
		store.setDefault(key.getUnderlineKey(), fontStyleSet
				.contains(FontStyle.UNDERLINE));
		store.setDefault(key.getStrikethroughKey(), fontStyleSet
				.contains(FontStyle.STRIKETHROUGH));
		if (key.getEnableKey() != null
				&& enablementStyle != EnablementStyle.ALWAYS_ON) {
			store.setDefault(key.getEnableKey(),
					enablementStyle == EnablementStyle.ON);
		}
	}

}
