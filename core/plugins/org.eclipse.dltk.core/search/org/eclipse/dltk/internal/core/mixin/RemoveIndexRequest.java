/*******************************************************************************
 * Copyright (c) 2008 xored software, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     xored software, Inc. - initial API and Implementation (Alex Panchenko)
 *******************************************************************************/
package org.eclipse.dltk.internal.core.mixin;

import java.io.IOException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;

public class RemoveIndexRequest extends MixinIndexRequest {

	private final IPath path;

	public RemoveIndexRequest(IPath path) {
		this.path = path;
	}

	protected String getName() {
		return path.toString();
	}

	protected void run() throws CoreException, IOException {
		getIndexManager().removeIndex(path);
	}

	public boolean equals(Object obj) {
		if (obj instanceof RemoveIndexRequest) {
			final RemoveIndexRequest other = (RemoveIndexRequest) obj;
			return path.equals(other.path);
		}
		return false;
	}

}
