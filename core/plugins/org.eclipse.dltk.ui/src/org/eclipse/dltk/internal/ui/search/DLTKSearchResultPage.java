/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.eclipse.dltk.internal.ui.search;

import java.util.HashMap;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.dltk.core.IMember;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.internal.corext.util.Messages;
import org.eclipse.dltk.internal.ui.dnd.DLTKViewerDragAdapter;
import org.eclipse.dltk.internal.ui.dnd.EditorInputTransferDragAdapter;
import org.eclipse.dltk.internal.ui.dnd.ResourceTransferDragAdapter;
import org.eclipse.dltk.internal.ui.scriptview.SelectionTransferDragAdapter;
import org.eclipse.dltk.ui.DLTKPluginImages;
import org.eclipse.dltk.ui.DLTKUIPlugin;
import org.eclipse.dltk.ui.search.IMatchPresentation;
import org.eclipse.dltk.ui.util.ExceptionHandler;
import org.eclipse.dltk.ui.viewsupport.ProblemTableViewer;
import org.eclipse.dltk.ui.viewsupport.ProblemTreeViewer;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.OpenEvent;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.search.ui.IContextMenuConstants;
import org.eclipse.search.ui.ISearchResultViewPart;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.search.ui.text.AbstractTextSearchResult;
import org.eclipse.search.ui.text.AbstractTextSearchViewPage;
import org.eclipse.search.ui.text.Match;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionContext;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.IPageSite;
import org.eclipse.ui.part.IShowInTargetList;
import org.eclipse.ui.part.ResourceTransfer;
import org.eclipse.ui.texteditor.ITextEditor;

public class DLTKSearchResultPage extends AbstractTextSearchViewPage implements IAdaptable {
	
	public static class DecoratorIgnoringViewerSorter extends ViewerSorter {

		private final ILabelProvider fLabelProvider;

		public DecoratorIgnoringViewerSorter(ILabelProvider labelProvider) {
			super(null); // lazy initialization
			fLabelProvider= labelProvider;
		}
		
	    public int compare(Viewer viewer, Object e1, Object e2) {
	        String name1= fLabelProvider.getText(e1);
	        String name2= fLabelProvider.getText(e2);
	        if (name1 == null)
	            name1 = "";//$NON-NLS-1$
	        if (name2 == null)
	            name2 = "";//$NON-NLS-1$
			return getComparator().compare(name1, name2);
	    }
		
	}
		
	private static final int DEFAULT_ELEMENT_LIMIT = 1000;
	private static final String FALSE = "FALSE"; //$NON-NLS-1$
	private static final String TRUE = "TRUE"; //$NON-NLS-1$
	private static final String KEY_GROUPING= "org.eclipse.dltk.search.resultpage.grouping"; //$NON-NLS-1$
	private static final String KEY_SORTING= "org.eclipse.dltk.search.resultpage.sorting"; //$NON-NLS-1$
	private static final String KEY_LIMIT_ENABLED= "org.eclipse.dltk.search.resultpage.limit_enabled"; //$NON-NLS-1$
	private static final String KEY_LIMIT= "org.eclipse.dltk.search.resultpage.limit"; //$NON-NLS-1$
	
	private static final String GROUP_GROUPING= "org.eclipse.dltk.search.resultpage.grouping"; //$NON-NLS-1$
	private static final String GROUP_FILTERING = "org.eclipse.dltk.search.resultpage.filtering"; //$NON-NLS-1$
	
	private NewSearchViewActionGroup fActionGroup;
	private DLTKSearchContentProvider fContentProvider;
	private int fCurrentSortOrder;
	private SortAction fSortByNameAction;
	private SortAction fSortByParentName;
	private SortAction fSortByPathAction;
	
	private GroupAction fGroupTypeAction;
	private GroupAction fGroupFileAction;
	private GroupAction fGroupPackageAction;
	private GroupAction fGroupProjectAction;
	private int fCurrentGrouping;
	
	private static final String[] SHOW_IN_TARGETS = new String[] {
			DLTKUIPlugin.ID_SCRIPT_EXPLORER, IPageLayout.ID_RES_NAV };
	public static final IShowInTargetList SHOW_IN_TARGET_LIST= new IShowInTargetList() {
		public String[] getShowInTargetIds() {
			return SHOW_IN_TARGETS;
		}
	};
	
