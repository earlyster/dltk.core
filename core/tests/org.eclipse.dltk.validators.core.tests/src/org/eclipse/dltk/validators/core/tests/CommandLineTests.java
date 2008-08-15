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
package org.eclipse.dltk.validators.core.tests;

import org.eclipse.dltk.validators.core.CommandLine;

import junit.framework.TestCase;

public class CommandLineTests extends TestCase {

	public void testConstructor() {
		assertEquals("", new CommandLine().toString());
		CommandLine commandLine = new CommandLine("A B");
		assertEquals("A B", commandLine.toString());
	}

	public void testAdd() {
		CommandLine commandLine = new CommandLine();
		commandLine.add("A");
		commandLine.add("B");
		assertEquals("A B", commandLine.toString());
	}

	public void testAddArray() {
		CommandLine commandLine = new CommandLine();
		commandLine.add(new String[] { "A", "B" });
		assertEquals("A B", commandLine.toString());
	}

	public void testToArray() {
		CommandLine commandLine = new CommandLine();
		commandLine.add(new String[] { "A", "B" });
		String[] args = commandLine.toArray();
		assertEquals(2, args.length);
		assertEquals("A", args[0]);
		assertEquals("B", args[1]);
	}

	public void testContains() {
		CommandLine commandLine = new CommandLine();
		commandLine.add(new String[] { "A", "B" });
		assertTrue(commandLine.contains("A"));
		assertFalse(commandLine.contains("AA"));
	}

	public void testAddCommandLine() {
		CommandLine commandLine = new CommandLine("A B");
		commandLine.add(new CommandLine("C D"));
		assertEquals("A B C D", commandLine.toString());
	}

	public void testClear() {
		CommandLine commandLine = new CommandLine();
		commandLine.add("A");
		commandLine.add("B");
		assertEquals("A B", commandLine.toString());
		commandLine.clear();
		assertEquals("", commandLine.toString());
	}

	public void testReplace() {
		CommandLine commandLine = new CommandLine("A B %c");
		commandLine.replaceSequence('x', "XX");
		assertEquals("A B %c", commandLine.toString());
		commandLine.replaceSequence('c', "CC");
		assertEquals("A B CC", commandLine.toString());
	}
}
