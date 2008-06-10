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
package org.eclipse.mylyn.internal.dltk;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.dltk.internal.ui.editor.ScriptEditor;
import org.eclipse.mylyn.internal.dltk.ui.editor.ActiveFoldingListener;
import org.eclipse.mylyn.monitor.ui.AbstractEditorTracker;

import org.eclipse.ui.IEditorPart;

public class ActiveFoldingEditorTracker extends AbstractEditorTracker {

	protected Map editorListenerMap = new HashMap();

	public void editorOpened(IEditorPart part) {
		if (part instanceof ScriptEditor)
			registerEditor((ScriptEditor) part);
	}

	public void editorClosed(IEditorPart part) {
		if (part instanceof ScriptEditor)
			unregisterEditor((ScriptEditor) part);
	}

	public void registerEditor(final ScriptEditor editor) {
		if (editorListenerMap.containsKey(editor)) {
			return;
		} else {
			ActiveFoldingListener listener = new ActiveFoldingListener(editor, /* WTF */
					new DLTKStructureBridge());
			editorListenerMap.put(editor, listener);
		}
	}

	public void unregisterEditor(ScriptEditor editor) {
		ActiveFoldingListener listener = (ActiveFoldingListener) editorListenerMap
				.get(editor);
		if (listener != null) {
			listener.dispose();
		}
		editorListenerMap.remove(editor);
	}

	/**
	 * For testing.
	 */
	public Map getEditorListenerMap() {
		return editorListenerMap;
	}

	protected void editorBroughtToTop(IEditorPart part) {
		// ignore
	}

}
