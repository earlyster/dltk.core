/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.eclipse.dltk.internal.ui.text;

import java.util.Iterator;
import java.util.List;

import org.eclipse.dltk.internal.ui.DLTKUIMessages;
import org.eclipse.dltk.utils.TextUtils;
import org.eclipse.jface.text.source.DefaultAnnotationHover;

/**
 * Determines all markers for the given line and collects, concatenates, and
 * formats returns their messages in HTML.
 */
public class HTMLAnnotationHover extends DefaultAnnotationHover {

	/**
	 * Creates a new HTML annotation hover.
	 * 
	 * @param showLineNumber
	 *            <code>true</code> if the line number should be shown when no
	 *            annotation is found
	 * @since 3.0
	 */
	public HTMLAnnotationHover(boolean showLineNumber) {
		super(showLineNumber);
	}

	/*
	 * Formats a message as HTML text.
	 */
	@Override
	protected String formatSingleMessage(String message) {
		StringBuffer buffer = new StringBuffer();
		HTMLPrinter.addPageProlog(buffer);
		HTMLPrinter.addParagraph(buffer, TextUtils.escapeHTML(message));
		HTMLPrinter.addPageEpilog(buffer);
		return buffer.toString();
	}

	/*
	 * Formats several message as HTML text.
	 */
	@Override
	protected String formatMultipleMessages(
			@SuppressWarnings("rawtypes") List messages) {
		StringBuffer buffer = new StringBuffer();
		HTMLPrinter.addPageProlog(buffer);
		HTMLPrinter
				.addParagraph(
						buffer,
						TextUtils
								.escapeHTML(DLTKUIMessages.ScriptAnnotationHover_multipleMarkersAtThisLine));

		HTMLPrinter.startBulletList(buffer);
		Iterator<?> e = messages.iterator();
		while (e.hasNext())
			HTMLPrinter.addBullet(buffer,
					TextUtils.escapeHTML((String) e.next()));
		HTMLPrinter.endBulletList(buffer);

		HTMLPrinter.addPageEpilog(buffer);
		return buffer.toString();
	}
}
