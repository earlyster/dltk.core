/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.dltk.internal.mylyn;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import org.eclipse.dltk.ui.DLTKUIPlugin;
import org.eclipse.dltk.ui.PreferenceConstants;
import org.eclipse.dltk.ui.text.completion.CompletionProposalCategory;
import org.eclipse.dltk.ui.text.completion.CompletionProposalComputerRegistry;
import org.eclipse.jface.preference.IPreferenceStore;

/**
 * @author Mik Kersten
 */
public class DLTKUiUtil {

	static final String SEPARATOR_CODEASSIST = "\0"; //$NON-NLS-1$

	public static final String ASSIST_MYLYN_ALL = "org.eclipse.dltk.mylyn.dltkAllProposalCategory"; //$NON-NLS-1$

	public static final String ASSIST_DLTK_ALL = "org.eclipse.dltk.ui.defaultProposalCategory"; //$NON-NLS-1$

	public static final String ASSIST_DLTK_TYPE = "org.eclipse.dltk.ui.scriptTypeProposalCategory"; //$NON-NLS-1$

	public static final String ASSIST_DLTK_TEXT = "org.eclipse.dltk.ui.textProposalCategory"; //$NON-NLS-1$

	public static final String ASSIST_DLTK_NOTYPE = "org.eclipse.dltk.ui.scriptNoTypeProposalCategory"; //$NON-NLS-1$

	private static final String ASSIST_DLTK_TEMPLATE = "org.eclipse.dltk.ui.templateProposalCategory"; //$NON-NLS-1$

	public static boolean isDefaultAssistActive(String computerId) {
		if (DLTKUiUtil.ASSIST_DLTK_ALL.equals(computerId)) {
			CompletionProposalCategory category = getProposalCategory(computerId);
			return (category != null) ? category.isEnabled()
					&& category.isIncluded() : false;
		}
		Set<String> disabledIds = getDisabledIds(DLTKUIPlugin.getDefault()
				.getPreferenceStore());
		return !disabledIds.contains(computerId);
	}

	public static CompletionProposalCategory getProposalCategory(
			String computerId) {
		List<?> computers = CompletionProposalComputerRegistry.getDefault()
				.getProposalCategories();
		for (Object object : computers) {
			CompletionProposalCategory proposalCategory = (CompletionProposalCategory) object;
			if (computerId.equals((proposalCategory).getId())) {
				return proposalCategory;
			}
		}
		return null;
	}

	public static void installContentAssist(IPreferenceStore javaPrefs,
			boolean mylynContentAssist) {
		Set<String> disabledIds = getDisabledIds(javaPrefs);
		if (!mylynContentAssist) {
			disabledIds.remove(ASSIST_DLTK_ALL);
			disabledIds.remove(ASSIST_DLTK_TYPE);
			disabledIds.remove(ASSIST_DLTK_NOTYPE);
			disabledIds.add(ASSIST_MYLYN_ALL);
		} else {
			disabledIds.add(ASSIST_DLTK_ALL);
			disabledIds.add(ASSIST_DLTK_TYPE);
			disabledIds.add(ASSIST_DLTK_NOTYPE);
			// re-enable, Mylyn versions <3.1 had a focused template computer
			// that has been removed
			disabledIds.remove(ASSIST_DLTK_TEMPLATE);
			disabledIds.remove(ASSIST_DLTK_TEXT);
			disabledIds.remove(ASSIST_MYLYN_ALL);
		}
		StringBuilder sb = new StringBuilder();
		for (String id : disabledIds) {
			sb.append(id);
			sb.append(SEPARATOR_CODEASSIST);
		}
		javaPrefs.setValue(PreferenceConstants.CODEASSIST_EXCLUDED_CATEGORIES,
				sb.toString());

		CompletionProposalComputerRegistry.getDefault().reload();
	}

	public static Set<String> getDisabledIds(IPreferenceStore javaPrefs) {
		String oldValue = javaPrefs
				.getString(PreferenceConstants.CODEASSIST_EXCLUDED_CATEGORIES);
		StringTokenizer tokenizer = new StringTokenizer(oldValue,
				SEPARATOR_CODEASSIST);
		Set<String> disabledIds = new HashSet<String>();
		while (tokenizer.hasMoreTokens()) {
			disabledIds.add((String) tokenizer.nextElement());
		}
		return disabledIds;
	}

}
