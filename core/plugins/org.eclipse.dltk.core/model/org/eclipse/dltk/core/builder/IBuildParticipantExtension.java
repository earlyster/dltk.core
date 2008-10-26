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

	int FULL_BUILD = IBuildContext.FULL_BUILD;
	int INCREMENTAL_BUILD = IBuildContext.INCREMENTAL_BUILD;
	int RECONCILE_BUILD = IBuildContext.RECONCILE_BUILD;

	void beginBuild(int buildType);

	void endBuild(IProgressMonitor monitor);

}
