/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.eclipse.dltk.internal.core.builder;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.DLTKLanguageManager;
import org.eclipse.dltk.core.IBuildpathEntry;
import org.eclipse.dltk.core.IDLTKLanguageToolkit;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.IModelElementVisitor;
import org.eclipse.dltk.core.IModelMarker;
import org.eclipse.dltk.core.IProjectFragment;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.core.builder.IScriptBuilder;
import org.eclipse.dltk.core.environment.EnvironmentPathUtils;
import org.eclipse.dltk.internal.core.BuildpathEntry;
import org.eclipse.dltk.internal.core.BuiltinProjectFragment;
import org.eclipse.dltk.internal.core.BuiltinSourceModule;
import org.eclipse.dltk.internal.core.ExternalProjectFragment;
import org.eclipse.dltk.internal.core.ExternalSourceModule;
import org.eclipse.dltk.internal.core.ModelManager;
import org.eclipse.dltk.internal.core.ScriptProject;
import org.eclipse.osgi.util.NLS;

public class ScriptBuilder extends IncrementalProjectBuilder {
	public static final boolean DEBUG = DLTKCore.DEBUG_SCRIPT_BUILDER;
	public static final boolean TRACE = DLTKCore.TRACE_SCRIPT_BUILDER;

	public IProject currentProject = null;
	ScriptProject scriptProject = null;
	State lastState;

	/**
	 * Last build following resource count.
	 */
	public long lastBuildResources = 0;
	public long lastBuildSourceFiles = 0;

	static class ResourceVisitor implements IResourceDeltaVisitor,
			IResourceVisitor {
		private Set resources;
		private IProgressMonitor monitor;

		public ResourceVisitor(Set resources, IProgressMonitor monitor) {
			this.resources = resources;
			this.monitor = monitor;
		}

		public boolean visit(IResourceDelta delta) throws CoreException {
			// monitor.worked(1);
			if (monitor.isCanceled()) {
				return false;
			}
			IResource resource = delta.getResource();
			switch (delta.getKind()) {
			case IResourceDelta.ADDED:
			case IResourceDelta.CHANGED:
				if (!this.resources.contains(resource)
						&& resource.getType() == IResource.FILE) {
					resources.add(resource);
					return false;
				}
				break;
			}
			return true;
		}

		public boolean visit(IResource resource) {
			// monitor.worked(1);
			if (monitor.isCanceled()) {
				return false;
			}
			if (!this.resources.contains(resource)
					&& resource.getType() == IResource.FILE) {
				resources.add(resource);
				return false;
			}
			return true;
		}
	}

	class ExternalModuleVisitor implements IModelElementVisitor {
		private Set elements;
		private IProgressMonitor monitor;
		private Set fragments = new HashSet();

		public ExternalModuleVisitor(Set elements, IProgressMonitor monitor) {
			this.elements = elements;
			this.monitor = monitor;
		}

		/**
		 * Visit only external source modules, witch we aren't builded yet.
		 */
		public boolean visit(IModelElement element) {
			// monitor.worked(1);
			if (monitor.isCanceled()) {
				return false;
			}
			if (element.getElementType() == IModelElement.PROJECT_FRAGMENT) {
				if (!(element instanceof ExternalProjectFragment)
						&& !(element instanceof BuiltinProjectFragment)) {
					return false;
				}
				IProjectFragment fragment = (IProjectFragment) element;

				fragments.add(fragment.getPath());

				String localPath = EnvironmentPathUtils.getLocalPath(
						fragment.getPath()).toString();
				if (!localPath.startsWith("#")) { //$NON-NLS-1$
					this.monitor
							.subTask(Messages.ScriptBuilder_Looking_into_folder
									+ localPath);
				}
				if (lastState.externalFolderLocations.contains(fragment
						.getPath())) {
					return false;
				} else {
					lastState.externalFolderLocations.add(fragment.getPath());
				}
			}
			if (element.getElementType() == IModelElement.SOURCE_MODULE
					&& (element instanceof ExternalSourceModule || element instanceof BuiltinSourceModule)) {
				if (!elements.contains(element)) {
					elements.add(element);
				}
				return false; // do not enter into source module content.
			}
			return true;
		}

