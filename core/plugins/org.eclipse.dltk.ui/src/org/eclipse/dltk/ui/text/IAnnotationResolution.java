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

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.source.Annotation;

/**
 * Resolution for a annotation. When run, a resolution would typically eliminate
 * the need for the annotation.
 */
public interface IAnnotationResolution {

	/**
	 * Returns a short label indicating what the resolution will do.
	 * 
	 * @return a short label for this resolution
	 */
	public String getLabel();

	/**
	 * Runs this resolution.
	 * 
	 * @param annotation
	 *            the annotation to resolve
	 * @param document
	 */
	void run(Annotation annotation, IDocument document);

}
