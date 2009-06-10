/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.dltk.internal.mylyn.search;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.DLTKLanguageManager;
import org.eclipse.dltk.core.IMember;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.IType;
import org.eclipse.dltk.core.search.IDLTKSearchConstants;
import org.eclipse.dltk.core.search.IDLTKSearchScope;
import org.eclipse.dltk.core.search.SearchEngine;
import org.eclipse.dltk.internal.core.ScriptProject;
import org.eclipse.dltk.internal.mylyn.DLTKStructureBridge;
import org.eclipse.dltk.internal.mylyn.DLTKUiBridgePlugin;
import org.eclipse.dltk.internal.ui.search.DLTKSearchQuery;
import org.eclipse.dltk.internal.ui.search.DLTKSearchResult;
import org.eclipse.dltk.ui.search.ElementQuerySpecification;
import org.eclipse.dltk.ui.search.QuerySpecification;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.context.core.AbstractContextStructureBridge;
import org.eclipse.mylyn.context.core.ContextCore;
import org.eclipse.mylyn.context.core.IInteractionElement;
import org.eclipse.mylyn.internal.context.core.AbstractRelationProvider;
import org.eclipse.mylyn.internal.context.core.ContextCorePlugin;
import org.eclipse.mylyn.internal.context.core.DegreeOfSeparation;
import org.eclipse.mylyn.internal.context.core.IActiveSearchListener;
import org.eclipse.mylyn.internal.context.core.IActiveSearchOperation;
import org.eclipse.mylyn.internal.context.core.IDegreeOfSeparation;
import org.eclipse.mylyn.internal.resources.ui.ResourcesUiBridgePlugin;
import org.eclipse.search.ui.ISearchResult;
import org.eclipse.search2.internal.ui.InternalSearchUI;

/**
 * @author Mik Kersten
 */
