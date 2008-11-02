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
package org.eclipse.dltk.ui.preferences;

import org.eclipse.core.runtime.Preferences;
import org.eclipse.dltk.ui.PreferencesAdapter;

/**
 * @deprecated use an implementation of
 *             {@link AbstractConfigurationBlockPropertyAndPreferencePage}
 * 
 * @see AbstractTodoTaskOptionsBlock
 */
public abstract class TodoTaskAbstractPreferencePage extends
		AbstractConfigurationBlockPreferencePage {

	protected abstract Preferences getPluginPreferences();

	protected IPreferenceConfigurationBlock createConfigurationBlock(
			OverlayPreferenceStore overlayPreferenceStore) {
		return new TodoTaskConfigurationBlock(getPluginPreferences(),
				overlayPreferenceStore, this);
	}

	protected void setPreferenceStore() {
		setPreferenceStore(new PreferencesAdapter(getPluginPreferences()));
	}

}
