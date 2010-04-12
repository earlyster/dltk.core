/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 *******************************************************************************/
package org.eclipse.dltk.internal.core;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.PriorityClassDLTKExtensionManager;
import org.eclipse.dltk.core.search.matching.IMatchLocator;
import org.eclipse.dltk.core.search.matching.MatchLocator;

public class InternalDLTKLanguageManager {
	private final static String LANGUAGE_EXTPOINT = DLTKCore.PLUGIN_ID
			+ ".language"; //$NON-NLS-1$
	private final static String SOURCE_ELEMENT_PARSERS_EXTPOINT = DLTKCore.PLUGIN_ID
			+ ".sourceElementParsers"; //$NON-NLS-1$
	private final static String BINARY_ELEMENT_PARSERS_EXTPOINT = DLTKCore.PLUGIN_ID
			+ ".binaryElementParsers"; //$NON-NLS-1$
	private final static String SOURCE_PARSERS_EXTPOINT = DLTKCore.PLUGIN_ID
			+ ".sourceParsers"; //$NON-NLS-1$
	private final static String PROBLEM_FACTORY_EXTPOINT = DLTKCore.PLUGIN_ID
			+ ".problemFactory"; //$NON-NLS-1$
	private final static String COMPLETION_ENGINE_EXTPOINT = DLTKCore.PLUGIN_ID
			+ ".completionEngine"; //$NON-NLS-1$
	private final static String SELECTION_ENGINE_EXTPOINT = DLTKCore.PLUGIN_ID
			+ ".selectionEngine"; //$NON-NLS-1$
	private final static String SEARCH_EXTPOINT = DLTKCore.PLUGIN_ID
			+ ".search"; //$NON-NLS-1$
	private final static String CALLHIERARCHY_EXTPOINT = DLTKCore.PLUGIN_ID
			+ ".callHierarchy"; //$NON-NLS-1$
	private final static String FILE_HIERARCHY_RESOLVER_EXTPOINT = DLTKCore.PLUGIN_ID
			+ ".fileHierarchyResolvers";//$NON-NLS-1$

	private static PriorityClassDLTKExtensionManager languageToolkitsManager = new PriorityClassDLTKExtensionManager(
			LANGUAGE_EXTPOINT);

	// Inner managers
	private static PriorityClassDLTKExtensionManager sourceElementParsersManager = new NewInstanceClassBasedDLTKExtensionManager(
			SOURCE_ELEMENT_PARSERS_EXTPOINT);
	private static PriorityClassDLTKExtensionManager binaryElementParsersManager = new NewInstanceClassBasedDLTKExtensionManager(
			BINARY_ELEMENT_PARSERS_EXTPOINT);
	private static PriorityClassDLTKExtensionManager problemFactoryManager = new PriorityClassDLTKExtensionManager(
			PROBLEM_FACTORY_EXTPOINT);

	private static PriorityClassDLTKExtensionManager selectionEngineManager = new NewInstanceClassBasedDLTKExtensionManager(
			SELECTION_ENGINE_EXTPOINT);
	private static PriorityClassDLTKExtensionManager completionEngineManager = new NewInstanceClassBasedDLTKExtensionManager(
			COMPLETION_ENGINE_EXTPOINT, true);
	private static PriorityClassDLTKExtensionManager sourceParsersManager = new NewInstanceClassBasedDLTKExtensionManager(
			SOURCE_PARSERS_EXTPOINT);

	private static PriorityClassDLTKExtensionManager searchManager = new PriorityClassDLTKExtensionManager(
			SEARCH_EXTPOINT) {
		@Override
		protected boolean isValidConfigurationElement(
				IConfigurationElement element) {
			return "searchFactory".equals(element.getName())
					|| "seachFactory".equals(element.getName());
			// XXX there was a typo in schema
		}
	};
	private static PriorityClassDLTKExtensionManager callHierarchyManager = new PriorityClassDLTKExtensionManager(
			CALLHIERARCHY_EXTPOINT);
	private static PriorityClassDLTKExtensionManager fileHierarchyResolversManager = new PriorityClassDLTKExtensionManager(
			FILE_HIERARCHY_RESOLVER_EXTPOINT);

	public static PriorityClassDLTKExtensionManager getSourceElementParsersManager() {
		return sourceElementParsersManager;
	}

	public static PriorityClassDLTKExtensionManager getBinaryElementParsersManager() {
		return binaryElementParsersManager;
	}

	public static PriorityClassDLTKExtensionManager getProblemFactoryManager() {
		return problemFactoryManager;
	}

	public static PriorityClassDLTKExtensionManager getSelectionEngineManager() {
		return selectionEngineManager;
	}

	public static PriorityClassDLTKExtensionManager getCompletionEngineManager() {
		return completionEngineManager;
	}

	public static PriorityClassDLTKExtensionManager getSourceParsersManager() {
		return sourceParsersManager;
	}

	public static PriorityClassDLTKExtensionManager getSearchManager() {
		return searchManager;
	}

	public static PriorityClassDLTKExtensionManager getCallHierarchyManager() {
		return callHierarchyManager;
	}

	public static PriorityClassDLTKExtensionManager getFileHierarchyResolversManager() {
		return fileHierarchyResolversManager;
	}

	public static PriorityClassDLTKExtensionManager getLanguageToolkitsManager() {
		return languageToolkitsManager;
	}

	private static final PriorityClassDLTKExtensionManager matchLocatorManager = new PriorityClassDLTKExtensionManager(
			SEARCH_EXTPOINT) {
		@Override
		protected boolean isValidConfigurationElement(
				IConfigurationElement element) {
			return "matchLocator".equals(element.getName());
		}

		@Override
		public Object getInitObject(ElementInfo ext) {
			try {
				if (ext != null)
					return createObject(ext.config);
			} catch (CoreException e) {
				if (DLTKCore.DEBUG)
					e.printStackTrace();
			}
			return null;
		}
	};

	/**
	 * @param natureID
	 * @return
	 */
	public static IMatchLocator createMatchLocator(String natureID) {
		final IMatchLocator locator = (IMatchLocator) matchLocatorManager
				.getObject(natureID);
		if (locator != null) {
			return locator;
		}
		return new MatchLocator();
	}

}
