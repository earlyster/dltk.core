package org.eclipse.dltk.ssh.internal.core;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.Vector;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.dltk.ssh.core.ISshConnection;
import org.eclipse.dltk.ssh.core.ISshFileHandle;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpATTRS;
import com.jcraft.jsch.SftpException;
import com.jcraft.jsch.ChannelSftp.LsEntry;

/**
 * TODO: Add correct operation synchronization.
 * 
 */
public class SshConnection extends ChannelPool implements ISshConnection {
	private long disabledTime = 0;

	private static abstract class Operation {
		boolean finished = false;

		public boolean isLongRunning() {
			return false;
		}

		public abstract void perform(ChannelSftp channel) throws SftpException;

		public void setFinished() {
			finished = true;
		}

		public boolean isFinished() {
			return finished;
		}

	}

	private static class GetStatOperation extends Operation {
		private IPath path;
		private SftpATTRS attrs;

		public GetStatOperation(IPath path) {
			this.path = path;
		}

		@Override
		public String toString() {
			return "Get information for file:" + path; //$NON-NLS-1$
		}

		@Override
		public void perform(ChannelSftp channel) throws SftpException {
			attrs = channel.stat(path.toString());
		}

		public SftpATTRS getAttrs() {
			return attrs;
		}
	}

	private static class ResolveLinkOperation extends Operation {
		private IPath path;
		private IPath resolvedPath;

		public ResolveLinkOperation(IPath path) {
			this.path = path;
		}

		@Override
		public String toString() {
			return "Resolve link information for file:" + path; //$NON-NLS-1$
		}

		@Override
		public void perform(ChannelSftp channel) throws SftpException {
			SftpATTRS attrs = channel.stat(path.toString());
			boolean isRoot = (path.segmentCount() == 0);
			String linkTarget = null;
			String canonicalPath;
			String parentPath = path.removeLastSegments(1).toString();
			if (attrs.isLink() && !isRoot) {
				try {
					String fullPath = path.toString();
					boolean readlinkDone = false;
					try {
						linkTarget = channel.readlink(fullPath);
						readlinkDone = true;
					} catch (Throwable t) {
						channel.cd(fullPath);
						linkTarget = channel.pwd();
						canonicalPath = linkTarget;
					}
					if (linkTarget != null && !linkTarget.equals(fullPath)) {
						if (readlinkDone) {
							String curdir = channel.pwd();
							if (!parentPath.equals(curdir)) {
								channel.cd(parentPath);
							}
						}
						SftpATTRS attrsTarget = channel.stat(linkTarget);
						if (readlinkDone && attrsTarget.isDir()) {
							channel.cd(fullPath);
							canonicalPath = channel.pwd();
						}
					} else {
						linkTarget = null;
					}
				} catch (Exception e) {
					if (e instanceof SftpException
							&& ((SftpException) e).id == ChannelSftp.SSH_FX_NO_SUCH_FILE) {
						if (linkTarget == null) {
							linkTarget = ":dangling link"; //$NON-NLS-1$
						} else {
							linkTarget = ":dangling link:" + linkTarget; //$NON-NLS-1$
						}
					}
				}
				resolvedPath = new Path(linkTarget);
			}
		}

		public IPath getResolvedPath() {
			return resolvedPath;
		}
	}

	private static final int STREAM_BUFFER_SIZE = 32000;

	private static interface StreamOperation {
		boolean isActiveCall();

		long getLastActivity();
	}

	private class GetOperation extends Operation implements StreamOperation {
		private IPath path;
		private GetOperationInputStream stream;

		public GetOperation(IPath path) {
			this.path = path;
		}

		@Override
		public boolean isLongRunning() {
			return true;
		}

		@Override
		public void perform(ChannelSftp channel) throws SftpException {
			stream = new GetOperationInputStream(channel.get(path.toString()),
					channel);
		}

		@Override
		public String toString() {
			return "Get input stream for file:" + path; //$NON-NLS-1$
		}

		public InputStream getStream() {
			return stream;
		}

		public boolean isActiveCall() {
			return stream != null && stream.activeCalls != 0;
		}

		public long getLastActivity() {
			if (stream != null) {
				return stream.lastActivity;
			} else {
				return Long.MIN_VALUE;
			}
		}

	}

	private class GetOperationInputStream extends BufferedInputStream {

		private final ChannelSftp channel;
		private int activeCalls;
		private long lastActivity;

		public GetOperationInputStream(InputStream in, ChannelSftp channel) {
			super(in, STREAM_BUFFER_SIZE);
			this.channel = channel;
			updateLastActivity();
		}

