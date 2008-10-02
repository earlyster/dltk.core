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

import org.eclipse.core.runtime.IProgressMonitor;

public interface IBuildParticipantExtension {

	public static final int FULL_BUILD = IScriptBuilder.FULL_BUILD;
	public static final int INCREMENTAL_BUILD = IScriptBuilder.INCREMENTAL_BUILD;

	public static final int RECONCILE_BUILD = 10;

	void beginBuild(int buildType);

	void endBuild(IProgressMonitor monitor);

}
