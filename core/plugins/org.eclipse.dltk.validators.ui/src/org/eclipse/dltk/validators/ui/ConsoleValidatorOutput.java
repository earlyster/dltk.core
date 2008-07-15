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
package org.eclipse.dltk.validators.ui;

import java.io.IOException;
import java.io.OutputStream;

import org.eclipse.dltk.validators.core.IValidatorOutput;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.IOConsole;
import org.eclipse.ui.console.IOConsoleOutputStream;
import org.eclipse.ui.console.IPatternMatchListener;

/**
 * Implementation of the {@link IValidatorOutput} sending output to the console.
 */
public class ConsoleValidatorOutput implements IValidatorOutput {

	private final IOConsoleOutputStream stream;
	private boolean error = false;

	public ConsoleValidatorOutput(String consoleName) {
		final IConsoleManager consoleManager = ConsolePlugin.getDefault()
				.getConsoleManager();
		final IOConsole console = new IOConsole(consoleName, null);
		final IPatternMatchListener[] listeners = ValidatorConsoleTrackerManager
				.getListeners();
		for (int i = 0; i < listeners.length; i++) {
			console.addPatternMatchListener(listeners[i]);
		}
		consoleManager.addConsoles(new IConsole[] { console });
		consoleManager.showConsoleView(console);
		this.stream = console.newOutputStream();
	}

	/**
	 * @param stream
	 */
	public ConsoleValidatorOutput(IOConsoleOutputStream stream) {
		this.stream = stream;
	}

	public OutputStream getStream() {
		return stream;
	}

	public boolean isEnabled() {
		return true;
	}

	public boolean checkError() {
		return error;
	}

	public void println(String x) {
		try {
			stream.write(x);
			stream.write("\n"); //$NON-NLS-1$
		} catch (IOException e) {
			error = true;
		}
	}

}