	private DLTKSearchEditorOpener fEditorOpener= new DLTKSearchEditorOpener();

	public DLTKSearchResultPage() {
		initSortActions();
		initGroupingActions();
		// initFilterActions();
		setElementLimit(new Integer(DEFAULT_ELEMENT_LIMIT));
	}
	
	private void initSortActions() {
		fSortByNameAction= new SortAction(SearchMessages.DLTKSearchResultPage_sortByName, this, SortingLabelProvider.SHOW_ELEMENT_CONTAINER); 
		fSortByPathAction= new SortAction(SearchMessages.DLTKSearchResultPage_sortByPath, this, SortingLabelProvider.SHOW_PATH); 
		fSortByParentName= new SortAction(SearchMessages.DLTKSearchResultPage_sortByParentName, this, SortingLabelProvider.SHOW_CONTAINER_ELEMENT); 
	}

	private void initGroupingActions() {
		fGroupProjectAction= new GroupAction(SearchMessages.DLTKSearchResultPage_groupby_project, SearchMessages.DLTKSearchResultPage_groupby_project_tooltip, this, LevelTreeContentProvider.LEVEL_PROJECT); 
		DLTKPluginImages.setLocalImageDescriptors(fGroupProjectAction, "prj_mode.gif"); //$NON-NLS-1$
		fGroupPackageAction= new GroupAction(SearchMessages.DLTKSearchResultPage_groupby_package, SearchMessages.DLTKSearchResultPage_groupby_package_tooltip, this, LevelTreeContentProvider.LEVEL_PACKAGE); 
		DLTKPluginImages.setLocalImageDescriptors(fGroupPackageAction, "package_mode.gif"); //$NON-NLS-1$
		fGroupFileAction= new GroupAction(SearchMessages.DLTKSearchResultPage_groupby_file, SearchMessages.DLTKSearchResultPage_groupby_file_tooltip, this, LevelTreeContentProvider.LEVEL_FILE); 
		DLTKPluginImages.setLocalImageDescriptors(fGroupFileAction, "file_mode.gif"); //$NON-NLS-1$
		fGroupTypeAction= new GroupAction(SearchMessages.DLTKSearchResultPage_groupby_type, SearchMessages.DLTKSearchResultPage_groupby_type_tooltip, this, LevelTreeContentProvider.LEVEL_TYPE); 
		DLTKPluginImages.setLocalImageDescriptors(fGroupTypeAction, "type_mode.gif"); //$NON-NLS-1$
	}

	public void setViewPart(ISearchResultViewPart part) {
		super.setViewPart(part);
		fActionGroup= new NewSearchViewActionGroup(part);
	}
	
	public void showMatch(Match match, int offset, int length, boolean activate) throws PartInitException {
		IEditorPart editor;
		try {
			editor= fEditorOpener.openMatch(match);
		} catch (ModelException e) {
			throw new PartInitException(e.getStatus());
		}
		
		if (editor != null && activate)
			editor.getEditorSite().getPage().activate(editor);
		Object element= match.getElement();
		if (editor instanceof ITextEditor) {
			ITextEditor textEditor= (ITextEditor) editor;
			textEditor.selectAndReveal(offset, length);
		} else if (editor != null){
			if (element instanceof IFile) {
				IFile file= (IFile) element;
				showWithMarker(editor, file, offset, length);
			}
		} else {
			DLTKSearchResult result= (DLTKSearchResult) getInput();
			IMatchPresentation participant= result.getSearchParticpant(element);
			if (participant != null)
				participant.showMatch(match, offset, length, activate);
		}
	}
	
	private void showWithMarker(IEditorPart editor, IFile file, int offset, int length) throws PartInitException {
		try {
			IMarker marker= file.createMarker(NewSearchUI.SEARCH_MARKER);
			HashMap<String, Integer> attributes= new HashMap<String, Integer>(4);
			attributes.put(IMarker.CHAR_START, new Integer(offset));
			attributes.put(IMarker.CHAR_END, new Integer(offset + length));
			marker.setAttributes(attributes);
			IDE.gotoMarker(editor, marker);
			marker.delete();
		} catch (CoreException e) {
			throw new PartInitException(SearchMessages.DLTKSearchResultPage_error_marker, e); 
		}
	}