		public Set getExternalFolders() {
			return this.fragments;
		}
	}

	/**
	 * Hook allowing to initialize some static state before a complete build
	 * iteration. This hook is invoked during PRE_AUTO_BUILD notification
	 */
	public static void buildStarting() {
		// build is about to start
	}

	/**
	 * Hook allowing to reset some static state after a complete build
	 * iteration. This hook is invoked during POST_AUTO_BUILD notification
	 */
	public static void buildFinished() {
		if (DLTKCore.DEBUG)
			System.out.println("build finished"); //$NON-NLS-1$
	}

	protected IProject[] build(int kind, Map args, IProgressMonitor monitor)
			throws CoreException {
		lastBuildResources = 0;
		lastBuildSourceFiles = 0;
		final long start = TRACE ? System.currentTimeMillis() : 0;
		if (monitor == null) {
			monitor = new NullProgressMonitor();
		}
		this.currentProject = getProject();

		if (!DLTKLanguageManager.hasScriptNature(this.currentProject)) {
			return null;
		}
		this.scriptProject = (ScriptProject) DLTKCore.create(currentProject);

		if (currentProject == null || !currentProject.isAccessible())
			return new IProject[0];

		if (kind == FULL_BUILD) {
			fullBuild(monitor);
		} else {
			if ((this.lastState = getLastState(currentProject, monitor)) == null) {
				if (DEBUG)
					System.out
							.println("Performing full build since last saved state was not found"); //$NON-NLS-1$
				fullBuild(monitor);
			} else {
				IResourceDelta delta = getDelta(getProject());
				if (delta == null) {
					fullBuild(monitor);
				} else {
					incrementalBuild(delta, monitor);
				}
			}
		}
		IProject[] requiredProjects = getRequiredProjects(true);
		if (DEBUG)
			System.out.println("Finished build of " + currentProject.getName() //$NON-NLS-1$
					+ " @ " + new Date(System.currentTimeMillis())); //$NON-NLS-1$
		if (TRACE) {
			System.out
					.println("-----SCRIPT-BUILDER-INFORMATION-TRACE----------------------------"); //$NON-NLS-1$
			System.out.println("Finished build of project:" //$NON-NLS-1$
					+ currentProject.getName() + "\n" //$NON-NLS-1$
					+ "Building time:" //$NON-NLS-1$
					+ Long.toString(System.currentTimeMillis() - start) + "\n" //$NON-NLS-1$
					+ "Resources count:" //$NON-NLS-1$
					+ this.lastBuildResources + "\n" //$NON-NLS-1$
					+ "Sources count:" //$NON-NLS-1$
					+ this.lastBuildSourceFiles + "\n" //$NON-NLS-1$
					+ "Build type:" //$NON-NLS-1$
					+ (kind == FULL_BUILD ? "Full build" //$NON-NLS-1$
							: "Incremental build")); //$NON-NLS-1$
			System.out
					.println("-----------------------------------------------------------------"); //$NON-NLS-1$
		}
		monitor.done();
		return requiredProjects;
	}

	protected void clean(IProgressMonitor monitor) throws CoreException {
		long start = 0;
		if (TRACE) {
			start = System.currentTimeMillis();
		}

		this.currentProject = getProject();

		if (!DLTKLanguageManager.hasScriptNature(this.currentProject)) {
			return;
		}
		this.scriptProject = (ScriptProject) DLTKCore.create(currentProject);

		if (currentProject == null || !currentProject.isAccessible())
			return;

		try {
			monitor.beginTask(MessageFormat.format(
					Messages.ScriptBuilder_cleaningScriptsIn,
					new Object[] { currentProject.getName() }), 66);
			if (monitor.isCanceled()) {
				return;
			}

			IScriptBuilder[] builders = getScriptBuilders();

			initializeBuilders(builders);
			if (builders != null) {
				for (int k = 0; k < builders.length; k++) {
					IProgressMonitor sub = new SubProgressMonitor(monitor, 1);
					builders[k].clean(scriptProject, sub);

					if (monitor.isCanceled()) {
						break;
					}
				}
			}
			resetBuilders(builders);
		} catch (CoreException e) {
			if (DLTKCore.DEBUG) {
				e.printStackTrace();
			}
		}

		if (TRACE) {
			System.out
					.println("-----SCRIPT-BUILDER-INFORMATION-TRACE----------------------------"); //$NON-NLS-1$
			System.out.println("Finished clean of project:" //$NON-NLS-1$
					+ currentProject.getName() + "\n" //$NON-NLS-1$
					+ "Building time:" //$NON-NLS-1$
					+ Long.toString(System.currentTimeMillis() - start));
			System.out
					.println("-----------------------------------------------------------------"); //$NON-NLS-1$
		}
		monitor.done();
	}

