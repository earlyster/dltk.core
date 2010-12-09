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
package org.eclipse.dltk.logconsole;

import org.eclipse.dltk.internal.logconsole.LogConsoleManager;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class LogConsolePlugin implements BundleActivator {

	private static LogConsolePlugin plugin = null;

	public void start(BundleContext context) throws Exception {
		plugin = this;
	}

	public void stop(BundleContext context) throws Exception {
	}

	private LogConsoleManager consoleManager;

	public static ILogConsoleManager getConsoleManager() {
		if (plugin.consoleManager == null) {
			plugin.consoleManager = new LogConsoleManager();
		}
		return plugin.consoleManager;
	}

}
