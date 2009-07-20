package org.eclipse.dltk.internal.ui;

import org.eclipse.dltk.internal.ui.text.IInformationControlExtension4;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.IInformationControlExtension5;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

public class BrowserInformationControl2 extends BrowserInformationControl
		implements IInformationControlExtension4, IInformationControlExtension5 {
	public BrowserInformationControl2(Shell parent) {
		super(parent);
	}

	public BrowserInformationControl2(Shell parent, int style) {
		super(parent, style);
	}

	public BrowserInformationControl2(Shell parent, int shellStyle, int style) {
		super(parent, shellStyle, style);
	}

	public BrowserInformationControl2(Shell parent, int shellStyle, int style,
			String statusFieldText) {
		super(parent, shellStyle, style, statusFieldText);
	}

	public Point computeSizeConstraints(int widthInChars, int heightInChars) {
		return null;
	}

	public boolean containsControl(Control control) {
		do {
			if (control == fShell)
				return true;
			if (control instanceof Shell)
				return false;
			control = control.getParent();
		} while (control != null);
		return false;
	}

	public IInformationControlCreator getInformationPresenterControlCreator() {
		return null;
	}

	public boolean isVisible() {
		return fShell != null && !fShell.isDisposed() && fShell.isVisible();
	}
}
