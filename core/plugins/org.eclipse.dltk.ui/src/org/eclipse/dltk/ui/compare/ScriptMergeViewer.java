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

import java.util.Stack;

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
public class ScriptMergeViewer extends TextMergeViewer {

	private static final Stack<IDLTKUILanguageToolkit> toolkitStorage = new Stack<IDLTKUILanguageToolkit>();

	/*
	 * Unfortunately, super constructor creates controls and calls getTitle().
	 * But the fields of this instance aren't initialized yet. So, this
	 * workaround is used to temporary save toolkit into the static variable.
	 */
	private static int pushToolkit(IDLTKUILanguageToolkit toolkit) {
		Assert.isNotNull(toolkit);
		toolkitStorage.push(toolkit);
		return SWT.LEFT_TO_RIGHT;
	}

	private static void popToolkit() {
		toolkitStorage.pop();
	}

	private final IDLTKUILanguageToolkit fToolkit;

	/**
	 * @param parent
	 * @param configuration
	 * @param title
	 */
	public ScriptMergeViewer(Composite parent,
			CompareConfiguration configuration, IDLTKUILanguageToolkit toolkit) {
		super(parent, pushToolkit(toolkit), configuration);
		popToolkit();
		this.fToolkit = toolkit;
	}

	public ScriptMergeViewer(Composite parent,
			CompareConfiguration configuration, String natureId) {
		this(parent, configuration, DLTKUILanguageManager
				.getLanguageToolkit(natureId));
	}

	protected IDLTKUILanguageToolkit getToolkit() {
		return fToolkit != null ? fToolkit : toolkitStorage.peek();
	}

	protected ScriptTextTools getTextTools() {
		return getToolkit().getTextTools();
	}

	protected IPreferenceStore getPreferenceStore() {
		return getToolkit().getPreferenceStore();
	}

	public String getTitle() {
		return NLS.bind("{0} Compare", getToolkit().getCoreToolkit()
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
