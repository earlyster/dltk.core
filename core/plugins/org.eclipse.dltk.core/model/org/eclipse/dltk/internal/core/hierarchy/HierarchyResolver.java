/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.dltk.internal.core.hierarchy;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.DLTKLanguageManager;
import org.eclipse.dltk.core.IDLTKLanguageToolkit;
import org.eclipse.dltk.core.IFileHierarchyInfo;
import org.eclipse.dltk.core.IFileHierarchyResolver;
import org.eclipse.dltk.core.ISearchPatternProcessor;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.IType;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.core.index2.search.ModelAccess;
import org.eclipse.dltk.core.index2.search.ISearchEngine.MatchRule;
import org.eclipse.dltk.core.search.IDLTKSearchConstants;
import org.eclipse.dltk.core.search.IDLTKSearchScope;
import org.eclipse.dltk.core.search.SearchEngine;
import org.eclipse.dltk.core.search.SearchPattern;
import org.eclipse.dltk.core.search.TypeNameRequestor;
import org.eclipse.dltk.internal.core.ModelElement;
import org.eclipse.dltk.internal.core.Openable;
import org.eclipse.dltk.internal.core.util.HandleFactory;

public class HierarchyResolver {

	private HierarchyBuilder hierarchyBuilder;

	public HierarchyResolver(HierarchyBuilder hierarchy) {
		this.hierarchyBuilder = hierarchy;
	}

	public void resolve(boolean computeSubtypes) throws CoreException {

		IType focusType = hierarchyBuilder.getType();
		hierarchyBuilder.hierarchy.initialize(0);

		if (computeSubtypes) {
			computeSubtypes(focusType);
		}
		computeSupertypes(focusType);
	}

	private IType[] findTypes(String pattern, IDLTKSearchScope scope)
			throws ModelException {

		// First try to use new indexing infrastructure:
		IType[] types = new ModelAccess().findTypes(pattern,
				pattern == null ? MatchRule.PREFIX : MatchRule.EXACT, 0, 0,
				scope, hierarchyBuilder.hierarchy.progressMonitor);
		if (types != null) {
			return types;
		}

		// Use JDT-like index:
		final List<IType> result = new LinkedList<IType>();
		final HandleFactory handleFactory = new HandleFactory();
		TypeNameRequestor typesCollector = new TypeNameRequestor() {
			public void acceptType(int modifiers, char[] packageName,
					char[] simpleTypeName, char[][] enclosingTypeNames,
					char[][] superTypes, String path) {

				if (superTypes != null) {
					for (int i = 0; i < superTypes.length; i++) {
						Openable openable = handleFactory.createOpenable(path,
								hierarchyBuilder.hierarchy.scope);
						ModelElement parent = openable;
						boolean binary = false;
						if (openable instanceof ISourceModule) {
							binary = ((ISourceModule) openable).isBinary();
						}
						if (enclosingTypeNames != null) {
							if (!binary) {
								for (int j = 0; j < enclosingTypeNames.length; ++j) {
									parent = new FakeType(parent, new String(
											enclosingTypeNames[j]));
								}
							} else {
								for (int j = 0; j < enclosingTypeNames.length; ++j) {
									if (parent instanceof ISourceModule) {
										parent = (ModelElement) ((ISourceModule) parent)
												.getType(new String(
														enclosingTypeNames[j]));
									} else if (parent instanceof IType) {
										parent = (ModelElement) ((IType) parent)
												.getType(new String(
														enclosingTypeNames[j]));
									}
									if (parent == null) {
										break;
									}
								}
							}
						}
						if (parent != null) {
							if (binary) {
								IType type = null;
								if (parent instanceof ISourceModule) {
									type = ((ISourceModule) parent)
											.getType(new String(simpleTypeName));
								} else if (parent instanceof IType) {
									type = ((IType) parent).getType(new String(
											simpleTypeName));
								}
								if (type != null) {
									result.add(type);
								}

							} else {
								FakeType type = new FakeType(parent,
										new String(simpleTypeName), modifiers);
								result.add(type);
							}
						}
					}
				}
			}
		};
		int matchRule = SearchPattern.R_EXACT_MATCH;
		if (pattern == null) {
			pattern = "*"; //$NON-NLS-1$
			matchRule = SearchPattern.R_PATTERN_MATCH;
		}

		SearchEngine searchEngine = new SearchEngine();
		searchEngine.searchAllTypeNames(null, 0, pattern.toCharArray(),
				matchRule, IDLTKSearchConstants.DECLARATIONS,
				hierarchyBuilder.hierarchy.scope, typesCollector,
				IDLTKSearchConstants.WAIT_UNTIL_READY_TO_SEARCH,
				hierarchyBuilder.hierarchy.progressMonitor);

		return (IType[]) result.toArray(new IType[result.size()]);
	}

