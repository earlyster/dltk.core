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

import org.eclipse.dltk.core.ISourceModule;

public interface ISearchRequestor {

	/**
	 * Callback for matching elements.
	 * 
	 * @param elementType
	 *            Element type
	 * @param flags
	 *            Element modifiers
	 * @param offset
	 *            Element offset
	 * @param length
	 *            Element length
	 * @param nameOffset
	 *            Element name offset
	 * @param nameLength
	 *            Element name length
	 * @param elementName
	 *            Element name
	 * @param metadata
	 *            Various metadata attached to the element
	 * @param qualifier
	 *            Element qualifier (package name + parent, for example)
	 * @param sourceModule
	 *            Source module where element is declared
	 * @param isReference
	 *            Whether this element is a reference or declaration
	 */
	public void match(int elementType, int flags, int offset, int length,
			int nameOffset, int nameLength, String elementName,
			String metadata, String qualifier, ISourceModule sourceModule,
			boolean isReference);

}
