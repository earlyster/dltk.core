/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.eclipse.dltk.internal.core.builder;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.internal.core.IStructureBuilder;
import org.eclipse.dltk.utils.NatureExtensionManager;

public class StructureBuilderManager {

	private final static String EXTPOINT = DLTKCore.PLUGIN_ID
			+ ".structureBuilder"; //$NON-NLS-1$

	private static NatureExtensionManager manager = null;

	private synchronized static NatureExtensionManager getManager() {
		if (manager == null) {
			manager = new NatureExtensionManager(EXTPOINT,
					IStructureBuilder.class, "#"); //$NON-NLS-1$
		}
		return manager;
	}

	/**
	 * Return merged with all elements with nature #
	 * 
	 * @param natureId
	 * @return
	 * @throws CoreException
	 */
	public static IStructureBuilder[] getBuilders(String natureId) {
		return (IStructureBuilder[]) getManager().getInstances(natureId);
	}

}
