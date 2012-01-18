/*******************************************************************************
 * Copyright (c) 2010 xored software, Inc.  
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html  
 *
 * Contributors:
 *     xored software, Inc. - initial API and Implementation (Alex Panchenko)
 *******************************************************************************/
package org.eclipse.dltk.utils;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.RegistryFactory;
import org.eclipse.dltk.core.DLTKCore;

public class SimpleExtensionManager<E> {

	private final String extensionPoint;
	private final Class<E> elementType;

	/**
	 * @param extensionPoint
	 * @param elementType
	 */
	public SimpleExtensionManager(Class<E> elementType, String extensionPoint) {
		this.elementType = elementType;
		this.extensionPoint = extensionPoint;
	}

	private E[] instances = null;

	public synchronized E[] getInstances() {
		if (instances == null) {
			initialize();
		}
		return instances;
	}

	@SuppressWarnings("unchecked")
	private void initialize() {
		final List<E> result = new ArrayList<E>();
		final IExtensionRegistry registry = RegistryFactory.getRegistry();
		if (registry != null) { // if running under OSGI
			final IConfigurationElement[] elements = registry
					.getConfigurationElementsFor(extensionPoint);
			for (IConfigurationElement element : elements) {
				final E instance = createInstance(element);
				if (instance != null) {
					result.add(instance);
				}
			}
		}
		instances = (E[]) Array.newInstance(elementType, result.size());
		result.toArray(instances);
	}

	@SuppressWarnings("unchecked")
	protected E createInstance(IConfigurationElement element) {
		final Object instance;
		try {
			instance = element.createExecutableExtension(getClassAttribute());
		} catch (CoreException e) {
			DLTKCore.error(e);
			return null;
		}
		if (elementType.isInstance(instance)) {
			return (E) instance;
		} else {
			DLTKCore.error(instance.getClass().getName() + " is not "
					+ element.getName());
			return null;
		}
	}

	protected String getClassAttribute() {
		return "class";
	}

}
