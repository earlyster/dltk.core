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
import java.sql.SQLException;
import java.sql.Statement;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.dltk.core.index.sql.DbFactory;
import org.eclipse.dltk.core.index.sql.IContainerDao;
import org.eclipse.dltk.core.index.sql.IElementDao;
import org.eclipse.dltk.core.index.sql.IFileDao;
import org.eclipse.dltk.core.index.sql.h2.H2Index;
import org.eclipse.dltk.core.index.sql.h2.H2IndexPreferences;
import org.h2.jdbcx.JdbcConnectionPool;
import org.h2.tools.DeleteDbFiles;

/**
 * Abstract database access factory
 * 
 * @author michael
 * 
 */
public class H2DbFactory extends DbFactory {

	private static final String DB_NAME = "model"; //$NON-NLS-1$
	private static final String DB_USER = ""; //$NON-NLS-1$
	private static final String DB_PASS = ""; //$NON-NLS-1$
	private JdbcConnectionPool pool;

	public H2DbFactory() throws SQLException {
		try {
			Class.forName("org.h2.Driver");
		} catch (ClassNotFoundException e) {
		}

		IPath dbPath = H2Index.getDefault().getStateLocation();
		String connString = getConnectionString(dbPath);

		pool = JdbcConnectionPool.create(connString, DB_USER, DB_PASS);

		Schema schema = new Schema();
		boolean initializeSchema = false;

		Connection connection = pool.getConnection();
		try {
			Statement statement = connection.createStatement();
			try {
				statement.executeQuery("SELECT COUNT(*) FROM FILES WHERE 1=0;");
				initializeSchema = !schema.isCompatible();

			} catch (SQLException e) {
				// Basic table doesn't exist
				initializeSchema = true;
			} finally {
				statement.close();
			}

			if (initializeSchema) {
				connection.close();
				pool.dispose();
				DeleteDbFiles.execute(dbPath.toOSString(), DB_NAME, true);

				pool = JdbcConnectionPool.create(connString, DB_USER, DB_PASS);
				connection = pool.getConnection();
				schema.initialize(connection);
			}
		} finally {
			connection.close();
		}
	}

	/**
	 * Generates connection string using user preferences
	 * 
	 * @param dbPath
	 *            Path to the database files
	 * @return
	 */
	private String getConnectionString(IPath dbPath) {

		IPreferencesService preferencesService = Platform
				.getPreferencesService();

		StringBuilder buf = new StringBuilder("jdbc:h2:").append(dbPath.append(
				DB_NAME).toOSString());

		buf.append(";UNDO_LOG=0");
		buf.append(";LOCK_MODE=").append(
				preferencesService.getInt(H2Index.PLUGIN_ID,
						H2IndexPreferences.DB_LOCK_MODE, 0, null));

		buf.append(";CACHE_TYPE=").append(
				preferencesService.getString(H2Index.PLUGIN_ID,
						H2IndexPreferences.DB_CACHE_TYPE, null, null));

		buf.append(";CACHE_SIZE=").append(
				preferencesService.getInt(H2Index.PLUGIN_ID,
						H2IndexPreferences.DB_CACHE_SIZE, 0, null));

		return buf.toString();
	}

	public Connection createConnection() throws SQLException {
		return pool == null ? null : pool.getConnection();
	}

	public void dispose() throws SQLException {
		if (pool != null) {
			pool.dispose();
			pool = null;
		}
	}

	public IContainerDao getContainerDao() {
		return new H2ContainerDao();
	}

	public IElementDao getElementDao() {
		return new H2ElementDao();
	}

	public IFileDao getFileDao() {
		return new H2FileDao();
	}
}