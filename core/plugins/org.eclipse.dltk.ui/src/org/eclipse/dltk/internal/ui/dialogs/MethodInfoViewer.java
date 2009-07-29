/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.eclipse.dltk.internal.ui.dialogs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.ProgressMonitorWrapper;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.dltk.core.IDLTKLanguageToolkit;
import org.eclipse.dltk.core.IMethod;
import org.eclipse.dltk.core.IProjectFragment;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.core.WorkingCopyOwner;
import org.eclipse.dltk.core.index2.search.ModelAccess;
import org.eclipse.dltk.core.search.IDLTKSearchConstants;
import org.eclipse.dltk.core.search.IDLTKSearchScope;
import org.eclipse.dltk.core.search.MethodNameMatch;
import org.eclipse.dltk.core.search.MethodNameMatchRequestor;
import org.eclipse.dltk.core.search.NopTypeNameRequestor;
import org.eclipse.dltk.core.search.SearchEngine;
import org.eclipse.dltk.core.search.SearchPattern;
import org.eclipse.dltk.internal.core.search.DLTKSearchMethodNameMatch;
import org.eclipse.dltk.internal.corext.util.Messages;
import org.eclipse.dltk.internal.corext.util.MethodFilter;
import org.eclipse.dltk.internal.corext.util.MethodInfoFilter;
import org.eclipse.dltk.internal.corext.util.OpenMethodHistory;
import org.eclipse.dltk.internal.corext.util.Strings;
import org.eclipse.dltk.internal.ui.DLTKUIMessages;
import org.eclipse.dltk.launching.IInterpreterInstall;
import org.eclipse.dltk.launching.IInterpreterInstallType;
import org.eclipse.dltk.launching.LibraryLocation;
import org.eclipse.dltk.launching.ScriptRuntime;
import org.eclipse.dltk.ui.DLTKPluginImages;
import org.eclipse.dltk.ui.DLTKUILanguageManager;
import org.eclipse.dltk.ui.DLTKUIPlugin;
import org.eclipse.dltk.ui.IDLTKUILanguageToolkit;
import org.eclipse.dltk.ui.ScriptElementImageProvider;
import org.eclipse.dltk.ui.ScriptElementLabels;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.progress.UIJob;

/**
 * A viewer to present method queried form the method history and form the
 * search engine. All viewer updating takes place in the UI thread. Therefore no
 * synchronization of the methods is necessary.
 * 
 */
public class MethodInfoViewer {
	private IDLTKUILanguageToolkit fToolkit;

	private static class SearchRequestor extends MethodNameMatchRequestor {
		private volatile boolean fStop;

		private Set fHistory;

		private MethodInfoFilter fFilter;
		private List fResult;
		private MethodFilter fMethodFilter;

		public SearchRequestor(MethodInfoFilter filter,
				MethodFilter MethodFilter) {
			super();
			fResult = new ArrayList(2048);
			fFilter = filter;
			fMethodFilter = MethodFilter;
		}

		public MethodNameMatch[] getResult() {
			return (MethodNameMatch[]) fResult
					.toArray(new MethodNameMatch[fResult.size()]);
		}

		public void cancel() {
			fStop = true;
		}

		public void setHistory(Set history) {
			fHistory = history;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @seeorg.eclipse.jdt.core.search.MethodNameMatchRequestor#
		 * acceptMethodNameMatch(org.eclipse.jdt.core.search.MethodNameMatch)
		 */
		public void acceptMethodNameMatch(MethodNameMatch match) {
			if (fStop)
				return;
			if (fMethodFilter.isFiltered(match))
				return;
			if (fHistory.contains(match))
				return;
			if (fFilter.matchesFilterExtension(match))
				fResult.add(match);
		}
	}

	protected static class MethodInfoComparator implements Comparator {
		private MethodInfoLabelProvider fLabelProvider;
		private MethodInfoFilter fFilter;

		public MethodInfoComparator(MethodInfoLabelProvider labelProvider,
				MethodInfoFilter filter) {
			fLabelProvider = labelProvider;
			fFilter = filter;
		}

		public int compare(Object left, Object right) {
			MethodNameMatch leftInfo = (MethodNameMatch) left;
			MethodNameMatch rightInfo = (MethodNameMatch) right;
			int leftCategory = getCamelCaseCategory(leftInfo);
			int rightCategory = getCamelCaseCategory(rightInfo);
			if (leftCategory < rightCategory)
				return -1;
			if (leftCategory > rightCategory)
				return +1;
			int result = compareName(leftInfo.getSimpleMethodName(), rightInfo
					.getSimpleMethodName());
			if (result != 0)
				return result;
			result = compareTypeContainerName(
					leftInfo.getMethodContainerName(), rightInfo
							.getMethodContainerName());
			if (result != 0)
				return result;

			leftCategory = getElementTypeCategory(leftInfo);
			rightCategory = getElementTypeCategory(rightInfo);
			if (leftCategory < rightCategory)
				return -1;
			if (leftCategory > rightCategory)
				return +1;
			return compareContainerName(leftInfo, rightInfo);
		}

		private int compareName(String leftString, String rightString) {
			int result = leftString.compareToIgnoreCase(rightString);
			if (result != 0 || rightString.length() == 0) {
				return result;
			} else if (Strings.isLowerCase(leftString.charAt(0))
					&& !Strings.isLowerCase(rightString.charAt(0))) {
				return +1;
			} else if (Strings.isLowerCase(rightString.charAt(0))
					&& !Strings.isLowerCase(leftString.charAt(0))) {
				return -1;
			} else {
				return leftString.compareTo(rightString);
			}
		}

		private int compareTypeContainerName(String leftString,
				String rightString) {
			int leftLength = leftString.length();
			int rightLength = rightString.length();
			if (leftLength == 0 && rightLength > 0)
				return -1;
			if (leftLength == 0 && rightLength == 0)
				return 0;
			if (leftLength > 0 && rightLength == 0)
				return +1;
			return compareName(leftString, rightString);
		}

		private int compareContainerName(MethodNameMatch leftType,
				MethodNameMatch rightType) {
			return fLabelProvider.getContainerName(leftType).compareTo(
					fLabelProvider.getContainerName(rightType));
		}

		private int getCamelCaseCategory(MethodNameMatch type) {
			if (fFilter == null)
				return 0;
			if (!fFilter.isCamelCasePattern())
				return 0;
			return fFilter.matchesRawNamePattern(type) ? 0 : 1;
		}

		private int getElementTypeCategory(MethodNameMatch type) {
			try {
				if (type.getProjectFragment().getKind() == IProjectFragment.K_SOURCE)
					return 0;
			} catch (ModelException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return 1;
		}
	}

	protected class MethodInfoLabelProvider {

