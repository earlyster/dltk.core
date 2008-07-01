/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.eclipse.dltk.core;

public interface ISourceModuleInfoCache {
	/**
	 * Info hold any kind of information associated with source module. If
	 * source module are modified, information are deleted.
	 */
	interface ISourceModuleInfo {
		Object get(Object key);

		void put(Object key, Object value);

		void remove(Object key);

		boolean isEmpty();
	}

	/**
	 * Return new cache instance for specified module.
	 */
	public ISourceModuleInfo get(ISourceModule module);

	/**
	 * Remove specified module from cache.
	 */
	public void remove(ISourceModule sourceModule);

	/**
	 * Remove all information from cache. Called then required reparse of all
	 * modules.
	 */
	public void clear();
}
