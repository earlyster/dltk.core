/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.eclipse.dltk.internal.ui.search;

import java.util.StringTokenizer;

import org.eclipse.dltk.core.IField;
import org.eclipse.dltk.core.search.IDLTKSearchConstants;
import org.eclipse.dltk.core.search.SearchMatch;
import org.eclipse.dltk.ui.DLTKUIPlugin;
import org.eclipse.dltk.ui.search.ElementQuerySpecification;
import org.eclipse.dltk.ui.search.PatternQuerySpecification;
import org.eclipse.dltk.ui.search.QuerySpecification;
import org.eclipse.search.ui.text.Match;
import org.eclipse.search.ui.text.MatchFilter;

abstract class DLTKMatchFilter extends MatchFilter {

	private static final String SETTINGS_LAST_USED_FILTERS = "filters_last_used"; //$NON-NLS-1$

	public static DLTKMatchFilter[] getLastUsedFilters() {
		String string = DLTKUIPlugin.getDefault().getDialogSettings().get(
				SETTINGS_LAST_USED_FILTERS);
		if (string != null && string.length() > 0) {
			return decodeFiltersString(string);
		}
		return getDefaultFilters();
	}

	public static void setLastUsedFilters(DLTKMatchFilter[] filters) {
		String encoded = encodeFilters(filters);
		DLTKUIPlugin.getDefault().getDialogSettings().put(
				SETTINGS_LAST_USED_FILTERS, encoded);
	}

	public static DLTKMatchFilter[] getDefaultFilters() {
		return new DLTKMatchFilter[] { POTENTIAL_FILTER };
	}

	private static String encodeFilters(DLTKMatchFilter[] enabledFilters) {
		StringBuffer buf = new StringBuffer();
		buf.append(enabledFilters.length);
		for (int i = 0; i < enabledFilters.length; i++) {
			buf.append(';');
			buf.append(enabledFilters[i].getID());
		}
		return buf.toString();
	}

	private static DLTKMatchFilter[] decodeFiltersString(String encodedString) {
		StringTokenizer tokenizer = new StringTokenizer(encodedString, String
				.valueOf(';'));
		int count = Integer.valueOf(tokenizer.nextToken()).intValue();
		DLTKMatchFilter[] res = new DLTKMatchFilter[count];
		for (int i = 0; i < count; i++) {
			res[i] = findMatchFilter(tokenizer.nextToken());
		}
		return res;
	}

	@Override
	public boolean filters(Match match) {
		if (match instanceof DLTKElementMatch) {
			return filters((DLTKElementMatch) match);
		}
		return false;
	}

	public abstract boolean isApplicable(DLTKSearchQuery query);

	public abstract boolean filters(DLTKElementMatch match);

	private static final DLTKMatchFilter POTENTIAL_FILTER = new PotentialFilter();
	// private static final MatchFilter IMPORT_FILTER= new ImportFilter();
	private static final DLTKMatchFilter SCRIPTDOC_FILTER = new ScriptdocFilter();
	private static final DLTKMatchFilter READ_FILTER = new ReadFilter();
	private static final DLTKMatchFilter WRITE_FILTER = new WriteFilter();
	// private static final MatchFilter INEXACT_FILTER= new
	// InexactMatchFilter();
	// private static final MatchFilter ERASURE_FILTER= new
	// ErasureMatchFilter();

	private static final DLTKMatchFilter[] ALL_FILTERS = new DLTKMatchFilter[] {
			POTENTIAL_FILTER,
			// IMPORT_FILTER,
			SCRIPTDOC_FILTER, READ_FILTER, WRITE_FILTER
	// INEXACT_FILTER,
	// ERASURE_FILTER
	};

	public static DLTKMatchFilter[] allFilters() {
		return ALL_FILTERS;
	}

	private static DLTKMatchFilter findMatchFilter(String id) {
		for (int i = 0; i < ALL_FILTERS.length; i++) {
			if (ALL_FILTERS[i].getID().equals(id))
				return ALL_FILTERS[i];
		}
		return POTENTIAL_FILTER; // just return something, should not happen
	}

}

class PotentialFilter extends DLTKMatchFilter {
	public boolean filters(DLTKElementMatch match) {
		return match.getAccuracy() == SearchMatch.A_INACCURATE;
	}

	public String getName() {
		return SearchMessages.MatchFilter_PotentialFilter_name;
	}

	public String getActionLabel() {
		return SearchMessages.MatchFilter_PotentialFilter_actionLabel;
	}

	public String getDescription() {
		return SearchMessages.MatchFilter_PotentialFilter_description;
	}

	public boolean isApplicable(DLTKSearchQuery query) {
		return true;
	}

	public String getID() {
		return "filter_potential"; //$NON-NLS-1$
	}
}

abstract class FieldFilter extends DLTKMatchFilter {
	public boolean isApplicable(DLTKSearchQuery query) {
		QuerySpecification spec = query.getSpecification();
		if (spec instanceof ElementQuerySpecification) {
			ElementQuerySpecification elementSpec = (ElementQuerySpecification) spec;
			return elementSpec.getElement() instanceof IField;
		} else if (spec instanceof PatternQuerySpecification) {
			PatternQuerySpecification patternSpec = (PatternQuerySpecification) spec;
			return patternSpec.getSearchFor() == IDLTKSearchConstants.FIELD;
		}
		return false;
	}

}

class WriteFilter extends FieldFilter {
	public boolean filters(DLTKElementMatch match) {
		return match.isWriteAccess() && !match.isReadAccess();
	}

	public String getName() {
		return SearchMessages.MatchFilter_WriteFilter_name;
	}

	public String getActionLabel() {
		return SearchMessages.MatchFilter_WriteFilter_actionLabel;
	}

	public String getDescription() {
		return SearchMessages.MatchFilter_WriteFilter_description;
	}

	public String getID() {
		return "filter_writes"; //$NON-NLS-1$
	}
}

class ReadFilter extends FieldFilter {
	public boolean filters(DLTKElementMatch match) {
		return match.isReadAccess() && !match.isWriteAccess();
	}

	public String getName() {
		return SearchMessages.MatchFilter_ReadFilter_name;
	}

	public String getActionLabel() {
		return SearchMessages.MatchFilter_ReadFilter_actionLabel;
	}

	public String getDescription() {
		return SearchMessages.MatchFilter_ReadFilter_description;
	}

	public String getID() {
		return "filter_reads"; //$NON-NLS-1$
	}
}

class ScriptdocFilter extends DLTKMatchFilter {
	public boolean filters(DLTKElementMatch match) {
		return match.isScriptdoc();
	}

	public String getName() {
		return SearchMessages.MatchFilter_DLTKdocFilter_name;
	}

	public String getActionLabel() {
		return SearchMessages.MatchFilter_DLTKdocFilter_actionLabel;
	}

	public String getDescription() {
		return SearchMessages.MatchFilter_DLTKdocFilter_description;
	}

	public boolean isApplicable(DLTKSearchQuery query) {
		return true;
	}

	public String getID() {
		return "filter_javadoc"; //$NON-NLS-1$
	}
}
