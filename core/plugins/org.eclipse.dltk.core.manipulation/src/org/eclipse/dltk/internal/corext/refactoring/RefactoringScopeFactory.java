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
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IBuildpathEntry;
import org.eclipse.dltk.core.IMember;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.IProjectFragment;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.core.search.IDLTKSearchScope;
import org.eclipse.dltk.core.search.SearchEngine;

public class RefactoringScopeFactory {

	/*
	 * Adds to <code> projects </code> ImodelProject objects for all projects directly or indirectly referencing focus. @param projects ImodelProjects will be added to this set
	 */
	private static void addReferencingProjects(IScriptProject focus, Set<IScriptProject> projects) throws ModelException {
		IProject[] referencingProjects= focus.getProject().getReferencingProjects();
		for (int i= 0; i < referencingProjects.length; i++) {
			IScriptProject candidate= DLTKCore.create(referencingProjects[i]);
			if (candidate == null || projects.contains(candidate) || !candidate.exists())
				continue; // break cycle
			IBuildpathEntry entry= getReferencingClassPathEntry(candidate, focus);
			if (entry != null) {
				projects.add(candidate);
				if (entry.isExported())
					addReferencingProjects(candidate, projects);
			}
		}
	}

	private static void addRelatedReferencing(IScriptProject focus, Set<IScriptProject> projects) throws CoreException {
		IProject[] referencingProjects= focus.getProject().getReferencingProjects();
		for (int i= 0; i < referencingProjects.length; i++) {
			IScriptProject candidate= DLTKCore.create(referencingProjects[i]);
			if (candidate == null || projects.contains(candidate) || !candidate.exists())
				continue; // break cycle
			IBuildpathEntry entry= getReferencingClassPathEntry(candidate, focus);
			if (entry != null) {
				projects.add(candidate);
				if (entry.isExported()) {
					addRelatedReferencing(candidate, projects);
					addRelatedReferenced(candidate, projects);
				}
			}
		}
	}

	private static void addRelatedReferenced(IScriptProject focus, Set<IScriptProject> projects) throws CoreException {
		IProject[] referencedProjects= focus.getProject().getReferencedProjects();
		for (int i= 0; i < referencedProjects.length; i++) {
			IScriptProject candidate= DLTKCore.create(referencedProjects[i]);
			if (candidate == null || projects.contains(candidate) || !candidate.exists())
				continue; // break cycle
			IBuildpathEntry entry= getReferencingClassPathEntry(focus, candidate);
			if (entry != null) {
				projects.add(candidate);
				if (entry.isExported()) {
					addRelatedReferenced(candidate, projects);
					addRelatedReferencing(candidate, projects);
				}
			}
		}
	}

	/**
	 * Creates a new search scope with all compilation units possibly referencing <code>modelElement</code>,
	 * considering the visibility of the element, references only from source
	 *
	 * @param modelElement the model element
	 * @return the search scope
	 * @throws ModelException if an error occurs
	 */
	public static IDLTKSearchScope create(IModelElement modelElement) throws ModelException {
		return RefactoringScopeFactory.create(modelElement, true, true);
	}

	/**
	 * Creates a new search scope with all compilation units possibly referencing <code>modelElement</code>,
	 * references only from source
	 *
	 * @param modelElement the model element
	 * @param considerVisibility consider visibility of modelElement iff <code>true</code>
	 * @return the search scope
	 * @throws ModelException if an error occurs
	 */
	public static IDLTKSearchScope create(IModelElement modelElement, boolean considerVisibility) throws ModelException {
		return RefactoringScopeFactory.create(modelElement, considerVisibility, true);
	}


	/**
	 * Creates a new search scope with all compilation units possibly referencing <code>modelElement</code>.
	 *
	 * @param modelElement the model element
	 * @param considerVisibility consider visibility of modelElement iff <code>true</code>
	 * @param sourceReferencesOnly consider references in source only (no references in binary)
	 * @return the search scope
	 * @throws ModelException if an error occurs
	 */
	public static IDLTKSearchScope create(IModelElement modelElement, boolean considerVisibility, boolean sourceReferencesOnly) throws ModelException {
		if (considerVisibility & modelElement instanceof IMember) {
			/*IMember member= (IMember) modelElement;
			if (JdtFlags.isPrivate(member)) {
				if (member.getCompilationUnit() != null)
					return SearchEngine.createmodelSearchScope(new ImodelElement[] { member.getCompilationUnit()});
				else
					return SearchEngine.createmodelSearchScope(new ImodelElement[] { member});
			}*/
			// Removed code that does some optimizations regarding package visible members. The problem is that
			// there can be a package fragment with the same name in a different source folder or project. So we
			// have to treat package visible members like public or protected members.
		}


		IScriptProject modelProject= modelElement.getScriptProject();
		return SearchEngine.createSearchScope(getAllScopeElements(modelProject, sourceReferencesOnly), false,
				modelProject.getLanguageToolkit());
	}

	/**
	 * Creates a new search scope comprising <code>members</code>.
	 *
	 * @param members the members
	 * @return the search scope
	 * @throws modelModelException if an error occurs
	 */
	public static IDLTKSearchScope create(IMember[] members) throws ModelException {
		return create(members, true);
	}

