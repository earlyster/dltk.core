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
package org.eclipse.dltk.internal.core;

import java.io.InputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.core.environment.IFileHandle;

public interface ISourceCodeCache {

	/**
	 * Tests if there is cached content of the specified {@link IFile}. Returns
	 * the {@link InputStream} to read the content or <code>null</code> if the
	 * content of the specified file is not cached yet.
	 * 
	 * @param file
	 * @return
	 */
	InputStream getContentsIfCached(IFile file);

	/**
	 * Retrieves the content of the specified workspace {@link IFile}. Uses the
	 * cached content or loads the content from disk and save copy in cache.
	 * 
	 * @param file
	 * @return
	 * @throws ModelException
	 */
	char[] get(IFile file) throws ModelException;

	/**
	 * Retrieves the content of the specified external {@link IFileHandle}. Uses
	 * the cached content or loads the content from disk and save copy in cache.
	 * 
	 * @param file
	 * @return
	 */
	char[] get(IFileHandle file) throws ModelException;

	/**
	 * Removes the cached content for the specified workspace {@link IFile} from
	 * cache. The cache is listening to the workspaces changes, so it should not
	 * be necessary to manually clean it.
	 * 
	 * @param file
	 */
	void remove(IFile file);

	/**
	 * Changes caching mode to get better performance for resource intensive
	 * operations.
	 * 
	 * This method should be called at the beginning of the long operations,
	 * e.g. build. After the operation is completed the {@link #endOperation()}
	 * method should be called.
	 */
	void beginOperation();

	/**
	 * Restores caching mode back to the standard mode.
	 */
	void endOperation();

	void clear();
}
