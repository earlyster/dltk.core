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
import org.eclipse.dltk.ui.IModelCompareProvider;
import org.eclipse.dltk.ui.IModelContentProvider;
import org.eclipse.jface.viewers.ILabelProvider;

/**
 * Used to provide or replace some model elements in structure model.
 */
public class UIModelProviderManager {
	private static final IModelCompareProvider[] NONE_MODEL_COMPARE_PROVIDERS = new IModelCompareProvider[0];
	private static final ILabelProvider[] NONE_LABEL_PROVIDERS = new ILabelProvider[0];
	private static final IModelContentProvider[] NONE_MODEL_CONTENT_PROVIDERS = new IModelContentProvider[0];

	private static final String REQUIRES = "requires"; //$NON-NLS-1$
	private static final String ID = "id"; //$NON-NLS-1$
	private static final String LANGUAGE = "language"; //$NON-NLS-1$

	private static SimpleClassDLTKExtensionManager contentProviderManager = new SimpleClassDLTKExtensionManager(
			DLTKUIPlugin.PLUGIN_ID + ".modelContentProvider"); //$NON-NLS-1$

	private static SimpleClassDLTKExtensionManager labelProviderManager = new SimpleClassDLTKExtensionManager(
			DLTKUIPlugin.PLUGIN_ID + ".modelLabelProvider"); //$NON-NLS-1$

	private static SimpleClassDLTKExtensionManager compareProviderManager = new SimpleClassDLTKExtensionManager(
			DLTKUIPlugin.PLUGIN_ID + ".modelCompareProvider"); //$NON-NLS-1$

	private static Map<String, List<IModelContentProvider>> contentProviders = null;
	private static Map<String, List<ILabelProvider>> labelProviders = null;
	private static Map<String, List<IModelCompareProvider>> compareProviders = null;

	public synchronized static IModelContentProvider[] getContentProviders(
			String lang) {
		if (contentProviders == null) {
			contentProviders = initializeProviders(contentProviderManager);
		}
		if (lang == null) {
			List<IModelContentProvider> providers = new ArrayList<IModelContentProvider>();
			for (List<IModelContentProvider> elements : contentProviders
					.values()) {
				providers.addAll(elements);
			}
			return providers
					.toArray(new IModelContentProvider[providers.size()]);
		}
		List<IModelContentProvider> result = contentProviders.get(lang);
		if (result != null) {
			return result.toArray(new IModelContentProvider[result.size()]);
		}
		return NONE_MODEL_CONTENT_PROVIDERS;
	}

	public synchronized static ILabelProvider[] getLabelProviders(String lang) {
		if (labelProviders == null) {
			labelProviders = initializeProviders(labelProviderManager);
		}
		if (lang == null) {
			List<ILabelProvider> providers = new ArrayList<ILabelProvider>();
			for (List<ILabelProvider> elements : labelProviders.values()) {
				providers.addAll(elements);
			}
			return providers.toArray(new ILabelProvider[providers.size()]);
		}
		List<ILabelProvider> result = labelProviders.get(lang);
		if (result != null) {
			return result.toArray(new ILabelProvider[result.size()]);
		}
		return NONE_LABEL_PROVIDERS;
	}

	public synchronized static IModelCompareProvider[] getCompareProviders(
			String lang) {
		if (compareProviders == null) {
			compareProviders = initializeProviders(compareProviderManager);
		}
		if (lang == null) {
			List<IModelCompareProvider> providers = new ArrayList<IModelCompareProvider>();
			for (List<IModelCompareProvider> elements : compareProviders
					.values()) {
				providers.addAll(elements);
			}
			return providers
					.toArray(new IModelCompareProvider[providers.size()]);
		}
		List<IModelCompareProvider> result = compareProviders.get(lang);
		if (result != null) {
			return result.toArray(new IModelCompareProvider[result.size()]);
		}
		return NONE_MODEL_COMPARE_PROVIDERS;
	}

	private synchronized static <T> Map<String, List<T>> initializeProviders(
			SimpleClassDLTKExtensionManager manager) {
		Map<String, List<T>> providers = new HashMap<String, List<T>>();
		ElementInfo[] infos = manager.getElementInfos();
		Map<String, List<ElementInfo>> langToElementList = new HashMap<String, List<ElementInfo>>();
		// Fill element names and sort elements by language
		for (int i = 0; i < infos.length; i++) {
			String langauge = infos[i].getConfig().getAttribute(LANGUAGE);
			List<ElementInfo> elements = langToElementList.get(langauge);
			if (elements == null) {
				elements = new ArrayList<ElementInfo>();
				langToElementList.put(langauge, elements);
			}
			elements.add(infos[i]);
		}
		for (Map.Entry<String, List<ElementInfo>> entry : langToElementList
				.entrySet()) {
			String language = entry.getKey();
			List<ElementInfo> elements = entry.getValue();

			// Contains map for all ids
			Set<String> allIds = new HashSet<String>();
			for (ElementInfo info : elements) {
				allIds.add(info.getConfig().getAttribute(ID));
			}
			// Final IModelProvider elements
			List<T> result = new ArrayList<T>();
			// Contains names for added elements
			Set<String> added = new HashSet<String>();
			// Process elements and keep dependencies
			List<ElementInfo> toProcess = new ArrayList<ElementInfo>(elements);
			while (!toProcess.isEmpty()) {
				ElementInfo info = toProcess.remove(0);
				String requires = info.getConfig().getAttribute(REQUIRES);
				if (requires == null) {
					@SuppressWarnings("unchecked")
					final T obj = (T) manager.getInitObject(info);
					if (obj != null) {
						result.add(obj);
						added.add(info.getConfig().getAttribute(ID));
					}
				} else {
					requires = requires.trim();
					if (added.contains(requires)) {
						@SuppressWarnings("unchecked")
						final T obj = (T) manager.getInitObject(info);
						if (obj != null) {
							result.add(obj);
							added.add(info.getConfig().getAttribute(ID));
						}
					} else if (allIds.contains(requires)) { // Dependency exist
						// Add element to end of process
						toProcess.add(info);
						// FIXME check endless loops
					} else {
						// Dependency doesn't exists so add to results
						@SuppressWarnings("unchecked")
						final T obj = (T) manager.getInitObject(info);
						if (obj != null) {
							result.add(obj);
							added.add(info.getConfig().getAttribute(ID));
						}
					}
				}
			}
			providers.put(language, result);
		}
		return providers;
	}
}
