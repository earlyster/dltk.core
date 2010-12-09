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
package org.eclipse.dltk.internal.logconsole;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.dltk.logconsole.ILogConsole;
import org.eclipse.dltk.logconsole.LogConsoleType;
import org.eclipse.dltk.logconsole.impl.ILogConsoleFactory;

public class LogConsoleFactoryManager {

	private static LogConsoleFactoryManager manager = null;

	public static synchronized LogConsoleFactoryManager getInstance() {
		if (manager == null) {
			manager = new LogConsoleFactoryManager();
		}
		return manager;
	}

	static class FactoryDescriptor {
		final IConfigurationElement element;

		public FactoryDescriptor(IConfigurationElement element) {
			this.element = element;
		}

		ILogConsoleFactory factory;

		boolean failure;

		String getConsoleType() {
			return element.getAttribute("consoleType");
		}

		ILogConsoleFactory getFactory() {
			if (failure) {
				return null;
			}
			if (factory != null) {
				return factory;
			}
			try {
				factory = (ILogConsoleFactory) element
						.createExecutableExtension("class");
				return factory;
			} catch (Exception e) {
				// TODO Auto-generated method stub
				failure = true;
				factory = null;
				return null;
			}
		}
	}

	private List<FactoryDescriptor> descriptors = null;

	public synchronized List<FactoryDescriptor> getDescriptors() {
		if (descriptors == null) {
			descriptors = new ArrayList<FactoryDescriptor>();
			for (IConfigurationElement element : Platform
					.getExtensionRegistry().getConfigurationElementsFor(
							"org.eclipse.dltk.logconsole")) {
				if ("factory".equals(element.getName())) {
					descriptors.add(new FactoryDescriptor(element));
				}
			}
		}
		return descriptors;
	}

	public ILogConsole create(LogConsoleType consoleType, Object identifier) {
		for (FactoryDescriptor descriptor : getDescriptors()) {
			final String type = descriptor.getConsoleType();
			if (type == null || type.equals(consoleType)) {
				ILogConsoleFactory factory = descriptor.getFactory();
				if (factory != null) {
					final ILogConsole console = factory.create(consoleType,
							identifier);
					if (console != null) {
						return console;
					}
				}
			}
		}
		return null;
	}

}
