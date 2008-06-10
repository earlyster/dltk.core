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

	private IToken token;
	private char[][] todoTags;
	private boolean caseSensitive;
	private boolean[] candidates;

	public TodoTagRule(IToken t, String[] tags, boolean caseSensitivity) {
		token = t;
		todoTags = new char[tags.length][];
		for (int i = 0; i < todoTags.length; i++)
			todoTags[i] = tags[i].toCharArray();
		candidates = new boolean[todoTags.length];
		caseSensitive = caseSensitivity;
	}

	public IToken evaluate(ICharacterScanner scanner, boolean resume) {
		return evaluate(scanner);
	}

	public IToken getSuccessToken() {
		return token;
	}

	public IToken evaluate(ICharacterScanner scanner) {
		Arrays.fill(candidates, true);
		int count = 0;

		int c;
		while ((c = scanner.read()) != ICharacterScanner.EOF) {
			boolean allok = false;
			for (int i = 0; i < todoTags.length; i++) {
				if (candidates[i]) {
					allok = true;
					if (count == todoTags[i].length - 1) {
						c = scanner.read();
						if (Character.isJavaIdentifierPart((char) c)) {
							allok = false;
							break;
						}
						scanner.unread();
						return getSuccessToken();
					}
					boolean equals = caseSensitive ? todoTags[i][count] == c
							: Character.toLowerCase(todoTags[i][count]) == Character
									.toLowerCase((char) c);
					if (!equals) {
						candidates[i] = false;
					}
				}
			}
			if (!allok)
				break;
			count++;
		}
		unreadScanner(scanner, count);
		return Token.UNDEFINED;
	}

	private void unreadScanner(ICharacterScanner scanner, int num) {
		for (int i = 0; i < num; i++)
			scanner.unread();
	}
}
