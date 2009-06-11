/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.eclipse.dltk.ui.text.util;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.dltk.ui.CodeFormatterConstants;

public enum TabStyle {

	TAB(CodeFormatterConstants.TAB),

	SPACES(CodeFormatterConstants.SPACE),

	MIXED(CodeFormatterConstants.MIXED);

	private final String name;

	private TabStyle(String name) {
		this.name = name;
	}

	public String toString() {
		return name;
	}

	public String getName() {
		return name;
	}

	private static final Map<String, TabStyle> byName = new HashMap<String, TabStyle>();

	static {
		byName.put(TAB.getName(), TAB);
		byName.put(SPACES.getName(), SPACES);
		byName.put(MIXED.getName(), MIXED);
	}

	public static TabStyle forName(String name) {
		return byName.get(name);
	}

	public static TabStyle forName(String name, TabStyle deflt) {
		final TabStyle result = forName(name);
		return result != null ? result : deflt;
	}

}
