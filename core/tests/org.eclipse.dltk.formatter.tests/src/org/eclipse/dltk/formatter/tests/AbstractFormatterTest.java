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
package org.eclipse.dltk.formatter.tests;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;
import java.util.Collection;
import java.util.Map;

import junit.framework.Assert;
import junit.framework.ComparisonFailure;
import junit.framework.TestCase;

import org.eclipse.dltk.compiler.util.Util;
import org.eclipse.dltk.ui.formatter.FormatterException;
import org.eclipse.dltk.ui.formatter.IScriptFormatter;
import org.eclipse.dltk.utils.TextUtils;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.text.edits.TextEdit;

@SuppressWarnings("nls")
public abstract class AbstractFormatterTest extends TestCase {

	/**
	 * @param input
	 * @return
	 * @throws FormatterException
	 */
	protected String format(String input) throws FormatterException {
		IScriptFormatter f = createFormatter();
		final TextEdit edit = f.format(input, 0, input.length(), 0);
		Assert.assertNotNull(edit);
		final IDocument document = new Document(input);
		try {
			edit.apply(document);
		} catch (BadLocationException e) {
			throw new RuntimeException(e);
		}
		return document.get();
	}

	/**
	 * Returns default preferences to be passed from {@link #createFormatter()}
	 * to {@link #createFormatter(Map)}
	 * 
	 * @return
	 */
	protected Map<String, Object> getDefaultPreferences() {
		return null;
	}

	/**
	 * Creates formatter with default preferences.
	 * 
	 * @return
	 */
	protected final IScriptFormatter createFormatter() {
		return createFormatter(getDefaultPreferences());
	}

	/**
	 * Creates formatter with the specified preferences.
	 * 
	 * @param preferences
	 *            or <code>null</code> to use default preferences.
	 * @return
	 */
	protected abstract IScriptFormatter createFormatter(
			Map<String, Object> preferences);

	protected static String joinLines(Collection<String> lines) {
		return joinLines(lines.toArray(new String[lines.size()]));
	}

	protected static String joinLines(String... lines) {
		return TextUtils.join(lines, Util.LINE_SEPARATOR) + Util.LINE_SEPARATOR;
	}

	/**
	 * @param lines
	 * @param beginIndex
	 * @param endIndex
	 * @return
	 */
	protected static String joinLines(String[] lines, int beginIndex,
			int endIndex) {
		final StringBuffer sb = new StringBuffer();
		for (int i = beginIndex; i < endIndex; ++i) {
			sb.append(lines[i]);
			sb.append(Util.LINE_SEPARATOR);
		}
		return sb.toString();
	}

	protected boolean compareIgnoreBlanks(String entryName, Reader inputReader,
			Reader outputReader) throws IOException {
		LineNumberReader input = new LineNumberReader(inputReader);
		LineNumberReader output = new LineNumberReader(outputReader);
		for (;;) {
			String inputLine;
			do {
				inputLine = input.readLine();
				if (inputLine != null) {
					inputLine = inputLine.trim();
				}
			} while (inputLine != null && inputLine.length() == 0);
			String outputLine;
			do {
				outputLine = output.readLine();
				if (outputLine != null) {
					outputLine = outputLine.trim();
				}
			} while (outputLine != null && outputLine.length() == 0);
			if (inputLine == null) {
				if (outputLine == null) {
					return true;
				} else {
					fail(entryName + ": Extra output " + output.getLineNumber()
							+ ":" + outputLine);
				}
			} else if (outputLine == null) {
				fail(entryName + ": Missing output " + input.getLineNumber()
						+ ":" + inputLine);
			} else if (!inputLine.equals(outputLine)) {
				throw new ComparisonFailure(entryName + ": Comparison failed",
						input.getLineNumber() + ":" + inputLine,
						output.getLineNumber() + ":" + outputLine);
			}
		}
	}

}