		@Override
		public void close() throws IOException {
			try {
				super.close();
			} finally {
				releaseChannel(channel);
			}
		}

		private void updateLastActivity() {
			lastActivity = System.currentTimeMillis();
		}

		private void beginActivity() {
			++activeCalls;
			updateLastActivity();
		}

		private void endActivity() {
			--activeCalls;
			updateLastActivity();
		}

		@Override
		public synchronized int read() throws IOException {
			beginActivity();
			try {
				return super.read();
			} finally {
				endActivity();
			}
		}

		@Override
		public int read(byte[] b) throws IOException {
			beginActivity();
			try {
				return super.read(b);
			} finally {
				endActivity();
			}
		}

		@Override
		public synchronized int read(byte[] b, int off, int len)
				throws IOException {
			beginActivity();
			try {
				return super.read(b, off, len);
			} finally {
				endActivity();
			}
		}

	}

	private class PutOperation extends Operation implements StreamOperation {
		private final IPath path;
		private final IOutputStreamCloseListener closeListener;
		private PutOperationOutputStream stream;

		public PutOperation(IPath path, IOutputStreamCloseListener closeListener) {
			this.path = path;
			this.closeListener = closeListener;
		}

		@Override
		public String toString() {
			return "Get output stream for file:" + path; //$NON-NLS-1$
		}

		@Override
		public boolean isLongRunning() {
			return true;
		}

		@Override
		public void perform(ChannelSftp channel) throws SftpException {
			final OutputStream rawStream = channel.put(path.toString(),
					ChannelSftp.OVERWRITE);
			stream = new PutOperationOutputStream(rawStream, channel,
					closeListener);
		}

		public OutputStream getStream() {
			return stream;
		}

		public boolean isActiveCall() {
			return stream != null && stream.activeCalls != 0;
		}

		public long getLastActivity() {
			if (stream != null) {
				return stream.lastActivity;
			} else {
				return Long.MIN_VALUE;
			}
		}

	}

	private class PutOperationOutputStream extends BufferedOutputStream {
		private final ChannelSftp channel;
		private final IOutputStreamCloseListener closeListener;
		private int activeCalls;
		private long lastActivity;

		public PutOperationOutputStream(OutputStream out, ChannelSftp channel,
				IOutputStreamCloseListener closeListener) {
			super(out, STREAM_BUFFER_SIZE);
			this.channel = channel;
			this.closeListener = closeListener;
			updateActivity();
		}

		@Override
		public void close() throws IOException {
			try {
				super.close();
				if (closeListener != null) {
					closeListener.streamClosed();
				}
			} finally {
				releaseChannel(channel);
			}
		}

		private void updateActivity() {
			lastActivity = System.currentTimeMillis();
		}

		private void beginActivity() {
			++activeCalls;
			updateActivity();
		}

		private void endActivity() {
			--activeCalls;
			updateActivity();
		}

		@Override
		public synchronized void write(int b) throws IOException {
			beginActivity();
			try {
				super.write(b);
			} finally {
				endActivity();
			}
		}

		@Override
		public void write(byte[] b) throws IOException {
			beginActivity();
			try {
				super.write(b);
			} finally {
				endActivity();
			}
		}

		@Override
		public synchronized void write(byte[] b, int off, int len)
				throws IOException {
			beginActivity();
			try {
				super.write(b, off, len);
			} finally {
				endActivity();
			}
		}

	}

	private static class ListFolderOperation extends Operation {
		private IPath path;
		private Vector<LsEntry> v;

		public ListFolderOperation(IPath path) {
			this.path = path;
		}

		@Override
		public String toString() {
			return "List folder:" + path + " for files"; //$NON-NLS-1$ //$NON-NLS-2$
		}

		@Override
		@SuppressWarnings("unchecked")
		public void perform(ChannelSftp channel) throws SftpException {
			v = channel.ls(path.toString());
		}

		public Vector<LsEntry> getVector() {
			return v;
		}
	}

	private static final int DEFAULT_RETRY_COUNT = 2;
	private static final long DEFAULT_ACQUIRE_TIMEOUT = 30 * 1000;
	private static final long DEFAULT_INACTIVITY_TIMEOUT = 60 * 1000;

	public SshConnection(String userName, String hostName, int port) {
		super(userName, hostName, port, DEFAULT_INACTIVITY_TIMEOUT);
	}

	public boolean connect() {
		try {
			final ChannelSftp channel = acquireChannel("connect()"); //$NON-NLS-1$
			try {
				return true;
			} finally {
				releaseChannel(channel);
			}
		} catch (JSchException e) {
			return false;
		}
	}

	private static boolean DEBUG = false;

