/*******************************************************************************
 * Copyright (c) 2008 xored software, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     xored software, Inc. - initial API and Implementation (Alex Panchenko)
 *******************************************************************************/
package org.eclipse.dltk.core.builder;

import org.eclipse.dltk.core.ISourceRange;
import org.eclipse.dltk.internal.core.SourceRange;

public interface ISourceLineTracker {

	public static final ISourceRange NULL_RANGE = new SourceRange(0, 0);
	public static final int WRONG_OFFSET = 0;

	int getLength();

	/**
	 * Returns the line delimiter of the specified line. Returns
	 * <code>null</code> if the line is not closed with a line delimiter.
	 * 
	 * If the line number is invalid in this tracker's line structure then
	 * <code>null</code> is returned
	 * 
	 * @param line
	 *            the line whose line delimiter is queried
	 * @return the line's delimiter or <code>null</code> if line does not have a
	 *         delimiter
	 */
	String getLineDelimiter(int line);

	/**
	 * Returns the number of lines.
	 * 
	 * @return the number of lines in this tracker's line structure
	 */
	int getNumberOfLines();

	/**
	 * Returns the position of the first character of the specified line.
	 * 
	 * if the line is unknown to this tracker then <code>0</code> is returned
	 * 
	 * @param line
	 *            the line of interest
	 * @return offset of the first character of the line
	 */
	int getLineOffset(int line);

	/**
	 * Returns length of the specified line including the line's delimiter.
	 * 
	 * If line is unknown to this tracker <code>0</code> is returned
	 * 
	 * @param line
	 *            the line of interest
	 * @return the length of the line
	 */
	int getLineLength(int line);

	/**
	 * Returns the line number the character at the given offset belongs to.
	 * 
	 * if the offset is invalid in this tracker 0 is returned.
	 * 
	 * @param offset
	 *            the offset whose line number to be determined
	 * @return the number of the line the offset is on
	 */
	int getLineNumberOfOffset(int offset);

	/**
	 * Returns a line description of the line at the given offset. The
	 * description contains the start offset and the length of the line
	 * excluding the line's delimiter.
	 * 
	 * if offset is invalid in this tracker {@link #NULL_RANGE} is returned
	 * 
	 * @param offset
	 *            the offset whose line should be described
	 * @return a region describing the line
	 */
	ISourceRange getLineInformationOfOffset(int offset);

	/**
	 * Returns a line description of the given line. The description contains
	 * the start offset and the length of the line excluding the line's
	 * delimiter.
	 * 
	 * if line is unknown to this tracker {@link #NULL_RANGE} is returned
	 * 
	 * @param line
	 *            the line that should be described
	 * @return a region describing the line
	 */
	ISourceRange getLineInformation(int line);

	int[] getLineOffsets();

	String[] getDelimeters();

}
