package org.eclipse.dltk.core.environment;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.SimpleClassDLTKExtensionManager;

public class EnvironmentsManager {
	private static EnvironmentsManager instance = new EnvironmentsManager();
	private static final String ENVIRONMENT_EXTENSION = DLTKCore.PLUGIN_ID
			+ ".environmentProvider";
	private static final String ENVIRONMENT_ID = DLTKCore.PLUGIN_ID
			+ ".environmentId";
	private IEnvironmentProvider[] providers;

	private EnvironmentsManager() {
		List providerList = new LinkedList();
		SimpleClassDLTKExtensionManager manager = new SimpleClassDLTKExtensionManager(
				ENVIRONMENT_EXTENSION);
		providerList.addAll(Arrays.asList(manager.getObjects()));

		providers = new IEnvironmentProvider[providerList.size()];
		providerList.toArray(providers);
	}

	public static IEnvironment getLocalEnvironment() {
		return LocalEnvironment.getInstance();
	}

	public static IEnvironment getEnvironment(IModelElement element) {
		IScriptProject project = (IScriptProject) element
				.getAncestor(IModelElement.SCRIPT_PROJECT);
		if (project == null)
			return null;

		String envId = project.getOption(ENVIRONMENT_ID, false);
		if (envId != null) {
			return getEnvironmentById(envId);
		} else {
			return getLocalEnvironment();
		}
	}

	public static IEnvironment[] getEnvironments() {
		return instance.getEnvironmentsImpl();
	}
	
	public static boolean isLocal(IEnvironment env) {
		return env == getLocalEnvironment();
	}

	public static IEnvironment getEnvironmentById(String envId) {
		return instance.getEnvironmentByIdImpl(envId);
	}

	private IEnvironment[] getEnvironmentsImpl() {
		List envList = new LinkedList();
		envList.add(getLocalEnvironment());
		for (int i = 0; i < providers.length; i++) {
			envList.addAll(Arrays.asList(providers[i].getEnvironments()));
		}
		IEnvironment[] environments = new IEnvironment[envList.size()];
		envList.toArray(environments);
		return environments;
	}

	private IEnvironment getEnvironmentByIdImpl(String envId) {
		if (LocalEnvironment.getInstance().getId().equals(envId))
			return getLocalEnvironment();

		for (int i = 0; i < providers.length; i++) {
			IEnvironment env = providers[i].getEnvironment(envId);
			if (env != null)
				return env;
		}
		return null;
	}
}
