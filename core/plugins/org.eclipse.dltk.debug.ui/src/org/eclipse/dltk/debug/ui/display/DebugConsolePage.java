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
package org.eclipse.dltk.debug.ui.display;

import org.eclipse.dltk.console.ScriptConsoleConstants;
import org.eclipse.dltk.console.ui.ScriptConsole;
import org.eclipse.dltk.console.ui.internal.ScriptConsolePage;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.console.IConsoleView;
import org.eclipse.ui.console.TextConsoleViewer;
import org.eclipse.ui.texteditor.IUpdate;

public class DebugConsolePage extends ScriptConsolePage {

	/**
	 * @param console
	 * @param view
	 * @param cfg
	 */
	public DebugConsolePage(ScriptConsole console, IConsoleView view,
			SourceViewerConfiguration cfg) {
		super(console, view, cfg);
	}

	protected IAction createTerminateConsoleAction() {
		return null;
	}

	private IAction runAction;

	protected void createActions() {
		super.createActions();
		final IToolBarManager tbManager = getSite().getActionBars()
				.getToolBarManager();
		tbManager.appendToGroup(ScriptConsoleConstants.SCRIPT_GROUP,
				new OpenInputFieldAction(this));
		runAction = new RunInputFieldAction(this);
		tbManager.appendToGroup(ScriptConsoleConstants.SCRIPT_GROUP, runAction);
		updateActions();
	}

	private SashForm sash;
	private StyledText inputField;

	protected TextConsoleViewer createViewer(Composite parent) {
		sash = new SashForm(parent, SWT.VERTICAL | SWT.SMOOTH);
		inputField = new StyledText(sash, SWT.V_SCROLL | SWT.H_SCROLL);
		inputField.setEditable(true);
		final TextConsoleViewer viewer = super.createViewer(sash);
		inputField.setFont(viewer.getControl().getFont());
		inputField.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent e) {
				updateActions();
			}

		});
		sash.setMaximizedControl(viewer.getControl());
		return viewer;
	}

	public boolean canExecuteInputField() {
		return sash.getMaximizedControl() == null
				&& inputField.getText().length() != 0;
	}

	public void openInputField() {
		sash.setWeights(new int[] { 30, 70 });
		sash.setMaximizedControl(null);
		inputField.setFocus();
		updateActions();
	}

	public void closeInputField() {
		sash.setMaximizedControl(getControl());
		updateActions();
	}

	private void updateActions() {
		if (runAction instanceof IUpdate) {
			((IUpdate) runAction).update();
		}
	}

	public void executeInputField() {
		final String input = inputField.getText();
		((ScriptConsole) getConsole()).executeCommand(input);
	}

}
