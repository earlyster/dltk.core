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

import java.io.File;
import java.io.IOException;

import org.eclipse.core.runtime.CoreException;

public class RemoveIndexRequest extends MixinIndexRequest {

	private final String indexLocation;

	public RemoveIndexRequest(String indexLocation) {
		this.indexLocation = indexLocation;
	}

	protected String getName() {
		return indexLocation;
	}

	protected void run() throws CoreException, IOException {
		new File(indexLocation).delete();
	}

	public boolean equals(Object obj) {
		if (obj instanceof RemoveIndexRequest) {
			final RemoveIndexRequest other = (RemoveIndexRequest) obj;
			return indexLocation.equals(other.indexLocation);
		}
		return false;
	}

}
