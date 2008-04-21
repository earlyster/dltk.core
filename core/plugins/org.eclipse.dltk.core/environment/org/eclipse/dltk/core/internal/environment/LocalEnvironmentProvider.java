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
package org.eclipse.dltk.core.internal.environment;

import org.eclipse.dltk.core.environment.IEnvironment;
import org.eclipse.dltk.core.environment.IEnvironmentProvider;

public class LocalEnvironmentProvider implements IEnvironmentProvider {

	public LocalEnvironmentProvider() {
	}

	public IEnvironment getEnvironment(String envId) {
		if (LocalEnvironment.ENVIRONMENT_ID.equals(envId)) {
			return LocalEnvironment.getInstance();
		}
		return null;
	}

	public IEnvironment[] getEnvironments() {
		return new IEnvironment[] { LocalEnvironment.getInstance() };
	}

	public void waitInitialized() {
	}
}