	private IProject[] getRequiredProjects(boolean includeBinaryPrerequisites) {
		if (scriptProject == null)
			return new IProject[0];
		IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
		ArrayList projects = new ArrayList();
		try {
			IBuildpathEntry[] entries = scriptProject
					.getExpandedBuildpath(true);
			for (int i = 0, l = entries.length; i < l; i++) {
				IBuildpathEntry entry = entries[i];
				IPath path = entry.getPath();
				IProject p = null;
				switch (entry.getEntryKind()) {
				case IBuildpathEntry.BPE_PROJECT:
					p = workspaceRoot.getProject(path.lastSegment()); // missing
					// projects
					// are
					// considered
					// too
					if (((BuildpathEntry) entry).isOptional()
							&& !ScriptProject.hasScriptNature(p)) // except if
						// entry is
						// optional
						p = null;
					break;
				case IBuildpathEntry.BPE_LIBRARY:
					if (includeBinaryPrerequisites && path.segmentCount() > 1) {
						// some binary resources on the class path can come from
						// projects that are not included in the project
						// references
						IResource resource = workspaceRoot.findMember(path
								.segment(0));
						if (resource instanceof IProject)
							p = (IProject) resource;
					}
				}
				if (p != null && !projects.contains(p))
					projects.add(p);
			}
		} catch (ModelException e) {
			return new IProject[0];
		}
		IProject[] result = new IProject[projects.size()];
		projects.toArray(result);
		return result;
	}

	public State getLastState(IProject project, IProgressMonitor monitor) {
		return (State) ModelManager.getModelManager().getLastBuiltState(
				project, monitor);
	}

	private State clearLastState() {
		State state = new State(this);
		State prevState = (State) ModelManager.getModelManager()
				.getLastBuiltState(currentProject, null);
		if (prevState != null) {
			if (prevState.noCleanExternalFolders) {
				state.externalFolderLocations = prevState.externalFolderLocations;
				return state;
			}
		}
		ModelManager.getModelManager().setLastBuiltState(currentProject, null);
		return state;
	}

	private static final int WORK_RESOURCES = 50;
	private static final int WORK_EXTERNAL = 100;
	private static final int WORK_SOURCES = 100;
	private static final int WORK_BUILD = 750;

	private static final String NONAME = ""; //$NON-NLS-1$

