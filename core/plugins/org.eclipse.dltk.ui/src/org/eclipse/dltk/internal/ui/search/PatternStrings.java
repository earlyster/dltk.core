/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.eclipse.dltk.internal.ui.search;

import org.eclipse.dltk.core.DLTKLanguageManager;
import org.eclipse.dltk.core.IDLTKLanguageToolkit;
import org.eclipse.dltk.core.IField;
import org.eclipse.dltk.core.IMethod;
import org.eclipse.dltk.core.ISearchPatternProcessor;
import org.eclipse.dltk.core.IType;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.ui.ScriptElementLabels;

public class PatternStrings {
	public static String getMethodSignature(IMethod method) {
		final StringBuilder buffer = new StringBuilder();
		final IDLTKLanguageToolkit toolkit = DLTKLanguageManager
				.getLanguageToolkit(method);
		if (toolkit != null) {
			final ISearchPatternProcessor patternProcessor = DLTKLanguageManager
					.getSearchPatternProcessor(toolkit);
			final IType type = method.getDeclaringType();
			if (type != null) {
				if (patternProcessor != null) {
					buffer.append(type.getTypeQualifiedName(patternProcessor
							.getDelimiterReplacementString()));
				} else {
					buffer.append(type.getTypeQualifiedName());
				}
				if (patternProcessor != null) {
					buffer.append(patternProcessor
							.getDelimiterReplacementString());
				} else {
					buffer.append('.');
				}
				// TODO for constructor call method name could be optional
			}
		}
		buffer.append(method.getElementName());
		buffer.append("(");
		if (method.exists()) {
			try {
				int count = 0;
				for (String param : method.getParameterNames()) {
					if (count != 0) {
						buffer.append(",");
					}
					++count;
					buffer.append(param);
				}
			} catch (ModelException e) {
				//
			}
		}
		buffer.append(")");
		return buffer.toString();
	}

	public static String getTypeSignature(IType field) {
		return ScriptElementLabels.getDefault().getElementLabel(
				field,
				ScriptElementLabels.T_FULLY_QUALIFIED
						| ScriptElementLabels.T_TYPE_PARAMETERS
						| ScriptElementLabels.USE_RESOLVED);
	}

	public static String getFieldSignature(IField field) {
		return ScriptElementLabels.getDefault().getElementLabel(field,
				ScriptElementLabels.F_FULLY_QUALIFIED);
	}
}
