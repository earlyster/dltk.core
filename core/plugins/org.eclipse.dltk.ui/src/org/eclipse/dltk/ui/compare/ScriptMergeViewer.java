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
import org.eclipse.dltk.ui.text.ScriptTextTools;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.jface.text.source.SourceViewer;
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

	/**
	 * @param parent
	 * @param configuration
	 * @param title
	 */
	public ScriptMergeViewer(Composite parent,
			CompareConfiguration configuration) {
		super(parent, SWT.LEFT_TO_RIGHT, configuration);
	}

	protected abstract ScriptTextTools getTextTools();

	protected abstract IPreferenceStore getPreferenceStore();

	public abstract String getTitle();

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
