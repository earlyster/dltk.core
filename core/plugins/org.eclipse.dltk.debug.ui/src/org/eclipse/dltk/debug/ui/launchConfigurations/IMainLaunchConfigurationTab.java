/*******************************************************************************
 * Copyright (c) 2008 xored software, Inc.  
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html  
 *
 * Contributors:
 *     xored software, Inc. - initial API and Implementation (Andrei Sobolev)
 *******************************************************************************/
package org.eclipse.dltk.debug.ui.launchConfigurations;

import org.eclipse.dltk.core.IScriptProject;

/**
 * @since 2.0
 */
public interface IMainLaunchConfigurationTab {

	/**
	 * @since 2.0
	 */
	String getNatureID();

	IScriptProject getProject();

	public void addListener(IMainLaunchConfigurationTabListener listener);

	public void removeListener(IMainLaunchConfigurationTabListener listener);
}
