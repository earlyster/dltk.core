package org.eclipse.dltk.core.internal.environment;

import org.eclipse.dltk.core.environment.IEnvironment;
import org.eclipse.dltk.core.environment.IEnvironmentProvider;

public class LocalEnvironmentProvider implements IEnvironmentProvider {

	public LocalEnvironmentProvider() {
	}

	public IEnvironment getEnvironment(String envId) {
		if (LocalEnvironment.ENVIRONMENT_ID.equals(envId)) {
			return LocalEnvironment.getInstance();
		}
		return null;
	}

	public IEnvironment[] getEnvironments() {
		return new IEnvironment[] { LocalEnvironment.getInstance() };
	}
}
