/*******************************************************************************
 * Copyright (c) 2005, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.dltk.core.environment;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.dltk.compiler.util.Util;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.DLTKLanguageManager;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.core.internal.environment.LocalEnvironment;
import org.eclipse.dltk.internal.core.BuildpathValidation;
import org.eclipse.dltk.internal.core.ExternalScriptProject;
import org.eclipse.dltk.internal.core.ModelManager;
import org.eclipse.dltk.internal.core.ScriptProject;
import org.eclipse.dltk.utils.ExecutableOperation;
import org.eclipse.dltk.utils.ExecutionContexts;
import org.eclipse.dltk.utils.LazyExtensionManager;
import org.eclipse.dltk.utils.LazyExtensionManager.Descriptor;
import org.eclipse.osgi.util.NLS;

public final class EnvironmentManager {
	private static final QualifiedName PROJECT_ENVIRONMENT = new QualifiedName(
			DLTKCore.PLUGIN_ID, "environment"); //$NON-NLS-1$

	private static final String ENVIRONMENT_EXTENSION = DLTKCore.PLUGIN_ID
			+ ".environment"; //$NON-NLS-1$

	private static class EnvironmentProviderManager extends
			LazyExtensionManager<IEnvironmentProvider> {

		private static class EnvironmentProviderDesc extends
				Descriptor<IEnvironmentProvider> {
			private String id;
			private int priority;

			public EnvironmentProviderDesc(EnvironmentProviderManager manager,
					IConfigurationElement configurationElement) {
				super(manager, configurationElement);
				this.priority = parseInt(configurationElement
						.getAttribute("priority")); //$NON-NLS-1$
				this.id = configurationElement.getAttribute("id"); //$NON-NLS-1$
			}

			public String getId() {
				return id;
			}

		}

		public EnvironmentProviderManager() {
			super(ENVIRONMENT_EXTENSION);
		}

		@Override
		protected Descriptor<IEnvironmentProvider> createDescriptor(
				IConfigurationElement confElement) {
			return new EnvironmentProviderDesc(this, confElement);
		}

		@Override
		protected void initializeDescriptors(
				List<Descriptor<IEnvironmentProvider>> descriptors) {
			Collections.sort(descriptors,
					new Comparator<Descriptor<IEnvironmentProvider>>() {
						public int compare(
								Descriptor<IEnvironmentProvider> arg0,
								Descriptor<IEnvironmentProvider> arg1) {
							EnvironmentProviderDesc d1 = (EnvironmentProviderDesc) arg0;
							EnvironmentProviderDesc d2 = (EnvironmentProviderDesc) arg1;
							return d1.priority - d2.priority;
						}
					});
		}

	}

	private static final EnvironmentProviderManager manager = new EnvironmentProviderManager();

	private static ListenerList listeners = new ListenerList();

	private static Map<IProject, IEnvironment> environmentCache = new HashMap<IProject, IEnvironment>();

	private static IResourceChangeListener resourceListener = new IResourceChangeListener() {

		public void resourceChanged(IResourceChangeEvent event) {
			int eventType = event.getType();
			IResource resource = event.getResource();

			switch (eventType) {
			case IResourceChangeEvent.PRE_DELETE:
				if (resource.getType() == IResource.PROJECT
						&& DLTKLanguageManager
								.hasScriptNature((IProject) resource)) {

					synchronized (environmentCache) {
						environmentCache.remove(resource);
					}
				}
				return;
			}
		}
	};

	private EnvironmentManager() {
		ResourcesPlugin.getWorkspace().addResourceChangeListener(
				resourceListener);
	}

	/**
	 * Returns {@link IEnvironmentProvider} with the specified
	 * <code>providerId</code> or <code>null</code>.
	 * 
	 * @since 2.0
	 */
	public static IEnvironmentProvider getEnvironmentProvider(String providerId) {
		if (providerId != null) {
			for (Descriptor<IEnvironmentProvider> descriptor : manager
					.getDescriptors()) {
				final EnvironmentProviderManager.EnvironmentProviderDesc desc = (EnvironmentProviderManager.EnvironmentProviderDesc) descriptor;
				if (providerId.equals(desc.getId())) {
					return desc.get();
				}
			}
		}
		return null;
	}

	public static IEnvironment getEnvironment(IModelElement element) {
		if (element == null) {
			return null;
		}
		IResource res = element.getResource();
		if (res != null && res.getType() != IResource.PROJECT) {
			URI locationURI = res.getLocationURI();
			if (locationURI != null) {
				for (IEnvironmentProvider provider : manager) {
					waitInitialized(provider);
					IEnvironment env = provider.getEnvironment(locationURI);
					if (env != null) {
						return env;
					}
				}
			}
		}
		IScriptProject scriptProject = element.getScriptProject();
		if (scriptProject == null) {
			return null;
		}
		IProject project = scriptProject.getProject();
		if (project == null)
			return null;

		return getEnvironment(project);
	}

	public static IEnvironment getEnvironment(IResource element) {
		if (element == null) {
			return null;
		}
		if (element.getType() != IResource.PROJECT) {
			URI locationURI = element.getLocationURI();
			if (locationURI != null) {
				for (IEnvironmentProvider provider : manager) {
					waitInitialized(provider);
					IEnvironment env = provider.getEnvironment(locationURI);
					if (env != null) {
						return env;
					}
				}
			}
		}
		IProject project = element.getProject();
		if (project == null)
			return null;

		return getEnvironment(project);
	}

	public static IEnvironment getEnvironment(IProject project) {
		synchronized (environmentCache) {
			IEnvironment environment = environmentCache.get(project);
			if (environment == null) {
				if (!ExternalScriptProject.EXTERNAL_PROJECT_NAME.equals(project
						.getName())) {
					try {
						final String environmentId = project
								.getPersistentProperty(PROJECT_ENVIRONMENT);
						if (environmentId != null) {
							environment = getEnvironmentById(environmentId);
						}
					} catch (CoreException e) {
						if (DLTKCore.DEBUG) {
							e.printStackTrace();
						}
					}
				}
				if (environment == null) {
					environment = detectEnvironment(project);
				}
				environmentCache.put(project, environment);
			}
			return environment;
		}
	}

	/**
	 * @since 2.0
	 */
	public static IEnvironment detectEnvironment(IProject project) {
		for (IEnvironmentProvider provider : manager) {
			waitInitialized(provider);
			IEnvironment environment = provider.getProjectEnvironment(project);
			if (environment != null) {
				return environment;
			}
		}
		return null;
	}

	public static String getEnvironmentId(IProject project) {
		return getEnvironmentId(project, true);
	}

	public static String getEnvironmentId(IProject project,
			boolean detectAutomatically) {
		try {
			final String environmentId = project
					.getPersistentProperty(PROJECT_ENVIRONMENT);
			if (environmentId != null) {
				return environmentId;
			}
		} catch (CoreException e) {
			if (DLTKCore.DEBUG) {
				e.printStackTrace();
			}
		}
		if (detectAutomatically) {
			final IEnvironment environment = detectEnvironment(project);
			return environment != null ? environment.getId() : null;
		}
		return null;
	}

	public static void setEnvironmentId(IProject project, String environmentId)
			throws CoreException {
		setEnvironmentId(project, environmentId, true);
	}

	public static void setEnvironmentId(IProject project, String environmentId,
			boolean refresh) throws CoreException {
		// TODO check project.getDescription.getLocationURI() scheme ?
		project.setPersistentProperty(PROJECT_ENVIRONMENT, environmentId);
		if (refresh) {
			final IScriptProject scriptProject = DLTKCore.create(project);
			if (scriptProject != null) {
				DLTKCore.refreshBuildpathContainers(scriptProject);
				new BuildpathValidation((ScriptProject) scriptProject)
						.validate();
			}
		}
	}

	/**
	 * @since 2.0
	 */
	public static void refreshBuildpathContainersForMixedProjects(
			IProgressMonitor monitor) {
		try {
			SubMonitor subMonitor = SubMonitor.convert(monitor, 100);
			final IScriptProject[] projects = ModelManager.getModelManager()
					.getModel().getScriptProjects();
			subMonitor.worked(10);
			subMonitor = subMonitor.newChild(90);
			subMonitor.beginTask(Util.EMPTY_STRING, projects.length);
			for (int i = 0; i < projects.length; i++) {
				final IProject project = projects[i].getProject();
				final SubMonitor projectMonitor = subMonitor.newChild(1);
				projectMonitor.beginTask(NLS.bind(
						Messages.EnvironmentManager_RefreshProjectInterpreter,
						project.getName()), 2);
				final String property = project
						.getPersistentProperty(PROJECT_ENVIRONMENT);
				if (property != null) {
					DLTKCore.refreshBuildpathContainers(projects[i]);
					projectMonitor.worked(1);
					new BuildpathValidation((ScriptProject) projects[i])
							.validate();
					projectMonitor.worked(1);
				}
			}
		} catch (ModelException e) {
			if (DLTKCore.DEBUG) {
				e.printStackTrace();
			}
		} catch (CoreException e) {
			if (DLTKCore.DEBUG) {
				e.printStackTrace();
			}
		} finally {
			monitor.done();
		}
	}

	public static void setEnvironment(IProject project, IEnvironment environment)
			throws CoreException {
		setEnvironmentId(project, environment != null ? environment.getId()
				: null);
	}

	public static IEnvironment[] getEnvironments() {
		return getEnvironments(true);
	}

	public static IEnvironment[] getEnvironments(boolean allowWait) {
		List<IEnvironment> envList = new ArrayList<IEnvironment>();
		for (IEnvironmentProvider provider : manager) {
			if (allowWait) {
				waitInitialized(provider);
			}
			envList.addAll(Arrays.asList(provider.getEnvironments()));
		}
		IEnvironment[] environments = new IEnvironment[envList.size()];
		envList.toArray(environments);
		return environments;
	}

	public static boolean isLocal(IEnvironment env) {
		return LocalEnvironment.ENVIRONMENT_ID.equals(env.getId());
	}

	public static IEnvironment getEnvironmentById(String envId) {
		for (IEnvironmentProvider provider : manager) {
			waitInitialized(provider);
			IEnvironment env = provider.getEnvironment(envId);
			if (env != null) {
				return env;
			}
		}
		return null;
	}

	public static void addEnvironmentChangedListener(
			IEnvironmentChangedListener listener) {
		listeners.add(listener);
	}

	public static void removeEnvironmentChangedListener(
			IEnvironmentChangedListener listener) {
		listeners.remove(listener);
	}

	public static void environmentAdded(IEnvironment environment) {
		Object[] environmentListeners = listeners.getListeners();
		for (int i = 0; i < environmentListeners.length; i++) {
			IEnvironmentChangedListener listener = (IEnvironmentChangedListener) environmentListeners[i];
			listener.environmentAdded(environment);
		}
		fireEnvirontmentChange();
	}

	public static void environmentRemoved(IEnvironment environment) {
		Object[] environmentListeners = listeners.getListeners();
		for (int i = 0; i < environmentListeners.length; i++) {
			IEnvironmentChangedListener listener = (IEnvironmentChangedListener) environmentListeners[i];
			listener.environmentRemoved(environment);
		}
		fireEnvirontmentChange();
	}

	public static void environmentChanged(IEnvironment environment) {
		Object[] environmentListeners = listeners.getListeners();
		for (int i = 0; i < environmentListeners.length; i++) {
			IEnvironmentChangedListener listener = (IEnvironmentChangedListener) environmentListeners[i];
			listener.environmentChanged(environment);
		}
		fireEnvirontmentChange();
	}

	public static void fireEnvirontmentChange() {
		Object[] environmentListeners = listeners.getListeners();
		for (int i = 0; i < environmentListeners.length; i++) {
			IEnvironmentChangedListener listener = (IEnvironmentChangedListener) environmentListeners[i];
			listener.environmentsModified();
		}
	}

	public static IEnvironment getLocalEnvironment() {
		return getEnvironmentById(LocalEnvironment.ENVIRONMENT_ID);
	}

	/**
	 * Tests if all providers are initialized.
	 */
	public static boolean isInitialized() {
		for (IEnvironmentProvider provider : manager) {
			if (!provider.isInitialized()) {
				return false;
			}
		}
		return true;
	}

	private static void waitInitialized(final IEnvironmentProvider provider) {
		if (provider.isInitialized()) {
			return;
		}
		ExecutionContexts.getManager().executeInBackground(
				new ExecutableOperation(
						Messages.EnvironmentManager_initializingOperationName) {
					public void execute(IProgressMonitor monitor) {
						monitor.beginTask(Util.EMPTY_STRING, 1);
						monitor
								.setTaskName(NLS
										.bind(
												Messages.EnvironmentManager_initializingTaskName,
												provider.getProviderName()));
						provider.waitInitialized();
						monitor.worked(1);
						monitor.done();
					}
				});
	}

	/**
	 * Waits until all structures are initialized.
	 */
	public static void waitInitialized() {
		waitInitialized((IProgressMonitor) null);
	}

	public static void waitInitialized(IProgressMonitor monitor) {
		if (monitor != null) {
			monitor.beginTask(Util.EMPTY_STRING,
					manager.getDescriptors().length);
		}
		for (IEnvironmentProvider provider : manager) {
			if (monitor != null) {
				monitor.setTaskName(NLS.bind(
						Messages.EnvironmentManager_initializingTaskName,
						provider.getProviderName()));
			}
			provider.waitInitialized();
			if (monitor != null) {
				monitor.worked(1);
			}
		}
		if (monitor != null) {
			monitor.done();
		}
	}

	private static class LocationResolverManager extends
			LazyExtensionManager<IEnvironmentLocationResolver> {

		private static class Desc extends
				Descriptor<IEnvironmentLocationResolver> {
			private int priority;

			public Desc(LocationResolverManager manager,
					IConfigurationElement configurationElement) {
				super(manager, configurationElement);
				this.priority = parseInt(configurationElement
						.getAttribute("priority")); //$NON-NLS-1$
			}

		}

		private static final String LOCATION_RESOLVER_EXTENSION = DLTKCore.PLUGIN_ID
				+ ".locationResolver"; //$NON-NLS-1$

		public LocationResolverManager() {
			super(LOCATION_RESOLVER_EXTENSION);
		}

		@Override
		protected Descriptor<IEnvironmentLocationResolver> createDescriptor(
				IConfigurationElement confElement) {
			return new Desc(this, confElement);
		}

		@Override
		protected void initializeDescriptors(
				List<Descriptor<IEnvironmentLocationResolver>> descriptors) {
			Collections.sort(descriptors,
					new Comparator<Descriptor<IEnvironmentLocationResolver>>() {
						public int compare(
								Descriptor<IEnvironmentLocationResolver> arg0,
								Descriptor<IEnvironmentLocationResolver> arg1) {
							Desc d1 = (Desc) arg0;
							Desc d2 = (Desc) arg1;
							return d1.priority - d2.priority;
						}
					});
		}
	}

	private static LocationResolverManager resolverManager = null;

	/**
	 * @since 2.0
	 */
	public static URI[] resolve(URI location) {
		if (resolverManager == null) {
			resolverManager = new LocationResolverManager();
		}
		final List<URI> result = new ArrayList<URI>();
		for (IEnvironmentLocationResolver resolver : resolverManager) {
			final URI[] resolved = resolver.resolve(location);
			if (resolved.length != 0) {
				result.addAll(Arrays.asList(resolved));
			}
		}
		return result.toArray(new URI[result.size()]);
	}

}
