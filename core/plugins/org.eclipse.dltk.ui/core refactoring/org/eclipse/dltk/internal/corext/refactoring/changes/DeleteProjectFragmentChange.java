/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.eclipse.dltk.internal.corext.refactoring.changes;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.eclipse.core.filebuffers.FileBuffers;
import org.eclipse.core.filebuffers.ITextFileBufferManager;
import org.eclipse.core.filebuffers.LocationKind;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IProjectFragment;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.internal.core.ScriptProject;
import org.eclipse.dltk.internal.corext.refactoring.RefactoringCoreMessages;
import org.eclipse.dltk.internal.corext.refactoring.reorg.IProjectFragmentManipulationQuery;
import org.eclipse.dltk.internal.corext.refactoring.util.ModelElementUtil;
import org.eclipse.dltk.internal.corext.util.Messages;
import org.eclipse.dltk.ui.DLTKUIPlugin;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.NullChange;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.ui.ide.undo.ResourceDescription;


public class DeleteProjectFragmentChange extends AbstractDeleteChange {
	
	private final String fHandle;
	private final boolean fIsExecuteChange;
	private final IProjectFragmentManipulationQuery fUpdateClasspathQuery;

	public DeleteProjectFragmentChange(IProjectFragment root, boolean isExecuteChange, 
			IProjectFragmentManipulationQuery updateClasspathQuery) {
		Assert.isNotNull(root);
		Assert.isTrue(! root.isExternal());
		fHandle= root.getHandleIdentifier();
		fIsExecuteChange= isExecuteChange;
		fUpdateClasspathQuery= updateClasspathQuery;
	}

	public String getName() {
		String[] keys= {getRoot().getElementName()};
		return Messages.format(RefactoringCoreMessages.DeleteProjectFragmentChange_delete, keys); 
	}

	public Object getModifiedElement() {
		return getRoot();
	}
	
	private IProjectFragment getRoot(){
		return (IProjectFragment)DLTKCore.create(fHandle);
	}
	
	public RefactoringStatus isValid(IProgressMonitor pm) throws CoreException {
		if (fIsExecuteChange) {
			// don't check for read-only resources since we already
			// prompt the user via a dialog to confirm deletion of
			// read only resource. The change is currently not used
			// as 
			return super.isValid(pm, DIRTY);
		} else {
			return super.isValid(pm, READ_ONLY | DIRTY);
		}
	}

	protected Change doDelete(IProgressMonitor pm) throws CoreException {
		if (! confirmDeleteIfReferenced())
			return new NullChange();
		int resourceUpdateFlags= IResource.KEEP_HISTORY;
		int jCoreUpdateFlags= IProjectFragment.ORIGINATING_PROJECT_BUILDPATH | IProjectFragment.OTHER_REFERRING_PROJECTS_BUILDPATH;

		pm.beginTask("", 2); //$NON-NLS-1$
		IProjectFragment root = getRoot();
		IResource rootResource = root.getResource();
		CompositeChange result = new CompositeChange(getName());

		ResourceDescription rootDescription = ResourceDescription
				.fromResource(rootResource);
		IScriptProject[] referencingProjects = ModelElementUtil
				.getReferencingProjects(root);
		HashMap/* <IFile, String> */classpathFilesContents = new HashMap();
		for (int i = 0; i < referencingProjects.length; i++) {
			IScriptProject javaProject = referencingProjects[i];
			IFile classpathFile = javaProject.getProject().getFile(
					ScriptProject.BUILDPATH_FILENAME);
			if (classpathFile.exists()) {
				classpathFilesContents.put(classpathFile,
						getFileContents(classpathFile));
			}
		}

		root.delete(resourceUpdateFlags, jCoreUpdateFlags,
				new SubProgressMonitor(pm, 1));

		rootDescription.recordStateFromHistory(rootResource,
				new SubProgressMonitor(pm, 1));
		for (Iterator iterator = classpathFilesContents.entrySet().iterator(); iterator
				.hasNext();) {
			Entry entry = (Entry) iterator.next();
			IFile file = (IFile) entry.getKey();
			String contents = (String) entry.getValue();
			// Restore time stamps? This should probably be some sort of
			// UndoTextFileChange.
			TextFileChange classpathUndo = new TextFileChange(
					Messages
							.format(
									RefactoringCoreMessages.DeleteProjectFragmentChange_restore_file,
									file.getFullPath().toOSString()), file);
			classpathUndo.setEdit(new ReplaceEdit(0, getFileLength(file),
					contents));
			result.add(classpathUndo);
		}
		result.add(new UndoDeleteResourceChange(rootDescription));

		pm.done();
		return result;
	}

	private boolean confirmDeleteIfReferenced() throws ModelException {		
		if (! getRoot().isArchive()) //for source folders, you don't ask, just do it
			return true;
		if (fUpdateClasspathQuery == null)
			return true;
		IScriptProject[] referencingProjects= ModelElementUtil.getReferencingProjects(getRoot());
		if (referencingProjects.length == 0)
			return true;
		return fUpdateClasspathQuery.confirmManipulation(getRoot(), referencingProjects);
	}

	private static int getFileLength(IFile file) throws CoreException {
		// Cannot use file buffers here, since they are not yet in sync at this
		// point.
		InputStream contents = file.getContents();
		InputStreamReader reader;
		try {
			reader = new InputStreamReader(contents, file.getCharset());
		} catch (UnsupportedEncodingException e) {
			DLTKUIPlugin.log(e);
			reader = new InputStreamReader(contents);
		}
		try {
			return (int) reader.skip(Integer.MAX_VALUE);
		} catch (IOException e) {
			throw new CoreException(new Status(IStatus.ERROR, DLTKUIPlugin
					.getPluginId(), e.getMessage(), e));
		}
	}

	private static String getFileContents(IFile file) throws CoreException {
		ITextFileBufferManager manager = FileBuffers.getTextFileBufferManager();
		IPath path = file.getFullPath();
		manager.connect(path, LocationKind.IFILE, new NullProgressMonitor());
		try {
			return manager.getTextFileBuffer(path, LocationKind.IFILE)
					.getDocument().get();
		} finally {
			manager.disconnect(path, LocationKind.IFILE,
					new NullProgressMonitor());
		}
	}
}
