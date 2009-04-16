/*******************************************************************************
 * Copyright (c) 2008 xored software, Inc.  
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html  
 *
 * Contributors:
 *     xored software, Inc. - initial API and Implementation (Andrei Sobolev)
 *******************************************************************************/
package org.eclipse.dltk.internal.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.dltk.core.SimpleClassDLTKExtensionManager;
import org.eclipse.dltk.core.SimpleDLTKExtensionManager.ElementInfo;
import org.eclipse.dltk.ui.DLTKUIPlugin;
import org.eclipse.dltk.ui.IModelContentProvider;

/**
 * Used to provide or replace some model elements in structure model.
 */
public class ModelContentProviderManager {
	private static final String REQUIRES = "requires";
	private static final String ID = "id";
	private static SimpleClassDLTKExtensionManager manager = new SimpleClassDLTKExtensionManager(
			DLTKUIPlugin.PLUGIN_ID + ".modelContentProvider");
	private static IModelContentProvider[] providers = null;

	public static IModelContentProvider[] getProviders() {
		if (providers == null) {
			List elements = new ArrayList();

			ElementInfo[] infos = manager.getElementInfos();
			// Fill element names and sort elements by language
			for (int i = 0; i < infos.length; i++) {
				elements.add(infos[i]);
			}

			Map names = new HashMap(); // Contains map for all ids
			for (int i = 0; i < elements.size(); i++) {
				ElementInfo info = (ElementInfo) elements.get(i);
				String name = info.getConfig().getAttribute(ID);
				names.put(name, info);
			}
			List result = new ArrayList(); // Final IModelProvider elements
			Set added = new HashSet(); // Contain names for added elements
			// Process elements and keep dependencies
			List toProcess = new ArrayList();
			toProcess.addAll(elements);
			while (!toProcess.isEmpty()) {
				ElementInfo info = (ElementInfo) toProcess.remove(0);
				String requires = info.getConfig().getAttribute(REQUIRES);
				if (requires == null) {
					result.add(manager.getInitObject(info));
				} else {
					String req = requires.trim();
					if (added.contains(req)) { // Dependency
						// present
						result.add(info.object);
					} else {
						if (names.containsKey(req)) { // Dependency exist
							// Add element to end of process
							toProcess.add(info);
						}
					}
				}
			}
			providers = (IModelContentProvider[]) result
					.toArray(new IModelContentProvider[result.size()]);
		}
		return providers;
	}
}
