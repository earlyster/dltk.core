/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.eclipse.dltk.dbgp.internal.commands;

import java.io.IOException;
import java.util.IdentityHashMap;
import java.util.Map;

import org.eclipse.dltk.dbgp.exceptions.DbgpException;
import org.eclipse.dltk.dbgp.exceptions.DbgpIOException;
import org.eclipse.dltk.dbgp.exceptions.DbgpOpertionCanceledException;
import org.eclipse.dltk.dbgp.exceptions.DbgpTimeoutException;
import org.eclipse.dltk.dbgp.internal.DbgpRequest;
import org.eclipse.dltk.dbgp.internal.IDbgpDebugingEngine;
import org.eclipse.dltk.dbgp.internal.packets.DbgpResponsePacket;
import org.eclipse.dltk.dbgp.internal.utils.DbgpXmlParser;
import org.eclipse.dltk.debug.core.DLTKDebugPlugin;
import org.eclipse.dltk.debug.core.DLTKDebugPreferenceConstants;
import org.w3c.dom.Element;

public class DbgpDebuggingEngineCommunicator implements IDbgpCommunicator {
	private final int timeout;

	private final IDbgpDebugingEngine engine;

	private void sendRequest(String command) throws IOException {
		engine.sendCommand(command);
	}

	private DbgpResponsePacket receiveResponse(int transactionId)
			throws IOException, InterruptedException {
		return engine.getResponsePacket(transactionId, timeout);
	}

	public DbgpDebuggingEngineCommunicator(IDbgpDebugingEngine engine) {
		if (engine == null) {
			throw new IllegalArgumentException();
		}

		this.engine = engine;

		timeout = DLTKDebugPlugin.getDefault().getPluginPreferences().getInt(
				DLTKDebugPreferenceConstants.PREF_DBGP_RESPONSE_TIMEOUT);
	}

	private final Map activeRequests = new IdentityHashMap();

	public Element communicate(DbgpRequest request) throws DbgpException {
		try {
			final DbgpResponsePacket packet;
			final int requestId = Integer.parseInt(request
					.getOption(DbgpBaseCommands.ID_OPTION));
			if (request.isAsync()) {
				sendRequest(request.toString());
				packet = receiveResponse(requestId);
			} else {
				final long startTime = DEBUG ? System.currentTimeMillis() : 0;
				synchronized (activeRequests) {
					while (!activeRequests.isEmpty()) {
						activeRequests.wait();
					}
					activeRequests.put(request, request);
				}
				if (DEBUG) {
					final long waited = System.currentTimeMillis() - startTime;
					if (waited > 0) {
						System.out.println("Waited " + waited + " ms"); //$NON-NLS-1$ //$NON-NLS-2$
					}
				}
				try {
					sendRequest(request.toString());
					packet = receiveResponse(requestId);
				} finally {
					synchronized (activeRequests) {
						activeRequests.remove(request);
						activeRequests.notifyAll();
					}
				}
			}

			if (packet == null) {
				throw new DbgpTimeoutException();
			}

			Element response = packet.getContent();

			DbgpException e = DbgpXmlParser.checkError(response);
			if (e != null) {
				throw e;
			}

			return response;
		} catch (InterruptedException e) {
			throw new DbgpOpertionCanceledException();
		} catch (IOException e) {
			throw new DbgpIOException(e);
		}
	}

	public void send(DbgpRequest request) throws DbgpException {
		try {
			sendRequest(request.toString());
		} catch (IOException e) {
			throw new DbgpIOException(e);
		}
	}

	private static final boolean DEBUG = false;
}
