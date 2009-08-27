package org.eclipse.dltk.ui.environment;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.ListenerList;
import org.eclipse.dltk.compiler.util.Util;
import org.eclipse.dltk.core.environment.EnvironmentManager;
import org.eclipse.dltk.core.environment.IEnvironment;
import org.eclipse.dltk.ui.util.PixelConverter;
import org.eclipse.jface.util.SafeRunnable;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;

public class EnvironmentPathBlock {

	private Table pathTable;
	private TableViewer pathViewer;

	private ListenerList listeners = new ListenerList();

	/**
	 * Environment to path association.
	 */
	private Map<IEnvironment, String> paths = new HashMap<IEnvironment, String>();

	private boolean useFolders = false;

	public EnvironmentPathBlock() {
	}

	public EnvironmentPathBlock(boolean useFolders) {
		this.useFolders = useFolders;
	}

	protected class PathLabelProvider extends LabelProvider implements
			ITableLabelProvider {

		private final int pathColumn;

		public PathLabelProvider(int pathColumn) {
			this.pathColumn = pathColumn;
		}

		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}

		public String getColumnText(Object element, int columnIndex) {
			if (element instanceof IEnvironment) {
				if (columnIndex == 0) {
					return ((IEnvironment) element).getName();
				} else if (columnIndex == pathColumn) {
					Object path = paths.get(element);
					if (path != null) {
						return (String) path;
					}
				}
			}
			return Util.EMPTY_STRING;
		}
	}

	public void createControl(Composite parent) {
		createControl(parent, 1);
	}

	public void createControl(Composite parent, int columns) {
		createControl(parent, columns, null);
	}

	public void createControl(Composite parent, IEnvironment[] environments) {
		createControl(parent, 1, environments);
	}

	public void createControl(Composite parent, int columns,
			IEnvironment[] environments) {
		PixelConverter conv = new PixelConverter(parent);

		pathTable = new Table(parent, SWT.SINGLE | SWT.BORDER
				| SWT.FULL_SELECTION);
		pathTable.setHeaderVisible(true);
		pathTable.setLinesVisible(true);
		// GridData tableData = new GridData(SWT.FILL, SWT.DEFAULT, true,
		// false);
		// tableData.heightHint = conv.convertHeightInCharsToPixels(4);
		GridData tableData = new GridData(SWT.FILL, SWT.FILL, true, true);
		tableData.heightHint = conv.convertHeightInCharsToPixels(4);
		tableData.horizontalSpan = columns;
		pathTable.setLayoutData(tableData);

		pathViewer = new TableViewer(pathTable);

		initColumns(pathViewer, conv);

		pathViewer.setLabelProvider(createPathLabelProvider());
		pathViewer.setContentProvider(new IStructuredContentProvider() {
			public Object[] getElements(Object inputElement) {
				if (inputElement instanceof IEnvironment[]) {
					return (Object[]) inputElement;
				}
				return new Object[0];
			}

			public void dispose() {
			}

			public void inputChanged(Viewer viewer, Object oldInput,
					Object newInput) {
			}
		});
		pathViewer.setInput(environments != null ? environments
				: EnvironmentManager.getEnvironments());
		if (pathTable.getItemCount() > 0) {
			pathTable.select(0);
		}
	}

	protected PathLabelProvider createPathLabelProvider() {
		return new PathLabelProvider(1);
	}

	protected void initColumns(TableViewer viewer, PixelConverter conv) {
		initEnvironmentColumn(viewer, conv);
		initPathColumn(viewer, conv);
	}

	protected void initEnvironmentColumn(TableViewer viewer, PixelConverter conv) {
		TableViewerColumn environmentsColumn = new TableViewerColumn(
				pathViewer, SWT.NULL);
		environmentsColumn.getColumn().setText(
				Messages.EnvironmentPathBlock_environment);
		environmentsColumn.getColumn().setWidth(
				conv.convertWidthInCharsToPixels(30));
	}

	protected void initPathColumn(TableViewer viewer, PixelConverter conv) {
		TableViewerColumn pathColumn = new TableViewerColumn(pathViewer,
				SWT.NULL);
		pathColumn.getColumn().setText(Messages.EnvironmentPathBlock_path);
		pathColumn.getColumn().setWidth(conv.convertWidthInCharsToPixels(40));
		pathColumn.setEditingSupport(new EditingSupport(pathViewer) {
			@Override
			protected boolean canEdit(Object element) {
				return true;
			}

			@Override
			protected CellEditor getCellEditor(Object element) {
				return new TextCellEditor(pathTable) {
					private Button browse;
					private Composite composite;

					@Override
					protected Control createControl(Composite compositeParent) {
						composite = new Composite(compositeParent, SWT.NONE);
						composite
								.setBackground(compositeParent.getBackground());
						GridLayout layout = new GridLayout(2, false);
						layout.marginLeft = -4;
						layout.marginTop = -4;
						layout.marginBottom = -4;
						layout.marginRight = -4;
						composite.setLayout(layout);
						super.createControl(composite);
						text.setLayoutData(new GridData(SWT.FILL, SWT.DEFAULT,
								true, false));
						browse = new Button(composite, SWT.PUSH);
						browse.setText("..."); //$NON-NLS-1$
						Font font = new Font(compositeParent.getDisplay(),
								"arial", 6, 0); //$NON-NLS-1$
						browse.setFont(font);
						browse.setLayoutData(new GridData(SWT.DEFAULT,
								SWT.FILL, false, true));
						browse.addSelectionListener(new SelectionAdapter() {
							@Override
							public void widgetSelected(SelectionEvent e) {
								editPath();
								doFocusLost();
							}
						});
						FocusAdapter listener = new FocusAdapter() {
							@Override
							public void focusLost(FocusEvent e) {
								Control cursorControl = composite.getDisplay()
										.getCursorControl();
								if (cursorControl != null) {
									if (cursorControl.equals(browse)) {
										return;
									}
								}
								doFocusLost();
							}
						};
						browse.addFocusListener(listener);
						text.addFocusListener(listener);
						return composite;
					}

					public void doFocusLost() {
						super.focusLost();
					}

					@Override
					protected void focusLost() {
					}
				};
			}

			@Override
			protected Object getValue(Object element) {
				Object value = paths.get(element);
				return value != null ? value : Util.EMPTY_STRING;
			}

			@Override
			protected void setValue(Object element, Object value) {
				if (value == null || Util.EMPTY_STRING.equals(value)) {
					paths.remove(element);
				} else {
					paths.put((IEnvironment) element, (String) value);
				}
				pathViewer.refresh();
				fireValueChanged();
			}
		});
	}

	protected TableViewer getViewer() {
		return pathViewer;
	}

	public void addSelectionListener(ISelectionChangedListener listener) {
		pathViewer.addSelectionChangedListener(listener);
	}

	public void removeSelectionListener(ISelectionChangedListener listener) {
		pathViewer.removeSelectionChangedListener(listener);
	}

	protected void editPath() {
		ISelection selection = pathViewer.getSelection();
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection sel = (IStructuredSelection) selection;
			IEnvironment environment = (IEnvironment) sel.getFirstElement();
			IEnvironmentUI ui = (IEnvironmentUI) environment
					.getAdapter(IEnvironmentUI.class);
			String file = null;
			if (!useFolders) {
				file = ui.selectFile(this.pathTable.getShell(),
						IEnvironmentUI.DEFAULT);
			} else {
				file = ui.selectFolder(this.pathTable.getShell());
			}
			if (file != null) {
				this.paths.put(environment, file);
				this.pathViewer.refresh();
				fireValueChanged();
			}
		}
	}

	public IStructuredSelection getSelection() {
		return (IStructuredSelection) pathViewer.getSelection();
	}

	public void setPaths(Map<IEnvironment, String> paths) {
		this.paths = paths;
		pathTable.getDisplay().asyncExec(new Runnable() {
			public void run() {
				pathViewer.refresh();
				fireValueChanged();
			}
		});
	}

	public Map<IEnvironment, String> getPaths() {
		return this.paths;
	}

	protected void fireValueChanged() {
		Object[] array = listeners.getListeners();
		for (int i = 0; i < array.length; i++) {
			final IEnvironmentPathBlockListener listener = (IEnvironmentPathBlockListener) array[i];
			SafeRunnable.run(new SafeRunnable() {
				public void run() {
					listener.valueChanged(getPaths());
				}
			});
		}
	}

	public void addListener(IEnvironmentPathBlockListener listener) {
		listeners.add(listener);
	}

	public void removeListener(IEnvironmentPathBlockListener listener) {
		listeners.add(listener);
	}
}