		private Map fLib2Name = new HashMap();
		private String[] fInstallLocations;
		private String[] fVMNames;

		public MethodInfoLabelProvider() {
			List locations = new ArrayList();
			List labels = new ArrayList();
			IInterpreterInstallType[] installs = ScriptRuntime
					.getInterpreterInstallTypes(fToolkit.getCoreToolkit()
							.getNatureId());
			for (int i = 0; i < installs.length; i++) {
				processVMInstallType(installs[i], locations, labels);
			}
			fInstallLocations = (String[]) locations
					.toArray(new String[locations.size()]);
			fVMNames = (String[]) labels.toArray(new String[labels.size()]);

		}

		private void processVMInstallType(IInterpreterInstallType installType,
				List locations, List labels) {
			if (installType != null) {
				IInterpreterInstall[] installs = installType
						.getInterpreterInstalls();
				final boolean isMac = Platform.OS_MACOSX.equals(Platform
						.getOS());
				for (int i = 0; i < installs.length; i++) {
					final IInterpreterInstall install = installs[i];
					final String label = getFormattedLabel(install.getName());
					final LibraryLocation[] libLocations = install
							.getLibraryLocations();
					if (libLocations != null) {
						processLibraryLocation(libLocations, label);
					} else {
						String filePath = install.getInstallLocation()
								.toOSString();
						/*
						 * filePath could be null if environment is configured,
						 * but environment-specific plugins are absent.
						 */
						if (filePath != null) {
							// on MacOS X install locations end in an additional
							// "/Home" segment; remove it
							if (isMac && filePath.endsWith(HOME_SUFFIX))
								filePath = filePath.substring(0, filePath
										.length()
										- (HOME_SUFFIX.length() - 1));
							locations.add(filePath);
							labels.add(label);
						}
					}
				}
			}
		}

		private static final String HOME_SUFFIX = "/Home"; //$NON-NLS-1$

		private void processLibraryLocation(LibraryLocation[] libLocations,
				String label) {
			for (int l = 0; l < libLocations.length; l++) {
				LibraryLocation location = libLocations[l];
				fLib2Name.put(location.getLibraryPath().toString(), label);
			}
		}

		private String getFormattedLabel(String name) {
			return Messages.format(
					DLTKUIMessages.TypeInfoViewer_library_name_format, name);
		}

		public String getText(Object element) {
			MethodNameMatch type = (MethodNameMatch) element;
			return getTypeContainerName(type, 0);
		}

		public String getQualifiedText(MethodNameMatch type) {
			StringBuffer result = new StringBuffer();
			result.append(getTypeContainerName(type, 2));
			// String containerName= type.getTypeContainerName();
			// result.append(ScriptElementLabels.CONCAT_STRING);
			// if (containerName.length() > 0) {
			// result.append(containerName);
			// } else {
			// result.append(DLTKUIMessages.TypeInfoViewer_default_package);
			// }
			return result.toString();
		}

		public String getFullyQualifiedText(MethodNameMatch type) {
			StringBuffer result = new StringBuffer();
			result.append(getTypeContainerName(type, 2));
			// IType dltkType = ((DLTKSearchMethodNameMatch) type).getType();
			// ISourceModule sourceModule = (ISourceModule)
			// dltkType.getAncestor(IModelElement.SOURCE_MODULE);
			// String sourceModuleName = sourceModule.getElementName();
			// if (sourceModuleName.length() > 0) {
			// result.append(ScriptElementLabels.CONCAT_STRING);
			// result.append(sourceModuleName);
			// }
			// result.append(ScriptElementLabels.CONCAT_STRING);
			// result.append(getContainerName(type));
			return result.toString();
		}

		public String getText(MethodNameMatch last, MethodNameMatch current,
				MethodNameMatch next) {
			int qualifications = 0;
			String current0 = getTypeContainerName(current, 0);
			String current1 = getTypeContainerName(current, 1);
			String current2 = getTypeContainerName(current, 2);
			if (last != null) {
				String last0 = getTypeContainerName(last, 0);
				String last1 = getTypeContainerName(last, 1);
				if (current0.equals(last0)) {
					if (current1.equals(last1))
						qualifications = Math.max(qualifications, 2);
					else
						qualifications = Math.max(qualifications, 1);
				}
			}
			if (next != null) {
				String next0 = getTypeContainerName(next, 0);
				String next1 = getTypeContainerName(next, 1);
				if (current0.equals(next0)) {
					if (current1.equals(next1))
						qualifications = Math.max(qualifications, 2);
					else
						qualifications = Math.max(qualifications, 1);
				}
			}
			if (qualifications > 1) {
				return current2;
			}
			if (qualifications > 0) {
				return current1;
			}
			return current0;
		}

		public String getQualificationText(MethodNameMatch type) {
			StringBuffer result = new StringBuffer();
			String containerName = type.getMethodContainerName();
			if (containerName.length() > 0) {
				result.append(containerName);
				result.append(ScriptElementLabels.CONCAT_STRING);
			}
			result.append(getContainerName(type));
			return result.toString();
		}

		// private boolean isInnerType(MethodNameMatch match) {
		// return match.getTypeQualifiedName().indexOf('.') != -1;
		// }

		public ImageDescriptor getImageDescriptor(Object element) {
			MethodNameMatch method = (MethodNameMatch) element;
			return ScriptElementImageProvider.getMethodImageDescriptor(method
					.getModifiers());
		}

		private String getTypeContainerName(MethodNameMatch info, int infoLevel) {
			// String result= info.getTypeContainerName();
			String result = ""; //$NON-NLS-1$
			IDLTKUILanguageToolkit toolkit = DLTKUILanguageManager
					.getLanguageToolkit(info.getMethod());
			if (toolkit != null) {
				ScriptElementLabels labels = toolkit.getScriptElementLabels();
				result = labels
						.getElementLabel(
								info.getMethod(),
								ScriptElementLabels.T_CONTAINER_QUALIFIED
										| (infoLevel > 0 ? ScriptElementLabels.APPEND_FILE
												: 0)
										| (infoLevel > 1 ? ScriptElementLabels.CU_POST_QUALIFIED
												: 0));
			}
			if (result.length() > 0)
				return result;
			return DLTKUIMessages.TypeInfoViewer_default_package;
		}

		private String getContainerName(MethodNameMatch type) {
			IProjectFragment root = type.getProjectFragment();
			if (root.isExternal()) {
				String name = root.getPath().toOSString();
				for (int i = 0; i < fInstallLocations.length; i++) {
					if (name.startsWith(fInstallLocations[i])) {
						return fVMNames[i];
					}
				}
				String lib = (String) fLib2Name.get(name);
				if (lib != null)
					return lib;
			}
			StringBuffer buf = new StringBuffer();
			ScriptElementLabels labels = fToolkit.getScriptElementLabels();
			labels.getProjectFragmentLabel(root,
					ScriptElementLabels.ROOT_QUALIFIED
							| ScriptElementLabels.ROOT_VARIABLE, buf);

			return buf.toString();
		}
	}

