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
package org.eclipse.dltk.utils;

/**
 * @since 2.0
 */
public class DLTKLoggingOption {

	private final String fullOption;

	public DLTKLoggingOption(String bundleName, String localOption) {
		this.fullOption = bundleName + "/" + localOption; //$NON-NLS-1$
	}

	public boolean isEnabled() {
		return DLTKLogging.isEnabled(fullOption);
	}

}
