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

import org.eclipse.dltk.ui.templates.ScriptTemplateContext;
import org.eclipse.jface.text.Document;

class TestTemplateContext extends ScriptTemplateContext {

	private TestTemplateContext() {
		super(null, null, 0, 0, null);
	}

	public static String calculateIndent(String line) {
		return calculateIndent(new Document(line), line.length());
	}

}
