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

import java.util.Set;

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

	/**
	 * Records the structural change at the specified path
	 * 
	 * @param path
	 */
	void recordStructuralChange(IPath path);

	/**
	 * Returns the currently collected set of structural changes. The set is
	 * unmodifiable.
	 * 
	 * @return
	 */
	Set<IPath> getStructuralChanges();

}
