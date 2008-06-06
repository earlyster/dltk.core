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
package org.eclipse.dltk.ui.templates;

public class TabExpandScriptTemplateIndenter implements IScriptTemplateIndenter {

	private final int tabSize;

	public TabExpandScriptTemplateIndenter(int tabSize) {
		this.tabSize = tabSize;
	}

	public void indentLine(StringBuffer sb, String indent, String line) {
		sb.append(indent);
		int i = 0;
		while (i < line.length() && line.charAt(i) == '\t') {
			++i;
		}
		if (i > 0) {
			int spaceCount = i * tabSize;
			while (spaceCount > 0) {
				sb.append(' ');
				--spaceCount;
			}
		}
		if (i < line.length()) {
			sb.append(line.substring(i));
		}
	}

}
