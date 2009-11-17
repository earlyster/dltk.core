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
import org.eclipse.dltk.core.DLTKLanguageManager;
import org.eclipse.dltk.core.IDLTKLanguageToolkit;
import org.eclipse.dltk.core.IField;
import org.eclipse.dltk.core.IMethod;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.ISearchPatternProcessor;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.IType;
import org.eclipse.dltk.core.index2.IElementResolver;
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
 * @since 2.0
 * 
 */
public class ModelAccess {

	/**
	 * Finds field elements in index. Element qualifier (package name) will be
	 * calculated from the field name.
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
		return result.toArray(new IField[result.size()]);
	}

	/**
	 * Finds field elements in index
	 * 
	 * @param qualifier
	 *            Element qualifier (package name)
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
	public IField[] findFields(String qualifier, String name,
			MatchRule matchRule, int trueFlags, int falseFlags,
			IDLTKSearchScope scope, IProgressMonitor monitor) {

		List<IField> result = new LinkedList<IField>();
		if (!findElements(IModelElement.FIELD, qualifier, name, matchRule,
				trueFlags, falseFlags, scope, result, monitor)) {
			return null;
		}
		return result.toArray(new IField[result.size()]);
	}

	/**
	 * Finds method elements in index.Element qualifier (package name) will be
	 * calculated from the method name.
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
		return result.toArray(new IMethod[result.size()]);
	}

	/**
	 * Finds method elements in index
	 * 
	 * @param qualifier
	 *            Element qualifier (package name)
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
	public IMethod[] findMethods(String qualifier, String name,
			MatchRule matchRule, int trueFlags, int falseFlags,
			IDLTKSearchScope scope, IProgressMonitor monitor) {

		List<IMethod> result = new LinkedList<IMethod>();
		if (!findElements(IModelElement.METHOD, qualifier, name, matchRule,
				trueFlags, falseFlags, scope, result, monitor)) {
			return null;
		}
		return result.toArray(new IMethod[result.size()]);
	}

	/**
	 * Finds type elements in index. Element qualifier (package name) will be
	 * calculated from the type name.
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
		return result.toArray(new IType[result.size()]);
	}

	/**
	 * Finds type elements in index.
	 * 
	 * @param qualifier
	 *            Element qualifier (package name)
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
	public IType[] findTypes(String qualifier, String name,
			MatchRule matchRule, int trueFlags, int falseFlags,
			IDLTKSearchScope scope, IProgressMonitor monitor) {

		List<IType> result = new LinkedList<IType>();
		if (!findElements(IModelElement.TYPE, qualifier, name, matchRule,
				trueFlags, falseFlags, scope, result, monitor)) {
			return null;
		}
		return result.toArray(new IType[result.size()]);
	}

	protected <T extends IModelElement> boolean findElements(int elementType,
			String name, MatchRule matchRule, int trueFlags, int falseFlags,
			IDLTKSearchScope scope, final Collection<T> result,
			IProgressMonitor monitor) {

		String qualifier = null;
		if (name != null) {
			ISearchPatternProcessor processor = DLTKLanguageManager
					.getSearchPatternProcessor(scope.getLanguageToolkit());
			if (processor != null) {
				String delim = processor.getDelimiterReplacementString();
				int i = name.lastIndexOf(delim);
				if (i != -1) {
					qualifier = name.substring(0, i);
					name = name.substring(i + 1);
				}
			}
		}

		return findElements(elementType, qualifier, name, matchRule, trueFlags,
				falseFlags, scope, result, monitor);
	}

	protected <T extends IModelElement> boolean findElements(int elementType,
			String qualifier, String name, MatchRule matchRule, int trueFlags,
			int falseFlags, IDLTKSearchScope scope, final Collection<T> result,
			IProgressMonitor monitor) {

		IDLTKLanguageToolkit toolkit = scope.getLanguageToolkit();
		if (toolkit == null) {
			return false;
		}
		final IElementResolver elementResolver = getElementResolver(toolkit);
		if (elementResolver == null) {
			return false;
		}
		ISearchEngine searchEngine = getSearchEngine(toolkit);
		if (searchEngine == null) {
			return false;
		}

		searchEngine.search(elementType, qualifier, name, trueFlags,
				falseFlags, 0, SearchFor.DECLARATIONS, matchRule, scope,
				new ISearchRequestor() {

					@SuppressWarnings("unchecked")
					public void match(int elementType, int flags, int offset,
							int length, int nameOffset, int nameLength,
							String elementName, String metadata,
							String qualifier, String parent,
							ISourceModule sourceModule, boolean isReference) {

						IModelElement element = elementResolver.resolve(
								elementType, flags, offset, length, nameOffset,
								nameLength, elementName, metadata, qualifier,
								parent, sourceModule);
						if (element != null) {
							result.add((T) element);
						}
					}
				}, monitor);

		return true;
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
		if ((searchRule & SearchPattern.R_PREFIX_MATCH) != 0) {
			matchRule = MatchRule.PREFIX;
		} else if ((searchRule & SearchPattern.R_CAMELCASE_MATCH) != 0) {
			matchRule = MatchRule.CAMEL_CASE;
		} else if ((searchRule & SearchPattern.R_PATTERN_MATCH) != 0) {
			matchRule = MatchRule.PATTERN;
		} else {
			matchRule = MatchRule.EXACT;
		}
		return matchRule;
	}

	/**
	 * Creates search participant
	 * 
	 * @param toolkit
	 *            Language toolkit
	 * @return indexer participant instance or <code>null</code> in case new
	 *         indexing infrastructure is not initiated
	 */
	public static IIndexerParticipant getIndexerParticipant(
			IDLTKLanguageToolkit toolkit) {
		if (toolkit == null) {
			return null;
		}
		IIndexer indexer = IndexerManager.getIndexer();
		if (indexer == null) {
			return null;
		}
		return IndexerManager.getIndexerParticipant(indexer, toolkit
				.getNatureId());
	}

	/**
	 * Creates new search engine instance
	 * 
	 * @param toolkit
	 *            Language toolkit
	 * @return search engine instance or <code>null</code> in case new indexing
	 *         infrastructure is not initiated
	 */
	public static ISearchEngine getSearchEngine(IDLTKLanguageToolkit toolkit) {
		if (toolkit != null) {
			IIndexer indexer = IndexerManager.getIndexer();
			if (indexer != null) {
				return indexer.createSearchEngine();
			}
		}
		return null;
	}

	/**
	 * Creates element resolver
	 * 
	 * @param toolkit
	 *            Language toolkit
	 * @return element resolver or <code>null</code> in case new indexing
	 *         infrastructure is not initiated
	 */
	public static IElementResolver getElementResolver(
			IDLTKLanguageToolkit toolkit) {
		if (toolkit != null) {
			IIndexerParticipant participant = getIndexerParticipant(toolkit);
			if (participant != null) {
				return participant.getElementResolver();
			}
		}
		return null;
	}
}