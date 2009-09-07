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
package org.eclipse.dltk.launching.debug;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.dltk.core.environment.IEnvironment;

/**
 * @since 2.0
 */
public class DebuggingUtils {

	private static final String ENGINE_INSTALL = "engineInstall"; //$NON-NLS-1$
	private static final String ATTR_ENGINE_ID = "engineId"; //$NON-NLS-1$
	private static final String ATTR_PATH = "path"; //$NON-NLS-1$

	public static String getDefaultEnginePath(IEnvironment environment,
			String engineId) {
		if (environment.isLocal()) {
			final IConfigurationElement[] elements = Platform
					.getExtensionRegistry().getConfigurationElementsFor(
							DebuggingEngineManager.DEBUGGING_ENGINE_EXT_POINT);
			for (IConfigurationElement element : elements) {
				if (ENGINE_INSTALL.equals(element.getName())) {
					if (engineId.equals(element.getAttribute(ATTR_ENGINE_ID))) {
						return element.getAttribute(ATTR_PATH);
					}
				}
			}
		}
		return null;
	}
}
