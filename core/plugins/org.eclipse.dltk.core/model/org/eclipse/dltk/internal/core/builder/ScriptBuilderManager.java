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
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.builder.IScriptBuilder;
import org.eclipse.dltk.utils.NatureExtensionManager;

public class ScriptBuilderManager extends
		NatureExtensionManager<IScriptBuilder> {

	private ScriptBuilderManager() {
		super(LANGUAGE_EXTPOINT, IScriptBuilder.class, "#"); //$NON-NLS-1$
	}

	private final static String LANGUAGE_EXTPOINT = DLTKCore.PLUGIN_ID
			+ ".builder"; //$NON-NLS-1$

	private static ScriptBuilderManager manager = null;

	private synchronized static ScriptBuilderManager getManager() {
		if (manager == null) {
			manager = new ScriptBuilderManager();
		}
		return manager;
	}

	@Override
	protected void registerConfigurationElements(
			IConfigurationElement[] elements, String categoryAttr) {
		super.registerConfigurationElements(elements, categoryAttr);
		for (IConfigurationElement element : elements) {
			if ("disable".equals(element.getName())) { //$NON-NLS-1$
				final String natureId = element.getAttribute(categoryAttr);
				if (natureId != null && natureId.length() != 0) {
					final String className = element.getAttribute("className"); //$NON-NLS-1$
					if (className != null && className.length() != 0) {
						List<String> disable = disabled.get(natureId);
						if (disable == null) {
							disable = new ArrayList<String>();
							disabled.put(natureId, disable);
						}
						if (!disable.contains(className)) {
							disable.add(className);
						}
					}
				}
			}
		}
	}

	private Map<String, List<String>> disabled = new HashMap<String, List<String>>();

	@Override
	protected boolean isValidElement(IConfigurationElement element) {
		return "builder".equals(element.getName()); //$NON-NLS-1$
	}

	@Override
	protected IScriptBuilder[] filter(IScriptBuilder[] objects, String natureId) {
		if (objects != null) {
			final List<String> disable = disabled.get(natureId);
			if (disable != null) {
				final List<IScriptBuilder> output = new ArrayList<IScriptBuilder>();
				for (IScriptBuilder object : objects) {
					if (!disable.contains(object.getClass().getName())) {
						output.add(object);
					}
				}
				if (!output.isEmpty()) {
					return output.toArray(createArray(output.size()));
				} else {
					return null;
				}
			}
		}
		return objects;
	}

	/**
	 * Return merged with all elements with nature #
	 * 
	 * @param natureId
	 * @return
	 * @throws CoreException
	 */
	public static IScriptBuilder[] getScriptBuilders(String natureId) {
		return getManager().getInstances(natureId);
	}

	public static IScriptBuilder[] getAllScriptBuilders() {
		return getManager().getAllInstances();
	}
}
