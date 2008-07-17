/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.eclipse.dltk.internal.core.builder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.internal.core.IStructureBuilder;

public class StructureBuilderManager {

	private final static String EXTPOINT = DLTKCore.PLUGIN_ID
			+ ".structureBuilder"; //$NON-NLS-1$

	private final static String NATURE_ATTR = "nature"; //$NON-NLS-1$

	// Contains list of builders for selected nature.
	private static Map builders;

	private synchronized static void initialize() {
		if (builders != null) {
			return;
		}

		builders = new HashMap(5);
		IConfigurationElement[] cfg = Platform.getExtensionRegistry()
				.getConfigurationElementsFor(EXTPOINT);

		for (int i = 0; i < cfg.length; i++) {
			String nature = cfg[i].getAttribute(NATURE_ATTR);
			if (builders.get(nature) != null) {
				List elements = (List) builders.get(nature);
				elements.add(cfg[i]);
			} else {
				List elements = new ArrayList();
				elements.add(cfg[i]);
				builders.put(nature, elements);
			}
		}
	}

	/**
	 * Return merged with all elements with nature #
	 * 
	 * @param natureId
	 * @return
	 * @throws CoreException
	 */
	public static IStructureBuilder[] getBuilders(String natureId)
			throws CoreException {
		initialize();
		IStructureBuilder[] nature = getByNature(natureId);
		IStructureBuilder[] all = getByNature("#"); //$NON-NLS-1$
		if (all == null) {
			return nature;
		}
		if (nature == null) {
			return all;
		}
		final IStructureBuilder[] result = new IStructureBuilder[nature.length
				+ all.length];
		System.arraycopy(nature, 0, result, 0, nature.length);
		System.arraycopy(all, 0, result, nature.length, all.length);
		return result;
	}

	private static IStructureBuilder[] getByNature(String natureId)
			throws CoreException {
		final Object ext = builders.get(natureId);
		if (ext != null) {
			if (ext instanceof IStructureBuilder[]) {
				return (IStructureBuilder[]) ext;
			} else if (ext instanceof List) {
				final List elements = (List) ext;
				final IStructureBuilder[] result = new IStructureBuilder[elements
						.size()];
				for (int i = 0; i < elements.size(); ++i) {
					Object e = elements.get(i);
					if (e instanceof IStructureBuilder) {
						result[i] = (IStructureBuilder) e;
					} else {
						final IConfigurationElement cfg = (IConfigurationElement) e;
						final IStructureBuilder builder = (IStructureBuilder) cfg
								.createExecutableExtension("class"); //$NON-NLS-1$
						result[i] = builder;
					}
				}
				builders.put(natureId, result);
				return result;
			}
		}
		return null;
	}
}
