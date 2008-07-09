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

import java.util.Arrays;

import junit.framework.TestCase;

import org.eclipse.dltk.internal.core.util.Util;

public class InternalCoreUtilTest extends TestCase {

	public void testProblemArguments1() {
		final String[] input = new String[] { "ABC" };
		final String encoded = Util.getProblemArgumentsForMarker(input);
		final String[] output = Util.getProblemArgumentsFromMarker(encoded);
		assertTrue(Arrays.equals(input, output));
	}

	public void testProblemArguments2() {
		final String[] input = new String[] { "AAA", "BBB" };
		final String encoded = Util.getProblemArgumentsForMarker(input);
		final String[] output = Util.getProblemArgumentsFromMarker(encoded);
		assertTrue(Arrays.equals(input, output));
	}

	public void testProblemArgumentsComplex1() {
		final String[] input = new String[] { "A#1", "B#2" };
		final String encoded = Util.getProblemArgumentsForMarker(input);
		final String[] output = Util.getProblemArgumentsFromMarker(encoded);
		assertTrue(Arrays.equals(input, output));
	}

	public void testProblemArgumentsComplex2() {
		final String[] input = new String[] { "A#1", "B#2", "C+3" };
		final String encoded = Util.getProblemArgumentsForMarker(input);
		final String[] output = Util.getProblemArgumentsFromMarker(encoded);
		assertTrue(Arrays.equals(input, output));
	}

	public void testProblemArgumentsOldFormat() {
		String[] result = Util.getProblemArgumentsFromMarker("1:ABC");
		assertEquals(1, result.length);
		assertEquals(3, result[0].length());
		assertEquals("ABC", result[0]);
		assertNull(Util.getProblemArgumentsFromMarker("1:ABC#DEF"));
		assertNull(Util.getProblemArgumentsFromMarker("2:ABC"));
	}

	public void testProblemArgumentsNewFormat() {
		String[] result = Util.getProblemArgumentsFromMarker("+1#3#ABC");
		assertEquals(1, result.length);
		assertEquals(3, result[0].length());
		assertEquals("ABC", result[0]);
		assertNull(Util.getProblemArgumentsFromMarker("+1#3#ABC" + "?"));
		assertNull(Util.getProblemArgumentsFromMarker("+1#4#ABC"));
		assertNull(Util.getProblemArgumentsFromMarker("+2#3#ABC"));
	}

}
