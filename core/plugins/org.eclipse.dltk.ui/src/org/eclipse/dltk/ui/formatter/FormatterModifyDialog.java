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
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.dltk.compiler.util.Util;
import org.eclipse.dltk.ui.preferences.ControlBindingManager;
import org.eclipse.dltk.ui.preferences.IPreferenceDelegate;
import org.eclipse.dltk.ui.util.IStatusChangeListener;
import org.eclipse.jface.dialogs.StatusDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

public abstract class FormatterModifyDialog extends StatusDialog implements
		IFormatterModifyDialog, IPreferenceDelegate, IStatusChangeListener {

	protected final IFormatterDialogOwner dialogOwner;

	private final ControlBindingManager bindingManager = new ControlBindingManager(
			this, this);

	private final Map preferences = new HashMap();

	/**
	 * @param parent
	 */
	public FormatterModifyDialog(IFormatterDialogOwner dialogOwner) {
		super(dialogOwner.getShell());
		this.dialogOwner = dialogOwner;
		setStatusLineAboveButtons(false);
		setShellStyle(getShellStyle() | SWT.RESIZE);
	}

	private TabFolder fTabFolder;
	private final List fTabPages = new ArrayList();

	protected Control createDialogArea(Composite parent) {
		final Composite composite = (Composite) super.createDialogArea(parent);
		fTabFolder = new TabFolder(composite, SWT.NONE);
		fTabFolder.setFont(composite.getFont());
		fTabFolder.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		addPages();
		bindingManager.initialize();
		return composite;
	}

	protected abstract void addPages();

	protected void addTabPage(String title, IFormatterModifiyTabPage tabPage) {
		final TabItem tabItem = new TabItem(fTabFolder, SWT.NONE);
		applyDialogFont(tabItem.getControl());
		tabItem.setText(title);
		tabItem.setData(tabPage);
		tabItem.setControl(tabPage.createContents(fTabFolder));
		fTabPages.add(tabPage);
	}

	public IFormatterDialogOwner getOwner() {
		return dialogOwner;
	}

	public ControlBindingManager getBindingManager() {
		return bindingManager;
	}

	public void statusChanged(IStatus status) {
		updateStatus(status);
		for (Iterator i = fTabPages.iterator(); i.hasNext();) {
			IFormatterModifiyTabPage tabPage = (IFormatterModifiyTabPage) i
					.next();
			tabPage.updatePreview();
		}
	}

	public String getString(Object key) {
		final String value = (String) preferences.get(key);
		return value != null ? value : Util.EMPTY_STRING;
	}

	public boolean getBoolean(Object key) {
		return Boolean.valueOf(getString(key)).booleanValue();
	}

	public void setString(Object key, String value) {
		preferences.put(key, value);
	}

	public void setBoolean(Object key, boolean value) {
		setString(key, String.valueOf(value));
	}

	public void setPreferences(Map prefs) {
		if (prefs != null) {
			preferences.clear();
			preferences.putAll(prefs);
		}
	}

	public Map getPreferences() {
		return Collections.unmodifiableMap(preferences);
	}

}
