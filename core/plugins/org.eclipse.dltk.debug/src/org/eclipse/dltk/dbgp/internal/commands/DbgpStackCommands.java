/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.eclipse.dltk.dbgp.internal.commands;

import java.util.Arrays;
import java.util.Comparator;

import org.eclipse.dltk.dbgp.IDbgpStackLevel;
import org.eclipse.dltk.dbgp.commands.IDbgpStatckCommands;
import org.eclipse.dltk.dbgp.exceptions.DbgpDebuggingEngineException;
import org.eclipse.dltk.dbgp.exceptions.DbgpException;
import org.eclipse.dltk.dbgp.internal.DbgpRequest;
import org.eclipse.dltk.dbgp.internal.utils.DbgpXmlEntityParser;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class DbgpStackCommands extends DbgpBaseCommands implements
		IDbgpStatckCommands {
	private static final String STACK_DEPTH_COMMAND = "stack_depth"; //$NON-NLS-1$

	private static final String STACK_GET_COMMAND = "stack_get"; //$NON-NLS-1$

	private static final String TAG_STACK = "stack"; //$NON-NLS-1$

	private static final String ATTR_DEPTH = "depth"; //$NON-NLS-1$

	protected int parseStackDepthResponse(Element response)
			throws DbgpDebuggingEngineException {
		return Integer.parseInt(response.getAttribute(ATTR_DEPTH));
	}

	protected IDbgpStackLevel[] parseStackLevels(Element response)
			throws DbgpException {
		NodeList nodes = response.getElementsByTagName(TAG_STACK);
		IDbgpStackLevel[] list = new IDbgpStackLevel[nodes.getLength()];
		for (int i = 0; i < nodes.getLength(); ++i) {
			final Element level = (Element) nodes.item(i);
			list[i] = DbgpXmlEntityParser.parseStackLevel(level);
		}
		Arrays.sort(list, STACK_LEVEL_COMPARATOR);
		return list;
	}

	private static final Comparator STACK_LEVEL_COMPARATOR = new Comparator() {

		public int compare(Object o1, Object o2) {
			final IDbgpStackLevel level1 = (IDbgpStackLevel) o1;
			final IDbgpStackLevel level2 = (IDbgpStackLevel) o2;
			return level1.getLevel() - level2.getLevel();
		}

	};

	public DbgpStackCommands(IDbgpCommunicator communicator) {
		super(communicator);
	}

	public int getStackDepth() throws DbgpException {
		return parseStackDepthResponse(communicate(createRequest(STACK_DEPTH_COMMAND)));
	}

	public IDbgpStackLevel getStackLevel(int stackDepth) throws DbgpException {
		DbgpRequest request = createRequest(STACK_GET_COMMAND);
		request.addOption("-d", stackDepth); //$NON-NLS-1$
		IDbgpStackLevel[] levels = parseStackLevels(communicate(request));

		if (levels.length < 1) {
			return null;
		}

		return levels[0];
	}

	public IDbgpStackLevel[] getStackLevels() throws DbgpException {
		return parseStackLevels(communicate(createRequest(STACK_GET_COMMAND)));
	}
}
