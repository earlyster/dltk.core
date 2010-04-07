/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.eclipse.dltk.internal.launching;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IPath;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IAccessRule;
import org.eclipse.dltk.core.IBuildpathAttribute;
import org.eclipse.dltk.core.IBuildpathContainer;
import org.eclipse.dltk.core.IBuildpathContainerExtension;
import org.eclipse.dltk.core.IBuildpathEntry;
import org.eclipse.dltk.core.IBuiltinModuleProvider;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.internal.core.BuildpathEntry;
import org.eclipse.dltk.launching.DLTKInterpreterManager;
import org.eclipse.dltk.launching.IInterpreterContainerExtension;
import org.eclipse.dltk.launching.IInterpreterContainerExtension2;
import org.eclipse.dltk.launching.IInterpreterInstall;
import org.eclipse.dltk.launching.IInterpreterInstallChangedListener;
import org.eclipse.dltk.launching.LaunchingMessages;
import org.eclipse.dltk.launching.LibraryLocation;
import org.eclipse.dltk.launching.PropertyChangeEvent;
import org.eclipse.dltk.launching.ScriptRuntime;
import org.eclipse.osgi.util.NLS;

/**
 * Interpreter Container - resolves a buildpath container to an interpreter
 */
public class InterpreterContainer implements IBuildpathContainer,
		IBuildpathContainerExtension {
	/**
	 * Corresponding interpreter
	 */
	private IInterpreterInstall fInterpreterInstall = null;
	/**
	 * Container path used to resolve to this interpreter
	 */
	private IPath fPath = null;

	private IScriptProject fProject;
	/**
	 * Cache of buildpath entries per Interpreter install. Cleared when a
	 * Interpreter changes.
	 */
	private static final Map<IInterpreterInstall, IBuildpathEntry[]> fgBuildpathEntries = new HashMap<IInterpreterInstall, IBuildpathEntry[]>(
			10);

	private static ChangeListener changeListener = null;

	private static class ChangeListener implements
			IInterpreterInstallChangedListener {

		public void defaultInterpreterInstallChanged(
				IInterpreterInstall previous, IInterpreterInstall current) {
		}

		public void interpreterChanged(PropertyChangeEvent event) {
			if (event.getSource() != null
					&& event.getSource() instanceof IInterpreterInstall) {
				remove((IInterpreterInstall) event.getSource());
			}
		}

		public void interpreterAdded(IInterpreterInstall newInterpreter) {
		}

		public void interpreterRemoved(IInterpreterInstall removedInterpreter) {
			remove(removedInterpreter);
		}

		private void remove(IInterpreterInstall interpreter) {
			synchronized (fgBuildpathEntries) {
				fgBuildpathEntries.remove(interpreter);
			}
		}
	}

	/**
	 * Returns the buildpath entries associated with the given interpreter.
	 * 
	 * @param interpreter
	 * @return buildpath entries
	 */
	public static IBuildpathEntry[] getBuildpathEntries(
			IInterpreterInstall interpreter) {
		IBuildpathEntry[] entries;
		synchronized (fgBuildpathEntries) {
			if (changeListener == null) {
				// add a listener to clear cached value when an interpreter
				// changes or is removed
				changeListener = new ChangeListener();
				ScriptRuntime
						.addInterpreterInstallChangedListener(changeListener);
			}
			entries = fgBuildpathEntries.get(interpreter);
		}
		if (entries == null) {
			// TODO don't call it simultaneously for the same interpreter
			entries = computeBuildpathEntries(interpreter);
			synchronized (fgBuildpathEntries) {
				fgBuildpathEntries.put(interpreter, entries);
			}
		}
		return entries;
	}

	/**
	 * Computes the buildpath entries associated with a interpreter - one entry
	 * per library.
	 * 
	 * @param interpreter
	 * @return buildpath entries
	 */
	private static IBuildpathEntry[] computeBuildpathEntries(
			IInterpreterInstall interpreter) {
		LibraryLocation[] libs = ScriptRuntime.getLibraryLocations(interpreter);
		List<IBuildpathEntry> entries = new ArrayList<IBuildpathEntry>(
				libs.length);
		Set<IPath> rawEntries = new HashSet<IPath>(libs.length);
		for (int i = 0; i < libs.length; i++) {
			IPath entryPath = libs[i].getLibraryPath();

			if (!entryPath.isEmpty()) {
				if (rawEntries.contains(entryPath))
					continue;

				IBuildpathAttribute[] attributes = new IBuildpathAttribute[0];
				ArrayList<IPath> excluded = new ArrayList<IPath>(); // paths to
				// exclude
				for (int j = 0; j < libs.length; j++) {
					IPath otherPath = libs[j].getLibraryPath();
					if (otherPath.isEmpty())
						continue;

					// compare, if it contains some another
					if (entryPath.isPrefixOf(otherPath)
							&& !otherPath.equals(entryPath)) {
						IPath pattern = otherPath.setDevice(null)
								.removeFirstSegments(entryPath.segmentCount())
								.append("*"); //$NON-NLS-1$
						if (!excluded.contains(pattern)) {
							excluded.add(pattern);
						}
					}
				}

				entries.add(DLTKCore.newLibraryEntry(entryPath,
						IAccessRule.EMPTY_RULES, attributes,
						BuildpathEntry.INCLUDE_ALL, excluded
								.toArray(new IPath[excluded.size()]), false,
						true));
				rawEntries.add(entryPath);
			}
		}
		// Add builtin entry.
		{
			entries.add(DLTKCore.newBuiltinEntry(
					IBuildpathEntry.BUILTIN_EXTERNAL_ENTRY.append(interpreter
							.getInstallLocation().toOSString()),
					IAccessRule.EMPTY_RULES, new IBuildpathAttribute[0],
					BuildpathEntry.INCLUDE_ALL, BuildpathEntry.EXCLUDE_NONE,
					false, true));
		}

		// Preprocess entries using extension
		IInterpreterContainerExtension extension = DLTKInterpreterManager
				.getInterpreterContainerExtensions(interpreter.getNatureId());
		if (extension instanceof IInterpreterContainerExtension2) {
			((IInterpreterContainerExtension2) extension).preProcessEntries(
					interpreter, entries);
		}
		return entries.toArray(new IBuildpathEntry[entries.size()]);
	}

	/**
	 * Constructs a interpreter buildpath container on the given interpreter
	 * install
	 * 
	 * @param interpreter
	 *            Interpreter install - cannot be <code>null</code>
	 * @param path
	 *            container path used to resolve this interpreter
	 */
	public InterpreterContainer(IInterpreterInstall interpreter, IPath path,
			IScriptProject project) {
		fInterpreterInstall = interpreter;
		fPath = path;
		fProject = project;
	}

	/**
	 * @see IBuildpathContainer#getBuildpathEntries(IScriptProject)
	 */
	public IBuildpathEntry[] getBuildpathEntries() {
		IBuildpathEntry[] buildpathEntries = getBuildpathEntries(fInterpreterInstall);
		List<IBuildpathEntry> entries = new ArrayList<IBuildpathEntry>();
		entries.addAll(Arrays.asList(buildpathEntries));
		// Use custom per project interpreter entries.
		IInterpreterContainerExtension extension = DLTKInterpreterManager
				.getInterpreterContainerExtensions(fProject);
		if (extension != null) {
			extension.processEntres(fProject, entries);
		}
		return entries.toArray(new IBuildpathEntry[entries.size()]);
	}

	/**
	 * @see IBuildpathContainer#getDescription()
	 */
	public String getDescription() {
		String tag = fInterpreterInstall.getName();
		return NLS
				.bind(
						LaunchingMessages.InterpreterEnvironmentContainer_InterpreterEnvironment_System_Library_1,
						tag);
	}

	/**
	 * @see IBuildpathContainer#getKind()
	 */
	public int getKind() {
		return IBuildpathContainer.K_DEFAULT_SYSTEM;
	}

	/**
	 * @see IBuildpathContainer#getPath()
	 */
	public IPath getPath() {
		return fPath;
	}

	public IBuiltinModuleProvider getBuiltinProvider() {
		return fInterpreterInstall;
	}
}
