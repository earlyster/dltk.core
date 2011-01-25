/*******************************************************************************
 * Copyright (c) 2011 xored software, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     xored software, Inc. - initial API and Implementation (Alex Panchenko)
 *******************************************************************************/
package org.eclipse.dltk.core.keyword;

import org.eclipse.dltk.core.ISourceModule;

/**
 * Keyword provider
 * 
 * @since 3.0
 */
public interface IKeywordProvider {

	/**
	 * Returns the keywords for the specified <code>category</code>. The meaning
	 * of the category is completely language specific.
	 * 
	 * @param category
	 * @param module
	 * @return
	 */
	String[] getKeywords(IKeywordCategory category, ISourceModule module);

}
