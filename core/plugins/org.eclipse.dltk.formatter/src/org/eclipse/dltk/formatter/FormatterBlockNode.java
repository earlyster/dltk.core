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
package org.eclipse.dltk.formatter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class FormatterBlockNode extends AbstractFormatterNode implements
		IFormatterContainerNode {

	/**
	 * @param document
	 */
	public FormatterBlockNode(IFormatterDocument document) {
		super(document);
	}

	private final List body = new ArrayList();

	protected void acceptNodes(final List nodes, IFormatterContext context,
			IFormatterWriter visitor) throws Exception {
		for (Iterator i = nodes.iterator(); i.hasNext();) {
			IFormatterNode node = (IFormatterNode) i.next();
			context.enter(node);
			node.accept(context, visitor);
			context.leave(node);
		}
	}

	public void addChild(IFormatterNode child) {
		body.add(child);
	}

	public void accept(IFormatterContext context, IFormatterWriter visitor)
			throws Exception {
		acceptBody(context, visitor);
	}

	protected void acceptBody(IFormatterContext context,
			IFormatterWriter visitor) throws Exception {
		acceptNodes(body, context, visitor);
	}

	/*
	 * @see org.eclipse.dltk.ruby.formatter.node.IFormatterNode#getEndOffset()
	 */
	public int getEndOffset() {
		if (!body.isEmpty()) {
			return ((IFormatterNode) body.get(body.size() - 1)).getEndOffset();
		} else {
			return DEFAULT_OFFSET;
		}
	}

	/*
	 * @see org.eclipse.dltk.ruby.formatter.node.IFormatterNode#getStartOffset()
	 */
	public int getStartOffset() {
		if (!body.isEmpty()) {
			return ((IFormatterNode) body.get(0)).getStartOffset();
		} else {
			return DEFAULT_OFFSET;
		}
	}

	/*
	 * @see
	 * org.eclipse.dltk.ruby.formatter.node.IFormatterContainerNode#isEmpty()
	 */
	public boolean isEmpty() {
		for (Iterator i = body.iterator(); i.hasNext();) {
			IFormatterNode node = (IFormatterNode) i.next();
			if (!node.isEmpty()) {
				return false;
			}
		}
		return true;
	}

	public List getChildren() {
		return Collections.unmodifiableList(body);
	}

	public String toString() {
		return body.toString();
	}

	protected boolean isIndenting() {
		return true;
	}

	public List getBody() {
		return body;
	}

}
