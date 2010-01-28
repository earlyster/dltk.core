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
package org.eclipse.dltk.utils;

import java.util.EmptyStackException;

public class CharacterStack {

	private char[] buffer;
	private int size;

	public CharacterStack() {
		this(16);
	}

	/**
	 * @param capacity
	 */
	public CharacterStack(int capacity) {
		buffer = new char[capacity];
		size = 0;
	}

	/**
	 * @return
	 */
	public int size() {
		return size;
	}

	/**
	 * @return
	 */
	public boolean isEmpty() {
		return size == 0;
	}

	/**
	 * @return
	 */
	public char peek() {
		final int len = size;
		if (size == 0) {
			throw new EmptyStackException();
		}
		return buffer[len - 1];
	}

	/**
	 * @return
	 */
	public char pop() {
		int len = size;
		if (len == 0) {
			throw new EmptyStackException();
		}
		--len;
		final char result = buffer[len];
		size = len;
		return result;
	}

	/**
	 * @param c
	 */
	public void push(char c) {
		if (size == buffer.length) {
			char[] newBuffer = new char[buffer.length * 2 + 1];
			System.arraycopy(buffer, 0, newBuffer, 0, buffer.length);
			buffer = newBuffer;
		}
		buffer[size++] = c;
	}

}
