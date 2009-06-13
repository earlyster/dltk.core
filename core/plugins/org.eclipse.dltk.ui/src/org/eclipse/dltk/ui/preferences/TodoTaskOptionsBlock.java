/*******************************************************************************
 * Copyright (c) 2009 xored software, Inc.
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

import org.eclipse.core.resources.IProject;
import org.eclipse.dltk.ui.util.IStatusChangeListener;
import org.eclipse.ui.preferences.IWorkbenchPreferenceContainer;

public class TodoTaskOptionsBlock extends AbstractTodoTaskOptionsBlock {

	private final PreferenceKey tagsKey;
	private final PreferenceKey enabledKey;
	private final PreferenceKey caseSensitiveKey;

	public TodoTaskOptionsBlock(IStatusChangeListener context,
			IProject project, IWorkbenchPreferenceContainer container,
			String qualifier) {
		this(context, project, container, createTagKey(qualifier),
				createEnabledKey(qualifier), createCaseSensitiveKey(qualifier));
	}

	public TodoTaskOptionsBlock(IStatusChangeListener context,
			IProject project, IWorkbenchPreferenceContainer container,
			PreferenceKey tagsKey, PreferenceKey enabledKey,
			PreferenceKey caseSensitiveKey) {
		super(context, project, new PreferenceKey[] { tagsKey, enabledKey,
				caseSensitiveKey }, container);
		this.tagsKey = tagsKey;
		this.enabledKey = enabledKey;
		this.caseSensitiveKey = caseSensitiveKey;
	}

	/**
	 * Returns the preference key that will be used to store the 'case
	 * sensitive' preference
	 * 
	 * @see #createCaseSensitiveKey(String)
	 */
	protected final PreferenceKey getCaseSensitiveKey() {
		return caseSensitiveKey;
	}

	/**
	 * Returns the preference key that will be used to store the 'enabled'
	 * preference
	 * 
	 * @see #createEnabledKey(String)
	 */
	protected final PreferenceKey getEnabledKey() {
		return enabledKey;
	}

	/**
	 * Returns the preference key that will be used to store the task tags
	 * 
	 * @see #createTagKey(String)
	 */
	protected final PreferenceKey getTags() {
		return tagsKey;
	}

}
