/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.eclipse.dltk.core;

public class SourceRange implements ISourceRange {

	public static final ISourceRange UNKNOWN_RANGE = new SourceRange(-1, 0);

	private final int offset, length;

	public SourceRange(int offset, int length) {
		this.offset = offset;
		this.length = length;
	}

	public int getLength() {
		return length;
	}

	public int getOffset() {
		return offset;
	}

	@Override
	public String toString() {
		int end = getOffset() + getLength() - 1;
		return "[" + getOffset() + "," + end + "]"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	/**
	 * Helper method that answers whether a valid source range is available in
	 * the given ISourceRange.
	 * 
	 * @param range
	 *            a source range, can be <code>null</code>
	 * @return <code>true</code> if range is not <code>null</code> and is not
	 *         {@link #UNKNOWN_RANGE}
	 */
	public static boolean isAvailable(ISourceRange range) {
		return range != null && range != UNKNOWN_RANGE;
	}
}
