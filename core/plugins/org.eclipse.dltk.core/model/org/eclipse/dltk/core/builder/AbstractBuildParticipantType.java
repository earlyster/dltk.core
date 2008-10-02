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
package org.eclipse.dltk.core.builder;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.dltk.core.IScriptProject;

public abstract class AbstractBuildParticipantType implements
		IBuildParticipantFactory {

	private final String id;
	private final String name;

	protected AbstractBuildParticipantType(String id, String name) {
		this.id = id;
		this.name = name;
	}

	public String getID() {
		return id;
	}

	public String getName() {
		return name;
	}

	public final IBuildParticipant newBuildParticipant(IScriptProject project)
			throws CoreException {
		return createBuildParticipant(project);
	}

	protected abstract IBuildParticipant createBuildParticipant(
			IScriptProject project) throws CoreException;

}
