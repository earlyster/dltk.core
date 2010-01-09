package org.eclipse.dltk.ssh.internal.core;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.dltk.ssh.core.ISshFileHandle;

import com.jcraft.jsch.SftpATTRS;
import com.jcraft.jsch.ChannelSftp.LsEntry;

public class SshFileHandle implements ISshFileHandle,
		IOutputStreamCloseListener {
	private static final int CACHE_LIMIT = 1000;

	private static class CacheEntry {
		final SftpATTRS attrs;
		final long lastAccess;

		public CacheEntry(SftpATTRS attrs, long lastAccess) {
			this.attrs = attrs;
			this.lastAccess = lastAccess;
		}

	}

	private static final Map<SshFileHandle, CacheEntry> attrCache = new HashMap<SshFileHandle, CacheEntry>();

	private SshConnection connection = null;
	private IPath path;
	// private IPath linkTarget;
	private SftpATTRS attrs;
	private Map<String, SshFileHandle> children = new HashMap<String, SshFileHandle>();
	private boolean childrenFetched = false;

	public SshFileHandle(SshConnection connection, IPath path, SftpATTRS attrs) {
		this.connection = connection;
		this.path = path;
		this.attrs = attrs;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.dltk.ssh.internal.core.ISshFileHandle#createFolder(java.lang
	 * .String, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public ISshFileHandle createFolder(String newEntryName,
			IProgressMonitor monitor) throws CoreException {
		ISshFileHandle child = getChild(newEntryName);
		if (child != null) {
			child.mkdir();
			fetchAttrs();
		}
		return child;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.dltk.ssh.internal.core.ISshFileHandle#mkdir()
	 */
	public void mkdir() {
		connection.mkdir(path);
		cleanAttrs();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.dltk.ssh.internal.core.ISshFileHandle#delete()
	 */
	public void delete() throws CoreException {
		fetchAttrs();
		if (attrs != null) {
			connection.delete(path, attrs.isDir());
			cleanAttrs();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.dltk.ssh.internal.core.ISshFileHandle#exists()
	 */
	public boolean exists() {
		fetchAttrs();
		return attrs != null;
	}

	private void fetchAttrs() {
		fetchAttrs(false);
	}

	private void cleanAttrs() {
		attrs = null;
		synchronized (attrCache) {
			attrCache.remove(this);
		}
	}

	private void fetchAttrs(boolean clean) {
		if (attrs == null || clean) {
			attrs = fetchCacheAttrs(clean);
		}
		if (attrs != null && attrs.isLink()) {
			attrs = fetchCacheAttrs(true);
			// this.linkTarget = connection.getResolvedPath(path);
		}
	}

	private SftpATTRS fetchCacheAttrs(boolean clean) {
		long c = 0;
		synchronized (attrCache) {
			if (attrCache.size() > CACHE_LIMIT) {
				attrCache.clear();
			} else if (!clean) {
				final CacheEntry entry = attrCache.get(this);
				if (entry != null) {
					c = System.currentTimeMillis();
					if ((c - entry.lastAccess) < 1000 * 10) {
						return entry.attrs;
					}
				}
			}
		}
		SftpATTRS attrs = connection.getAttrs(path);
		if (c == 0) {
			c = System.currentTimeMillis();
		}
		synchronized (attrCache) {
			attrCache.put(this, new CacheEntry(attrs, c));
		}
		return attrs;
	}

	public synchronized ISshFileHandle getChild(String newEntryName) {
		if (children.containsKey(newEntryName)) {
			return children.get(newEntryName);
		}
		ISshFileHandle child = new SshFileHandle(connection, path
				.append(newEntryName), null);
		return child;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.dltk.ssh.internal.core.ISshFileHandle#getChildren(org.eclipse
	 * .core.runtime.IProgressMonitor)
	 */
	public synchronized ISshFileHandle[] getChildren(IProgressMonitor monitor)
			throws CoreException {
		if (!childrenFetched) {
			// Fetch all child handles
			fetchChildren();
		}
		return children.values().toArray(new SshFileHandle[children.size()]);
	}

	private void fetchChildren() {
		Vector<LsEntry> list = connection.list(path);
		if (list != null) {
			children.clear();
			long c = System.currentTimeMillis();
			for (LsEntry entry : list) {
				String filename = entry.getFilename();
				if (filename.equals(".") || filename.equals("..")) { //$NON-NLS-1$ //$NON-NLS-2$
					continue;
				}
				final SftpATTRS childAttrs = entry.getAttrs();
				final IPath childPath;
				if (filename.indexOf(IPath.DEVICE_SEPARATOR) == -1) {
					childPath = path.append(filename);
				} else {
					// this way DEVICE_SEPARATOR is kept in path segment
					childPath = path.append(new Path(null, filename));
				}
				SshFileHandle childHandle = new SshFileHandle(connection,
						childPath, childAttrs);
				synchronized (attrCache) {
					attrCache.put(childHandle, new CacheEntry(childAttrs, c));
				}
				children.put(filename, childHandle);
			}
			childrenFetched = true;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.dltk.ssh.internal.core.ISshFileHandle#getInputStream(org.
	 * eclipse.core.runtime.IProgressMonitor)
	 */
	public InputStream getInputStream(IProgressMonitor monitor)
			throws CoreException {
		// fetchAttrs();
		// if (attrs != null) {
		// IPath current = this.path;
		// if (attrs.isLink() && linkTarget != null) {
		// current = linkTarget;
		// }
		final InputStream stream = connection.get(this.path);
		// TODO throw/wrap original exception?
		return stream;
		// }
		// return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.dltk.ssh.internal.core.ISshFileHandle#getName()
	 */
	public String getName() {
		return path.lastSegment();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.dltk.ssh.core.ISshFileHandle#getPath()
	 */
	public IPath getPath() {
		return path;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return path.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.dltk.ssh.internal.core.ISshFileHandle#getOutputStream(org
	 * .eclipse.core.runtime.IProgressMonitor)
	 */
	public OutputStream getOutputStream(IProgressMonitor monitor)
			throws CoreException {
		final OutputStream stream = connection.put(this.path, this);
		return stream;
	}

	public void streamClosed() {
		cleanAttrs();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.dltk.ssh.internal.core.ISshFileHandle#isDirectory()
	 */
	public boolean isDirectory() {
		fetchAttrs();
		if (attrs != null) {
			return attrs.isDir();
		} else {
			// IStatus status = new Status(IStatus.ERROR, "blah",
			// "Failed to retrive file information:" + path);
			// throw new CoreException(status);
			return false;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.dltk.ssh.internal.core.ISshFileHandle#lastModificationTime()
	 */
	public long lastModificationTime() {
		fetchAttrs();
		if (attrs != null) {
			return attrs.getMTime() * 1000L;
		} else {
			// IStatus status = new Status(IStatus.ERROR, "blah",
			// "Failed to retrive file information:" + path);
			// throw new CoreException(status);
			return 0;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.dltk.ssh.internal.core.ISshFileHandle#setLastModified(long,
	 * org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void setLastModified(long timestamp, IProgressMonitor monitor)
			throws CoreException {
		connection.setLastModified(path, timestamp);
		cleanAttrs();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.dltk.ssh.internal.core.ISshFileHandle#getSize()
	 */
	public long getSize() {
		fetchAttrs();
		if (attrs != null) {
			return attrs.getSize();
		}
		return 0;
	}

	public boolean isSymlink() {
		final SftpATTRS attrs = connection.getLAttrs(path);
		return attrs != null && attrs.isLink();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((connection == null) ? 0 : connection.hashCode());
		result = prime * result + ((path == null) ? 0 : path.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SshFileHandle other = (SshFileHandle) obj;
		if (connection == null) {
			if (other.connection != null)
				return false;
		} else if (!connection.equals(other.connection))
			return false;
		if (path == null) {
			if (other.path != null)
				return false;
		} else if (!path.equals(other.path))
			return false;
		return true;
	}

	public String readLink() {
		return connection.readLink(path);
	}

	public void move(IPath destination) throws CoreException {
		connection.move(path, destination);
	}
}
