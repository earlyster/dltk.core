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
import org.eclipse.dltk.core.environment.IEnvironment;
import org.eclipse.dltk.core.environment.IFileHandle;
import org.eclipse.dltk.core.environment.IFileStoreProvider;
import org.eclipse.dltk.core.internal.rse.perfomance.RSEPerfomanceStatistics;
import org.eclipse.dltk.core.internal.rse.ssh.RSESshManager;
import org.eclipse.dltk.ssh.core.ISshConnection;
import org.eclipse.dltk.ssh.core.ISshFileHandle;
import org.eclipse.rse.core.model.IHost;
import org.eclipse.rse.internal.efs.RSEFileSystem;

public class RSEFileHandle implements IFileHandle, IFileStoreProvider {
	private static final int SYMLINK_CONNECTION_TIMEOUT = 30 * 1000;
	private static final int CACHE_LIMIT = 1000;
	private static Map<RSEFileHandle, IFileInfo> timestamps = new HashMap<RSEFileHandle, IFileInfo>();
	private static Map<RSEFileHandle, Long> lastaccess = new HashMap<RSEFileHandle, Long>();

	private IFileStore file;
	private IEnvironment environment;
	private ISshFileHandle sshFile;

	/**
	 * @param infos
	 * @since 2.0
	 */
	public RSEFileHandle(IEnvironment env, IFileStore file, IFileInfo info) {
		this.environment = env;
		this.file = file;
		if (info != null) {
			timestamps.put(this, info);
		}
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
					sshFile = connection.getHandle(new Path(file.toURI()
							.getPath()));
				} catch (Exception e) {
					DLTKRSEPlugin.log("Failed to locate direct ssh connection",
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
		this(env, RSEFileSystem.getInstance().getStore(locationURI),
				(IFileInfo) null);
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
		if (timestamps.size() > CACHE_LIMIT) {
			timestamps.clear();
			lastaccess.clear();
		}
		boolean flag = !environment.isLocal();
		long c = 0;
		if (flag && !force) {
			if (timestamps.containsKey(this)) {
				c = System.currentTimeMillis();
				Long last = lastaccess.get(this);
				if (last != null && (c - last.longValue()) < 1000 * 10) {
					return timestamps.get(this);
				}
			}
		}
		IFileInfo info = file.fetchInfo();
		if (flag) {
			timestamps.put(this, info);
			if (c == 0) {
				c = System.currentTimeMillis();
			}
			lastaccess.put(this, c);
		}
		return info;
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
				childURI = new URI(toURI().toString() + "/" + childname);
				return new RSEFileHandle(environment, childURI);
			} catch (URISyntaxException e) {
				DLTKRSEPlugin.log(e);
			}
		}
		fetchSshFile();
		IFileStore childStore = file.getChild(new Path(childname));
		if (sshFile != null) {
			return new RSEFileHandle(environment, childStore, sshFile
					.getChild(childname));
		}
		return new RSEFileHandle(environment, childStore, (IFileInfo) null);
	}

	public IFileHandle[] getChildren() {
		if (!environment.connect()) {
			return null;
		}
		fetchSshFile();
		if (sshFile != null) {
			try {
				ISshFileHandle[] children = sshFile
						.getChildren(new NullProgressMonitor());
				IFileHandle rseChildren[] = new IFileHandle[children.length];
				for (int i = 0; i < children.length; i++) {
					IFileStore childStore = file.getChild(new Path(children[i]
							.getName()));
					rseChildren[i] = new RSEFileHandle(environment, childStore,
							children[i]);
				}
				return rseChildren;
			} catch (CoreException e) {
				DLTKRSEPlugin.log(e);
			}
		}
		try {
			IFileInfo[] infos = file.childInfos(EFS.NONE,
					new NullProgressMonitor());

			// IFileStore[] files = file.childStores(EFS.NONE,
			// new NullProgressMonitor());
			IFileHandle[] children = new IFileHandle[infos.length];
			long c = System.currentTimeMillis();
			for (int i = 0; i < infos.length; i++) {
				children[i] = new RSEFileHandle(environment, file
						.getFileStore(new Path(infos[i].getName())), infos[i]);
				timestamps.put((RSEFileHandle) children[i], infos[i]);
				lastaccess.put((RSEFileHandle) children[i], c);
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
		return new RSEFileHandle(environment, parent, (IFileInfo) null);
	}

	public IPath getPath() {
		return new Path(file.toURI().getPath());
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
		long startTime = System.currentTimeMillis();
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
		timestamps.clear();
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

	public boolean equals(Object obj) {
		if (obj instanceof RSEFileHandle) {
			RSEFileHandle anotherFile = (RSEFileHandle) obj;
			return this.file.equals(anotherFile.file);
		}
		return false;
	}

	public int hashCode() {
		return file.hashCode();
	}

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
		p.done("#", "Return file timestamp", 0);
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

	private final class CountStream extends BufferedInputStream {
		private InputStream stream;

		public CountStream(InputStream stream) {
			super(stream);
		}

		public int read() throws IOException {
			int read = stream.read();
			if (read != -1) {
				RSEPerfomanceStatistics
						.inc(RSEPerfomanceStatistics.TOTAL_BYTES_RECEIVED);
			}
			return read;
		}

		public int read(byte[] b, int off, int len) throws IOException {
			int read = this.stream.read(b, off, len);
			if (read != -1) {
				RSEPerfomanceStatistics.inc(
						RSEPerfomanceStatistics.TOTAL_BYTES_RECEIVED, read);
			}
			return read;
		}

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
		timestamps.remove(toString());
	}

	/**
	 * @since 2.0
	 */
	public String getResolveCanonicalPath() {
		if (environment.connect()) {
			// Try to resolve canonical path using direct ssh connection
			if (isSymlink()) {
				if (sshFile != null) {
					String link = sshFile.readLink();
					if (link.startsWith("/")) {
						IFileHandle handle = environment
								.getFile(new Path(link));
						return handle.getCanonicalPath();
					} else {
						String canonicalPath = environment.getFile(
								getPath().removeLastSegments(1).append(link))
								.getCanonicalPath();
						return canonicalPath;
					}
				}
			} else {
				return file.toURI().getPath();
			}

			IFileInfo info = file.fetchInfo();
			if (info != null && info.getAttribute(EFS.ATTRIBUTE_SYMLINK)) {
				String linkTarget = info
						.getStringAttribute(EFS.ATTRIBUTE_LINK_TARGET);
				IFileStore resolved = file.getFileStore(new Path(linkTarget));
				return resolved.toURI().getPath();
			} else {
				return file.toURI().getPath();
			}
		}
		return null;
	}
}
