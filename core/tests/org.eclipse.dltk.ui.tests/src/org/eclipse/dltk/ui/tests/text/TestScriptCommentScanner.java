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

import org.eclipse.dltk.internal.ui.text.DLTKColorManager;
import org.eclipse.dltk.ui.DLTKUIPlugin;
import org.eclipse.dltk.ui.text.ScriptCommentScanner;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.Token;

class TestScriptCommentScanner extends ScriptCommentScanner {

	public TestScriptCommentScanner(String[] tags, String commentKey,
			String todoKey, boolean caseSensitive) {
		super(new DLTKColorManager(true), DLTKUIPlugin.getDefault()
				.getPreferenceStore(), commentKey, todoKey,
				new TestTodoTaskPreferences(tags, caseSensitive));
	}

	public void setText(String text) {
		setRange(new Document(text), 0, text.length());
	}

	public String getText() {
		return fDocument.get();
	}

	/*
	 * increase visibility
	 */
	public Token getToken(String key) {
		return super.getToken(key);
	}

	/*
	 * increase visibility
	 */
	public IRule createTodoRule() {
		return super.createTodoRule();
	}
}
