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
package org.eclipse.dltk.core.index2;

/**
 * Model element handler for indexing process
 * 
 * @author michael
 * 
 */
public interface IIndexingRequestor {

	/**
	 * Adds new element declaration to the index.
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
	 */
	public void addDeclaration(int elementType, int flags, int offset,
			int length, int nameOffset, int nameLength, String elementName,
			String metadata, String qualifier);

	/**
	 * Adds new element reference to the index.
	 * 
	 * @param elementType
	 *            Element type
	 * @param offset
	 *            Element offset
	 * @param length
	 *            Element length
	 * @param elementName
	 *            Element name
	 * @param metadata
	 *            Various metadata attached to the element
	 * @param qualifier
	 *            Element qualifier (package name + parent, for example)
	 */
	public void addReference(int elementType, int offset, int length,
			String elementName, String metadata, String qualifier);
}
