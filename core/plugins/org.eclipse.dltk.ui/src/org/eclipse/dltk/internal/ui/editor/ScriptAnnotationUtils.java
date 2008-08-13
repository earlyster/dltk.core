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
package org.eclipse.dltk.internal.ui.editor;

import org.eclipse.dltk.ui.DLTKUILanguageManager;
import org.eclipse.dltk.ui.DLTKUIPlugin;
import org.eclipse.dltk.ui.IDLTKCorrectionProcessor;
import org.eclipse.dltk.ui.IDLTKUILanguageToolkit;
import org.eclipse.dltk.ui.PreferenceConstants;
import org.eclipse.dltk.ui.editor.IScriptAnnotation;
import org.eclipse.jface.text.source.Annotation;

public class ScriptAnnotationUtils {

	public static boolean hasCorrections(IScriptAnnotation annotation) {
		final IDLTKUILanguageToolkit uiToolkit = DLTKUILanguageManager
				.getLanguageToolkit(annotation.getSourceModule());
		if (uiToolkit == null) {
			return false;
		}
		if (!uiToolkit.getPreferenceStore().getBoolean(
				PreferenceConstants.EDITOR_CORRECTION_INDICATION)) {
			return false;
		}
		final IDLTKCorrectionProcessor correctionProcessor = DLTKUIPlugin
				.getCorrectionProcessor(uiToolkit);
		if (correctionProcessor == null) {
			return false;
		}
		return correctionProcessor.hasCorrections((Annotation) annotation);
	}

}
