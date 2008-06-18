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
package org.eclipse.dltk.ui.text;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.dltk.compiler.task.ITodoTaskPreferences;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;

public class ScriptCommentScanner extends AbstractScriptScanner {

	private final String[] fProperties;
	private final ITodoTaskPreferences preferences;

	public ScriptCommentScanner(IColorManager manager, IPreferenceStore store,
			String comment, String todoTag, ITodoTaskPreferences preferences) {
		super(manager, store);
		fProperties = new String[] { comment, todoTag };
		this.preferences = preferences;
		initialize();
	}

	protected String[] getTokenProperties() {
		return fProperties;
	}

	protected List createRules() {
		setDefaultReturnToken(getToken(fProperties[0]));
		List rules = new ArrayList();
		rules.add(createTodoRule());
		return rules;
	}

	protected IRule createTodoRule() {
		return new TodoTagRule(getToken(fProperties[1]), preferences
				.getTagNames(), preferences.isCaseSensitive());
	}

	public void setRange(IDocument document, int offset, int length) {
		super.setRange(document, offset, length);
		state = STATE_START;
	}

	private int state = STATE_START;

	private static final int STATE_START = 0;
	private static final int STATE_STARTED = 1;
	private static final int STATE_BODY = 2;

	/*
	 * We overload nextToken() because of the way task parsing is implemented:
	 * the TO-DO tasks are recognized only at the beginning of the comment
	 */
	public IToken nextToken() {
		fTokenOffset = fOffset;
		fColumn = UNDEFINED;
		if (state == STATE_START) {
			state = STATE_STARTED;
			int count = 0;
			int c = read();
			if (c == COMMENT_CHAR) {
				c = read();
				++count;
			}
			while (c != EOF && Character.isWhitespace((char) c)) {
				c = read();
				++count;
			}
			unread();
			if (count > 0) {
				return fDefaultReturnToken;
			} else if (c == EOF) {
				return Token.EOF;
			}
		}
		if (state == STATE_STARTED) {
			state = STATE_BODY;
			final IToken token = fRules[0].evaluate(this);
			if (!token.isUndefined()) {
				return token;
			}
		}
		int count = 0;
		while (read() != EOF) {
			++count;
		}
		return count > 0 ? fDefaultReturnToken : Token.EOF;
	}

	private static final char COMMENT_CHAR = '#';
}
