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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.dltk.core.SimpleClassDLTKExtensionManager;
import org.eclipse.dltk.core.SimpleDLTKExtensionManager.ElementInfo;
import org.eclipse.dltk.ui.DLTKUIPlugin;
import org.eclipse.dltk.ui.IModelContentProvider;
import org.eclipse.jface.viewers.ILabelProvider;

/**
 * Used to provide or replace some model elements in structure model.
 */
public class UIModelProviderManager {
	private static final String REQUIRES = "requires";
	private static final String ID = "id";
	private static final String LANGUAGE = "language";
	private static SimpleClassDLTKExtensionManager contentProviderManager = new SimpleClassDLTKExtensionManager(
			DLTKUIPlugin.PLUGIN_ID + ".modelContentProvider");

	private static SimpleClassDLTKExtensionManager labelProviderManager = new SimpleClassDLTKExtensionManager(
			DLTKUIPlugin.PLUGIN_ID + ".modelLabelProvider");

	private static IModelContentProvider[] contentProviders = null;
	private static Map labelProviders = null;

	public static IModelContentProvider[] getContentProviders() {
		if (contentProviders == null) {
			List elements = new ArrayList();

			ElementInfo[] infos = contentProviderManager.getElementInfos();
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
					result.add(contentProviderManager.getInitObject(info));
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
			contentProviders = (IModelContentProvider[]) result
					.toArray(new IModelContentProvider[result.size()]);
		}
		return contentProviders;
	}

	public static ILabelProvider[] getLabelProviders(String lang) {
		if (labelProviders == null) {
			labelProviders = new HashMap();

			ElementInfo[] infos = labelProviderManager.getElementInfos();
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
						result.add(labelProviderManager.getInitObject(info));
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
				labelProviders.put(language, result
						.toArray(new ILabelProvider[result.size()]));
			}
		}
		if (lang == null) {
			List providers = new ArrayList();
			providers.addAll(UIModelProviderManager.labelProviders.values());
		}
		return (ILabelProvider[]) labelProviders.get(lang);
	}
}
