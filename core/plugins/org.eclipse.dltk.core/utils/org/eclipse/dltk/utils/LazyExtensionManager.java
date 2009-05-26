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
package org.eclipse.dltk.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;

public class LazyExtensionManager<E> implements Iterable<E> {

	public class Descriptor {
		private final IConfigurationElement configurationElement;
		private E instance;
		private boolean valid;

		public Descriptor(IConfigurationElement configurationElement) {
			this.configurationElement = configurationElement;
			this.valid = true;
		}

		@SuppressWarnings("unchecked")
		public synchronized E get() {
			if (instance != null) {
				return instance;
			} else if (!valid) {
				return null;
			}
			try {
				instance = (E) configurationElement
						.createExecutableExtension(classAttr);
				return instance;
			} catch (CoreException e) {
				valid = false;
				LazyExtensionManager.this.remove(this);
				return null;
			}
		}
	}

	private static class InstanceIterator<E> implements Iterator<E> {

		private final LazyExtensionManager<E>.Descriptor[] descriptors;

		public InstanceIterator(LazyExtensionManager<E>.Descriptor[] descriptors) {
			this.descriptors = descriptors;
		}

		private int index = 0;
		private boolean nextEvaluated = false;
		private E next = null;

		public boolean hasNext() {
			if (!nextEvaluated) {
				evaluateNext();
				nextEvaluated = true;
			}
			return next != null;
		}

		private void evaluateNext() {
			while (index < descriptors.length) {
				next = descriptors[index++].get();
				if (next != null) {
					return;
				}
			}
		}

		public E next() {
			if (!hasNext()) {
				throw new NoSuchElementException();
			}
			final E result = next;
			next = null;
			nextEvaluated = false;
			return result;
		}

		public void remove() {
			throw new UnsupportedOperationException();
		}

	}

	private final String extensionPoint;
	protected final String classAttr = "class"; //$NON-NLS-1$

	/**
	 * @param extensionPoint
	 * @param elementType
	 */
	public LazyExtensionManager(String extensionPoint) {
		this.extensionPoint = extensionPoint;
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
		return internalGetInstances();
	}

	private synchronized Descriptor[] internalGetInstances() {
		if (extensions == null) {
			initialize();
		}
		@SuppressWarnings("unchecked")
		final Descriptor[] resultArray = new LazyExtensionManager.Descriptor[extensions
				.size()];
		extensions.toArray(resultArray);
		return resultArray;
	}

	/**
	 * Returns instance iterator
	 * 
	 * @return
	 */
	public Iterator<E> iterator() {
		return new InstanceIterator<E>(internalGetInstances());
	}

	private synchronized void remove(Descriptor descriptor) {
		if (extensions != null) {
			extensions.remove(descriptor);
		}
	}

	private void initialize() {
		extensions = new ArrayList<Descriptor>(5);
		registerConfigurationElements();
		initializeDescriptors(extensions);
	}

	protected void registerConfigurationElements() {
		registerConfigurationElements(Platform.getExtensionRegistry()
				.getConfigurationElementsFor(extensionPoint));
	}

	protected void registerConfigurationElements(
			IConfigurationElement[] confElements) {
		for (int i = 0; i < confElements.length; i++) {
			final IConfigurationElement confElement = confElements[i];
			final Descriptor descriptor = createDescriptor(confElement);
			if (isValidDescriptor(descriptor)) {
				extensions.add(descriptor);
			}
		}
	}

	/**
	 * @param confElement
	 * @return
	 */
	protected Descriptor createDescriptor(IConfigurationElement confElement) {
		return new Descriptor(confElement);
	}

	protected boolean isValidDescriptor(Descriptor descriptor) {
		return descriptor != null;
	}

	/**
	 * @param descriptors
	 */
	protected void initializeDescriptors(List<Descriptor> descriptors) {
		// empty
	}

}
