package org.eclipse.dltk.ui.text.folding;

import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.IModelElementVisitor;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.ISourceRange;
import org.eclipse.dltk.core.ISourceReference;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.internal.core.SourceType;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;

public class DefaultElementCommentResolver implements IElementCommentResolver {

	private final ISourceModule fModule;
	private final String fContent;
	private Document fDocument = null;

	public DefaultElementCommentResolver(ISourceModule module) {
		this(module, null);
	}

	/**
	 * @param modelElement
	 * @param contents
	 */
	public DefaultElementCommentResolver(ISourceModule module, String contents) {
		this.fModule = module;
		this.fContent = contents;
	}

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
	public IModelElement getElementByCommentPosition(int offset, int length) {
		try {
			return getElementByCommentPositionImpl(offset, length);
		} catch (BadLocationException e1) {
			return null;
		} catch (ModelException e) {
			return null;
		}
	}

	protected IModelElement getElementByCommentPositionImpl(int offset,
			int length) throws BadLocationException, ModelException {

		if (fDocument == null) {
			fDocument = new Document(getSource());
		}

		// Determine that the desired position is inside a comment
		if (!checkIfPositionIsComment(fDocument, offset))
			return null;

		// Determine the innermost element that contains the clicked comment
		// (for example, class declaration)
		IModelElement el = getContainingElement(fModule, offset, length);

		// If the comment is inside a method, we do not need to process further
		if (el != null && el.getElementType() == IModelElement.METHOD)
			return el;

		// Determine the position after which the search will be stopped - for
		// example, EOF or end of the class declaration
		int sourceRangeEnd = getSourceRangeEnd(fDocument, el);

		// Search for first non-comment element after the clicked comment
		IModelElement res = searchForNonCommentElement(fDocument, fModule,
				offset + length, sourceRangeEnd);
		if (res == null)
			return el;
		return res;
	}

	/**
	 * @return
	 * @throws ModelException
	 */
	protected String getSource() throws ModelException {
		return fContent != null ? fContent : fModule.getSource();
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

		public boolean visit(IModelElement element) {
			if (element instanceof ISourceReference) {
				final ISourceRange range;
				try {
					range = ((ISourceReference) element).getSourceRange();
				} catch (ModelException e) {
					return true;
				}
				// The comment is entirely inside the element
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
