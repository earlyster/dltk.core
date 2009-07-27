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
package org.eclipse.dltk.core.index.sql;

import java.sql.Connection;
import java.sql.SQLException;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;

/**
 * Abstract database access factory
 * 
 * @author michael
 * 
 */
public abstract class DbFactory {

	private static final String EXTPOINT = SqlIndex.PLUGIN_ID + ".dbFactory"; //$NON-NLS-1$
	private static final String FACTORY_ELEM = "factory"; //$NON-NLS-1$
	private static final String CLASS_ATTR = "class"; //$NON-NLS-1$

	private static DbFactory instance;
	static {
		try {
			for (IConfigurationElement element : Platform
					.getExtensionRegistry().getConfigurationElementsFor(
							EXTPOINT)) {
				if (FACTORY_ELEM.equals(element.getName())) {
					instance = (DbFactory) element
							.createExecutableExtension(CLASS_ATTR);
				}
			}
		} catch (Exception e) {
			SqlIndex
					.error(
							"An exception has occurred while creating database factory",
							e);
		}
	}

	/**
	 * Returns current DAO factory provided through extension point
	 * 
	 * @return
	 */
	public static DbFactory getInstance() {
		return instance;
	}

	/**
	 * Creates connection to the database. Clients are responsible for closing
	 * this connection when not needed anymore.
	 * 
	 * @return connection Database connection
	 * @throws SQLException
	 */
	public abstract Connection createConnection() throws SQLException;

	/**
	 * Dispose connection pool
	 * 
	 * @throws SQLException
	 */
	public abstract void dispose() throws SQLException;

	/**
	 * Returns concrete DBMS implementation of element DAO
	 * 
	 * @return element DAO
	 */
	public abstract IElementDao getElementDao();

	/**
	 * Returns concrete DBMS implementation of file DAO
	 * 
	 * @return file DAO
	 */
	public abstract IFileDao getFileDao();

	/**
	 * Returns concrete DBMS implementation of container DAO
	 * 
	 * @return container DAO
	 */
	public abstract IContainerDao getContainerDao();
}