/*******************************************************************************
 * Copyright (c) 2008 xored software, Inc.  
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html  
 *
 * Contributors:
 *     xored software, Inc. - initial API and Implementation (Andrei Sobolev)
 *******************************************************************************/
package org.eclipse.dltk.core.internal.rse;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileInfo;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.RuntimePerformanceMonitor;
import org.eclipse.dltk.core.RuntimePerformanceMonitor.PerformanceNode;
import org.eclipse.dltk.core.environment.EnvironmentPathUtils;
import org.eclipse.dltk.core.environment.FileHandles;
import org.eclipse.dltk.core.environment.IEnvironment;
import org.eclipse.dltk.core.environment.IFileHandle;
import org.eclipse.dltk.core.environment.IFileStoreProvider;
import org.eclipse.dltk.core.internal.rse.perfomance.RSEPerfomanceStatistics;
import org.eclipse.dltk.core.internal.rse.ssh.RSESshManager;
import org.eclipse.dltk.ssh.core.ISshConnection;
import org.eclipse.dltk.ssh.core.ISshFileHandle;
import org.eclipse.rse.core.model.IHost;

public class RSEFileHandle implements IFileHandle, IFileStoreProvider {
	private static final int SYMLINK_CONNECTION_TIMEOUT = 30 * 1000;
	private static final int CACHE_LIMIT = 1000;
	private static final long CACHE_ENTRY_LIFETIME = 10 * 1000;

	private static class CacheEntry {
		final IFileInfo fileInfo;
		final long timestamp;

		public CacheEntry(IFileInfo fileInfo, long timestamp) {
			this.fileInfo = fileInfo;
			this.timestamp = timestamp;
		}

	}

	private static final Map<IFileStore, CacheEntry> cache = new HashMap<IFileStore, CacheEntry>();

	private final IFileStore file;
	private final IEnvironment environment;
	private ISshFileHandle sshFile;

	/**
	 * @param infos
	 * @since 2.0
	 */
	public RSEFileHandle(IEnvironment env, IFileStore file) {
		this.environment = env;
		this.file = file;
	}

	private void fetchSshFile() {
		if (sshFile != null) {
			return;
		}
		if (environment instanceof RSEEnvironment) {
			RSEEnvironment rseEnv = (RSEEnvironment) environment;
			IHost host = rseEnv.getHost();
			ISshConnection connection = RSESshManager.getConnection(host);
			if (connection != null) { // This is ssh connection, and it's alive.
				try {
					sshFile = connection.getHandle(new Path(getPathString()));
				} catch (Exception e) {
					DLTKRSEPlugin.log("Failed to locate direct ssh connection", //$NON-NLS-1$
							e);
				}
			}
		}
	}

	/**
	 * @since 2.0
	 */
	public RSEFileHandle(IEnvironment env, IFileStore file,
			ISshFileHandle sshFile) {
		this.environment = env;
		this.file = file;
		this.sshFile = sshFile;
	}

	public RSEFileHandle(IEnvironment env, URI locationURI) {
		this(env, RSEEnvironment.getStoreFor(locationURI));
	}

	public boolean exists() {
		if (!environment.connect()) {
			return false;
		}
		fetchSshFile();
		if (sshFile != null) {
			return sshFile.exists();
		}
		try {
			return fetchInfo(false).exists();
		} catch (RuntimeException e) {
			return false;
		}
	}

	private IFileInfo fetchInfo(boolean force) {
		final boolean isRemote = !environment.isLocal();
		long now = 0;
		if (isRemote && !force) {
			CacheEntry entry;
			synchronized (cache) {
				entry = cache.get(getCacheKey());
			}
			if (entry != null) {
				now = System.currentTimeMillis();
				if (now - entry.timestamp < CACHE_ENTRY_LIFETIME) {
					return entry.fileInfo;
				}
			}
		}
		final IFileInfo info = file.fetchInfo();
		if (isRemote) {
			if (now == 0) {
				now = System.currentTimeMillis();
			}
			synchronized (cache) {
				checkCacheLimit();
				cache.put(getCacheKey(), new CacheEntry(info, now));
			}
		}
		return info;
	}

	private static void checkCacheLimit() {
		if (cache.size() > CACHE_LIMIT) {
			cache.clear();
		}
	}

	/**
	 * @return
	 */
	private final IFileStore getCacheKey() {
		return file;
	}

	public String toOSString() {
		return this.environment.convertPathToString(getPath());
	}

	public String getCanonicalPath() {
		return this.environment.getCanonicalPath(getPath());
	}

