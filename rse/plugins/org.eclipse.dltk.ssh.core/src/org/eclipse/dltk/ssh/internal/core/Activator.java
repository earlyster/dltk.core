package org.eclipse.dltk.ssh.internal.core;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.eclipse.dltk.ssh.core.SshConnectionManager;
import org.eclipse.jsch.core.IJSchService;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends Plugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.eclipse.dltk.ssh"; //$NON-NLS-1$

	// The shared instance
	private static Activator plugin;

	private ServiceTracker tracker;

	/**
	 * The constructor
	 */
	public Activator() {
	}

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		tracker = new ServiceTracker(getBundle().getBundleContext(),
				org.eclipse.jsch.core.IJSchService.class.getName(), null);
		tracker.open();
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		SshConnectionManager.disconnectAll();
		tracker.close();
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	public IJSchService getJSch() {
		return (IJSchService) tracker.getService();
	}

	public static void log(IStatus status) {
		getDefault().getLog().log(status);
	}

	public static void log(String message) {
		log(new Status(IStatus.ERROR, PLUGIN_ID, message, null));
	}

	public static void error(String message, Throwable t) {
		log(new Status(IStatus.ERROR, PLUGIN_ID, message, t));
	}

	public static void warn(String message) {
		log(new Status(IStatus.WARNING, PLUGIN_ID, message));
	}

	public static void log(Throwable e) {
		log(new Status(IStatus.ERROR, PLUGIN_ID, e.getLocalizedMessage(), e));
	}

}