	protected void computeSubtypes(IType focusType) throws CoreException {

		// Collect all inheritance information:
		final Map<String, List<String>> superTypeToExtender = new HashMap<String, List<String>>();
		final String delimiter = getDelimiterReplacementString(focusType);

		Map<String, Set<IType>> tmpCache = new HashMap<String, Set<IType>>();

		IType[] types = findTypes(null, hierarchyBuilder.hierarchy.scope);
		for (IType type : types) {
			String[] superTypes = type.getSuperClasses();
			if (superTypes != null) {
				for (int i = 0; i < superTypes.length; i++) {
					String s = superTypes[i];
					List<String> extenders = superTypeToExtender.get(s);
					if (extenders == null) {
						extenders = new LinkedList<String>();
						superTypeToExtender.put(s, extenders);
					}
					extenders.add(type.getTypeQualifiedName(delimiter));
				}
			}

			// Cache this type for further searches
			String elementName = type.getElementName();
			Set<IType> set = tmpCache.get(elementName);
			if (set == null) {
				set = new HashSet<IType>();
				tmpCache.put(elementName, set);
			}
			set.add(type);
		}

		// Rebuild temporary cache in a useful format:
		HashMap<String, IType[]> cache = new HashMap<String, IType[]>();
		Iterator<String> i = tmpCache.keySet().iterator();
		while (i.hasNext()) {
			String typeName = i.next();
			Set<IType> typeElements = tmpCache.get(typeName);
			cache.put(typeName, (IType[]) typeElements
					.toArray(new IType[typeElements.size()]));
		}

		// Create file hierarchy resolver for filtering non-included elements
		IFileHierarchyResolver fileHierarchyResolver = createFileHierarchyResolver(focusType);
		IFileHierarchyInfo hierarchyInfo = null;
		if (fileHierarchyResolver != null) {
			hierarchyInfo = fileHierarchyResolver.resolveDown(focusType
					.getSourceModule(),
					hierarchyBuilder.hierarchy.progressMonitor);
		}

		computeSubtypesFor(focusType, superTypeToExtender, cache,
				hierarchyInfo, new HashSet<IType>(), delimiter);
	}

	protected void computeSubtypesFor(IType focusType,
			Map<String, List<String>> superTypeToExtender,
			Map<String, IType[]> subTypesCache,
			IFileHierarchyInfo hierarchyInfo, Set<IType> processedTypes,
			String delimiter) throws CoreException {

		List<String> extenders = superTypeToExtender.get(focusType
				.getTypeQualifiedName(delimiter));
		if (extenders != null) {
			IType[] subTypes = searchTypes((String[]) extenders
					.toArray(new String[extenders.size()]), subTypesCache,
					hierarchyInfo);
			for (int i = 0; i < subTypes.length; i++) {
				IType subType = subTypes[i];
				hierarchyBuilder.hierarchy.addSubtype(focusType, subType);
			}

			for (int i = 0; i < subTypes.length; i++) {
				IType subType = subTypes[i];
				if (processedTypes.add(subType)) {
					computeSubtypesFor(subType, superTypeToExtender,
							subTypesCache, hierarchyInfo, processedTypes,
							delimiter);
				}
			}
		}
	}

	protected void computeSupertypes(IType focusType) throws CoreException {
		IFileHierarchyResolver fileHierarchyResolver = createFileHierarchyResolver(focusType);
		IFileHierarchyInfo hierarchyInfo = null;
		if (fileHierarchyResolver != null) {
			hierarchyInfo = fileHierarchyResolver.resolveUp(focusType
					.getSourceModule(),
					hierarchyBuilder.hierarchy.progressMonitor);
		}

		computeSupertypesFor(focusType, new HashMap<String, IType[]>(),
				hierarchyInfo, new HashSet<IType>());
	}

