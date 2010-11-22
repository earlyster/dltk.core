/*******************************************************************************
 * Copyright (c) 2010 xored software, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     xored software, Inc. - initial API and Implementation (Alex Panchenko)
 *******************************************************************************/
package org.eclipse.dltk.ui.search;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.dltk.ast.parser.IModuleDeclaration;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.core.search.IDLTKSearchConstants;
import org.eclipse.dltk.core.search.SearchEngine;
import org.eclipse.dltk.core.search.SearchMatch;
import org.eclipse.dltk.core.search.SearchParticipant;
import org.eclipse.dltk.core.search.SearchPattern;
import org.eclipse.dltk.core.search.SearchRequestor;
import org.eclipse.dltk.ui.DLTKUIPlugin;

public class ModelElementOccurrencesFinder implements IOccurrencesFinder {

	private ISourceModule module;
	private SearchPattern pattern;
	private String occurrenceLocationDescription;

	public String initialize(ISourceModule module, IModuleDeclaration root,
			int offset, int length) {
		this.module = module;
		pattern = null;
		IModelElement[] elements;
		try {
			elements = module.codeSelect(offset, length);
		} catch (ModelException e) {
			return e.toString();
		}
		if (elements.length == 0) {
			return "No selection";
		}
		for (IModelElement element : elements) {
			pattern = SearchPattern.createPattern(element,
					IDLTKSearchConstants.ALL_OCCURRENCES);
			if (pattern != null) {
				occurrenceLocationDescription = "Occurrence of '"
						+ element.getElementName() + "'";
				break;
			}
		}
		if (pattern == null) {
			return "Can't search for current selection";
		}
		return null;
	}

	public OccurrenceLocation[] getOccurrences() {
		try {
			final List<OccurrenceLocation> result = new ArrayList<IOccurrencesFinder.OccurrenceLocation>();
			new SearchEngine().search(pattern,
					new SearchParticipant[] { SearchEngine
							.getDefaultSearchParticipant() }, SearchEngine
							.createSearchScope(module), new SearchRequestor() {
						@Override
						public void acceptSearchMatch(SearchMatch match)
								throws CoreException {
							result.add(new OccurrenceLocation(
									match.getOffset(), match.getLength(),
									occurrenceLocationDescription));
						}
					}, null);
			if (!result.isEmpty()) {
				return result.toArray(new OccurrenceLocation[result.size()]);
			}
		} catch (CoreException e) {
			DLTKUIPlugin.log(e);
		}
		return null;
	}
}
