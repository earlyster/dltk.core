/*******************************************************************************
 * Copyright (c) 2010 xored software, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     xored software, Inc. - initial API and Implementation (Alex Panchenko)
 *******************************************************************************/
package org.eclipse.dltk.ui.text.folding;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.dltk.ui.text.IPartitioningProvider;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.TextUtilities;
import org.eclipse.jface.text.rules.FastPartitioner;

/**
 * @since 2.0
 */
public abstract class PartitioningFoldingBlockProvider implements
		IFoldingBlockProvider {

	private final IPartitioningProvider partitioningProvider;

	public PartitioningFoldingBlockProvider(
			IPartitioningProvider partitioningProvider) {
		this.partitioningProvider = partitioningProvider;
	}

	protected boolean collapseEmptyLines() {
		// TODO
		return true;
	}

	private IFoldingBlockRequestor requestor;

	public void setRequestor(IFoldingBlockRequestor requestor) {
		this.requestor = requestor;
	}

	protected void computeBlocksForPartitionType(IFoldingContent content,
			String partition, IFoldingBlockKind kind) {
		try {
			final String contents = content.getSourceContents();
			if (contents == null || contents.length() == 0) {
				return;
			}
			Document d = new Document(contents);
			installDocumentStuff(d);
			ITypedRegion start = null;
			ITypedRegion lastRegion = null;
			List<IRegion> regions = new ArrayList<IRegion>();
			for (ITypedRegion region : computePartitioning(d)) {
				if (region.getType().equals(partition)
						&& startsAtLineBegin(d, region)) {
					if (start == null)
						start = region;
				} else if (start != null
						&& (isBlankRegion(d, region) || isEmptyRegion(d, region)
								&& collapseEmptyLines())) {
					// blanks or empty lines
					// TODO introduce line limit for collapseEmptyLines() ?
				} else {
					if (start != null) {
						assert lastRegion != null;
						int offset0 = start.getOffset();
						int length0 = lastRegion.getOffset()
								+ lastRegion.getLength() - offset0 - 1;
						length0 = contents
								.substring(offset0, offset0 + length0).trim()
								.length();
						IRegion fullRegion = new Region(offset0, length0);
						regions.add(fullRegion);
					}
					start = null;
				}
				lastRegion = region;
			}
			if (start != null) {
				assert lastRegion != null;
				int offset0 = start.getOffset();
				int length0 = lastRegion.getOffset() - offset0
						+ lastRegion.getLength() - 1;
				IRegion fullRegion = new Region(offset0, length0);
				regions.add(fullRegion);
			}
			prepareRegions(d, regions);
			removeDocumentStuff(d);
			for (IRegion region : regions) {
				Object element = null;
				requestor.acceptBlock(region, kind, element);
			}
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}

	private List<ITypedRegion> computePartitioning(Document d) {
		// TODO TextUtilities.computePartitioning() ?
		List<ITypedRegion> docRegionList = new ArrayList<ITypedRegion>();
		int offset = 0;
		for (;;) {
			try {
				ITypedRegion region = getRegion(d, offset);
				docRegionList.add(region);
				offset = region.getLength() + region.getOffset() + 1;
			} catch (BadLocationException e1) {
				break;
			}
		}
		return docRegionList;
	}

	/**
	 * @param d
	 * @param regions
	 */
	protected void prepareRegions(Document d, List<IRegion> regions) {
		// override in descendants
	}

	private ITypedRegion getRegion(IDocument d, int offset)
			throws BadLocationException {
		return TextUtilities.getPartition(d, partitioningProvider
				.getPartitioning(), offset, true);
	}

	/**
	 * Tests if the specified region contains only space or tab characters.
	 * 
	 * @param document
	 * @param region
	 * @return
	 * @throws BadLocationException
	 * @since 2.0
	 */
	protected boolean isBlankRegion(IDocument document, ITypedRegion region)
			throws BadLocationException {
		String value = document.get(region.getOffset(), region.getLength());
		for (int i = 0; i < value.length(); ++i) {
			char ch = value.charAt(i);
			if (ch != ' ' && ch != '\t') {
				return false;
			}
		}
		return true;
	}

	private boolean startsAtLineBegin(Document d, ITypedRegion region)
			throws BadLocationException {
		int lineStart = d.getLineOffset(d.getLineOfOffset(region.getOffset()));
		if (lineStart != region.getOffset()) {
			if (!isEmptyRegion(d, lineStart, region.getOffset() - lineStart)) {
				return false;
			}
		}
		return true;
	}

	protected boolean isEmptyRegion(IDocument d, ITypedRegion r)
			throws BadLocationException {
		return isEmptyRegion(d, r.getOffset(), r.getLength());
	}

	protected boolean isEmptyRegion(IDocument d, int offset, int length)
			throws BadLocationException {
		return d.get(offset, length).trim().length() == 0;
	}

	/**
	 * Installs a partitioner with <code>document</code>.
	 * 
	 * @param document
	 *            the document
	 */
	private void installDocumentStuff(Document document) {
		final IDocumentPartitioner partitioner = new FastPartitioner(
				partitioningProvider.createPartitionScanner(),
				partitioningProvider.getPartitionContentTypes());
		partitioner.connect(document);
		document.setDocumentPartitioner(partitioningProvider.getPartitioning(),
				partitioner);
	}

	/**
	 * Removes partitioner with <code>document</code>.
	 * 
	 * @param document
	 *            the document
	 */
	private void removeDocumentStuff(Document document) {
		final String partitioning = partitioningProvider.getPartitioning();
		final IDocumentPartitioner partitioner = document
				.getDocumentPartitioner(partitioning);
		if (partitioner != null) {
			document.setDocumentPartitioner(partitioning, null);
			partitioner.disconnect();
		}
	}

}
