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
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IModelProvider;
import org.eclipse.dltk.core.IProjectFragmentFactory;
import org.eclipse.dltk.core.SimpleClassDLTKExtensionManager;
import org.eclipse.dltk.core.SimpleDLTKExtensionManager.ElementInfo;
import org.eclipse.dltk.utils.NatureExtensionManager;

/**
 * Used to provide or replace some model elements in structure model.
 */
public class ModelProviderManager {
	private static final String EXT_POINT = DLTKCore.PLUGIN_ID + ".model";

	private static final String REQUIRES = "requires";
	private static final String ID = "id";
	private static final String LANGUAGE = "language";
	private static Map<String, IModelProvider[]> providers = null;

	public synchronized static IModelProvider[] getProviders(String lang) {
		if (providers == null) {
			providers = new HashMap<String, IModelProvider[]>();

			final SimpleClassDLTKExtensionManager manager = new SimpleClassDLTKExtensionManager(
					EXT_POINT) {
				@Override
				protected boolean isValidElement(
						IConfigurationElement confElement) {
					return "model".equals(confElement.getName());
				}
			};
			ElementInfo[] infos = manager.getElementInfos();
			Map<String, List<ElementInfo>> langToElementList = new HashMap<String, List<ElementInfo>>();
			// Fill element names and sort elements by language
			for (int i = 0; i < infos.length; i++) {
				String langauge = infos[i].getConfig().getAttribute(LANGUAGE);
				if (langToElementList.containsKey(langauge)) {
					List<ElementInfo> elements = langToElementList
							.get(langauge);
					elements.add(infos[i]);
				} else {
					List<ElementInfo> elements = new ArrayList<ElementInfo>();
					elements.add(infos[i]);
					langToElementList.put(langauge, elements);
				}
			}
			for (Map.Entry<String, List<ElementInfo>> entry : langToElementList
					.entrySet()) {
				String language = entry.getKey();
				List<ElementInfo> elements = entry.getValue();

				Map<String, ElementInfo> names = new HashMap<String, ElementInfo>(); // Contains
																						// map
																						// for
																						// all
																						// ids
				for (int i = 0; i < elements.size(); i++) {
					ElementInfo info = elements.get(i);
					String name = info.getConfig().getAttribute(ID);
					names.put(name, info);
				}
				List<IModelProvider> result = new ArrayList<IModelProvider>(); // Final
																				// IModelProvider
																				// elements
				Set<String> added = new HashSet<String>(); // Contain names for
															// added elements
				// Process elements and keep dependencies
				List<ElementInfo> toProcess = new ArrayList<ElementInfo>();
				toProcess.addAll(elements);
				while (!toProcess.isEmpty()) {
					ElementInfo info = toProcess.remove(0);
					String requires = info.getConfig().getAttribute(REQUIRES);
					if (requires == null) {
						result.add((IModelProvider) manager.getInitObject(info));
					} else {
						String req = requires.trim();
						if (added.contains(req)) { // Dependency
							// present
							result.add((IModelProvider) info.object);
						} else {
							if (names.containsKey(req)) { // Dependency exist
								// Add element to end of process
								toProcess.add(info);
								added.add(info.getConfig().getAttribute(ID));
							} else {
								// Dependency doesn't exists so add to results
								result.add((IModelProvider) info.object);
								added.add(info.getConfig().getAttribute(ID));
							}
						}
					}
				}
				providers.put(language,
						result.toArray(new IModelProvider[result.size()]));
			}
		}
		return providers.get(lang);
	}

	private static final NatureExtensionManager<IProjectFragmentFactory> projectFragmentFactories = new NatureExtensionManager<IProjectFragmentFactory>(
			EXT_POINT, IProjectFragmentFactory.class) {
		protected boolean isValidElement(IConfigurationElement element) {
			return "projectFragment".equals(element.getName());
		}
	};

	public static IProjectFragmentFactory[] getProjectFragmentFactories(
			String natureId) {
		return projectFragmentFactories.getInstances(natureId);
	}

}
