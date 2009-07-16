/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Zend Technologies
 *******************************************************************************/
package org.eclipse.dltk.core.index2.search;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.search.IDLTKSearchScope;

/**
 * Basic interface for searching model elements in index.
 * 
 * @author michael
 * 
 */
public interface ISearchEngine {

	/**
	 * Match rule: the pattern name must match exactly the name of search result
	 */
	public static final int MR_EXACT = (1 << 0);

	/**
	 * Match rule: the pattern name is a prefix of search results.
	 */
	public static final int MR_PREFIX = (1 << 1);

	/**
	 * Search for: References
	 */
	public static final int SF_REFS = (1 << 0);

	/**
	 * Search for: Declarations
	 */
	public static final int SF_DECLS = (1 << 1);

	/**
	 * Search for model elements in index.
	 * 
	 * @param elementType
	 *            Element type ({@link IModelElement#TYPE},
	 *            {@link IModelElement#METHOD},{@link IModelElement#FIELD},etc.)
	 * @param elementName
	 *            Element name pattern
	 * @param flags
	 *            Element flags (<code>0</code> - disable flag filtering)
	 * @param limit
	 *            Limit number of results (<code>0</code> - unlimited)
	 * @param searchFor
	 *            A combination of {@link #SF_REFS}, {@link #SF_DECLS}
	 * @param matchRule
	 *            A combination of {@link #MR_EXACT}, {@link #MR_PREFIX}
	 * @param scope
	 *            Search scope
	 * @param requestor
	 *            Search requestor
	 * @param monitor
	 *            Progress monitor
	 */
	public void search(int elementType, String elementName, int flags, int limit,
			int searchFor, int matchRule, IDLTKSearchScope scope,
			ISearchRequestor requestor, IProgressMonitor monitor);

}
