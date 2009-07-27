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
package org.eclipse.dltk.internal.core.index.sql.h2;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.eclipse.dltk.core.index.sql.Container;
import org.eclipse.dltk.core.index.sql.IContainerDao;

/**
 * Container path data access object
 * 
 * @author michael
 * 
 */
public class H2ContainerDao implements IContainerDao {

	public Container insert(Connection connection, String path)
			throws SQLException {

		Container container = selectByPath(connection, path);
		if (container != null) {
			return container;
		}

		Statement statement = connection.createStatement();
		try {
			statement.execute(new StringBuilder(
					"INSERT INTO CONTAINERS(PATH) VALUES('").append(path)
					.append("');").toString(), Statement.RETURN_GENERATED_KEYS);

			ResultSet result = statement.getGeneratedKeys();
			try {
				result.next();
				container = new Container(result.getInt(1), path);
			} finally {
				result.close();
			}
		} finally {
			statement.close();
		}
		return container;
	}

	public Container selectByPath(Connection connection, String path)
			throws SQLException {

		Container container = null;
		Statement statement = connection.createStatement();
		try {
			ResultSet result = statement.executeQuery(new StringBuilder(
					"SELECT * FROM CONTAINERS WHERE PATH='").append(path)
					.append("';").toString());
			try {
				if (result.next()) {
					container = new Container(result.getInt(1), result
							.getString(2));
				}
			} finally {
				result.close();
			}
		} finally {
			statement.close();
		}
		return container;
	}

	public Container selectById(Connection connection, int key)
			throws SQLException {
		Statement statement = connection.createStatement();
		try {
			ResultSet result = statement.executeQuery(new StringBuilder(
					"SELECT * FROM CONTAINERS WHERE ID=").append(key).append(
					";").toString());
			try {
				if (result.next()) {
					return new Container(result.getInt(1), result.getString(2));
				}
			} finally {
				result.close();
			}
		} finally {
			statement.close();
		}
		return null;
	}

	public void deleteById(Connection connection, int key) throws SQLException {
		Statement statement = connection.createStatement();
		try {
			statement.executeUpdate(new StringBuilder(
					"DELETE FROM CONTAINERS WHERE ID=").append(key).append(";")
					.toString());
		} finally {
			statement.close();
		}
	}

	public void deleteByPath(Connection connection, String path)
			throws SQLException {
		Statement statement = connection.createStatement();
		try {
			statement.executeUpdate(new StringBuilder(
					"DELETE FROM CONTAINERS WHERE PATH='").append(path).append(
					"';").toString());
		} finally {
			statement.close();
		}
	}
}
