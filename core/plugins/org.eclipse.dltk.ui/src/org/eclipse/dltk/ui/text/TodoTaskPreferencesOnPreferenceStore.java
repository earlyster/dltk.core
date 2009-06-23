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
package org.eclipse.dltk.ui.text;

import org.eclipse.dltk.compiler.task.AbstractTodoTaskPreferences;
import org.eclipse.jface.preference.IPreferenceStore;

/**
 * Implementation of the {@link IPreferenceStore} backed by
 * {@link IPreferenceStore}
 */
public class TodoTaskPreferencesOnPreferenceStore extends
		AbstractTodoTaskPreferences {

	private final IPreferenceStore store;

	public TodoTaskPreferencesOnPreferenceStore(IPreferenceStore store) {
		this.store = store;
	}

	@Override
	protected String getRawTaskTags() {
		return store.getString(TAGS);
	}

	public boolean isCaseSensitive() {
		return store.getBoolean(CASE_SENSITIVE);
	}

	public boolean isEnabled() {
		return store.getBoolean(ENABLED);
	}

}
