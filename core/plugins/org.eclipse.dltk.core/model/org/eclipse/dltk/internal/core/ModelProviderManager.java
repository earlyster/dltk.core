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
package org.eclipse.dltk.internal.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IModelProvider;
import org.eclipse.dltk.core.SimpleClassDLTKExtensionManager;
import org.eclipse.dltk.core.SimpleDLTKExtensionManager.ElementInfo;

/**
 * Used to provide or replace some model elements in structure model.
 */
public class ModelProviderManager {
	private static final String REQUIRES = "requires";
	private static final String ID = "id";
	private static final String LANGUAGE = "language";
	private static SimpleClassDLTKExtensionManager manager = new SimpleClassDLTKExtensionManager(
			DLTKCore.PLUGIN_ID + ".model");
	private static Map providers = null;

	public static IModelProvider[] getProviders(String lang) {
		if (providers == null) {
			providers = new HashMap();

			ElementInfo[] infos = manager.getElementInfos();
			Map langToElementList = new HashMap();
			// Fill element names and sort elements by language
			for (int i = 0; i < infos.length; i++) {
				String langauge = infos[i].getConfig().getAttribute(LANGUAGE);
				if (langToElementList.containsKey(langauge)) {
					List elements = (List) langToElementList.get(langauge);
					elements.add(infos[i]);
				} else {
					List elements = new ArrayList();
					elements.add(infos[i]);
					langToElementList.put(langauge, elements);
				}
			}
			for (Iterator iterator = langToElementList.entrySet().iterator(); iterator
					.hasNext();) {
				Map.Entry entry = (Map.Entry) iterator.next();
				String language = (String) entry.getKey();
				List elements = (List) entry.getValue();

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
				providers.put(language, result
						.toArray(new IModelProvider[result.size()]));
			}
		}
		return (IModelProvider[]) providers.get(lang);
	}
}
