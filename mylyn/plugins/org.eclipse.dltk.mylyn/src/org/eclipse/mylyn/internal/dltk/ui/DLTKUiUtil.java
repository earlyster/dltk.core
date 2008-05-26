/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.dltk.ui;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.dltk.core.DLTKContentTypeManager;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.DLTKLanguageManager;
import org.eclipse.dltk.core.IDLTKLanguageToolkit;
import org.eclipse.dltk.core.IMember;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.IType;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.internal.ui.editor.ScriptEditor;
import org.eclipse.dltk.ui.PreferenceConstants;
import org.eclipse.dltk.ui.ScriptElementImageDescriptor;
import org.eclipse.dltk.ui.util.ExceptionHandler;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.mylyn.internal.dltk.MylynStatusHandler;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.AbstractTextEditor;
import org.eclipse.ui.views.markers.internal.ConcreteMarker;

public class DLTKUiUtil {

	private static final Point SMALL_SIZE = new Point(16, 16);

	private static final String SEPARATOR_CODEASSIST = "\0"; //$NON-NLS-1$

	public static void installContentAssist(IPreferenceStore scriptPrefs,
			boolean mylynContentAssist) {
		String oldValue = scriptPrefs
				.getString(PreferenceConstants.CODEASSIST_EXCLUDED_CATEGORIES);
		StringTokenizer tokenizer = new StringTokenizer(oldValue,
				SEPARATOR_CODEASSIST);
		Set disabledIds = new HashSet();
		while (tokenizer.hasMoreTokens()) {
			disabledIds.add((String) tokenizer.nextElement());
		}
		String newValue = "";
		// for (String id : disabledIds) {
		for (Iterator it = disabledIds.iterator(); it.hasNext();) {
			String id = (String) it.next();
			newValue += id + SEPARATOR_CODEASSIST;
		}
		scriptPrefs.setValue(
				PreferenceConstants.CODEASSIST_EXCLUDED_CATEGORIES, newValue);
	}

	public static ImageDescriptor decorate(ImageDescriptor base, int decorations) {
		ImageDescriptor imageDescriptor = new ScriptElementImageDescriptor(
				base, decorations, SMALL_SIZE);
		return imageDescriptor;
	}

	public static IModelElement getScriptElement(ConcreteMarker marker) {
		if (marker == null)
			return null;
		try {
			IResource res = marker.getResource();
			ISourceModule cu = null;
			if (res instanceof IFile) {
				IFile file = (IFile) res;
				IProject project = res.getProject();
				IScriptProject scriptProject = DLTKCore.create(project);
				if (scriptProject.exists()) {
					IDLTKLanguageToolkit toolkit = DLTKLanguageManager
							.getLanguageToolkit(scriptProject);
					if (DLTKContentTypeManager.isValidResourceForContentType(
							toolkit, res)) {
						cu = DLTKCore.createSourceModuleFrom(file);
					}
				} else {
					return null;
				}
			}
			if (cu != null) {
				IModelElement je = cu.getElementAt(marker.getMarker()
						.getAttribute(IMarker.CHAR_START, 0));
				return je;
			} else {
				return null;
			}
		} catch (ModelException ex) {
			if (!ex.isDoesNotExist())
				ExceptionHandler.handle(ex,
						"error", "could not find java element"); //$NON-NLS-2$ //$NON-NLS-1$
			return null;
		} catch (Throwable t) {
			MylynStatusHandler.fail(t, "Could not find element for: " + marker,
					false);
			return null;
		}
	}

	/**
	 * Get the fully qualified name of a IMember
	 * 
	 * @param m
	 *            The IMember to get the fully qualified name for
	 * @return String representing the fully qualified name
	 */
	public static String getFullyQualifiedName(IModelElement je) {
		if (!(je instanceof IMember))
			return null;

		IMember m = (IMember) je;
		if (m.getDeclaringType() == null)
			return ((IType) m).getFullyQualifiedName();
		else
			return m.getDeclaringType().getFullyQualifiedName() + "."
					+ m.getElementName();
	}

	public static void closeActiveEditors(boolean javaOnly) {
		// for (IWorkbenchWindow workbenchWindow :
		// PlatformUI.getWorkbench().getWorkbenchWindows()) {
		for (int j = 0; j < PlatformUI.getWorkbench().getWorkbenchWindows().length; j++) {
			IWorkbenchWindow workbenchWindow = PlatformUI.getWorkbench()
					.getWorkbenchWindows()[j];
			IWorkbenchPage page = workbenchWindow.getActivePage();
			if (page != null) {
				IEditorReference[] references = page.getEditorReferences();
				for (int i = 0; i < references.length; i++) {
					IEditorPart part = references[i].getEditor(false);
					if (part != null) {
						if (javaOnly
								&& part.getEditorInput() instanceof IFileEditorInput
								&& part instanceof ScriptEditor) {
							ScriptEditor editor = (ScriptEditor) part;
							editor.close(true);
						} else if (part instanceof ScriptEditor) {
							((AbstractTextEditor) part).close(true);
						}
					}
				}
			}
		}
	}
}
