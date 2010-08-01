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

/**
 * Model element handler for indexing process
 * 
 * @author michael
 * @since 2.0
 * 
 */
public interface IIndexingRequestor {

	/**
	 * Element reference information
	 */
	public class ReferenceInfo {
		/**
		 * Element type ({@link IModelElement#FIELD}, {@link IModelElement#Type}
		 * , etc...)
		 */
		public int elementType;

		/** Element start offset in document */
		public int offset;

		/** Element length in document */
		public int length;

		/** Element name */
		public String elementName;

		/** Various element metadata */
		public String metadata;

		/** Element qualifier */
		public String qualifier;

		public ReferenceInfo(int elementType, int offset, int length,
				String elementName, String metadata, String qualifier) {
			super();
			this.elementType = elementType;
			this.offset = offset;
			this.length = length;
			this.elementName = elementName;
			this.metadata = metadata;
			this.qualifier = qualifier;
		}
	}

	/**
	 * Element declaration information
	 */
	public class DeclarationInfo extends ReferenceInfo {

		/** Element modifiers */
		public int flags;

		/** Element name offset in document */
		public int nameOffset;

		/** Element name length in document */
		public int nameLength;

		/** Element parent information */
		public String parent;

		/** DOC information */
		public String doc;

		public DeclarationInfo(int elementType, int flags, int offset,
				int length, int nameOffset, int nameLength, String elementName,
				String metadata, String doc, String qualifier, String parent) {

			super(elementType, offset, length, elementName, metadata, qualifier);

			this.flags = flags;
			this.nameOffset = nameOffset;
			this.nameLength = nameLength;
			this.parent = parent;
			this.doc = doc;
		}
	}

	/**
	 * Adds new element declaration to the index.
	 * 
	 * @param info
	 *            Information about element declaration
	 */
	public void addDeclaration(DeclarationInfo info);

	/**
	 * Adds new element reference to the index.
	 * 
	 * @param info
	 *            Information about element reference
	 */
	public void addReference(ReferenceInfo info);
}
