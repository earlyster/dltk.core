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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.collections.map.ReferenceMap;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IModelStatusConstants;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.core.environment.IFileHandle;
import org.eclipse.dltk.internal.core.util.Util;

public class SourceCodeCache implements IFileCacheManagement {

	private static final String ID = "org.eclipse.dltk.core.fileCache.default"; //$NON-NLS-1$

	/*
	 * @see org.eclipse.dltk.internal.core.ISourceCodeCache#getId()
	 */
	public String getId() {
		return ID;
	}

	private class ChangeListener implements IResourceChangeListener,
			IResourceDeltaVisitor {

		public void resourceChanged(IResourceChangeEvent event) {
			try {
				event.getDelta().accept(this);
			} catch (CoreException e) {
				if (DLTKCore.DEBUG) {
					e.printStackTrace();
				}
			}
		}

		public boolean visit(IResourceDelta delta) throws CoreException {
			final IResource resource = delta.getResource();
			if (resource.getType() == IResource.FILE) {
				if (isChanged(delta) || isRemoved(delta)) {
					removeFileEntry((IFile) resource);
				}
				return false;
			}
			return true;
		}
	};

	private static boolean isChanged(IResourceDelta delta) {
		return delta.getKind() == IResourceDelta.CHANGED
				&& (delta.getFlags() & (IResourceDelta.CONTENT | IResourceDelta.REPLACED)) != 0;
	}

	private static boolean isRemoved(IResourceDelta delta) {
		return delta.getKind() == IResourceDelta.REMOVED;
	}

	private IResourceChangeListener listener = new ChangeListener();

	protected void removeFileEntry(IFile resource) {
		synchronized (resourceMap) {
			resourceMap.remove(resource);
		}
	}

	private final ReferenceMap resourceMap = new ReferenceMap(
			ReferenceMap.HARD, ReferenceMap.SOFT);

	private static final long EXTERNAL_CACHE_LIFETIME = 10 * 60 * 1000;

	private static class ResourceCacheEntry {
		final byte[] content;
		final int charLength;

		public ResourceCacheEntry(int charLength, byte[] content) {
			this.charLength = charLength;
			this.content = content;
		}

	}

	private static class ExternalCacheEntry {
		final char[] content;
		final long addTime;

		/**
		 * @param result
		 * @param currentTimeMillis
		 */
		public ExternalCacheEntry(char[] result, long currentTimeMillis) {
			this.content = result;
			this.addTime = currentTimeMillis;
		}

	}

	private final ReferenceMap externalResourceMap = new ReferenceMap(
			ReferenceMap.HARD, ReferenceMap.SOFT);

	public void start() {
		DLTKCore.addPreProcessingResourceChangedListener(listener,
				IResourceChangeEvent.POST_CHANGE);
	}

	public void stop() {
		DLTKCore.removePreProcessingResourceChangedListener(listener);
	}

	public InputStream getContentsIfCached(IFile file) {
		final ResourceCacheEntry entry;
		synchronized (resourceMap) {
			entry = (ResourceCacheEntry) resourceMap.get(file);
		}
		if (entry != null) {
			return new ByteArrayInputStream(entry.content);
		} else {
			return null;
		}
	}

	public char[] get(IFile file) throws ModelException {
		final ResourceCacheEntry entry;
		synchronized (resourceMap) {
			entry = (ResourceCacheEntry) resourceMap.get(file);
		}
		// Get encoding from file
		String encoding = null;
		try {
			encoding = file.getCharset();
		} catch (CoreException ce) {
			// do not use any encoding
		}
		if (entry != null) {
			try {
				ByteArrayInputStream stream = new ByteArrayInputStream(
						entry.content);
				char[] result = org.eclipse.dltk.compiler.util.Util
						.getInputStreamAsCharArray(stream, entry.charLength,
								encoding);
				stream.close();
				return result;
			} catch (IOException e) {
				// should not happen actually
				throw new ModelException(e, IModelStatusConstants.IO_EXCEPTION);
			}
		}
		final byte[] content = Util.getResourceContentsAsByteArray(file);
		final char[] result;
		try {
			result = org.eclipse.dltk.compiler.util.Util
					.getInputStreamAsCharArray(
							new ByteArrayInputStream(content), content.length,
							encoding);
		} catch (IOException e) {
			// should not happen actually
			throw new ModelException(e, IModelStatusConstants.IO_EXCEPTION);
		}
		synchronized (resourceMap) {
			resourceMap.put(file,
					new ResourceCacheEntry(result.length, content));
		}
		return result;
	}

	public char[] get(IFileHandle file) throws ModelException {
		synchronized (externalResourceMap) {
			final ExternalCacheEntry entry = (ExternalCacheEntry) externalResourceMap
					.get(file);
			if (entry != null
					&& entry.addTime + EXTERNAL_CACHE_LIFETIME > System
							.currentTimeMillis()) {
				return entry.content;
			}
		}
		final char[] result = Util.getResourceContentsAsCharArrayNoCache(file);
		synchronized (externalResourceMap) {
			externalResourceMap.put(file, new ExternalCacheEntry(result, System
					.currentTimeMillis()));
		}
		return result;
	}

	public final void remove(IFile file) {
		removeFileEntry(file);
	}

	public void beginOperation() {
		// NOP
	}

	public void endOperation() {
		// NOP
	}

	public void clear() {
		synchronized (resourceMap) {
			resourceMap.clear();
		}
	}
}
