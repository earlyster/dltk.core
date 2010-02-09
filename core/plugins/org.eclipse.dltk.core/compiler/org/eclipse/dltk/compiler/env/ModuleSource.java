/*******************************************************************************
 * Copyright (c) 2010 xored software, Inc.
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

import org.eclipse.dltk.compiler.util.Util;
import org.eclipse.dltk.core.IModelElement;

public class ModuleSource implements IModuleSource {

	private final String filename;
	private String string;
	private char[] charArray;

	public ModuleSource(char[] content) {
		this(Util.EMPTY_STRING, content);
	}

	public ModuleSource(String content) {
		this(Util.EMPTY_STRING, content);
	}

	public ModuleSource(String filename, char[] content) {
		assert content != null;
		this.filename = filename;
		this.charArray = content;
	}

	public ModuleSource(String filename, String content) {
		assert content != null;
		this.filename = filename;
		this.string = content;
	}

	public char[] getContentsAsCharArray() {
		if (charArray == null) {
			charArray = string.toCharArray();
		}
		return charArray;
	}

	public String getSourceContents() {
		if (string == null) {
			string = new String(charArray);
		}
		return string;
	}

	public String getFileName() {
		return filename;
	}

	public IModelElement getModelElement() {
		return null;
	}

}
