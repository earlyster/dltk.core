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
package org.eclipse.dltk.core.tests.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

@SuppressWarnings("serial")
public class StringList extends ArrayList<String> {

	public StringList() {
		super();
	}

	public StringList(int initialCapacity) {
		super(initialCapacity);
	}

	public StringList(Collection<? extends String> c) {
		super(c);
	}

	public StringList(String... strings) {
		Collections.addAll(this, strings);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (String line : this) {
			sb.append(line).append("\n");
		}
		return sb.toString();
	}

	@Override
	public String[] toArray() {
		return toArray(new String[size()]);
	}

	public StringList sort() {
		StringList copy = new StringList(this);
		Collections.sort(copy);
		return copy;
	}

	/**
	 * @return
	 */
	public int length() {
		int length = 0;
		for (String line : this) {
			length += line.length();
			length += 1; // EOL
		}
		return length;
	}

}
