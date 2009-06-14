/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.dltk.internal.mylyn;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.dltk.internal.mylyn.editor.ActiveFoldingListener;
import org.eclipse.dltk.internal.ui.editor.ScriptEditor;
import org.eclipse.mylyn.monitor.ui.AbstractEditorTracker;
import org.eclipse.ui.IEditorPart;

/**
 * @author Mik Kersten
 */
public class ActiveFoldingEditorTracker extends AbstractEditorTracker {

	protected Map<ScriptEditor, ActiveFoldingListener> editorListenerMap = new HashMap<ScriptEditor, ActiveFoldingListener>();

	@Override
	public void editorOpened(IEditorPart part) {
		if (part instanceof ScriptEditor) {
			registerEditor((ScriptEditor) part);
		}
	}

	@Override
	public void editorClosed(IEditorPart part) {
		if (part instanceof ScriptEditor) {
			unregisterEditor((ScriptEditor) part);
		}
	}

	public void registerEditor(final ScriptEditor editor) {
		if (editorListenerMap.containsKey(editor)) {
			return;
		} else {
			ActiveFoldingListener listener = new ActiveFoldingListener(editor);
			editorListenerMap.put(editor, listener);
		}
	}

	public void unregisterEditor(ScriptEditor editor) {
		ActiveFoldingListener listener = editorListenerMap.get(editor);
		if (listener != null) {
			listener.dispose();
		}
		editorListenerMap.remove(editor);
	}

	/**
	 * For testing.
	 */
	public Map<ScriptEditor, ActiveFoldingListener> getEditorListenerMap() {
		return editorListenerMap;
	}

	@Override
	protected void editorBroughtToTop(IEditorPart part) {
		// ignore
	}

}
