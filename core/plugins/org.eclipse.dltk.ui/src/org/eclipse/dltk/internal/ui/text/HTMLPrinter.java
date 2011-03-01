/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.eclipse.dltk.internal.ui.text;

import java.io.IOException;
import java.io.Reader;
import java.net.URL;

import org.eclipse.dltk.ui.text.HTMLUtils;
import org.eclipse.dltk.utils.TextUtils;
import org.eclipse.swt.graphics.RGB;

/**
 * Provides a set of convenience methods for creating HTML pages.
 */
public class HTMLPrinter {

	private HTMLPrinter() {
	}

	/**
	 * @param content
	 * @return
	 * @deprecated
	 */
	public static String convertToHTMLContent(String content) {
		return TextUtils.escapeHTML(content);
	}

	public static String read(Reader rd) {

		StringBuffer buffer = new StringBuffer();
		char[] readBuffer = new char[2048];

		try {
			int n = rd.read(readBuffer);
			while (n > 0) {
				buffer.append(readBuffer, 0, n);
				n = rd.read(readBuffer);
			}
			return buffer.toString();
		} catch (IOException x) {
		}

		return null;
	}

	public static void insertPageProlog(StringBuffer buffer, int position,
			RGB bgRGB, URL styleSheetURL) {

		if (bgRGB == null)
			insertPageProlog(buffer, position, styleSheetURL);
		else {
			StringBuffer pageProlog = new StringBuffer(300);

			pageProlog.append("<html>"); //$NON-NLS-1$

			appendStyleSheetURL(pageProlog, styleSheetURL);

			pageProlog.append("<body text=\"#000000\" bgcolor=\""); //$NON-NLS-1$
			appendColor(pageProlog, bgRGB);
			pageProlog.append("\">"); //$NON-NLS-1$

			buffer.insert(position, pageProlog.toString());
		}
	}

	public static void insertPageProlog(StringBuffer buffer, int position,
			RGB bgRGB, String styleSheet) {

		if (bgRGB == null)
			insertPageProlog(buffer, position, styleSheet);
		else {
			StringBuffer pageProlog = new StringBuffer(300);

			pageProlog.append("<html>"); //$NON-NLS-1$

			appendStyleSheetURL(pageProlog, styleSheet);

			pageProlog.append("<body text=\"#000000\" bgcolor=\""); //$NON-NLS-1$
			appendColor(pageProlog, bgRGB);
			pageProlog.append("\">"); //$NON-NLS-1$

			buffer.insert(position, pageProlog.toString());
		}
	}

	public static void insertStyles(StringBuffer buffer, String[] styles) {
		if (styles == null || styles.length == 0)
			return;

		StringBuffer styleBuf = new StringBuffer(10 * styles.length);
		for (int i = 0; styles != null && i < styles.length; i++) {
			styleBuf.append(" style=\""); //$NON-NLS-1$
			styleBuf.append(styles[i]);
			styleBuf.append('"');
		}

		// Find insertion index
		int index = buffer.indexOf("<body "); //$NON-NLS-1$
		if (index == -1)
			return;

		buffer.insert(index + 5, styleBuf);
	}

	public static void insertPageProlog(StringBuffer buffer, int position,
			RGB bgRGB) {
		if (bgRGB == null)
			insertPageProlog(buffer, position);
		else {
			StringBuffer pageProlog = new StringBuffer(60);
			pageProlog.append("<html><body text=\"#000000\" bgcolor=\""); //$NON-NLS-1$
			appendColor(pageProlog, bgRGB);
			pageProlog.append("\">"); //$NON-NLS-1$
			buffer.insert(position, pageProlog.toString());
		}
	}

	private static void appendStyleSheetURL(StringBuffer buffer,
			String styleSheet) {
		if (styleSheet == null)
			return;

		buffer.append("<head><style CHARSET=\"ISO-8859-1\" TYPE=\"text/css\">"); //$NON-NLS-1$
		buffer.append(styleSheet);
		buffer.append("</style></head>"); //$NON-NLS-1$
	}

	private static void appendStyleSheetURL(StringBuffer buffer,
			URL styleSheetURL) {
		if (styleSheetURL == null)
			return;

		buffer.append("<head>"); //$NON-NLS-1$

		buffer.append("<LINK REL=\"stylesheet\" HREF= \""); //$NON-NLS-1$
		buffer.append(styleSheetURL);
		buffer.append("\" CHARSET=\"ISO-8859-1\" TYPE=\"text/css\">"); //$NON-NLS-1$

		buffer.append("</head>"); //$NON-NLS-1$
	}

	private static void appendColor(StringBuffer buffer, RGB rgb) {
		buffer.append('#');
		buffer.append(Integer.toHexString(rgb.red));
		buffer.append(Integer.toHexString(rgb.green));
		buffer.append(Integer.toHexString(rgb.blue));
	}

	public static void insertPageProlog(StringBuffer buffer, int position) {
		insertPageProlog(buffer, position, HTMLUtils.getBgColor());
	}

	public static void insertPageProlog(StringBuffer buffer, int position,
			URL styleSheetURL) {
		insertPageProlog(buffer, position, HTMLUtils.getBgColor(),
				styleSheetURL);
	}

	public static void insertPageProlog(StringBuffer buffer, int position,
			String styleSheet) {
		insertPageProlog(buffer, position, HTMLUtils.getBgColor(), styleSheet);
	}

	public static void addPageProlog(StringBuffer buffer) {
		insertPageProlog(buffer, buffer.length());
	}

	public static void addPageEpilog(StringBuffer buffer) {
		buffer.append("</font></body></html>"); //$NON-NLS-1$
	}

	public static void startBulletList(StringBuffer buffer) {
		buffer.append("<ul>"); //$NON-NLS-1$
	}

	public static void endBulletList(StringBuffer buffer) {
		buffer.append("</ul>"); //$NON-NLS-1$
	}

	public static void addBullet(StringBuffer buffer, String bullet) {
		if (bullet != null) {
			buffer.append("<li>"); //$NON-NLS-1$
			buffer.append(bullet);
			buffer.append("</li>"); //$NON-NLS-1$
		}
	}

	public static void addSmallHeader(StringBuffer buffer, String header) {
		if (header != null) {
			buffer.append("<h5>"); //$NON-NLS-1$
			buffer.append(TextUtils.escapeHTML(header));
			buffer.append("</h5>"); //$NON-NLS-1$
		}
	}

	public static void addParagraph(StringBuffer buffer, String paragraph) {
		if (paragraph != null && paragraph.length() > 0) {
			//buffer.append("<p>"); //$NON-NLS-1$
			buffer.append(paragraph);
		}
	}

	public static void addParagraph(StringBuffer buffer, Reader paragraphReader) {
		if (paragraphReader != null)
			addParagraph(buffer, read(paragraphReader));
	}
}
