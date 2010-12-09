/*******************************************************************************
 * Copyright (c) 2010 xored software, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     xored software, Inc. - initial API and Implementation (Alex Panchenko)
 *******************************************************************************/
package org.eclipse.dltk.logconsole.ui;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.dltk.logconsole.ILogConsole;
import org.eclipse.dltk.logconsole.ILogConsoleStream;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.console.IOConsole;
import org.eclipse.ui.console.IOConsoleOutputStream;

public class LogConsoleImpl extends IOConsole {

	private final DefaultLogConsole logConsole;

	public LogConsoleImpl(DefaultLogConsole logConsole) {
		super(logConsole.getConsoleType().computeTitle(
				logConsole.getIdentifier()), logConsole.getConsoleType()
				.getType(), null, true);
		this.logConsole = logConsole;
	}

	@Override
	protected void init() {
		super.init();
		logConsole.consoleInitialized();
	}

	@Override
	protected void dispose() {
		logConsole.consoleDisposed();
		super.dispose();
	}

	private final Map<ILogConsoleStream, IOConsoleOutputStream> streams = new HashMap<ILogConsoleStream, IOConsoleOutputStream>();

	public void println(ILogConsoleStream stream, String message) {
		IOConsoleOutputStream outputStream;
		synchronized (streams) {
			outputStream = streams.get(stream);
			if (outputStream == null) {
				outputStream = newOutputStream();
				if (stream == ILogConsole.STDERR) {
					outputStream.setColor(Display.getDefault().getSystemColor(
							SWT.COLOR_RED));
				}
			}
			streams.put(stream, outputStream);
		}
		try {
			outputStream.write(message + "\n");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
