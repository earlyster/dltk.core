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
package org.eclipse.dltk.ui.compare;

import org.eclipse.compare.CompareConfiguration;
import org.eclipse.compare.contentmergeviewer.TextMergeViewer;
import org.eclipse.core.runtime.Assert;
import org.eclipse.dltk.ui.DLTKUILanguageManager;
import org.eclipse.dltk.ui.IDLTKUILanguageToolkit;
import org.eclipse.dltk.ui.text.ScriptTextTools;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

/**
 * Abstract base class for compare viewers.
 * 
 * Note: {@link TextMergeViewer} uses current class name as font symbolic name,
 * so it descendants should not be anonymous classes, because their class name
 * is unpredictable.
 */
public abstract class ScriptMergeViewer extends TextMergeViewer {

	private final IDLTKUILanguageToolkit fToolkit;

	/**
	 * @param parent
	 * @param configuration
	 * @param title
	 */
	public ScriptMergeViewer(Composite parent,
			CompareConfiguration configuration, IDLTKUILanguageToolkit toolkit) {
		super(parent, SWT.LEFT_TO_RIGHT, configuration);
		Assert.isNotNull(toolkit);
		this.fToolkit = toolkit;
	}

	public ScriptMergeViewer(Composite parent,
			CompareConfiguration configuration, String natureId) {
		this(parent, configuration, DLTKUILanguageManager
				.getLanguageToolkit(natureId));
	}

	protected ScriptTextTools getTextTools() {
		return fToolkit.getTextTools();
	}

	protected IPreferenceStore getPreferenceStore() {
		return fToolkit.getPreferenceStore();
	}

	public String getTitle() {
		return NLS.bind("{0} Compare", fToolkit.getCoreToolkit()
				.getLanguageName());
	}

	/*
	 * @see
	 * org.eclipse.compare.contentmergeviewer.TextMergeViewer#configureTextViewer
	 * (org.eclipse.jface.text.TextViewer)
	 */
	protected void configureTextViewer(TextViewer textViewer) {
		if (!(textViewer instanceof SourceViewer)) {
			return;
		}
		final ScriptTextTools tools = getTextTools();
		if (tools == null) {
			return;
		}
		final SourceViewer sourceViewer = (SourceViewer) textViewer;
		sourceViewer.configure(tools.createSourceViewerConfiguraton(
				getPreferenceStore(), null));
	}

	protected IDocumentPartitioner getDocumentPartitioner() {
		final ScriptTextTools tools = getTextTools();
		return tools != null ? tools.createDocumentPartitioner() : null;
	}

	protected String getDocumentPartitioning() {
		final ScriptTextTools tools = getTextTools();
		return tools != null ? tools.getDefaultPartitioning() : null;
	}

}
