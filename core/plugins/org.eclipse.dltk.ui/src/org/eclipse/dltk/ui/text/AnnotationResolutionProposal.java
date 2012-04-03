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

import org.eclipse.dltk.ui.editor.IScriptAnnotation;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.IMarkerResolution2;

public class AnnotationResolutionProposal implements ICompletionProposal {

	private final IAnnotationResolution resolution;
	private final IScriptAnnotation annotation;

	/**
	 * @param annotation
	 * @param resolution
	 */
	public AnnotationResolutionProposal(IAnnotationResolution resolution,
			IScriptAnnotation annotation) {
		this.resolution = resolution;
		this.annotation = annotation;
	}

	public void apply(IDocument document) {
		resolution.run(annotation, document);
	}

	public String getAdditionalProposalInfo() {
		if (resolution instanceof IAnnotationResolution2) {
			return ((IAnnotationResolution2) resolution).getDescription();
		}
		return annotation.getText();
	}

	public IContextInformation getContextInformation() {
		return null;
	}

	public String getDisplayString() {
		return resolution.getLabel();
	}

	public Image getImage() {
		if (resolution instanceof IMarkerResolution2) {
			return ((IMarkerResolution2) resolution).getImage();
		}
		return null;
	}

	public Point getSelection(IDocument document) {
		return null;
	}

}
