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
import java.util.TooManyListenersException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.dltk.core.IDLTKLanguageToolkit;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.mixin.MixinModelRegistry;

public class MixinReconcileSourceModuleRequest extends MixinSourceModuleRequest {

	/**
	 * @param module
	 * @param toolkit
	 */
	public MixinReconcileSourceModuleRequest(ISourceModule module,
			IDLTKLanguageToolkit toolkit) {
		super(module, toolkit);
	}

	/*
	 * @see org.eclipse.dltk.internal.core.mixin.MixinSourceModuleRequest#run()
	 */
	protected void run() throws CoreException, IOException {
		MixinModelRegistry.removeSourceModule(toolkit, module);
		super.run();
	}

	public boolean equals(Object obj) {
		if (obj instanceof MixinReconcileSourceModuleRequest) {
			final MixinReconcileSourceModuleRequest other = (MixinReconcileSourceModuleRequest) obj;
			return module.equals(other.module);
		}
		return false;
	}

}
