package org.eclipse.dltk.ui.editor.highlighting;

import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;

/**
 * Highlighted Positions.
 */
public class HighlightedPosition extends Position {

	/**
	 * Empty array of {@link HighlightedPosition}s
	 */
	public static final HighlightedPosition[] NO_POSITIONS = new HighlightedPosition[0];

	/** Highlighting of the position */
	private HighlightingStyle fStyle;

	/** Lock object */
	private Object fLock;

	/**
	 * Initialize the styled positions with the given offset, length and
	 * foreground color.
	 * 
	 * @param offset
	 *            The position offset
	 * @param length
	 *            The position length
	 * @param highlighting
	 *            The position's highlighting
	 * @param lock
	 *            The lock object
	 */
	public HighlightedPosition(int offset, int length,
			HighlightingStyle highlighting, Object lock) {
		super(offset, length);
		fStyle = highlighting;
		fLock = lock;
	}

	/**
	 * @return Returns a corresponding style range.
	 */
	public StyleRange createStyleRange() {
		int len = 0;
		if (fStyle.isEnabled())
			len = getLength();

		TextAttribute textAttribute = fStyle.getTextAttribute();
		int style = textAttribute.getStyle();
		int fontStyle = style & (SWT.ITALIC | SWT.BOLD | SWT.NORMAL);
		StyleRange styleRange = new StyleRange(getOffset(), len, textAttribute
				.getForeground(), textAttribute.getBackground(), fontStyle);
		styleRange.strikeout = (style & TextAttribute.STRIKETHROUGH) != 0;
		styleRange.underline = (style & TextAttribute.UNDERLINE) != 0;

		return styleRange;
	}

	/**
	 * Uses reference equality for the highlighting.
	 * 
	 * @param off
	 *            The offset
	 * @param len
	 *            The length
	 * @param highlighting
	 *            The highlighting
	 * @return <code>true</code> iff the given offset, length and highlighting
	 *         are equal to the internal ones.
	 */
	public boolean isEqual(int off, int len, HighlightingStyle highlighting) {
		synchronized (fLock) {
			return !isDeleted() && getOffset() == off && getLength() == len
					&& fStyle == highlighting;
		}
	}

	/**
	 * Is this position contained in the given range (inclusive)? Synchronizes
	 * on position updater.
	 * 
	 * @param off
	 *            The range offset
	 * @param len
	 *            The range length
	 * @return <code>true</code> iff this position is not delete and contained
	 *         in the given range.
	 */
	public boolean isContained(int off, int len) {
		synchronized (fLock) {
			return !isDeleted() && off <= getOffset()
					&& off + len >= getOffset() + getLength();
		}
	}

	public void update(int off, int len) {
		synchronized (fLock) {
			super.setOffset(off);
			super.setLength(len);
		}
	}

	/*
	 * @see org.eclipse.jface.text.Position#setLength(int)
	 */
	@Override
	public void setLength(int length) {
		synchronized (fLock) {
			super.setLength(length);
		}
	}

	/*
	 * @see org.eclipse.jface.text.Position#setOffset(int)
	 */
	@Override
	public void setOffset(int offset) {
		synchronized (fLock) {
			super.setOffset(offset);
		}
	}

	/*
	 * @see org.eclipse.jface.text.Position#delete()
	 */
	@Override
	public void delete() {
		synchronized (fLock) {
			super.delete();
		}
	}

	/*
	 * @see org.eclipse.jface.text.Position#undelete()
	 */
	@Override
	public void undelete() {
		synchronized (fLock) {
			super.undelete();
		}
	}

	/**
	 * @return Returns the highlighting.
	 */
	public HighlightingStyle getHighlighting() {
		return fStyle;
	}

	@Override
	public boolean equals(Object other) {
		return this == other;
	}

	@Override
	public int hashCode() {
		return System.identityHashCode(this);
	}

	@Override
	public String toString() {
		final StringBuffer sb = new StringBuffer();
		sb.append("HLPos["); //$NON-NLS-1$
		sb.append(offset);
		sb.append("+"); //$NON-NLS-1$
		sb.append(length);
		sb.append(":"); //$NON-NLS-1$
		sb.append(fStyle.getSemaHighlighting().getPreferenceKey());
		sb.append("]"); //$NON-NLS-1$
		return sb.toString();
	}
}
