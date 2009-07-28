package org.eclipse.dltk.internal.ui.rse;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.dltk.core.internal.rse.DLTKRSEPlugin;
import org.eclipse.dltk.core.internal.rse.RSEConnectionQueryManager;
import org.eclipse.dltk.core.internal.rse.RSEConnectionQueryManager.IConnector;
import org.eclipse.rse.core.model.IHost;
import org.eclipse.rse.core.subsystems.ISubSystem;
import org.eclipse.rse.subsystems.files.core.subsystems.IRemoteFileSubSystem;
import org.eclipse.swt.widgets.Display;

/**
 * @since 2.0
 */
public class RSEConnector implements IConnector {
	private static boolean running = true;
	private Thread processingThread = new Thread("RSE connection resolver") {
		public void run() {
			while (running) {
				IHost host = RSEConnectionQueryManager.getInstance()
						.getNextHost(true);
				connect(host);
				RSEConnectionQueryManager.getInstance()
						.markHostAsFinished(host);
			}
		}
	};

	private void connect(IHost host) {
		ISubSystem[] subSystems = host.getSubSystems();
		for (ISubSystem subsystem : subSystems) {
			if (subsystem instanceof IRemoteFileSubSystem) {
				try {
					subsystem.connect(new NullProgressMonitor(), false);
				} catch (Exception e) {
					DLTKRSEPlugin.log(e);
				}
			}
		}
	};

	public RSEConnector() {
	}

	public boolean isDirectProcessingRequired() {
		// Process direct connection request.
		Display current = Display.getCurrent();
		if (current != null) {
			// We are in UI thread
			return true;
		}
		return false;
	}

	public void register() {
		processingThread.start();
	}

	public void runDisplayRunnables() {
		Display current = Display.getCurrent();
		while (RSEConnectionQueryManager.getInstance().hasHosts()
				&& current.readAndDispatch()) {
			current.sleep();
		}
	}

	public static void stop() {
		running = false;
	}
}
