package org.eclipse.dltk.validators.internal.externalchecker.core;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class ExternalCheckerPlugin extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.eclipse.dltk.validators.externalchecker"; //$NON-NLS-1$

	// The shared instance
	private static ExternalCheckerPlugin plugin;

	/**
	 * The constructor
	 */
	public ExternalCheckerPlugin() {
	}

	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static ExternalCheckerPlugin getDefault() {
		return plugin;
	}

}
