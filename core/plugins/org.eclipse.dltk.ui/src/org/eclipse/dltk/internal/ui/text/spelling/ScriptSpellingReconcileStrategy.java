/*******************************************************************************
 * Copyright (c) 2000, 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.dltk.internal.ui.text.spelling;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.dltk.compiler.problem.IProblem;
import org.eclipse.dltk.core.IProblemRequestor;
import org.eclipse.dltk.ui.text.spelling.SpellCheckDelegate;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.TextUtilities;
import org.eclipse.jface.text.reconciler.DirtyRegion;
import org.eclipse.jface.text.reconciler.IReconcilingStrategy;
import org.eclipse.jface.text.reconciler.IReconcilingStrategyExtension;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.editors.text.EditorsUI;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.spelling.ISpellingProblemCollector;
import org.eclipse.ui.texteditor.spelling.SpellingContext;
import org.eclipse.ui.texteditor.spelling.SpellingProblem;
import org.eclipse.ui.texteditor.spelling.SpellingService;

/**
 * Reconcile strategy for spell checking comments.
 * 
 * @since 4.0
 */
public class ScriptSpellingReconcileStrategy implements IReconcilingStrategy,
		IReconcilingStrategyExtension {

	/**
	 * Spelling problem collector that forwards {@link SpellingProblem}s as
	 * {@link IProblem}s to the {@link IProblemRequestor}.
	 */
	private class SpellingProblemCollector implements ISpellingProblemCollector {

		SpellingProblemCollector() {
		}

		/*
		 * @see ISpellingProblemCollector#accept(SpellingProblem)
		 */
		public void accept(SpellingProblem problem) {
			final IProblemRequestor requestor = fRequestor;
			if (requestor != null) {
				try {
					final IDocument document = getDocument();
					int line = document.getLineOfOffset(problem.getOffset()) + 1;
					String word = document.get(problem.getOffset(),
							problem.getLength());
					// boolean dictionaryMatch= false;
					// boolean sentenceStart= false;
					// if (problem instanceof JavaSpellingProblem) {
					// dictionaryMatch=
					// ((JavaSpellingProblem)problem).isDictionaryMatch();
					// sentenceStart= ((JavaSpellingProblem)
					// problem).isSentenceStart();
					// }
					// see https://bugs.eclipse.org/bugs/show_bug.cgi?id=81514
					IEditorInput editorInput = fEditor.getEditorInput();
					if (editorInput != null) {
						ScriptSpellingProblem iProblem = new ScriptSpellingProblem(
								problem.getOffset(), problem.getOffset()
										+ problem.getLength(), line,
								problem.getMessage(), word,
								false /* dictionaryMatch */,
								false /* sentenceStart */, document,
								editorInput.getName());
						requestor.acceptProblem(iProblem);
					}
				} catch (BadLocationException x) {
					// drop this SpellingProblem
				}
			}
		}

		/*
		 * @see ISpellingProblemCollector#beginCollecting()
		 */
		public void beginCollecting() {
			if (fRequestor != null)
				fRequestor.beginReporting();
		}

		/*
		 * @see ISpellingProblemCollector#endCollecting()
		 */
		public void endCollecting() {
			if (fRequestor != null)
				fRequestor.endReporting();
		}
	}

	private final SpellingService fSpellingService;

	/** The text editor to operate on. */
	protected final ITextEditor fEditor;

	/** The problem requester. */
	protected IProblemRequestor fRequestor;

	private final String fPartitioning;

	/** The spelling context containing the Java source content type. */
	private final SpellingContext fSpellingContext;

	protected SpellCheckDelegate fCheckDelegate;

	/**
	 * Creates a new comment reconcile strategy.
	 * 
	 * @param editor
	 *            the text editor to operate on
	 * @param partitioning
	 */
	public ScriptSpellingReconcileStrategy(ITextEditor editor,
			String partitioning, IContentType contentType,
			SpellCheckDelegate checkDelegate) {
		fSpellingService = EditorsUI.getSpellingService();
		fEditor = editor;
		fPartitioning = partitioning;
		fSpellingContext = new SpellingContext();
		fSpellingContext.setContentType(contentType);
		fCheckDelegate = checkDelegate;
	}

	public void initialReconcile() {
		reconcile(new Region(0, fDocument.getLength()));
	}

	/*
	 * @see IReconcilingStrategy#reconcile(DirtyRegion, IRegion)
	 */
	public void reconcile(DirtyRegion dirtyRegion, IRegion subRegion) {
		try {
			IRegion startLineInfo = fDocument
					.getLineInformationOfOffset(subRegion.getOffset());
			IRegion endLineInfo = fDocument
					.getLineInformationOfOffset(subRegion.getOffset()
							+ Math.max(0, subRegion.getLength() - 1));
			if (startLineInfo.getOffset() == endLineInfo.getOffset())
				subRegion = startLineInfo;
			else
				subRegion = new Region(startLineInfo.getOffset(),
						endLineInfo.getOffset()
								+ Math.max(0, endLineInfo.getLength() - 1)
								- startLineInfo.getOffset());

		} catch (BadLocationException e) {
			subRegion = new Region(0, fDocument.getLength());
		}
		reconcile(subRegion);
	}

	/*
	 * @see IReconcilingStrategy#reconcile(IRegion)
	 */
	public void reconcile(IRegion region) {
		if (fRequestor != null && isSpellingEnabled()) {
			fRequestor.beginReporting();
			try {
				ITypedRegion[] partitions = TextUtilities.computePartitioning(
						fDocument, fPartitioning, region.getOffset(),
						region.getLength(), false);
				for (int index = 0; index < partitions.length; index++) {
					if (fProgressMonitor != null
							&& fProgressMonitor.isCanceled())
						return;
					// if (listener.isProblemsThresholdReached())
					// return;
					final ITypedRegion partition = partitions[index];
					// if (isIgnoringJavaStrings
					// && type.equals(IJavaPartitions.JAVA_STRING))
					// continue;
					final IRegion[] regions = fCheckDelegate
							.computeRegions(partition);
					if (regions != null) {
						fSpellingService.check(fDocument, regions,
								fSpellingContext, fSpellingProblemCollector,
								fProgressMonitor);
					}
				}
			} catch (BadLocationException e) {
				// ignore
			} finally {
				fRequestor.endReporting();
			}
		}
	}

	private boolean isSpellingEnabled() {
		return EditorsUI.getPreferenceStore().getBoolean(
				SpellingService.PREFERENCE_SPELLING_ENABLED);
	}

	/*
	 * @see IReconcilingStrategy#setDocument(IDocument)
	 */
	public void setDocument(IDocument document) {
		fDocument = document;
		fSpellingProblemCollector = new SpellingProblemCollector();
		updateProblemRequester();
	}

	/**
	 * Update the problem requester based on the current editor
	 */
	private void updateProblemRequester() {
		IAnnotationModel model = getAnnotationModel();
		fRequestor = (model instanceof IProblemRequestor) ? (IProblemRequestor) model
				: null;
	}

	private IAnnotationModel getAnnotationModel() {
		final IDocumentProvider documentProvider = fEditor
				.getDocumentProvider();
		if (documentProvider == null)
			return null;
		return documentProvider.getAnnotationModel(fEditor.getEditorInput());
	}

	/** The document to operate on. */
	private IDocument fDocument;

	/** The progress monitor. */
	private IProgressMonitor fProgressMonitor;

	private ISpellingProblemCollector fSpellingProblemCollector;

	/**
	 * Returns the document which is spell checked.
	 * 
	 * @return the document
	 */
	protected final IDocument getDocument() {
		return fDocument;
	}

	/*
	 * @see IReconcilingStrategyExtension#setProgressMonitor(IProgressMonitor)
	 */
	public final void setProgressMonitor(IProgressMonitor monitor) {
		fProgressMonitor = monitor;
	}

}