	protected void fullBuild(final IProgressMonitor monitor) {

		State newState = clearLastState();
		this.lastState = newState;
		IScriptBuilder[] builders = null;
		try {
			monitor.setTaskName(NLS.bind(
					Messages.ScriptBuilder_buildingScriptsIn, currentProject
							.getName()));
			monitor.beginTask(NONAME, WORK_RESOURCES + WORK_EXTERNAL
					+ WORK_SOURCES + WORK_BUILD);
			Set resources = getResourcesFrom(currentProject, monitor,
					WORK_RESOURCES);
			if (monitor.isCanceled()) {
				return;
			}
			Set elements = getExternalElementsFrom(scriptProject, monitor,
					WORK_EXTERNAL);
			Set externalFolders = new HashSet();
			externalFolders.addAll(this.lastState.externalFolderLocations);
			if (monitor.isCanceled()) {
				return;
			}
			// Project external resources should also be added into list. Only
			// on full build we need to manage this.
			// Call builders for resources.
			int totalFiles = resources.size() + elements.size();
			if (totalFiles == 0)
				totalFiles = 1;

			builders = getScriptBuilders();

			initializeBuilders(builders);

			List realResources = new ArrayList();

			List relements = locateSourceModule(resources, monitor,
					WORK_SOURCES, resources, realResources);

			if (monitor.isCanceled()) {
				return;
			}
			int resourceTicks = WORK_BUILD
					* (resources.size() - relements.size()) / totalFiles;
			resourceTicks = Math.min(resourceTicks, WORK_BUILD / 4);

			List els = new ArrayList();

			els.addAll(relements);
			els.addAll(elements);

			try {
				buildElements(els, elements, monitor, WORK_BUILD
						- resourceTicks, IScriptBuilder.FULL_BUILD,
						new HashSet(), externalFolders, builders);
				lastBuildSourceFiles += elements.size();
			} catch (CoreException e) {
				DLTKCore.error(Messages.ScriptBuilder_errorBuildElements, e);
			}

			if (monitor.isCanceled()) {
				return;
			}
			buildResources(realResources, monitor, resourceTicks, FULL_BUILD);

			lastBuildResources = resources.size() + elements.size();
		} catch (CoreException e) {
			if (DLTKCore.DEBUG) {
				e.printStackTrace();
			}
		} finally {
			resetBuilders(builders);

			monitor.done();
			ModelManager.getModelManager().setLastBuiltState(currentProject,
					this.lastState);
		}
	}

	private void initializeBuilders(IScriptBuilder[] builders) {
		if (builders != null) {
			for (int k = 0; k < builders.length; k++) {
				builders[k].initialize(scriptProject);
			}
		}
	}

	private void resetBuilders(IScriptBuilder[] builders) {
		if (builders != null) {
			for (int k = 0; k < builders.length; k++) {
				builders[k].reset(scriptProject);
			}
		}
	}

	private Set getResourcesFrom(Object el, final IProgressMonitor monitor,
			int ticks) throws CoreException {
		Set resources = new HashSet();
		monitor.subTask(Messages.ScriptBuilder_scanningResourcesIn);
		try {
			ResourceVisitor resourceVisitor = new ResourceVisitor(resources,
					monitor);
			if (el instanceof IProject) {
				IProject prj = (IProject) el;
				prj.accept(resourceVisitor);
			} else if (el instanceof IResourceDelta) {
				IResourceDelta delta = (IResourceDelta) el;
				delta.accept(resourceVisitor);
			}
			return resources;
		} finally {
			monitor.worked(ticks);
		}
	}

	private Set getExternalElementsFrom(ScriptProject prj,
			final IProgressMonitor monitor, int tiks) throws ModelException {
		Set elements = new HashSet();
		String name = Messages.ScriptBuilder_scanningExternalResourcesFor;
		monitor.subTask(name);
		SubProgressMonitor mon = new SubProgressMonitor(monitor, tiks);
		ExternalModuleVisitor visitor = new ExternalModuleVisitor(elements, mon);

		IProjectFragment[] fragments = prj.getAllProjectFragments();
		List extFragments = new ArrayList();
		List currentFragments = new ArrayList();
		for (int i = 0; i < fragments.length; i++) {
			final IProjectFragment fragment = fragments[i];
			if (fragment instanceof ExternalProjectFragment
					|| fragment instanceof BuiltinProjectFragment) {
				IPath path = fragment.getPath();
				if (!this.lastState.externalFolderLocations.contains(path)) {
					extFragments.add(fragment);
				} else {
					currentFragments.add(path);
				}
			}
		}
		// monitor.subTask(name);
		mon.beginTask(name, extFragments.size());
		for (Iterator iterator = extFragments.iterator(); iterator.hasNext();) {
			IProjectFragment fragment = (IProjectFragment) iterator.next();
			// New project fragment, we need to obtain all modules
			// from this fragment.
			fragment.accept(visitor);
			mon.worked(1);
		}
		mon.done();

		this.lastState.externalFolderLocations.clear();
		this.lastState.externalFolderLocations.addAll(visitor
				.getExternalFolders());
		this.lastState.externalFolderLocations.addAll(currentFragments);

		return elements;
	}

