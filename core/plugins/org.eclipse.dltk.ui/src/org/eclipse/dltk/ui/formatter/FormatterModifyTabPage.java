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
package org.eclipse.dltk.ui.formatter;

import java.net.URL;

import org.eclipse.dltk.ui.formatter.internal.WhitespaceCharacterPainter;
import org.eclipse.dltk.ui.util.SWTFactory;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.text.ITextViewerExtension2;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

public abstract class FormatterModifyTabPage implements
		IFormatterModifiyTabPage {

	protected static final String SHOW_INVISIBLE_PREFERENCE_KEY = "invisible.characters"; //$NON-NLS-1$

	private final IFormatterModifyDialog dialog;
	private ISourceViewer previewViewer;

	/**
	 * @param dialog
	 */
	public FormatterModifyTabPage(IFormatterModifyDialog dialog) {
		this.dialog = dialog;
	}

	private Button fShowInvisibleButton;

	public Composite createContents(IFormatterControlManager manager,
			Composite parent) {
		final SashForm page = new SashForm(parent, SWT.HORIZONTAL);
		Composite options = SWTFactory.createComposite(page, page.getFont(), 1,
				1, GridData.FILL_BOTH);
		createOptions(manager, options);
		Composite previewBlock = SWTFactory.createComposite(page, page
				.getFont(), 1, 1, GridData.FILL_BOTH);
		//
		fShowInvisibleButton = new Button(previewBlock, SWT.CHECK);
		fShowInvisibleButton
				.setText(FormatterMessages.FormatterModifyTabPage_showInvisible);
		fShowInvisibleButton.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP,
				true, false));
		fShowInvisibleButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				final boolean newValue = fShowInvisibleButton.getSelection();
				updateShowInvisible(newValue);
				getDialogSettings()
						.put(SHOW_INVISIBLE_PREFERENCE_KEY, newValue);
			}
		});
		previewViewer = dialog.getOwner().createPreview(previewBlock);
		//
		final boolean savedValue = getDialogSettings().getBoolean(
				SHOW_INVISIBLE_PREFERENCE_KEY);
		fShowInvisibleButton.setSelection(savedValue);
		updateShowInvisible(savedValue);
		return page;
	}

	private WhitespaceCharacterPainter whitespaceCharacterPainter = null;

	protected void updateShowInvisible(boolean value) {
		if (value) {
			if (whitespaceCharacterPainter == null) {
				whitespaceCharacterPainter = new WhitespaceCharacterPainter(
						previewViewer);
				((ITextViewerExtension2) previewViewer)
						.addPainter(whitespaceCharacterPainter);
			}
		} else {
			if (whitespaceCharacterPainter != null) {
				((ITextViewerExtension2) previewViewer)
						.removePainter(whitespaceCharacterPainter);
				whitespaceCharacterPainter = null;
			}
		}
	}

	private IDialogSettings getDialogSettings() {
		return ((FormatterModifyDialog) dialog).fDialogSettings;
	}

	public void updatePreview() {
		if (previewViewer != null) {
			FormatterPreviewUtils.updatePreview(previewViewer,
					getPreviewContent(), dialog.getFormatterFactory(), dialog
							.getPreferences());
		}
	}

	protected abstract void createOptions(IFormatterControlManager manager,
			Composite parent);

	protected URL getPreviewContent() {
		return null;
	}

}
