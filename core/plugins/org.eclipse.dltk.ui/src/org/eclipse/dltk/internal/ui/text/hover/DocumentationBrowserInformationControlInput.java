/*******************************************************************************
 * Copyright (c) 2008, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.dltk.internal.ui.text.hover;

import org.eclipse.core.runtime.Assert;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.jface.internal.text.html.BrowserInformationControlInput;

/**
 * Browser input for Javadoc hover.
 * 
 * @since 4.0
 */
@SuppressWarnings("restriction")
public class DocumentationBrowserInformationControlInput extends
		BrowserInformationControlInput {

	private final Object fElement;
	private final String fHtml;
	private final int fLeadingImageWidth;

	/**
	 * Creates a new browser information control input.
	 * 
	 * @param previous
	 *            previous input, or <code>null</code> if none available
	 * @param element
	 *            the element, or <code>null</code> if none available
	 * @param html
	 *            HTML contents, must not be null
	 * @param leadingImageWidth
	 *            the indent required for the element image
	 */
	public DocumentationBrowserInformationControlInput(
			DocumentationBrowserInformationControlInput previous,
			Object element, String html, int leadingImageWidth) {
		super(previous);
		Assert.isNotNull(html);
		fElement = element;
		fHtml = html;
		fLeadingImageWidth = leadingImageWidth;
	}

	/*
	 * @see BrowserInformationControlInput#getLeadingImageWidth()
	 * 
	 * @since 4.0
	 */
	@Override
	public int getLeadingImageWidth() {
		return fLeadingImageWidth;
	}

	/**
	 * Returns the Java element.
	 * 
	 * @return the element or <code>null</code> if none available
	 */
	public Object getElement() {
		return fElement;
	}

	/*
	 * @see org.eclipse.jface.internal.text.html.BrowserInput#getHtml()
	 */
	@Override
	public String getHtml() {
		return fHtml;
	}

	/*
	 * @see org.eclipse.jdt.internal.ui.infoviews.BrowserInput#getInputElement()
	 */
	@Override
	public Object getInputElement() {
		return fElement == null ? (Object) fHtml : fElement;
	}

	/*
	 * @see org.eclipse.jdt.internal.ui.infoviews.BrowserInput#getInputName()
	 */
	@Override
	public String getInputName() {
		return fElement instanceof IModelElement ? ((IModelElement) fElement)
				.getElementName() : ""; //$NON-NLS-1$
	}

}
