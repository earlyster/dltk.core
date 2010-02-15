/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     xored software, Inc. - initial API and Implementation
 *     xored software, Inc. - indenting tab policy fixes (Alex Panchenko) 
 *******************************************************************************/
package org.eclipse.dltk.ui.templates;

import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IPreferencesLookupDelegate;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.PreferencesLookupDelegate;
import org.eclipse.dltk.utils.TextUtils;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.TextUtilities;
import org.eclipse.jface.text.templates.DocumentTemplateContext;
import org.eclipse.jface.text.templates.Template;
import org.eclipse.jface.text.templates.TemplateBuffer;
import org.eclipse.jface.text.templates.TemplateContextType;
import org.eclipse.jface.text.templates.TemplateException;

public class ScriptTemplateContext extends DocumentTemplateContext implements
		IScriptTemplateContext {
	private final ISourceModule sourceModule;

	protected ScriptTemplateContext(TemplateContextType type,
			IDocument document, int completionOffset, int completionLength,
			ISourceModule sourceModule) {
		super(type, document, completionOffset, completionLength);

		if (sourceModule == null) {
			throw new IllegalArgumentException();
		}

		this.sourceModule = sourceModule;
	}

	public final ISourceModule getSourceModule() {
		return sourceModule;
	}

	protected IPreferencesLookupDelegate getPreferences() {
		return new PreferencesLookupDelegate(sourceModule.getScriptProject());
	}

	/**
	 * Tests if specified char is tab or space
	 * 
	 * @param ch
	 * @return
	 */
	private static boolean isSpaceOrTab(char ch) {
		return ch == ' ' || ch == '\t';
	}

	protected static String calculateIndent(IDocument document, int offset) {
		try {
			final IRegion region = document.getLineInformationOfOffset(offset);
			String indent = document.get(region.getOffset(), offset
					- region.getOffset());
			int i = 0;
			while (i < indent.length() && isSpaceOrTab(indent.charAt(i))) {
				++i;
			}
			if (i > 0) {
				return indent.substring(0, i);
			}
		} catch (BadLocationException e) {
			if (DLTKCore.DEBUG) {
				e.printStackTrace();
			}
		}

		return ""; //$NON-NLS-1$
	}

	public TemplateBuffer evaluate(Template template)
			throws BadLocationException, TemplateException {
		if (!canEvaluate(template)) {
			return null;
		}
		final String[] lines = TextUtils.splitLines(template.getPattern());
		if (lines.length > 1) {
			final String delimeter = TextUtilities
					.getDefaultLineDelimiter(getDocument());
			final String indent = calculateIndent(getDocument(), getStart());
			final IScriptTemplateIndenter indenter = getIndenter();
			final StringBuffer buffer = new StringBuffer(lines[0]);

			// Except first line
			for (int i = 1; i < lines.length; i++) {
				buffer.append(delimeter);
				indenter.indentLine(buffer, indent, lines[i]);
			}

			template = new Template(template.getName(), template
					.getDescription(), template.getContextTypeId(), buffer
					.toString(), template.isAutoInsertable());
		}

		return super.evaluate(template);
	}

	/**
	 * @return
	 */
	protected IScriptTemplateIndenter getIndenter() {
		return new NopScriptTemplateIndenter();
	}
}
