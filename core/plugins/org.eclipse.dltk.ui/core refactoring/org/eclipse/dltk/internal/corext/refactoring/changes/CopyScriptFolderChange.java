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
package org.eclipse.dltk.internal.corext.refactoring.changes;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.dltk.core.IProjectFragment;
import org.eclipse.dltk.core.IScriptFolder;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.internal.corext.refactoring.RefactoringCoreMessages;
import org.eclipse.dltk.internal.corext.refactoring.reorg.INewNameQuery;
import org.eclipse.dltk.internal.corext.util.Messages;
import org.eclipse.dltk.ui.ScriptElementLabels;
import org.eclipse.ltk.core.refactoring.Change;

public class CopyScriptFolderChange extends PackageReorgChange {

	public CopyScriptFolderChange(IScriptFolder pack, IProjectFragment dest,
			INewNameQuery nameQuery) {
		super(pack, dest, nameQuery);
	}

	protected Change doPerformReorg(IProgressMonitor pm) throws ModelException,
			OperationCanceledException {
		getPackage().copy(getDestination(), null, getNewName(), true, pm);
		return null;
	}

	public String getName() {
		String packageName = ScriptElementLabels.getDefault().getElementLabel(
				getPackage(), ScriptElementLabels.ALL_DEFAULT);
		String destinationName = ScriptElementLabels.getDefault()
				.getElementLabel(getDestination(),
						ScriptElementLabels.ALL_DEFAULT);
		return Messages.format(RefactoringCoreMessages.CopyPackageChange_copy,
				new String[] { packageName, destinationName });
	}
}