	protected void fillContextMenu(IMenuManager mgr) {
		super.fillContextMenu(mgr);
		addSortActions(mgr);

		fActionGroup.setContext(new ActionContext(getSite().getSelectionProvider().getSelection()));
		fActionGroup.fillContextMenu(mgr);
	}
	
	private void addSortActions(IMenuManager mgr) {
		if (getLayout() != FLAG_LAYOUT_FLAT)
			return;
		MenuManager sortMenu= new MenuManager(SearchMessages.DLTKSearchResultPage_sortBylabel); 
		sortMenu.add(fSortByNameAction);
		sortMenu.add(fSortByPathAction);
		sortMenu.add(fSortByParentName);
		
		fSortByNameAction.setChecked(fCurrentSortOrder == fSortByNameAction.getSortOrder());
		fSortByPathAction.setChecked(fCurrentSortOrder == fSortByPathAction.getSortOrder());
		fSortByParentName.setChecked(fCurrentSortOrder == fSortByParentName.getSortOrder());
		
		mgr.appendToGroup(IContextMenuConstants.GROUP_VIEWER_SETUP, sortMenu);
	}
	
	protected void fillToolbar(IToolBarManager tbm) {
		super.fillToolbar(tbm);
		if (getLayout() != FLAG_LAYOUT_FLAT)
			addGroupActions(tbm);
	}
		
	private void addGroupActions(IToolBarManager mgr) {
		mgr.appendToGroup(IContextMenuConstants.GROUP_VIEWER_SETUP, new Separator(GROUP_GROUPING));
		mgr.appendToGroup(GROUP_GROUPING, fGroupProjectAction);
		mgr.appendToGroup(GROUP_GROUPING, fGroupPackageAction);
		mgr.appendToGroup(GROUP_GROUPING, fGroupFileAction);
		mgr.appendToGroup(GROUP_GROUPING, fGroupTypeAction);
		
		updateGroupingActions();
	}

	private void updateGroupingActions() {
		fGroupProjectAction.setChecked(fCurrentGrouping == LevelTreeContentProvider.LEVEL_PROJECT);
		fGroupPackageAction.setChecked(fCurrentGrouping == LevelTreeContentProvider.LEVEL_PACKAGE);
		fGroupFileAction.setChecked(fCurrentGrouping == LevelTreeContentProvider.LEVEL_FILE);
		fGroupTypeAction.setChecked(fCurrentGrouping == LevelTreeContentProvider.LEVEL_TYPE);
	}

	public void dispose() {
		fActionGroup.dispose();
		super.dispose();
	}
	
	protected void elementsChanged(Object[] objects) {
		if (fContentProvider != null)
			fContentProvider.elementsChanged(objects);
	}

	protected void clear() {
		if (fContentProvider != null)
			fContentProvider.clear();
	}
	
	private void addDragAdapters(StructuredViewer viewer) {
		Transfer[] transfers = new Transfer[] {
				LocalSelectionTransfer.getTransfer(),
				ResourceTransfer.getInstance() };
		int ops= DND.DROP_COPY | DND.DROP_LINK;
		
		DLTKViewerDragAdapter dragAdapter= new DLTKViewerDragAdapter(viewer);
		dragAdapter.addDragSourceListener(new SelectionTransferDragAdapter(viewer));
		dragAdapter.addDragSourceListener(new EditorInputTransferDragAdapter(viewer));
		dragAdapter.addDragSourceListener(new ResourceTransferDragAdapter(viewer));
		viewer.addDragSupport(ops, transfers, dragAdapter);
	}	

	protected void configureTableViewer(TableViewer viewer) {
		viewer.setUseHashlookup(true);
		SortingLabelProvider sortingLabelProvider= new SortingLabelProvider(this);
		viewer.setLabelProvider(new ColorDecoratingLabelProvider(sortingLabelProvider, PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator()));
		fContentProvider=new DLTKSearchTableContentProvider(this);
		viewer.setContentProvider(fContentProvider);
		viewer.setSorter(new DecoratorIgnoringViewerSorter(sortingLabelProvider));
		setSortOrder(fCurrentSortOrder);
		addDragAdapters(viewer);
	}