	private void performOperation(final Operation op) {
		performOperation(op, DEFAULT_RETRY_COUNT);
	}

	private void performOperation(final Operation op, int tryCount) {
		final ChannelSftp channel = acquireChannel(op, DEFAULT_ACQUIRE_TIMEOUT);
		if (channel != null) {
			try {
				if (DEBUG) {
					log(" [do] " + op); //$NON-NLS-1$
				}
				op.perform(channel);
				op.setFinished();
			} catch (SftpException e) {
				if (e.id == ChannelSftp.SSH_FX_FAILURE
						&& e.getCause() instanceof JSchException) {
					Activator.log(e);
					destroyChannel(channel);
					disconnect();
					if (tryCount > 0) {
						performOperation(op, tryCount - 1);
					}
				} else if (e.id != ChannelSftp.SSH_FX_NO_SUCH_FILE) {
					if (e.id == ChannelSftp.SSH_FX_PERMISSION_DENIED) {
						Activator.log("Permission denied to perform:" //$NON-NLS-1$
								+ op.toString());
					} else {
						Activator.log(e);
					}
				}
			} finally {
				if (!op.isLongRunning()) {
					releaseChannel(channel);
				}
			}
		}
	}

	@Override
	protected boolean canClose(Object context) {
		return context instanceof StreamOperation;
	}

	@Override
	protected long getLastActivity(Object context) {
		if (context instanceof StreamOperation) {
			return ((StreamOperation) context).getLastActivity();
		} else {
			return super.getLastActivity(context);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.dltk.ssh.core.ISshConnection#getHandle(org.eclipse.core
	 * .runtime .IPath)
	 */
	public ISshFileHandle getHandle(IPath path) throws Exception {
		if (isDisabled()) {
			return null;
		}
		// GetStatOperation op = new GetStatOperation(path);
		// performOperation(op, DEFAULT_RETRY_COUNT);
		// if (op.isFinished()) {
		// return new SshFileHandle(this, path, op.getAttrs());
		// }
		return new SshFileHandle(this, path, null);
	}

	public boolean isDisabled() {
		return disabledTime > System.currentTimeMillis();
	}

	public void setDisabled(int timeout) {
		disabledTime = System.currentTimeMillis() + timeout;
	}

	SftpATTRS getAttrs(IPath path) {
		GetStatOperation op = new GetStatOperation(path);
		performOperation(op);
		if (op.isFinished()) {
			return op.getAttrs();
		}
		return null;
	}

	IPath getResolvedPath(IPath path) {
		ResolveLinkOperation op = new ResolveLinkOperation(path);
		performOperation(op);
		if (op.isFinished()) {
			return op.getResolvedPath();
		}
		return null;
	}

	Vector<LsEntry> list(IPath path) {
		ListFolderOperation op = new ListFolderOperation(path);
		performOperation(op);
		if (op.isFinished()) {
			return op.getVector();
		}
		return null;
	}

	void setLastModified(final IPath path, final long timestamp) {
		Operation op = new Operation() {
			@Override
			public void perform(ChannelSftp channel) throws SftpException {
				Date date = new Date(timestamp);
				System.out.println(date.toString());
				channel.setMtime(path.toString(), (int) (timestamp / 1000L));
			}

			@Override
			public String toString() {
				return "setLastModified " + path; //$NON-NLS-1$
			}
		};
		performOperation(op);
	}

	void delete(final IPath path, final boolean dir) {
		Operation op = new Operation() {
			@Override
			public void perform(ChannelSftp channel) throws SftpException {
				if (!dir) {
					channel.rm(path.toString());
				} else {
					channel.rmdir(path.toString());
				}
			}

			@Override
			public String toString() {
				return "delete " + path; //$NON-NLS-1$
			}
		};
		performOperation(op);
	}

	void mkdir(final IPath path) {
		Operation op = new Operation() {
			@Override
			public void perform(ChannelSftp channel) throws SftpException {
				channel.mkdir(path.toString());
			}

			@Override
			public String toString() {
				return "mkdir " + path; //$NON-NLS-1$
			}
		};
		performOperation(op);
	}

	InputStream get(IPath path) {
		GetOperation op = new GetOperation(path);
		performOperation(op);
		if (op.isFinished()) {
			return op.getStream();
		}
		return null;
	}

	OutputStream put(IPath path) {
		return put(path, null);
	}

	OutputStream put(IPath path, IOutputStreamCloseListener closeListener) {
		PutOperation op = new PutOperation(path, closeListener);
		performOperation(op);
		if (op.isFinished()) {
			return op.getStream();
		}
		return null;
	}

}
