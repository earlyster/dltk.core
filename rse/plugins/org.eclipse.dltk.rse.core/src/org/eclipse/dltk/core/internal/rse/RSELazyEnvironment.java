/*******************************************************************************
 * Copyright (c) 2009 xored software, Inc.  
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html  
 *
 * Contributors:
 *     xored software, Inc. - initial API and Implementation (Alex Panchenko)
 *******************************************************************************/
package org.eclipse.dltk.core.internal.rse;

import org.eclipse.dltk.core.environment.IEnvironment;
import org.eclipse.dltk.core.internal.environment.LazyEnvironment;

/**
 * @since 2.0
 */
public class RSELazyEnvironment extends LazyEnvironment {

	private final RSEEnvironmentProvider provider;

	public RSELazyEnvironment(String environmentId,
			RSEEnvironmentProvider provider) {
		super(environmentId);
		this.provider = provider;
	}

	@Override
	protected IEnvironment resolveEnvironment(String envId) {
		return provider.getEnvironment(envId, false);
	}

}
