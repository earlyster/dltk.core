package org.eclipse.dltk.internal.ui.actions.refactoring;

import org.eclipse.dltk.internal.ui.editor.ScriptEditor;
import org.eclipse.dltk.ui.actions.SelectionDispatchAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IWorkbenchSite;

public class ContributedRefactoringAction extends SelectionDispatchAction {

	private final IEditorActionDelegate delegate;

	protected ContributedRefactoringAction(ScriptEditor editor,
			IEditorActionDelegate delegate) {
		super(editor.getSite());
		this.delegate = delegate;
		delegate.setActiveEditor(this, editor);
	}
	
	protected ContributedRefactoringAction(IWorkbenchSite site,
			IEditorActionDelegate delegate) {
		super(site);
		this.delegate = delegate;
	}

	@Override
	public void selectionChanged(ISelection selection) {
		delegate.selectionChanged(this, selection);
	}

	@Override
	public void run(ISelection selection) {
		delegate.selectionChanged(this, selection);
		delegate.run(this);
	}

}
