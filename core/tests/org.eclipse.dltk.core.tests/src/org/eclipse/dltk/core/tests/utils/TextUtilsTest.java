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
package org.eclipse.dltk.core.tests.utils;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.dltk.utils.TextUtils;

public class TextUtilsTest extends TestCase {

	public static Test suite() {
		return new TestSuite(TextUtilsTest.class);
	}

	public TextUtilsTest(String name) {
		super(name);
	}

	public void testNull() {
		assertNull(TextUtils.splitLines(null));
	}

	public void testEmpty() {
		assertEquals(0, TextUtils.splitLines("").length);
	}

	public void testSingleLine() {
		String[] lines = TextUtils.splitLines("1");
		assertEquals(1, lines.length);
		assertEquals("1", lines[0]);
	}

	public void testMultipleLines() {
		String[] lines = TextUtils.splitLines("1\n" + "2\n" + "3");
		assertEquals(3, lines.length);
		assertEquals("1", lines[0]);
		assertEquals("2", lines[1]);
		assertEquals("3", lines[2]);
		lines = TextUtils.splitLines("1\n" + "2\n" + "3\n");
		assertEquals(3, lines.length);
		assertEquals("1", lines[0]);
		assertEquals("2", lines[1]);
		assertEquals("3", lines[2]);
	}

	public void testWindowsEOL() {
		String[] lines = TextUtils.splitLines("1\r\n" + "2\r\n" + "3");
		assertEquals(3, lines.length);
		assertEquals("1", lines[0]);
		assertEquals("2", lines[1]);
		assertEquals("3", lines[2]);
		lines = TextUtils.splitLines("1\r\n" + "2\r\n" + "3\r\n");
		assertEquals(3, lines.length);
		assertEquals("1", lines[0]);
		assertEquals("2", lines[1]);
		assertEquals("3", lines[2]);
	}

	public void testMacEOL() {
		String[] lines = TextUtils.splitLines("1\r" + "2\r" + "3");
		assertEquals(3, lines.length);
		assertEquals("1", lines[0]);
		assertEquals("2", lines[1]);
		assertEquals("3", lines[2]);
		lines = TextUtils.splitLines("1\r" + "2\r" + "3\r");
		assertEquals(3, lines.length);
		assertEquals("1", lines[0]);
		assertEquals("2", lines[1]);
		assertEquals("3", lines[2]);
	}

	public void testSelectHeadLines1() {
		assertEquals("123", TextUtils.selectHeadLines("123", 1));
		assertEquals("123\n", TextUtils.selectHeadLines("123\n", 1));
		assertEquals("123\n", TextUtils.selectHeadLines("123\n456", 1));
		assertEquals("123\n", TextUtils.selectHeadLines("123\n456\n", 1));
	}

	public void testSelectHeadLines2() {
		assertEquals("123\n456\n", TextUtils
				.selectHeadLines("123\n456\n789", 2));
		assertEquals("123\n456\n789", TextUtils.selectHeadLines(
				"123\n456\n789", 10));
	}

	public void testSplit1() {
		final String[] parts = TextUtils.split("123456", ':');
		assertEquals(1, parts.length);
		assertEquals("123456", parts[0]);
	}

	public void testSplit2() {
		final String[] parts = TextUtils.split("123:456", ':');
		assertEquals(2, parts.length);
		assertEquals("123", parts[0]);
		assertEquals("456", parts[1]);
	}

	public void testSplitAtBounds() {
		final String[] parts = TextUtils.split(":123456:", ':');
		assertEquals(1, parts.length);
		assertEquals("123456", parts[0]);
	}

	public void testSplitWords() {
		String[] words = TextUtils.splitWords("A B C");
		assertEquals(3, words.length);
		assertEquals("A", words[0]);
		assertEquals("B", words[1]);
		assertEquals("C", words[2]);
	}

}
