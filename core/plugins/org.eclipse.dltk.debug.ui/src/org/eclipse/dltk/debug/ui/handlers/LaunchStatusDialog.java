/*******************************************************************************
 * Copyright (c) 2009 xored software, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     xored software, Inc. - initial API and Implementation (Alex Panchenko)
 *******************************************************************************/
package org.eclipse.dltk.debug.ui.handlers;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.dltk.compiler.util.Util;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IconAndMessageDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class LaunchStatusDialog extends IconAndMessageDialog {

	private final IProgressMonitor monitor;

	public LaunchStatusDialog(Shell shell, IProgressMonitor monitor) {
		super(shell);
		this.monitor = monitor;
		setShellStyle(getDefaultOrientation() | SWT.BORDER | SWT.TITLE
				| SWT.APPLICATION_MODAL);
		message = HandlerMessages.LaunchStatusDialog_message;
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText(HandlerMessages.LaunchStatusDialog_title);
	}

	@Override
	protected Image getImage() {
		return getWarningImage();
	}

	private String commandLine;

	private Label elapsedTimeValue;

	public void updateElapsedTime(long elapsedTime) {
		if (elapsedTimeValue == null || elapsedTimeValue.isDisposed()) {
			return;
		}
		int h = (int) (elapsedTime / 1000 / 60 / 60);
		elapsedTime -= h * 1000 * 60 * 60;
		int m = (int) (elapsedTime / 1000 / 60);
		elapsedTime -= m * 1000 * 60;
		int s = (int) (elapsedTime / 1000);
		final StringBuffer sb = new StringBuffer();
		if (h < 10)
			sb.append('0');
		sb.append(h);
		sb.append(':');
		if (m < 10)
			sb.append('0');
		sb.append(m);
		sb.append(':');
		if (s < 10)
			sb.append('0');
		sb.append(s);
		elapsedTimeValue.setText(sb.toString());
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		createMessageArea(parent);

		final Composite composite = new Composite(parent, SWT.NONE);
		final GridLayout layout = new GridLayout(2, false);
		composite.setLayout(layout);
		final GridData compositeData = new GridData(GridData.FILL_BOTH);
		compositeData.horizontalSpan = 2;
		composite.setLayoutData(compositeData);

		if (commandLine != null) {
			final Label commandLinePrompt = new Label(composite, SWT.NONE);
			commandLinePrompt
					.setText(HandlerMessages.LaunchStatusDialog_commandLinePrompt);
			final GridData commandLineData = new GridData(
					GridData.FILL_HORIZONTAL);
			commandLineData.horizontalSpan = 2;
			commandLinePrompt.setLayoutData(commandLineData);

			final Text commandLineValue = new Text(composite, SWT.WRAP
					| SWT.READ_ONLY | SWT.BORDER);
			commandLineValue.setText(commandLine);
			final GridData commandLineValueData = new GridData(
					GridData.FILL_HORIZONTAL);
			commandLineValueData.horizontalSpan = 2;
			commandLineValueData.widthHint = 500;
			commandLineValue.setLayoutData(commandLineValueData);
		}

		final Label elapsedTimePrompt = new Label(composite, SWT.NONE);
		elapsedTimePrompt
				.setText(HandlerMessages.LaunchStatusDialog_elapsedTimePrompt);

		elapsedTimeValue = new Label(composite, SWT.NONE);
		elapsedTimeValue.setText(Util.EMPTY_STRING);
		elapsedTimeValue.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		return parent;
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createCancelButton(parent);
	}

	protected void createCancelButton(Composite parent) {
		createButton(parent, IDialogConstants.CANCEL_ID,
				IDialogConstants.CANCEL_LABEL, true);
	}

	@Override
	protected void cancelPressed() {
		this.monitor.setCanceled(true);
		close();
	}

	public void setCommandLine(String commandLine) {
		this.commandLine = commandLine;
	}

	/**
	 * @since 2.0
	 */
	public void setLaunchName(String launchName) {
		if (launchName != null) {
			message = NLS.bind(HandlerMessages.LaunchStatusDialog_message0,
					launchName);
		}
	}

}
