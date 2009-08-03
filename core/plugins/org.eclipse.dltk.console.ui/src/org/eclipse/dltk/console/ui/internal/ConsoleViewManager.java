/*******************************************************************************
 * Copyright (c) 2009 xored software, Inc.  
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html  
 *
 * Contributors:
 *     xored software, Inc. - initial API and Implementation (Alex Panchenko)
 *******************************************************************************/
package org.eclipse.dltk.console.ui.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.console.IConsoleConstants;

/**
 * @since 2.0
 */
public class ConsoleViewManager {

	public static class Descriptor {
		private final String viewId;
		private final int priority;

		public Descriptor(String viewId, int priority) {
			this.viewId = viewId;
			this.priority = priority;
		}

		public Descriptor(IConfigurationElement element) {
			this.viewId = element.getAttribute("viewId"); //$NON-NLS-1$
			this.priority = parseInt(element.getAttribute("priority")); //$NON-NLS-1$
		}

		protected static int parseInt(String value) {
			try {
				return Integer.parseInt(value);
			} catch (NumberFormatException e) {
				return 0;
			}
		}

		public String getViewId() {
			return viewId;
		}
	}

	/**
	 * @param extensionPoint
	 * @param elementType
	 */
	public ConsoleViewManager(String extensionPoint) {
		extensions = new ArrayList<Descriptor>(5);
		extensions.add(new Descriptor(IConsoleConstants.ID_CONSOLE_VIEW, 0));
		for (final IConfigurationElement element : Platform
				.getExtensionRegistry().getConfigurationElementsFor(
						extensionPoint)) {
			final Descriptor descriptor = new Descriptor(element);
			if (isValidDescriptor(descriptor)) {
				extensions.add(descriptor);
			}
		}
		Collections.sort(extensions, new Comparator<Descriptor>() {
			public int compare(Descriptor arg0, Descriptor arg1) {
				return arg1.priority - arg0.priority;
			}
		});
	}

	// Contains list of descriptors.
	private List<Descriptor> extensions;

	/**
	 * Return array of descriptors. If there are no contributed instances the
	 * empty array is returned.
	 * 
	 * @param natureId
	 * @return
	 * @throws CoreException
	 */
	public Descriptor[] getDescriptors() {
		return extensions.toArray(new Descriptor[extensions.size()]);
	}

	public Descriptor getFirst() {
		return extensions.get(0);
	}

	protected boolean isValidDescriptor(Descriptor descriptor) {
		return descriptor != null;
	}

}
