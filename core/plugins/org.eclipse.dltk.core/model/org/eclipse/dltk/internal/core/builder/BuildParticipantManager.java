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

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.builder.IBuildParticipant;
import org.eclipse.dltk.core.builder.IBuildParticipantFactory;
import org.eclipse.dltk.utils.NatureExtensionManager;
import org.eclipse.osgi.util.NLS;

public class BuildParticipantManager extends NatureExtensionManager {

	private static final String EXT_POINT = DLTKCore.PLUGIN_ID
			+ ".buildParticipant"; //$NON-NLS-1$

	public static class BuildParticipantDescriptor {
		final IBuildParticipantFactory factory;
		final String id;
		final String name;
		public final Set<String> requirements = new HashSet<String>();

		/**
		 * @param factory
		 */
		public BuildParticipantDescriptor(IBuildParticipantFactory factory,
				String id, String name) {
			this.factory = factory;
			this.id = id != null ? id : factory.getClass().getName();
			this.name = name;
		}

	}

	private BuildParticipantManager() {
		super(EXT_POINT, BuildParticipantDescriptor.class);
	}

	private static final String REQUIRES = "requires"; //$NON-NLS-1$
	private static final String REQUIRES_ID = "id"; //$NON-NLS-1$

	private static final String ATTR_ID = "id"; //$NON-NLS-1$
	private static final String ATTR_NAME = "name"; //$NON-NLS-1$

	protected Object createInstanceByDescriptor(Object input)
			throws CoreException {
		final IConfigurationElement element = (IConfigurationElement) input;
		final Object factory = element.createExecutableExtension(classAttr);
		if (!(factory instanceof IBuildParticipantFactory)) {
			return null;
		}
		final BuildParticipantDescriptor descriptor = new BuildParticipantDescriptor(
				(IBuildParticipantFactory) factory,
				element.getAttribute(ATTR_ID), element.getAttribute(ATTR_NAME));
		final IConfigurationElement[] requires = element.getChildren(REQUIRES);
		for (int i = 0; i < requires.length; ++i) {
			final String id = requires[i].getAttribute(REQUIRES_ID);
			if (id != null) {
				descriptor.requirements.add(id);
			}
		}
		return descriptor;
	}

	private static BuildParticipantManager instance = null;

	private static synchronized BuildParticipantManager getInstance() {
		if (instance == null) {
			instance = new BuildParticipantManager();
		}
		return instance;
	}

	private static final IBuildParticipant[] NO_PARTICIPANTS = new IBuildParticipant[0];

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
		final BuildParticipantDescriptor[] descriptors = (BuildParticipantDescriptor[]) getInstance()
				.getInstances(natureId);
		if (descriptors == null || descriptors.length == 0) {
			return NO_PARTICIPANTS;
		}
		return createParticipants(project, descriptors);
	}

	public static IBuildParticipant[] createParticipants(
			IScriptProject project, BuildParticipantDescriptor[] descriptors) {
		final IBuildParticipant[] result = new IBuildParticipant[descriptors.length];
		final Set<String> processed = new HashSet<String>();
		final Set<String> created = new HashSet<String>();
		for (;;) {
			final int iterationStartCount = created.size();
			for (int i = 0; i < descriptors.length; ++i) {
				final BuildParticipantDescriptor desc = descriptors[i];
				if (!processed.contains(desc.id)
						&& created.containsAll(desc.requirements)) {
					processed.add(desc.id);
					try {
						final IBuildParticipant participant = desc.factory
								.createBuildParticipant(project);
						if (participant != null) {
							result[created.size()] = participant;
							created.add(desc.id);
						}
					} catch (CoreException e) {
						final String tpl = Messages.BuildParticipantManager_buildParticipantCreateError;
						DLTKCore.warn(NLS.bind(tpl, desc.id), e);
					}
				}
			}
			if (iterationStartCount == created.size()) {
				break;
			}
		}
		if (created.size() != result.length) {
			final IBuildParticipant[] newResult = new IBuildParticipant[created
					.size()];
			System.arraycopy(result, 0, newResult, 0, created.size());
			return newResult;
		} else {
			return result;
		}
	}

	public static IBuildParticipant[] copyFirst(IBuildParticipant[] array,
			int length) {
		if (length == array.length) {
			return array;
		}
		if (length == 0) {
			return BuildParticipantManager.NO_PARTICIPANTS;
		} else {
			IBuildParticipant[] temp = new IBuildParticipant[length];
			System.arraycopy(array, 0, temp, 0, length);
			return temp;
		}
	}

}
