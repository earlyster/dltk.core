package org.eclipse.dltk.internal.testing.util;

import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.dltk.debug.ui.ScriptDebugConsole;
import org.eclipse.dltk.testing.DLTKTestingConstants;
import org.eclipse.dltk.testing.ITestingProcessor;
import org.eclipse.jface.text.IRegion;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleListener;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.TextConsole;

public class TestingConsoleListener implements IConsoleListener {
	private final String launchKey;
	private final ILaunch launch;
	private final ITestingProcessor processor;
	private boolean initialized = false;
	private boolean finalized = false;

	public TestingConsoleListener(String launchKey, ILaunch launch,
			ITestingProcessor processor) {
		this.launchKey = launchKey;
		this.launch = launch;
		this.processor = processor;
	}

	public synchronized void consolesAdded(IConsole[] consoles) {
		// System.out.println("consolesAdded:" + consoles.length);
		checkConsoles(consoles);
		if (initialized) {
			uninstall();
		}
	}

	private synchronized void checkConsoles(IConsole[] consoles) {
		if (initialized) {
			return;
		}
		for (int i = 0; i < consoles.length; i++) {
			final IConsole console = consoles[i];
			if (console instanceof org.eclipse.debug.ui.console.IConsole) {
				org.eclipse.debug.ui.console.IConsole pc = (org.eclipse.debug.ui.console.IConsole) console;
				IProcess process = pc.getProcess();
				if (process != null
						&& launchKey.equals(process.getLaunch().getAttribute(
								DLTKTestingConstants.LAUNCH_ATTR_KEY))) {
					process((TextConsole) console);
					initialized = true;
				}
			} else if (console instanceof ScriptDebugConsole) {
				ScriptDebugConsole cl = (ScriptDebugConsole) console;
				ILaunch launch2 = cl.getLaunch();
				if (launch2 != null
						&& launchKey
								.equals(launch2
										.getAttribute(DLTKTestingConstants.LAUNCH_ATTR_KEY))) {
					process(cl);
					initialized = true;
				}
			}
		}
	}

	private synchronized void done() {
		if (!finalized) {
			finalized = true;
			processor.done();
			uninstall();
		}
	}

	private void process(TextConsole pc) {
		pc.addPatternMatchListener(new ConsoleLineNotifier() {
			private boolean first = true;

			public void connect(TextConsole console) {
				super.connect(console);
				// System.out.println("%");
			}

			public synchronized void lineAppended(IRegion region, String content) {
				if (first) {
					first = false;
					processor.start();
				}
				processor.processLine(content);
			}

			public synchronized void disconnect() {
				super.disconnect();
				done();
			}

			public synchronized void consoleClosed() {
				super.consoleClosed();
				done();
			}
		});
	}

	public void consolesRemoved(IConsole[] consoles) {
		// empty
	}

	public int hashCode() {
		return launchKey.hashCode();
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final TestingConsoleListener other = (TestingConsoleListener) obj;
		return launchKey.equals(other.launchKey);
	}

	/**
	 * 
	 */
	public void install() {
		if (initialized) {
			return;
		}
		checkConsoles(getConsoleManager().getConsoles());
		if (!initialized) {
			getConsoleManager().addConsoleListener(this);
		}
	}

	public void uninstall() {
		getConsoleManager().removeConsoleListener(this);
	}

	private static IConsoleManager getConsoleManager() {
		return ConsolePlugin.getDefault().getConsoleManager();
	}

}
