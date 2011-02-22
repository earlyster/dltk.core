/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.eclipse.dltk.internal.ui.text.hover;

import java.io.IOException;
import java.io.Reader;

import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.internal.ui.BrowserInformationControl;
import org.eclipse.dltk.internal.ui.text.HTMLPrinter;
import org.eclipse.dltk.internal.ui.text.HTMLTextPresenter;
import org.eclipse.dltk.internal.ui.text.IInformationControlExtension4;
import org.eclipse.dltk.ui.ScriptElementLabels;
import org.eclipse.dltk.ui.documentation.IDocumentationResponse;
import org.eclipse.dltk.ui.documentation.IScriptDocumentationTitleAdapter;
import org.eclipse.dltk.ui.documentation.ScriptDocumentationAccess;
import org.eclipse.dltk.ui.documentation.TextDocumentationResponse;
import org.eclipse.dltk.utils.TextUtils;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.AbstractReusableInformationControlCreator;
import org.eclipse.jface.text.DefaultInformationControl;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.ITextHoverExtension;
import org.eclipse.jface.text.information.IInformationProviderExtension2;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.editors.text.EditorsUI;

/**
 * Provides documentation as hover info for Script elements and keywords.
 * 
 * 
 */
public class DocumentationHover extends AbstractScriptEditorTextHover implements
		IInformationProviderExtension2, ITextHoverExtension {

	private final long LABEL_FLAGS = // ScriptElementLabels.ALL_FULLY_QUALIFIED
	ScriptElementLabels.M_APP_RETURNTYPE
			| ScriptElementLabels.F_APP_TYPE_SIGNATURE
			| ScriptElementLabels.M_PARAMETER_TYPES
			| ScriptElementLabels.M_PARAMETER_NAMES
			| ScriptElementLabels.M_EXCEPTIONS
			| ScriptElementLabels.F_PRE_TYPE_SIGNATURE
			| ScriptElementLabels.M_PRE_TYPE_PARAMETERS
			| ScriptElementLabels.T_TYPE_PARAMETERS
			| ScriptElementLabels.USE_RESOLVED;
	private final long LOCAL_VARIABLE_FLAGS = LABEL_FLAGS
			& ~ScriptElementLabels.F_FULLY_QUALIFIED
			| ScriptElementLabels.F_POST_QUALIFIED;

	/**
	 * The hover control creator.
	 * 
	 * 
	 */
	private IInformationControlCreator fHoverControlCreator;
	/**
	 * The presentation control creator.
	 * 
	 * 
	 */
	private IInformationControlCreator fPresenterControlCreator;

	public IInformationControlCreator getInformationPresenterControlCreator() {
		if (fPresenterControlCreator == null) {
			fPresenterControlCreator = new AbstractReusableInformationControlCreator() {
				public IInformationControl doCreateInformationControl(
						Shell parent) {
					if (BrowserInformationControl.isAvailable(parent))
						return new BrowserInformationControl(parent,
								JFaceResources.DIALOG_FONT, true);
					else
						return new DefaultInformationControl(parent,
								new HTMLTextPresenter(false));
				}
			};
		}
		return fPresenterControlCreator;
	}

	@Override
	public IInformationControlCreator getHoverControlCreator() {
		if (fHoverControlCreator == null) {
			fHoverControlCreator = new AbstractReusableInformationControlCreator() {
				public IInformationControl doCreateInformationControl(
						Shell parent) {
					if (BrowserInformationControl.isAvailable(parent))
						return new BrowserInformationControl(parent,
								JFaceResources.DIALOG_FONT,
								EditorsUI
								.getTooltipAffordanceString());
					else
						return new DefaultInformationControl(parent,
								EditorsUI.getTooltipAffordanceString(),
								new HTMLTextPresenter(true));
				}

				public boolean canReuse(IInformationControl control) {
					boolean canReuse = super.canReuse(control);
					if (canReuse
							&& control instanceof IInformationControlExtension4)
						((IInformationControlExtension4) control)
								.setStatusText(EditorsUI
										.getTooltipAffordanceString());
					return canReuse;

				}
			};
		}
		return fHoverControlCreator;
	}

	@Override
	protected String getHoverInfo(String nature, Object[] result) {
		StringBuffer buffer = new StringBuffer();
		int nResults = result.length;
		if (nResults == 0)
			return null;

		boolean hasContents = false;
		if (nResults > 1) {
			HTMLPrinter
					.addSmallHeader(buffer, titleAdapter.getTitle(result[0]));
			HTMLPrinter.addParagraph(buffer, "<hr>"); //$NON-NLS-1$
			for (int i = 0; i < result.length; i++) {
				Object element = result[i];
				Reader reader = ScriptDocumentationAccess.getHTMLContentReader(
						nature, element, true, true);
				if (reader == null) {
					continue;
				}
				if (hasContents) {
					HTMLPrinter.addParagraph(buffer, "<hr>"); //$NON-NLS-1$
				}
				HTMLPrinter.addParagraph(buffer, reader);
				hasContents = true;
			}
		} else {
			Object element = result[0];
			IDocumentationResponse response = ScriptDocumentationAccess
					.getDocumentation(nature, element, titleAdapter);
			// Provide hint why there's no doc
			if (response == null) {
				response = new TextDocumentationResponse(
						element,
						titleAdapter.getTitle(element),
						ScriptHoverMessages.ScriptdocHover_noAttachedInformation);
			}
			try {
				HTMLPrinter.addSmallHeader(buffer, response.getTitle());
				HTMLPrinter.addParagraph(buffer, response.getReader());
				hasContents = true;
			} catch (IOException e) {
				return null;
			}
			/*
			 * else if (curr.getElementType() == IModelElement.LOCAL_VARIABLE ||
			 * curr.getElementType() == IModelElement.TYPE_PARAMETER) {
			 * HTMLPrinter.addSmallHeader(buffer, getInfoText(curr));
			 * hasContents= true; }
			 */
		}
		if (!hasContents)
			return null;
		if (buffer.length() > 0) {
			HTMLPrinter.insertPageProlog(buffer, 0, getStyleSheet());
			HTMLPrinter.addPageEpilog(buffer);
			return buffer.toString();
		}
		return null;
	}

	@Override
	protected String getHoverInfo(String nature, String content) {
		try {
			Reader reader = ScriptDocumentationAccess.getKeywordDocumentation(
					nature, getEditorInputModelElement(), content);
			if (reader != null) {
				StringBuffer buffer = new StringBuffer();
				HTMLPrinter.addParagraph(buffer, reader);
				if (buffer.length() > 0) {
					HTMLPrinter.insertPageProlog(buffer, 0, getStyleSheet());
					HTMLPrinter.addPageEpilog(buffer);
					return buffer.toString();
				}
			}
		} catch (ModelException ex) {
			// TODO: log
		}
		return null;
	}

	private final IScriptDocumentationTitleAdapter titleAdapter = new IScriptDocumentationTitleAdapter() {
		public String getTitle(Object element) {
			if (element instanceof IModelElement) {
				IModelElement member = (IModelElement) element;
				long flags = member.getElementType() == IModelElement.FIELD ? LOCAL_VARIABLE_FLAGS
						: LABEL_FLAGS;
				String label = ScriptElementLabels.getDefault()
						.getElementLabel(member, flags);
				return TextUtils.escapeHTML(label);
			} else {
				return null;
			}
		}
	};
}
