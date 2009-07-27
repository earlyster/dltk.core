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
import java.util.LinkedList;
import java.util.List;

import org.eclipse.dltk.core.index.sql.File;
import org.eclipse.dltk.core.index.sql.IFileDao;

/**
 * Element file data access object
 * 
 * @author michael
 * 
 */
public class H2FileDao implements IFileDao {

	public File insert(Connection connection, String path, long timestamp,
			int containerId) throws SQLException {

		Statement statement = connection.createStatement();
		try {
			statement.execute(new StringBuilder(
					"INSERT INTO FILES(PATH,TIMESTAMP,CONTAINER_ID) VALUES('")
					.append(path).append("',").append(timestamp).append(",")
					.append(containerId).append(");").toString(),
					Statement.RETURN_GENERATED_KEYS);

			ResultSet result = statement.getGeneratedKeys();
			try {
				result.next();
				return new File(result.getInt(1), path, timestamp, containerId);
			} finally {
				result.close();
			}
		} finally {
			statement.close();
		}
	}

	public File select(Connection connection, String path, int containerId)
			throws SQLException {

		Statement statement = connection.createStatement();
		try {
			ResultSet result = statement.executeQuery(new StringBuilder(
					"SELECT * FROM FILES WHERE PATH='").append(path).append(
					"' AND CONTAINER_ID=").append(containerId).append(";")
					.toString());
			try {
				if (result.next()) {
					return new File(result.getInt(1), result.getString(2),
							result.getLong(3), result.getInt(4));
				}
			} finally {
				result.close();
			}
		} finally {
			statement.close();
		}
		return null;
	}

	public File[] selectByContainerId(Connection connection, int containerId)
			throws SQLException {

		List<File> files = new LinkedList<File>();
		Statement statement = connection.createStatement();
		try {
			ResultSet result = statement.executeQuery(new StringBuilder(
					"SELECT * FROM FILES WHERE CONTAINER_ID=").append(
					containerId).append(";").toString());
			try {
				if (result.next()) {
					files.add(new File(result.getInt(1), result.getString(2),
							result.getLong(3), result.getInt(4)));
				}
			} finally {
				result.close();
			}
		} finally {
			statement.close();
		}
		return (File[]) files.toArray(new File[files.size()]);
	}

	public File selectById(Connection connection, int id) throws SQLException {

		Statement statement = connection.createStatement();
		try {
			ResultSet result = statement.executeQuery(new StringBuilder(
					"SELECT * FROM FILES WHERE ID=").append(id).append(";")
					.toString());
			try {
				if (result.next()) {
					return new File(result.getInt(1), result.getString(2),
							result.getLong(3), result.getInt(4));
				}
			} finally {
				result.close();
			}
		} finally {
			statement.close();
		}
		return null;
	}

	public void delete(Connection connection, String path, int containerId)
			throws SQLException {

		Statement statement = connection.createStatement();
		try {
			statement.executeUpdate(new StringBuilder(
					"DELETE FROM FILES WHERE PATH='").append(path).append(
					"' AND CONTAINER_ID=").append(containerId).append(";")
					.toString());
		} finally {
			statement.close();
		}
	}

	public void deleteById(Connection connection, int key) throws SQLException {
		Statement statement = connection.createStatement();
		try {
			statement.executeUpdate(new StringBuilder(
					"DELETE FROM FILES WHERE ID=").append(key).append(";")
					.toString());
		} finally {
			statement.close();
		}
	}
}
