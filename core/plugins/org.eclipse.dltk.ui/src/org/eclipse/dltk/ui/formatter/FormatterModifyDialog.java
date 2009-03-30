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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.dltk.ui.formatter.internal.FormatterControlManager;
import org.eclipse.dltk.ui.formatter.internal.FormatterDialogPreferences;
import org.eclipse.dltk.ui.util.IStatusChangeListener;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.StatusDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

public abstract class FormatterModifyDialog extends StatusDialog implements
		IFormatterModifyDialog, IStatusChangeListener {

	private final FormatterDialogPreferences preferences = new FormatterDialogPreferences();

	private final FormatterControlManager controlManager = new FormatterControlManager(
			preferences, this);

	private final IFormatterModifyDialogOwner dialogOwner;
	private final IScriptFormatterFactory formatterFactory;
	final IDialogSettings fDialogSettings;

	/**
	 * @param parent
	 */
	public FormatterModifyDialog(IFormatterModifyDialogOwner dialogOwner,
			IScriptFormatterFactory formatterFactory) {
		super(dialogOwner.getShell());
		this.dialogOwner = dialogOwner;
		this.formatterFactory = formatterFactory;
		this.fDialogSettings = getDialogSettingsSection(dialogOwner
				.getDialogSettings(), formatterFactory.getId());
		setStatusLineAboveButtons(false);
		setShellStyle(getShellStyle() | SWT.RESIZE);
	}

	private static IDialogSettings getDialogSettingsSection(
			IDialogSettings settings, String sectionId) {
		IDialogSettings section = settings.getSection(sectionId);
		if (section == null) {
			section = settings.addNewSection(sectionId);
		}
		return section;
	}

	private static final String KEY_X = "x"; //$NON-NLS-1$
	private static final String KEY_Y = "y"; //$NON-NLS-1$
	private static final String KEY_WIDTH = "width"; //$NON-NLS-1$
	private static final String KEY_HEIGHT = "height"; //$NON-NLS-1$

	protected Point getInitialSize() {
		Point initialSize = super.getInitialSize();
		try {
			int lastWidth = fDialogSettings.getInt(KEY_WIDTH);
			if (initialSize.x > lastWidth)
				lastWidth = initialSize.x;
			int lastHeight = fDialogSettings.getInt(KEY_HEIGHT);
			if (initialSize.y > lastHeight)
				lastHeight = initialSize.y;
			return new Point(lastWidth, lastHeight);
		} catch (NumberFormatException ex) {
		}
		return initialSize;
	}

	protected Point getInitialLocation(Point initialSize) {
		try {
			return new Point(fDialogSettings.getInt(KEY_X), fDialogSettings
					.getInt(KEY_Y));
		} catch (NumberFormatException ex) {
			return super.getInitialLocation(initialSize);
		}
	}

	public boolean close() {
		final Rectangle shell = getShell().getBounds();
		fDialogSettings.put(KEY_WIDTH, shell.width);
		fDialogSettings.put(KEY_HEIGHT, shell.height);
		fDialogSettings.put(KEY_X, shell.x);
		fDialogSettings.put(KEY_Y, shell.y);
		return super.close();
	}

	private TabFolder fTabFolder;
	private final List fTabPages = new ArrayList();

	protected Control createDialogArea(Composite parent) {
		final Composite composite = (Composite) super.createDialogArea(parent);
		fTabFolder = new TabFolder(composite, SWT.NONE);
		fTabFolder.setFont(composite.getFont());
		fTabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		addPages();
		controlManager.initialize();
		return composite;
	}

	protected abstract void addPages();

	protected void addTabPage(String title, IFormatterModifiyTabPage tabPage) {
		final TabItem tabItem = new TabItem(fTabFolder, SWT.NONE);
		applyDialogFont(tabItem.getControl());
		tabItem.setText(title);
		tabItem.setData(tabPage);
		tabItem.setControl(tabPage.createContents(controlManager, fTabFolder));
		fTabPages.add(tabPage);
	}

	public void statusChanged(IStatus status) {
		updateStatus(status);
		for (Iterator i = fTabPages.iterator(); i.hasNext();) {
			IFormatterModifiyTabPage tabPage = (IFormatterModifiyTabPage) i
					.next();
			tabPage.updatePreview();
		}
	}

	public IFormatterModifyDialogOwner getOwner() {
		return dialogOwner;
	}

	public IScriptFormatterFactory getFormatterFactory() {
		return formatterFactory;
	}

	public void setPreferences(Map prefs) {
		preferences.set(prefs);
		final Shell shell = getShell();
		if (shell != null && !shell.isDisposed()) {
			controlManager.initialize();
		}
	}

	public Map getPreferences() {
		return preferences.get();
	}

}
