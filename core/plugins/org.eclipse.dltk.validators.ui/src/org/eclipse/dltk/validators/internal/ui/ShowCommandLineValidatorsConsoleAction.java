/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.eclipse.dltk.validators.internal.ui;

import org.eclipse.dltk.validators.core.IValidatorOutput;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.part.IPageBookViewPage;

public class ShowCommandLineValidatorsConsoleAction extends Action {

	private final IPageBookViewPage page;
	private final ValidatorConsole console;

	public ShowCommandLineValidatorsConsoleAction(IPageBookViewPage page,
			ValidatorConsole console) {
		this.page = page;
		this.console = console;
		setText(Messages.ValidatorsConsolePageParticipant_showCommandLine);
	}

	public void run() {
		final MessageBox box = new MessageBox(page.getSite().getShell(),
				SWT.ICON_INFORMATION | SWT.OK);
		box.setText(console.getInitialName());
		String commandLine = (String) console
				.getAttribute(IValidatorOutput.COMMAND_LINE);
		if (commandLine == null) {
			commandLine = "(null)"; //$NON-NLS-1$
		}
		box.setMessage(commandLine);
		box.open();
	}

	public void update() {
		setEnabled(console.getAttribute(IValidatorOutput.COMMAND_LINE) != null);
	}

}
