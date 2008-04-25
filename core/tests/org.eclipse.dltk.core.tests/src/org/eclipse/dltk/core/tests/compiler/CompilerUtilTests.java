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
package org.eclipse.dltk.core.tests.compiler;

import org.eclipse.dltk.compiler.util.Util;

import junit.framework.TestCase;

public class CompilerUtilTests extends TestCase {

	public void testIsArchiveName() {
		assertFalse(Util.isArchiveFileName(null));
		assertFalse(Util.isArchiveFileName(""));
		assertFalse(Util.isArchiveFileName(".txt"));
		assertTrue(Util.isArchiveFileName("a.zip"));
		assertTrue(Util.isArchiveFileName("a.ZIP"));
		assertTrue(Util.isArchiveFileName("a.Zip"));
	}

}
