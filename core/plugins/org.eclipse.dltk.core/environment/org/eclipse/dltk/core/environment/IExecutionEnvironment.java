/*******************************************************************************
 * Copyright (c) 2005, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.dltk.core.environment;

import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;

public interface IExecutionEnvironment {
	/**
	 * If realyNeed are set to true then values should be returned in any case.
	 * if realyNeed are set to false then values could be returned onle if
	 * platform require override of environment each time.
	 * 
	 * Local environment will return environment each time. RSE environment will
	 * return environment only if realYneed is true.
	 */
	Map<String, String> getEnvironmentVariables(boolean realyNeed);

	IDeployment createDeployment();

	Process exec(String[] cmdLine, IPath workingDir, String[] environment)
			throws CoreException;

	Process exec(String[] cmdLine, IPath workingDir, String[] environment,
			IExecutionLogger logger) throws CoreException;

	IEnvironment getEnvironment();

	boolean isValidExecutableAndEquals(String name, IPath fName);

	/**
	 * Tests if it's safe to specify value for the specified environment
	 * variable.
	 * 
	 * @param envVarName
	 * @return
	 * @since 2.0
	 */
	boolean isSafeEnvironmentVariable(String envVarName);

}
