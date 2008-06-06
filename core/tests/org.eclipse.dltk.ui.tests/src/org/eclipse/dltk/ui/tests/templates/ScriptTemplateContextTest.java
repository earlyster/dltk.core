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
package org.eclipse.dltk.ui.tests.templates;

import org.eclipse.dltk.core.tests.model.SuiteOfTestCases;

public class ScriptTemplateContextTest extends SuiteOfTestCases {

	public ScriptTemplateContextTest(String name) {
		super(name);
	}

	public static Suite suite() {
		return new Suite(ScriptTemplateContextTest.class);
	}

	public void testCalulateIndent() {
		assertEquals("", TestTemplateContext.calculateIndent(""));
		assertEquals("", TestTemplateContext.calculateIndent("if"));
		assertEquals("\t", TestTemplateContext.calculateIndent("\t" + "if"));
		assertEquals("\t\t", TestTemplateContext.calculateIndent("\t\t" + "if"));
		assertEquals("  ", TestTemplateContext.calculateIndent("  " + "if"));
		assertEquals("\t  ", TestTemplateContext.calculateIndent("\t  " + "if"));
	}

}