	public IFileHandle getChild(final String childname) {
		if (!environment.connect()) {
			URI childURI;
			try {
				childURI = new URI(toURI().toString() + "/" + childname); //$NON-NLS-1$
				return new RSEFileHandle(environment, childURI);
			} catch (URISyntaxException e) {
				DLTKRSEPlugin.log(e);
			}
		}
		fetchSshFile();
		IFileStore childStore = file.getChild(childname);
		if (sshFile != null) {
			return new RSEFileHandle(environment, childStore, sshFile
					.getChild(childname));
		}
		return new RSEFileHandle(environment, childStore);
	}

	public IFileHandle[] getChildren() {
		if (!environment.connect()) {
			return null;
		}
		fetchSshFile();
		if (sshFile != null) {
			try {
				final ISshFileHandle[] children = sshFile
						.getChildren(new NullProgressMonitor());
				final IFileHandle rseChildren[] = new IFileHandle[children.length];
				for (int i = 0; i < children.length; i++) {
					final ISshFileHandle child = children[i];
					final IFileStore childStore = file
							.getChild(child.getName());
					rseChildren[i] = new RSEFileHandle(environment, childStore,
							child);
				}
				return rseChildren;
			} catch (CoreException e) {
				DLTKRSEPlugin.log(e);
			}
		}
		try {
			final IFileInfo[] infos = file.childInfos(EFS.NONE,
					new NullProgressMonitor());
			if (infos.length != 0) {
				synchronized (cache) {
					checkCacheLimit();
				}
			}
			final IFileHandle[] children = new IFileHandle[infos.length];
			final long now = System.currentTimeMillis();
			for (int i = 0; i < infos.length; i++) {
				final IFileInfo childInfo = infos[i];
				children[i] = new RSEFileHandle(environment, file
						.getChild(childInfo.getName()));
				final IFileStore childCacheKey = ((RSEFileHandle) children[i])
						.getCacheKey();
				synchronized (cache) {
					cache.put(childCacheKey, new CacheEntry(childInfo, now));
				}
			}
			return children;
		} catch (CoreException e) {
			if (DLTKCore.DEBUG)
				e.printStackTrace();
			return null;
		}
	}

	public IEnvironment getEnvironment() {
		return environment;
	}

	public URI toURI() {
		return file.toURI();
	}

	public String getName() {
		return file.getName();
	}

	public IFileHandle getParent() {
		IFileStore parent = file.getParent();
		if (parent == null)
			return null;
		return new RSEFileHandle(environment, parent);
	}

	public IPath getPath() {
		return new Path(getPathString());
	}

	private String getPathString() {
		return file.toURI().getPath();
	}

	public boolean isDirectory() {
		if (!environment.connect()) {
			return false;
		}
		fetchSshFile();
		if (sshFile != null) {
			return sshFile.isDirectory();
		}
		return fetchInfo(false).isDirectory();
	}

	public boolean isFile() {
		if (!environment.connect()) {
			return false;
		}
		fetchSshFile();
		if (sshFile != null) {
			return sshFile.exists() && !sshFile.isDirectory();
		}
		final IFileInfo info = fetchInfo(false);
		return info.exists() && !info.isDirectory();
	}

	public boolean isSymlink() {
		if (!environment.connect()) {
			return false;
		}
		fetchSshFileWait();
		if (sshFile != null) {
			return sshFile.isSymlink();
		}
		return fetchInfo(false).getAttribute(EFS.ATTRIBUTE_SYMLINK);
	}

