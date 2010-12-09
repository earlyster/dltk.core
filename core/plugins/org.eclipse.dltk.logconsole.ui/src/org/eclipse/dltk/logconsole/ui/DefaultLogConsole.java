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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.dltk.logconsole.ILogConsoleStream;
import org.eclipse.dltk.logconsole.LogConsoleType;
import org.eclipse.dltk.logconsole.impl.AbstractLogConsole;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;

public class DefaultLogConsole extends AbstractLogConsole {

	public DefaultLogConsole(LogConsoleType consoleType, Object identifier) {
		super(consoleType, identifier);
	}

	private static class LogItem {
		final ILogConsoleStream stream;
		final String message;

		public LogItem(ILogConsoleStream stream, String message) {
			this.stream = stream;
			this.message = message;
		}

	}

	private final List<LogItem> items = new ArrayList<LogItem>();
	private int writePos = 0;

	public void println(ILogConsoleStream stream, Object message) {
		if (message == null) {
			return;
		}
		final LogItem item = new LogItem(stream, message.toString());
		synchronized (items) {
			items.add(item);
			// TODO limit buffer size
			if (consoleImpl != null) {
				writeJob.schedule(100);
			}
		}
	}

	private final Job writeJob = new Job("") {
		protected IStatus run(IProgressMonitor monitor) {
			execute();
			return Status.OK_STATUS;
		}

		protected void execute() {
			synchronized (items) {
				if (consoleImpl == null) {
					return;
				}
				if (writePos < items.size()) {
					for (int i = writePos; i < items.size(); ++i) {
						final LogItem item = items.get(i);
						consoleImpl.println(item.stream, item.message);
					}
					writePos = items.size();
				}
			}
		}
	};

	private LogConsoleImpl consoleImpl = null;

	@Override
	public void activate() {
		synchronized (items) {
			final IConsoleManager consoleManager = ConsolePlugin.getDefault()
					.getConsoleManager();
			if (consoleImpl == null) {
				consoleImpl = new LogConsoleImpl(this);
				consoleManager.addConsoles(new IConsole[] { consoleImpl });
			}
			consoleManager.showConsoleView(consoleImpl);
		}
	}

	protected void consoleInitialized() {
		writeJob.schedule(100);
	}

	protected void consoleDisposed() {
		synchronized (items) {
			consoleImpl = null;
			writePos = 0;
		}
	}

}
