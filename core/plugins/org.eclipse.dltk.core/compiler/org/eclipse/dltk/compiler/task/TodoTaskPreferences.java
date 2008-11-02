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
package org.eclipse.dltk.compiler.task;

import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.Preferences;
import org.eclipse.dltk.core.IPreferencesLookupDelegate;
import org.eclipse.dltk.core.PluginPreferencesLookupDelegate;

/**
 */
public class TodoTaskPreferences implements ITodoTaskPreferences {

	private String pluginId;
	private IPreferencesLookupDelegate delegate;

	private Preferences store;

	/**
	 * @deprecated use
	 *             {@link #TodoTaskPreferences(String, IPreferencesLookupDelegate)}
	 *             instead
	 */
	public TodoTaskPreferences(Preferences store) {
		this.store = store;
		delegate = new PluginPreferencesLookupDelegate(store);
	}

	public TodoTaskPreferences(String pluginId,
			IPreferencesLookupDelegate delegate) {
		Assert.isNotNull(pluginId);
		Assert.isNotNull(delegate);

		this.pluginId = pluginId;
		this.delegate = delegate;
	}

	public boolean isEnabled() {
		return delegate.getBoolean(pluginId, ENABLED);
	}

	public boolean isCaseSensitive() {
		return delegate.getBoolean(pluginId, CASE_SENSITIVE);
	}

	public List getTaskTags() {
		final String tags = delegate.getString(pluginId, TAGS);
		return TaskTagUtils.decodeTaskTags(tags);
	}

	/**
	 * @deprecated
	 */
	public void setTaskTags(List elements) {
		store.setValue(TAGS, TaskTagUtils.encodeTaskTags(elements));
	}

	public String[] getTagNames() {
		final List taskTags = getTaskTags();
		final int size = taskTags.size();
		final String[] result = new String[size];
		for (int i = 0; i < size; ++i) {
			result[i] = ((TodoTask) taskTags.get(i)).name;
		}
		return result;
	}

	/**
	 * @deprecated use
	 *             {@link TaskTagUtils#initializeDefaultValues(Preferences)}
	 */
	public static void initializeDefaultValues(Preferences store) {
		TaskTagUtils.initializeDefaultValues(store);
	}
}