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

import org.eclipse.dltk.dbgp.IDbgpSession;
import org.eclipse.dltk.internal.debug.core.model.DbgpService;

public class DbgpServiceDispatcherTests extends AbstractDbgpServiceTests {

	private static final String IDE1 = "IDE001";
	private static final String IDE2 = "IDE002";
	private static final String IDE3 = "IDE003";

	private static final int COUNT1 = 10;
	private static final int COUNT2 = 20;
	private static final int COUNT3 = 5;

	volatile int count1;
	volatile int count2;
	volatile int count3;

	protected void setUp() throws Exception {
		super.setUp();
		count1 = 0;
		count2 = 0;
		count3 = 0;
	}

	public void testInitPacketParser() throws IOException {
		final DbgpService service = createService(ANY_PORT);
		try {
			service.registerAcceptor(IDE1, new AbstractDbgpAcceptor() {
				public void acceptDbgpThread(IDbgpSession session) {
					synchronized (DbgpServiceDispatcherTests.this) {
						++count1;
					}
				}
			});
			service.registerAcceptor(IDE2, new AbstractDbgpAcceptor() {
				public void acceptDbgpThread(IDbgpSession session) {
					synchronized (DbgpServiceDispatcherTests.this) {
						++count2;
					}
				}
			});
			service.registerAcceptor(IDE3, new AbstractDbgpAcceptor() {
				public void acceptDbgpThread(IDbgpSession session) {
					synchronized (DbgpServiceDispatcherTests.this) {
						++count3;
					}
				}
			});
			for (int i = 0; i < COUNT1; ++i) {
				final byte[] packet = TestInitPacket.build(IDE1);
				assertNotNull(packet);
				performOperation(service.getPort(), new SendPacketOperation(
						packet));
			}
			for (int i = 0; i < COUNT2; ++i) {
				final byte[] packet = TestInitPacket.build(IDE2);
				assertNotNull(packet);
				performOperation(service.getPort(), new SendPacketOperation(
						packet));
			}
			for (int i = 0; i < COUNT3; ++i) {
				final byte[] packet = TestInitPacket.build(IDE3);
				assertNotNull(packet);
				performOperation(service.getPort(), new SendPacketOperation(
						packet));
			}
			for (int i = 0; i < 40; ++i) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// 
				}
				synchronized (DbgpServiceDispatcherTests.this) {
					if (count1 + count2 + count3 == COUNT1 + COUNT2 + COUNT3) {
						break;
					}
				}
			}
		} finally {
			service.shutdown();
		}
		assertEquals(COUNT1, count1);
		assertEquals(COUNT2, count2);
		assertEquals(COUNT3, count3);
	}
}
