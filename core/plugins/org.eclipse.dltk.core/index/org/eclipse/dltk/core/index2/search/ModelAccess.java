/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Zend Technologies
 *******************************************************************************/
package org.eclipse.dltk.core.index2.search;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.dltk.core.IDLTKLanguageToolkit;
import org.eclipse.dltk.core.IField;
import org.eclipse.dltk.core.IMethod;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.IType;
import org.eclipse.dltk.core.index2.IIndexer;
import org.eclipse.dltk.core.index2.IIndexerParticipant;
import org.eclipse.dltk.core.index2.search.ISearchEngine.MatchRule;
import org.eclipse.dltk.core.index2.search.ISearchEngine.SearchFor;
import org.eclipse.dltk.core.search.IDLTKSearchScope;
import org.eclipse.dltk.core.search.SearchPattern;
import org.eclipse.dltk.internal.core.index2.IndexerManager;

/**
 * Utility for accessing DLTK model elements through index
 * 
 * @author michael
 * 
 */
public class ModelAccess {

	/**
	 * Finds field elements in index
	 * 
	 * @param name
	 *            Element name
	 * @param matchRule
	 *            Match rule
	 * @param trueFlags
	 *            Logical OR of flags that must exist in element flags bitset.
	 *            Set to <code>0</code> to disable filtering by trueFlags.
	 * @param falseFlags
	 *            Logical OR of flags that must not exist in the element flags
	 *            bitset. Set to <code>0</code> to disable filtering by
	 *            falseFlags.
	 * @param scope
	 *            Search scope
	 * @param monitor
	 *            Progress monitor
	 * @return elements array, or <code>null</code> in case error has occurred.
	 */
	public IField[] findFields(String name, MatchRule matchRule, int trueFlags,
			int falseFlags, IDLTKSearchScope scope, IProgressMonitor monitor) {

		List<IField> result = new LinkedList<IField>();
		if (!findElements(IModelElement.FIELD, name, matchRule, trueFlags,
				falseFlags, scope, result, monitor)) {
			return null;
		}
		return (IField[]) result.toArray(new IField[result.size()]);
	}

	/**
	 * Finds method elements in index
	 * 
	 * @param name
	 *            Element name
	 * @param matchRule
	 *            Match rule
	 * @param trueFlags
	 *            Logical OR of flags that must exist in element flags bitset.
	 *            Set to <code>0</code> to disable filtering by trueFlags.
	 * @param falseFlags
	 *            Logical OR of flags that must not exist in the element flags
	 *            bitset. Set to <code>0</code> to disable filtering by
	 *            falseFlags.
	 * @param scope
	 *            Search scope
	 * @param monitor
	 *            Progress monitor
	 * @return elements array, or <code>null</code> in case error has occurred.
	 */
	public IMethod[] findMethods(String name, MatchRule matchRule,
			int trueFlags, int falseFlags, IDLTKSearchScope scope,
			IProgressMonitor monitor) {

		List<IMethod> result = new LinkedList<IMethod>();
		if (!findElements(IModelElement.METHOD, name, matchRule, trueFlags,
				falseFlags, scope, result, monitor)) {
			return null;
		}
		return (IMethod[]) result.toArray(new IMethod[result.size()]);
	}

	/**
	 * Finds type elements in index
	 * 
	 * @param name
	 *            Element name
	 * @param matchRule
	 *            Match rule
	 * @param trueFlags
	 *            Logical OR of flags that must exist in element flags bitset.
	 *            Set to <code>0</code> to disable filtering by trueFlags.
	 * @param falseFlags
	 *            Logical OR of flags that must not exist in the element flags
	 *            bitset. Set to <code>0</code> to disable filtering by
	 *            falseFlags.
	 * @param scope
	 *            Search scope
	 * @param monitor
	 *            Progress monitor
	 * @return elements array, or <code>null</code> in case error has occurred.
	 */
	public IType[] findTypes(String name, MatchRule matchRule, int trueFlags,
			int falseFlags, IDLTKSearchScope scope, IProgressMonitor monitor) {

		List<IType> result = new LinkedList<IType>();
		if (!findElements(IModelElement.TYPE, name, matchRule, trueFlags,
				falseFlags, scope, result, monitor)) {
			return null;
		}
		return (IType[]) result.toArray(new IType[result.size()]);
	}

	/**
	 * Converts old-style search flags to MatchRule.
	 * 
	 * @param searchRule
	 *            Search flags: {@link SearchPattern#R_EXACT_MATCH}, etc...
	 * @return match rule. If searchRule is not supported by the new mechanism
	 *         {@link MatchRule#EXACT} is returned.
	 */
	public static MatchRule convertSearchRule(int searchRule) {
		MatchRule matchRule;
		switch (searchRule) {
		case SearchPattern.R_PREFIX_MATCH:
			matchRule = MatchRule.PREFIX;
			break;
		case SearchPattern.R_CAMELCASE_MATCH:
			matchRule = MatchRule.CAMEL_CASE;
			break;
		case SearchPattern.R_PATTERN_MATCH:
			matchRule = MatchRule.PATTERN;
			break;
		default:
			matchRule = MatchRule.EXACT;
		}
		return matchRule;
	}

	public static ISearchEngine createSearchEngine(IDLTKLanguageToolkit toolkit) {
		if (toolkit != null) {
			IIndexer indexer = IndexerManager.getIndexer();
			if (indexer != null) {
				return indexer.createSearchEngine();
			}
		}
		return null;
	}

	protected <T extends IModelElement> boolean findElements(int elementType,
			String name, MatchRule matchRule, int trueFlags, int falseFlags,
			IDLTKSearchScope scope, final Collection<T> result,
			IProgressMonitor monitor) {

		IIndexer indexer = IndexerManager.getIndexer();
		final IIndexerParticipant participant = IndexerManager
				.getIndexerParticipant(indexer, scope.getLanguageToolkit()
						.getNatureId());
		if (indexer == null || participant == null) {
			return false;
		}
		ISearchEngine searchEngine = indexer.createSearchEngine();

		searchEngine.search(elementType, name, trueFlags, falseFlags, 0,
				SearchFor.DECLARATIONS, matchRule, scope,
				new ISearchRequestor() {

					public void match(int elementType, int flags, int offset,
							int length, int nameOffset, int nameLength,
							String elementName, String metadata,
							String qualifier, String parent,
							ISourceModule sourceModule, boolean isReference) {

						IModelElement element = participant
								.getElementResolver().resolve(elementType,
										flags, offset, length, nameOffset,
										nameLength, elementName, metadata,
										qualifier, parent, sourceModule);
						if (element != null) {
							result.add((T) element);
						}
					}
				}, monitor);

		return true;
	}
}