	/**
	 * Creates a new search scope comprising <code>members</code>.
	 *
	 * @param members the members
	 * @param sourceReferencesOnly consider references in source only (no references in binary)
	 * @return the search scope
	 * @throws modelModelException if an error occurs
	 */
	public static IDLTKSearchScope create(IMember[] members, boolean sourceReferencesOnly) throws ModelException {
		/*Assert.isTrue(members != null && members.length > 0);
		IMember candidate= members[0];
		int visibility= getVisibility(candidate);
		for (int i= 1; i < members.length; i++) {
			int mv= getVisibility(members[i]);
			if (mv > visibility) {
				visibility= mv;
				candidate= members[i];
			}
		}
		return create(candidate, true, sourceReferencesOnly);*/
		return create(members[0], true, sourceReferencesOnly);
	}

	/**
	 * Creates a new search scope with all projects possibly referenced
	 * from the given <code>modelElements</code>.
	 *
	 * @param modelElements the model elements
	 * @return the search scope
	 */
	public static IDLTKSearchScope createReferencedScope(IModelElement[] modelElements) {
		Set<IScriptProject> projects= new HashSet<IScriptProject>();
		for (int i= 0; i < modelElements.length; i++) {
			projects.add(modelElements[i].getScriptProject());
		}
		IScriptProject[] prj= (IScriptProject[]) projects.toArray(new IScriptProject[projects.size()]);
		return SearchEngine.createSearchScope(prj, true,
				prj.length > 0 ? prj[0].getLanguageToolkit() : null);
	}

	/**
	 * Creates a new search scope with all projects possibly referenced
	 * from the given <code>modelElements</code>.
	 *
	 * @param modelElements the model elements
	 * @param includeMask the include mask
	 * @return the search scope
	 */
	public static IDLTKSearchScope createReferencedScope(IModelElement[] modelElements, int includeMask) {
		Set<IScriptProject> projects= new HashSet<IScriptProject>();
		for (int i= 0; i < modelElements.length; i++) {
			projects.add(modelElements[i].getScriptProject());
		}
		IScriptProject[] prj= (IScriptProject[]) projects.toArray(new IScriptProject[projects.size()]);
		return SearchEngine.createSearchScope(prj, true,
				prj.length > 0 ? prj[0].getLanguageToolkit() : null);
	}

	/**
	 * Creates a new search scope containing all projects which reference or are referenced by the specified project.
	 *
	 * @param project the project
	 * @param includeMask the include mask
	 * @return the search scope
	 * @throws CoreException if a referenced project could not be determined
	 */
	public static IDLTKSearchScope createRelatedProjectsScope(IScriptProject project, int includeMask) throws CoreException {
		IScriptProject[] projects= getRelatedProjects(project);
		return SearchEngine.createSearchScope(projects, true,
				projects.length > 0 ? projects[0].getLanguageToolkit() : null);
	}

	/*
	 * @param projects a collection of ImodelProject
	 * @return Array of IPackageFragmentRoot, one element for each packageFragmentRoot which lies within a project in <code> projects </code> .
	 */
	private static IProjectFragment[] getAllScopeElements(IScriptProject project, boolean onlySourceRoots) throws ModelException {
		Collection<IScriptProject> referencingProjects= getReferencingProjects(project);
		List<IProjectFragment> result= new ArrayList<IProjectFragment>();
		for (IScriptProject scriptProject : referencingProjects) {
			IProjectFragment[] roots= scriptProject.getProjectFragments();
			// Add all package fragment roots except archives
			for (int i= 0; i < roots.length; i++) {
				IProjectFragment root= roots[i];
				if (!onlySourceRoots || root.getKind() == IProjectFragment.K_SOURCE)
					result.add(root);
			}
		}
		return result.toArray(new IProjectFragment[result.size()]);
	}

	/*
	 * Finds, if possible, a classpathEntry in one given project such that this classpath entry references another given project. If more than one entry exists for the referenced project and at least one is exported, then an exported entry will be returned.
	 */
	private static IBuildpathEntry getReferencingClassPathEntry(IScriptProject referencingProject, IScriptProject referencedProject) throws ModelException {
		IBuildpathEntry result= null;
		IPath path= referencedProject.getProject().getFullPath();
		IBuildpathEntry[] classpath= referencingProject.getResolvedBuildpath(true);
		for (int i= 0; i < classpath.length; i++) {
			IBuildpathEntry entry= classpath[i];
			if (entry.getEntryKind() == IBuildpathEntry.BPE_PROJECT && path.equals(entry.getPath())) {
				if (entry.isExported())
					return entry;
				// Consider it as a candidate. May be there is another entry that is
				// exported.
				result= entry;
			}
		}
		return result;
	}

	private static IScriptProject[] getRelatedProjects(IScriptProject focus) throws CoreException {
		final Set<IScriptProject> projects= new HashSet<IScriptProject>();

		addRelatedReferencing(focus, projects);
		addRelatedReferenced(focus, projects);

		projects.add(focus);
		return projects.toArray(new IScriptProject[projects.size()]);
	}

	private static Collection<IScriptProject> getReferencingProjects(IScriptProject focus) throws ModelException {
		Set<IScriptProject> projects= new HashSet<IScriptProject>();

		addReferencingProjects(focus, projects);
		projects.add(focus);
		return projects;
	}

	/*private static int getVisibility(IMember member) throws ModelException {
		if (JdtFlags.isPrivate(member))
			return 0;
		if (JdtFlags.isPackageVisible(member))
			return 1;
		if (JdtFlags.isProtected(member))
			return 2;
		return 4;
	}*/

	private RefactoringScopeFactory() {
		// no instances
	}
}
