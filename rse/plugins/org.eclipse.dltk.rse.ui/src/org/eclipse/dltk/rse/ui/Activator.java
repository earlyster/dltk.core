package org.eclipse.dltk.rse.ui;

import java.net.URI;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.DLTKLanguageManager;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.core.internal.rse.RSEEnvironmentProvider;
import org.eclipse.dltk.internal.ui.rse.RSEConnectionMonitor;
import org.eclipse.dltk.internal.ui.rse.RSEConnector;
import org.eclipse.rse.ui.RSEUIPlugin;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchListener;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.eclipse.dltk.rse.ui"; //$NON-NLS-1$

	// The shared instance
	private static Activator plugin;

	/**
	 * The constructor
	 */
	public Activator() {
	}

	/*
	 * @see AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		RSEUIPlugin.getDefault();
		RSEConnectionMonitor.start();
		// try {
		// PlatformUI.getWorkbench().addWorkbenchListener(
		// new ShutdownCloseProjectsWithLinkedFiles());
		// } catch (IllegalStateException e) {
		// // IGNORE: workbench has not been created yet.
		// }
	}

	/*
	 * @see AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		RSEConnector.stop();
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

	private static class ShutdownCloseProjectsWithLinkedFiles implements
			IWorkbenchListener {
		public void postShutdown(IWorkbench workbench) {
			// empty
		}

		public boolean preShutdown(IWorkbench workbench, boolean forced) {
			closeProjectsWithLinkedFiles();
			return true;
		}

		/**
		 * At the moment RSE calls Display.syncExec() to ask password and if
		 * it's called from RefreshJob when workspace is locked and at the same
		 * time UI thread wants to perform a workspace operation - deadlock
		 * occurs.
		 * 
		 * @throws ModelException
		 * @throws CoreException
		 */
		private void closeProjectsWithLinkedFiles() {
			final IProject[] projects = ResourcesPlugin.getWorkspace()
					.getRoot().getProjects();
			for (IProject project : projects) {
				if (project.isAccessible()
						&& DLTKLanguageManager.hasScriptNature(project)) {
					try {
						if (shouldBeClosed(project)) {
							project.close(null);
						}
					} catch (CoreException e) {
						if (DLTKCore.DEBUG)
							e.printStackTrace();
					}
				}
			}
		}

		private boolean shouldBeClosed(IProject project) throws CoreException {
			for (final IResource child : project.members()) {
				if (child.isLinked()) {
					final URI location = child.getLocationURI();
					if (location != null
							&& RSEEnvironmentProvider.RSE_SCHEME
									.equalsIgnoreCase(location.getScheme())) {
						return true;
					}
				}
			}
			return false;
		}

	}
}
