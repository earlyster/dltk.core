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

import org.eclipse.core.runtime.Assert;
import org.eclipse.dltk.core.IPreferencesLookupDelegate;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.PreferencesLookupDelegate;

/**
 * Implementation of the {@link ITodoTaskPreferences} backed by
 * {@link IPreferencesLookupDelegate}
 */
public class TodoTaskPreferencesOnPreferenceLookupDelegate extends
		AbstractTodoTaskPreferences implements ITodoTaskPreferences {

	private String pluginId;
	private IPreferencesLookupDelegate delegate;

	public TodoTaskPreferencesOnPreferenceLookupDelegate(String pluginId,
			IPreferencesLookupDelegate delegate) {
		Assert.isNotNull(pluginId);
		Assert.isNotNull(delegate);

		this.pluginId = pluginId;
		this.delegate = delegate;
	}

	public TodoTaskPreferencesOnPreferenceLookupDelegate(String pluginId,
			IScriptProject project) {
		this(pluginId, new PreferencesLookupDelegate(project));
	}

	public boolean isEnabled() {
		return delegate.getBoolean(pluginId, ENABLED);
	}

	public boolean isCaseSensitive() {
		return delegate.getBoolean(pluginId, CASE_SENSITIVE);
	}

	@Override
	protected String getRawTaskTags() {
		return delegate.getString(pluginId, TAGS);
	}

}
