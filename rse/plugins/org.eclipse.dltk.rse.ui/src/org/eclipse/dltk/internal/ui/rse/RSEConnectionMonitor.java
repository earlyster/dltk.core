package org.eclipse.dltk.internal.ui.rse;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.environment.EnvironmentManager;
import org.eclipse.dltk.core.environment.IEnvironment;
import org.eclipse.dltk.core.internal.rse.RSEEnvironment;
import org.eclipse.dltk.internal.core.ProjectRefreshOperation;
import org.eclipse.dltk.internal.core.search.ProjectIndexerManager;
import org.eclipse.rse.core.model.IHost;
import org.eclipse.rse.core.subsystems.CommunicationsEvent;
import org.eclipse.rse.core.subsystems.ICommunicationsListener;
import org.eclipse.rse.core.subsystems.IConnectorService;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

/**
 * @since 2.0
 */
public class RSEConnectionMonitor implements Runnable {
	private static final String FAMILY = "rse_connection_changed_project_update_job";

	private static final class ProjectUpdateJob extends Job {

		private RSEEnvironment environnent;

		private ProjectUpdateJob(String name) {
			super(name);
		}

		protected IStatus run(IProgressMonitor monitor) {
			monitor.beginTask("Checking projects consistency", 100);
			IProject[] projects = ResourcesPlugin.getWorkspace().getRoot()
					.getProjects();
			List<IScriptProject> projectsToProcess = new ArrayList<IScriptProject>();
			SubProgressMonitor m = new SubProgressMonitor(monitor, 10);
			m.beginTask("Locate projects for envirinment", projects.length);
			for (IProject project : projects) {
				final String envId = EnvironmentManager.getEnvironmentId(
						project, false);
				if (envId != null) {
					if (envId.equals(environnent.getId())) {
						IScriptProject scriptProject = DLTKCore.create(project);
						projectsToProcess.add(scriptProject);
					}

				}
				m.worked(1);
			}
			m.done();
			IScriptProject scriptProjects[] = (IScriptProject[]) projectsToProcess
					.toArray(new IScriptProject[projectsToProcess.size()]);
			ProjectRefreshOperation op = new ProjectRefreshOperation(
					scriptProjects);
			try {
				op.run(new SubProgressMonitor(monitor, 50));
			} catch (CoreException e1) {
				if (DLTKCore.DEBUG) {
					e1.printStackTrace();
				}
			}
			EnvironmentManager.fireEnvirontmentChange();
			EnvironmentManager
					.refreshBuildpathContainersForMixedProjects(new SubProgressMonitor(
							monitor, 10));
			SubProgressMonitor mm = new SubProgressMonitor(monitor, 30);
			mm.beginTask("Procesing project", projectsToProcess.size() * 10);
			for (IScriptProject project : projectsToProcess) {
				ProjectIndexerManager.indexProject(project);
				try {
					project.getProject().build(
							IncrementalProjectBuilder.FULL_BUILD,
							new SubProgressMonitor(monitor, 10));
				} catch (CoreException e) {
					if (DLTKCore.DEBUG) {
						e.printStackTrace();
					}
				}
			}
			mm.done();
			monitor.done();
			return Status.OK_STATUS;
		}

		@Override
		public boolean belongsTo(Object family) {
			return FAMILY.equals(family);
		}

		public void setEnvironment(RSEEnvironment rseENV) {
			this.environnent = rseENV;
		}
	}

	private static void updateDecorator() {
		Display display = PlatformUI.getWorkbench().getDisplay();
		if (display.isDisposed()) {
			return;
		}
		display.asyncExec(new Runnable() {
			public void run() {
				PlatformUI.getWorkbench().getDecoratorManager().update(
						"org.eclipse.dltk.rse.decorators.projectdecorator");
			}
		});
	}

	private static RSEConnectionMonitor monitor = new RSEConnectionMonitor();

	public static void start() {
		Thread t = new Thread(monitor);
		t.start();
	}

	public void run() {
		EnvironmentManager.waitInitialized();
		while (Platform.isRunning()) {
			Set<String> eventListenerAdded = new HashSet<String>();

			IEnvironment[] environments = EnvironmentManager.getEnvironments();
			for (final IEnvironment env : environments) {
				if (env instanceof RSEEnvironment) {
					final RSEEnvironment rseENV = (RSEEnvironment) env;
					if (eventListenerAdded.add(rseENV.getId())) {
						// Add connection status listener
						IHost host = rseENV.getHost();
						IConnectorService[] services = host
								.getConnectorServices();
						for (IConnectorService service : services) {
							service
									.addCommunicationsListener(new ICommunicationsListener() {
										public boolean isPassiveCommunicationsListener() {
											return false;
										}

										public void communicationsStateChange(
												CommunicationsEvent e) {
											if (e.getState() == CommunicationsEvent.AFTER_CONNECT) {
												if (rseENV.isConnected()) {
													// Need to update
													// environment.
													rseENV
															.setTryToConnect(true);
													if (Job.getJobManager()
															.find(FAMILY).length == 0) {
														ProjectUpdateJob job = new ProjectUpdateJob(
																"Environment configuration changed. Updating projects.");
														job
																.setEnvironment(rseENV);
														job.setUser(true);
														job.schedule();
													}
												}
											}
											updateDecorator();
										}
									});
						}
					}
				}
			}
			try {
				Thread.sleep(250);
			} catch (InterruptedException e) {
				return;
			}
		}
	}
}
