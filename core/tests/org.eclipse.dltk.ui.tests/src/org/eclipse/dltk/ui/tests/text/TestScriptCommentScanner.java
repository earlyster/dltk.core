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
import org.eclipse.dltk.ui.text.TodoTagRule;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;

class TestScriptCommentScanner extends ScriptCommentScanner {

	private final boolean caseSensitive;

	public TestScriptCommentScanner(String[] tags, String commentKey, String todoKey) {
		this(tags, commentKey, todoKey, true);
	}

	public TestScriptCommentScanner(String[] tags, String commentKey, String todoKey, boolean caseSensitive) {
		super(new DLTKColorManager(true), DLTKUIPlugin.getDefault()
				.getPreferenceStore(), commentKey, todoKey, tags);
		this.caseSensitive = caseSensitive;
	}

	protected IRule createTodoRule(IToken todoToken) {
		return new TodoTagRule(todoToken, todoTags, caseSensitive);
	}

	/*
	 * increase visibility
	 */
	public Token getToken(String key) {
		return super.getToken(key);
	}
}
