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

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.dltk.core.index2.search.ISearchEngine.MatchRule;

/**
 * Data access object for model element
 * 
 * @author michael
 */
public interface IElementDao {

	/**
	 * Adds new element entry to the batch insert procedure. You have to call
	 * {@link #commit()} after all elements are inserted.
	 * 
	 * @param connection
	 *            Database connection
	 * @param type
	 *            Element type
	 * @param flags
	 *            Element flags
	 * @param offset
	 *            Element offset
	 * @param length
	 *            Element length
	 * @param nameOffset
	 *            Element name offset
	 * @param nameLength
	 *            Element name length
	 * @param name
	 *            Element name
	 * @param metadata
	 *            Element metadata
	 * @param qualifier
	 *            Element qualifier (package)
	 * @param parent
	 *            Element parent (declaring type, for example)
	 * @param fileId
	 *            Source file foreign key
	 * @param natureId
	 *            Language nature
	 * @param isReference
	 *            Whether this element is reference or declaration
	 * @return Element DAO or <code>null</code> if insert was not successful
	 * @throws SQLException
	 */
	void insert(Connection connection, int type, int flags, int offset,
			int length, int nameOffset, int nameLength, String name,
			String metadata, String qualifier, String parent, int fileId,
			String natureId, boolean isReference) throws SQLException;

	/**
	 * Commits previously inserted entries
	 * 
	 * @throws SQLException
	 */
	void commitInsertions() throws SQLException;

	/**
	 * Search elements in index.
	 * 
	 * @param connection
	 *            Database connection
	 * @param pattern
	 *            Element name pattern (<code>null</code> or empty string -
	 *            disable filtering by name)
	 * @param matchRule
	 *            Element match type
	 * @param elementType
	 *            Element type
	 * @param trueFlags
	 *            Logical OR of flags that must exist in element flags bitset.
	 *            Set to <code>0</code> to disable filtering by trueFlags.
	 * @param falseFlags
	 *            Logical OR of flags that must not exist in the element flags
	 *            bitset. Set to <code>0</code> to disable filtering by
	 *            falseFlags.
	 * @param qualifier
	 *            Element qualifier (<code>null</code> - disable filtering by
	 *            qualifier)
	 * @param parent
	 *            Element parent (<code>null</code> - disable filtering by
	 *            parent)
	 * @param filesId
	 *            Foreign keys of files to filter by (<code>null</code> -
	 *            disable filtering by files). Either filesId or containersId
	 *            can be used simultaneously.
	 * @param containersId
	 *            Foreign keys of container paths to filter by (
	 *            <code>null</code> - disable filtering by containers). Either
	 *            filesId or containersId can be used simultaneously.
	 * @param natureId
	 *            Language nature foreign key (<code>0</code> - disable
	 *            filtering by nature)
	 * @param limit
	 *            Records limit (<code>0</code> - disable limit)
	 * @param isReference
	 *            Whether to search element reference or declaration.
	 * @param handler
	 *            Elements are returned through this handler
	 * @param monitor
	 *            Progress monitor (can be set to <code>null</code>)
	 * @throws SQLException
	 */
	void search(Connection connection, String pattern, MatchRule matchRule,
			int elementType, int trueFlags, int falseFlags, String qualifier,
			String parent, int[] filesId, int containersId[], String natureId,
			int limit, boolean isReference, IElementHandler handler,
			IProgressMonitor monitor) throws SQLException;

}