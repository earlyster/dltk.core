/*******************************************************************************
 * Copyright (c) 2000, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.dltk.ui.search;

import org.eclipse.dltk.ast.parser.IModuleDeclaration;
import org.eclipse.dltk.core.ISourceModule;

public interface IOccurrencesFinder {

	/**
	 * Element representing a occurrence
	 */
	public static class OccurrenceLocation {
		private final int fOffset;
		private final int fLength;
		private final int fFlags;
		private final String fDescription;

		public OccurrenceLocation(int offset, int length, String description) {
			this(offset, length, 0, description);
		}

		public OccurrenceLocation(int offset, int length, int flags,
				String description) {
			fOffset = offset;
			fLength = length;
			fFlags = flags;
			fDescription = description;
		}

		public int getOffset() {
			return fOffset;
		}

		public int getLength() {
			return fLength;
		}

		public int getFlags() {
			return fFlags;
		}

		public String getDescription() {
			return fDescription;
		}

		public String toString() {
			return "[" + fOffset + " / " + fLength + "] " + fDescription; //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
		}

	}

	/**
	 * Initialize this finder with the specified selection.
	 * 
	 * @param module
	 * @param root
	 * @param offset
	 * @param length
	 * @return <code>null</code> if selection can be processed or a String
	 *         describing the reason of failure.
	 */
	public String initialize(ISourceModule module, IModuleDeclaration root,
			int offset, int length);

	// public String initialize(CompilationUnit root, ASTNode node);

	// public String getJobLabel();

	/**
	 * Returns the plural label for this finder with 3 placeholders:
	 * <ul>
	 * <li>{0} for the {@link #getElementName() element name}</li>
	 * <li>{1} for the number of results found</li>
	 * <li>{2} for the scope (name of the compilation unit)</li>
	 * </ul>
	 * 
	 * @return the unformatted label
	 */
	// public String getUnformattedPluralLabel();

	/**
	 * Returns the singular label for this finder with 2 placeholders:
	 * <ul>
	 * <li>{0} for the {@link #getElementName() element name}</li>
	 * <li>{1} for the scope (name of the compilation unit)</li>
	 * </ul>
	 * 
	 * @return the unformatted label
	 */
	// public String getUnformattedSingularLabel();

	/**
	 * Returns the name of the element to look for or <code>null</code> if the
	 * finder hasn't been initialized yet.
	 * 
	 * @return the name of the element
	 */
	// public String getElementName();

	/**
	 * Returns the AST root.
	 * 
	 * @return the AST root
	 */
	// public CompilationUnit getASTRoot();

	/**
	 * Returns the occurrences found or <code>null</code>
	 * 
	 * @return the occurrences
	 */
	public OccurrenceLocation[] getOccurrences();

	// public int getSearchKind();

	/**
	 * Returns the id of this finder.
	 * 
	 * @return returns the id of this finder.
	 */
	// public String getID();

}
