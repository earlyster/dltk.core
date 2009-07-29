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
import org.eclipse.dltk.core.ISearchFactory;
import org.eclipse.dltk.core.ISearchPatternProcessor;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.IType;
import org.eclipse.dltk.core.search.IDLTKSearchConstants;
import org.eclipse.dltk.core.search.SearchEngine;
import org.eclipse.dltk.core.search.SearchPattern;
import org.eclipse.dltk.core.search.TypeNameRequestor;
import org.eclipse.dltk.internal.core.ModelElement;
import org.eclipse.dltk.internal.core.Openable;
import org.eclipse.dltk.internal.core.util.HandleFactory;

public class HierarchyResolver {

	private HierarchyBuilder hierarchyBuilder;
	private SearchEngine engine;

	public HierarchyResolver(HierarchyBuilder hierarchy) {
		this.hierarchyBuilder = hierarchy;
		this.engine = new SearchEngine();
	}

	public void resolve(boolean computeSubtypes) throws CoreException {

		IType focusType = hierarchyBuilder.getType();

		hierarchyBuilder.hierarchy.initialize(0);

		if (computeSubtypes) {
			computeSubtypes(focusType);
		}

		computeSupertypes(focusType);
	}

	protected void computeSubtypes(IType focusType) throws CoreException {

		// Collect all inheritance information:
		final Map superTypeToExtender = new HashMap();
		final HandleFactory handleFactory = new HandleFactory();
		final String delimiter = getDelimiterReplacementString(focusType);
		final Map<String, Set<IType>> tmpCache = new HashMap<String, Set<IType>>();

		TypeNameRequestor typesCollector = new TypeNameRequestor() {
			public void acceptType(int modifiers, char[] packageName,
					char[] simpleTypeName, char[][] enclosingTypeNames,
					char[][] superTypes, String path) {

				if (superTypes != null) {
					for (int i = 0; i < superTypes.length; i++) {
						final String s = new String(superTypes[i]);
						List extenders = (List) superTypeToExtender.get(s);
						if (extenders == null) {
							extenders = new LinkedList();
							superTypeToExtender.put(s, extenders);
						}

						Openable openable = handleFactory.createOpenable(path,
								hierarchyBuilder.hierarchy.scope);
						ModelElement parent = openable;
						if (enclosingTypeNames != null) {
							for (int j = 0; j < enclosingTypeNames.length; ++j) {
								parent = new FakeType(parent, new String(
										enclosingTypeNames[j]));
							}
						}
						FakeType type = new FakeType(parent, new String(
								simpleTypeName), modifiers);
						extenders.add(new String(type
								.getTypeQualifiedName(delimiter)));

						// Cache this type for further searches
						String elementName = type.getElementName();
						Set<IType> set = tmpCache.get(elementName);
						if (set == null) {
							set = new HashSet<IType>();
							tmpCache.put(elementName, set);
						}
						set.add(type);
					}
				}
			}
		};

		engine.searchAllTypeNames(null, 0, "*".toCharArray(),
				SearchPattern.R_PATTERN_MATCH,
				IDLTKSearchConstants.DECLARATIONS,
				hierarchyBuilder.hierarchy.scope, typesCollector,
				IDLTKSearchConstants.WAIT_UNTIL_READY_TO_SEARCH,
				hierarchyBuilder.hierarchy.progressMonitor);

		IFileHierarchyResolver fileHierarchyResolver = createFileHierarchyResolver(focusType);
		IFileHierarchyInfo hierarchyInfo = null;
		if (fileHierarchyResolver != null) {
			hierarchyInfo = fileHierarchyResolver.resolveDown(focusType
					.getSourceModule(),
					hierarchyBuilder.hierarchy.progressMonitor);
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

		computeSubtypesFor(focusType, superTypeToExtender, cache,
				hierarchyInfo, new HashSet(), delimiter);
	}

	protected void computeSubtypesFor(IType focusType, Map superTypeToExtender,
			Map subTypesCache, IFileHierarchyInfo hierarchyInfo,
			Set processedTypes, String delimiter) throws CoreException {

		List extenders = (List) superTypeToExtender.get(focusType
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

		computeSupertypesFor(focusType, new HashMap(), hierarchyInfo,
				new HashSet());
	}

	protected void computeSupertypesFor(IType focusType, Map superTypesCache,
			IFileHierarchyInfo hierarchyInfo, Set processedTypes)
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

	protected IType[] searchTypes(String[] typeNames, Map cache,
			IFileHierarchyInfo hierarchyInfo) throws CoreException {
		List result = new LinkedList();
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

	protected IType[] searchTypes(final String typeName, Map cache,
			final IFileHierarchyInfo hierarchyInfo) throws CoreException {
		if (cache != null && cache.containsKey(typeName)) {
			return (IType[]) cache.get(typeName);
		}

		final List result = new LinkedList();
		final List filteredTypes = new LinkedList();
		final HandleFactory handleFactory = new HandleFactory();

		TypeNameRequestor typesCollector = new TypeNameRequestor() {

			public void acceptType(int modifiers, char[] packageName,
					char[] simpleTypeName, char[][] enclosingTypeNames,
					char[][] superTypes, String path) {

				Openable openable = handleFactory.createOpenable(path,
						hierarchyBuilder.hierarchy.scope);
				ModelElement parent = openable;
				ISourceModule sourceModule = (ISourceModule) openable;

				if (enclosingTypeNames != null) {
					for (int j = 0; j < enclosingTypeNames.length; ++j) {
						parent = new FakeType(parent, new String(
								enclosingTypeNames[j]));
					}
				}
				FakeType type = new FakeType(parent,
						new String(simpleTypeName), modifiers);

				String delimiter = getDelimiterReplacementString(type);
				String qualifiedName = type.getTypeQualifiedName(delimiter);
				if (!typeName.equalsIgnoreCase(qualifiedName)) {
					return;
				}

				if (hierarchyInfo != null
						&& !hierarchyInfo.exists(sourceModule)) {
					filteredTypes.add(type);
					return;
				}
				result.add(type);
			}
		};

		engine.searchAllTypeNames(null, 0, typeName.toCharArray(),
				SearchPattern.R_PATTERN_MATCH,
				IDLTKSearchConstants.DECLARATIONS,
				hierarchyBuilder.hierarchy.scope, typesCollector,
				IDLTKSearchConstants.WAIT_UNTIL_READY_TO_SEARCH,
				hierarchyBuilder.hierarchy.progressMonitor);

		// If all results where filtered that means we could find a path to any
		// of elements.
		// In this case return all elements.
		if (result.isEmpty()) {
			result.addAll(filteredTypes);
		}

		IType[] types = (IType[]) result.toArray(new IType[result.size()]);
		if (cache != null) {
			cache.put(typeName, types);
		}
		return types;
	}

	public void resolve(Openable[] openables, HashSet localTypes) {
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
		IDLTKLanguageToolkit toolkit = DLTKLanguageManager
				.getLanguageToolkit(type);
		if (toolkit != null) {
			ISearchFactory factory = DLTKLanguageManager
					.getSearchFactory(toolkit.getNatureId());
			if (factory != null) {
				return factory.createSearchPatternProcessor();
			}
		}
		return null;
	}

	protected String getDelimiterReplacementString(IType type) {
		ISearchPatternProcessor searchPatternProcessor = getSearchPatternProcessor(type);
		if (searchPatternProcessor != null) {
			return searchPatternProcessor.getDelimiterReplacementString();
		}
		return "::"; //$NON-NLS-N$
	}
}