	private static class ProgressUpdateJob extends UIJob {
		private MethodInfoViewer fViewer;
		private boolean fStopped;

		public ProgressUpdateJob(Display display, MethodInfoViewer viewer) {
			super(display, DLTKUIMessages.TypeInfoViewer_progressJob_label);
			fViewer = viewer;
		}

		public void stop() {
			fStopped = true;
			cancel();
		}

		public IStatus runInUIThread(IProgressMonitor monitor) {
			if (stopped())
				return new Status(IStatus.CANCEL, DLTKUIPlugin.getPluginId(),
						IStatus.CANCEL, "", null); //$NON-NLS-1$
			fViewer.updateProgressMessage();
			if (!stopped())
				schedule(300);
			return new Status(IStatus.OK, DLTKUIPlugin.getPluginId(),
					IStatus.OK, "", null); //$NON-NLS-1$
		}

		private boolean stopped() {
			return fStopped || fViewer.getTable().isDisposed();
		}
	}

	private static class ProgressMonitor extends ProgressMonitorWrapper {
		private MethodInfoViewer fViewer;
		private String fName;
		private int fTotalWork;
		private double fWorked;
		private boolean fDone;

		public ProgressMonitor(IProgressMonitor monitor, MethodInfoViewer viewer) {
			super(monitor);
			fViewer = viewer;
		}

		public void setTaskName(String name) {
			super.setTaskName(name);
			fName = name;
		}

		public void beginTask(String name, int totalWork) {
			super.beginTask(name, totalWork);
			if (fName == null)
				fName = name;
			fTotalWork = totalWork;
		}

		public void worked(int work) {
			super.worked(work);
			internalWorked(work);
		}

		public void done() {
			fDone = true;
			fViewer.setProgressMessage(""); //$NON-NLS-1$
			super.done();
		}

		public void internalWorked(double work) {
			fWorked = fWorked + work;
			fViewer.setProgressMessage(getMessage());
		}

		private String getMessage() {
			if (fDone) {
				return ""; //$NON-NLS-1$
			} else if (fTotalWork == 0) {
				return fName;
			} else {
				return Messages
						.format(
								DLTKUIMessages.TypeInfoViewer_progress_label,
								new Object[] {
										fName,
										new Integer(
												(int) ((fWorked * 100) / fTotalWork)) });
			}
		}
	}

	private static abstract class AbstractJob extends Job {
		protected MethodInfoViewer fViewer;

		protected AbstractJob(String name, MethodInfoViewer viewer) {
			super(name);
			fViewer = viewer;
			setSystem(true);
		}

		protected final IStatus run(IProgressMonitor parent) {
			ProgressMonitor monitor = new ProgressMonitor(parent, fViewer);
			try {
				fViewer.scheduleProgressUpdateJob();
				return doRun(monitor);
			} finally {
				fViewer.stopProgressUpdateJob();
			}
		}

		protected abstract IStatus doRun(ProgressMonitor monitor);
	}

	private static abstract class AbstractSearchJob extends AbstractJob {
		private int fMode;

		protected int fTicket;
		protected MethodInfoLabelProvider fLabelProvider;

		protected MethodInfoFilter fFilter;
		protected OpenMethodHistory fHistory;

		protected AbstractSearchJob(int ticket, MethodInfoViewer viewer,
				MethodInfoFilter filter, OpenMethodHistory history,
				int numberOfVisibleItems, int mode) {
			super(DLTKUIMessages.TypeInfoViewer_job_label, viewer);
			fMode = mode;
			fTicket = ticket;
			fViewer = viewer;
			fLabelProvider = fViewer.getLabelProvider();
			fFilter = filter;
			fHistory = history;
		}

		public void stop() {
			cancel();
		}

		protected IStatus doRun(ProgressMonitor monitor) {
			try {
				if (VIRTUAL) {
					internalRunVirtual(monitor);
				} else {
					internalRun(monitor);
				}
			} catch (CoreException e) {
				fViewer.searchJobFailed(fTicket, e);
				return new Status(IStatus.ERROR, DLTKUIPlugin.getPluginId(),
						IStatus.ERROR, DLTKUIMessages.TypeInfoViewer_job_error,
						e);
			} catch (InterruptedException e) {
				return canceled(e, true);
			} catch (OperationCanceledException e) {
				return canceled(e, false);
			}
			fViewer.searchJobDone(fTicket);
			return ok();
		}

		protected abstract MethodNameMatch[] getSearchResult(
				Set matchIdsInHistory, ProgressMonitor monitor)
				throws CoreException;

		private void internalRun(ProgressMonitor monitor) throws CoreException,
				InterruptedException {
			if (monitor.isCanceled())
				throw new OperationCanceledException();

			fViewer.clear(fTicket);

			// local vars to speed up rendering
			MethodNameMatch last = null;
			MethodNameMatch type = null;
			MethodNameMatch next = null;
			List elements = new ArrayList();
			List imageDescriptors = new ArrayList();
			List labels = new ArrayList();
			Set filteredMatches = new HashSet();

			MethodNameMatch[] matchingTypes = fHistory
					.getFilteredTypeInfos(fFilter);
			if (matchingTypes.length > 0) {
				Arrays.sort(matchingTypes, new MethodInfoComparator(
						fLabelProvider, fFilter));
				type = matchingTypes[0];
				int i = 1;
				while (type != null) {
					next = (i == matchingTypes.length) ? null
							: matchingTypes[i];
					elements.add(type);
					filteredMatches.add(type);
					imageDescriptors.add(fLabelProvider
							.getImageDescriptor(type));
					labels.add(fLabelProvider.getText(last, type, next));
					last = type;
					type = next;
					i++;
				}
			}
			matchingTypes = null;
			fViewer.fExpectedItemCount = elements.size();
			fViewer.addHistory(fTicket, elements, imageDescriptors, labels);

			if ((fMode & INDEX) == 0) {
				return;
			}
			MethodNameMatch[] result = getSearchResult(filteredMatches, monitor);
			fViewer.fExpectedItemCount += result.length;
			if (result.length == 0) {
				return;
			}
			if (monitor.isCanceled())
				throw new OperationCanceledException();
			int processed = 0;
			int nextIndex = 1;
			type = result[0];
			if (!filteredMatches.isEmpty()) {
				fViewer.addDashLineAndUpdateLastHistoryEntry(fTicket, type);
			}
			while (true) {
				long startTime = System.currentTimeMillis();
				elements.clear();
				imageDescriptors.clear();
				labels.clear();
				int delta = Math.min(nextIndex == 1 ? fViewer
						.getNumberOfVisibleItems() : 10, result.length
						- processed);
				if (delta == 0)
					break;
				processed = processed + delta;
				while (delta > 0) {
					next = (nextIndex == result.length) ? null
							: result[nextIndex];
					elements.add(type);
					labels.add(fLabelProvider.getText(last, type, next));
					imageDescriptors.add(fLabelProvider
							.getImageDescriptor(type));
					last = type;
					type = next;
					nextIndex++;
					delta--;
				}
				fViewer.addAll(fTicket, elements, imageDescriptors, labels);
				long sleep = 100 - (System.currentTimeMillis() - startTime);
				if (false)
					System.out.println("Sleeping for: " + sleep); //$NON-NLS-1$

				if (sleep > 0)
					Thread.sleep(sleep);

				if (monitor.isCanceled())
					throw new OperationCanceledException();
			}
		}

