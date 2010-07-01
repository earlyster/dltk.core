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

import org.eclipse.swt.graphics.RGB;

public interface IColoringPreferenceRequestor {

	void enterCategory(String category);

	void addPreference(String baseKey, String name, RGB color,
			FontStyle... styles);

	void addPreference(String baseKey, String name, RGB color,
			EnablementStyle enablementStyle, FontStyle... fontStyles);

	void addPreference(IColoringPreferenceKey key, String name, RGB color,
			FontStyle... styles);

	void addPreference(IColoringPreferenceKey key, String name, RGB color,
			EnablementStyle enablementStyle, FontStyle... fontStyles);

}
