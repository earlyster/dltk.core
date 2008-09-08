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
package org.eclipse.dltk.compiler.env;

import org.eclipse.core.runtime.Assert;

public class CompilerSourceCode extends AbstractSourceCode {

	private String string;
	private char[] charArray;

	public CompilerSourceCode(String content) {
		Assert.isNotNull(content);
		this.string = content;
	}

	public CompilerSourceCode(char[] content) {
		Assert.isNotNull(content);
		this.charArray = content;
	}

	/*
	 * @see org.eclipse.dltk.compiler.env.ISourceModule#getContentsAsCharArray()
	 */
	public char[] getContentsAsCharArray() {
		if (charArray == null) {
			charArray = string.toCharArray();
		}
		return charArray;
	}

	/*
	 * @see org.eclipse.dltk.compiler.env.ISourceModule#getSourceContents()
	 */
	public String getSourceContents() {
		if (string == null) {
			string = new String(charArray);
		}
		return string;
	}

}
