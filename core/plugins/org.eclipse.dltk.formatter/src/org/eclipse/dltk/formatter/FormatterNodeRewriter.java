/*******************************************************************************
 * Copyright (c) 2009 xored software, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     xored software, Inc. - initial API and Implementation (Alex Panchenko)
 *******************************************************************************/
package org.eclipse.dltk.formatter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class FormatterNodeRewriter {

	protected void mergeTextNodes(IFormatterContainerNode root) {
		final List body = root.getBody();
		final List newBody = new ArrayList();
		final List texts = new ArrayList();
		for (Iterator i = body.iterator(); i.hasNext();) {
			final IFormatterNode node = (IFormatterNode) i.next();
			if (isPlainTextNode(node)) {
				if (!texts.isEmpty()
						&& ((IFormatterTextNode) texts.get(texts.size() - 1))
								.getEndOffset() != node.getStartOffset()) {
					flushTextNodes(texts, newBody);
				}
				texts.add(node);
			} else {
				if (!texts.isEmpty()) {
					flushTextNodes(texts, newBody);
				}
				newBody.add(node);
			}
		}
		if (!texts.isEmpty()) {
			flushTextNodes(texts, newBody);
		}
		if (body.size() != newBody.size()) {
			body.clear();
			body.addAll(newBody);
		}
		for (Iterator i = body.iterator(); i.hasNext();) {
			final IFormatterNode node = (IFormatterNode) i.next();
			if (node instanceof IFormatterContainerNode) {
				mergeTextNodes((IFormatterContainerNode) node);
			}
		}
	}

	private void flushTextNodes(List texts, List newBody) {
		if (texts.size() > 1) {
			final IFormatterNode first = (IFormatterNode) texts.get(0);
			final IFormatterNode last = (IFormatterNode) texts
					.get(texts.size() - 1);
			newBody.add(new FormatterTextNode(first.getDocument(), first
					.getStartOffset(), last.getEndOffset()));
		} else {
			newBody.addAll(texts);
		}
		texts.clear();
	}

	protected boolean isPlainTextNode(final IFormatterNode node) {
		return node.getClass() == FormatterTextNode.class;
	}

}
