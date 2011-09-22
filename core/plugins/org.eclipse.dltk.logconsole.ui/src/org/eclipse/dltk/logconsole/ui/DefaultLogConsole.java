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

import static java.lang.System.currentTimeMillis;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.dltk.logconsole.ILogCategory;
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
		final long timestamp;
		final ILogCategory category;
		final Object message;

		public LogItem(ILogConsoleStream stream, long timestamp,
				ILogCategory category, Object message) {
			this.stream = stream;
			this.timestamp = timestamp;
			this.category = category;
			this.message = message;
		}

	}

	protected final List<LogItem> items = new ArrayList<LogItem>();
	protected int writePos = 0;

	private static final int LIMIT = 1000;
	private static final int PURGE = LIMIT / 4;

	public void println(ILogConsoleStream stream, Object message) {
		if (message == null) {
			return;
		}
		print(new LogItem(stream, currentTimeMillis(), null, message));
	}

	public void println(ILogCategory category, Object message) {
		if (message == null) {
			return;
		}
		print(new LogItem(category.stream(), currentTimeMillis(), category,
				message));
	}

	private void print(final LogItem item) {
		synchronized (items) {
			items.add(item);
			if (items.size() > LIMIT) {
				// TODO (alex) keep list of pages and drop the whole page(s)
				items.removeAll(new ArrayList<LogItem>(items.subList(0, PURGE)));
				if (writePos > PURGE) {
					writePos -= PURGE;
				} else {
					writePos = 0;
				}
			}
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
				// TODO (alex) release lock earlier
				if (writePos < items.size()) {
					for (int i = writePos; i < items.size(); ++i) {
						final LogItem item = items.get(i);
						buffer.setLength(0);
						if (item.timestamp != 0) {
							timestamp.setTime(item.timestamp);
							final String timeStr = timestamp.toString();
							buffer.append(timeStr.substring(11));
							if (timeStr.length() < 23) {
								buffer.append("000".substring(0,
										23 - timeStr.length()));
							}
							buffer.append(' ');
						}
						if (item.category != null) {
							buffer.append(item.category);
							buffer.append(' ');
						}
						buffer.append(item.message);
						consoleImpl.println(item.stream, buffer.toString());
					}
					writePos = items.size();
				}
			}
		}

		private final StringBuilder buffer = new StringBuilder(128);

		private final Timestamp timestamp = new Timestamp(currentTimeMillis());
	};

	protected LogConsoleImpl consoleImpl = null;

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

	protected void clear() {
		synchronized (items) {
			items.clear();
			writePos = 0;
		}
	}

}