	private void fetchSshFileWait() {
		final long startTime = System.currentTimeMillis();
		while (sshFile == null
				&& (System.currentTimeMillis() - startTime < SYMLINK_CONNECTION_TIMEOUT)) {
			fetchSshFile();
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				if (DLTKCore.DEBUG) {
					e.printStackTrace();
				}
			}
		}
	}

	private InputStream internalOpenInputStream(IProgressMonitor monitor)
			throws IOException {
		if (!environment.connect()) {
			return null;
		}
		fetchSshFile();
		if (sshFile != null) {
			try {
				return sshFile.getInputStream(monitor);
			} catch (CoreException e) {
				throw new IOException(e.getLocalizedMessage());
			}
		}
		try {
			return file.openInputStream(EFS.NONE, monitor);
		} catch (CoreException e) {
			if (DLTKCore.DEBUG)
				e.printStackTrace();
			throw new IOException(e.getLocalizedMessage());
		}
	}

	public InputStream openInputStream(IProgressMonitor monitor)
			throws IOException {
		if (!environment.connect()) {
			return null;
		}
		if (RSEPerfomanceStatistics.PERFOMANCE_TRACING) {
			return new CountStream(this.internalOpenInputStream(monitor));
		}
		return this.internalOpenInputStream(monitor);
	}

	public OutputStream openOutputStream(IProgressMonitor monitor)
			throws IOException {
		if (!environment.connect()) {
			return null;
		}
		synchronized (cache) {
			cache.clear();
		}
		fetchSshFile();
		if (sshFile != null) {
			try {
				return sshFile.getOutputStream(monitor);
			} catch (CoreException e) {
				throw new IOException(e.getLocalizedMessage());
			}
		}
		try {
			return new BufferedOutputStream(file.openOutputStream(EFS.NONE,
					monitor)) {
				@Override
				public void close() throws IOException {
					super.close();
					clearLastModifiedCache();
				}
			};
		} catch (CoreException e) {
			if (DLTKCore.DEBUG)
				e.printStackTrace();
			throw new IOException(e.getLocalizedMessage());
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof RSEFileHandle) {
			RSEFileHandle anotherFile = (RSEFileHandle) obj;
			return this.file.equals(anotherFile.file);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return file.hashCode();
	}

	@Override
	public String toString() {
		return toOSString();
	}

	public long lastModified() {
		if (!environment.connect()) {
			return 0;
		}
		fetchSshFile();
		PerformanceNode p = RuntimePerformanceMonitor.begin();
		long lm = 0;
		if (sshFile != null) {
			lm = sshFile.lastModificationTime();
		} else {
			lm = fetchInfo(false).getLastModified();
		}
		p.done("#", "Return file timestamp", 0); //$NON-NLS-1$//$NON-NLS-2$
		return lm;

	}

	public long length() {
		if (!environment.connect()) {
			return 0;
		}
		fetchSshFile();
		if (sshFile != null) {
			return sshFile.getSize();
		}
		return fetchInfo(false).getLength();
	}

	public IPath getFullPath() {
		return EnvironmentPathUtils.getFullPath(environment, getPath());
	}

	public String getEnvironmentId() {
		return environment.getId();
	}

	private static final class CountStream extends BufferedInputStream {
		private InputStream stream;

		public CountStream(InputStream stream) {
			super(stream);
		}

		@Override
		public int read() throws IOException {
			int read = stream.read();
			if (read != -1) {
				RSEPerfomanceStatistics
						.inc(RSEPerfomanceStatistics.TOTAL_BYTES_RECEIVED);
			}
			return read;
		}

		@Override
		public int read(byte[] b, int off, int len) throws IOException {
			int read = this.stream.read(b, off, len);
			if (read != -1) {
				RSEPerfomanceStatistics.inc(
						RSEPerfomanceStatistics.TOTAL_BYTES_RECEIVED, read);
			}
			return read;
		}

		@Override
		public int read(byte[] b) throws IOException {
			int read = this.stream.read(b);
			if (read != -1) {
				RSEPerfomanceStatistics.inc(
						RSEPerfomanceStatistics.TOTAL_BYTES_RECEIVED, read);
			}
			return read;
		}
	}

	/**
	 * @since 2.0
	 */
	public IFileStore getFileStore() {
		return this.file;
	}

	/**
	 * Removes saved timestamp for this element.
	 * 
	 * @since 2.0
	 */
	public void clearLastModifiedCache() {
		synchronized (cache) {
			cache.remove(getCacheKey());
		}
	}

	/**
	 * @since 2.0
	 */
	public String resolvePath() {
		final String currentPath = getPathString();
		if (environment.connect() && isSymlink()) {
			// Try to resolve canonical path using direct ssh connection
			if (sshFile != null) {
				final String link = sshFile.readLink();
				if (link != null) {
					if (link.startsWith("/")) { //$NON-NLS-1$
						if (!link.equals(currentPath)) {
							return environment.getFile(new Path(link))
									.getCanonicalPath();
						}
					} else {
						final IPath fullLink = getPath().removeLastSegments(1)
								.append(link);
						if (!currentPath.equals(fullLink.toString())) {
							return environment.getFile(fullLink)
									.getCanonicalPath();
						}
					}
				}
			} else {
				final IFileInfo info = file.fetchInfo();
				if (info != null && info.getAttribute(EFS.ATTRIBUTE_SYMLINK)) {
					final String linkTarget = info
							.getStringAttribute(EFS.ATTRIBUTE_LINK_TARGET);
					if (linkTarget != null && !currentPath.equals(linkTarget)) {
						final Path link = new Path(linkTarget);
						final IFileStore resolved;
						if (link.isAbsolute()) {
							resolved = file.getFileSystem().getStore(link);
						} else {
							resolved = file.getFileStore(link);
						}
						return resolved.toURI().getPath();
					}
				}
			}
		}
		return currentPath;
	}

	public void move(IFileHandle destination) throws CoreException {
		fetchSshFile();
		if (sshFile != null) {
			sshFile.move(FileHandles.asPath(destination, environment));
		} else {
			file
					.move(FileHandles.asFileStore(destination), EFS.OVERWRITE,
							null);
		}
	}
}