	protected void configureTreeViewer(TreeViewer viewer) {
		PostfixLabelProvider postfixLabelProvider= new PostfixLabelProvider(this);
		viewer.setUseHashlookup(true);
		viewer.setSorter(new DecoratorIgnoringViewerSorter(postfixLabelProvider));
		viewer.setLabelProvider(new ColorDecoratingLabelProvider(postfixLabelProvider, PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator()));
		fContentProvider= new LevelTreeContentProvider(this, fCurrentGrouping);
		viewer.setContentProvider(fContentProvider);
		addDragAdapters(viewer);
	}
	
	protected TreeViewer createTreeViewer(Composite parent) {
		return new ProblemTreeViewer(parent, SWT.MULTI | SWT.H_SCROLL
				| SWT.V_SCROLL);
	}
	
	protected TableViewer createTableViewer(Composite parent) {
		return new ProblemTableViewer(parent, SWT.MULTI | SWT.H_SCROLL
				| SWT.V_SCROLL);
	}
	
	void setSortOrder(int order) {
		fCurrentSortOrder= order;
		StructuredViewer viewer= getViewer();
		viewer.getControl().setRedraw(false);
		DecoratingLabelProvider dlp= (DecoratingLabelProvider) viewer.getLabelProvider();
		((SortingLabelProvider)dlp.getLabelProvider()).setOrder(order);
		viewer.getControl().setRedraw(true);
		viewer.refresh();
		getSettings().put(KEY_SORTING, fCurrentSortOrder);
	}

	public void init(IPageSite site) {
		super.init(site);
		IMenuManager menuManager = site.getActionBars().getMenuManager();
		menuManager.insertBefore(IContextMenuConstants.GROUP_PROPERTIES, new Separator(GROUP_FILTERING));
		fActionGroup.fillActionBars(site.getActionBars());
		menuManager.appendToGroup(IContextMenuConstants.GROUP_PROPERTIES, new Action(SearchMessages.DLTKSearchResultPage_preferences_label) {
			public void run() {
				String pageId= "org.eclipse.search.preferences.SearchPreferencePage"; //$NON-NLS-1$
				PreferencesUtil.createPreferenceDialogOn(DLTKUIPlugin.getActiveWorkbenchShell(), pageId, null, null).open();
			}
		});
	}

	/**
	 * Precondition here: the viewer must be showing a tree with a LevelContentProvider.
	 * @param grouping
	 */
	void setGrouping(int grouping) {
		fCurrentGrouping= grouping;
		StructuredViewer viewer= getViewer();
		LevelTreeContentProvider cp= (LevelTreeContentProvider) viewer.getContentProvider();
		cp.setLevel(grouping);
		updateGroupingActions();
		getSettings().put(KEY_GROUPING, fCurrentGrouping);
		getViewPart().updateLabel();
	}
	
