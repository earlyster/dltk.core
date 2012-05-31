package org.eclipse.dltk.ui.text.heredoc;

import java.util.Arrays;
import java.util.ListIterator;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.BadPositionCategoryException;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.TypedPosition;
import org.eclipse.jface.text.rules.FastPartitioner;
import org.eclipse.jface.text.rules.IPartitionTokenScanner;
import org.eclipse.jface.text.rules.IToken;

/**
 * A slightly modified implemenation of the <code>FastPartitioner</code> that
 * can properly partition heredoc.
 * 
 * <p>
 * There is no need to use this partitioner if heredoc is not supported by the
 * underlying dynamic language.
 * </p>
 * 
 * <p>
 * If you do use this partitioner, you <b>must</b> also use the
 * <code>HereDocEnabledPartitionScanner</code> partition scanner implemenation
 * it requires a heredoc specific partitioning rule and knows how to properly
 * buffer tokens in the event other partitions are found on the heredoc
 * identifier line, ie:
 * </p>
 * 
 * <pre>
 *   &lt;&lt;EOF . "hello world\n";
 *     heredoc body
 *   EOF
 * 
 *    &lt;&lt;ABC . "hello world\n" .  &lt;&lt;XYZ;
 *     abc body
 *   ABC
 *     xyz body
 *   XYZ
 * </pre>
 * 
 * @see HereDocPartitionRule
 * @see HereDocEnabledPartitionScanner
 * @see HereDocEnabledPresentationReconciler
 */
public class HereDocEnabledPartitioner extends FastPartitioner {

	private boolean fIsInitialized;
	private String fPositionCategory;

	private HereDocEnabledPartitionScanner fScanner;

	public HereDocEnabledPartitioner(IPartitionTokenScanner scanner,
			String[] legalContentTypes) {
		super(scanner, legalContentTypes);
		// you can't have one w/o the other...
		Assert.isTrue(scanner instanceof HereDocEnabledPartitionScanner);

		fScanner = (HereDocEnabledPartitionScanner) scanner;
		// meh, why isn't this a protected field in parent?
		fPositionCategory = getManagingPositionCategories()[0];
	}

	@Override public IRegion documentChanged2(DocumentEvent e) {
		if (!fIsInitialized)
			return null;

		try {
			Assert.isTrue(e.getDocument() == fDocument);

			Position[] category = getPositions();
			IRegion line = fDocument.getLineInformationOfOffset(e.getOffset());
			int reparseStart = line.getOffset();
			int partitionStart = -1;
			String contentType = null;
			int newLength = (e.getText() == null) ? 0 : e.getText().length();

			int first = fDocument.computeIndexInCategory(fPositionCategory,
					reparseStart);
			if (first > 0) {
				TypedPosition partition = (TypedPosition) category[first - 1];

				// heredoc partitions trump all others...
				if (HereDocUtils.isHereDocContent(partition.getType())) {
					reparseStart = findReparseStartForHereDoc(category, first,
							partition);
					partitionStart = reparseStart;
					--first;
				} else if (partition.includes(reparseStart)) {
					partitionStart = partition.getOffset();
					contentType = partition.getType();
					if (e.getOffset() == (partition.getOffset() + partition
							.getLength()))
						reparseStart = partitionStart;
					--first;
				} else if ((reparseStart == e.getOffset())
						&& (reparseStart == (partition.getOffset() + partition
								.getLength()))) {
					partitionStart = partition.getOffset();
					contentType = partition.getType();
					reparseStart = partitionStart;
					--first;
				} else {
					/*
					 * the partition will start wherever we begin reparsing
					 * otherwise interrurpting the heredoc identifier won't be
					 * reparsed correctly, ie: '<a<ABC'
					 */
					partitionStart = reparseStart;
					contentType = IDocument.DEFAULT_CONTENT_TYPE;
				}
			}

			fPositionUpdater.update(e);
			for (int i = first; i < category.length; i++) {
				Position p = category[i];
				if (p.isDeleted) {
					rememberDeletedOffset(e.getOffset());
					break;
				}
			}
			clearPositionCache();
			category = getPositions();

			fScanner.setPartialRange(fDocument, reparseStart,
					fDocument.getLength() - reparseStart, contentType,
					partitionStart);

			int behindLastScannedPosition = reparseStart;
			IToken token = fScanner.nextToken();

			while (!token.isEOF()) {
				contentType = getTokenContentType(token);

				if (!isSupportedContentType(contentType)) {
					token = fScanner.nextToken();
					continue;
				}

				int start = fScanner.getTokenOffset();
				int length = fScanner.getTokenLength();

				behindLastScannedPosition = start + length;

				int lastScannedPosition = behindLastScannedPosition - 1;

				// remove all affected positions
				while (first < category.length) {
					TypedPosition p = (TypedPosition) category[first];
					if ((lastScannedPosition >= (p.offset + p.length))
							|| (p.overlapsWith(start, length) && (!fDocument
									.containsPosition(fPositionCategory, start,
											length) || !contentType.equals(p
									.getType())))) {

						rememberRegion(p.offset, p.length);
						fDocument.removePosition(fPositionCategory, p);
						++first;

					}
					// remove any 'stacked' partitions, they will be re-added
					else if (HereDocUtils.isHereDocContent(contentType)) {
						rememberRegion(p.offset, p.length);
						fDocument.removePosition(fPositionCategory, p);
						++first;
					} else
						break;
				}

				// if position already exists and we have scanned at least the
				// area covered by the event, we are done
				if (fDocument
						.containsPosition(fPositionCategory, start, length)) {
					if (lastScannedPosition >= (e.getOffset() + newLength))
						return createRegion();
					++first;
				} else {
					// insert the new type position
					try {
						fDocument.addPosition(fPositionCategory,
								new TypedPosition(start, length, contentType));
						rememberRegion(start, length);
					} catch (BadPositionCategoryException x) {
						// should never happen on connected documents
					} catch (BadLocationException x) {
						// should never happen on connected documents
					}
				}

				token = fScanner.nextToken();
			}

			first = fDocument.computeIndexInCategory(fPositionCategory,
					behindLastScannedPosition);

			clearPositionCache();
			category = getPositions();

			TypedPosition p;
			while (first < category.length) {
				p = (TypedPosition) category[first++];
				fDocument.removePosition(fPositionCategory, p);
				rememberRegion(p.offset, p.length);
			}

		} catch (BadPositionCategoryException x) {
			// should never happen on connected documents
		} catch (BadLocationException x) {
			// should never happen on connected documents
		} finally {
			clearPositionCache();
		}

		return createRegion();
	}

