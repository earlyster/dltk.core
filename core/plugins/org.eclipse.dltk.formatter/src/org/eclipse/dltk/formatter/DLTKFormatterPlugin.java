/*******************************************************************************
 * Copyright (c) 2009 xored software, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     xored software, Inc. - initial API and Implementation (Alex Panchenko)
 *******************************************************************************/
package org.eclipse.dltk.formatter;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.BundleContext;

public class DLTKFormatterPlugin extends Plugin {

	public static final String PLUGIN_ID = "org.eclipse.dltk.formatter"; //$NON-NLS-1$

	private static DLTKFormatterPlugin plugin;

	public static DLTKFormatterPlugin getDefault() {
		return plugin;
	}

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	public static void error(String message) {
		getDefault().getLog()
				.log(
						new Status(IStatus.ERROR, PLUGIN_ID, IStatus.OK,
								message, null));
	}

	public static void error(String message, Throwable t) {
		getDefault().getLog().log(
				new Status(IStatus.ERROR, PLUGIN_ID, IStatus.OK, message, t));
	}

	/**
	 * @param e
	 */
	public static void error(Throwable t) {
		error(t.toString(), t);
	}

}
