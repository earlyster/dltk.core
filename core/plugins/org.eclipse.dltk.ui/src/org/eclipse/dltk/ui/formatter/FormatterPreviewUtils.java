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
package org.eclipse.dltk.ui.formatter;

import java.io.IOException;
import java.net.URL;
import java.util.Map;

import org.eclipse.dltk.compiler.util.Util;
import org.eclipse.dltk.internal.corext.util.Strings;
import org.eclipse.dltk.ui.DLTKUIPlugin;
import org.eclipse.dltk.utils.TextUtils;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.TextEdit;

public class FormatterPreviewUtils {

	private static final String LINE_SEPARATOR = "\n"; //$NON-NLS-1$

	private static final String ENCODING = "ISO-8859-1"; //$NON-NLS-1$

	public static void updatePreview(ISourceViewer viewer, URL previewContent,
			IScriptFormatterFactory factory, Map preferences) {
		if (previewContent != null) {
			final String content;
			try {
				final String c = new String(Util.getInputStreamAsCharArray(
						previewContent.openConnection().getInputStream(), -1,
						ENCODING));
				content = Strings.concatenate(TextUtils.splitLines(c),
						LINE_SEPARATOR);
			} catch (IOException e) {
				DLTKUIPlugin.log(e);
				disablePreview(viewer);
				return;
			}
			viewer.getTextWidget().setEnabled(true);
			IScriptFormatter formatter = factory.createFormatter(
					LINE_SEPARATOR, preferences);
			try {
				TextEdit textEdit = formatter.format(content, 0, content
						.length(), 0);
				if (textEdit != null) {
					IDocument document = new Document(content);
					textEdit.apply(document);
					// TODO change background to white
					viewer.getDocument().set(document.get());
					return;
				}
			} catch (BadLocationException e) {
				DLTKUIPlugin.log(e);
			} catch (MalformedTreeException e) {
				DLTKUIPlugin.log(e);
			} catch (FormatterSyntaxProblemException e) {
				// skip
			} catch (FormatterException e) {
				DLTKUIPlugin.log(e);
			}
			// TODO indicate error/warning state
			viewer.getDocument().set(content);
		} else {
			disablePreview(viewer);
		}
	}

	private static void disablePreview(ISourceViewer viewer) {
		viewer.getTextWidget().setEnabled(false);
		// TODO change background to gray
		viewer.getDocument().set(Util.EMPTY_STRING);
	}

}