public abstract class AbstractJavaRelationProvider extends
		AbstractRelationProvider {

	public static final String ID_GENERIC = "org.eclipse.mylyn.java.relation"; //$NON-NLS-1$

	public static final String NAME = "Java relationships"; //$NON-NLS-1$

	private static final int DEFAULT_DEGREE = 2;

	private static final List<Job> runningJobs = new ArrayList<Job>();

	@Override
	public String getGenericId() {
		return ID_GENERIC;
	}

	protected AbstractJavaRelationProvider(String structureKind, String id) {
		super(structureKind, id);
	}

	@Override
	public List<IDegreeOfSeparation> getDegreesOfSeparation() {
		List<IDegreeOfSeparation> separations = new ArrayList<IDegreeOfSeparation>();
		separations.add(new DegreeOfSeparation(DOS_0_LABEL, 0));
		separations.add(new DegreeOfSeparation(DOS_1_LABEL, 1));
		separations.add(new DegreeOfSeparation(DOS_2_LABEL, 2));
		separations.add(new DegreeOfSeparation(DOS_3_LABEL, 3));
		separations.add(new DegreeOfSeparation(DOS_4_LABEL, 4));
		separations.add(new DegreeOfSeparation(DOS_5_LABEL, 5));
		return separations;
	}

	@Override
	protected void findRelated(final IInteractionElement node,
			int degreeOfSeparation) {
		if (node == null) {
			return;
		}
		if (node.getContentType() == null) {
			StatusHandler.log(new Status(IStatus.WARNING,
					DLTKUiBridgePlugin.ID_PLUGIN, "Null content type for: " //$NON-NLS-1$
							+ node));
			return;
		}
		if (!node.getContentType().equals(DLTKStructureBridge.CONTENT_TYPE)) {
			return;
		}
		IModelElement javaElement = DLTKCore.create(node.getHandleIdentifier());
		if (!acceptElement(javaElement) || !javaElement.exists()/*
																 * ||
																 * javaElement
																 * instanceof
																 * IInitializer
																 */) {
			return;
		}

		IDLTKSearchScope scope = createJavaSearchScope(javaElement,
				degreeOfSeparation);
		if (scope != null) {
			runJob(node, degreeOfSeparation, getId());
		}
	}

	private IDLTKSearchScope createJavaSearchScope(IModelElement element,
			int degreeOfSeparation) {
		Set<IInteractionElement> landmarks = ContextCore.getContextManager()
				.getActiveLandmarks();
		List<IInteractionElement> interestingElements = ContextCore
				.getContextManager().getActiveContext().getInteresting();

		Set<IModelElement> searchElements = new HashSet<IModelElement>();
		int includeMask = IDLTKSearchScope.SOURCES;
		if (degreeOfSeparation == 1) {
			for (IInteractionElement landmark : landmarks) {
				AbstractContextStructureBridge bridge = ContextCore
						.getStructureBridge(landmark.getContentType());
				if (includeNodeInScope(landmark, bridge)) {
					Object o = bridge.getObjectForHandle(landmark
							.getHandleIdentifier());
					if (o instanceof IModelElement) {
						IModelElement landmarkElement = (IModelElement) o;
						if (landmarkElement.exists()) {
							if (landmarkElement instanceof IMember
									&& !landmark.getInterest().isPropagated()) {
								searchElements.add(((IMember) landmarkElement)
										.getSourceModule());
							} else if (landmarkElement instanceof ISourceModule) {
								searchElements.add(landmarkElement);
							}
						}
					}
				}
			}
		} else if (degreeOfSeparation == 2) {
			for (IInteractionElement interesting : interestingElements) {
				AbstractContextStructureBridge bridge = ContextCore
						.getStructureBridge(interesting.getContentType());
				if (includeNodeInScope(interesting, bridge)) {
					Object object = bridge.getObjectForHandle(interesting
							.getHandleIdentifier());
					if (object instanceof IModelElement) {
						IModelElement interestingElement = (IModelElement) object;
						if (interestingElement.exists()) {
							if (interestingElement instanceof IMember
									&& !interesting.getInterest()
											.isPropagated()) {
								searchElements
										.add(((IMember) interestingElement)
												.getSourceModule());
							} else if (interestingElement instanceof ISourceModule) {
								searchElements.add(interestingElement);
							}
						}
					}
				}
			}
		} else if (degreeOfSeparation == 3 || degreeOfSeparation == 4) {
			for (IInteractionElement interesting : interestingElements) {
				AbstractContextStructureBridge bridge = ContextCore
						.getStructureBridge(interesting.getContentType());
				if (includeNodeInScope(interesting, bridge)) {
					// TODO what to do when the element is not a java element,
					// how determine if a javaProject?
					IResource resource = ResourcesUiBridgePlugin.getDefault()
							.getResourceForElement(interesting, true);
					if (resource != null) {
						IProject project = resource.getProject();
						if (project != null
								&& ScriptProject.hasScriptNature(project)
								&& project.exists()) {
							IScriptProject javaProject = DLTKCore
									.create(project);// ((IModelElement)o).getJavaProject();
							if (javaProject != null && javaProject.exists()) {
								searchElements.add(javaProject);
							}
						}
					}
				}
			}
			if (degreeOfSeparation == 4) {

				includeMask = IDLTKSearchScope.SOURCES
						| IDLTKSearchScope.APPLICATION_LIBRARIES
						| IDLTKSearchScope.SYSTEM_LIBRARIES;
			}
		} else if (degreeOfSeparation == 5) {
			return SearchEngine.createWorkspaceScope(DLTKLanguageManager
					.getLanguageToolkit(element));
		}

		if (searchElements.size() == 0) {
			return null;
		} else {
			IModelElement[] elements = new IModelElement[searchElements.size()];
			int j = 0;
			for (IModelElement searchElement : searchElements) {
				elements[j] = searchElement;
				j++;
			}
			return SearchEngine.createSearchScope(elements, includeMask,
					DLTKLanguageManager.getLanguageToolkit(element));
		}
	}

	/**
	 * Only include Java elements and files.
	 */
	private boolean includeNodeInScope(IInteractionElement interesting,
			AbstractContextStructureBridge bridge) {
		if (interesting == null || bridge == null) {
			return false;
		} else {
			if (interesting.getContentType() == null) {
				// TODO: remove
				StatusHandler.log(new Status(IStatus.WARNING,
						DLTKUiBridgePlugin.ID_PLUGIN, "Null content type for: " //$NON-NLS-1$
								+ interesting.getHandleIdentifier()));
				return false;
			} else {
				return interesting.getContentType().equals(
						DLTKStructureBridge.CONTENT_TYPE)
						|| bridge.isDocument(interesting.getHandleIdentifier());
			}
		}
	}

	protected boolean acceptResultElement(IModelElement element) {
		return true; // !(element instanceof IImportDeclaration);
	}

	protected boolean acceptElement(IModelElement javaElement) {
		return javaElement != null
				&& (javaElement instanceof IMember || javaElement instanceof IType);
	}

	private void runJob(final IInteractionElement node,
			final int degreeOfSeparation, final String kind) {

		int limitTo = 0;
		if (kind.equals(DLTKReferencesProvider.ID)) {
			limitTo = IDLTKSearchConstants.REFERENCES;
			// } else if (kind.equals(JavaImplementorsProvider.ID)) {
			// limitTo = IDLTKSearchConstants.IMPLEMENTORS;
		} else if (kind.equals(JUnitReferencesProvider.ID)) {
			limitTo = IDLTKSearchConstants.REFERENCES;
		} else if (kind.equals(DLTKReadAccessProvider.ID)) {
			limitTo = IDLTKSearchConstants.REFERENCES;
		} else if (kind.equals(DLTKWriteAccessProvider.ID)) {
			limitTo = IDLTKSearchConstants.REFERENCES;
		}

		final JavaSearchOperation query = (JavaSearchOperation) getSearchOperation(
				node, limitTo, degreeOfSeparation);
		if (query == null) {
			return;
		}

		JavaSearchJob job = new JavaSearchJob(query.getLabel(), query);
		query.addListener(new IActiveSearchListener() {

			private boolean gathered = false;

			public boolean resultsGathered() {
				return gathered;
			}

			@SuppressWarnings("unchecked")
			public void searchCompleted(List l) {
				if (l == null) {
					return;
				}
				List<IModelElement> relatedHandles = new ArrayList<IModelElement>();
				Object[] elements = l.toArray();
				for (Object element : elements) {
					if (element instanceof IModelElement) {
						relatedHandles.add((IModelElement) element);
					}
				}

				for (IModelElement element : relatedHandles) {
					if (!acceptResultElement(element)) {
						continue;
					}
					incrementInterest(node, DLTKStructureBridge.CONTENT_TYPE,
							element.getHandleIdentifier(), degreeOfSeparation);
				}
				gathered = true;
				AbstractJavaRelationProvider.this.searchCompleted(node);
			}

		});
		InternalSearchUI.getInstance();

		runningJobs.add(job);
		job.setPriority(Job.DECORATE - 10);
		job.schedule();
	}

	@Override
	public IActiveSearchOperation getSearchOperation(IInteractionElement node,
			int limitTo, int degreeOfSeparation) {
		IModelElement javaElement = DLTKCore.create(node.getHandleIdentifier());
		if (javaElement == null || !javaElement.exists()) {
			return null;
		}

		IDLTKSearchScope scope = createJavaSearchScope(javaElement,
				degreeOfSeparation);

		if (scope == null) {
			return null;
		}

		QuerySpecification specs = new ElementQuerySpecification(
				javaElement,
				limitTo,
				scope,
				Messages.AbstractJavaRelationProvider_Mylyn_degree_of_separation
						+ degreeOfSeparation);

		return new JavaSearchOperation(specs);
	}

	protected static class JavaSearchJob extends Job {

		private final JavaSearchOperation op;

		public JavaSearchJob(String name, JavaSearchOperation op) {
			super(name);
			this.op = op;
		}

		/**
		 * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.IProgressMonitor)
		 */
		@Override
		protected IStatus run(IProgressMonitor monitor) {
			return op.run(monitor);
		}

	}

	protected static class JavaSearchOperation extends DLTKSearchQuery
			implements IActiveSearchOperation {
		private ISearchResult result = null;

		@Override
		public ISearchResult getSearchResult() {
			if (result == null) {
				result = new DLTKSearchResult(this);
			}
			new DLTKActiveSearchResultUpdater((DLTKSearchResult) result);
			return result;
		}

		@Override
		public IStatus run(IProgressMonitor monitor) {
			try {
				IStatus runStatus = super.run(monitor);
				ISearchResult result = getSearchResult();
				if (result instanceof DLTKSearchResult) {
					// TODO make better
					Object[] objs = ((DLTKSearchResult) result).getElements();
					if (objs == null) {
						notifySearchCompleted(null);
					} else {
						List<Object> l = new ArrayList<Object>();
						for (Object obj : objs) {
							l.add(obj);
						}
						notifySearchCompleted(l);
					}
				}
				return runStatus;
			} catch (Throwable t) {
				StatusHandler.log(new Status(IStatus.ERROR,
						DLTKUiBridgePlugin.ID_PLUGIN, "Java search failed", t)); //$NON-NLS-1$
			}

			IStatus status = new Status(
					IStatus.WARNING,
					ContextCorePlugin.ID_PLUGIN,
					IStatus.OK,
					Messages.AbstractJavaRelationProvider_could_not_run_Java_search,
					null);
			notifySearchCompleted(null);
			return status;
		}

		/**
		 * Constructor
		 * 
		 * @param data
		 */
		public JavaSearchOperation(QuerySpecification data) {
			super(data);

		}

		/** List of listeners wanting to know about the searches */
		private final List<IActiveSearchListener> listeners = new ArrayList<IActiveSearchListener>();

		/**
		 * Add a listener for when the bugzilla search is completed
		 * 
		 * @param l
		 *            The listener to add
		 */
		public void addListener(IActiveSearchListener l) {
			// add the listener to the list
			listeners.add(l);
		}

		/**
		 * Remove a listener for when the bugzilla search is completed
		 * 
		 * @param l
		 *            The listener to remove
		 */
		public void removeListener(IActiveSearchListener l) {
			// remove the listener from the list
			listeners.remove(l);
		}

		/**
		 * Notify all of the listeners that the bugzilla search is completed
		 * 
		 * @param doiList
		 *            A list of BugzillaSearchHitDoiInfo
		 * @param member
		 *            The IMember that the search was performed on
		 */
		public void notifySearchCompleted(List<Object> l) {
			// go through all of the listeners and call
			// searchCompleted(colelctor,
			// member)
			for (IActiveSearchListener listener : listeners) {
				listener.searchCompleted(l);
			}
		}

	}

	@Override
	public void stopAllRunningJobs() {
		for (Job j : runningJobs) {
			j.cancel();
		}
		runningJobs.clear();
	}

	@Override
	protected int getDefaultDegreeOfSeparation() {
		return DEFAULT_DEGREE;
	}
}
