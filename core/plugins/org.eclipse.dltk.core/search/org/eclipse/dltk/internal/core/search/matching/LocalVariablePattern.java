/*******************************************************************************
 * Copyright (c) 2000, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.dltk.internal.core.search.matching;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.dltk.core.IDLTKLanguageToolkit;
import org.eclipse.dltk.core.ILocalVariable;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.IProjectFragment;
import org.eclipse.dltk.core.model.LocalVariable;
import org.eclipse.dltk.core.search.IDLTKSearchConstants;
import org.eclipse.dltk.core.search.IDLTKSearchScope;
import org.eclipse.dltk.core.search.SearchParticipant;
import org.eclipse.dltk.core.search.index.Index;
import org.eclipse.dltk.core.search.indexing.IIndexConstants;
import org.eclipse.dltk.internal.compiler.env.AccessRuleSet;
import org.eclipse.dltk.internal.core.search.DLTKSearchScope;
import org.eclipse.dltk.internal.core.search.IndexQueryRequestor;
import org.eclipse.dltk.internal.core.util.Util;

public class LocalVariablePattern extends VariablePattern implements
		IIndexConstants {

	private final ILocalVariable localVariable;

	public LocalVariablePattern(ILocalVariable localVariable, int limitTo,
			int matchRule, IDLTKLanguageToolkit toolkit) {
		this(localVariable, isDeclarations(limitTo), isReferences(limitTo),
				matchRule, toolkit);
	}

	private static boolean isReferences(int limitTo) {
		return limitTo == IDLTKSearchConstants.REFERENCES
				|| limitTo == IDLTKSearchConstants.ALL_OCCURRENCES;
	}

	private static boolean isDeclarations(int limitTo) {
		return limitTo == IDLTKSearchConstants.DECLARATIONS
				|| limitTo == IDLTKSearchConstants.ALL_OCCURRENCES;
	}

	private LocalVariablePattern(ILocalVariable localVariable,
			boolean declarations, boolean references, int matchRule,
			IDLTKLanguageToolkit toolkit) {
		super(LOCAL_VAR_PATTERN, declarations, references, references,
				localVariable.getElementName().toCharArray(), matchRule,
				toolkit);
		this.localVariable = localVariable;
	}

	public void findIndexMatches(Index index, IndexQueryRequestor requestor,
			SearchParticipant participant, IDLTKSearchScope scope,
			IProgressMonitor progressMonitor) {
		IProjectFragment root = (IProjectFragment) this.localVariable
				.getAncestor(IModelElement.PROJECT_FRAGMENT);
		String documentPath;
		String relativePath;
		if (root.isArchive()) {
			// FIXME provide correct implementation
			documentPath = "";
			relativePath = "";
			// IType type = (IType) this.localVariable
			// .getAncestor(IModelElement.TYPE);
			// relativePath = (type.getFullyQualifiedName('$')).replace('.',
			// '/')
			// + SuffixConstants.SUFFIX_STRING_class;
			// documentPath = root.getPath()
			// + IJavaSearchScope.JAR_FILE_ENTRY_SEPARATOR + relativePath;
		} else {
			IPath path = this.localVariable.getPath();
			documentPath = path.toString();
			relativePath = Util
					.relativePath(path, 1/* remove project segment */);
		}

		if (scope instanceof DLTKSearchScope) {
			DLTKSearchScope javaSearchScope = (DLTKSearchScope) scope;
			// Get document path access restriction from java search scope
			// Note that requestor has to verify if needed whether the document
			// violates the access restriction or not
			AccessRuleSet access = javaSearchScope.getAccessRuleSet(
					relativePath, index.containerPath);
			if (access != DLTKSearchScope.NOT_ENCLOSED) { // scope encloses the
															// path
				if (!requestor.acceptIndexMatch(documentPath, this,
						participant, access))
					throw new OperationCanceledException();
			}
		} else if (scope.encloses(documentPath)) {
			if (!requestor.acceptIndexMatch(documentPath, this, participant,
					null))
				throw new OperationCanceledException();
		}
	}

	protected StringBuffer print(StringBuffer output) {
		if (this.findDeclarations) {
			output.append(this.findReferences ? "LocalVarCombinedPattern: " //$NON-NLS-1$
					: "LocalVarDeclarationPattern: "); //$NON-NLS-1$
		} else {
			output.append("LocalVarReferencePattern: "); //$NON-NLS-1$
		}
		if (localVariable instanceof LocalVariable) {
			output.append(((LocalVariable) localVariable)
					.toStringWithAncestors());
		} else {
			output.append(localVariable.getElementName());
		}
		return super.print(output);
	}

	public ILocalVariable getLocalVariable() {
		return localVariable;
	}
}
