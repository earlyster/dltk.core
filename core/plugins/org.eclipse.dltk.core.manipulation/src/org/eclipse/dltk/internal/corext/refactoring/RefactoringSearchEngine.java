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
package org.eclipse.dltk.internal.corext.refactoring;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IDLTKLanguageToolkit;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.WorkingCopyOwner;
import org.eclipse.dltk.core.search.IDLTKSearchScope;
import org.eclipse.dltk.core.search.SearchEngine;
import org.eclipse.dltk.core.search.SearchMatch;
import org.eclipse.dltk.core.search.SearchPattern;
import org.eclipse.dltk.core.search.SearchRequestor;
import org.eclipse.dltk.internal.corext.util.SearchUtils;
import org.eclipse.ltk.core.refactoring.IRefactoringStatusEntryComparator;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.RefactoringStatusEntry;

/**
 * Convenience wrapper for {@link SearchEngine} - performs searching and sorts the results by {@link IResource}.
 * TODO: throw CoreExceptions from search(..) methods instead of wrapped JavaModelExceptions.
 */
public class RefactoringSearchEngine {

	private RefactoringSearchEngine(){
		//no instances
	}

	public static ISourceModule[] findAffectedCompilationUnits(SearchPattern pattern,
			IDLTKSearchScope scope, final IProgressMonitor pm, RefactoringStatus status, final boolean tolerateInAccurateMatches) throws CoreException {

		boolean hasNonCuMatches= false;

		class ResourceSearchRequestor extends SearchRequestor{
			boolean hasPotentialMatches= false ;
			Set<IResource> resources= new HashSet<IResource>(5);
			private IResource fLastResource;

			public void acceptSearchMatch(SearchMatch match) {
				if (!tolerateInAccurateMatches && match.getAccuracy() == SearchMatch.A_INACCURATE) {
					hasPotentialMatches= true;
				}
				if (fLastResource != match.getResource()) {
					fLastResource= match.getResource();
					resources.add(fLastResource);
				}
			}
		}
		ResourceSearchRequestor requestor = new ResourceSearchRequestor();
		new SearchEngine().search(pattern, SearchUtils.getDefaultSearchParticipants(), scope, requestor, pm);
		List<ISourceModule> result= new ArrayList<ISourceModule>(requestor.resources.size());
		for (IResource resource : requestor.resources) {
			IModelElement element= DLTKCore.create(resource);
			if (element instanceof ISourceModule) {
				result.add((ISourceModule)element);
			} else {
				hasNonCuMatches= true;
			}
		}
		addStatusErrors(status, requestor.hasPotentialMatches, hasNonCuMatches);
		return (ISourceModule[]) result.toArray(new ISourceModule[result.size()]);
	}

	public static ISourceModule[] findAffectedCompilationUnits(SearchPattern pattern,
			IDLTKSearchScope scope, final IProgressMonitor pm, RefactoringStatus status) throws CoreException {
		return findAffectedCompilationUnits(pattern, scope, pm, status, false);
	}

	/**
	 * Performs a search and groups the resulting {@link SearchMatch}es by
	 * {@link SearchResultGroup#getCompilationUnit()}.
	 * @param pattern the search pattern
	 * @param scope the search scope
	 * @param monitor the progress monitor
	 * @param status an error is added here if inaccurate or non-cu matches have been found
	 * @return a {@link SearchResultGroup}[], where each {@link SearchResultGroup}
	 * 		has a different {@link SearchMatch#getResource() getResource()}s.
	 * @see SearchMatch
	 * @throws JavaModelException when the search failed
	 */
	public static SearchResultGroup[] search(SearchPattern pattern, IDLTKSearchScope scope, IProgressMonitor monitor, RefactoringStatus status)
			throws CoreException {
		return internalSearch(new SearchEngine(), pattern, scope, new CollectingSearchRequestor(), monitor, status);
	}

	public static SearchResultGroup[] search(SearchPattern pattern, WorkingCopyOwner owner, IDLTKSearchScope scope, IProgressMonitor monitor, RefactoringStatus status)
			throws CoreException {
		return internalSearch(owner != null ? new SearchEngine(owner) : new SearchEngine(), pattern, scope, new CollectingSearchRequestor(), monitor, status);
	}

	public static SearchResultGroup[] search(SearchPattern pattern, IDLTKSearchScope scope, CollectingSearchRequestor requestor,
			IProgressMonitor monitor, RefactoringStatus status) throws CoreException {
		return internalSearch(new SearchEngine(), pattern, scope, requestor, monitor, status);
	}

	public static SearchResultGroup[] search(SearchPattern pattern, WorkingCopyOwner owner, IDLTKSearchScope scope,
			CollectingSearchRequestor requestor, IProgressMonitor monitor, RefactoringStatus status) throws CoreException {
		return internalSearch(owner != null ? new SearchEngine(owner) : new SearchEngine(), pattern, scope, requestor, monitor, status);
	}

