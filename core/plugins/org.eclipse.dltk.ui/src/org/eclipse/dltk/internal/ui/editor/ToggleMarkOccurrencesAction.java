/*******************************************************************************
 * Copyright (c) 2000, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.dltk.internal.ui.editor;

import org.eclipse.dltk.ui.DLTKPluginImages;
import org.eclipse.dltk.ui.DLTKUILanguageManager;
import org.eclipse.dltk.ui.IDLTKUILanguageToolkit;
import org.eclipse.dltk.ui.PreferenceConstants;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.TextEditorAction;

/**
 * A toolbar action which toggles the
 * {@linkplain PreferenceConstants#EDITOR_MARK_OCCURRENCES mark occurrences
 * preference}.
 * 
 * @since 3.0
 */
public class ToggleMarkOccurrencesAction extends TextEditorAction implements
		IPropertyChangeListener {

	private IPreferenceStore fStore;

	/**
	 * Constructs and updates the action.
	 */
	public ToggleMarkOccurrencesAction() {
		super(ScriptEditorMessages.getBundleForConstructedKeys(),
				"ToggleMarkOccurrencesAction.", null, IAction.AS_CHECK_BOX); //$NON-NLS-1$
		DLTKPluginImages.setToolImageDescriptors(this, "mark_occurrences.gif"); //$NON-NLS-1$
		// PlatformUI.getWorkbench().getHelpSystem().setHelp(this,
		// IJavaHelpContextIds.TOGGLE_MARK_OCCURRENCES_ACTION);
		update();
	}

	/*
	 * @see IAction#actionPerformed
	 */
	public void run() {
		if (fStore != null) {
			fStore.setValue(PreferenceConstants.EDITOR_MARK_OCCURRENCES,
					isChecked());
		}
	}

	/*
	 * @see TextEditorAction#update
	 */
	public void update() {
		final ITextEditor editor = getTextEditor();
		boolean checked = false;
		if (editor instanceof ScriptEditor)
			checked = ((ScriptEditor) editor).isMarkingOccurrences();
		setChecked(checked);
		setEnabled(editor != null);
	}

	/*
	 * @see TextEditorAction#setEditor(ITextEditor)
	 */
	public void setEditor(ITextEditor editor) {
		super.setEditor(editor);
		if (editor != null) {
			disconnectPreferenceStore();
			if (editor instanceof ScriptEditor) {
				final IDLTKUILanguageToolkit toolkit = DLTKUILanguageManager
						.getLanguageToolkit(((ScriptEditor) editor)
								.getLanguageToolkit());
				if (toolkit != null) {
					fStore = toolkit.getPreferenceStore();
					if (fStore != null) {
						fStore.addPropertyChangeListener(this);
					}
				}
			}
		} else {
			disconnectPreferenceStore();
		}
		update();
	}

	private void disconnectPreferenceStore() {
		if (fStore != null) {
			fStore.removePropertyChangeListener(this);
			fStore = null;
		}
	}

	/*
	 * @see IPropertyChangeListener#propertyChange(PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent event) {
		if (event.getProperty().equals(
				PreferenceConstants.EDITOR_MARK_OCCURRENCES))
			setChecked(Boolean.valueOf(event.getNewValue().toString())
					.booleanValue());
	}
}
