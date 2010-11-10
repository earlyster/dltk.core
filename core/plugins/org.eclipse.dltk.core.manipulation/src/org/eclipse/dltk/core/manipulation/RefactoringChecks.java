/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.eclipse.dltk.core.manipulation;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.dltk.internal.corext.refactoring.RefactoringCoreMessages;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.internal.core.refactoring.Resources;

@SuppressWarnings("restriction")
public class RefactoringChecks {

    public static RefactoringStatus validateModifiesFiles(IFile[] filesToModify, Object context) {
        RefactoringStatus result = new RefactoringStatus();
        IStatus status = Resources.checkInSync(filesToModify);
        if (!status.isOK())
            result.merge(RefactoringStatus.create(status));
        status = Resources.makeCommittable(filesToModify, context);
        if (!status.isOK()) {
            result.merge(RefactoringStatus.create(status));
            if (!result.hasFatalError()) {
                result.addFatalError(RefactoringCoreMessages.Checks_validateEdit);
            }
        }
        return result;
    }

}
