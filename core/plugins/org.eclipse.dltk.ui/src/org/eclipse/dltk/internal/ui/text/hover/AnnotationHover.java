/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.eclipse.dltk.internal.ui.text.hover;

import org.eclipse.dltk.compiler.problem.IProblem;
import org.eclipse.dltk.core.IScriptModelMarker;
import org.eclipse.dltk.internal.core.util.Util;
import org.eclipse.dltk.internal.ui.editor.SourceModuleDocumentProvider.ProblemAnnotation;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.ui.texteditor.MarkerAnnotation;

/**
 * This annotation hover shows the description of the selected annotation.
 * 
 * @since 3.0
 */
public class AnnotationHover extends AbstractAnnotationHover {

	public AnnotationHover() {
		super(true);
	}

	protected String postUpdateMessage(String message) {
		return super.postUpdateMessage(message);
	}

	protected String getMessageFromAnnotation(Annotation a) {
		if (a instanceof MarkerAnnotation) {
			MarkerAnnotation ma = (MarkerAnnotation) a;
			String args = ma.getMarker().getAttribute(
					IScriptModelMarker.ARGUMENTS, null);
			if (args != null) {
				String[] arguments = Util.getProblemArgumentsFromMarker(args);
				return returnText(a, arguments);
			}
		} else if (a instanceof ProblemAnnotation) {
			ProblemAnnotation p = (ProblemAnnotation) a;
			String[] arguments = p.getArguments();
			return returnText(a, arguments);
		}
		return a.getText();
	}

	private String returnText(Annotation a, String[] arguments) {
		for (int i = 0; i < arguments.length; i++) {
			String ar = arguments[i];
			if (ar.startsWith(IProblem.DESCRIPTION_ARGUMENT_PREFIX)) {
				return a.getText()
						+ "\n"
						+ ar.substring(IProblem.DESCRIPTION_ARGUMENT_PREFIX
								.length());
			}
		}
		return a.getText();
	}
}
