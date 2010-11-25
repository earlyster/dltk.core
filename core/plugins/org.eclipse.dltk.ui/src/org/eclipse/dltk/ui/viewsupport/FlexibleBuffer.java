/*******************************************************************************
 * Copyright (c) 2000, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.dltk.ui.viewsupport;

import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.StyledString.Styler;

/**
 * An adapter for buffer supported by the label composer.
 */
public abstract class FlexibleBuffer {

	/**
	 * Appends the string representation of the given character to the buffer.
	 * 
	 * @param ch
	 *            the character to append
	 * @return a reference to this object
	 */
	public abstract FlexibleBuffer append(char ch);

	/**
	 * Appends the given string to the buffer.
	 * 
	 * @param string
	 *            the string to append
	 * @return a reference to this object
	 */
	public abstract FlexibleBuffer append(String string);

	/**
	 * Returns the length of the the buffer.
	 * 
	 * @return the length of the current string
	 */
	public abstract int length();

	/**
	 * Sets a styler to use for the given source range. The range must be
	 * subrange of actual string of this buffer. Stylers previously set for that
	 * range will be overwritten.
	 * 
	 * @param offset
	 *            the start offset of the range
	 * @param length
	 *            the length of the range
	 * @param styler
	 *            the styler to set
	 * 
	 * @throws StringIndexOutOfBoundsException
	 *             if <code>start</code> is less than zero, or if offset plus
	 *             length is greater than the length of this object.
	 */
	public abstract void setStyle(int offset, int length, Styler styler);

	public static class FlexibleStringBuffer extends FlexibleBuffer {
		private final StringBuffer fStringBuffer;

		public FlexibleStringBuffer(StringBuffer stringBuffer) {
			fStringBuffer = stringBuffer;
		}

		public FlexibleBuffer append(char ch) {
			fStringBuffer.append(ch);
			return this;
		}

		public FlexibleBuffer append(String string) {
			fStringBuffer.append(string);
			return this;
		}

		public int length() {
			return fStringBuffer.length();
		}

		public void setStyle(int offset, int length, Styler styler) {
			// no style
		}

		public String toString() {
			return fStringBuffer.toString();
		}
	}

	public static class FlexibleStyledString extends FlexibleBuffer {
		private final StyledString fStyledString;

		public FlexibleStyledString(StyledString stringBuffer) {
			fStyledString = stringBuffer;
		}

		public FlexibleBuffer append(char ch) {
			fStyledString.append(ch);
			return this;
		}

		public FlexibleBuffer append(String string) {
			fStyledString.append(string);
			return this;
		}

		public int length() {
			return fStyledString.length();
		}

		public void setStyle(int offset, int length, Styler styler) {
			fStyledString.setStyle(offset, length, styler);
		}

		public String toString() {
			return fStyledString.toString();
		}
	}
}
