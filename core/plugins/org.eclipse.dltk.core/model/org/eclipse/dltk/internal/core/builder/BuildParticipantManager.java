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
package org.eclipse.dltk.internal.core.builder;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.builder.IBuildParticipant;
import org.eclipse.dltk.core.builder.IBuildParticipantFactory;
import org.eclipse.dltk.utils.NatureExtensionManager;
import org.eclipse.osgi.util.NLS;

public class BuildParticipantManager extends NatureExtensionManager {

	private static final String EXT_POINT = DLTKCore.PLUGIN_ID
			+ ".buildParticipant"; //$NON-NLS-1$

	private BuildParticipantManager() {
		super(EXT_POINT, IBuildParticipantFactory.class);
	}

	private static final IBuildParticipant[] NO_PARTICIPANTS = new IBuildParticipant[0];

	private static BuildParticipantManager instance = null;

	private static BuildParticipantManager getInstance() {
		if (instance == null) {
			instance = new BuildParticipantManager();
		}
		return instance;
	}

	/**
	 * Returns {@link IBuildParticipant} instances of the specified nature. If
	 * there are no build participants then the empty array is returned.
	 * 
	 * @param project
	 * @param natureId
	 * @return
	 */
	public static IBuildParticipant[] getBuildParticipants(
			IScriptProject project, String natureId) {
		final IBuildParticipantFactory[] factories = (IBuildParticipantFactory[]) getInstance()
				.getInstances(natureId);
		if (factories == null || factories.length == 0) {
			return NO_PARTICIPANTS;
		}
		final IBuildParticipant[] result = new IBuildParticipant[factories.length];
		int index = 0;
		for (int i = 0; i < factories.length; ++i) {
			try {
				final IBuildParticipant participant = factories[i]
						.newBuildParticipant(project);
				if (participant != null) {
					result[index++] = participant;
				}
			} catch (CoreException e) {
				final String tpl = Messages.BuildParticipantManager_buildParticipantCreateError;
				DLTKCore.warn(NLS.bind(tpl, factories[i].getName()), e);
			}
		}
		if (index != result.length) {
			final IBuildParticipant[] newResult = new IBuildParticipant[index];
			System.arraycopy(result, 0, newResult, 0, index);
			return newResult;
		} else {
			return result;
		}
	}
}
