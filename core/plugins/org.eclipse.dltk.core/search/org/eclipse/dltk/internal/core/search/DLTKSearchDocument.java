/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.eclipse.dltk.internal.core.search;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.dltk.core.search.IDLTKSearchScope;
import org.eclipse.dltk.core.search.SearchDocument;
import org.eclipse.dltk.core.search.SearchParticipant;

public class DLTKSearchDocument extends SearchDocument {
	protected char[] charContents;

	public DLTKSearchDocument(String path,
			char[] contents, SearchParticipant participant) {
		super(IDLTKSearchScope.FILE_ENTRY_SEPARATOR + path, participant);
		this.charContents = contents;
	}

	public String getContents() {		
		return new String(charContents);
	}

	public char[] getCharContents() {
		return charContents;
	}

	public String getEncoding() {
		try {
			return ResourcesPlugin.getWorkspace().getRoot().getDefaultCharset();
		} catch (CoreException e) {
			// use no encoding
		}
		return null;
	}

	public String toString() {
		return "SearchDocument for " + getPath(); //$NON-NLS-1$
	}
}