	private int findReparseStartForHereDoc(Position[] category, int first,
			TypedPosition partition) throws BadLocationException {

		if (HereDocUtils.isIdentifier(partition.getType())) {
			int hdLine = fDocument.getLineOfOffset(partition.getOffset());
			return findReparseStartForIdent(hdLine, category, first);
		}

		return findReparseStartForTerm(category, first, partition.getType());
	}

	/*
	 * b/c heredoc tokens can be 'stacked', find the 'identifier' partition for
	 * the specified 'terminator' partition and then search for any additional
	 * 'identifier' paritions that may come before it on the same line
	 */
	private int findReparseStartForTerm(Position[] positions, int index,
			String termContentType) throws BadLocationException {

		TypedPosition ident = null;
		ListIterator<Position> iter = Arrays.asList(positions).listIterator(
				index);

		while (iter.hasPrevious()) {
			ident = (TypedPosition) iter.previous();
			if (HereDocUtils.isIdentForTerm(termContentType, ident.getType())) {
				index = iter.nextIndex() + 1;
				break;
			}
		}

		// this should never happen as there should always be an opening
		// parition above us
		Assert.isNotNull(ident,
				"unable to find line of a starting heredoc partition");

		@SuppressWarnings("null")
		int hdLine = fDocument.getLineOfOffset(ident.getOffset());

		return findReparseStartForIdent(hdLine, positions, index);
	}

	/*
	 * b/c heredoc tokens can be 'stacked', find the first heredoc identifier in
	 * the list of positions that falls on the same line
	 */
	private int findReparseStartForIdent(int hdLine, Position[] positions,
			int index) throws BadLocationException {

		int reparseStart = 0;
		ListIterator<Position> iter = Arrays.asList(positions).listIterator(
				index);

		TypedPosition pos = null;
		int pLine;
		do {
			pos = (TypedPosition) iter.previous();
			pLine = fDocument.getLineOfOffset(pos.getOffset());

			if (HereDocUtils.isIdentifier(pos.getType())) {
				reparseStart = pos.getOffset();
			}
		} while (hdLine == pLine && iter.hasPrevious());

		return reparseStart;
	}

	@Override protected void initialize() {
		// annoying, why isn't this exposed by the parent in some other manner?
		fIsInitialized = true;
		super.initialize();
	}

	@Override protected boolean isSupportedContentType(String contentType) {
		if (HereDocUtils.isHereDocContent(contentType)) {
			return true;
		}

		return super.isSupportedContentType(contentType);
	}

	// why isn't this protected/final?
	private IRegion createRegion() {
		if (fDeleteOffset == -1) {
			if ((fStartOffset == -1) || (fEndOffset == -1))
				return null;
			return new Region(fStartOffset, fEndOffset - fStartOffset);
		} else if ((fStartOffset == -1) || (fEndOffset == -1)) {
			return new Region(fDeleteOffset, 0);
		} else {
			int offset = Math.min(fDeleteOffset, fStartOffset);
			int endOffset = Math.max(fDeleteOffset, fEndOffset);
			return new Region(offset, endOffset - offset);
		}
	}

	// why isn't this protected/final?
	private void rememberDeletedOffset(int offset) {
		fDeleteOffset = offset;
	}

	// why isn't this protected/final?
	private void rememberRegion(int offset, int length) {
		// remember start offset
		if (fStartOffset == -1)
			fStartOffset = offset;
		else if (offset < fStartOffset)
			fStartOffset = offset;

		// remember end offset
		int endOffset = offset + length;
		if (fEndOffset == -1)
			fEndOffset = endOffset;
		else if (endOffset > fEndOffset)
			fEndOffset = endOffset;
	}
}
