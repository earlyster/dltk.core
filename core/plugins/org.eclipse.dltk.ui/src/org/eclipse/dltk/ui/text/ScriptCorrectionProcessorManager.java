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

import org.eclipse.core.resources.IMarker;
import org.eclipse.dltk.ui.editor.IScriptAnnotation;
import org.eclipse.dltk.utils.NatureExtensionManager;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.ui.texteditor.SimpleMarkerAnnotation;

public class ScriptCorrectionProcessorManager extends NatureExtensionManager {

	private static final String EXT_POINT = "org.eclipse.dltk.ui.correctionProcessor"; //$NON-NLS-1$

	private ScriptCorrectionProcessorManager() {
		super(EXT_POINT, IScriptCorrectionProcessor.class);
	}

	private static ScriptCorrectionProcessorManager instance = null;

	private static synchronized ScriptCorrectionProcessorManager getInstance() {
		if (instance == null) {
			instance = new ScriptCorrectionProcessorManager();
		}
		return instance;
	}

	public static IScriptCorrectionProcessor[] getProcessors(String natureId) {
		return (IScriptCorrectionProcessor[]) getInstance().getInstances(
				natureId);
	}

	/**
	 * @param natureId
	 * @param annotation
	 * @return
	 */
	public static boolean canFix(String natureId, Annotation annotation) {
		final IScriptCorrectionProcessor[] processors = getProcessors(natureId);
		if (processors == null) {
			return false;
		}
		if (annotation instanceof IScriptAnnotation) {
			final IScriptAnnotation sa = (IScriptAnnotation) annotation;
			for (int i = 0; i < processors.length; ++i) {
				if (processors[i].canFix(sa)) {
					return true;
				}
			}
		} else if (annotation instanceof SimpleMarkerAnnotation) {
			final IMarker marker = ((SimpleMarkerAnnotation) annotation)
					.getMarker();
			for (int i = 0; i < processors.length; ++i) {
				if (processors[i].canFix(marker)) {
					return true;
				}
			}
		}
		return false;
	}

}
