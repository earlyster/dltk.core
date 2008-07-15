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
package org.eclipse.dltk.validators.internal.externalchecker.core;

class ExternalCheckerCodeModel {
	private String[] codeLines;

	private int[] codeLineLengths;

	public ExternalCheckerCodeModel(String code) {
		this.codeLines = code.split("\n"); //$NON-NLS-1$
		int count = this.codeLines.length;

		this.codeLineLengths = new int[count];

		int sum = 0;
		for (int i = 0; i < count; ++i) {
			this.codeLineLengths[i] = sum;
			sum += this.codeLines[i].length() + 1;
		}
	}

	public int[] getBounds(int lineNumber) {
		if (codeLines.length <= lineNumber) {
			return new int[] { 0, 1 };
		}
		String codeLine = codeLines[lineNumber];
		String trimmedCodeLine = codeLine.trim();

		int start = codeLineLengths[lineNumber]
				+ codeLine.indexOf(trimmedCodeLine);
		int end = start + trimmedCodeLine.length();

		return new int[] { start, end };
	}
}