		private void internalRunVirtual(ProgressMonitor monitor)
				throws CoreException, InterruptedException {
			if (monitor.isCanceled())
				throw new OperationCanceledException();

			fViewer.clear(fTicket);

			MethodNameMatch[] matchingTypes = fHistory
					.getFilteredTypeInfos(fFilter);
			fViewer.setHistoryResult(fTicket, matchingTypes);
			if ((fMode & INDEX) == 0)
				return;

			Set filteredMatches = new HashSet(matchingTypes.length * 2);
			for (int i = 0; i < matchingTypes.length; i++) {
				filteredMatches.add(matchingTypes[i]);
			}

			MethodNameMatch[] result = getSearchResult(filteredMatches, monitor);
			if (monitor.isCanceled())
				throw new OperationCanceledException();

			fViewer.setSearchResult(fTicket, result);
		}

		private IStatus canceled(Exception e, boolean removePendingItems) {
			fViewer.searchJobCanceled(fTicket, removePendingItems);
			return new Status(IStatus.CANCEL, DLTKUIPlugin.getPluginId(),
					IStatus.CANCEL, DLTKUIMessages.TypeInfoViewer_job_cancel, e);
		}

		private IStatus ok() {
			return new Status(IStatus.OK, DLTKUIPlugin.getPluginId(),
					IStatus.OK, "", null); //$NON-NLS-1$
		}
	}

	private static class SearchEngineJob extends AbstractSearchJob {
		private IDLTKSearchScope fScope;
		private int fElementKind;
		private SearchRequestor fReqestor;

		public SearchEngineJob(int ticket, MethodInfoViewer viewer,
				MethodInfoFilter filter, OpenMethodHistory history,
				int numberOfVisibleItems, int mode, IDLTKSearchScope scope,
				int elementKind, IDLTKUILanguageToolkit toolkit) {
			super(ticket, viewer, filter, history, numberOfVisibleItems, mode);
			fScope = scope;
			fElementKind = elementKind;
			fReqestor = new SearchRequestor(filter, new MethodFilter(toolkit));
		}

		public void stop() {
			fReqestor.cancel();
			super.stop();
		}

		protected MethodNameMatch[] getSearchResult(Set matchIdsInHistory,
				ProgressMonitor monitor) throws CoreException {
			long start = System.currentTimeMillis();
			fReqestor.setHistory(matchIdsInHistory);

			monitor
					.setTaskName(DLTKUIMessages.TypeInfoViewer_searchJob_taskName);

			IMethod[] methods = new ModelAccess().findMethods(fFilter
					.getNamePattern(), ModelAccess.convertSearchRule(fFilter
					.getSearchFlags()), 0, 0, fScope, monitor);
			if (methods != null) {
				for (IMethod method : methods) {
					fReqestor
							.acceptMethodNameMatch(new DLTKSearchMethodNameMatch(
									method, method.getFlags()));
				}
			} else {
				// consider primary working copies during searching
				SearchEngine engine = new SearchEngine((WorkingCopyOwner) null);
				engine.searchAllMethodNames(fFilter.getNamePattern()
						.toCharArray(), fFilter.getSearchFlags(), fElementKind,
						fScope, fReqestor,
						IDLTKSearchConstants.WAIT_UNTIL_READY_TO_SEARCH,
						monitor);
			}
			if (DEBUG)
				System.out
						.println("Time needed until search has finished: " + (System.currentTimeMillis() - start)); //$NON-NLS-1$
			MethodNameMatch[] result = fReqestor.getResult();
			Arrays.sort(result, new MethodInfoComparator(fLabelProvider,
					fFilter));
			if (DEBUG)
				System.out
						.println("Time needed until sort has finished: " + (System.currentTimeMillis() - start)); //$NON-NLS-1$
			fViewer.rememberResult(fTicket, result);
			return result;
		}
	}

	private static class CachedResultJob extends AbstractSearchJob {
		private MethodNameMatch[] fLastResult;

		public CachedResultJob(int ticket, MethodNameMatch[] lastResult,
				MethodInfoViewer viewer, MethodInfoFilter filter,
				OpenMethodHistory history, int numberOfVisibleItems, int mode) {
			super(ticket, viewer, filter, history, numberOfVisibleItems, mode);
			fLastResult = lastResult;
		}

		protected MethodNameMatch[] getSearchResult(Set filteredHistory,
				ProgressMonitor monitor) throws CoreException {
			List result = new ArrayList(2048);
			for (int i = 0; i < fLastResult.length; i++) {
				MethodNameMatch type = fLastResult[i];
				if (filteredHistory.contains(type))
					continue;
				if (fFilter.matchesCachedResult(type))
					result.add(type);
			}
			// we have to sort if the filter is a camel case filter.
			MethodNameMatch[] types = (MethodNameMatch[]) result
					.toArray(new MethodNameMatch[result.size()]);
			if (fFilter.isCamelCasePattern()) {
				Arrays.sort(types, new MethodInfoComparator(fLabelProvider,
						fFilter));
			}
			return types;
		}
	}

	private static class SyncJob extends AbstractJob {
		private IDLTKLanguageToolkit fToolkit;

		public SyncJob(MethodInfoViewer viewer, IDLTKLanguageToolkit toolkit) {
			super(DLTKUIMessages.TypeInfoViewer_syncJob_label, viewer);
			this.fToolkit = toolkit;
		}

		public void stop() {
			cancel();
		}

