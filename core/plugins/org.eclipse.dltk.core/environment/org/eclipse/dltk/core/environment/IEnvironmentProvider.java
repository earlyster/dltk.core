package org.eclipse.dltk.core.environment;

public interface IEnvironmentProvider {
	public IEnvironment[] getEnvironments();
	public IEnvironment getEnvironment(String envId);
}
