package org.eclipse.dltk.debug.ui.launchConfigurations;

import org.eclipse.core.resources.IProject;

public interface IMainLaunchConfigurationTabListener {
	void projectChanged(IProject project);
}
