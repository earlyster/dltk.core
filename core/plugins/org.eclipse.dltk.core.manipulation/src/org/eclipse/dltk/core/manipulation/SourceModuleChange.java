/*******************************************************************************
 * Copyright (c) 2000, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.dltk.core.manipulation;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.internal.core.manipulation.ScriptManipulationPlugin;
import org.eclipse.dltk.internal.corext.refactoring.changes.UndoSourceModuleChange;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.ChangeDescriptor;
import org.eclipse.ltk.core.refactoring.ContentStamp;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.UndoEdit;

/**
 * A {@link TextFileChange} that operates on an {@link ISourceModule} in the workspace.
 * 
 * @since 1.3
 */
public class SourceModuleChange extends TextFileChange {

	private final ISourceModule fCUnit;

	/** The (optional) refactoring descriptor */
	private ChangeDescriptor fDescriptor;

	/**
	 * Creates a new <code>SourceModuleChange</code>.
	 *
	 * @param name the change's name, mainly used to render the change in the UI
	 * @param cunit the compilation unit this change works on
	 */
	public SourceModuleChange(String name, ISourceModule cunit) {
		super(name, getFile(cunit));
		Assert.isNotNull(cunit);
		fCUnit= cunit;
	}

	private static IFile getFile(ISourceModule cunit) {
		return (IFile) cunit.getResource();
	}

	/**
	 * {@inheritDoc}
	 */
	public Object getModifiedElement(){
		return fCUnit;
	}

	/**
	 * Returns the compilation unit this change works on.
	 *
	 * @return the compilation unit this change works on
	 */
	public ISourceModule getSourceModule() {
		return fCUnit;
	}

	/**
	 * {@inheritDoc}
	 */
	protected IDocument acquireDocument(IProgressMonitor pm) throws CoreException {
		pm.beginTask("", 2); //$NON-NLS-1$
		fCUnit.becomeWorkingCopy(null, new SubProgressMonitor(pm, 1));
		return super.acquireDocument(new SubProgressMonitor(pm, 1));
	}

	/**
	 * {@inheritDoc}
	 */
	protected void releaseDocument(IDocument document, IProgressMonitor pm) throws CoreException {
		boolean isModified= isDocumentModified();
		super.releaseDocument(document, pm);
		try {
			fCUnit.discardWorkingCopy();
		} finally {
			if (isModified && !isDocumentAcquired()) {
				if (fCUnit.isWorkingCopy())
					fCUnit.reconcile(
							false /* don't force problem detection */,
							null /* use primary owner */,
							null /* no progress monitor */);

				else
					fCUnit.makeConsistent(pm);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected Change createUndoChange(UndoEdit edit, ContentStamp stampToRestore) {
		try {
			return new UndoSourceModuleChange(getName(), fCUnit, edit, stampToRestore, getSaveMode());
		} catch (CoreException e) {
			ScriptManipulationPlugin.log(e);
			return null;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Object getAdapter(@SuppressWarnings("rawtypes") Class adapter) {
		if (ISourceModule.class.equals(adapter))
			return fCUnit;
		return super.getAdapter(adapter);
	}

	/**
	 * Sets the refactoring descriptor for this change.
	 *
	 * @param descriptor the descriptor to set, or <code>null</code> to set no descriptor
	 */
	public void setDescriptor(ChangeDescriptor descriptor) {
		fDescriptor= descriptor;
	}

	/**
	 * {@inheritDoc}
	 */
	public ChangeDescriptor getDescriptor() {
		return fDescriptor;
	}
}

