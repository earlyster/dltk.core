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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.dltk.compiler.util.Util;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IBuildpathEntry;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.environment.EnvironmentManager;
import org.eclipse.dltk.core.environment.EnvironmentPathUtils;
import org.eclipse.dltk.core.environment.IEnvironment;
import org.eclipse.dltk.internal.ui.wizards.NewWizardMessages;
import org.eclipse.dltk.ui.DLTKPluginImages;
import org.eclipse.dltk.ui.util.ExceptionHandler;
import org.eclipse.dltk.ui.wizards.LinkedProjectModel.ProjectFolder;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.model.WorkbenchViewerComparator;

public class ProjectWizardConfigureLinkedFoldersPage extends WizardPage
		implements IProjectWizardLastPage {

	private static class ProjectNameItem {

		final LinkedProjectModel model;

		public ProjectNameItem(LinkedProjectModel model) {
			this.model = model;
		}

	}

	private class FolderContentProvider implements ITreeContentProvider {

		public Object[] getChildren(Object parentElement) {
			if (parentElement instanceof ProjectNameItem) {
				return ((ProjectNameItem) parentElement).model.getFolders();
			}
			return new Object[0];
		}

		public Object getParent(Object element) {
			return null;
		}

		public boolean hasChildren(Object element) {
			return element instanceof LinkedProjectModel
					|| element instanceof ProjectNameItem
					|| element instanceof ProjectFolder;
		}

		public Object[] getElements(Object inputElement) {
			if (inputElement instanceof LinkedProjectModel) {
				return new Object[] { new ProjectNameItem(
						(LinkedProjectModel) inputElement) };
			}
			return new Object[] { inputElement };
		}

		public void dispose() {
			// 
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			// 
		}

	}

	private class FolderLabelProvider extends LabelProvider implements
			ITableLabelProvider {

		public String getText(Object element) {
			return getColumnText(element, 0);
		}

		public Image getColumnImage(Object element, int columnIndex) {
			if (columnIndex == 0) {
				if (element instanceof ProjectFolder) {
					switch (((ProjectFolder) element).getKind()) {
					case ProjectFolder.KIND_SOURCE:
						return DLTKPluginImages
								.get(DLTKPluginImages.IMG_OBJS_PACKFRAG_ROOT);
					case ProjectFolder.KIND_RESOURCE:
						return PlatformUI.getWorkbench().getSharedImages()
								.getImage(ISharedImages.IMG_OBJ_FOLDER);
					case ProjectFolder.KIND_EXTERNAL:
						return DLTKPluginImages
								.get(DLTKPluginImages.IMG_OBJS_EXTJAR);
					}

				} else if (element instanceof ProjectNameItem) {
					return PlatformUI.getWorkbench().getSharedImages()
							.getImage(IDE.SharedImages.IMG_OBJ_PROJECT);
				}
			}
			return null;
		}

		public String getColumnText(Object element, int columnIndex) {
			if (element instanceof ProjectNameItem) {
				if (columnIndex == 0) {
					return locationGroup.getProjectName();
				}
			} else if (element instanceof ProjectFolder) {
				final ProjectFolder folder = (ProjectFolder) element;
				switch (columnIndex) {
				case 0:
					return folder.getPath().setDevice(null)
							.removeFirstSegments(
									locationGroup.getLocation().segmentCount())
							.toString();
				case 1:
					return ProjectFolder.describeKind(folder.getKind());
				case 2:
					if (folder.getKind() == ProjectFolder.KIND_SOURCE
							|| folder.getKind() == ProjectFolder.KIND_RESOURCE) {
						return folder.getLocalFolderName();
					} else {
						return Util.EMPTY_STRING;
					}
				}
			}
			if (columnIndex == 0) {
				return element.toString();
			}
			return Util.EMPTY_STRING;
		}

	}

	private static class FolderKindEditingSupport extends EditingSupport {

		public FolderKindEditingSupport(ColumnViewer viewer) {
			super(viewer);
		}

		protected boolean canEdit(Object element) {
			return element instanceof ProjectFolder;
		}

		protected CellEditor getCellEditor(Object element) {
			return new FolderKindComboBoxCellEditor((Composite) getViewer()
					.getControl());
		}

		protected Object getValue(Object element) {
			if (element instanceof ProjectFolder) {
				return new Integer(((ProjectFolder) element).getKind());
			}
			return null;
		}

		protected void setValue(Object element, Object value) {
			if (element instanceof ProjectFolder && value instanceof Integer) {
				final ProjectFolder folder = (ProjectFolder) element;
				final int kind = ((Integer) value).intValue();
				if (ProjectFolder.isValidKind(kind)) {
					folder.setKind(kind);
					getViewer().refresh(folder);
				}
			}
		}

	}

	private static class FolderKindComboBoxCellEditor extends
			ComboBoxCellEditor {

		private static final int[] KINDS = new int[] {
				ProjectFolder.KIND_SOURCE, ProjectFolder.KIND_RESOURCE,
				ProjectFolder.KIND_EXTERNAL };

		private static String[] createKindItems() {
			final String[] items = new String[KINDS.length];
			for (int i = 0; i < KINDS.length; ++i) {
				items[i] = ProjectFolder.describeKind(KINDS[i]);
			}
			return items;
		}

		public FolderKindComboBoxCellEditor(Composite parent) {
			super(parent, createKindItems(), SWT.READ_ONLY);
		}

		private CCombo combobox;

		protected Control createControl(Composite parent) {
			return combobox = (CCombo) super.createControl(parent);
		}

		protected void doSetValue(Object value) {
			if (value instanceof Integer) {
				for (int i = 0; i < KINDS.length; ++i) {
					if (KINDS[i] == ((Integer) value).intValue()) {
						combobox.select(i);
						break;
					}
				}
			}
		}

		protected Object doGetValue() {
			final int index = combobox.getSelectionIndex();
			if (index >= 0 && index < KINDS.length) {
				return new Integer(KINDS[index]);
			} else {
				return new Integer(ProjectFolder.KIND_SOURCE);
			}
		}

	}

	private static final String PAGE_NAME = "ConfigureLinkedFolders"; //$NON-NLS-1$

	private final String natureId;
	private final ILocationGroup locationGroup;
	private final LinkedProjectModel.IFolderProvider folderProvider;

	public ProjectWizardConfigureLinkedFoldersPage(String natureId,
			ILocationGroup locationGroup,
			LinkedProjectModel.IFolderProvider folderProvider) {
		super(PAGE_NAME);
		this.natureId = natureId;
		this.locationGroup = locationGroup;
		this.folderProvider = folderProvider;
		setTitle(Messages.ConfigureFolders_title);
		setDescription(Messages.ConfigureFolders_description);
	}

	private TreeViewer folderViewer;
	private Label environmentValue;
	private Label directoryValue;

	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout());
		final Composite directoryComposite = new Composite(composite, SWT.NONE);
		directoryComposite
				.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		directoryComposite.setLayout(new GridLayout(2, false));

		final Label environmentLabel = new Label(directoryComposite, SWT.NONE);
		environmentLabel.setText(Messages.LinkedFolders_environment_label);
		environmentValue = new Label(directoryComposite, SWT.BORDER);
		environmentValue.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		final Label directoryLabel = new Label(directoryComposite, SWT.NONE);
		directoryLabel.setText(Messages.LinkedFolders_directory_label);
		directoryValue = new Label(directoryComposite, SWT.BORDER);
		directoryValue.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		folderViewer = new TreeViewer(composite, SWT.BORDER
				| SWT.FULL_SELECTION);
		final Tree tree = folderViewer.getTree();
		tree.setLayoutData(new GridData(GridData.FILL_BOTH));
		tree.setHeaderVisible(true);

		TreeColumn column1 = new TreeColumn(tree, SWT.LEFT);
		column1.setText(Messages.ConfigureFolders_pathColumn);
		column1.setResizable(true);
		column1.setWidth(150);

		TreeColumn column2 = new TreeColumn(tree, SWT.LEFT);
		column2.setText(Messages.ConfigureFolders_typeColumn);
		column2.setResizable(true);
		column2.setWidth(75);

		new TreeViewerColumn(folderViewer, column2)
				.setEditingSupport(new FolderKindEditingSupport(folderViewer));

		TreeColumn column3 = new TreeColumn(tree, SWT.LEFT);
		column3.setText(Messages.ConfigureFolders_localNameColumn);
		column3.setResizable(true);
		column3.setWidth(75);

		folderViewer.setContentProvider(new FolderContentProvider());
		folderViewer.setLabelProvider(new FolderLabelProvider());
		folderViewer.setComparator(new WorkbenchViewerComparator());

		setControl(composite);
	}

	private void resizeTreeColumns() {
		final Tree tree = folderViewer.getTree();
		final int width = tree.getSize().x - tree.getVerticalBar().getSize().x
				- 8;
		tree.getColumn(0).setWidth(width / 2);
		tree.getColumn(1).setWidth(width / 4);
		tree.getColumn(2).setWidth(width / 4);
	}

	public void setVisible(boolean visible) {
		if (visible) {
			resizeTreeColumns();
			updateModel();
			if (folderViewer.getInput() != model) {
				folderViewer.setInput(model);
			} else {
				folderViewer.refresh();
			}
			folderViewer.expandToLevel(2);
		} else {
			removeProject();
		}
		super.setVisible(visible);
	}

	private LinkedProjectModel model = null;

	private void updateModel() {
		final IEnvironment environment = locationGroup.getEnvironment();
		final IPath location = locationGroup.getLocation();
		if (model == null || !model.matches(environment, location)) {
			environmentValue.setText(environment.getName());
			directoryValue.setText(location.toString());
			final LinkedProjectModel newModel = new LinkedProjectModel(
					environment, location);
			updateFolders(newModel);
			model = newModel;
		} else {
			updateFolders(model);
		}
	}

	/**
	 * @param projectModel
	 */
	private void updateFolders(LinkedProjectModel projectModel) {
		final Map prevFolderMap = new HashMap();
		final ProjectFolder[] prevFolders = projectModel.getFolders();
		for (int i = 0; i < prevFolders.length; ++i) {
			final ProjectFolder folder = prevFolders[i];
			prevFolderMap.put(folder.getPath(), folder);
		}
		final ProjectFolder[] folders = folderProvider.getFolders();
		for (int i = 0; i < folders.length; ++i) {
			final ProjectFolder sourceFolder = folders[i];
			final ProjectFolder destFolder = (ProjectFolder) prevFolderMap
					.remove(sourceFolder.getPath());
			if (destFolder == null) {
				projectModel.addFolder(sourceFolder);
			}
		}
		for (Iterator i = prevFolderMap.values().iterator(); i.hasNext();) {
			projectModel.removeFolder((ProjectFolder) i.next());
		}
	}

	private IProject fProject;
	private IScriptProject fScriptProject;
	private boolean fKeepContent;

	/**
	 * @param monitor
	 * @throws CoreException
	 */
	public void performFinish(IProgressMonitor monitor) throws CoreException {
		updateModel();
		fKeepContent = locationGroup.getDetect();
		fProject = locationGroup.getProjectHandle();
		if (monitor == null) {
			monitor = new NullProgressMonitor();
		}
		try {
			monitor
					.beginTask(
							NewWizardMessages.ScriptProjectWizardSecondPage_operation_initialize,
							70);
			if (monitor.isCanceled()) {
				throw new OperationCanceledException();
			}
			try {
				final URI rootLocation = ResourcesPlugin.getWorkspace()
						.getRoot().getLocationURI();
				final URI realLocation = new URI(rootLocation.getScheme(),
						null, Path.fromPortableString(rootLocation.getPath())
								.append(fProject.getName()).toString(), null);
				rememberExistingFiles(realLocation);
			} catch (URISyntaxException e) {
				Assert.isTrue(false, "Can't happen"); //$NON-NLS-1$
			}
			BuildpathsBlock.createProject(fProject, null,
					new SubProgressMonitor(monitor, 20));
			final IEnvironment environment = model.getEnvironment();
			fProject
					.setPersistentProperty(
							EnvironmentManager.PROJECT_ENVIRONMENT, environment
									.getId());
			final ProjectFolder[] folders = model.getFolders();
			createLinkedFolders(fProject, environment, folders,
					new SubProgressMonitor(monitor, 20));
			final List cpEntries = new ArrayList();
			for (int i = 0; i < folders.length; ++i) {
				final ProjectFolder folder = folders[i];
				if (folder.getKind() == ProjectFolder.KIND_SOURCE) {
					final IPath sourcePath = new Path(fProject.getName())
							.append(folder.getLocalFolderName()).makeAbsolute();
					cpEntries.add(DLTKCore.newSourceEntry(sourcePath));
				} else if (folder.getKind() == ProjectFolder.KIND_EXTERNAL) {
					final IPath externalPath = EnvironmentPathUtils
							.getFullPath(environment, folder.getPath());
					cpEntries.add(DLTKCore.newExtLibraryEntry(externalPath));
				}
			}
			cpEntries.addAll(ProjectWizardUtils
					.getDefaultBuildpathEntry(locationGroup));
			monitor.worked(20);
			if (monitor.isCanceled()) {
				throw new OperationCanceledException();
			}
			BuildpathsBlock.addScriptNature(fProject, new SubProgressMonitor(
					monitor, 1), natureId);
			final IBuildpathEntry[] entries = (IBuildpathEntry[]) cpEntries
					.toArray(new IBuildpathEntry[cpEntries.size()]);
			fScriptProject = DLTKCore.create(fProject);
			fScriptProject.setRawBuildpath(entries, new SubProgressMonitor(
					monitor, 2));
		} finally {
			monitor.done();
		}
	}

	private static void createLinkedFolders(IProject project,
			IEnvironment environment, ProjectFolder[] folders,
			IProgressMonitor monitor) throws CoreException {
		int linkCount = ProjectFolder.countLinked(folders);
		monitor.beginTask(Messages.LinkedFolders_initializingFolders_taskName,
				linkCount);
		for (int i = 0; i < folders.length; ++i) {
			final ProjectFolder projectFolder = folders[i];
			if (projectFolder.getKind() == ProjectFolder.KIND_SOURCE
					|| projectFolder.getKind() == ProjectFolder.KIND_RESOURCE) {
				final IFolder folder = project.getFolder(projectFolder
						.getLocalFolderName());
				folder.createLink(environment.getFile(projectFolder.getPath())
						.toURI(), IResource.REPLACE, new SubProgressMonitor(
						monitor, 1));
			}
		}
	}

	private ProjectMetadataBackup projectMetadataBackup = null;

	/**
	 * @param realLocation
	 * @throws CoreException
	 */
	private void rememberExistingFiles(URI realLocation) throws CoreException {
		projectMetadataBackup = new ProjectMetadataBackup();
		projectMetadataBackup.backup(realLocation, new String[] {
				ProjectWizardUtils.FILENAME_PROJECT,
				ProjectWizardUtils.FILENAME_BUILDPATH });
	}

	/*
	 * @see IProjectWizardLastPage#getScriptProject()
	 */
	public IScriptProject getScriptProject() {
		return fScriptProject;
	}

	/*
	 * @see IProjectWizardLastPage#performCancel()
	 */
	public void performCancel() {
		removeProject();
	}

	private void removeProject() {
		if (fProject != null) {
			try {
				final URI projLoc = fProject.getLocationURI();
				final boolean removeContent = !fKeepContent
						&& fProject.isSynchronized(IResource.DEPTH_INFINITE);
				fProject.delete(removeContent, new NullProgressMonitor());
				if (projectMetadataBackup != null) {
					projectMetadataBackup.restore(projLoc,
							new NullProgressMonitor());
				}
			} catch (CoreException e) {
				final String title = NewWizardMessages.ScriptProjectWizardSecondPage_error_remove_title;
				final String message = NewWizardMessages.ScriptProjectWizardSecondPage_error_remove_message;
				ExceptionHandler.handle(e, getShell(), title, message);
			} finally {
				fProject = null;
				fKeepContent = false;
			}
		}
	}
}
