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

public abstract class FormatterNodeRewriter {

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

	private static class CommentInfo {
		final int startOffset;
		final int endOffset;
		final Object object;

		public CommentInfo(int startOffset, int endOffset, Object object) {
			this.startOffset = startOffset;
			this.endOffset = endOffset;
			this.object = object;
		}

	}

	private final List comments = new ArrayList();

	protected void addComment(int startOffset, int endOffset, Object object) {
		comments.add(new CommentInfo(startOffset, endOffset, object));
	}

	protected void insertComments(IFormatterContainerNode root) {
		final List body = root.getBody();
		final List newBody = new ArrayList();
		boolean changes = false;
		for (Iterator i = body.iterator(); i.hasNext();) {
			final IFormatterNode node = (IFormatterNode) i.next();
			if (isPlainTextNode(node)) {
				if (hasComments(node.getStartOffset(), node.getEndOffset())) {
					selectValidRanges(root.getDocument(),
							node.getStartOffset(), node.getEndOffset(), newBody);
					changes = true;
				} else {
					newBody.add(node);
				}
			} else {
				newBody.add(node);
			}
		}
		if (changes) {
			body.clear();
			body.addAll(newBody);
		}
		for (Iterator i = body.iterator(); i.hasNext();) {
			final IFormatterNode node = (IFormatterNode) i.next();
			if (node instanceof IFormatterContainerNode) {
				insertComments((IFormatterContainerNode) node);
			}
		}
	}

	private boolean hasComments(int startOffset, int endOffset) {
		for (Iterator i = comments.iterator(); i.hasNext();) {
			final CommentInfo commentNode = (CommentInfo) i.next();
			if (commentNode.startOffset < endOffset
					&& startOffset < commentNode.endOffset) {
				return true;
			}
		}
		return false;
	}

	private void selectValidRanges(IFormatterDocument document, int start,
			int end, List result) {
		for (Iterator i = comments.iterator(); i.hasNext();) {
			final CommentInfo comment = (CommentInfo) i.next();
			if (start <= comment.endOffset && comment.startOffset <= end) {
				if (start < comment.startOffset) {
					int validEnd = Math.min(end, comment.startOffset);
					result
							.add(new FormatterTextNode(document, start,
									validEnd));
					start = comment.startOffset;
				}
				result.add(createCommentNode(document, start, Math.min(
						comment.endOffset, end), comment.object));
				start = comment.endOffset;
				if (start > end) {
					break;
				}
			}
		}
		if (start < end) {
			result.add(new FormatterTextNode(document, start, end));
		}
	}

	protected abstract IFormatterNode createCommentNode(
			IFormatterDocument document, int startOffset, int endOffset,
			Object object);

}
