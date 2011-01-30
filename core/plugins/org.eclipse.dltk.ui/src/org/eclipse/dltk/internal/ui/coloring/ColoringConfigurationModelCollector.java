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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.dltk.ui.coloring.EnablementStyle;
import org.eclipse.dltk.ui.coloring.FontStyle;
import org.eclipse.dltk.ui.coloring.IColoringCategoryConstants;
import org.eclipse.dltk.ui.coloring.IColoringPreferenceKey;
import org.eclipse.swt.graphics.RGB;

public class ColoringConfigurationModelCollector extends
		AbstractColoringPreferenceRequestor implements
		IColoringCategoryConstants {

	private String category = sCoreCategory;

	@Override
	public void enterCategory(String category) {
		this.category = category;
	}

	private static class Item {
		final String name;
		final String key;
		final String category;
		final EnablementStyle enablementStyle;

		public Item(String name, String key, String category,
				EnablementStyle enablementStyle) {
			this.name = name;
			this.key = key;
			this.category = category;
			this.enablementStyle = enablementStyle;
		}

		boolean canEnable() {
			return enablementStyle != EnablementStyle.ALWAYS_ON;
		}

	}

	private final List<Item> entries = new ArrayList<Item>();

	public void addPreference(IColoringPreferenceKey key, String name,
			RGB color, EnablementStyle enablementStyle, FontStyle... fontStyles) {
		entries.add(new Item(name, key.getColorKey(), category, enablementStyle));
	}

	public String[][] getColorListModel() {
		final String[][] result = new String[entries.size()][];
		for (int i = 0; i < entries.size(); ++i) {
			final Item entry = entries.get(i);
			result[i] = new String[entry.canEnable() ? 4 : 3];
			result[i][0] = entry.name;
			result[i][1] = entry.key;
			result[i][2] = entry.category;
			if (entry.canEnable()) {
				result[i][3] = entry.enablementStyle.name();
			}
		}
		return result;
	}
}