	protected StructuredViewer getViewer() {
		// override so that it's visible in the package.
		return super.getViewer();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.search.ui.text.AbstractTextSearchViewPage#restoreState(org.eclipse.ui.IMemento)
	 */
	public void restoreState(IMemento memento) {
		super.restoreState(memento);
		try {
			fCurrentSortOrder= getSettings().getInt(KEY_SORTING);
		} catch (NumberFormatException e) {
			fCurrentSortOrder=  SortingLabelProvider.SHOW_ELEMENT_CONTAINER;
		}
		try {
			fCurrentGrouping= getSettings().getInt(KEY_GROUPING);
		} catch (NumberFormatException e) {
			fCurrentGrouping= LevelTreeContentProvider.LEVEL_PACKAGE;
		}
		int elementLimit = DEFAULT_ELEMENT_LIMIT;
		if (FALSE.equals(getSettings().get(KEY_LIMIT_ENABLED))) {
			elementLimit = -1;
		} else {
			try {
				elementLimit = getSettings().getInt(KEY_LIMIT);
			} catch (NumberFormatException e) {
			}
		}
		if (memento != null) {
			Integer value= memento.getInteger(KEY_GROUPING);
			if (value != null)
				fCurrentGrouping= value.intValue();
			value= memento.getInteger(KEY_SORTING);
			if (value != null)
				fCurrentSortOrder= value.intValue();
			boolean limitElements = !FALSE.equals(memento
					.getString(KEY_LIMIT_ENABLED));
			value = memento.getInteger(KEY_LIMIT);
			if (value != null)
				elementLimit = limitElements ? value.intValue() : -1;
		}
		setElementLimit(new Integer(elementLimit));
	}

	/* (non-Javadoc)
	 * @see org.eclipse.search.ui.text.AbstractTextSearchViewPage#saveState(org.eclipse.ui.IMemento)
	 */
	public void saveState(IMemento memento) {
		super.saveState(memento);
		memento.putInteger(KEY_GROUPING, fCurrentGrouping);
		memento.putInteger(KEY_SORTING, fCurrentSortOrder);
		int limit = getElementLimit().intValue();
		if (limit != -1)
			memento.putString(KEY_LIMIT_ENABLED, TRUE);
		else
			memento.putString(KEY_LIMIT_ENABLED, FALSE);
		memento.putInteger(KEY_LIMIT, getElementLimit().intValue());
	}
	
	private boolean isQueryRunning() {
		AbstractTextSearchResult result= getInput();
		if (result != null) {
			return NewSearchUI.isQueryRunning(result.getQuery());
		}
		return false;
	}

	public String getLabel() {
		String label= super.getLabel();
		AbstractTextSearchResult input = getInput();
		if (input != null && input.getActiveMatchFilters() != null
				&& input.getActiveMatchFilters().length > 0) {
			if (isQueryRunning()) {
				String message = SearchMessages.DLTKSearchResultPage_filtered_message;
				return Messages.format(message, new Object[] { label });

			} else {
				int filteredOut = input.getMatchCount()
						- getFilteredMatchCount();
				String message = SearchMessages.DLTKSearchResultPage_filteredWithCount_message;
				return Messages.format(message, new Object[] { label,
						String.valueOf(filteredOut) });
			}
		}
		return label;
	}

	private int getFilteredMatchCount() {
		StructuredViewer viewer= getViewer();
		if (viewer instanceof TreeViewer) {
			ITreeContentProvider tp= (ITreeContentProvider) viewer.getContentProvider();
			return getMatchCount(tp, getRootElements((TreeViewer) getViewer()));
		} else {
			return getMatchCount((TableViewer) viewer);
		}
	}
	
	private Object[] getRootElements(TreeViewer viewer) {
		Tree t= viewer.getTree();
		Item[] roots= t.getItems();
		Object[] elements= new Object[roots.length];
		for (int i = 0; i < elements.length; i++) {
			elements[i]= roots[i].getData();
		}
		return elements;
	}
	
	private Object[] getRootElements(TableViewer viewer) {
		Table t= viewer.getTable();
		Item[] roots= t.getItems();
		Object[] elements= new Object[roots.length];
		for (int i = 0; i < elements.length; i++) {
			elements[i]= roots[i].getData();
		}
		return elements;
	}

	private int getMatchCount(ITreeContentProvider cp, Object[] elements) {
		int count= 0;
		for (int j = 0; j < elements.length; j++) {
			count+= getDisplayedMatchCount(elements[j]);
			Object[] children = cp.getChildren(elements[j]);
			count+= getMatchCount(cp, children);
		}
		return count;
	}
	
	private int getMatchCount(TableViewer viewer) {
		Object[] elements=	getRootElements(viewer);
		int count= 0;
		for (int i = 0; i < elements.length; i++) {
			count+= getDisplayedMatchCount(elements[i]);
		}
		return count;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
	public Object getAdapter(Class adapter) {
		if (IShowInTargetList.class.equals(adapter)) {
			return SHOW_IN_TARGET_LIST;
		}
		return null;
	}
	
	protected void handleOpen(OpenEvent event) {
		Object firstElement= ((IStructuredSelection)event.getSelection()).getFirstElement();
		if (firstElement instanceof ISourceModule || 				
				firstElement instanceof IMember) {
			if (getDisplayedMatchCount(firstElement) == 0) {
				try {
					fEditorOpener.openElement(firstElement);
				} catch (CoreException e) {
					ExceptionHandler.handle(e, getSite().getShell(), SearchMessages.DLTKSearchResultPage_open_editor_error_title, SearchMessages.DLTKSearchResultPage_open_editor_error_message); 
				}
				return;
			}
		}
		super.handleOpen(event);
	}

	public void setElementLimit(Integer elementLimit) {
		super.setElementLimit(elementLimit);
		int limit = elementLimit.intValue();
		getSettings().put(KEY_LIMIT, limit);
		getSettings().put(KEY_LIMIT_ENABLED, limit != -1 ? TRUE : FALSE);
	}	
}
