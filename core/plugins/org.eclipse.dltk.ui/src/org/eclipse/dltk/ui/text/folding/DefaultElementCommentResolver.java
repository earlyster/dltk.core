package org.eclipse.dltk.ui.text.folding;

import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.IModelElementVisitor;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.ISourceRange;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.internal.core.SourceRefElement;
import org.eclipse.dltk.internal.core.SourceType;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;

public class DefaultElementCommentResolver implements IElementCommentResolver {

	/**
	 * Determines the element that contains the clicked comment
	 * 
	 * @throws ModelException
	 */
	protected IModelElement getContainingElement(IModelElement el, int offset,
			int length) throws ModelException {
		final PositionVisitor visitor = new PositionVisitor(offset, length);
		el.accept(visitor);
		return visitor.result;
	}

	/**
	 * Returns the model element that the comment corresponds to
	 */
	public IModelElement getElementByCommentPosition(ISourceModule content,
			int offset, int length) {
		try {
			return getElementByCommentPositionImpl(content, offset, length);
		} catch (BadLocationException e1) {
			return null;
		} catch (ModelException e) {
			return null;
		}
	}

	protected IModelElement getElementByCommentPositionImpl(
			ISourceModule content, int offset, int length)
			throws BadLocationException, ModelException {

		Document d = new Document(content.getSource());

		// Determine that the desired position is inside a comment
		if (!checkIfPositionIsComment(d, offset))
			return null;

		// Determine the innermost element that contains the clicked comment
		// (for example, class declaration)
		IModelElement el = getContainingElement(content, offset, length);

		// If the comment is inside a method, we do not need to process further
		if (el != null && el.getElementType() == IModelElement.METHOD)
			return el;

		// Determine the position after which the search will be stopped - for
		// example, EOF or end of the class declaration
		int sourceRangeEnd = getSourceRangeEnd(d, el);

		// Search for first non-comment element after the clicked comment
		IModelElement res = searchForNonCommentElement(d, content, offset
				+ length, sourceRangeEnd);
		if (res == null)
			return el;
		return res;
	}

	protected int getSourceRangeEnd(Document d, IModelElement el)
			throws ModelException {

		int sourceRangeEnd = d.getLength();

		// If the comment is inside a class, we need to stop searching the
		// element once we leave the class boundaries
		if (el != null && el.getElementType() == IModelElement.TYPE) {
			SourceType t = (SourceType) el;
			sourceRangeEnd = t.getSourceRange().getOffset()
					+ t.getSourceRange().getLength();

		}
		return sourceRangeEnd;
	}

	protected boolean checkIfPositionIsComment(Document d, int offset)
			throws BadLocationException {
		int line = d.getLineOfOffset(offset);
		int q = d.getLineOffset(line);

		while (q < d.getLength() && Character.isWhitespace(d.getChar(q))
				&& q <= offset) {
			q++;
		}

		if (d.getChar(q) != '#') {
			/* First non-space char is not a comment start, so stop processing */
			return false;
		}

		return true;
	}

	protected IModelElement searchForNonCommentElement(Document d,
			ISourceModule content, int endOfCommentOffset, int lowerbound)
			throws BadLocationException, ModelException {
		IModelElement res = null;
		int off = endOfCommentOffset;
		int line = d.getLineOfOffset(off);
		off = d.getLineOffset(line);
		while (off < lowerbound) {

			while (off < lowerbound - 1
					&& Character.isWhitespace(d.getChar(off))) {
				off++;
			}

			if (d.getChar(off) != '#') {
				// It's neither a comment nor whitespace, so we can get the
				// model element at this position
				res = content.getElementAt(off);
				break;
			}
			line++;
			off = d.getLineOffset(line);

		}

		return res;
	}

	/**
	 * Visitor to search the AST for elements that contain the clicked comment
	 */
	private static class PositionVisitor implements IModelElementVisitor {

		IModelElement result = null;
		private final int offset;
		private final int length;

		public PositionVisitor(int offset, int length) {
			this.offset = offset;
			this.length = length;
		}

		public boolean visit(IModelElement el) {
			if (el instanceof SourceRefElement) {
				SourceRefElement element = (SourceRefElement) el;

				// The comment is entirely inside the element
				ISourceRange range;
				try {
					range = element.getSourceRange();
				} catch (ModelException e) {
					return true;
				}
				if (offset >= range.getOffset()
						&& offset + length <= range.getOffset()
								+ range.getLength()) {
					if (element.getElementType() == IModelElement.METHOD
							|| element.getElementType() == IModelElement.TYPE)
						result = element;
				}
			}

			return true;
		}

	}

}
