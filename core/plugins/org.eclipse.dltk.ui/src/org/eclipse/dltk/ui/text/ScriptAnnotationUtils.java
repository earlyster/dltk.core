/*******************************************************************************
 * Copyright (c) 2008 xored software, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     xored software, Inc. - initial API and Implementation (Alex Panchenko)
 *******************************************************************************/
package org.eclipse.dltk.ui.text;

import org.eclipse.dltk.core.DLTKLanguageManager;
import org.eclipse.dltk.core.IDLTKLanguageToolkit;
import org.eclipse.dltk.ui.DLTKUILanguageManager;
import org.eclipse.dltk.ui.IDLTKUILanguageToolkit;
import org.eclipse.dltk.ui.PreferenceConstants;
import org.eclipse.dltk.ui.editor.IScriptAnnotation;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.ui.texteditor.SimpleMarkerAnnotation;

public class ScriptAnnotationUtils {

	public static boolean hasCorrections(IScriptAnnotation annotation) {
		final IDLTKLanguageToolkit toolkit = DLTKLanguageManager
				.getLanguageToolkit(annotation.getSourceModule());
		if (toolkit == null) {
			return false;
		}
		final IDLTKUILanguageToolkit uiToolkit = DLTKUILanguageManager
				.getLanguageToolkit(toolkit.getNatureId());
		if (uiToolkit != null
				&& !uiToolkit.getPreferenceStore().getBoolean(
						PreferenceConstants.EDITOR_CORRECTION_INDICATION)) {
			return false;
		}
		return ScriptCorrectionProcessorManager.canFix(toolkit.getNatureId(),
				(Annotation) annotation);
	}

	public static boolean isQuickFixableType(Annotation annotation) {
		return (annotation instanceof IScriptAnnotation || annotation instanceof SimpleMarkerAnnotation)
				&& !annotation.isMarkedDeleted();
	}

}
