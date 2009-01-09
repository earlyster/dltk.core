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
package org.eclipse.dltk.core.environment;

public abstract class EnvironmentChangedListener implements
		IEnvironmentChangedListener {

	public void environmentAdded(IEnvironment environment) {
	}

	public void environmentChanged(IEnvironment environment) {
	}

	public void environmentRemoved(IEnvironment environment) {
	}

	public void environmentsModified() {
	}

}
