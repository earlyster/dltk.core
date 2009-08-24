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
package org.eclipse.dltk.debug.ui.handlers;

import org.eclipse.osgi.util.NLS;

public class HandlerMessages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.dltk.debug.ui.handlers.HandlerMessages"; //$NON-NLS-1$
	public static String LaunchStatusDialog_commandLinePrompt;
	public static String LaunchStatusDialog_elapsedTimePrompt;
	public static String LaunchStatusDialog_message;
	/**
	 * @since 2.0
	 */
	public static String LaunchStatusDialog_message0;
	public static String LaunchStatusDialog_title;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, HandlerMessages.class);
	}

	private HandlerMessages() {
	}
}
