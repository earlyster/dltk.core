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
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.eclipse.dltk.compiler.CharOperation;

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

	public static String replace(String text, char c, String s) {

		int previous = 0;
		int current = text.indexOf(c, previous);

		if (current == -1)
			return text;

		StringBuffer buffer = new StringBuffer();
		while (current > -1) {
			buffer.append(text.substring(previous, current));
			buffer.append(s);
			previous = current + 1;
			current = text.indexOf(c, previous);
		}
		buffer.append(text.substring(previous));

		return buffer.toString();
	}

	public static String escapeHTML(String content) {
		content = replace(content, '&', "&amp;"); //$NON-NLS-1$
		content = replace(content, '"', "&quot;"); //$NON-NLS-1$
		content = replace(content, '<', "&lt;"); //$NON-NLS-1$
		return replace(content, '>', "&gt;"); //$NON-NLS-1$
	}

	/**
	 * <p>
	 * Joins the elements of the provided <code>Collection</code> into a single
	 * String containing the provided elements.
	 * </p>
	 * 
	 * <p>
	 * No delimiter is added before or after the list. A <code>null</code>
	 * separator is the same as an empty String ("").
	 * </p>
	 * 
	 * @param collection
	 *            the <code>Collection</code> of values to join together, may be
	 *            null
	 * @param separator
	 *            the separator character to use, null treated as ""
	 * @return the joined String, <code>null</code> if null collection input
	 */
	public static String join(Collection collection, String separator) {
		// handle null, zero and one elements before building a buffer
		if (collection == null) {
			return null;
		}
		if (collection.isEmpty()) {
			return ""; //$NON-NLS-1$
		}
		final Iterator iterator = collection.iterator();
		final Object first = iterator.next();
		if (!iterator.hasNext()) {
			return first != null ? first.toString() : ""; //$NON-NLS-1$
		}
		// two or more elements
		final StringBuffer buf = new StringBuffer(256);
		if (first != null) {
			buf.append(first);
		}
		while (iterator.hasNext()) {
			if (separator != null) {
				buf.append(separator);
			}
			final Object obj = iterator.next();
			if (obj != null) {
				buf.append(obj);
			}
		}
		return buf.toString();
	}

	/**
	 * <p>
	 * Joins the elements of the provided <code>Collection</code> into a single
	 * String containing the provided elements.
	 * </p>
	 * 
	 * <p>
	 * No delimiter is added before or after the list. A <code>null</code>
	 * separator is the same as an empty String ("").
	 * </p>
	 * 
	 * @param collection
	 *            the <code>Collection</code> of values to join together, may be
	 *            null
	 * @param separator
	 *            the separator character to use, null treated as ""
	 * @return the joined String, <code>null</code> if null collection input
	 */
	public static String join(Collection collection, char separator) {
		// handle null, zero and one elements before building a buffer
		if (collection == null) {
			return null;
		}
		if (collection.isEmpty()) {
			return ""; //$NON-NLS-1$
		}
		final Iterator iterator = collection.iterator();
		final Object first = iterator.next();
		if (!iterator.hasNext()) {
			return first != null ? first.toString() : ""; //$NON-NLS-1$
		}
		// two or more elements
		final StringBuffer buf = new StringBuffer(256);
		if (first != null) {
			buf.append(first);
		}
		while (iterator.hasNext()) {
			buf.append(separator);
			final Object obj = iterator.next();
			if (obj != null) {
				buf.append(obj);
			}
		}
		return buf.toString();
	}

	public static String[] split(String str, char separatorChar) {
		if (str == null) {
			return null;
		}
		int len = str.length();
		if (len == 0) {
			return CharOperation.NO_STRINGS;
		}
		int i = str.indexOf(separatorChar);
		if (i == -1) {
			return new String[] { str };
		}
		final List list = new ArrayList();
		int start = 0;
		boolean match = i != 0;
		while (i < len) {
			if (str.charAt(i) == separatorChar) {
				if (match) {
					list.add(str.substring(start, i));
					match = false;
				}
				start = ++i;
				continue;
			}
			match = true;
			i++;
		}
		if (match) {
			list.add(str.substring(start, i));
		}
		return (String[]) list.toArray(new String[list.size()]);
	}

}
