/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.eclipse.dltk.debug.ui.breakpoints;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.ILineBreakpoint;
import org.eclipse.dltk.core.DLTKLanguageManager;
import org.eclipse.dltk.core.IDLTKLanguageToolkit;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.IType;
import org.eclipse.dltk.debug.core.DLTKDebugPlugin;
import org.eclipse.dltk.debug.core.ScriptDebugManager;
import org.eclipse.dltk.debug.core.model.IScriptBreakpoint;
import org.eclipse.dltk.debug.core.model.IScriptMethodEntryBreakpoint;
import org.eclipse.dltk.debug.ui.DLTKDebugUIPlugin;
import org.eclipse.dltk.internal.debug.core.model.AbstractScriptBreakpoint;
import org.eclipse.dltk.internal.debug.core.model.ScriptDebugModel;
import org.eclipse.dltk.ui.DLTKUILanguageManager;
import org.eclipse.dltk.ui.IDLTKUILanguageToolkit;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.texteditor.ITextEditor;

public class BreakpointUtils {
	public static String getNatureId(IScriptBreakpoint breakpoint) {
		ScriptDebugManager manager = ScriptDebugManager.getInstance();
		return manager.getNatureByDebugModel(breakpoint.getModelIdentifier());
	}

	public static IDLTKLanguageToolkit getLanguageToolkit(
			IScriptBreakpoint breakpoint) {
		return DLTKLanguageManager.getLanguageToolkit(getNatureId(breakpoint));
	}

	public static IDLTKUILanguageToolkit getUILanguageToolkit(
			IScriptBreakpoint breakpoint) {
		return DLTKUILanguageManager
				.getLanguageToolkit(getNatureId(breakpoint));
	}

	public static void addLineBreakpoint(ITextEditor textEditor, int lineNumber)
			throws CoreException {
		IDocument document = textEditor.getDocumentProvider().getDocument(
				textEditor.getEditorInput());

		IResource resource = getBreakpointResource(textEditor);
		try {
			IRegion line = document.getLineInformation(lineNumber - 1);
			int start = line.getOffset();
			int end = start + line.getLength();

			String debugModelId = getDebugModelId(textEditor, resource);
			if (debugModelId == null)
				return;

			IPath location = getBreakpointResourceLocation(textEditor);
			ScriptDebugModel.createLineBreakpoint(debugModelId, resource,
					location, lineNumber, start, end, true, null);
		} catch (BadLocationException e) {
			DLTKDebugPlugin.log(e);
		}
	}

	public static void addSpawnpoint(ITextEditor textEditor, int lineNumber)
			throws CoreException {
		IDocument document = textEditor.getDocumentProvider().getDocument(
				textEditor.getEditorInput());

		IResource resource = getBreakpointResource(textEditor);
		try {
			IRegion line = document.getLineInformation(lineNumber - 1);
			int start = line.getOffset();
			int end = start + line.getLength();

			String debugModelId = getDebugModelId(textEditor, resource);
			if (debugModelId == null)
				return;

			IPath location = getBreakpointResourceLocation(textEditor);
			ScriptDebugModel.createSpawnpoint(debugModelId, resource, location,
					lineNumber, start, end, true, null);
		} catch (BadLocationException e) {
			DLTKDebugPlugin.log(e);
		}
	}

	public static IResource getBreakpointResource(ITextEditor textEditor) {
		return getBreakpointResource(textEditor.getEditorInput());
	}

	public static IResource getBreakpointResource(final IEditorInput editorInput) {
		IResource resource = (IResource) editorInput
				.getAdapter(IResource.class);
		if (resource == null)
			resource = getWorkspaceRoot();
		return resource;
	}

	private static IWorkspaceRoot getWorkspaceRoot() {
		return ResourcesPlugin.getWorkspace().getRoot();
	}

	/**
	 * @since 2.0
	 */
	public static interface IBreakpointLocationTester {

		/**
		 * @param bpLocation
		 * @return
		 * @throws CoreException
		 */
		boolean evaluate(IBreakpoint breakpoint) throws CoreException;
	}

	private static class ResourceBreakpointLocationTester implements
			IBreakpointLocationTester {

		private final IResource resource;

		/**
		 * @param resource
		 */
		public ResourceBreakpointLocationTester(IResource resource) {
			this.resource = resource;
		}

		public boolean evaluate(IBreakpoint breakpoint) {
			return resource.equals(breakpoint.getMarker().getResource());
		}
	}

	private static class SimpleBreakpointLocationTester implements
			IBreakpointLocationTester {

		private final String path;

		/**
		 * @param path
		 */
		public SimpleBreakpointLocationTester(IPath path) {
			this.path = path.toPortableString();
		}

		public boolean evaluate(IBreakpoint breakpoint) throws CoreException {
			final IResource bpResource = breakpoint.getMarker().getResource();
			if (bpResource.equals(getWorkspaceRoot())) {
				final String bpLocation = (String) breakpoint.getMarker()
						.getAttribute(IMarker.LOCATION);
				return path.equals(bpLocation);
			}
			return false;
		}
	}

