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

/**
 * Container path data access object
 * 
 * @author michael
 * 
 */
public interface IContainerDao {

	/**
	 * Inserts new type into CONTAINERS table if not exists. This method calls
	 * {@link #select(Connection, String)}
	 * 
	 * @param connection
	 *            Database connection
	 * @param path
	 *            Search container
	 * @return new container object associated with added row
	 * @throws SQLException
	 */
	public Container insert(Connection connection, String path)
			throws SQLException;

	/**
	 * Selects container by path
	 * 
	 * @param connection
	 *            Database connection
	 * @param path
	 *            Search container
	 * @return container object or <code>null</code> in case it doesn't exist
	 * @throws SQLException
	 */
	public Container selectByPath(Connection connection, String path)
			throws SQLException;

	/**
	 * Selects container by key
	 * 
	 * @param connection
	 *            Database connection
	 * @param id
	 *            Primary key
	 * @return container object or <code>null</code> in case it doesn't exist
	 * @throws SQLException
	 */
	public Container selectById(Connection connection, int id)
			throws SQLException;

	/**
	 * Deletes container from the database by primary key
	 * 
	 * @param connection
	 *            Database connection
	 * @param id
	 *            Primary key
	 * @throws SQLException
	 */
	public void deleteById(Connection connection, int id) throws SQLException;

	/**
	 * Deletes container from the database
	 * 
	 * @param connection
	 *            Database connection
	 * @param path
	 *            File path
	 * @throws SQLException
	 */
	public void deleteByPath(Connection connection, String path)
			throws SQLException;

}
