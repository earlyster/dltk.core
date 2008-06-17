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

import java.util.Arrays;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;

public class TodoTagRule implements IPredicateRule {

	private final IToken token;
	private final char[][] todoTags;
	private final boolean caseSensitive;
	private final boolean[] candidates;
	private final int maxLength;

	public TodoTagRule(IToken t, String[] tags, boolean caseSensitive) {
		token = t;
		todoTags = new char[tags.length][];
		int maxLen = 0;
		for (int i = 0; i < todoTags.length; i++) {
			final char[] tag = tags[i].toCharArray();
			if (!caseSensitive) {
				for (int j = 0; j < tag.length; ++j) {
					tag[j] = Character.toUpperCase(tag[j]);
				}
			}
			todoTags[i] = tag;
			maxLen = Math.max(tag.length, maxLen);
		}
		candidates = new boolean[todoTags.length];
		this.caseSensitive = caseSensitive;
		this.maxLength = maxLen;
	}

	public IToken evaluate(ICharacterScanner scanner, boolean resume) {
		return evaluate(scanner);
	}

	public IToken getSuccessToken() {
		return token;
	}

	public IToken evaluate(ICharacterScanner scanner) {
		Arrays.fill(candidates, true);
		int candidateCount = todoTags.length;
		int count = 0;
		int c;
		while ((c = scanner.read()) != ICharacterScanner.EOF) {
			for (int i = 0; i < todoTags.length; i++) {
				if (candidates[i]) {
					final char[] tag = todoTags[i];
					if (count < tag.length) {
						final boolean eq = caseSensitive ? c == tag[count]
								: Character.toUpperCase((char) c) == tag[count];
						if (!eq) {
							candidates[i] = false;
							--candidateCount;
							if (candidateCount == 0) {
								unreadScanner(scanner, count + 1);
								return Token.UNDEFINED;
							}
						}
					} else if (count == tag.length) {
						if (!Character.isJavaIdentifierPart((char) c)) {
							scanner.unread();
							return getSuccessToken();
						}
					}
				}
			}
			++count;
			if (count == maxLength) {
				c = scanner.read();
				if (c != ICharacterScanner.EOF
						&& !Character.isJavaIdentifierPart((char) c)) {
					c = ICharacterScanner.EOF;
				}
				scanner.unread();
				break;
			}
		}
		if (c == ICharacterScanner.EOF) {
			for (int i = 0; i < todoTags.length; i++) {
				if (candidates[i] && count == todoTags[i].length) {
					return getSuccessToken();
				}
			}
		}
		unreadScanner(scanner, count);
		return Token.UNDEFINED;
	}

	private void unreadScanner(ICharacterScanner scanner, int num) {
		for (int i = 0; i < num; i++) {
			scanner.unread();
		}
	}
}
