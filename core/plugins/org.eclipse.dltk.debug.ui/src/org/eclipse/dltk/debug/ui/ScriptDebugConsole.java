package org.eclipse.dltk.debug.ui;

import org.eclipse.debug.core.ILaunch;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.console.IOConsole;

public class ScriptDebugConsole extends IOConsole {
	private ILaunch launch;

	public ILaunch getLaunch() {
		return launch;
	}

	public void setLaunch(ILaunch launch) {
		this.launch = launch;
	}

	public ScriptDebugConsole(String name, String encoding) {
		this(name, null, encoding);
	}

	public ScriptDebugConsole(String name, ImageDescriptor imageDescriptor,
			String encoding) {
		super(name, null, imageDescriptor, encoding, true);

		this.addPatternMatchListener(new ScriptDebugConsoleTraceTracker());
	}

	@Override
	public void matcherFinished() {
		super.matcherFinished();
	}

	@Override
	public void partitionerFinished() {
		super.partitionerFinished();
	}

	/*
	 * Increase visibility
	 */
	@Override
	protected void setName(String name) {
		super.setName(name);
	}

}