		protected IStatus doRun(ProgressMonitor monitor) {
			try {
				monitor
						.setTaskName(DLTKUIMessages.TypeInfoViewer_syncJob_taskName);
				new SearchEngine().searchAllTypeNames(
						null,
						0, // make sure we search a concrete name. This is
						// faster
						// according to Kent
						"_______________".toCharArray(), //$NON-NLS-1$
						SearchPattern.R_EXACT_MATCH
								| SearchPattern.R_CASE_SENSITIVE,
						IDLTKSearchConstants.TYPE, SearchEngine
								.createWorkspaceScope(fToolkit),
						new NopTypeNameRequestor(),
						IDLTKSearchConstants.WAIT_UNTIL_READY_TO_SEARCH,
						monitor);
			} catch (ModelException e) {
				DLTKUIPlugin.log(e);
				return new Status(IStatus.ERROR, DLTKUIPlugin.getPluginId(),
						IStatus.ERROR, DLTKUIMessages.TypeInfoViewer_job_error,
						e);
			} catch (OperationCanceledException e) {
				return new Status(IStatus.CANCEL, DLTKUIPlugin.getPluginId(),
						IStatus.CANCEL,
						DLTKUIMessages.TypeInfoViewer_job_cancel, e);
			} finally {
				fViewer.syncJobDone();
			}
			return new Status(IStatus.OK, DLTKUIPlugin.getPluginId(),
					IStatus.OK, "", null); //$NON-NLS-1$
		}
	}

	private static class DashLine {
		private int fSeparatorWidth;
		private String fMessage;
		private int fMessageLength;

		public String getText(int width) {
			StringBuffer dashes = new StringBuffer();
			int chars = (((width - fMessageLength) / fSeparatorWidth) / 2) - 2;
			for (int i = 0; i < chars; i++) {
				dashes.append(SEPARATOR);
			}
			StringBuffer result = new StringBuffer();
			result.append(dashes);
			result.append(fMessage);
			result.append(dashes);
			return result.toString();
		}

		public void initialize(GC gc) {
			fSeparatorWidth = gc.getAdvanceWidth(SEPARATOR);
			fMessage = " " + DLTKUIMessages.TypeInfoViewer_separator_message + " "; //$NON-NLS-1$ //$NON-NLS-2$
			fMessageLength = gc.textExtent(fMessage).x;
		}
	}

	private static class ImageManager {
		private Map fImages = new HashMap(20);

		public Image get(ImageDescriptor descriptor) {
			if (descriptor == null)
				descriptor = ImageDescriptor.getMissingImageDescriptor();

			Image result = (Image) fImages.get(descriptor);
			if (result != null)
				return result;
			result = descriptor.createImage();
			if (result != null)
				fImages.put(descriptor, result);
			return result;
		}

		public void dispose() {
			for (Iterator iter = fImages.values().iterator(); iter.hasNext();) {
				Image image = (Image) iter.next();
				image.dispose();
			}
			fImages.clear();
		}
	}

	private Display fDisplay;

	private String fProgressMessage;
	private Label fProgressLabel;
	private int fProgressCounter;
	private ProgressUpdateJob fProgressUpdateJob;

	private OpenMethodHistory fHistory;

	/* non virtual table */
	private int fNextElement;
	private List fItems;

	/* virtual table */
	private MethodNameMatch[] fHistoryMatches;
	private MethodNameMatch[] fSearchMatches;

	private int fNumberOfVisibleItems;
	private int fExpectedItemCount;
	private Color fDashLineColor;
	private int fScrollbarWidth;
	private int fTableWidthDelta;
	private int fDashLineIndex = -1;
	private Image fSeparatorIcon;
	private DashLine fDashLine = new DashLine();

	private boolean fFullyQualifySelection;
	/* remembers the last selection to restore unqualified labels */
	private TableItem[] fLastSelection;
	private String[] fLastLabels;

	private MethodInfoLabelProvider fLabelProvider;
	private ImageManager fImageManager;

	private Table fTable;

	private SyncJob fSyncJob;

	private MethodInfoFilter fMethodInfoFilter;
	private MethodNameMatch[] fLastCompletedResult;
	private MethodInfoFilter fLastCompletedFilter;

	private int fSearchJobTicket;
	protected int fElementKind;
	protected IDLTKSearchScope fSearchScope;

	private AbstractSearchJob fSearchJob;

	private static final int HISTORY = 1;
	private static final int INDEX = 2;
	private static final int FULL = HISTORY | INDEX;

	private static final char SEPARATOR = '-';

	private static final boolean DEBUG = false;
	private static final boolean VIRTUAL = false;

	private static final MethodNameMatch[] EMTPY_TYPE_INFO_ARRAY = new MethodNameMatch[0];
	// only needed when in virtual table mode

	private static final MethodNameMatch DASH_LINE = SearchEngine
			.createMethodNameMatch(null, 0);