	protected void incrementalBuild(IResourceDelta delta,
			IProgressMonitor monitor) throws CoreException {
		State newState = new State(this);

		Set externalFoldersBefore = new HashSet();
		Set externalFolders = new HashSet();
		if (this.lastState != null) {
			newState.copyFrom(this.lastState);
			externalFoldersBefore.addAll(newState.getExternalFolders());
		}

		this.lastState = newState;
		IScriptBuilder[] builders = null;
		try {
			monitor.setTaskName(NLS.bind(
					Messages.ScriptBuilder_buildingScriptsIn, currentProject
							.getName()));
			monitor.beginTask(NONAME, WORK_RESOURCES + WORK_EXTERNAL
					+ WORK_SOURCES + WORK_BUILD);

			if (monitor.isCanceled()) {
				return;
			}
			Set resources = getResourcesFrom(delta, monitor, WORK_RESOURCES);
			if (monitor.isCanceled()) {
				return;
			}
			Set elements = getExternalElementsFrom(scriptProject, monitor,
					WORK_EXTERNAL);
			if (monitor.isCanceled()) {
				return;
			}
			// New external folders set
			externalFolders.addAll(this.lastState.externalFolderLocations);

			int totalFiles = resources.size() + elements.size();
			if (totalFiles == 0)
				totalFiles = 1;

			builders = getScriptBuilders();

			initializeBuilders(builders);

			List realResources = new ArrayList();

			List relements = locateSourceModule(resources, monitor,
					WORK_SOURCES, resources, realResources);

			if (monitor.isCanceled()) {
				return;
			}
			int resourceTicks = WORK_BUILD
					* (resources.size() - relements.size()) / totalFiles;

			if (monitor.isCanceled()) {
				return;
			}
			List els = new ArrayList();

			els.addAll(relements);
			els.addAll(elements);
			try {
				buildElements(els, elements, monitor, WORK_BUILD
						- resourceTicks, IScriptBuilder.INCREMENTAL_BUILD,
						externalFoldersBefore, externalFolders, builders);
			} catch (CoreException e) {
				DLTKCore.error(Messages.ScriptBuilder_errorBuildElements, e);
			}
			lastBuildSourceFiles += elements.size();

			if (monitor.isCanceled()) {
				return;
			}
			buildResources(realResources, monitor, resourceTicks, FULL_BUILD);

			lastBuildResources = resources.size() + elements.size();
		} finally {
			resetBuilders(builders);

			monitor.done();
			ModelManager.getModelManager().setLastBuiltState(currentProject,
					this.lastState);
		}
	}

	private IScriptBuilder[] getScriptBuilders() throws CoreException {
		IDLTKLanguageToolkit toolkit = DLTKLanguageManager
				.getLanguageToolkit(scriptProject);
		if (toolkit != null) {
			return ScriptBuilderManager
					.getScriptBuilders(toolkit.getNatureId());
		} else {
			return null;
		}
	}

	protected List locateSourceModule(Set resources, IProgressMonitor monitor,
			int tiks, Set allresources, List realResources) {
		List elements = new ArrayList(); // Model elements

		Set allElements = new HashSet();
		Set allResources = new HashSet();
		String name = Messages.ScriptBuilder_locatingResourcesFor;
		IProgressMonitor sub = new SubProgressMonitor(monitor, tiks / 3);
		// sub.subTask(name);
		sub.beginTask(name, allresources.size());
		monitor.subTask(name);
		int id = 0;
		for (Iterator iterator = allresources.iterator(); iterator.hasNext();) {
			IResource res = (IResource) iterator.next();
			sub.subTask(NLS.bind(
					Messages.ScriptBuilder_Location_source_modules, String
							.valueOf(allresources.size() - id), res.getName()));
			sub.worked(1);
			if (sub.isCanceled()) {
				return null;
			}
			IModelElement element = DLTKCore.create(res);
			if (element != null
					&& element.getElementType() == IModelElement.SOURCE_MODULE
					&& element.exists()) {
				allElements.add(element);
				if (resources.contains(res)) {
					elements.add(element);
				}
			} else {
				if (resources.contains(res)) {
					realResources.add(res);
				}
				allResources.add(res);
			}
			id++;
		}
		sub.done();
		lastBuildSourceFiles += elements.size();
		return elements;
	}

