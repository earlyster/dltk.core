package org.eclipse.dltk.debug.ui.launchConfigurations;

public interface IMainLaunchConfigurationTabListenerManager {
	public void addListener(IMainLaunchConfigurationTabListener listener);

	public void removeListener(IMainLaunchConfigurationTabListener listener);
}
