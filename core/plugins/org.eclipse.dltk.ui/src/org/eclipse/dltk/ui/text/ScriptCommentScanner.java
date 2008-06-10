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
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.IWhitespaceDetector;
import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WhitespaceRule;
import org.eclipse.jface.text.rules.WordRule;

public class ScriptCommentScanner extends AbstractScriptScanner {

	private final String[] fProperty;
	private final ITodoTaskPreferences preferences;

	public ScriptCommentScanner(IColorManager manager, IPreferenceStore store,
			String comment, String todoTag, ITodoTaskPreferences preferences) {
		super(manager, store);
		fProperty = new String[] { comment, todoTag };
		this.preferences = preferences;
		initialize();
	}

	protected String[] getTokenProperties() {
		return fProperty;
	}

	protected List createRules() {
		setDefaultReturnToken(getToken(fProperty[0]));

		List rules = new ArrayList();

		// the order of rules is critical
		rules.add(new WhitespaceRule(new WhitespaceDetector()));
		WordRule r = new WordRule(new CommentStartDetector(),
				getToken(fProperty[0]), true);
		r.addWord(COMMENT_STRING, getToken(fProperty[0]));
		rules.add(r);
		rules.add(new TodoTagRule(getToken(fProperty[1]), preferences
				.getTagNames(), preferences.isCaseSensitive()));

		return rules;
	}

	private boolean appeared = false;

	/*
	 * We overload nextToken() because of the way task parsing in implemented:
	 * the TO-DO tasks are recognized only at the beginning of the comment
	 */
	public IToken nextToken() {
		fTokenOffset = fOffset;
		fColumn = UNDEFINED;
		if (fRules != null) {
			for (int i = 0; i < fRules.length; i++) {
				final IToken token = (fRules[i].evaluate(this));
				if (!token.isUndefined()) {
					if (appeared)
						return fDefaultReturnToken;
					else
						return token;
				} else {
					if (i == 2) {
						/*
						 * a TO-DO rule was processed, and found something
						 * before TO-DO tag, so no highlighting
						 */
						appeared = true;
					}
				}
			}
		}
		if (read() == EOF) {
			appeared = false;
			return Token.EOF;
		}
		if (appeared) {
			int k = read();
			while (k != EOF)
				k = read();
			appeared = false;
			return fDefaultReturnToken;
		}
		return fDefaultReturnToken;
	}

	private static final char COMMENT_CHAR = '#';
	private static final String COMMENT_STRING = String.valueOf(COMMENT_CHAR);

	private static class CommentStartDetector implements IWordDetector {

		public boolean isWordPart(char c) {
			return false;
		}

		public boolean isWordStart(char c) {
			return c == COMMENT_CHAR;
		}

	}

	private static class WhitespaceDetector implements IWhitespaceDetector {
		public boolean isWhitespace(char character) {
			return Character.isWhitespace(character);
		}
	}
}
