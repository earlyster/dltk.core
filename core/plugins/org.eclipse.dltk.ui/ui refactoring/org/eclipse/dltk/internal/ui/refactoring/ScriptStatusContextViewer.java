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
package org.eclipse.dltk.internal.ui.refactoring;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.ISourceRange;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.internal.corext.refactoring.base.ScriptStatusContext;
import org.eclipse.dltk.internal.ui.editor.ScriptSourceViewer;
import org.eclipse.dltk.ui.DLTKUILanguageManager;
import org.eclipse.dltk.ui.DLTKUIPlugin;
import org.eclipse.dltk.ui.IDLTKUILanguageToolkit;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.ltk.core.refactoring.RefactoringStatusContext;
import org.eclipse.ltk.ui.refactoring.TextStatusContextViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.IDocumentProvider;


public class ScriptStatusContextViewer extends TextStatusContextViewer {
	protected SourceViewer createSourceViewer(Composite parent) {
		return new ScriptSourceViewer(parent, null, null, false,
				SWT.LEFT_TO_RIGHT | SWT.V_SCROLL | SWT.H_SCROLL | SWT.MULTI
						| SWT.FULL_SELECTION, null);
	}

	/*private IPackageFragmentRoot getPackageFragmentRoot(IClassFile file) {

		IJavaElement element= file.getParent();
		while (element != null && element.getElementType() != IJavaElement.PACKAGE_FRAGMENT_ROOT)
			element= element.getParent();

		return (IPackageFragmentRoot) element;
	}*/

	public void setInput(RefactoringStatusContext context) {
		if (context instanceof ScriptStatusContext) {
			ScriptStatusContext jsc = (ScriptStatusContext) context;
			IDocument document= null;
			/*if (jsc.isBinary()) {
				IClassFile file = jsc.getClassFile();
				IEditorInput editorInput= new InternalClassFileEditorInput(file);
				document = getDocument(DLTKUIPlugin.getDefault()
						.getClassFileDocumentProvider(), editorInput);
				if (document.getLength() == 0)
					document= new Document(Messages.format(RefactoringMessages.JavaStatusContextViewer_no_source_found0, JavaElementLabels.getElementLabel(getPackageFragmentRoot(file), JavaElementLabels.ALL_DEFAULT)));
				updateTitle(file);
			} else {*/
			ISourceModule cunit = jsc.getSourceModule();
			if (cunit.isWorkingCopy()) {
				try {
					document= newScriptDocument(cunit);
				} catch (ModelException e) {
					// document is null which is a valid input.
				}
			} else {
				IEditorInput editorInput= new FileEditorInput((IFile)cunit.getResource());
				document = getDocument(DLTKUIPlugin.getDefault()
						.getSourceModuleDocumentProvider(), editorInput);
			}
			if (document == null)
				document = new Document(
						RefactoringMessages.ScriptStatusContextViewer_no_source_available);
			updateTitle(cunit);
			//}
			setInput(document, createRegion(jsc.getSourceRange()));
		}/* else if (context instanceof JavaStringStatusContext) {
			updateTitle(null);
			JavaStringStatusContext sc= (JavaStringStatusContext)context;
			setInput(newJavaDocument(sc.getSource()), createRegion(sc.getSourceRange()));
		}*/
	}

	private IDocument newScriptDocument(ISourceModule cu) throws ModelException {
		IDocument result= new Document(cu.getSource());
		IDLTKUILanguageToolkit toolkit = DLTKUILanguageManager
				.getLanguageToolkit(cu);
		toolkit.getTextTools().setupDocumentPartitioner(result);
		ScriptSourceViewer viewer = (ScriptSourceViewer) getSourceViewer();
		viewer.setPreferenceStore(toolkit.getCombinedPreferenceStore());
		viewer.configure(toolkit.createSourceViewerConfiguration());
		return result;
	}

	private static IRegion createRegion(ISourceRange range) {
		return new Region(range.getOffset(), range.getLength());
	}

	private IDocument getDocument(IDocumentProvider provider, IEditorInput input) {
		if (input == null)
			return null;
		IDocument result= null;
		try {
			provider.connect(input);
			result= provider.getDocument(input);
		} catch (CoreException e) {
		} finally {
			provider.disconnect(input);
		}
		return result;
	}
}
