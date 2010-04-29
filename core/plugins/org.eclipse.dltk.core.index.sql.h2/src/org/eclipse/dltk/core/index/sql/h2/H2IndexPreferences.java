/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Zend Technologies
 *******************************************************************************/
package org.eclipse.dltk.core.index.sql.h2;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;

public class H2IndexPreferences extends AbstractPreferenceInitializer {

	/**
	 * H2 Database index cache type.
	 * 
	 * @see http://www.h2database.com/html/features.html#cache_settings
	 */
	public static final String DB_CACHE_TYPE = "cacheType"; //$NON-NLS-1$

	/**
	 * H2 Database index cache size.
	 * 
	 * @see http://www.h2database.com/html/features.html#cache_settings
	 */
	public static final String DB_CACHE_SIZE = "cacheSize"; //$NON-NLS-1$

	/**
	 * Whether to use transaction locking.
	 * 
	 * @see http://www.h2database.com/html/grammar.html#set_lock_mode
	 */
	public static final String DB_LOCK_MODE = "lockMode"; //$NON-NLS-1$

	/**
	 * Schema version
	 */
	public static final String SCHEMA_VERSION = "schemaVersion"; //$NON-NLS-1$

	@Override
	public void initializeDefaultPreferences() {
		IEclipsePreferences p = ((IScopeContext) new DefaultScope())
				.getNode(H2Index.PLUGIN_ID);

		p.putInt(DB_CACHE_SIZE, 32000); // 32Mb
		p.put(DB_CACHE_TYPE, "LRU");
		p.putInt(DB_LOCK_MODE, 0); // no transaction isolation
	}
}
