/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.eclipse.dltk.debug.ui;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.eclipse.debug.internal.ui.DebugUIPlugin;
import org.eclipse.debug.internal.ui.preferences.IDebugPreferenceConstants;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.internal.debug.core.model.IScriptStreamProxy;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.IOConsole;
import org.eclipse.ui.console.IOConsoleInputStream;
import org.eclipse.ui.console.IOConsoleOutputStream;

public class ScriptStreamProxy implements IScriptStreamProxy {
	private IOConsoleInputStream input;
	private IOConsoleOutputStream stdOut;
	private IOConsoleOutputStream stdErr;

	private boolean closed = false;

	public ScriptStreamProxy(IOConsole console) {
		input = console.getInputStream();
		stdOut = console.newOutputStream();
		stdErr = console.newOutputStream();

		// TODO is there a better way to access these internal preferences??
		boolean activeOnStderr = DebugUIPlugin.getDefault()
				.getPreferenceStore().getBoolean(
						IDebugPreferenceConstants.CONSOLE_OPEN_ON_ERR);
		boolean activeOnStdou = DebugUIPlugin.getDefault().getPreferenceStore()
				.getBoolean(IDebugPreferenceConstants.CONSOLE_OPEN_ON_OUT);
		stdErr.setActivateOnWrite(activeOnStderr);
		stdOut.setActivateOnWrite(activeOnStdou);

		final Display display = getDisplay();
		display.asyncExec(new Runnable() {
			public void run() {
				RGB errRGB = PreferenceConverter.getColor(DebugUIPlugin
						.getDefault().getPreferenceStore(),
						IDebugPreferenceConstants.CONSOLE_SYS_ERR_COLOR);
				RGB outRGB = PreferenceConverter.getColor(DebugUIPlugin
						.getDefault().getPreferenceStore(),
						IDebugPreferenceConstants.CONSOLE_SYS_OUT_COLOR);

				stdErr
						.setColor(DLTKDebugUIPlugin.getDefault().getColor(
								errRGB));
				stdOut
						.setColor(DLTKDebugUIPlugin.getDefault().getColor(
								outRGB));
			}

		});

	}

	private Display getDisplay() {
		// If we are in the UI Thread use that
		if (Display.getCurrent() != null) {
			return Display.getCurrent();
		}

		if (PlatformUI.isWorkbenchRunning()) {
			return PlatformUI.getWorkbench().getDisplay();
		}

		return Display.getDefault();
	}

	public OutputStream getStderr() {
		return stdErr;
	}

	public OutputStream getStdout() {
		return stdOut;
	}

	public InputStream getStdin() {
		return input;
	}

	public synchronized void close() {
		if (!closed) {
			try {
				stdOut.close();
				stdErr.close();
				input.close();
				closed = true;
			} catch (IOException e) {
				if (DLTKCore.DEBUG) {
					e.printStackTrace();
				}
			}
		}
	}
}
