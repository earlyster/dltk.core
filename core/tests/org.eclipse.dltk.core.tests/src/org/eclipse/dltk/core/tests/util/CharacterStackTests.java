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
package org.eclipse.dltk.core.tests.util;

import java.util.EmptyStackException;

import org.eclipse.dltk.utils.CharacterStack;

import junit.framework.TestCase;

public class CharacterStackTests extends TestCase {

	private CharacterStack stack;

	protected void setUp() throws Exception {
		stack = new CharacterStack();
	}

	public void testPush() {
		stack.push('A');
		assertEquals(1, stack.size());
	}

	public void testPop() {
		stack.push('A');
		stack.push('B');
		assertEquals('B', stack.pop());
		assertEquals('A', stack.pop());
	}

	public void testPeek() {
		stack.push('A');
		assertEquals('A', stack.peek());
		stack.push('B');
		assertEquals('B', stack.peek());
	}

	public void testEmptyPop() {
		try {
			stack.pop();
			fail("should throw EmptyStackException");
		} catch (EmptyStackException e) {
			// ignore
		}
	}

	public void testEmptyPeek() {
		try {
			stack.peek();
			fail("should throw EmptyStackException");
		} catch (EmptyStackException e) {
			// ignore
		}
	}

}
