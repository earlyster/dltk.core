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
package org.eclipse.dltk.core.search.indexing.core;

import java.io.IOException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.dltk.core.IDLTKLanguageToolkit;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.mixin.MixinModelRegistry;
import org.eclipse.dltk.core.search.indexing.IProjectIndexer;

public class ReconcileSourceModuleRequest extends SourceModuleRequest {

	/**
	 * @param module
	 * @param toolkit
	 */
	public ReconcileSourceModuleRequest(IProjectIndexer indexer,
			ISourceModule module, IDLTKLanguageToolkit toolkit) {
		super(indexer, module, toolkit);
	}

	/*
	 * @see org.eclipse.dltk.internal.core.mixin.MixinSourceModuleRequest#run()
	 */
	protected void run() throws CoreException, IOException {
		MixinModelRegistry.removeSourceModule(toolkit, module);
		super.run();
	}

	public boolean equals(Object obj) {
		if (obj instanceof ReconcileSourceModuleRequest) {
			return super.equals(obj);
		}
		return false;
	}
}
