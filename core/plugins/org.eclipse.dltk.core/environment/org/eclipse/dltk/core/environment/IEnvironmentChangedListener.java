package org.eclipse.dltk.core.environment;

public interface IEnvironmentChangedListener {
	void environmentAdded(IEnvironment environment);
	void environmentRemoved(IEnvironment environment);
	void environmentChanged(IEnvironment environment);
}