	protected void computeSupertypesFor(IType focusType,
			Map<String, IType[]> superTypesCache,
			IFileHierarchyInfo hierarchyInfo, Set<IType> processedTypes)
			throws CoreException {

		processedTypes.add(focusType);

		// Build superclasses hieararchy:
		String[] superClasses = focusType.getSuperClasses();
		if (superClasses != null && superClasses.length > 0) {
			IType[] searchTypes = searchTypes(superClasses, superTypesCache,
					hierarchyInfo);

			for (int i = 0; i < searchTypes.length; i++) {
				IType superclass = searchTypes[i];
				hierarchyBuilder.hierarchy.cacheSuperclass(focusType,
						superclass);
			}

			for (int i = 0; i < searchTypes.length; i++) {
				IType superclass = searchTypes[i];
				if (!processedTypes.contains(superclass)) {
					computeSupertypesFor(superclass, superTypesCache,
							hierarchyInfo, processedTypes);
				}
			}
		} else {
			if (!hierarchyBuilder.hierarchy.contains(focusType)) {
				hierarchyBuilder.hierarchy.addRootClass(focusType);
			}
		}
	}

	protected IType[] searchTypes(String[] typeNames,
			Map<String, IType[]> cache, IFileHierarchyInfo hierarchyInfo)
			throws CoreException {
		List<IType> result = new LinkedList<IType>();
		for (int i = 0; i < typeNames.length; i++) {
			String typeName = typeNames[i];
			result.addAll(Arrays.asList(searchTypes(typeName, cache,
					hierarchyInfo)));
		}
		return (IType[]) result.toArray(new IType[result.size()]);
	}

	protected IType[] searchTypes(String type, IFileHierarchyInfo hierarchyInfo)
			throws CoreException {
		return searchTypes(type, null, hierarchyInfo);
	}

	protected IType[] searchTypes(final String typeName,
			Map<String, IType[]> cache, final IFileHierarchyInfo hierarchyInfo)
			throws CoreException {
		if (cache != null && cache.containsKey(typeName)) {
			return (IType[]) cache.get(typeName);
		}

		final List<IType> result = new LinkedList<IType>();
		final List<IType> filteredTypes = new LinkedList<IType>();

		IType[] types = findTypes(typeName, hierarchyBuilder.hierarchy.scope);
		for (IType type : types) {
			String delimiter = getDelimiterReplacementString(type);
			String qualifiedName = type.getTypeQualifiedName(delimiter);
			if (!typeName.equalsIgnoreCase(qualifiedName)) {
				continue;
			}

			if (hierarchyInfo != null
					&& !hierarchyInfo.exists(type.getSourceModule())) {
				filteredTypes.add(type);
			} else {
				result.add(type);
			}
		}

		// If all results where filtered that means we could find a path to any
		// of elements.
		// In this case return all elements.
		if (result.isEmpty()) {
			result.addAll(filteredTypes);
		}

		types = (IType[]) result.toArray(new IType[result.size()]);
		if (cache != null) {
			cache.put(typeName, types);
		}
		return types;
	}

	public void resolve(Openable[] openables, HashSet<String> localTypes) {
		try {
			resolve(true);
		} catch (CoreException e) {
			if (DLTKCore.DEBUG) {
				e.printStackTrace();
			}
		}
	}

	private static IFileHierarchyResolver createFileHierarchyResolver(IType type)
			throws CoreException {
		IFileHierarchyResolver fileHierarchyResolver = null;
		IDLTKLanguageToolkit toolkit = DLTKLanguageManager
				.getLanguageToolkit(type);
		if (toolkit != null) {
			fileHierarchyResolver = DLTKLanguageManager
					.getFileHierarchyResolver(toolkit.getNatureId());
		}
		return fileHierarchyResolver;
	}

	private static ISearchPatternProcessor getSearchPatternProcessor(IType type) {
		return DLTKLanguageManager
				.getSearchPatternProcessor(DLTKLanguageManager
						.getLanguageToolkit(type));
	}

	protected String getDelimiterReplacementString(IType type) {
		ISearchPatternProcessor searchPatternProcessor = getSearchPatternProcessor(type);
		if (searchPatternProcessor != null) {
			return searchPatternProcessor.getDelimiterReplacementString();
		}
		return "::"; //$NON-NLS-N$
	}
}
