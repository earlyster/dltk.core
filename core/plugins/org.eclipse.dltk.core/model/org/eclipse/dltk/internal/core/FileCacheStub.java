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
import org.eclipse.core.runtime.CoreException;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.core.environment.IFileHandle;
import org.eclipse.dltk.internal.core.util.Util;

/**
 * Implementation of the {@link IFileCache} without any caching.
 */
public class FileCacheStub implements IFileCache {

	public static final String ID = "org.eclipse.dltk.core.fileCache.nop"; //$NON-NLS-1$

	public String getId() {
		return ID;
	}

	public InputStream getContentsIfCached(IFile file) {
		return null;
	}

	public char[] get(IFile file) throws ModelException {
		// Get encoding from file
		String encoding = null;
		try {
			encoding = file.getCharset();
		} catch (CoreException ce) {
			// do not use any encoding
		}
		return Util.getResourceContentsAsCharArray(file, encoding);
	}

	public char[] get(IFileHandle file) throws ModelException {
		return Util.getResourceContentsAsCharArrayNoCache(file);
	}

	public final void remove(IFile file) {
		// NOP
	}

	public void beginOperation() {
		// NOP
	}

	public void endOperation() {
		// NOP
	}

	public void clear() {
		// NOP
	}
}
