/*******************************************************************************
 * Copyright (c) 2010 xored software, Inc.
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

import org.eclipse.core.runtime.IPath;

/**
 * Reserved for future use.
 * 
 * Eventually it will be used to collect dependencies between project sources.
 */
public interface IBuildState {
	/**
	 * @param path
	 */
	void recordImportProblem(IPath path);

	/**
	 * @param path
	 *            module path
	 * @param dependency
	 *            it's dependency
	 */
	void recordDependency(IPath path, IPath dependency);
}
