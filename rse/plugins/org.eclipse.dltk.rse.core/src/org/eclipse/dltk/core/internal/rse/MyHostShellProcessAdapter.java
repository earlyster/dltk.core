/*******************************************************************************
 * Copyright (c) 2006 PalmSource, Inc.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Ewa Matejska (PalmSource) - initial version
 * Martin Oberhuber (Wind River) - adapt to IHostOutput API (bug 161773, 158312)
 * Martin Oberhuber (Wind River) - moved from org.eclipse.rse.remotecdt (bug 161777)
 * Martin Oberhuber (Wind River) - renamed from HostShellAdapter (bug 161777)
 * Martin Oberhuber (Wind River) - improved Javadoc
 *******************************************************************************/

package org.eclipse.dltk.core.internal.rse;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.environment.IExecutionLogger;
import org.eclipse.rse.services.shells.HostShellOutputStream;
import org.eclipse.rse.services.shells.IHostOutput;
import org.eclipse.rse.services.shells.IHostShell;
import org.eclipse.rse.services.shells.IHostShellChangeEvent;
import org.eclipse.rse.services.shells.IHostShellOutputListener;
import org.eclipse.rse.services.shells.IHostShellOutputReader;

/**
 * This class represents a host shell process. It does not represent one process
 * running in the shell. This means that the output of multiple shell commands
 * will be returned until the shell exits.
 * 
 * @author Ewa Matejska
 */
public class MyHostShellProcessAdapter extends Process implements
		IHostShellOutputListener {
	private final IExecutionLogger logger;
	private IHostShell hostShell;
	private PipedInputStream inputStream = null;
	private PipedInputStream errorStream = null;
	private HostShellOutputStream outputStream = null;

	private PipedOutputStream hostShellInput = null;
	private PipedOutputStream hostShellError = null;
	private String pattern1;
	private boolean done = false;

	/**
	 * Constructor.
	 * 
	 * @param hostShell
	 *            An instance of the IHostShell class.
	 * @param logger
	 * @param postfix
	 * @throws java.io.IOException
	 */
	public MyHostShellProcessAdapter(IHostShell hostShell, String pattern1,
			IExecutionLogger logger) throws java.io.IOException {
		this.logger = logger;
		this.hostShell = hostShell;
		this.pattern1 = pattern1;
		hostShellInput = new PipedOutputStream();
		hostShellError = new PipedOutputStream();
		inputStream = new PipedInputStream(hostShellInput);
		errorStream = new PipedInputStream(hostShellError);
		outputStream = new HostShellOutputStream(hostShell);
		IHostShellOutputReader outputReader;
		outputReader = this.hostShell.getStandardOutputReader();
		if (outputReader != null) {
			outputReader.addOutputListener(this);
		}
		outputReader = this.hostShell.getStandardErrorReader();
		if (outputReader != null) {
			outputReader.addOutputListener(this);
		}
	}

	/**
	 * Exits the shell.
	 * 
	 * @see java.lang.Process#destroy()
	 */
	public synchronized void destroy() {
		hostShell.exit();
		notifyAll();
		closeStreams();
	}

	private void closeStreams() {
		closeStreams(new Object[] { hostShellInput, hostShellError,
				inputStream, errorStream, outputStream });
	}

	private void closeStreams(Object[] streams) {
		for (int i = 0; i < streams.length; ++i) {
			final Object stream = streams[i];
			if (stream != null) {
				if (stream instanceof InputStream) {
					try {
						((InputStream) stream).close();
					} catch (IOException e) {
						if (DLTKCore.DEBUG) {
							e.printStackTrace();
						}
					}
				} else if (stream instanceof OutputStream) {
					try {
						((OutputStream) stream).close();
					} catch (IOException e) {
						if (DLTKCore.DEBUG) {
							e.printStackTrace();
						}
					}
				} else {
					DLTKRSEPlugin
							.log("closeStream(" + stream.getClass().getName() + ")"); //$NON-NLS-1$ //$NON-NLS-2$
				}
			}
		}
	}

	/**
	 * There is no relevant exit value to return when the shell exits. This
	 * always returns 0.
	 */
	public synchronized int exitValue() {
		if (!done && hostShell.isActive())
			throw new IllegalThreadStateException();
		// No way to tell what the exit value was.
		// TODO it would be possible to get the exit value
		// when the remote process is started like this:
		// sh -c' remotecmd ; echo" -->RSETAG<-- $?\"'
		// Then the output steram could be examined for -->RSETAG<-- to get the
		// exit value.
		return 0;
	}

	/**
	 * Returns the error stream of the shell.
	 * 
	 * @see java.lang.Process#getErrorStream()
	 */
	public InputStream getErrorStream() {
		return errorStream;
	}

	/**
	 * Returns the input stream for the shell.
	 * 
	 * @see java.lang.Process#getInputStream()
	 */
	public InputStream getInputStream() {
		return inputStream;
	}

	/**
	 * Returns the output stream for the shell.
	 * 
	 * @see java.lang.Process#getOutputStream()
	 */
	public OutputStream getOutputStream() {
		return outputStream;
	}

	/**
	 * Waits for the shell to exit.
	 * 
	 * @see java.lang.Process#waitFor()
	 */
	public synchronized int waitFor() throws InterruptedException {

		while (!done && hostShell.isActive()) {
			try {
				wait(1000);
			} catch (InterruptedException e) {
				// ignore because we're polling to see if shell is still active.
			}
		}

		try {
			// Wait a second to try to get some more output from the target
			// shell before closing.
			wait(1000);
			// Allow for the data from the stream to be read if it's available
			if (inputStream.available() != 0 || errorStream.available() != 0)
				throw new InterruptedException();

			hostShell.exit();
			closeStreams();
		} catch (IOException e) {
			// Ignore
		}
		return 0;
	}

	private synchronized void endOfOutput() {
		done = true;
		notifyAll();
	}

	/**
	 * Process an RSE Shell event, by writing the lines of text contained in the
	 * event into the adapter's streams.
	 * 
	 * @see org.eclipse.rse.services.shells.IHostShellOutputListener#
	 *      shellOutputChanged
	 *      (org.eclipse.rse.services.shells.IHostShellChangeEvent)
	 */
	private int prefixCounter = 0;

	public void shellOutputChanged(IHostShellChangeEvent event) {
		IHostOutput[] input = event.getLines();
		OutputStream outputStream = event.isError() ? hostShellError
				: hostShellInput;
		try {
			for (int i = 0; i < input.length; i++) {
				String line = input[i].getString();
				if (logger != null) {
					logger.logLine(line);
				}
				if (line == null) {
					continue;
				}
				if (!event.isError()) {
					String trimLine = line.trim();
					if (trimLine.endsWith(this.pattern1)) {
						if (prefixCounter == 1 && !trimLine.equals(pattern1)) {
							// We need to output part of line
							int pos = line.indexOf(pattern1);
							outputStream.write(line.substring(0, pos)
									.getBytes());
							outputStream.write('\n');
							outputStream.flush();
						}
						prefixCounter++;
						if (prefixCounter == 2) {
							endOfOutput();
							return;
						}
						continue;
					}
				}
				if (prefixCounter == 1) {
					outputStream.write(line.getBytes());
					outputStream.write('\n');
					outputStream.flush();
				}
			}
		} catch (IOException e) {
			// Ignore
		}
	}

}
