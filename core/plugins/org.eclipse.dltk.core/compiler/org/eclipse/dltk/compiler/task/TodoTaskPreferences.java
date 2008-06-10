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

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.eclipse.core.runtime.Preferences;
import org.eclipse.dltk.core.DLTKCore;

public class TodoTaskPreferences implements ITodoTaskPreferences {

	public static final String CASE_SENSITIVE = DLTKCore.PLUGIN_ID
			+ "tasks.case_sensitive"; //$NON-NLS-1$
	public static final String TAGS = DLTKCore.PLUGIN_ID + "tasks.tags"; //$NON-NLS-1$
	public static final String ENABLED = DLTKCore.PLUGIN_ID + "tasks.enabled"; //$NON-NLS-1$

	private static final String TAG_SEPARATOR = ","; //$NON-NLS-1$
	private static final String PRIORITY_SEPARATOR = ";"; //$NON-NLS-1$

	public static boolean isValidName(String newText) {
		return newText.indexOf(TAG_SEPARATOR.charAt(0)) < 0
				&& newText.indexOf(PRIORITY_SEPARATOR.charAt(0)) < 0;
	}

	private final Preferences store;

	public TodoTaskPreferences(Preferences store) {
		this.store = store;
	}

	protected String[] getTokens(String text, String separator) {
		final StringTokenizer tok = new StringTokenizer(text, separator);
		final int nTokens = tok.countTokens();
		final String[] res = new String[nTokens];
		for (int i = 0; i < res.length; i++) {
			res[i] = tok.nextToken().trim();
		}
		return res;
	}

	public boolean isEnabled() {
		return store.getBoolean(ENABLED);
	}

	public boolean isCaseSensitive() {
		return store.getBoolean(CASE_SENSITIVE);
	}

	public List getTaskTags() {
		final String tags = store.getString(TAGS);
		final String[] tagPairs = getTokens(tags, TAG_SEPARATOR);
		final List elements = new ArrayList();
		for (int i = 0; i < tagPairs.length; ++i) {
			final String[] values = getTokens(tagPairs[i], PRIORITY_SEPARATOR);
			final TodoTask task = new TodoTask();
			task.name = values[0];
			if (values.length == 2) {
				task.priority = values[1];
			} else {
				task.priority = TodoTask.PRIORITY_NORMAL;
			}
			elements.add(task);
		}
		return elements;
	}

	public void setTaskTags(List elements) {
		store.setValue(TAGS, encodeTaskTags(elements));
	}

	private static String encodeTaskTags(List elements) {
		final StringBuffer sb = new StringBuffer();
		for (int i = 0; i < elements.size(); ++i) {
			final TodoTask task = (TodoTask) elements.get(i);
			if (i > 0) {
				sb.append(TAG_SEPARATOR);
			}
			sb.append(task.name);
			sb.append(PRIORITY_SEPARATOR);
			sb.append(task.priority);
		}
		final String string = sb.toString();
		return string;
	}

	public static List getDefaultTags() {
		final List defaultTags = new ArrayList();
		defaultTags.add(new TodoTask("TODO", TodoTask.PRIORITY_NORMAL)); //$NON-NLS-1$
		defaultTags.add(new TodoTask("FIXME", TodoTask.PRIORITY_HIGH)); //$NON-NLS-1$ 
		defaultTags.add(new TodoTask("XXX", TodoTask.PRIORITY_NORMAL)); //$NON-NLS-1$
		return defaultTags;
	}

	public static void initializeDefaultValues(Preferences store) {
		store.setDefault(ENABLED, true);
		store.setDefault(CASE_SENSITIVE, true);
		store.setDefault(TAGS, encodeTaskTags(getDefaultTags()));
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

}
