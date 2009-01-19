/*******************************************************************************
 * Copyright (c) 2009 xored software, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     xored software, Inc. - initial API and Implementation (Alex Panchenko)
 *******************************************************************************/
package org.eclipse.dltk.ui.wizards;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.dltk.core.environment.IEnvironment;
import org.eclipse.dltk.core.environment.IFileHandle;
import org.eclipse.dltk.internal.ui.wizards.NewWizardMessages;
import org.eclipse.dltk.ui.util.PixelConverter;
import org.eclipse.dltk.ui.wizards.LinkedProjectModel.ProjectFolder;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.model.IWorkbenchAdapter;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.model.WorkbenchViewerComparator;

public class ProjectWizardSelectLinkedFoldersPage extends WizardPage implements
		ILocationGroup.Listener, ICheckStateListener,
		LinkedProjectModel.IFolderProvider {

	/**
	 * Checked folders and files. The key is FileElement, value is
	 * List<FileElement>.
	 */
	private final Map checkedItems = new HashMap();

	private static class FileElement extends PlatformObject implements
			IAdaptable, IWorkbenchAdapter {
		protected final DirectoryElement parent;
		protected final IFileHandle fileHandle;

		public FileElement(DirectoryElement parent, IFileHandle fileHandle) {
			this.parent = parent;
			this.fileHandle = fileHandle;
		}

		public boolean isDirectory() {
			return false;
		}

		public DirectoryElement getParent() {
			return parent;
		}

		public FileElement[] getFolders() {
			return EMPTY_LIST;
		}

		public FileElement[] getFiles() {
			return EMPTY_LIST;
		}

		public boolean hasChildren() {
			return false;
		}

		public Object getAdapter(Class adapter) {
			if (adapter == IWorkbenchAdapter.class) {
				return this;
			}
			return super.getAdapter(adapter);
		}

		public Object[] getChildren(Object o) {
			return getFolders();
		}

		public ImageDescriptor getImageDescriptor(Object object) {
			return PlatformUI.getWorkbench().getEditorRegistry()
					.getImageDescriptor(fileHandle.getName());
		}

		public String getLabel(Object o) {
			return fileHandle.getName();
		}

		public Object getParent(Object o) {
			return parent;
		}

		public IPath getPath() {
			return fileHandle.getPath();
		}

		public int hashCode() {
			return fileHandle.hashCode();
		}

		public boolean equals(Object obj) {
			if (obj instanceof FileElement) {
				final FileElement other = (FileElement) obj;
				return fileHandle.equals(other.fileHandle);
			}
			return false;
		}
	}

	private static final FileElement[] EMPTY_LIST = new FileElement[0];

	private static class DirectoryElement extends FileElement {

		private FileElement[] directories;
		private FileElement[] files;

		public DirectoryElement(DirectoryElement parent, IFileHandle fileHandle) {
			super(parent, fileHandle);
		}

		public boolean isDirectory() {
			return true;
		}

		public FileElement[] getFolders() {
			if (directories == null) {
				populate();
			}
			return directories;
		}

		public FileElement[] getFiles() {
			if (files == null) {
				populate();
			}
			return files;
		}

		public boolean hasChildren() {
			return directories == null ? true : directories.length != 0;
		}

		private void populate() {
			final IFileHandle[] children = fileHandle.getChildren();
			if (children != null) {
				final List directoryElements = new ArrayList();
				final List fileElements = new ArrayList();
				for (int i = 0; i < children.length; ++i) {
					final IFileHandle child = children[i];
					if (child.isDirectory()) {
						directoryElements
								.add(new DirectoryElement(this, child));
					} else {
						fileElements.add(new FileElement(this, child));
					}
				}
				directories = (FileElement[]) directoryElements
						.toArray(new FileElement[directoryElements.size()]);
				files = (FileElement[]) fileElements
						.toArray(new FileElement[fileElements.size()]);
			} else {
				directories = new FileElement[0];
				files = new FileElement[0];
			}
		}

		public ImageDescriptor getImageDescriptor(Object arg0) {
			return PlatformUI.getWorkbench().getSharedImages()
					.getImageDescriptor(ISharedImages.IMG_OBJ_FOLDER);
		}

	}

	private class ProjectRootElement extends PlatformObject implements
			IAdaptable {

		private final DirectoryElement directory;

		public ProjectRootElement(DirectoryElement directory) {
			this.directory = directory;
		}

		public Object getAdapter(Class adapter) {
			if (adapter == IWorkbenchAdapter.class) {
				return directory;
			}
			return super.getAdapter(adapter);
		}

	}

	private class DirectoryContentProvider implements ITreeContentProvider {

		public Object[] getElements(Object inputElement) {
			if (inputElement instanceof DirectoryElement) {
				return new ProjectRootElement[] { new ProjectRootElement(
						(DirectoryElement) inputElement) };
			} else {
				return EMPTY_LIST;
			}
		}

		public void dispose() {
			// 
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			// 
		}

		public Object[] getChildren(Object parentElement) {
			if (parentElement instanceof ProjectRootElement) {
				return ((ProjectRootElement) parentElement).directory
						.getFolders();
			}
			if (parentElement instanceof FileElement) {
				return ((FileElement) parentElement).getFolders();
			}
			return EMPTY_LIST;
		}

		public Object getParent(Object element) {
			if (element instanceof FileElement) {
				return ((FileElement) element).parent;
			}
			return null;
		}

		public boolean hasChildren(Object element) {
			if (element instanceof ProjectRootElement) {
				return ((ProjectRootElement) element).directory.hasChildren();
			} else if (element instanceof FileElement) {
				return ((FileElement) element).hasChildren();
			} else {
				return false;
			}
		}

	}

	private class FileContentProvider implements IStructuredContentProvider {

		public Object[] getElements(Object inputElement) {
			if (inputElement instanceof ProjectRootElement) {
				return ((ProjectRootElement) inputElement).directory.getFiles();
			} else if (inputElement instanceof FileElement) {
				return ((FileElement) inputElement).getFiles();
			}
			return EMPTY_LIST;
		}

		public void dispose() {
			// empty
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			// empty
		}

	}

	private static final String PAGE_NAME = "SelectLinkedFolders"; //$NON-NLS-1$

	private final ILocationGroup locationGroup;

	public ProjectWizardSelectLinkedFoldersPage(ILocationGroup locationGroup) {
		super(PAGE_NAME);
		this.locationGroup = locationGroup;
		setTitle(Messages.SelectFolders_title);
		setDescription(Messages.SelectFolders_description);
	}

	private Label projectNameValue;
	private Label environmentValue;
	private Label directoryValue;

	private CheckboxTreeViewer directoryViewer;
	private CheckboxTableViewer fileViewer;

	public void createControl(Composite parent) {
		final Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout());

		final Composite directoryComposite = new Composite(composite, SWT.NONE);
		directoryComposite
				.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		directoryComposite.setLayout(new GridLayout(2, false));

		final Label projectNameLabel = new Label(directoryComposite, SWT.NONE);
		projectNameLabel
				.setText(NewWizardMessages.ScriptProjectWizardFirstPage_NameGroup_label_text);
		projectNameValue = new Label(directoryComposite, SWT.BORDER);
		projectNameValue.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		final Label environmentLabel = new Label(directoryComposite, SWT.NONE);
		environmentLabel.setText(Messages.LinkedFolders_environment_label);
		environmentValue = new Label(directoryComposite, SWT.BORDER);
		environmentValue.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		final Label directoryLabel = new Label(directoryComposite, SWT.NONE);
		directoryLabel.setText(Messages.LinkedFolders_directory_label);
		directoryValue = new Label(directoryComposite, SWT.BORDER);
		directoryValue.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		final SashForm listingSash = new SashForm(composite, SWT.NONE);
		listingSash.setLayoutData(createViewerLayoutData(listingSash));
		final WorkbenchViewerComparator comparator = new WorkbenchViewerComparator();
		directoryViewer = new CheckboxTreeViewer(listingSash, SWT.BORDER);
		directoryViewer.setContentProvider(new DirectoryContentProvider());
		directoryViewer.setLabelProvider(new WorkbenchLabelProvider());
		directoryViewer.setComparator(comparator);
		directoryViewer.getTree().setLayoutData(
				new GridData(GridData.FILL_BOTH));
		directoryViewer
				.addSelectionChangedListener(new ISelectionChangedListener() {
					public void selectionChanged(SelectionChangedEvent event) {
						directoryChanged(event.getSelection());
					}
				});
		directoryViewer.addCheckStateListener(this);
		fileViewer = CheckboxTableViewer.newCheckList(listingSash, SWT.BORDER);
		fileViewer.setContentProvider(new FileContentProvider());
		fileViewer.setLabelProvider(new WorkbenchLabelProvider());
		fileViewer.setComparator(comparator);
		fileViewer.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));
		fileViewer.addCheckStateListener(this);
		listingSash.setWeights(new int[] { 60, 40 });

		setControl(composite);

		updateListing();

		locationGroup.addLocationListener(this);
		updateState();
	}

	private GridData createViewerLayoutData(Control control) {
		final GridData gd = new GridData(GridData.FILL_BOTH);
		gd.heightHint = new PixelConverter(control)
				.convertHeightInCharsToPixels(20);
		return gd;
	}

	private void directoryChanged(ISelection selection) {
		if (!selection.isEmpty() && selection instanceof IStructuredSelection) {
			final Object directory = ((IStructuredSelection) selection)
					.getFirstElement();
			fileViewer.setInput(directory);
			final List checkedFiles = (List) checkedItems.get(directory);
			if (checkedFiles != null) {
				for (Iterator i = checkedFiles.iterator(); i.hasNext();) {
					fileViewer.setChecked(i.next(), true);
				}
			}
		} else {
			fileViewer.setInput(null);
		}
	}

	private void updateListing() {
		projectNameValue.setText(locationGroup.getProjectName());
		final IEnvironment environment = locationGroup.getEnvironment();
		environmentValue.setText(environment.getName());
		final IPath location = locationGroup.getLocation();
		directoryValue.setText(location.toString());
		final DirectoryElement input = new DirectoryElement(null, environment
				.getFile(location));
		directoryViewer.setInput(input);
		fileViewer.setInput(input);
	}

	public void update(ILocationGroup o, Object arg) {
		updateListing();
	}

	public void checkStateChanged(CheckStateChangedEvent event) {
		if (event.getElement() instanceof FileElement) {
			final FileElement element = (FileElement) event.getElement();
			if (event.getCheckable() == directoryViewer) {
				directoryItemChecked(element, event.getChecked());
			} else if (event.getCheckable() == fileViewer) {
				fileItemChecked(element, event.getChecked());
			}
		} else if (event.getElement() instanceof ProjectRootElement) {
			final FileElement element = ((ProjectRootElement) event
					.getElement()).directory;
			if (event.getCheckable() == directoryViewer) {
				directoryItemChecked(element, event.getChecked());
			}
		}
	}

	private void fileItemChecked(FileElement element, boolean checked) {
		final DirectoryElement directory = element.getParent();
		if (directory == null)
			return;
		List selectedFiles = (List) checkedItems.get(directory);
		if (checked) {
			if (selectedFiles == null) {
				selectedFiles = new ArrayList();
				checkedItems.put(directory, selectedFiles);
			}
			if (!selectedFiles.contains(element)) {
				selectedFiles.add(element);
			}
		} else if (selectedFiles != null) {
			selectedFiles.remove(element);
			if (selectedFiles.isEmpty()) {
				checkedItems.remove(directory);
			}
		}
	}

	private static class FolderEntry {

		final IPath path;
		final DirectoryElement directory;

		/**
		 * @param path
		 * @param directory
		 */
		public FolderEntry(IPath path, DirectoryElement directory) {
			this.path = path;
			this.directory = directory;
		}
	}

	private static class FolderEntryComparator implements Comparator {

		public int compare(Object arg0, Object arg1) {
			FolderEntry entry0 = (FolderEntry) arg0;
			FolderEntry entry1 = (FolderEntry) arg1;
			return entry0.path.toString().compareTo(entry1.path.toString());
		}

	}

	public ProjectFolder[] getFolders() {
		return selectedFolders;
	}

	private void updateSelectedFolders() {
		final Object[] checkedFolders = directoryViewer.getCheckedElements();
		final List folderEntries = new ArrayList();
		for (int i = 0; i < checkedFolders.length; ++i) {
			final DirectoryElement element;
			if (checkedFolders[i] instanceof ProjectRootElement) {
				element = ((ProjectRootElement) checkedFolders[i]).directory;
			} else if (checkedFolders[i] instanceof DirectoryElement) {
				element = (DirectoryElement) checkedFolders[i];
			} else {
				continue;
			}
			folderEntries.add(new FolderEntry(element.getPath(), element));
		}
		Collections.sort(folderEntries, new FolderEntryComparator());
		final List topLevel = new ArrayList();
		int i = 0;
		while (i < folderEntries.size()) {
			final FolderEntry entry = (FolderEntry) folderEntries.get(i);
			// final int startIndex = i;
			++i;
			while (i < folderEntries.size()
					&& entry.path.isPrefixOf(((FolderEntry) folderEntries
							.get(i)).path)) {
				++i;
			}
			// final List children = folderEntries.subList(startIndex, i);
			final ProjectFolder folder = new ProjectFolder();
			folder.setPath(entry.path);
			// TODO include/exclude patterns
			topLevel.add(folder);
		}
		this.selectedFolders = new ProjectFolder[topLevel.size()];
		topLevel.toArray(this.selectedFolders);
		selectLocalNames(this.selectedFolders, locationGroup.getLocation());
	}

	private ProjectFolder[] selectedFolders;

	private static interface ILocalFolderNameStrategy {
		String evaluate(IPath path);
	}

	private static class LastSegmentLocalFolderNameStrategy implements
			ILocalFolderNameStrategy {

		public String evaluate(IPath path) {
			return path.segment(path.segmentCount() - 1);
		}

	}

	private static class FullPathLocalFolderNameStrategy implements
			ILocalFolderNameStrategy {

		private final int baseSegmentCount;

		public FullPathLocalFolderNameStrategy(IPath base) {
			this.baseSegmentCount = base.segmentCount();
		}

		public String evaluate(IPath path) {
			StringBuffer sb = new StringBuffer();
			for (int i = baseSegmentCount; i < path.segmentCount(); ++i) {
				if (sb.length() != 0) {
					sb.append('_');
				}
				sb.append(path.segment(i));
			}
			return sb.toString();
		}

	}

	/**
	 * @param folders
	 * @param base
	 */
	private void selectLocalNames(ProjectFolder[] folders, IPath base) {
		final Set currentNames = new HashSet();
		for (int i = 0; i < folders.length; ++i) {
			final ProjectFolder folder = folders[i];
			if (!folder.isEmptyLocalFolderName()) {
				currentNames.add(folder.getLocalFolderName());
			}
		}
		if (tryLocalFolderNameStrategy(folders, currentNames,
				new LastSegmentLocalFolderNameStrategy())) {
			return;
		}
		if (tryLocalFolderNameStrategy(folders, currentNames,
				new FullPathLocalFolderNameStrategy(base))) {
			return;
		}
	}

	private boolean tryLocalFolderNameStrategy(ProjectFolder[] folders,
			Set currentNames, ILocalFolderNameStrategy strategy) {
		final Set names = new HashSet(currentNames);
		for (int i = 0; i < folders.length; ++i) {
			final ProjectFolder folder = folders[i];
			if (folder.isEmptyLocalFolderName()) {
				if (!names.add(strategy.evaluate(folder.getPath()))) {
					return false;
				}
			}
		}
		for (int i = 0; i < folders.length; ++i) {
			final ProjectFolder folder = folders[i];
			if (folder.isEmptyLocalFolderName()) {
				folder.setLocalFolderName(strategy.evaluate(folder.getPath()));
			}
		}
		return true;
	}

	private void directoryItemChecked(FileElement element, boolean checked) {
		updateSelectedFolders();
		updateState();
	}

	private void updateState() {
		setPageComplete(selectedFolders != null && selectedFolders.length != 0);
	}

}