	public MethodInfoViewer(Composite parent, int flags, Label progressLabel,
			IDLTKSearchScope scope, int elementKind, String initialFilter,
			IDLTKUILanguageToolkit toolkit) {
		Assert.isNotNull(scope);

		fToolkit = toolkit;
		fDisplay = parent.getDisplay();
		fProgressLabel = progressLabel;
		fSearchScope = scope;
		fElementKind = elementKind;
		fFullyQualifySelection = (flags & SWT.MULTI) != 0;
		fTable = new Table(parent, SWT.V_SCROLL | SWT.H_SCROLL | SWT.BORDER
				| SWT.FLAT | flags | (VIRTUAL ? SWT.VIRTUAL : SWT.NONE));
		fTable.setFont(parent.getFont());
		fLabelProvider = new MethodInfoLabelProvider();
		fItems = new ArrayList(500);
		fTable.setHeaderVisible(false);
		addPopupMenu();
		fTable.addControlListener(new ControlAdapter() {
			public void controlResized(ControlEvent event) {
				int itemHeight = fTable.getItemHeight();
				Rectangle clientArea = fTable.getClientArea();
				fNumberOfVisibleItems = (clientArea.height / itemHeight) + 1;
			}
		});
		fTable.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if (e.keyCode == SWT.DEL) {
					deleteHistoryEntry();
				} else if (e.keyCode == SWT.ARROW_DOWN) {
					int index = fTable.getSelectionIndex();
					if (index == fDashLineIndex - 1) {
						e.doit = false;
						setTableSelection(index + 2);
					}
				} else if (e.keyCode == SWT.ARROW_UP) {
					int index = fTable.getSelectionIndex();
					if (fDashLineIndex != -1 && index == fDashLineIndex + 1) {
						e.doit = false;
						setTableSelection(index - 2);
					}
				}
			}
		});
		fTable.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (fLastSelection != null) {
					for (int i = 0; i < fLastSelection.length; i++) {
						TableItem item = fLastSelection[i];
						// could be disposed by deleting element from
						// type info history
						if (!item.isDisposed())
							item.setText(fLastLabels[i]);
					}
				}
				TableItem[] items = fTable.getSelection();
				fLastSelection = new TableItem[items.length];
				fLastLabels = new String[items.length];
				for (int i = 0; i < items.length; i++) {
					TableItem item = items[i];
					fLastSelection[i] = item;
					fLastLabels[i] = item.getText();
					Object data = item.getData();
					if (data instanceof MethodNameMatch) {
						String qualifiedText = getQualifiedText((MethodNameMatch) data);
						if (qualifiedText.length() > fLastLabels[i].length())
							item.setText(qualifiedText);
					}
				}
			}
		});
		fTable.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				stop(true, true);
				fDashLineColor.dispose();
				fSeparatorIcon.dispose();
				fImageManager.dispose();
				if (fProgressUpdateJob != null) {
					fProgressUpdateJob.stop();
					fProgressUpdateJob = null;
				}
			}
		});
		if (VIRTUAL) {
			fHistoryMatches = EMTPY_TYPE_INFO_ARRAY;
			fSearchMatches = EMTPY_TYPE_INFO_ARRAY;
			fTable.addListener(SWT.SetData, new Listener() {
				public void handleEvent(Event event) {
					TableItem item = (TableItem) event.item;
					setData(item);
				}
			});
		}

		fDashLineColor = computeDashLineColor();
		fScrollbarWidth = computeScrollBarWidth();
		fTableWidthDelta = fTable.computeTrim(0, 0, 0, 0).width
				- fScrollbarWidth;
		fSeparatorIcon = DLTKPluginImages.DESC_OBJS_TYPE_SEPARATOR
				.createImage(fTable.getDisplay());
		// Use a new image manager since an extension can provide its own
		// image descriptors. To avoid thread problems with SWT the registry
		// must be created in the UI thread.
		fImageManager = new ImageManager();

		fHistory = OpenMethodHistory.getInstance(this.fToolkit);
		if (initialFilter != null && initialFilter.length() > 0)
			fMethodInfoFilter = createMethodInfoFilter(initialFilter);
		GC gc = null;
		try {
			gc = new GC(fTable);
			gc.setFont(fTable.getFont());
			fDashLine.initialize(gc);
		} finally {
			gc.dispose();
		}
		// If we do have a type info filter then we are
		// scheduling a search job in startup. So no
		// need to sync the search indices.
		if (fMethodInfoFilter == null) {
			scheduleSyncJob();
		}
	}

	/* package */void startup() {
		if (fMethodInfoFilter == null) {
			reset();
		} else {
			scheduleSearchJob(FULL);
		}
	}

	public Table getTable() {
		return fTable;
	}

	/* package */MethodInfoLabelProvider getLabelProvider() {
		return fLabelProvider;
	}

	private int getNumberOfVisibleItems() {
		return fNumberOfVisibleItems;
	}

	public void setFocus() {
		fTable.setFocus();
	}

	public void setQualificationStyle(boolean value) {
		if (fFullyQualifySelection == value)
			return;
		fFullyQualifySelection = value;
		if (fLastSelection != null) {
			for (int i = 0; i < fLastSelection.length; i++) {
				TableItem item = fLastSelection[i];
				Object data = item.getData();
				if (data instanceof MethodNameMatch) {
					item.setText(getQualifiedText((MethodNameMatch) data));
				}
			}
		}
	}

	public MethodNameMatch[] getSelection() {
		TableItem[] items = fTable.getSelection();
		List result = new ArrayList(items.length);
		for (int i = 0; i < items.length; i++) {
			Object data = items[i].getData();
			if (data instanceof MethodNameMatch) {
				result.add(data);
			}
		}
		return (MethodNameMatch[]) result.toArray(new MethodNameMatch[result
				.size()]);
	}

	public void stop() {
		stop(true, false);
	}

	public void stop(boolean stopSyncJob, boolean dispose) {
		if (fSyncJob != null && stopSyncJob) {
			fSyncJob.stop();
			fSyncJob = null;
		}
		if (fSearchJob != null) {
			fSearchJob.stop();
			fSearchJob = null;
		}
	}

	public void forceSearch() {
		stop(false, false);
		if (fMethodInfoFilter == null) {
			reset();
		} else {
			// clear last results
			fLastCompletedFilter = null;
			fLastCompletedResult = null;
			scheduleSearchJob(isSyncJobRunning() ? HISTORY : FULL);
		}
	}

	public void setSearchPattern(String text) {
		stop(false, false);
		if (text.length() == 0 || "*".equals(text)) { //$NON-NLS-1$
			fMethodInfoFilter = null;
			reset();
		} else {
			fMethodInfoFilter = createMethodInfoFilter(text);
			scheduleSearchJob(isSyncJobRunning() ? HISTORY : FULL);
		}
	}

	public void setSearchScope(IDLTKSearchScope scope, boolean refresh) {
		fSearchScope = scope;
		if (!refresh)
			return;
		stop(false, false);
		fLastCompletedFilter = null;
		fLastCompletedResult = null;
		if (fMethodInfoFilter == null) {
			reset();
		} else {
			scheduleSearchJob(isSyncJobRunning() ? HISTORY : FULL);
		}
	}

	public void reset() {
		fLastSelection = null;
		fLastLabels = null;
		fExpectedItemCount = 0;
		fDashLineIndex = -1;
		MethodInfoFilter filter = (fMethodInfoFilter != null) ? fMethodInfoFilter
				: new MethodInfoFilter("*", fSearchScope, fElementKind); //$NON-NLS-1$
		if (VIRTUAL) {
			fHistoryMatches = fHistory.getFilteredTypeInfos(filter);
			fExpectedItemCount = fHistoryMatches.length;
			fTable.setItemCount(fHistoryMatches.length);
			// bug under windows.
			if (fHistoryMatches.length == 0) {
				fTable.redraw();
			}
			fTable.clear(0, fHistoryMatches.length - 1);
		} else {
			fNextElement = 0;
			MethodNameMatch[] historyItems = fHistory
					.getFilteredTypeInfos(filter);
			if (historyItems.length == 0) {
				shortenTable();
				return;
			}
			fExpectedItemCount = historyItems.length;
			int lastIndex = historyItems.length - 1;
			MethodNameMatch last = null;
			MethodNameMatch type = historyItems[0];
			for (int i = 0; i < historyItems.length; i++) {
				MethodNameMatch next = i == lastIndex ? null
						: historyItems[i + 1];
				addSingleElement(type, fLabelProvider.getImageDescriptor(type),
						fLabelProvider.getText(last, type, next));
				last = type;
				type = next;
			}
			shortenTable();
		}
	}

	protected MethodInfoFilter createMethodInfoFilter(String text) {
		if ("**".equals(text)) //$NON-NLS-1$
			text = "*"; //$NON-NLS-1$
		return new MethodInfoFilter(text, fSearchScope, fElementKind);
	}

	private void addPopupMenu() {
		Menu menu = new Menu(fTable.getShell(), SWT.POP_UP);
		fTable.setMenu(menu);
		final MenuItem remove = new MenuItem(menu, SWT.NONE);
		remove.setText(DLTKUIMessages.TypeInfoViewer_remove_from_history);
		menu.addMenuListener(new MenuAdapter() {
			public void menuShown(MenuEvent e) {
				TableItem[] selection = fTable.getSelection();
				remove.setEnabled(canEnable(selection));
			}
		});
		remove.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				deleteHistoryEntry();
			}
		});
	}

	private boolean canEnable(TableItem[] selection) {
		if (selection.length == 0)
			return false;
		for (int i = 0; i < selection.length; i++) {
			TableItem item = selection[i];
			Object data = item.getData();
			if (!(data instanceof MethodNameMatch))
				return false;
			if (!(fHistory.contains((MethodNameMatch) data)))
				return false;
		}
		return true;
	}

	// ---- History management
	// -------------------------------------------------------

	private void deleteHistoryEntry() {
		int index = fTable.getSelectionIndex();
		if (index == -1)
			return;
		TableItem item = fTable.getItem(index);
		Object element = item.getData();
		if (!(element instanceof MethodNameMatch))
			return;
		if (fHistory.remove(element) != null) {
			item.dispose();
			fItems.remove(index);
			int count = fTable.getItemCount();
			if (count > 0) {
				item = fTable.getItem(0);
				if (item.getData() instanceof DashLine) {
					item.dispose();
					fItems.remove(0);
					fDashLineIndex = -1;
					if (count > 1) {
						setTableSelection(0);
					}
				} else {
					if (index >= count) {
						index = count - 1;
					}
					setTableSelection(index);
				}
			} else {
				// send dummy selection
				fTable.notifyListeners(SWT.Selection, new Event());
			}
		}
	}

	// -- Search result updating
	// ----------------------------------------------------

	private void clear(int ticket) {
		syncExec(ticket, new Runnable() {
			public void run() {
				fNextElement = 0;
				fDashLineIndex = -1;
				fLastSelection = null;
				fLastLabels = null;
				fExpectedItemCount = 0;
			}
		});
	}

	private void rememberResult(int ticket, final MethodNameMatch[] result) {
		syncExec(ticket, new Runnable() {
			public void run() {
				if (fLastCompletedResult == null) {
					fLastCompletedFilter = fMethodInfoFilter;
					fLastCompletedResult = result;
				}
			}
		});
	}

	private void addHistory(int ticket, final List elements,
			final List imageDescriptors, final List labels) {
		addAll(ticket, elements, imageDescriptors, labels);
	}

	private void addAll(int ticket, final List elements,
			final List imageDescriptors, final List labels) {
		syncExec(ticket, new Runnable() {
			public void run() {
				int size = elements.size();
				for (int i = 0; i < size; i++) {
					addSingleElement(elements.get(i),
							(ImageDescriptor) imageDescriptors.get(i),
							(String) labels.get(i));
				}
			}
		});
	}

	private void addDashLineAndUpdateLastHistoryEntry(int ticket,
			final MethodNameMatch next) {
		syncExec(ticket, new Runnable() {
			public void run() {
				if (fNextElement > 0) {
					TableItem item = fTable.getItem(fNextElement - 1);
					String label = item.getText();
					String newLabel = fLabelProvider.getText(null,
							(MethodNameMatch) item.getData(), next);
					if (fLastSelection != null
							&& fLastSelection.length > 0
							&& fLastSelection[fLastSelection.length - 1] == item) {
						fLastLabels[fLastLabels.length - 1] = newLabel;
					} else {
						if (newLabel.length() != label.length())
							item.setText(newLabel);
					}
				}
				fDashLineIndex = fNextElement;
				addDashLine();
			}
		});
	}

	private void addDashLine() {
		TableItem item = null;
		if (fItems.size() > fNextElement) {
			item = (TableItem) fItems.get(fNextElement);
		} else {
			item = new TableItem(fTable, SWT.NONE);
			fItems.add(item);
		}
		fillDashLine(item);
		fNextElement++;
	}

	private void addSingleElement(Object element,
			ImageDescriptor imageDescriptor, String label) {
		TableItem item = null;
		Object old = null;
		if (fItems.size() > fNextElement) {
			item = (TableItem) fItems.get(fNextElement);
			old = item.getData();
			item.setForeground(null);
		} else {
			item = new TableItem(fTable, SWT.NONE);
			fItems.add(item);
		}
		item.setData(element);
		item.setImage(fImageManager.get(imageDescriptor));
		if (fNextElement == 0) {
			if (needsSelectionChange(old, element) || fLastSelection != null) {
				item.setText(label);
				fTable.setSelection(0);
				fTable.notifyListeners(SWT.Selection, new Event());
			} else {
				fLastSelection = new TableItem[] { item };
				fLastLabels = new String[] { label };
			}
		} else {
			item.setText(label);
		}
		fNextElement++;
	}

	private boolean needsSelectionChange(Object oldElement, Object newElement) {
		int[] selected = fTable.getSelectionIndices();
		if (selected.length != 1)
			return true;
		if (selected[0] != 0)
			return true;
		if (oldElement == null)
			return true;
		return !oldElement.equals(newElement);
	}

	private void scheduleSearchJob(int mode) {
		fSearchJobTicket++;
		if (fLastCompletedFilter != null
				&& fMethodInfoFilter
						.isSubFilter(fLastCompletedFilter.getText())) {
			fSearchJob = new CachedResultJob(fSearchJobTicket,
					fLastCompletedResult, this, fMethodInfoFilter, fHistory,
					fNumberOfVisibleItems, mode);
		} else {
			fLastCompletedFilter = null;
			fLastCompletedResult = null;
			fSearchJob = new SearchEngineJob(fSearchJobTicket, this,
					fMethodInfoFilter, fHistory, fNumberOfVisibleItems, mode,
					fSearchScope, fElementKind, this.fToolkit);
		}
		fSearchJob.schedule();
	}

	private void searchJobDone(int ticket) {
		syncExec(ticket, new Runnable() {
			public void run() {
				shortenTable();
				checkEmptyList();
				fSearchJob = null;
			}
		});
	}

	private void searchJobCanceled(int ticket, final boolean removePendingItems) {
		syncExec(ticket, new Runnable() {
			public void run() {
				if (removePendingItems) {
					shortenTable();
					checkEmptyList();
				}
				fSearchJob = null;
			}
		});
	}

	private synchronized void searchJobFailed(int ticket, CoreException e) {
		searchJobDone(ticket);
		DLTKUIPlugin.log(e);
	}

	// -- virtual table support
	// -------------------------------------------------------

	private void setHistoryResult(int ticket, final MethodNameMatch[] types) {
		syncExec(ticket, new Runnable() {
			public void run() {
				fExpectedItemCount = types.length;
				int lastHistoryLength = fHistoryMatches.length;
				fHistoryMatches = types;
				int length = fHistoryMatches.length + fSearchMatches.length;
				int dash = (fHistoryMatches.length > 0 && fSearchMatches.length > 0) ? 1
						: 0;
				fTable.setItemCount(length + dash);
				if (length == 0) {
					// bug under windows.
					fTable.redraw();
					return;
				}
				int update = Math
						.max(lastHistoryLength, fHistoryMatches.length);
				if (update > 0) {
					fTable.clear(0, update + dash - 1);
				}
			}
		});
	}

	private void setSearchResult(int ticket, final MethodNameMatch[] types) {
		syncExec(ticket, new Runnable() {
			public void run() {
				fExpectedItemCount += types.length;
				fSearchMatches = types;
				int length = fHistoryMatches.length + fSearchMatches.length;
				int dash = (fHistoryMatches.length > 0 && fSearchMatches.length > 0) ? 1
						: 0;
				fTable.setItemCount(length + dash);
				if (length == 0) {
					// bug under windows.
					fTable.redraw();
					return;
				}
				if (fHistoryMatches.length == 0) {
					fTable.clear(0, length + dash - 1);
				} else {
					fTable.clear(fHistoryMatches.length - 1, length + dash - 1);
				}
			}
		});
	}

	private void setData(TableItem item) {
		int index = fTable.indexOf(item);
		MethodNameMatch type = getTypeInfo(index);
		if (type == DASH_LINE) {
			item.setData(fDashLine);
			fillDashLine(item);
		} else {
			item.setData(type);
			item.setImage(fImageManager.get(fLabelProvider
					.getImageDescriptor(type)));
			item.setText(fLabelProvider.getText(getTypeInfo(index - 1), type,
					getTypeInfo(index + 1)));
			item.setForeground(null);
		}
	}

	private MethodNameMatch getTypeInfo(int index) {
		if (index < 0)
			return null;
		if (index < fHistoryMatches.length) {
			return fHistoryMatches[index];
		}
		int dash = (fHistoryMatches.length > 0 && fSearchMatches.length > 0) ? 1
				: 0;
		if (index == fHistoryMatches.length && dash == 1) {
			return DASH_LINE;
		}
		index = index - fHistoryMatches.length - dash;
		if (index >= fSearchMatches.length)
			return null;
		return fSearchMatches[index];
	}

	// -- Sync Job updates
	// ------------------------------------------------------------

	private void scheduleSyncJob() {
		fSyncJob = new SyncJob(this, this.fToolkit.getCoreToolkit());
		fSyncJob.schedule();
	}

	private void syncJobDone() {
		syncExec(new Runnable() {
			public void run() {
				fSyncJob = null;
				if (fMethodInfoFilter != null) {
					scheduleSearchJob(FULL);
				}
			}
		});
	}

	private boolean isSyncJobRunning() {
		return fSyncJob != null;
	}

	// -- progress monitor updates
	// -----------------------------------------------------

	private void scheduleProgressUpdateJob() {
		syncExec(new Runnable() {
			public void run() {
				if (fProgressCounter == 0) {
					clearProgressMessage();
					fProgressUpdateJob = new ProgressUpdateJob(fDisplay,
							MethodInfoViewer.this);
					fProgressUpdateJob.schedule(300);
				}
				fProgressCounter++;
			}
		});
	}

	private void stopProgressUpdateJob() {
		syncExec(new Runnable() {
			public void run() {
				fProgressCounter--;
				if (fProgressCounter == 0 && fProgressUpdateJob != null) {
					fProgressUpdateJob.stop();
					fProgressUpdateJob = null;
					clearProgressMessage();
				}
			}
		});
	}

	private void setProgressMessage(String message) {
		fProgressMessage = message;
	}

	private void clearProgressMessage() {
		fProgressMessage = ""; //$NON-NLS-1$
		fProgressLabel.setText(fProgressMessage);
	}

	private void updateProgressMessage() {
		fProgressLabel.setText(fProgressMessage);
	}

	// -- Helper methods
	// --------------------------------------------------------------

	private void syncExec(final Runnable runnable) {
		if (fDisplay.isDisposed())
			return;
		fDisplay.syncExec(new Runnable() {
			public void run() {
				if (fTable.isDisposed())
					return;
				runnable.run();
			}
		});
	}

	private void syncExec(final int ticket, final Runnable runnable) {
		if (fDisplay.isDisposed())
			return;
		fDisplay.syncExec(new Runnable() {
			public void run() {
				if (fTable.isDisposed() || ticket != fSearchJobTicket)
					return;
				runnable.run();
			}
		});
	}

	private void fillDashLine(TableItem item) {
		Rectangle bounds = item.getImageBounds(0);
		Rectangle area = fTable.getBounds();
		boolean willHaveScrollBar = fExpectedItemCount + 1 > fNumberOfVisibleItems;
		item
				.setText(fDashLine.getText(area.width - bounds.x - bounds.width
						- fTableWidthDelta
						- (willHaveScrollBar ? fScrollbarWidth : 0)));
		item.setImage(fSeparatorIcon);
		item.setForeground(fDashLineColor);
		item.setData(fDashLine);
	}

	private void shortenTable() {
		if (VIRTUAL)
			return;
		if (fNextElement < fItems.size()) {
			fTable.setRedraw(false);
			fTable.remove(fNextElement, fItems.size() - 1);
			fTable.setRedraw(true);
		}
		for (int i = fItems.size() - 1; i >= fNextElement; i--) {
			fItems.remove(i);
		}
	}

	private void checkEmptyList() {
		if (fTable.getItemCount() == 0) {
			fTable.notifyListeners(SWT.Selection, new Event());
		}
	}

	private void setTableSelection(int index) {
		fTable.setSelection(index);
		fTable.notifyListeners(SWT.Selection, new Event());
	}

	private Color computeDashLineColor() {
		Color fg = fTable.getForeground();
		int fGray = (int) (0.3 * fg.getRed() + 0.59 * fg.getGreen() + 0.11 * fg
				.getBlue());
		Color bg = fTable.getBackground();
		int bGray = (int) (0.3 * bg.getRed() + 0.59 * bg.getGreen() + 0.11 * bg
				.getBlue());
		int gray = (int) ((fGray + bGray) * 0.66);
		return new Color(fDisplay, gray, gray, gray);
	}

	private int computeScrollBarWidth() {
		Composite t = new Composite(fTable.getShell(), SWT.V_SCROLL);
		int result = t.computeTrim(0, 0, 0, 0).width;
		t.dispose();
		return result;
	}

	private String getQualifiedText(MethodNameMatch type) {
		return fFullyQualifySelection ? fLabelProvider
				.getFullyQualifiedText(type) : fLabelProvider
				.getQualifiedText(type);
	}
}
