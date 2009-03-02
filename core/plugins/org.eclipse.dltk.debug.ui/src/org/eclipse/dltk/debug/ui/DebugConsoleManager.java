/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.eclipse.dltk.debug.ui;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchListener;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.dltk.debug.core.DLTKDebugLaunchConstants;
import org.eclipse.dltk.debug.core.model.IScriptDebugTarget;
import org.eclipse.ui.WorkbenchEncoding;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.IOConsole;

public class DebugConsoleManager implements ILaunchListener {

	private static DebugConsoleManager instance;

	public static DebugConsoleManager getInstance() {
		if (instance == null) {
			instance = new DebugConsoleManager();
		}

		return instance;
	}

	private Map launchToConsoleMap;

	protected boolean acceptLaunch(ILaunch launch) {
		if (launch == null) {
			return false;
		}
		if (!ILaunchManager.DEBUG_MODE.equals(launch.getLaunchMode())) {
			return false;
		}
		return DLTKDebugLaunchConstants.isDebugConsole(launch);
	}

	protected ScriptDebugConsole createConsole(String name, ILaunch launch) {
		String encoding = launch
				.getAttribute(DebugPlugin.ATTR_CONSOLE_ENCODING);
		if (encoding == null) {
			try {
				encoding = launch.getLaunchConfiguration().getAttribute(
						DebugPlugin.ATTR_CONSOLE_ENCODING,
						WorkbenchEncoding.getWorkbenchDefaultEncoding());
			} catch (CoreException e) {
				e.printStackTrace();
			}
			if (encoding == null) {
				encoding = WorkbenchEncoding.getWorkbenchDefaultEncoding();
			}
		}
		ScriptDebugConsole console = new ScriptDebugConsole(name, null,
				encoding);
		console.setLaunch(launch);
		IConsoleManager manager = ConsolePlugin.getDefault()
				.getConsoleManager();
		manager.addConsoles(new IConsole[] { console });
		manager.showConsoleView(console);
		return console;
	}

	protected void destroyConsole(IOConsole console) {
		IConsoleManager manager = ConsolePlugin.getDefault()
				.getConsoleManager();
		manager.removeConsoles(new IConsole[] { console });
	}

	protected DebugConsoleManager() {
		this.launchToConsoleMap = new HashMap();
	}

	public void launchAdded(ILaunch launch) {
		if (!acceptLaunch(launch)) {
			return;
		}

		launchToConsoleMap.put(launch, createConsole(
				Messages.DebugConsoleManager_debugConsole, launch));
	}

	public void launchChanged(ILaunch launch) {
		if (!acceptLaunch(launch)) {
			return;
		}

		if (launch.getDebugTarget() instanceof IScriptDebugTarget) {
			IScriptDebugTarget target = (IScriptDebugTarget) launch
					.getDebugTarget();

			if (target != null && target.getStreamProxy() == null) {
				ScriptDebugConsole console = (ScriptDebugConsole) launchToConsoleMap
						.get(launch);
				if (console != null) {
					ScriptStreamProxy proxy = new ScriptStreamProxy(console);
					target.setStreamProxy(proxy);
				}
			}
		}
	}

	public void launchRemoved(ILaunch launch) {
		if (!acceptLaunch(launch)) {
			return;
		}

		ScriptDebugConsole console = (ScriptDebugConsole) launchToConsoleMap
				.get(launch);
		destroyConsole(console);
		launchToConsoleMap.remove(launch);
	}
}
