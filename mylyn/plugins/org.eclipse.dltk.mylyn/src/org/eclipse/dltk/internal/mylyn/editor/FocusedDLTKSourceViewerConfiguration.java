/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.dltk.internal.mylyn.editor;

import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.ui.DLTKUILanguageManager;
import org.eclipse.dltk.ui.DLTKUIPlugin;
import org.eclipse.dltk.ui.IDLTKUILanguageToolkit;
import org.eclipse.dltk.ui.text.IColorManager;
import org.eclipse.dltk.ui.text.ScriptSourceViewerConfiguration;
import org.eclipse.dltk.ui.text.ScriptTextTools;
import org.eclipse.dltk.ui.text.completion.ContentAssistPreference;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.texteditor.ITextEditor;

/**
 * Installs Mylyn content assist and hyperlink detection
 * 
 * @author Mik Kersten
 */
public class FocusedDLTKSourceViewerConfiguration extends ScriptSourceViewerConfiguration {

	public FocusedDLTKSourceViewerConfiguration(IColorManager colorManager, IPreferenceStore preferenceStore,
			ITextEditor editor, String partitioning) {
		super(colorManager, preferenceStore, editor, partitioning);
	}

	@Override
	protected ContentAssistPreference getContentAssistPreference() {
		ISourceModule modelElement = DLTKUIPlugin.getEditorInputModelElement(getEditor().getEditorInput());
		final IDLTKUILanguageToolkit languageToolkit = DLTKUILanguageManager.getLanguageToolkit(modelElement);
		return new ContentAssistPreference() {

			@Override
			protected ScriptTextTools getTextTools() {
				return languageToolkit.getTextTools();
			}
		};
	}
}
