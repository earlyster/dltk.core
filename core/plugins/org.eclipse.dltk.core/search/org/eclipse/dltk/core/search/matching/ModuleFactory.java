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
package org.eclipse.dltk.core.search.matching;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IPath;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.IProjectFragment;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.core.search.IDLTKSearchScope;
import org.eclipse.dltk.core.search.SearchDocument;
import org.eclipse.dltk.core.search.SearchParticipant;
import org.eclipse.dltk.core.search.SearchPattern;
import org.eclipse.dltk.internal.core.ArchiveProjectFragment;
import org.eclipse.dltk.internal.core.Openable;
import org.eclipse.dltk.internal.core.search.DLTKSearchDocument;
import org.eclipse.dltk.internal.core.search.IndexSelector;
import org.eclipse.dltk.internal.core.util.HandleFactory;

public class ModuleFactory {

	private final HandleFactory handleFactory;
	private final IDLTKSearchScope scope;

	public ModuleFactory(IDLTKSearchScope scope) {
		this(new HandleFactory(), scope);
	}

	ModuleFactory(HandleFactory handleFactory, IDLTKSearchScope scope) {
		this.handleFactory = handleFactory;
		this.scope = scope;
	}

	public ISourceModule create(SearchDocument searchDocument) {
		if (searchDocument instanceof WorkingCopyDocument) {
			return ((WorkingCopyDocument) searchDocument).workingCopy;
		} else {
			final Openable openable = this.handleFactory.createOpenable(
					searchDocument.getPath(), this.scope);
			return openable instanceof ISourceModule ? (ISourceModule) openable
					: null;
		}
	}

	public static ISourceModule[] selectWorkingCopies(
			SearchDocument[] searchDocuments) {
		final List<ISourceModule> copies = new ArrayList<ISourceModule>();
		for (int i = 0, length = searchDocuments.length; i < length; i++) {
			SearchDocument document = searchDocuments[i];
			if (document instanceof WorkingCopyDocument) {
				copies.add(((WorkingCopyDocument) document).workingCopy);
			}
		}
		return copies.toArray(new ISourceModule[copies.size()]);
	}

	private static class WorkingCopyDocument extends DLTKSearchDocument {
		public org.eclipse.dltk.core.ISourceModule workingCopy;

		WorkingCopyDocument(org.eclipse.dltk.core.ISourceModule workingCopy,
				SearchParticipant participant, boolean external) {
			super(workingCopy.getPath().toString(), getContents(workingCopy),
					participant, external, workingCopy.getScriptProject()
							.getProject());
			this.workingCopy = workingCopy;
		}

		private static char[] getContents(
				org.eclipse.dltk.core.ISourceModule workingCopy) {
			try {
				return workingCopy.getSourceAsCharArray();
			} catch (ModelException e) {
				if (DLTKCore.DEBUG) {
					e.printStackTrace();
				}
				return new char[0];
			}
		}

		@Override
		public String toString() {
			return "WorkingCopyDocument for " + getPath(); //$NON-NLS-1$
		}
	}

	public static SearchDocument[] addWorkingCopies(SearchPattern pattern,
			SearchDocument[] indexMatches, ISourceModule[] copies,
			SearchParticipant participant) {
		// working copies take precedence over corresponding compilation units
		Map<String, SearchDocument> workingCopyDocuments = ModuleFactory
				.workingCopiesThatCanSeeFocus(copies, pattern.focus, pattern
						.isPolymorphicSearch(), participant);
		SearchDocument[] matches = null;
		int length = indexMatches.length;
		for (int i = 0; i < length; i++) {
			SearchDocument searchDocument = indexMatches[i];
			if (searchDocument.getParticipant() == participant) {
				SearchDocument workingCopyDocument = workingCopyDocuments
						.remove(searchDocument.getPath());
				if (workingCopyDocument != null) {
					if (matches == null) {
						System
								.arraycopy(indexMatches, 0,
										matches = new SearchDocument[length],
										0, length);
					}
					matches[i] = workingCopyDocument;
				}
			}
		}
		if (matches == null) { // no working copy
			matches = indexMatches;
		}
		int remainingWorkingCopiesSize = workingCopyDocuments.size();
		if (remainingWorkingCopiesSize != 0) {
			System.arraycopy(matches, 0, matches = new SearchDocument[length
					+ remainingWorkingCopiesSize], 0, length);
			int index = length;
			for (SearchDocument document : workingCopyDocuments.values()) {
				matches[index++] = document;
			}
		}
		return matches;
	}

	/*
	 * Returns the working copies that can see the given focus.
	 */
	private static Map<String, SearchDocument> workingCopiesThatCanSeeFocus(
			ISourceModule[] copies, IModelElement focus,
			boolean isPolymorphicSearch, SearchParticipant participant) {
		if (copies == null)
			return Collections.emptyMap();
		if (focus != null) {
			while (!(focus instanceof IScriptProject)
					&& !(focus instanceof ArchiveProjectFragment)) {
				focus = focus.getParent();
			}
		}
		Map<String, SearchDocument> result = new HashMap<String, SearchDocument>();
		for (int i = 0, length = copies.length; i < length; i++) {
			org.eclipse.dltk.core.ISourceModule workingCopy = copies[i];
			IPath projectOrArchive = MatchLocator.getProjectOrArchive(
					workingCopy).getPath();
			if (focus == null
					|| IndexSelector.canSeeFocus(focus, isPolymorphicSearch,
							projectOrArchive)) {
				boolean external = false;
				IProjectFragment frag = (IProjectFragment) workingCopy
						.getAncestor(IModelElement.PROJECT_FRAGMENT);
				if (frag != null) {
					external = frag.isExternal();
				}

				result.put(workingCopy.getPath().toString(),
						new WorkingCopyDocument(workingCopy, participant,
								external));
			}
		}
		return result;
	}

}
