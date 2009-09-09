package org.eclipse.dltk.debug.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.IStreamListener;
import org.eclipse.debug.core.model.IFlushableStreamMonitor;
import org.eclipse.debug.core.model.IStreamMonitor;
import org.eclipse.debug.core.model.IStreamsProxy;
import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.debug.ui.console.IConsoleColorProvider;
import org.eclipse.dltk.launching.process.IScriptProcess;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.console.IOConsole;
import org.eclipse.ui.console.IOConsoleOutputStream;

public class ScriptDebugConsole extends IOConsole {
	private ILaunch launch;
	private final IConsoleColorProvider fColorProvider;

	public ILaunch getLaunch() {
		return launch;
	}

	public void setLaunch(ILaunch launch) {
		this.launch = launch;
	}

	public ScriptDebugConsole(String name, ImageDescriptor imageDescriptor,
			String encoding, IConsoleColorProvider colorProvider) {
		super(name, null, imageDescriptor, encoding, true);
		this.fColorProvider = colorProvider;

		this.addPatternMatchListener(new ScriptDebugConsoleTraceTracker());
	}

	@Override
	public void matcherFinished() {
		super.matcherFinished();
	}

	@Override
	public void partitionerFinished() {
		super.partitionerFinished();
	}

	/*
	 * Increase visibility
	 */
	@Override
	protected void setName(String name) {
		super.setName(name);
	}

	@Override
	protected void dispose() {
		closeStreams();
		disposeStreams();
		super.dispose();
	}

	private Set<IScriptProcess> connectedProcesses;

	/**
	 * @param process
	 */
	public synchronized void connect(IScriptProcess process) {
		if (connectedProcesses == null) {
			connectedProcesses = new HashSet<IScriptProcess>();
		}
		if (connectedProcesses.add(process)) {
			final IStreamsProxy proxy = process.getScriptStreamsProxy();
			if (proxy == null) {
				return;
			}
			connect(proxy);
		}
	}

	public void connect(final IStreamsProxy proxy) {
		IStreamMonitor streamMonitor = proxy.getErrorStreamMonitor();
		if (streamMonitor != null) {
			connect(streamMonitor, IDebugUIConstants.ID_STANDARD_ERROR_STREAM);
		}
		streamMonitor = proxy.getOutputStreamMonitor();
		if (streamMonitor != null) {
			connect(streamMonitor, IDebugUIConstants.ID_STANDARD_OUTPUT_STREAM);
		}
	}

	private List<StreamListener> fStreamListeners = new ArrayList<StreamListener>();

	/**
	 * @param streamMonitor
	 * @param idStandardErrorStream
	 */
	private void connect(IStreamMonitor streamMonitor, String streamIdentifier) {
		synchronized (streamMonitor) {
			IOConsoleOutputStream stream = newOutputStream();
			stream.setColor(fColorProvider.getColor(streamIdentifier));
			StreamListener listener = new StreamListener(streamMonitor, stream);
			fStreamListeners.add(listener);
		}
	}

	/**
	 * cleanup method to close all of the open stream to this console
	 */
	private synchronized void closeStreams() {
		for (StreamListener listener : fStreamListeners) {
			listener.closeStream();
		}
	}

	/**
	 * disposes of the listeners for each of the stream associated with this
	 * console
	 */
	private synchronized void disposeStreams() {
		for (StreamListener listener : fStreamListeners) {
			listener.dispose();
		}
	}

	/**
	 * This class listens to a specified IO stream
	 */
	private class StreamListener implements IStreamListener {
		private IOConsoleOutputStream fStream;
		private IStreamMonitor fStreamMonitor;
		private boolean fFlushed = false;
		private boolean fListenerRemoved = false;

		public StreamListener(IStreamMonitor monitor,
				IOConsoleOutputStream stream) {
			this.fStream = stream;
			this.fStreamMonitor = monitor;
			fStreamMonitor.addListener(this);
			// fix to bug 121454. Ensure that output to fast processes is
			// processed.
			streamAppended(null, monitor);
		}

		public void streamAppended(String text, IStreamMonitor monitor) {
			String encoding = getEncoding();
			if (fFlushed) {
				try {
					if (fStream != null) {
						if (encoding == null)
							fStream.write(text);
						else
							fStream.write(text.getBytes(encoding));
					}
				} catch (IOException e) {
					DLTKDebugUIPlugin.log(e);
				}
			} else {
				String contents = null;
				synchronized (fStreamMonitor) {
					fFlushed = true;
					contents = fStreamMonitor.getContents();
					if (fStreamMonitor instanceof IFlushableStreamMonitor) {
						IFlushableStreamMonitor m = (IFlushableStreamMonitor) fStreamMonitor;
						m.flushContents();
						m.setBuffered(false);
					}
				}
				try {
					if (contents != null && contents.length() > 0) {
						if (fStream != null) {
							fStream.write(contents);
						}
					}
				} catch (IOException e) {
					DLTKDebugUIPlugin.log(e);
				}
			}
		}

		public void closeStream() {
			if (fStreamMonitor == null) {
				return;
			}
			synchronized (fStreamMonitor) {
				fStreamMonitor.removeListener(this);
				if (!fFlushed) {
					String contents = fStreamMonitor.getContents();
					streamAppended(contents, fStreamMonitor);
				}
				fListenerRemoved = true;
			}
		}

		public void dispose() {
			if (!fListenerRemoved) {
				closeStream();
			}
			fStreamMonitor = null;
			fStream = null;
		}
	}

}