	/**
	 * @since 2.0
	 */
	public static IBreakpointLocationTester getBreakpointLocationTester(
			IEditorInput editorInput) throws CoreException {
		IResource resource = (IResource) editorInput
				.getAdapter(IResource.class);
		if (resource != null && !resource.equals(getWorkspaceRoot())) {
			return new ResourceBreakpointLocationTester(resource);
		}

		// else
		IModelElement element = (IModelElement) editorInput
				.getAdapter(IModelElement.class);
		if (element != null) {
			return new SimpleBreakpointLocationTester(element.getPath());
		}
		return null;
	}

	public static IPath getBreakpointResourceLocation(ITextEditor textEditor)
			throws CoreException {
		IResource resource = (IResource) textEditor.getEditorInput()
				.getAdapter(IResource.class);
		if (resource != null) {
			return resource.getFullPath();
		}

		// else
		IModelElement element = (IModelElement) textEditor.getEditorInput()
				.getAdapter(IModelElement.class);
		if (element != null) {
			return element.getPath();
		}
		return null;
	}

	private static String getDebugModelId(ITextEditor textEditor,
			IResource resource) throws CoreException {
		String debugModelId = ScriptDebugModel.getDebugModelId(resource);
		if (debugModelId != null) {
			return debugModelId;
		}

		// else
		IModelElement element = (IModelElement) textEditor.getEditorInput()
				.getAdapter(IModelElement.class);
		if (element != null) {
			IDLTKLanguageToolkit toolkit = DLTKLanguageManager
					.getLanguageToolkit(element);
			if (toolkit != null) {
				return ScriptDebugManager.getInstance().getDebugModelByNature(
						toolkit.getNatureId());
			}
		}

		return null;
	}

	public static ILineBreakpoint findLineBreakpoint(ITextEditor editor,
			int lineNumber) throws CoreException {
		final IEditorInput editorInput = editor.getEditorInput();
		final IResource resource = getBreakpointResource(editorInput);
		final String debugModelId = getDebugModelId(editor, resource);
		return findLineBreakpoint(editor, lineNumber, debugModelId);
	}

	/**
	 * 
	 * ...assume we already know debugModelId
	 * 
	 * @since 2.0
	 * @param editor
	 * @param lineNumber
	 * @return
	 * @throws CoreException
	 */
	public static ILineBreakpoint findLineBreakpoint(ITextEditor editor,
			int lineNumber, String debugModelId) throws CoreException {
		final IBreakpoint[] breakpoints = DebugPlugin.getDefault()
				.getBreakpointManager().getBreakpoints(debugModelId);

		final IBreakpointLocationTester tester = getBreakpointLocationTester(editor
				.getEditorInput());
		if (tester == null) {
			return null;
		}

		for (int i = 0; i < breakpoints.length; i++) {
			try {
				if (tester.evaluate(breakpoints[i])) {
					final ILineBreakpoint lineBreakpoint = (ILineBreakpoint) breakpoints[i];
					if (lineBreakpoint.getLineNumber() == lineNumber) {
						return lineBreakpoint;
					}
				}
			} catch (CoreException e) {
				DLTKDebugUIPlugin.log(e);
			}
		}

		return null;
	}

	public static void addMethodEntryBreakpoint(ITextEditor textEditor,
			int lineNumber, String methodName) throws CoreException {
		IDocument document = textEditor.getDocumentProvider().getDocument(
				textEditor.getEditorInput());

		IResource resource = (IResource) textEditor.getEditorInput()
				.getAdapter(IResource.class);
		if (resource != null) {
			try {
				IRegion line = document.getLineInformation(lineNumber - 1);
				int start = line.getOffset();
				int end = start + line.getLength() - 1;
				// TODO
				IPath path = resource.getLocation();
				IScriptMethodEntryBreakpoint methodEntryBreakpoint = ScriptDebugModel
						.createMethodEntryBreakpoint(resource, path,
								lineNumber, start, end, false, null, methodName);
				methodEntryBreakpoint.setBreakOnEntry(true);
				((AbstractScriptBreakpoint) methodEntryBreakpoint)
						.register(true);
			} catch (BadLocationException e) {
				DebugPlugin.log(e);
			}
		}
	}

	public static void addWatchPoint(ITextEditor textEditor, int lineNumber,
			String fieldName) throws CoreException {
		IDocument document = textEditor.getDocumentProvider().getDocument(
				textEditor.getEditorInput());

		IResource resource = (IResource) textEditor.getEditorInput()
				.getAdapter(IResource.class);
		if (resource != null) {
			try {
				IRegion line = document.getLineInformation(lineNumber - 1);
				int start = line.getOffset();
				int end = start + line.getLength() - 1;
				// TODO
				IPath path = resource.getLocation();

				/* ILineBreakpoint b = */ScriptDebugModel.createWatchPoint(
						resource, path, lineNumber, start, end, fieldName);
			} catch (BadLocationException e) {
				DebugPlugin.log(e);
			}
		}
	}

	public static void addExceptionBreakpoint(String debugModelId,
			boolean caught, final boolean uncaught, final IType type)
			throws CoreException {
		// TODO: Resource should refer to valid script type, so debug model id
		// can be calculated from it
		IResource resource = type.getResource();
		if (resource == null || !resource.getProject().exists()) {
			resource = getWorkspaceRoot();
		}
		if (resource != null) {
			ScriptDebugModel.createExceptionBreakpoint(debugModelId, resource,
					type.getTypeQualifiedName(), caught, uncaught, true, null);
		}
	}
}
