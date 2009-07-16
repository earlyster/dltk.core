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
package org.eclipse.dltk.internal.core.index2;

import java.io.IOException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.dltk.core.IDLTKLanguageToolkit;
import org.eclipse.dltk.core.index2.IIndexer;

/**
 * Request for removing container path from the index. All elements related to
 * the container path must be removed as well.
 * 
 * @author michael
 * 
 */
public class RemoveContainerRequest extends AbstractIndexRequest {

	private final IPath containerPath;
	private final IDLTKLanguageToolkit toolkit;

	public RemoveContainerRequest(AbstractProjectIndexer indexer,
			IPath containerPath, IDLTKLanguageToolkit toolkit) {
		super(indexer);
		this.containerPath = containerPath;
		this.toolkit = toolkit;
	}

	protected String getName() {
		return containerPath.toString();
	}

	protected void run() throws CoreException, IOException {
		IIndexer indexer = projectIndexer.getIndexer(toolkit.getNatureId());
		if (indexer == null) {
			return;
		}
		indexer.removeContainer(containerPath);
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		RemoveContainerRequest other = (RemoveContainerRequest) obj;
		if (containerPath == null) {
			if (other.containerPath != null)
				return false;
		} else if (!containerPath.equals(other.containerPath))
			return false;
		return true;
	}

	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((containerPath == null) ? 0 : containerPath.hashCode());
		return result;
	}
}
