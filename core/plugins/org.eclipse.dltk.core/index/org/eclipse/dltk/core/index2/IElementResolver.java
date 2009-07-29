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

import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.ISourceModule;

/**
 * Element resolver restores DLTK model element from index entry.
 * 
 * @author michael
 * 
 */
public interface IElementResolver {

	/**
	 * Resolves model element from the index entry
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
	 *            Element qualifier (package name, for example)
	 * @param parent
	 *            Element parent (declaring type, for example)
	 * @param sourceModule
	 *            Source module where this element is declared
	 */
	public IModelElement resolve(int elementType, int flags, int offset,
			int length, int nameOffset, int nameLength, String elementName,
			String metadata, String qualifier, String parent,
			ISourceModule sourceModule);
}
