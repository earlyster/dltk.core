/*******************************************************************************
 * Copyright (c) 2005, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
/**
 * 
 */
package org.eclipse.dltk.internal.testing.ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import org.eclipse.dltk.testing.ITestRunnerUI;

public class TextualTrace {
	public static final int LINE_TYPE_EXCEPTION = 1;

	public static final int LINE_TYPE_NORMAL = 0;

	public static final int LINE_TYPE_STACKFRAME = 2;

	private final String fTrace;
	private final ITestRunnerUI engineUI;

	public TextualTrace(String trace, ITestRunnerUI engineUI) {
		this.engineUI = engineUI;
		this.fTrace = engineUI.filterStackTrace(trace);
	}

	public void display(ITraceDisplay display, int maxLabelLength) {
		StringReader stringReader = new StringReader(fTrace);
		BufferedReader bufferedReader = new BufferedReader(stringReader);
		String line;

		try {
			// first line contains the thrown exception
			line = readLine(bufferedReader);
			if (line == null)
				return;

			displayWrappedLine(display, maxLabelLength, line,
					LINE_TYPE_EXCEPTION);

			// the stack frames of the trace
			while ((line = readLine(bufferedReader)) != null) {
				int type = engineUI.isStackFrame(line) ? LINE_TYPE_STACKFRAME
						: LINE_TYPE_NORMAL;
				displayWrappedLine(display, maxLabelLength, line, type);
			}
		} catch (IOException e) {
			display.addTraceLine(LINE_TYPE_NORMAL, fTrace);
		}
	}

	private void displayWrappedLine(ITraceDisplay display, int maxLabelLength,
			String line, int type) {
		final int labelLength = line.length();
		if (labelLength < maxLabelLength) {
			display.addTraceLine(type, line);
		} else {
			// workaround for bug 74647: JUnit view truncates
			// failure message
			display.addTraceLine(type, line.substring(0, maxLabelLength));
			int offset = maxLabelLength;
			while (offset < labelLength) {
				int nextOffset = Math.min(labelLength, offset + maxLabelLength);
				display.addTraceLine(LINE_TYPE_NORMAL, line.substring(offset,
						nextOffset));
				offset = nextOffset;
			}
		}
	}

	private String readLine(BufferedReader bufferedReader) throws IOException {
		String readLine = bufferedReader.readLine();
		return readLine == null ? null : readLine.replace('\t', ' ');
	}
}
