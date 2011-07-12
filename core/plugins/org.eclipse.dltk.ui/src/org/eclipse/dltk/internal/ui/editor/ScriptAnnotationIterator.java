/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.eclipse.dltk.internal.ui.editor;

import java.util.Collections;
import java.util.Iterator;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.dltk.ui.editor.IScriptAnnotation;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.ui.texteditor.MarkerAnnotation;

/**
 * Filters problems based on their types.
 */
public class ScriptAnnotationIterator implements Iterator<Annotation> {

	private Iterator<?> fIterator;
	private Annotation fNext;
	private boolean fReturnAllAnnotations;

	/**
	 * Returns a new JavaAnnotationIterator.
	 * 
	 * @param model
	 *            the annotation model
	 * @param skipIrrelevants
	 *            whether to skip irrelevant annotations
	 * @param returnAllAnnotations
	 *            Whether to return non IJavaAnnotations as well
	 */
	@Deprecated
	public ScriptAnnotationIterator(IAnnotationModel model,
			boolean skipIrrelevants, boolean returnAllAnnotations) {
		this(model, returnAllAnnotations);
	}

	/**
	 * Returns a new JavaAnnotationIterator.
	 * 
	 * @param model
	 *            the annotation model
	 * @param returnAllAnnotations
	 *            Whether to return non IJavaAnnotations as well
	 */
	public ScriptAnnotationIterator(IAnnotationModel model,
			boolean returnAllAnnotations) {
		fReturnAllAnnotations = returnAllAnnotations;
		if (model != null)
			fIterator = model.getAnnotationIterator();
		else
			fIterator = Collections.EMPTY_LIST.iterator();
		skip();
	}

	private void skip() {
		while (fIterator.hasNext()) {
			Annotation next = (Annotation) fIterator.next();
			if (next.isMarkedDeleted())
				continue;
			if (fReturnAllAnnotations || next instanceof IScriptAnnotation
					|| isProblemMarkerAnnotation(next)) {
				fNext = next;
				return;
			}
		}
		fNext = null;
	}

	private static boolean isProblemMarkerAnnotation(Annotation annotation) {
		if (!(annotation instanceof MarkerAnnotation))
			return false;
		try {
			return (((MarkerAnnotation) annotation).getMarker()
					.isSubtypeOf(IMarker.PROBLEM));
		} catch (CoreException e) {
			return false;
		}
	}

	/*
	 * @see Iterator#hasNext()
	 */
	public boolean hasNext() {
		return fNext != null;
	}

	/**
	 * @see Iterator#next()
	 * @since 3.0
	 */
	public Annotation next() {
		try {
			return fNext;
		} finally {
			skip();
		}
	}

	/*
	 * @see Iterator#remove()
	 */
	public void remove() {
		throw new UnsupportedOperationException();
	}
}
