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
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.Preferences;
import org.eclipse.dltk.compiler.task.TodoTask;
import org.eclipse.dltk.compiler.task.TodoTaskPreferences;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.IWhitespaceDetector;
import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WhitespaceRule;
import org.eclipse.jface.text.rules.WordRule;

public class ScriptCommentScanner extends AbstractScriptScanner {

	private final String[] fProperty;
	protected String[] todoTags = null;
	private Preferences pluginPrefs;

	public ScriptCommentScanner(IColorManager manager, IPreferenceStore store,
			String comment, String todoTag, Preferences pluginPreferences) {
		super(manager, store);
		fProperty = new String[] { comment, todoTag };
		pluginPrefs = pluginPreferences;
		initialize();
	}

	public ScriptCommentScanner(IColorManager manager, IPreferenceStore store,
			String comment, String todoTag, String[] tags) {
		super(manager, store);
		fProperty = new String[] { comment, todoTag };
		todoTags = tags;
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
		rules.add(createTodoRule(getToken(fProperty[1])));

		return rules;
	}

	protected IRule createTodoRule(IToken todoToken) {
		final TodoTaskPreferences preferences = new TodoTaskPreferences(
				pluginPrefs);
		if (todoTags == null) {
			List l = preferences.getTaskTags();
			todoTags = new String[l.size()];
			int i = 0;
			for (Iterator it = l.iterator(); it.hasNext();) {
				todoTags[i] = ((TodoTask) it.next()).name;
				i++;
			}
		}

		boolean caseSensitivity = preferences.isCaseSensitive();
		return new TodoTagRule(todoToken, todoTags, caseSensitivity);
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