	/**
	 * Build only resources, if some resources are elements they they will be
	 * returned.
	 */
	protected void buildResources(List realResources, IProgressMonitor monitor,
			int tiks, int buildType) {
		// Else build as resource.
		if (realResources.size() == 0) {
			monitor.worked(tiks);
		} else {
			Set alreadyPassed = new HashSet();
			try {
				IDLTKLanguageToolkit toolkit = DLTKLanguageManager
						.getLanguageToolkit(scriptProject);
				IScriptBuilder[] builders = ScriptBuilderManager
						.getScriptBuilders(toolkit.getNatureId());
				if (builders != null) {
					for (int k = 0; k < builders.length; k++) {
						IProgressMonitor ssub = new SubProgressMonitor(monitor,
								(tiks) / builders.length);
						ssub.beginTask(Messages.ScriptBuilder_building, 1);
						IScriptBuilder builder = builders[k];
						if (alreadyPassed.add(builder)) {
							builder.buildResources(this.scriptProject,
									realResources, ssub, buildType);
						}
						ssub.done();
					}
				}
			} catch (CoreException e) {
				if (DLTKCore.DEBUG) {
					e.printStackTrace();
				}
			}
		}
	}

	protected void buildElements(List elements, Set allElements,
			IProgressMonitor monitor, int ticks, int buildType,
			Set externalFoldersBefore, Set externalFolders,
			IScriptBuilder[] builders) throws CoreException {

		// TODO: replace this stuff with multistatus
		if (builders != null) {
			int total = 0;
			final int[] workEstimations = new int[builders.length];
			Map builderToElements = new HashMap();
			for (int k = 0; k < builders.length; k++) {
				IScriptBuilder builder = builders[k];
				List buildElementsList = getDependencies(elements, allElements,
						externalFoldersBefore, externalFolders, builder);
				builderToElements.put(builder, buildElementsList);
				workEstimations[k] = Math.max(builder.estimateElementsToBuild(
						scriptProject, buildElementsList), 1);
				total += workEstimations[k];
			}

			for (int k = 0; k < builders.length; k++) {
				IScriptBuilder builder = builders[k];

				List buildElementsList = (List) builderToElements.get(builder);
				final int builderWork = ticks * workEstimations[k] / total;
				if (buildElementsList.size() > 0) {
					IProgressMonitor sub = new SubProgressMonitor(monitor,
							builderWork);
					builder.buildModelElements(scriptProject,
							buildElementsList, sub, buildType);
				} else {
					monitor.worked(builderWork);
				}
			}
		}
		// TODO: Do something with status.
	}

	private List getDependencies(List elements, Set allElements,
			Set externalFoldersBefore, Set externalFolders,
			IScriptBuilder builder) {
		Set buildElements = new HashSet();
		buildElements.addAll(elements);
		Set dependencies = builder.getDependencies(this.scriptProject,
				new HashSet(elements), allElements, externalFoldersBefore,
				externalFolders);
		if (dependencies != null) {
			buildElements.addAll(dependencies);
		}
		List buildElementsList = new ArrayList();
		buildElementsList.addAll(buildElements);
		return buildElementsList;
	}

	public static void removeProblemsAndTasksFor(IResource resource) {
		try {
			if (resource != null && resource.exists()) {
				resource.deleteMarkers(
						IModelMarker.SCRIPT_MODEL_PROBLEM_MARKER, false,
						IResource.DEPTH_INFINITE);
				resource.deleteMarkers(IModelMarker.TASK_MARKER, false,
						IResource.DEPTH_INFINITE);

				// delete managed markers
			}
		} catch (CoreException e) {
			// assume there were no problems
		}
	}

	public static void writeState(Object state, DataOutputStream out)
			throws IOException {
		((State) state).write(out);
	}

	public static State readState(IProject project, DataInputStream in)
			throws IOException {
		State state = State.read(project, in);
		return state;
	}
}
