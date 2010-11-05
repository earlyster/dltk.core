/*******************************************************************************
 * Copyright (c) 2010 xored software, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     xored software, Inc. - initial API and Implementation (Alex Panchenko)
 *******************************************************************************/
package org.eclipse.dltk.ui.text.folding;

import org.eclipse.dltk.core.IMember;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.IModelElementVisitor;
import org.eclipse.dltk.core.ISourceRange;
import org.eclipse.dltk.core.ISourceReference;
import org.eclipse.dltk.core.IType;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.core.SourceRange;
import org.eclipse.dltk.ui.DLTKUIPlugin;
import org.eclipse.jface.preference.IPreferenceStore;

/**
 * The implementation of {@link IFoldingBlockProvider} performing folding based
 * on the structure of the source file.
 * 
 * @since 3.0
 */
public class ModelFoldingBlockProvider implements IFoldingBlockProvider,
		IModelElementVisitor {

	public void initializePreferences(IPreferenceStore preferenceStore) {
	}

	private IFoldingBlockRequestor requestor;

	public void setRequestor(IFoldingBlockRequestor requestor) {
		this.requestor = requestor;
	}

	public int getMinimalLineCount() {
		return 0;
	}

	public void computeFoldableBlocks(IFoldingContent content) {
		try {
			content.getModelElement().accept(this);
		} catch (ModelException e) {
			abortFolding(e);
		}
	}

	private void abortFolding(ModelException e) {
		if (!e.isDoesNotExist()) {
			DLTKUIPlugin.logErrorMessage("Error when computing folding", e);
		}
		throw new AbortFoldingException();
	}

	public boolean visit(IModelElement element) {
		if (element instanceof IType || element instanceof IMember) {
			reportElement(element);
		}
		return true;
	}

	protected void reportElement(IModelElement element) {
		if (!(element instanceof ISourceReference)) {
			return;
		}
		try {
			final ISourceRange range = ((ISourceReference) element)
					.getSourceRange();
			if (SourceRange.isAvailable(range) && range.getLength() > 0) {
				requestor.acceptBlock(range.getOffset(), range.getOffset()
						+ range.getLength(), getKind(element), element,
						isFoldedInitially(element));
			}
		} catch (ModelException e) {
			abortFolding(e);
		}
	}

	protected boolean isFoldedInitially(IModelElement element) {
		return false;
	}

	private static enum FoldingBlockKind implements IFoldingBlockKind {

		TYPE, METHOD;

		public boolean isComment() {
			return false;
		}

	}

	protected IFoldingBlockKind getKind(IModelElement element) {
		return element.getElementType() == IModelElement.TYPE ? FoldingBlockKind.TYPE
				: FoldingBlockKind.METHOD;
	}

}
