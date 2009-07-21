/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *

 *******************************************************************************/
package org.eclipse.dltk.ti;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.IType;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.core.index2.search.ModelAccess;
import org.eclipse.dltk.core.index2.search.ISearchEngine.MatchRule;
import org.eclipse.dltk.core.search.IDLTKSearchConstants;
import org.eclipse.dltk.core.search.IDLTKSearchScope;
import org.eclipse.dltk.core.search.SearchEngine;
import org.eclipse.dltk.core.search.SearchPattern;
import org.eclipse.dltk.core.search.TypeNameMatch;
import org.eclipse.dltk.core.search.TypeNameMatchRequestor;
import org.eclipse.dltk.evaluation.types.AmbiguousType;
import org.eclipse.dltk.evaluation.types.UnknownType;
import org.eclipse.dltk.ti.goals.AbstractTypeGoal;
import org.eclipse.dltk.ti.types.IEvaluatedType;

public class DLTKTypeInferenceEngine implements ITypeInferencer {

	private static final String NATURE = "nature"; //$NON-NLS-1$
	private static final String TYPE_EVALUATORS = "org.eclipse.dltk.core.typeEvaluators"; //$NON-NLS-1$
	private final static Map evaluatorsByNatures = new HashMap();

	static {
		IExtensionPoint extensionPoint = Platform.getExtensionRegistry()
				.getExtensionPoint(TYPE_EVALUATORS);
		IExtension[] ext = extensionPoint.getExtensions();
		// ArrayList resolvers = new ArrayList();
		for (int a = 0; a < ext.length; a++) {
			IConfigurationElement[] elements = ext[a]
					.getConfigurationElements();
			IConfigurationElement myElement = elements[0];
			try {
				String nature = myElement.getAttribute(NATURE);
				List list = (List) evaluatorsByNatures.get(nature);
				if (list == null) {
					list = new ArrayList();
					evaluatorsByNatures.put(nature, list);
				}
				// ITypeInferencer resolver = (ITypeInferencer) myElement
				// .createExecutableExtension("evaluator");
				// resolvers.add(resolver);
				// list.add(resolver);
				list.add(myElement);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public DLTKTypeInferenceEngine() {
	}

	private void flattenTypes(AmbiguousType type, Set typeSet) {
		IEvaluatedType[] possibleTypes = type.getPossibleTypes();
		for (int cnt = 0, max = possibleTypes.length; cnt < max; cnt++) {
			if (possibleTypes[cnt] instanceof AmbiguousType) {
				flattenTypes((AmbiguousType) possibleTypes[cnt], typeSet);
			} else {
				typeSet.add(possibleTypes[cnt]);
			}
		}
	}

	private void searchTypeDeclarations(IScriptProject dltkProject,
			String patternString, final Set typeSet) {
		try {
			int includeMask = IDLTKSearchScope.SOURCES;
			includeMask |= (IDLTKSearchScope.APPLICATION_LIBRARIES
					| IDLTKSearchScope.REFERENCED_PROJECTS | IDLTKSearchScope.SYSTEM_LIBRARIES);
			IDLTKSearchScope scope = SearchEngine.createSearchScope(
					dltkProject, includeMask);

			String typeName = ""; //$NON-NLS-1$
			if (patternString.indexOf("::") != -1) { //$NON-NLS-1$
				typeName = patternString
						.substring(patternString.indexOf("::") + 2); //$NON-NLS-1$
			} else {
				typeName = patternString;
			}

			// Search using new indexing infrastructure:
			IType[] types = new ModelAccess().findTypes(typeName,
					MatchRule.EXACT, null, scope, null);
			if (types != null) {
				typeSet.addAll(Arrays.asList(types));

			} else {
				// Fallback to old indexing engine:
				TypeNameMatchRequestor requestor = new TypeNameMatchRequestor() {
					public void acceptTypeNameMatch(TypeNameMatch match) {
						typeSet.add(match.getType());
					}
				};
				SearchEngine engine = new SearchEngine();
				engine.searchAllTypeNames(null, 0, typeName.toCharArray(),
						SearchPattern.R_EXACT_MATCH, IDLTKSearchConstants.TYPE,
						scope, requestor,
						IDLTKSearchConstants.WAIT_UNTIL_READY_TO_SEARCH, null);
			}
		} catch (CoreException cxcn) {
			cxcn.printStackTrace();
		}
	}

	private void collectSuperClasses(IScriptProject project, String typeName,
			Set superClassSet) {

		Set typeSet = new HashSet();
		searchTypeDeclarations(project, typeName, typeSet);

		if (typeSet.isEmpty() != true) {
			IType itype;
			String[] superClasses;
			for (Iterator typeIter = typeSet.iterator(); typeIter.hasNext();) {
				itype = (IType) typeIter.next();
				if (itype.exists()) {
					try {
						superClasses = itype.getSuperClasses();
						if (superClasses != null) {
							for (int cnt = 0, max = superClasses.length; cnt < max; cnt++) {
								if (!superClassSet.contains(superClasses[cnt])) {
									superClassSet.add(superClasses[cnt]);

									collectSuperClasses(project,
											superClasses[cnt], superClassSet);
								}
							}
						}
					} catch (ModelException mxcn) {
						mxcn.printStackTrace();
					}
				}
			}
		}
	}

	private void reduceTypes(BasicContext context, Set typeSet) {
		IEvaluatedType type;
		Set superClassSet = new HashSet();
		for (Iterator iter = typeSet.iterator(); iter.hasNext();) {
			type = (IEvaluatedType) iter.next();
			collectSuperClasses(context.getSourceModule().getScriptProject(),
					type.getTypeName(), superClassSet);
		}

		if (!superClassSet.isEmpty()) {
			for (Iterator iter = typeSet.iterator(); iter.hasNext();) {
				type = (IEvaluatedType) iter.next();
				if (superClassSet.contains(type.getTypeName())) {
					iter.remove();
				}
			}
		}
	}

	private static ThreadLocal<List<AbstractTypeGoal>> goals = new ThreadLocal<List<AbstractTypeGoal>>() {
		@Override
		protected List<AbstractTypeGoal> initialValue() {
			return new ArrayList<AbstractTypeGoal>();
		}
	};

	public IEvaluatedType evaluateType(AbstractTypeGoal goal, int time) {
		String nature = goal.getContext().getLangNature();
		List list = (List) evaluatorsByNatures.get(nature);
		if (list != null) {
			final List<AbstractTypeGoal> threadGoals = goals.get();
			if (threadGoals.size() > 32) {
				return null;
			}
			threadGoals.add(goal);
			try {
				return evaluateType(goal, time, list);
			} finally {
				threadGoals.remove(threadGoals.size() - 1);
			}
		}
		return null;
	}

	private IEvaluatedType evaluateType(AbstractTypeGoal goal, int time,
			List list) {
		Set typeSet = new HashSet();
		for (Iterator iterator = list.iterator(); iterator.hasNext();) {
			IConfigurationElement element = (IConfigurationElement) iterator
					.next();
			ITypeInferencer ti;
			try {
				ti = (ITypeInferencer) element
						.createExecutableExtension("evaluator"); //$NON-NLS-1$
			} catch (CoreException e) {
				e.printStackTrace();
				continue;
			}
			// ITypeInferencer ti = (ITypeInferencer)
			// iterator.next();
			IEvaluatedType type = ti.evaluateType(goal, time);
			if (type != null && !(type instanceof UnknownType)) {
				if (type instanceof AmbiguousType) {
					flattenTypes((AmbiguousType) type, typeSet);
				} else {
					typeSet.add(type);
				}
			}
		}
		if ((typeSet.size() > 1) && (goal.getContext() instanceof BasicContext)) {
			reduceTypes((BasicContext) goal.getContext(), typeSet);
		}

		if (typeSet.size() == 1) {
			return (IEvaluatedType) typeSet.iterator().next();
		} else if (typeSet.size() > 1) {
			return new AmbiguousType((IEvaluatedType[]) typeSet
					.toArray(new IEvaluatedType[typeSet.size()]));
		} else {
			return null;
		}
	}

}
