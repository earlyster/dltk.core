/*******************************************************************************
 * Copyright (c) 2000, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 *******************************************************************************/
package org.eclipse.dltk.core;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;

/**
 * @since 2.0
 */
public interface Archive {

	public Enumeration<? extends ArchiveEntry> getArchiveEntries();

	public String getName();

	public void close() throws IOException;

	public ArchiveEntry getArchiveEntry(String name);

	public InputStream getInputStream(ArchiveEntry entry) throws IOException;
}
