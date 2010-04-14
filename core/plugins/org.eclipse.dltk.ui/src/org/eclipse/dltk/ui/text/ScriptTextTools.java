/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.eclipse.dltk.ui.text;

import org.eclipse.dltk.internal.ui.text.DLTKColorManager;
import org.eclipse.dltk.ui.editor.highlighting.ISemanticHighlighter;
import org.eclipse.dltk.ui.editor.highlighting.SemanticHighlighting;
import org.eclipse.dltk.ui.text.templates.TemplateVariableProcessor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentExtension3;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.rules.FastPartitioner;
import org.eclipse.jface.text.rules.IPartitionTokenScanner;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.ui.texteditor.ITextEditor;

public abstract class ScriptTextTools implements IPartitioningProvider {
	private DLTKColorManager fColorManager;

	private String fDefaultPartitioning;

	private String[] fLegalContentTypes;

	protected ScriptTextTools(String defaultPartitioning,
			String[] legalContentTypes, boolean autoDisposeOnDisplayDispose) {
		fDefaultPartitioning = defaultPartitioning;
		fLegalContentTypes = legalContentTypes;
		fColorManager = new DLTKColorManager(autoDisposeOnDisplayDispose);
	}

	public String getPartitioning() {
		return fDefaultPartitioning;
	}

	public String[] getPartitionContentTypes() {
		return fLegalContentTypes;
	}

	/**
	 * Disposes all the individual tools of this tools collection.
	 */
	public void dispose() {
		if (fColorManager != null) {
			fColorManager.dispose();
			fColorManager = null;
		}
	}

	/**
	 * Returns the color manager which is used to manage any DLTK-specific
	 * colors needed for such things like syntax highlighting.
	 * <p>
	 * Clients which are only interested in the color manager of the DLTK UI
	 * plug-in should use ....
	 * </p>
	 * 
	 * @return the color manager to be used for DLTK text viewers
	 */
	public IColorManager getColorManager() {
		return fColorManager;
	}

	public final ScriptSourceViewerConfiguration createSourceViewerConfiguraton(
			IPreferenceStore preferenceStore, ITextEditor editor) {
		return createSourceViewerConfiguraton(preferenceStore, editor,
				fDefaultPartitioning);
	}

	public abstract ScriptSourceViewerConfiguration createSourceViewerConfiguraton(
			IPreferenceStore preferenceStore, ITextEditor editor,
			String partitioning);

	/**
	 * Creates {@link SourceViewerConfiguration} to use in code template editor.
	 * Should be overridden in ancestors.
	 * 
	 * @param preferenceStore
	 * @param variableProcessor
	 * @return
	 */
	public ScriptSourceViewerConfiguration createSourceViewerConfiguraton(
			IPreferenceStore preferenceStore, ITextEditor editor,
			TemplateVariableProcessor variableProcessor) {
		return createSourceViewerConfiguraton(preferenceStore, editor,
				fDefaultPartitioning);
	}

	public IPartitionTokenScanner createPartitionScanner() {
		return getPartitionScanner();
	}

	@Deprecated
	public IPartitionTokenScanner getPartitionScanner() {
		return null;
	}

	/**
	 * Factory method for creating a script-specific document partitioner using
	 * this object's partitions scanner. This method is a convenience method.
	 * 
	 * @return a newly created script document partitioner
	 */
	public IDocumentPartitioner createDocumentPartitioner() {
		IPartitionTokenScanner scaner = createPartitionScanner();
		if (scaner == null) {
			return null;
		}
		return new FastPartitioner(scaner, fLegalContentTypes);
	}

	/**
	 * Sets up the script document partitioner for the given document for the
	 * {@link IDocumentExtension3.DEFAULT_PARTITIONING} default partitioning.
	 * 
	 * @param document
	 *            the document to be set up
	 */
	public void setupDefaultDocumentPartitioner(IDocument document) {
		setupDocumentPartitioner(document,
				IDocumentExtension3.DEFAULT_PARTITIONING);
	}

	/**
	 * Sets up the script document partitioner for the given document for the
	 * default partitioning of this object.
	 * 
	 * @param document
	 *            the document to be set up
	 */
	public void setupDocumentPartitioner(IDocument document) {
		setupDocumentPartitioner(document, fDefaultPartitioning);
	}

	/**
	 * Sets up the script document partitioner for the given document for the
	 * given partitioning.
	 * 
	 * @param document
	 *            the document to be set up
	 * @param partitioning
	 *            the document partitioning
	 */
	public void setupDocumentPartitioner(IDocument document, String partitioning) {
		IDocumentPartitioner partitioner = createDocumentPartitioner();
		if (partitioner != null) {
			partitioner.connect(document);
			if (document instanceof IDocumentExtension3) {
				IDocumentExtension3 extension3 = (IDocumentExtension3) document;
				extension3.setDocumentPartitioner(partitioning, partitioner);
			} else {
				document.setDocumentPartitioner(partitioner);
			}
		}
	}

	public SemanticHighlighting[] getSemanticHighlightings() {
		return new SemanticHighlighting[0];
	}

	public ISemanticHighlighter getSemanticPositionUpdater() {
		return null;
	}

	public String getDefaultPartitioning() {
		return fDefaultPartitioning;
	}

}
