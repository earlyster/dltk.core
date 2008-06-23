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
package org.eclipse.dltk.debug.dbgp.tests.service;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.dltk.dbgp.DbgpServer;
import org.eclipse.dltk.dbgp.IDbgpSession;
import org.eclipse.dltk.debug.core.DLTKDebugPreferenceConstants;
import org.eclipse.dltk.internal.debug.core.model.DbgpService;

public class AbstractDbgpServiceTests extends TestCase {

	protected static final int ANY_PORT = DLTKDebugPreferenceConstants.DBGP_AVAILABLE_PORT;

	/**
	 * Timeout for the connection operations
	 */
	private static final int TIMEOUT = 5000;

	protected static final int MIN_PORT = 0x8000;
	protected static final int MAX_PORT = 0xFFFF;

	/**
	 * Creates socket and connects it to the specified port. If connection could
	 * not be performed the error is thrown. Successfully connected socket is
	 * added to the {@link #sockets} and closed in {@link #tearDown()}
	 * 
	 * @param port
	 * @return con
	 * @throws IOException
	 */
	protected Socket connect(final int port) throws IOException {
		final Socket socket = new Socket();
		try {
			socket.connect(new InetSocketAddress(port), TIMEOUT);
		} catch (IOException e) {
			closeQuietly(socket);
			throw e;
		}
		sockets.add(socket);
		return socket;
	}

	/**
	 * {@link #connect(int)}s socket and performs operation on it.
	 * 
	 * @param port
	 * @param operation
	 * @return
	 * @throws IOException
	 */
	protected Socket performOperation(final int port, ISocketOperation operation)
			throws IOException {
		final Socket socket = connect(port);
		operation.execute(socket);
		return socket;
	}

	/**
	 * Find some available port in the specified range. The returned values is
	 * checked, so users should not perform addtitional checks.
	 * 
	 * @param minPort
	 * @param maxPort
	 * @return
	 */
	protected static int findAvailablePort(int minPort, int maxPort) {
		final int port = DbgpServer.findAvailablePort(minPort, maxPort);
		assertTrue(port > 0);
		return port;
	}

	/**
	 * Active sockets - list is cleared in {@link #tearDown()}. We do not want
	 * to close sockets as the corresponding {@link IDbgpSession} could be
	 * terminated before we processed the connection.
	 */
	private final List sockets = new ArrayList();

	protected void setUp() throws Exception {
		super.setUp();
		sockets.clear();
	}

	/**
	 * Closes all sockets opened by {@link #connect(int)}
	 * 
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		for (Iterator i = sockets.iterator(); i.hasNext();) {
			closeQuietly((Socket) i.next());
		}
		sockets.clear();
		super.tearDown();
	}

	/**
	 * Closes the specified socket and catch possible errors. So it could be
	 * safely used in finally statements and keep original exception if any.
	 * 
	 * @param socket
	 */
	protected void closeQuietly(final Socket socket) {
		try {
			socket.close();
		} catch (Exception e) {
			// ignore
		}
	}

	protected DbgpService createService(int port1) {
		final DbgpService service = new DbgpService(port1);
		assertTrue(service.waitStarted());
		return service;
	}

}
