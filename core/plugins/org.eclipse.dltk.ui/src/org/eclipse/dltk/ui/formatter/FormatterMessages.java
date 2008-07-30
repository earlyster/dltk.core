/*******************************************************************************
 * Copyright (c) 2008 xored software, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     xored software, Inc. - initial API and Implementation (Alex Panchenko)
 *******************************************************************************/
package org.eclipse.dltk.ui.formatter;

import org.eclipse.osgi.util.NLS;

public class FormatterMessages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.dltk.ui.formatter.FormatterMessages"; //$NON-NLS-1$
	public static String ScriptFormattingStrategy_formattingError;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, FormatterMessages.class);
	}

	private FormatterMessages() {
	}
}
