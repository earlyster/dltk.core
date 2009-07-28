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
import java.util.ArrayList;
import java.util.List;

import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.validators.core.IValidatorOutput;
import org.eclipse.dltk.validators.internal.ui.ValidatorConsole;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.IOConsoleOutputStream;
import org.eclipse.ui.console.IPatternMatchListener;

/**
 * Implementation of the {@link IValidatorOutput} sending output to the console.
 */
public class ConsoleValidatorOutput implements IValidatorOutput {

	private final IOConsoleOutputStream stream;
	private boolean error = false;
	private final ValidatorConsole console;

	public ConsoleValidatorOutput(String consoleName) {
		final IConsoleManager consoleManager = ConsolePlugin.getDefault()
				.getConsoleManager();
		removeTerminatedConsoles(consoleName, consoleManager);
		console = new ValidatorConsole(consoleName);
		final IPatternMatchListener[] listeners = ValidatorConsoleTrackerManager
				.getListeners();
		for (int i = 0; i < listeners.length; i++) {
			console.addPatternMatchListener(listeners[i]);
		}
		consoleManager.addConsoles(new IConsole[] { console });
		consoleManager.showConsoleView(console);
		this.stream = console.newOutputStream();
	}

	private void removeTerminatedConsoles(String consoleName,
			final IConsoleManager consoleManager) {
		List<IConsole> toRemove = null;
		final IConsole[] consoles = consoleManager.getConsoles();
		for (IConsole console : consoles) {
			if (console instanceof ValidatorConsole) {
				final ValidatorConsole vConsole = (ValidatorConsole) console;
				if (vConsole.isClosed()
						&& consoleName.equals(vConsole.getBaseName())) {
					if (toRemove == null) {
						toRemove = new ArrayList<IConsole>(consoles.length);
					}
					toRemove.add(console);
				}
			}
		}
		if (toRemove != null) {
			consoleManager.removeConsoles(toRemove
					.toArray(new IConsole[toRemove.size()]));
		}
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
		if (closed) {
			return;
		}
		try {
			stream.write(x);
			stream.write("\n"); //$NON-NLS-1$
		} catch (IOException e) {
			error = true;
		}
	}

	private boolean closed = false;

	public void close() {
		if (closed) {
			return;
		}
		closed = true;
		try {
			stream.close();
		} catch (IOException e) {
			if (DLTKCore.DEBUG) {
				e.printStackTrace();
			}
		}
		console.close();
	}

	public Object getAttribute(String name) {
		return console.getAttribute(name);
	}

	public void setAttribute(String name, Object value) {
		console.setAttribute(name, value);
	}

}
