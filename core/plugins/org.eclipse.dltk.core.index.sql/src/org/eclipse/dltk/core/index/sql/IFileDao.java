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
 * Element file data access object
 * 
 * @author michael
 * 
 */
public interface IFileDao {

	/**
	 * Inserts new type into FILES table.
	 * 
	 * @param connection
	 *            Database connection
	 * @param path
	 *            Element file
	 * @param timestamp
	 *            File last update time
	 * @param containerId
	 *            Container path
	 * @return new file DAO associated with added row
	 * @throws SQLException
	 */
	public File insert(Connection connection, String path, long timestamp,
			int containerId) throws SQLException;

	/**
	 * Selects file by path
	 * 
	 * @param connection
	 *            Database connection
	 * @param path
	 *            Element file
	 * @param containerId
	 *            Container path id
	 * @return file DAO or <code>null</code> in case it doesn't exist
	 * @throws SQLException
	 */
	public File select(Connection connection, String path, int containerId)
			throws SQLException;

	/**
	 * Selects files by conatiner path
	 * 
	 * @param connection
	 *            Database connection
	 * @param containerId
	 *            Container path id
	 * @return files DAO array
	 * @throws SQLException
	 */
	public File[] selectByContainerId(Connection connection, int containerId)
			throws SQLException;

	/**
	 * Selects file by key
	 * 
	 * @param connection
	 *            Database connection
	 * @param id
	 *            Primary key
	 * @return file DAO or <code>null</code> in case it doesn't exist
	 * @throws SQLException
	 */
	public File selectById(Connection connection, int id) throws SQLException;

	/**
	 * Deletes file from the database
	 * 
	 * @param connection
	 *            Database connection
	 * @param path
	 *            File path
	 * @param containerId
	 *            Container path ID
	 * @throws SQLException
	 */
	public void delete(Connection connection, String path, int containerId)
			throws SQLException;

	/**
	 * Deletes file from the database by primary key
	 * 
	 * @param connection
	 *            Database connection
	 * @param id
	 *            Primary key
	 * @throws SQLException
	 */
	public void deleteById(Connection connection, int id) throws SQLException;

}
