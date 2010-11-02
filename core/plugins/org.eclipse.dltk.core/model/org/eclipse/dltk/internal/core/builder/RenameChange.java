/*******************************************************************************
 * Copyright (c) 2010 xored software, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     xored software, Inc. - initial API and Implementation (Alex Panchenko)
 *******************************************************************************/
package org.eclipse.dltk.internal.core.builder;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.dltk.core.builder.IRenameChange;

public class RenameChange implements IRenameChange {

	private final IPath source;
	private final IFile target;

	public RenameChange(IPath source, IFile target) {
		this.source = source;
		this.target = target;
	}

	public IPath getSource() {
		return source;
	}

	public IFile getTarget() {
		return target;
	}

}
