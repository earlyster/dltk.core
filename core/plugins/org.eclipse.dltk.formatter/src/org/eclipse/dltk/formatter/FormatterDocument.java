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
package org.eclipse.dltk.formatter;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.text.IRegion;

public class FormatterDocument implements IFormatterDocument {

	private final String text;
	private final Map booleans = new HashMap();
	private final Map ints = new HashMap();

	/**
	 * @param text
	 */
	public FormatterDocument(String text) {
		this.text = text;
	}

	/*
	 * @see org.eclipse.dltk.ruby.formatter.node.IFormatterDocument#getText()
	 */
	public String getText() {
		return text;
	}

	/*
	 * @see org.eclipse.dltk.ruby.formatter.node.IFormatterDocument#getLength()
	 */
	public int getLength() {
		return text.length();
	}

	public String get(int startOffset, int endOffset) {
		return text.substring(startOffset, endOffset);
	}

	public String get(IRegion region) {
		return get(region.getOffset(), region.getOffset() + region.getLength());
	}

	public void setBoolean(String key, boolean value) {
		booleans.put(key, Boolean.valueOf(value));
	}

	public boolean getBoolean(String key) {
		final Boolean value = (Boolean) booleans.get(key);
		return value != null && value.booleanValue();
	}

	public void setInt(String key, int value) {
		ints.put(key, new Integer(value));
	}

	public int getInt(String key) {
		final Integer value = (Integer) ints.get(key);
		return value != null ? value.intValue() : 0;
	}

	public char charAt(int index) {
		return text.charAt(index);
	}

}
