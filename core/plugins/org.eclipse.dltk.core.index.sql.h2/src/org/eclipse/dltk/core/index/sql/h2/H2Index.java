/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Zend Technologies
 *******************************************************************************/
package org.eclipse.dltk.core.index.sql.h2;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.eclipse.dltk.internal.core.index.sql.h2.H2Cache;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class H2Index extends Plugin {

	public static final String PLUGIN_ID = "org.eclipse.dltk.core.index.sql.h2"; //$NON-NLS-1$

	public static final boolean DEBUG = Boolean.valueOf(
			Platform.getDebugOption(PLUGIN_ID + "/debug")).booleanValue(); //$NON-NLS-1$

	private static H2Index plugin;

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;

		H2Cache.load();
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static H2Index getDefault() {
		return plugin;
	}

	public static void error(String message) {
		plugin.getLog()
				.log(
						new Status(IStatus.ERROR, PLUGIN_ID, IStatus.OK,
								message, null));
	}

	public static void error(String message, Throwable t) {
		plugin.getLog().log(
				new Status(IStatus.ERROR, PLUGIN_ID, IStatus.OK, message, t));
	}

	public static void warn(String message) {
		warn(message, null);
	}

	public static void warn(String message, Throwable t) {
		plugin.getLog().log(
				new Status(IStatus.WARNING, PLUGIN_ID, IStatus.OK, message, t));
	}
}
