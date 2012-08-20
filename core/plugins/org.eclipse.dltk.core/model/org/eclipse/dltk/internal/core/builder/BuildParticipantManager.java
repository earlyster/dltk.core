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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.builder.IBuildParticipant;
import org.eclipse.dltk.core.builder.IBuildParticipantFactory;
import org.eclipse.dltk.core.builder.IBuildParticipantFilter;
import org.eclipse.dltk.core.builder.IBuildParticipantFilterFactory;
import org.eclipse.dltk.internal.core.builder.BuildParticipantManager.FactoryValue;
import org.eclipse.dltk.utils.NatureExtensionManager;
import org.eclipse.osgi.util.NLS;

public class BuildParticipantManager extends
		NatureExtensionManager<FactoryValue> {

	private static final String EXT_POINT = DLTKCore.PLUGIN_ID
			+ ".buildParticipant"; //$NON-NLS-1$

	public static class FactoryValue<T> {
		final T factory;

		public FactoryValue(T factory) {
			this.factory = factory;
		}
	}

	public static class BuildParticipantFactoryValue extends
			FactoryValue<IBuildParticipantFactory> {
		final String id;
		final String name;
		public final Set<String> requirements = new HashSet<String>();

		/**
		 * @param factory
		 */
		public BuildParticipantFactoryValue(IBuildParticipantFactory factory,
				String id, String name) {
			super(factory);
			this.id = id != null ? id : factory.getClass().getName();
			this.name = name;
		}

	}

	public static class FilterFactoryValue extends
			FactoryValue<IBuildParticipantFilterFactory> {

		public FilterFactoryValue(IBuildParticipantFilterFactory factory) {
			super(factory);
		}

	}

	private BuildParticipantManager() {
		super(EXT_POINT, FactoryValue.class);
	}

	private static final String REQUIRES = "requires"; //$NON-NLS-1$
	private static final String REQUIRES_ID = "id"; //$NON-NLS-1$

	private static final String PARTICIPANT = "buildParticipant";
	private static final String FILTER = "filter";

	private static final String ATTR_ID = "id"; //$NON-NLS-1$
	private static final String ATTR_NAME = "name"; //$NON-NLS-1$

	protected Object createInstanceByDescriptor(Object input)
			throws CoreException {
		final IConfigurationElement element = (IConfigurationElement) input;
		if (PARTICIPANT.equals(element.getName())) {
			final Object factory = element.createExecutableExtension(classAttr);
			if (!(factory instanceof IBuildParticipantFactory)) {
				DLTKCore.warn(NLS.bind(
						"{0} contributed by {1} must implement {2}",
						new Object[] { element.getName(),
								element.getContributor(),
								IBuildParticipantFactory.class.getName() }));
				return null;
			}
			final BuildParticipantFactoryValue factoryValue = new BuildParticipantFactoryValue(
					(IBuildParticipantFactory) factory,
					element.getAttribute(ATTR_ID),
					element.getAttribute(ATTR_NAME));
			final IConfigurationElement[] requires = element
					.getChildren(REQUIRES);
			for (int i = 0; i < requires.length; ++i) {
				final String id = requires[i].getAttribute(REQUIRES_ID);
				if (id != null) {
					factoryValue.requirements.add(id);
				}
			}
			return factoryValue;
		} else if (FILTER.equals(element.getName())) {
			final Object factory = element.createExecutableExtension(classAttr);
			if (!(factory instanceof IBuildParticipantFilterFactory)) {
				DLTKCore.warn(NLS
						.bind("{0} contributed by {1} must implement {2}",
								new Object[] {
										element.getName(),
										element.getContributor(),
										IBuildParticipantFilterFactory.class
												.getName() }));
				return null;
			}
			return new FilterFactoryValue(
					(IBuildParticipantFilterFactory) factory);
		} else {
			DLTKCore.warn(NLS
					.bind("Wrong element {0} in {1} extension point contributed by {2}",
							new Object[] { element.getName(), extensionPoint,
									element.getContributor() }));
			return null;
		}
	}

	private static BuildParticipantManager instance = null;

	private static synchronized BuildParticipantManager getInstance() {
		if (instance == null) {
			instance = new BuildParticipantManager();
		}
		return instance;
	}

	private static final IBuildParticipant[] NO_PARTICIPANTS = new IBuildParticipant[0];

	private static final IBuildParticipantFilter[] NO_PREDICATES = new IBuildParticipantFilter[0];

	public static class BuildParticipantResult {
		public final IBuildParticipant[] participants;
		public final Map<IBuildParticipant, List<IBuildParticipant>> dependencies;

		public BuildParticipantResult(IBuildParticipant[] participants,
				Map<IBuildParticipant, List<IBuildParticipant>> dependencies) {
			this.participants = participants;
			this.dependencies = dependencies;
		}
	}

	/**
	 * Returns {@link IBuildParticipant} instances of the specified nature. If
	 * there are no build participants then the empty array is returned.
	 * 
	 * @param project
	 * @param natureId
	 * @return
	 */
	public static BuildParticipantResult getBuildParticipants(
			IScriptProject project, String natureId) {
		final FactoryValue<?>[] factories = getInstance()
				.getInstances(natureId);
		if (factories == null || factories.length == 0) {
			return new BuildParticipantResult(NO_PARTICIPANTS, null);
		}
		return createParticipants(project, factories);
	}

	public static BuildParticipantResult createParticipants(
			IScriptProject project, FactoryValue<?>[] factories) {
		final IBuildParticipant[] result = new IBuildParticipant[factories.length];
		final Set<String> processed = new HashSet<String>();
		final Map<String, IBuildParticipant> created = new HashMap<String, IBuildParticipant>();
		final Map<IBuildParticipant, List<IBuildParticipant>> dependencies = new HashMap<IBuildParticipant, List<IBuildParticipant>>();
		for (;;) {
			final int iterationStartCount = created.size();
			for (int i = 0; i < factories.length; ++i) {
				if (!(factories[i] instanceof BuildParticipantFactoryValue)) {
					continue;
				}
				final BuildParticipantFactoryValue factory = (BuildParticipantFactoryValue) factories[i];
				if (!processed.contains(factory.id)
						&& created.keySet().containsAll(factory.requirements)) {
					processed.add(factory.id);
					try {
						final IBuildParticipant participant = factory.factory
								.createBuildParticipant(project);
						if (participant != null) {
							result[created.size()] = participant;
							created.put(factory.id, participant);
							if (!factory.requirements.isEmpty()) {
								for (String req : factory.requirements) {
									final IBuildParticipant reqParticipant = created
											.get(req);
									List<IBuildParticipant> depList = dependencies
											.get(reqParticipant);
									if (depList == null) {
										depList = new ArrayList<IBuildParticipant>();
										dependencies.put(reqParticipant,
												depList);
									}
									depList.add(participant);
								}
							}
						}
					} catch (CoreException e) {
						final String tpl = Messages.BuildParticipantManager_buildParticipantCreateError;
						DLTKCore.warn(NLS.bind(tpl, factory.id), e);
					}
				}
			}
			if (iterationStartCount == created.size()) {
				break;
			}
		}
		if (created.size() != result.length) {
			if (created.size() == 0) {
				return new BuildParticipantResult(NO_PARTICIPANTS, null);
			}
			final IBuildParticipant[] newResult = new IBuildParticipant[created
					.size()];
			System.arraycopy(result, 0, newResult, 0, created.size());
			return new BuildParticipantResult(newResult, dependencies);
		} else {
			return new BuildParticipantResult(result, dependencies);
		}
	}

	public static IBuildParticipantFilter[] getFilters(IScriptProject project,
			String natureId, Object context) {
		final FactoryValue<?>[] factories = getInstance()
				.getInstances(natureId);
		if (factories == null || factories.length == 0) {
			return NO_PREDICATES;
		}
		return createFilters(project, factories, context);
	}

	public static IBuildParticipantFilter[] createFilters(
			IScriptProject project, FactoryValue<?>[] factories, Object context) {
		final IBuildParticipantFilter[] result = new IBuildParticipantFilter[factories.length];
		int created = 0;
		for (int i = 0; i < factories.length; ++i) {
			if (!(factories[i] instanceof FilterFactoryValue)) {
				continue;
			}
			final FilterFactoryValue factory = (FilterFactoryValue) factories[i];
			try {
				final IBuildParticipantFilter filter = factory.factory
						.createPredicate(project, context);
				if (filter != null) {
					result[created++] = filter;
				}
			} catch (CoreException e) {
				final String tpl = Messages.BuildParticipantManager_buildParticipantCreateError;
				DLTKCore.warn(
						NLS.bind(tpl, factory.factory.getClass().getName()), e);
			}
		}
		if (created != result.length) {
			if (created == 0) {
				return NO_PREDICATES;
			}
			final IBuildParticipantFilter[] newResult = new IBuildParticipantFilter[created];
			System.arraycopy(result, 0, newResult, 0, created);
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
