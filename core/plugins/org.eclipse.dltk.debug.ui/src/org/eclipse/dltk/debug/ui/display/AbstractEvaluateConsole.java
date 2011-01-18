package org.eclipse.dltk.debug.ui.display;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.console.AbstractConsole;

public abstract class AbstractEvaluateConsole extends AbstractConsole implements
		IEvaluateConsole {

	public AbstractEvaluateConsole(String name, ImageDescriptor imageDescriptor) {
		super(name, imageDescriptor, false);
	}

	@Override
	public void activate() {
		// TODO Auto-generated method stub
	}

}
