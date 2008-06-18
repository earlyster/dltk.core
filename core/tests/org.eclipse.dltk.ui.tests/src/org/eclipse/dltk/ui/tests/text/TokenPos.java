/*******************************************************************************
 * Copyright (c) 2008 xored software, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     xored software, Inc. - initial API and Implementation (Sam Faktorovich)
 *******************************************************************************/
package org.eclipse.dltk.ui.tests.text;

class TokenPos {
	private final int begin;
	private final int length;

	public TokenPos(int start, int len) {
		begin = start;
		length = len;
	}

	public boolean equals(Object arg0) {
		if (arg0 instanceof TokenPos) {
			TokenPos other = (TokenPos) arg0;
			return other.begin == begin && other.length == length;
		}
		return false;
	}

	public String toString() {
		return "TokenPos[" + begin + "+" + length + "]";
	}
}
