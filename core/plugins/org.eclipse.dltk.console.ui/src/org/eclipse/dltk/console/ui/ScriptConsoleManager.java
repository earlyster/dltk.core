/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.eclipse.dltk.console.ui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchListener;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.dltk.console.IScriptInterpreter;
import org.eclipse.dltk.console.ScriptConsoleServer;
import org.eclipse.dltk.console.ScriptInterpreterManager;
import org.eclipse.dltk.console.ui.internal.ConsoleViewManager;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.launching.ScriptLaunchConfigurationConstants;
import org.eclipse.dltk.launching.process.IScriptProcess;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.IConsoleView;

public class ScriptConsoleManager implements ILaunchListener {
	private static ScriptConsoleManager instance;

	public static synchronized ScriptConsoleManager getInstance() {
		if (instance == null) {
			instance = new ScriptConsoleManager();
		}

		return instance;
	}

	private IConsoleManager getConsoleManager() {
		return ConsolePlugin.getDefault().getConsoleManager();
	}

	/**
	 * @since 2.0
	 */
	public IScriptConsole[] getScriptConsoles(String consoleType) {
		List<IScriptConsole> consoles = new ArrayList<IScriptConsole>();
		for (IConsole console : getConsoleManager().getConsoles()) {
			if (console instanceof IScriptConsole
					&& console.getType().equals(consoleType)) {
				consoles.add((ScriptConsole) console);
			}
		}
		return consoles.toArray(new IScriptConsole[consoles.size()]);
	}

	private String[] consoleViewIds = null;

	private synchronized String[] getConsoleViewIds() {
		if (consoleViewIds == null) {
			final List<String> viewIds = new ArrayList<String>();
			for (ConsoleViewManager.Descriptor descriptor : new ConsoleViewManager(
					"org.eclipse.dltk.console.ui.consoleView").getDescriptors()) { //$NON-NLS-1$
				viewIds.add(descriptor.getViewId());
			}
			consoleViewIds = viewIds.toArray(new String[viewIds.size()]);
		}
		return consoleViewIds;
	}

	/**
	 * @since 2.0
	 */
	public IScriptConsole getActiveScriptConsole(String consoleType) {
		final IWorkbenchWindow window = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow();
		if (window != null) {
			final IWorkbenchPage page = window.getActivePage();
			if (page != null) {
				for (String consoleViewId : getConsoleViewIds()) {
					IViewPart part = page.findView(consoleViewId);
					if (part != null && part instanceof IConsoleView) {
						IConsoleView view = (IConsoleView) part;
						IConsole console = view.getConsole();
						if (console instanceof IScriptConsole
								&& console.getType().equals(consoleType)) {
							return (IScriptConsole) console;
						}
					}
				}
			}
		}

		return null;
	}

	/**
	 * @since 2.0
	 */
	public void close(IScriptConsole console) {
		console.terminate();
		remove(console);
	}

	public void closeAll() {
		IConsole[] consoles = getConsoleManager().getConsoles();
		for (int i = 0; i < consoles.length; ++i) {
			IConsole console = consoles[i];
			if (console instanceof IScriptConsole) {
				close((IScriptConsole) console);
			}
		}
	}

	/**
	 * @since 2.0
	 */
	public void showConsole(IScriptConsole console) {
		getConsoleManager().showConsoleView(console);
	}

	/**
	 * @since 2.0
	 */
	public void add(IScriptConsole console) {
		getConsoleManager().addConsoles(new IConsole[] { console });
	}

	/**
	 * @since 2.0
	 */
	public void remove(IScriptConsole console) {
		getConsoleManager().removeConsoles(new IConsole[] { console });
	}

	protected IScriptConsoleFactory findScriptConsoleFactory(String natureId)
			throws CoreException {
		IExtensionRegistry reg = Platform.getExtensionRegistry();
		IExtensionPoint ep = reg
				.getExtensionPoint(ScriptConsoleUIConstants.SCRIPT_CONSOLE_EP);
		IExtension[] extensions = ep.getExtensions();

		for (int i = 0; i < extensions.length; i++) {
			IExtension ext = extensions[i];
			IConfigurationElement[] ce = ext.getConfigurationElements();
			for (int j = 0; j < ce.length; j++) {
				if (natureId
						.equals(ce[j]
								.getAttribute(ScriptConsoleUIConstants.SCRIPT_CONSOLE_NATURE_ID))) {
					Object obj = ce[j]
							.createExecutableExtension(ScriptConsoleUIConstants.SCRIPT_CONSOLE_CLASS);
					if (obj instanceof IScriptConsoleFactory) {
						return (IScriptConsoleFactory) obj;
					} else {
						return null;
					}
				}
			}
		}

		return null;
	}

	// ILaunchListener
	public void launchAdded(final ILaunch launch) {
		launchChanged(launch);
	}

	public void launchChanged(final ILaunch launch) {
		if (!ILaunchManager.RUN_MODE.equals(launch.getLaunchMode())) {
			return;
		}
		try {
			final ILaunchConfiguration configuration = launch
					.getLaunchConfiguration();
			if (configuration == null) {
				return;
			}
			boolean useDltk = configuration
					.getAttribute(
							ScriptLaunchConfigurationConstants.ATTR_USE_INTERACTIVE_CONSOLE,
							false);
			if (!useDltk) {
				return;
			}
			final ScriptConsole console = getConsole(launch);
			if (console != null) {
				IProcess[] processes = launch.getProcesses();
				for (int i = 0; i < processes.length; ++i) {
					final IProcess process = processes[i];
					if (process instanceof IScriptProcess) {
						console.connect((IScriptProcess) process);
					}
				}
				return;
			}
			final String natureId = configuration.getAttribute(
					ScriptLaunchConfigurationConstants.ATTR_SCRIPT_NATURE,
					(String) null);
			if (natureId == null) {
				return;
			}
			final String consoleId = configuration.getAttribute(
					ScriptLaunchConfigurationConstants.ATTR_DLTK_CONSOLE_ID,
					(String) null);
			final IScriptConsoleFactory factory = findScriptConsoleFactory(natureId);
			if (factory == null) {
				return;
			}
			SafeRunner.run(new ISafeRunnable() {
				public void handleException(Throwable exception) {
				}

				public void run() throws Exception {
					IScriptInterpreter interpreter = ScriptInterpreterManager
							.getInstance().createInterpreter(natureId);
					ScriptConsoleServer.getInstance().register(consoleId,
							interpreter);
					factory.openConsole(interpreter, configuration.getName(),
							launch);
				}
			});
		} catch (CoreException e) {
			if (DLTKCore.DEBUG) {
				e.printStackTrace();
			}
		}
	}

	private ScriptConsole getConsole(ILaunch launch) {
		final IConsoleManager manager = ConsolePlugin.getDefault()
				.getConsoleManager();
		final IConsole[] consoles = manager.getConsoles();
		for (int i = 0; i < consoles.length; i++) {
			final IConsole console = consoles[i];
			if (console instanceof ScriptConsole) {
				final ScriptConsole sc = (ScriptConsole) console;
				final ILaunch consoleLaunch = sc.getLaunch();
				if (consoleLaunch != null && consoleLaunch.equals(launch)) {
					return sc;
				}
			}
		}
		return null;
	}

	public void launchRemoved(ILaunch launch) {
		final IConsole console = getConsole(launch);
		if (console != null) {
			IConsoleManager manager = ConsolePlugin.getDefault()
					.getConsoleManager();
			manager.removeConsoles(new IConsole[] { console });
		}
	}
}