	private static SearchResultGroup[] internalSearch(SearchEngine searchEngine, SearchPattern pattern, IDLTKSearchScope scope,
			CollectingSearchRequestor requestor, IProgressMonitor monitor, RefactoringStatus status) throws CoreException {
		searchEngine.search(pattern, SearchUtils.getDefaultSearchParticipants(), scope, requestor, monitor);
		return groupByCu(requestor.getResults(), status);
	}

	public static SearchResultGroup[] groupByCu(SearchMatch[] matches, RefactoringStatus status) {
		return groupByCu(Arrays.asList(matches), status);
	}

	/**
	 * @param matchList a List of SearchMatch
	 * @param status the status to report errors.
	 * @return a SearchResultGroup[], grouped by SearchMatch#getResource()
	 */
	public static SearchResultGroup[] groupByCu(List<SearchMatch> matchList, RefactoringStatus status) {
		Map<IResource, List<SearchMatch>> grouped= new HashMap<IResource, List<SearchMatch>>();
		boolean hasPotentialMatches= false;
		boolean hasNonCuMatches= false;

		for (SearchMatch searchMatch : matchList) {
			if (searchMatch.getAccuracy() == SearchMatch.A_INACCURATE)
				hasPotentialMatches= true;
			if (! grouped.containsKey(searchMatch.getResource()))
				grouped.put(searchMatch.getResource(), new ArrayList<SearchMatch>(1));
			grouped.get(searchMatch.getResource()).add(searchMatch);
		}

		for (Iterator<IResource> iter= grouped.keySet().iterator(); iter.hasNext();) {
			IResource resource= iter.next();
			IModelElement element= DLTKCore.create(resource);
			if (! (element instanceof ISourceModule)) {
				iter.remove();
				hasNonCuMatches= true;
			}
		}

		SearchResultGroup[] result= new SearchResultGroup[grouped.keySet().size()];
		int i= 0;
		for (IResource resource : grouped.keySet()) {
			List<SearchMatch> searchMatches= grouped.get(resource);
			SearchMatch[] matchArray= searchMatches.toArray(new SearchMatch[searchMatches.size()]);
			result[i]= new SearchResultGroup(resource, matchArray);
			i++;
		}
		addStatusErrors(status, hasPotentialMatches, hasNonCuMatches);
		return result;
	}

	public static SearchPattern createOrPattern(IModelElement[] elements, int limitTo, IDLTKLanguageToolkit toolkit) {
		if (elements == null || elements.length == 0)
			return null;
		Set<IModelElement> set= new HashSet<IModelElement>(Arrays.asList(elements));
		Iterator<IModelElement> iter= set.iterator();
		IModelElement first= iter.next();
		SearchPattern pattern= SearchPattern.createPattern(first, limitTo, SearchUtils.GENERICS_AGNOSTIC_MATCH_RULE, toolkit);
		if (pattern == null) // check for bug 90138
			throw new IllegalArgumentException("Invalid element: " + first.getHandleIdentifier() + "\n" + first.toString()); //$NON-NLS-1$ //$NON-NLS-2$
		while(iter.hasNext()){
			IModelElement each= (IModelElement)iter.next();
			SearchPattern nextPattern= SearchPattern.createPattern(each, limitTo, SearchUtils.GENERICS_AGNOSTIC_MATCH_RULE, toolkit);
			if (nextPattern == null) // check for bug 90138
				throw new IllegalArgumentException("Invalid element: " + each.getHandleIdentifier() + "\n" + each.toString()); //$NON-NLS-1$ //$NON-NLS-2$
			pattern= SearchPattern.createOrPattern(pattern, nextPattern);
		}
		return pattern;
	}

	private static boolean containsStatusEntry(final RefactoringStatus status, final RefactoringStatusEntry other) {
		return status.getEntries(new IRefactoringStatusEntryComparator() {
			public final int compare(final RefactoringStatusEntry entry1, final RefactoringStatusEntry entry2) {
				return entry1.getMessage().compareTo(entry2.getMessage());
			}
		}, other).length > 0;
	}

	private static void addStatusErrors(RefactoringStatus status, boolean hasPotentialMatches, boolean hasNonCuMatches) {
		if (hasPotentialMatches) {
			final RefactoringStatusEntry entry= new RefactoringStatusEntry(RefactoringStatus.ERROR, RefactoringCoreMessages.RefactoringSearchEngine_potential_matches);
			if (!containsStatusEntry(status, entry))
				status.addEntry(entry);
		}
		if (hasNonCuMatches) {
			final RefactoringStatusEntry entry= new RefactoringStatusEntry(RefactoringStatus.ERROR, RefactoringCoreMessages.RefactoringSearchEngine_non_cu_matches);
			if (!containsStatusEntry(status, entry))
				status.addEntry(entry);
		}
	}
}
