/*******************************************************************************
 * Copyright (c) 2011 NumberFour AG
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     NumberFour AG - initial API and Implementation (Alex Panchenko)
 *******************************************************************************/
package org.eclipse.dltk.utils;

import java.util.Collections;
import java.util.Comparator;

import org.eclipse.core.runtime.IConfigurationElement;

public class PriorityNatureExtensionManager<E> extends
		NatureExtensionManager<E> {

	public PriorityNatureExtensionManager(String extensionPoint,
			Class<E> elementType) {
		super(extensionPoint, elementType);
	}

	public PriorityNatureExtensionManager(String extensionPoint,
			Class<?> elementType, String universalNatureId) {
		super(extensionPoint, elementType, universalNatureId);
	}

	@Override
	protected void initializeDescriptors(java.util.List<Object> descriptors) {
		Collections.sort(descriptors, new Comparator<Object>() {
			int priority(IConfigurationElement element) {
				try {
					return Integer.parseInt(element.getAttribute("priority"));
				} catch (NumberFormatException e) {
					return 0;
				}
			}

			public int compare(Object o1, Object o2) {
				return priority((IConfigurationElement) o2)
						- priority((IConfigurationElement) o1);
			}
		});
	}

}
