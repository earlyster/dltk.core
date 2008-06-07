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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.dltk.core.tests.model.SuiteOfTestCases;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.rules.IToken;

public class TodoHighlightingTest extends SuiteOfTestCases {

	private static final String COMMENT_KEY = "SingleLineCommentIdentifier";
	private static final String TODO_KEY = "TodoTagIdentifier";

	public static Suite suite() {
		return new Suite(TodoHighlightingTest.class);
	}

	public TodoHighlightingTest(String name) {
		super(name);
	}

	protected List findTodoTokens(String data, String[] tags) {
		TestScriptCommentScanner scanner = new TestScriptCommentScanner(tags,
				COMMENT_KEY, TODO_KEY);
		scanner.setRange(new Document(data), 0, data.length());
		final IToken todoToken = scanner.getToken(TODO_KEY);
		List result = new ArrayList();
		IToken t;
		while (!(t = scanner.nextToken()).isEOF()) {
			if (t.equals(todoToken)) {
				result.add(new TokenPos(scanner.getTokenOffset(), scanner
						.getTokenLength()));
			}
		}
		return result;
	}

	/**
	 * @param data
	 * @param tag
	 * @return
	 */
	protected List findTodoTokens(String data, String tag) {
		return findTodoTokens(data, new String[] { tag });
	}

	private TokenPos newTokenPos(final String data, final String tag) {
		return new TokenPos(data.indexOf(tag), tag.length());
	}

	public void testBare() throws Exception {
		if (notYetImplemented())
			return;
		final String data = "#TODO";
		final String tag = "TODO";
		List l = findTodoTokens(data, tag);
		assertEquals(l, Collections.singletonList(newTokenPos(data, tag)));
	}

	public void testNormal() throws Exception {
		final String data = "#TODO comment";
		final String tag = "TODO";
		List l = findTodoTokens(data, tag);
		assertEquals(l, Collections.singletonList(newTokenPos(data, tag)));
	}

	public void testSpaced() throws Exception {
		final String data = "#   TODO comment";
		final String tag = "TODO";
		List l = findTodoTokens(data, tag);
		assertEquals(l, Collections.singletonList(newTokenPos(data, tag)));
	}

	public void testPrefixed() throws Exception {
		final String data = "# aTODO comment";
		final String tag = "TODO";
		List l = findTodoTokens(data, tag);
		assertEquals(l, Collections.EMPTY_LIST);
	}

	public void testSuffixed() throws Exception {
		final String data = "# TODOa comment";
		final String tag = "TODO";
		List l = findTodoTokens(data, tag);
		assertEquals(l, Collections.EMPTY_LIST);
	}

	public void testLegallySuffixed() throws Exception {
		final String data = "# TODO: comment";
		final String tag = "TODO";
		List l = findTodoTokens(data, tag);
		assertEquals(l, Collections.singletonList(newTokenPos(data, tag)));
	}

	public void testDoubleTagOccurence() throws Exception {
		String data = "# TODO add TODO tag support";
		final String tag = "TODO";
		List l = findTodoTokens(data, tag);
		assertEquals(l, Collections.singletonList(newTokenPos(data, tag)));
	}

	public void testDifferentTags() throws Exception {
		String data = "#FIXME tag support";
		final String[] tags = { "TODO", "FIXME" };
		List l = findTodoTokens(data, tags);
		assertEquals(l, Collections.singletonList(new TokenPos(data
				.indexOf(tags[1]), tags[1].length())));
	}

}
