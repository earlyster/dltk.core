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

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

class ClientPackets {

	public static void send(final Socket socket, final byte[] packet)
			throws IOException {
		OutputStream out = new BufferedOutputStream(socket.getOutputStream());
		send(out, packet);
		out.flush();
	}

	public static void send(OutputStream stream, byte[] initPacket)
			throws IOException {
		stream.write(String.valueOf(initPacket.length).getBytes());
		stream.write(0);
		stream.write(initPacket);
		stream.write(0);
	}

}
