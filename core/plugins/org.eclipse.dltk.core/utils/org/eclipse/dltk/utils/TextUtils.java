/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.eclipse.dltk.utils;

import java.util.ArrayList;
import java.util.List;

public abstract class TextUtils {

	private TextUtils() {
		throw new AssertionError("Cannot instantiate utility class"); //$NON-NLS-1$
	}

	/**
	 * (Copied from <code>Pattern</code> class, JRE 5.) Returns a literal
	 * pattern <code>String</code> for the specified <code>String</code>.
	 * 
	 * <p>
	 * This method produces a <code>String</code> that can be used to create a
	 * <code>Pattern</code> that would match the string <code>s</code> as if it
	 * were a literal pattern.
	 * </p>
	 * Metacharacters or escape sequences in the input sequence will be given no
	 * special meaning.
	 * 
	 * @param s
	 *            The string to be literalized
	 * @return A literal string replacement
	 */
	public static String Pattern_quote(String s) {
		int slashEIndex = s.indexOf("\\E"); //$NON-NLS-1$
		if (slashEIndex == -1)
			return "\\Q" + s + "\\E"; //$NON-NLS-1$ //$NON-NLS-2$

		StringBuffer sb = new StringBuffer(s.length() * 2);
		sb.append("\\Q"); //$NON-NLS-1$
		slashEIndex = 0;
		int current = 0;
		while ((slashEIndex = s.indexOf("\\E", current)) != -1) { //$NON-NLS-1$
			sb.append(s.substring(current, slashEIndex));
			current = slashEIndex + 2;
			sb.append("\\E\\\\E\\Q"); //$NON-NLS-1$
		}
		sb.append(s.substring(current, s.length()));
		sb.append("\\E"); //$NON-NLS-1$
		return sb.toString();
	}

	/**
	 * Split this string around line boundaries (handles any line boundaries -
	 * "\n", "\r", "\r\n" so it is not equivalent to String#split("\n"))
	 * 
	 * @param content
	 * @return
	 */
	public static String[] splitLines(CharSequence content) {
		if (content == null) {
			return null;
		}
		final LineSplitter splitter = new LineSplitter(content);
		return splitter.split();
	}

	/**
	 * Counts the number of lines in the specified string. Lines are counter by
	 * the separators ("\n", "\r", "\r\n")
	 * 
	 * @param content
	 * @return
	 */
	public static int countLines(CharSequence content) {
		return new LineSplitter(content).countLines();
	}

	/**
	 * @param content
	 * @param lines
	 * @return
	 */
	public static CharSequence selectHeadLines(CharSequence content, int lines) {
		return new LineSplitter(content).selectHeadLines(lines);
	}

	private static class LineSplitter {

		private final CharSequence content;
		private final int contentEnd;
		private int contentPos;

		public LineSplitter(CharSequence content) {
			this.content = content;
			this.contentEnd = content.length();
		}

		/**
		 * @param lines
		 * @return
		 */
		public CharSequence selectHeadLines(int lines) {
			contentPos = 0;
			while (lines > 0 && contentPos < contentEnd) {
				findEndOfLine();
				--lines;
			}
			return content.subSequence(0, contentPos);
		}

		public String[] split() {
			final List result = new ArrayList();
			contentPos = 0;
			while (contentPos < contentEnd) {
				final int begin = contentPos;
				final int end = findEndOfLine();
				result.add(content.subSequence(begin, end).toString());
			}
			return (String[]) result.toArray(new String[result.size()]);
		}

		public int countLines() {
			contentPos = 0;
			int count = 0;
			while (contentPos < contentEnd) {
				findEndOfLine();
				++count;
			}
			return count;
		}

		private int findEndOfLine() {
			while (contentPos < contentEnd) {
				if (content.charAt(contentPos) == '\r') {
					final int endLine = contentPos;
					++contentPos;
					if (contentPos < contentEnd
							&& content.charAt(contentPos) == '\n') {
						++contentPos;
					}
					return endLine;
				} else if (content.charAt(contentPos) == '\n') {
					final int endLine = contentPos;
					++contentPos;
					return endLine;
				} else {
					++contentPos;
				}
			}
			return contentPos;
		}

	}
}
