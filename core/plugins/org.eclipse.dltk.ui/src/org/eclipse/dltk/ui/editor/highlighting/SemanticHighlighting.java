/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.dltk.ui.editor.highlighting;

import org.eclipse.dltk.ui.PreferenceConstants;

/**
 * Semantic highlighting
 */
public abstract class SemanticHighlighting {

	/**
	 * @return the preference key, will be augmented by a prefix and a suffix
	 *         for each preference
	 */
	public abstract String getPreferenceKey();

	/**
	 * @return the preference key to control enable of this semantic
	 *         highlighting.
	 */
	public String getEnabledPreferenceKey() {
		return getPreferenceKey()
				+ PreferenceConstants.EDITOR_SEMANTIC_HIGHLIGHTING_ENABLED_SUFFIX;
	}

	/**
	 * Tests if this semantic highlighting could be enabled/disabled.
	 * 
	 * @return <code>true</code> means that this highlighting is an additional
	 *         feature that could be disabled in preferences, and
	 *         <code>false</code> means that this highlighting is used to
	 *         correct highlighting based on rules, so this could not be
	 *         disabled in preferences.
	 */
	public boolean isSemanticOnly() {
		return false;
	}

	/**
	 * @return <code>true</code> if this highlighting should be enabled by
	 *         default
	 */
	public boolean isEnabledByDefault() {
		return true;
	}

	/**
	 * @return the display name
	 */
	public String getDisplayName() {
		return getPreferenceKey();
	}

	public String getBackgroundPreferenceKey() {
		return null;
	}

}
