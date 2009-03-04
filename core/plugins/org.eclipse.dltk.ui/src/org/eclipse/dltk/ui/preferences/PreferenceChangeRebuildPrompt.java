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

import org.eclipse.osgi.util.NLS;

/**
 * Default implementation of the {@link IPreferenceChangeRebuildPrompt}
 */
public class PreferenceChangeRebuildPrompt implements
		IPreferenceChangeRebuildPrompt {

	private String title;
	private String message;
	private String messageTemplate;

	/**
	 * Creates instances without messageTemplate
	 */
	public PreferenceChangeRebuildPrompt() {
		this(null);
	}

	/**
	 * Creates instance with the specified messageTemplate
	 * 
	 * @param messageTemplate
	 */
	public PreferenceChangeRebuildPrompt(String messageTemplate) {
		this.messageTemplate = messageTemplate;
	}

	/**
	 * Creates instances with default messageTemplate depends on the
	 * <code>workspaceSettings</code> parameter
	 * 
	 * @param workspaceSettings
	 */
	public PreferenceChangeRebuildPrompt(boolean workspaceSettings) {
		if (workspaceSettings) {
			messageTemplate = PreferencesMessages.PreferenceChange_rebuildWorkspaceMessageTemplate;
		} else {
			messageTemplate = PreferencesMessages.PreferenceChange_rebuildProjectMessageTemplate;
		}
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void setMessageTemplate(String messageTemplate) {
		this.messageTemplate = messageTemplate;
	}

	public String getMessage() {
		if (messageTemplate == null || messageTemplate.length() == 0) {
			return message;
		} else {
			return NLS.bind(messageTemplate, message);
		}
	}

	public String getTitle() {
		return title;
	}

	/**
	 * Factory method to create the instance with the specified parameters
	 * 
	 * @param workspaceSettings
	 * @param title
	 * @param message
	 * @return
	 */
	public static IPreferenceChangeRebuildPrompt create(
			boolean workspaceSettings, String title, String message) {
		PreferenceChangeRebuildPrompt prompt = new PreferenceChangeRebuildPrompt(
				workspaceSettings);
		prompt.setTitle(title);
		prompt.setMessage(message);
		return prompt;
	}

}
