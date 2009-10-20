package org.eclipse.dltk.internal.debug.ui.interpreters;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.ExpandableComposite;

/**
 * @since 2.0
 */
public class ExpandableBlock extends ExpandableComposite {
	private final Composite content;

	public ExpandableBlock(final Composite parent, int style) {
		super(parent, SWT.NONE, ExpandableComposite.TWISTIE
				| ExpandableComposite.CLIENT_INDENT | style);
		content = new Composite(this, SWT.NONE);
		content.setLayout(new GridLayout(2, false));
		setClient(content);
		addExpansionListener(new ExpansionAdapter() {
			@Override
			public void expansionStateChanged(ExpansionEvent e) {
				parent.layout();
				Shell shell = parent.getShell();
				Point size = shell.computeSize(SWT.DEFAULT, SWT.DEFAULT);
				Point size2 = shell.getSize();
				if (size.y > size2.y) {
					shell.setSize(size2.x, size.y);
				}
			}
		});
	}

	public Composite getContent() {
		return content;
	}
}