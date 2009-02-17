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
package org.eclipse.dltk.core;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;

public class PreferencesDelegate extends PreferencesLookupDelegate implements
		IPreferencesSaveDelegate {

	/**
	 * @param project
	 */
	public PreferencesDelegate(IProject project) {
		super(project);
	}

	private IEclipsePreferences getNode(String qualifier) {
		return getTopScopeContext().getNode(qualifier);
	}

	public void setBoolean(String qualifier, String key, boolean value) {
		getNode(qualifier).putBoolean(key, value);
	}

	public void setInt(String qualifier, String key, int value) {
		getNode(qualifier).putInt(key, value);
	}

	public void setString(String qualifier, String key, String value) {
		if (value != null) {
			getNode(qualifier).put(key, value);
		} else {
			getNode(qualifier).remove(key);
		}
	}

}
