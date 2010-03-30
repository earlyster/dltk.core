/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.eclipse.dltk.internal.ui.search;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.IParent;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.ui.DLTKUIPlugin;
import org.eclipse.dltk.ui.search.IMatchPresentation;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.search.ui.ISearchQuery;
import org.eclipse.search.ui.text.AbstractTextSearchResult;
import org.eclipse.search.ui.text.IEditorMatchAdapter;
import org.eclipse.search.ui.text.IFileMatchAdapter;
import org.eclipse.search.ui.text.Match;
import org.eclipse.ui.IEditorPart;

public class DLTKSearchResult extends AbstractTextSearchResult implements IEditorMatchAdapter, IFileMatchAdapter {
	
	private DLTKSearchQuery fQuery;
	private Map<Object, IMatchPresentation> fElementsToParticipants;
	private static final Match[] NO_MATCHES= new Match[0];
	
	public DLTKSearchResult(DLTKSearchQuery query) {
		fQuery= query;
		fElementsToParticipants= new HashMap<Object, IMatchPresentation>();
		setActiveMatchFilters(DLTKMatchFilter.getLastUsedFilters());
	}

	public ImageDescriptor getImageDescriptor() {
		return fQuery.getImageDescriptor();
	}

	public String getLabel() {
		return fQuery.getResultLabel(getMatchCount());
	}

	public String getTooltip() {
		return getLabel();
	}
		
	public Match[] computeContainedMatches(AbstractTextSearchResult result, IEditorPart editor) {
		return computeContainedMatches(editor.getEditorInput());
	}

	public Match[] computeContainedMatches(AbstractTextSearchResult result, IFile file) {
		return computeContainedMatches(file);
	}
	
	private Match[] computeContainedMatches(IAdaptable adaptable) {
		IModelElement modelElement= (IModelElement) adaptable.getAdapter(IModelElement.class);
		Set<Match> matches= new HashSet<Match>();
		if (modelElement != null) {
			collectMatches(matches, modelElement);
		}
		IFile file= (IFile) adaptable.getAdapter(IFile.class);
		if (file != null) {
			collectMatches(matches, file);
		}
		if (!matches.isEmpty()) {
			return matches.toArray(new Match[matches.size()]);
		}
		return NO_MATCHES;
	}
	
	
	private void collectMatches(Set<Match> matches, IFile element) {
		Match[] m= getMatches(element);
		if (m.length != 0) {
			for (int i= 0; i < m.length; i++) {
				matches.add(m[i]);
			}
		}
	}
	
	private void collectMatches(Set<Match> matches, IModelElement element) {
		Match[] m= getMatches(element);
		if (m.length != 0) {
			for (int i= 0; i < m.length; i++) {
				matches.add(m[i]);
			}
		}
		if (element instanceof IParent) {
			IParent parent= (IParent) element;
			try {
				IModelElement[] children= parent.getChildren();
				for (int i= 0; i < children.length; i++) {
					collectMatches(matches, children[i]);
				}
			} catch (ModelException e) {
				// we will not be tracking these results
			}
		}
	}
	/* (non-Javadoc)
	 * @see org.eclipse.search.ui.ISearchResultCategory#getFile(java.lang.Object)
	 */
	public IFile getFile(Object element) {
		if (element instanceof IModelElement) {
			IModelElement modelElement= (IModelElement) element;
			ISourceModule cu= (ISourceModule) modelElement.getAncestor(IModelElement.SOURCE_MODULE);
			if (cu != null) {
				return (IFile) cu.getResource();
			}
			return null;
		}
		if (element instanceof IFile)
			return (IFile) element;
		return null;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.search2.ui.text.IStructureProvider#isShownInEditor(org.eclipse.search2.ui.text.Match,
	 *      org.eclipse.ui.IEditorPart)
	 */
	public boolean isShownInEditor(Match match, IEditorPart editor) {
		Object element= match.getElement();
		if (element instanceof IModelElement) {
			element= ((IModelElement) element).getOpenable(); // class file or compilation unit 
			return element != null && element.equals(editor.getEditorInput().getAdapter(IModelElement.class));
		} else if (element instanceof IFile) {
			return element.equals(editor.getEditorInput().getAdapter(IFile.class));
		}
		return false;
	}

	/*
	 * @see AbstractTextSearchResult#getAllMatchFilters()
	 */
	@Override
	public org.eclipse.search.ui.text.MatchFilter[] getAllMatchFilters() {
		return DLTKMatchFilter.allFilters();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.search.ui.ISearchResult#getQuery()
	 */
	public ISearchQuery getQuery() {
		return fQuery;
	}
	
	synchronized IMatchPresentation getSearchParticpant(Object element) {
		return fElementsToParticipants.get(element);
	}

	boolean addMatch(Match match, IMatchPresentation participant) {
		Object element= match.getElement();
		if (fElementsToParticipants.get(element) != null) {
			// TODO must access the participant id / label to properly report the error.
			DLTKUIPlugin.log(new Status(IStatus.WARNING, DLTKUIPlugin.getPluginId(), 0, "A second search participant was found for an element", null)); //$NON-NLS-1$
			return false;
		}
		fElementsToParticipants.put(element, participant);
		addMatch(match);
		return true;
	}
	
	public void removeAll() {
		synchronized(this) {
			fElementsToParticipants.clear();
		}
		super.removeAll();
	}
	
	public void removeMatch(Match match) {
		synchronized(this) {
			if (getMatchCount(match.getElement()) == 1)
				fElementsToParticipants.remove(match.getElement());
		}
		super.removeMatch(match);
	}
	public IFileMatchAdapter getFileMatchAdapter() {
		return this;
	}
	
	public IEditorMatchAdapter getEditorMatchAdapter() {
		return this;
	}

}
