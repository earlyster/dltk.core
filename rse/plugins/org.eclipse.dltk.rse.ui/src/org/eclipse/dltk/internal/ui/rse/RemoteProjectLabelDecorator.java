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
package org.eclipse.dltk.internal.ui.rse;

import java.net.URI;

import org.eclipse.core.resources.IProject;
import org.eclipse.dltk.core.environment.EnvironmentManager;
import org.eclipse.dltk.core.environment.IEnvironment;
import org.eclipse.dltk.core.internal.rse.RSEEnvironment;
import org.eclipse.dltk.core.internal.rse.RSEEnvironmentProvider;
import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;

/**
 * Decorator to append host name for the remote projects.
 */
public class RemoteProjectLabelDecorator extends BaseLabelProvider implements
		ILightweightLabelDecorator {

	public void decorate(Object element, IDecoration decoration) {
		if (element instanceof IProject) {
			decorateProjectText((IProject) element, decoration);
		}
	}

	/**
	 * @param project
	 * @param decoration
	 * @return
	 */
	private void decorateProjectText(IProject project, IDecoration decoration) {
		final URI uri = project.getLocationURI();
		if (uri != null
				&& RSEEnvironmentProvider.RSE_SCHEME.equalsIgnoreCase(uri
						.getScheme()) && uri.getHost() != null) {
			addHostNameSuffix(decoration, uri.getHost());
		} else if (project.isOpen()) {
			final String envId = EnvironmentManager.getEnvironmentId(project,
					false);
			if (envId != null) {
				final IEnvironment environment = EnvironmentManager
						.getEnvironmentById(envId);
				if (environment instanceof RSEEnvironment) {
					final String hostName = ((RSEEnvironment) environment)
							.getHost().getHostName();
					if (hostName != null) {
						addHostNameSuffix(decoration, hostName);
						if (!environment.isConnected()) {
							decoration.addSuffix(" [Not connected]");
						}
					}
				}
			}
		}
	}

	private static void addHostNameSuffix(IDecoration decoration,
			final String hostName) {
		decoration.addSuffix(" (" + hostName.toLowerCase() + ")"); //$NON-NLS-1$ //$NON-NLS-2$
	}

}
