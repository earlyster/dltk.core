/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     xored software, Inc. - initial API and implementation
 *     xored software, Inc. - fix tab handling (Bug# 200024) (Alex Panchenko) 
 *******************************************************************************/
package org.eclipse.dltk.internal.ui.editor;

import java.text.CharacterIterator;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Stack;

import org.eclipse.core.filebuffers.IPersistableAnnotationModel;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.dltk.compiler.CharOperation;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.DLTKLanguageManager;
import org.eclipse.dltk.core.IDLTKLanguageToolkit;
import org.eclipse.dltk.core.IImportDeclaration;
import org.eclipse.dltk.core.ILocalVariable;
import org.eclipse.dltk.core.IMember;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.IPackageDeclaration;
import org.eclipse.dltk.core.IScriptLanguageProvider;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.ISourceRange;
import org.eclipse.dltk.core.ISourceReference;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.core.PreferencesLookupDelegate;
import org.eclipse.dltk.core.ScriptModelUtil;
import org.eclipse.dltk.internal.ui.BrowserInformationControl;
import org.eclipse.dltk.internal.ui.actions.CompositeActionGroup;
import org.eclipse.dltk.internal.ui.actions.FoldingActionGroup;
import org.eclipse.dltk.internal.ui.actions.refactoring.RefactorActionGroup;
import org.eclipse.dltk.internal.ui.editor.SourceModuleDocumentProvider.SourceModuleAnnotationModel;
import org.eclipse.dltk.internal.ui.editor.selectionaction.GoToNextPreviousMemberAction;
import org.eclipse.dltk.internal.ui.editor.semantic.highlighting.SemanticHighlightingManager;
import org.eclipse.dltk.internal.ui.editor.semantic.highlighting.SemanticHighlightingReconciler;
import org.eclipse.dltk.internal.ui.text.DLTKWordIterator;
import org.eclipse.dltk.internal.ui.text.DocumentCharacterIterator;
import org.eclipse.dltk.internal.ui.text.HTMLTextPresenter;
import org.eclipse.dltk.internal.ui.text.IScriptReconcilingListener;
import org.eclipse.dltk.internal.ui.text.hover.ScriptExpandHover;
import org.eclipse.dltk.internal.ui.text.hover.SourceViewerInformationControl;
import org.eclipse.dltk.ui.CodeFormatterConstants;
import org.eclipse.dltk.ui.DLTKUILanguageManager;
import org.eclipse.dltk.ui.DLTKUIPlugin;
import org.eclipse.dltk.ui.EclipsePreferencesAdapter;
import org.eclipse.dltk.ui.IContextMenuConstants;
import org.eclipse.dltk.ui.IDLTKUILanguageToolkit;
import org.eclipse.dltk.ui.IWorkingCopyManager;
import org.eclipse.dltk.ui.PreferenceConstants;
import org.eclipse.dltk.ui.PreferencesAdapter;
import org.eclipse.dltk.ui.actions.DLTKActionConstants;
import org.eclipse.dltk.ui.actions.GenerateActionGroup;
import org.eclipse.dltk.ui.actions.IScriptEditorActionDefinitionIds;
import org.eclipse.dltk.ui.actions.OpenEditorActionGroup;
import org.eclipse.dltk.ui.actions.OpenViewActionGroup;
import org.eclipse.dltk.ui.actions.SearchActionGroup;
import org.eclipse.dltk.ui.editor.IScriptAnnotation;
import org.eclipse.dltk.ui.editor.highlighting.ISemanticHighlightingUpdater;
import org.eclipse.dltk.ui.formatter.IScriptFormatterFactory;
import org.eclipse.dltk.ui.formatter.ScriptFormatterManager;
import org.eclipse.dltk.ui.formatter.internal.ScriptFormattingContextProperties;
import org.eclipse.dltk.ui.text.ScriptSourceViewerConfiguration;
import org.eclipse.dltk.ui.text.ScriptTextTools;
import org.eclipse.dltk.ui.text.folding.FoldingProviderManager;
import org.eclipse.dltk.ui.text.folding.IFoldingStructureProvider;
import org.eclipse.dltk.ui.text.folding.IFoldingStructureProviderExtension;
import org.eclipse.dltk.ui.text.templates.ITemplateAccess;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.AbstractInformationControlManager;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.BadPositionCategoryException;
import org.eclipse.jface.text.DefaultInformationControl;
import org.eclipse.jface.text.DocumentCommand;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.IPositionUpdater;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.ITextOperationTarget;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.ITextViewerExtension2;
import org.eclipse.jface.text.ITextViewerExtension4;
import org.eclipse.jface.text.ITextViewerExtension5;
import org.eclipse.jface.text.IWidgetTokenKeeper;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.text.contentassist.ContentAssistEvent;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.ICompletionListener;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.contentassist.IContentAssistantExtension2;
import org.eclipse.jface.text.formatter.FormattingContextProperties;
import org.eclipse.jface.text.formatter.IFormattingContext;
import org.eclipse.jface.text.information.IInformationProvider;
import org.eclipse.jface.text.information.IInformationProviderExtension;
import org.eclipse.jface.text.information.IInformationProviderExtension2;
import org.eclipse.jface.text.information.InformationPresenter;
import org.eclipse.jface.text.link.ILinkedModeListener;
import org.eclipse.jface.text.link.LinkedModeModel;
import org.eclipse.jface.text.link.LinkedModeUI;
import org.eclipse.jface.text.link.LinkedModeUI.ExitFlags;
import org.eclipse.jface.text.link.LinkedModeUI.IExitPolicy;
import org.eclipse.jface.text.reconciler.IReconciler;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.AnnotationRulerColumn;
import org.eclipse.jface.text.source.CompositeRuler;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.ICharacterPairMatcher;
import org.eclipse.jface.text.source.IOverviewRuler;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.ISourceViewerExtension2;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.jface.text.source.IVerticalRulerColumn;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.jface.text.source.projection.ProjectionSupport;
import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IPostSelectionProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ST;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IPartService;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionContext;
import org.eclipse.ui.actions.ActionGroup;
import org.eclipse.ui.editors.text.EditorsUI;
import org.eclipse.ui.part.IShowInTargetList;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditor;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditorPreferenceConstants;
import org.eclipse.ui.texteditor.ChainedPreferenceStore;
import org.eclipse.ui.texteditor.ContentAssistAction;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditorActionConstants;
import org.eclipse.ui.texteditor.ITextEditorActionDefinitionIds;
import org.eclipse.ui.texteditor.IUpdate;
import org.eclipse.ui.texteditor.SourceViewerDecorationSupport;
import org.eclipse.ui.texteditor.TextEditorAction;
import org.eclipse.ui.texteditor.TextNavigationAction;
import org.eclipse.ui.texteditor.TextOperationAction;
import org.eclipse.ui.texteditor.templates.ITemplatesPage;
import org.eclipse.ui.views.contentoutline.ContentOutline;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

import com.ibm.icu.text.BreakIterator;

public abstract class ScriptEditor extends AbstractDecoratedTextEditor
		implements IScriptReconcilingListener, IScriptLanguageProvider,
		IScriptEditor {

	/** The editor's save policy */
	protected ISavePolicy fSavePolicy = null;

	/** Preference key for matching brackets */
	protected final static String MATCHING_BRACKETS = PreferenceConstants.EDITOR_MATCHING_BRACKETS;
	/** Preference key for matching brackets color */
	protected final static String MATCHING_BRACKETS_COLOR = PreferenceConstants.EDITOR_MATCHING_BRACKETS_COLOR;

	private ScriptEditorErrorTickUpdater fScriptEditorErrorTickUpdater;

	private OccurrencesFinder occurrencesFinder;

	private static String[] GLOBAL_FOLDING_PROPERTIES = {
			PreferenceConstants.EDITOR_FOLDING_ENABLED,
			PreferenceConstants.EDITOR_COMMENTS_FOLDING_ENABLED,
			PreferenceConstants.EDITOR_FOLDING_LINES_LIMIT,
			PreferenceConstants.EDITOR_COMMENT_FOLDING_JOIN_NEWLINES };

	public ISourceViewer getScriptSourceViewer() {
		return super.getSourceViewer();
	}

	public static class BracketLevel {
		public int fOffset;
		public int fLength;
		public LinkedModeUI fUI;
		public Position fFirstPosition;
		public Position fSecondPosition;
	}

	public class ExitPolicy implements IExitPolicy {

		public final char fExitCharacter;
		public final char fEscapeCharacter;
		public final Stack fStack;
		public final int fSize;

		public ExitPolicy(char exitCharacter, char escapeCharacter, Stack stack) {
			fExitCharacter = exitCharacter;
			fEscapeCharacter = escapeCharacter;
			fStack = stack;
			fSize = fStack.size();
		}

		/*
		 * @see
		 * org.eclipse.jdt.internal.ui.text.link.LinkedPositionUI.ExitPolicy
		 * #doExit(org.eclipse.jdt.internal.ui.text.link.LinkedPositionManager,
		 * org.eclipse.swt.events.VerifyEvent, int, int)
		 */
		public ExitFlags doExit(LinkedModeModel model, VerifyEvent event,
				int offset, int length) {

			if (fSize == fStack.size() && !isMasked(offset)) {
				if (event.character == fExitCharacter) {
					BracketLevel level = (BracketLevel) fStack.peek();
					if (level.fFirstPosition.offset > offset
							|| level.fSecondPosition.offset < offset)
						return null;
					if (level.fSecondPosition.offset == offset && length == 0)
						// don't enter the character if if its the closing peer
						return new ExitFlags(ILinkedModeListener.UPDATE_CARET,
								false);
				}
				// when entering an anonymous class between the parenthesis', we
				// don't want
				// to jump after the closing parenthesis when return is pressed
				if (event.character == SWT.CR && offset > 0) {
					// ssanders: If completion popup is displayed, Enter
					// dismisses it
					if (((AdaptedSourceViewer) getScriptSourceViewer()).fInCompletionSession)
						return new ExitFlags(ILinkedModeListener.NONE, true);

					IDocument document = getSourceViewer().getDocument();
					try {
						if (document.getChar(offset - 1) == '{')
							return new ExitFlags(ILinkedModeListener.EXIT_ALL,
									true);
					} catch (BadLocationException e) {
					}
				}
			}
			return null;
		}

		private boolean isMasked(int offset) {
			IDocument document = getSourceViewer().getDocument();
			try {
				return fEscapeCharacter == document.getChar(offset - 1);
			} catch (BadLocationException e) {
			}
			return false;
		}
	}

	static class ExclusivePositionUpdater implements IPositionUpdater {

		/** The position category. */
		private final String fCategory;

		/**
		 * Creates a new updater for the given <code>category</code>.
		 * 
		 * @param category
		 *            the new category.
		 */
		public ExclusivePositionUpdater(String category) {
			fCategory = category;
		}

		/*
		 * @see
		 * org.eclipse.jface.text.IPositionUpdater#update(org.eclipse.jface.
		 * text.DocumentEvent)
		 */
		public void update(DocumentEvent event) {

			int eventOffset = event.getOffset();
			int eventOldLength = event.getLength();
			int eventNewLength = event.getText() == null ? 0 : event.getText()
					.length();
			int deltaLength = eventNewLength - eventOldLength;

			try {
				Position[] positions = event.getDocument().getPositions(
						fCategory);

				for (int i = 0; i != positions.length; i++) {

					Position position = positions[i];

					if (position.isDeleted())
						continue;

					int offset = position.getOffset();
					int length = position.getLength();
					int end = offset + length;

					if (offset >= eventOffset + eventOldLength)
						// position comes
						// after change - shift
						position.setOffset(offset + deltaLength);
					else if (end <= eventOffset) {
						// position comes way before change -
						// leave alone
					} else if (offset <= eventOffset
							&& end >= eventOffset + eventOldLength) {
						// event completely internal to the position - adjust
						// length
						position.setLength(length + deltaLength);
					} else if (offset < eventOffset) {
						// event extends over end of position - adjust length
						int newEnd = eventOffset;
						position.setLength(newEnd - offset);
					} else if (end > eventOffset + eventOldLength) {
						// event extends from before position into it - adjust
						// offset
						// and length
						// offset becomes end of event, length adjusted
						// accordingly
						int newOffset = eventOffset + eventNewLength;
						position.setOffset(newOffset);
						position.setLength(end - newOffset);
					} else {
						// event consumes the position - delete it
						position.delete();
					}
				}
			} catch (BadPositionCategoryException e) {
				// ignore and return
			}
		}

		/**
		 * Returns the position category.
		 * 
		 * @return the position category
		 */
		public String getCategory() {
			return fCategory;
		}

	}

	/**
	 * Text operation code for requesting common prefix completion.
	 */
	public static final int CONTENTASSIST_COMPLETE_PREFIX = 60;

	interface ITextConverter {
		void customizeDocumentCommand(IDocument document,
				DocumentCommand command);
	}

	class AdaptedSourceViewer extends ScriptSourceViewer implements
			ICompletionListener {
		private List<ITextConverter> fTextConverters;

		private boolean fIgnoreTextConverters = false;
		private boolean fInCompletionSession;

		protected IContentAssistant getContentAssistant() {
			return fContentAssistant;
		}

		public AdaptedSourceViewer(Composite parent,
				IVerticalRuler verticalRuler, IOverviewRuler overviewRuler,
				boolean showAnnotationsOverview, int styles,
				IPreferenceStore store) {
			super(parent, verticalRuler, overviewRuler,
					showAnnotationsOverview, styles, store);
		}

		public void configure(SourceViewerConfiguration configuration) {
			super.configure(configuration);

			final IContentAssistant ca = getContentAssistant();
			if (ca instanceof IContentAssistantExtension2) {
				((IContentAssistantExtension2) ca).addCompletionListener(this);
			}
		}

		public void unconfigure() {
			final IContentAssistant ca = getContentAssistant();
			if (ca instanceof IContentAssistantExtension2) {
				((IContentAssistantExtension2) ca)
						.removeCompletionListener(this);
			}

			super.unconfigure();
		}

		/*
		 * @see ITextOperationTarget#doOperation(int)
		 */
		public void doOperation(int operation) {

			if (getTextWidget() == null)
				return;

			switch (operation) {
			case CONTENTASSIST_PROPOSALS:
				String msg = fContentAssistant.showPossibleCompletions();
				setStatusLineErrorMessage(msg);
				return;
			case QUICK_ASSIST:
				/*
				 * XXX: We can get rid of this once the SourceViewer has a way
				 * to update the status line
				 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=133787
				 */
				msg = fQuickAssistAssistant.showPossibleQuickAssists();
				setStatusLineErrorMessage(msg);
				return;
			case UNDO:
				fIgnoreTextConverters = true;
				super.doOperation(operation);
				fIgnoreTextConverters = false;
				return;
			case REDO:
				fIgnoreTextConverters = true;
				super.doOperation(operation);
				fIgnoreTextConverters = false;
				return;
			}

			super.doOperation(operation);
		}

		public void insertTextConverter(ITextConverter textConverter, int index) {
			throw new UnsupportedOperationException();
		}

		public void addTextConverter(ITextConverter textConverter) {
			if (fTextConverters == null) {
				fTextConverters = new ArrayList<ITextConverter>(1);
				fTextConverters.add(textConverter);
			} else if (!fTextConverters.contains(textConverter))
				fTextConverters.add(textConverter);
		}

		public void removeTextConverter(ITextConverter textConverter) {
			if (fTextConverters != null) {
				fTextConverters.remove(textConverter);
				if (fTextConverters.size() == 0)
					fTextConverters = null;
			}
		}

		/*
		 * @see TextViewer#customizeDocumentCommand(DocumentCommand)
		 */
		@Override
		protected void customizeDocumentCommand(DocumentCommand command) {
			super.customizeDocumentCommand(command);
			if (!fIgnoreTextConverters && fTextConverters != null) {
				for (ITextConverter c : fTextConverters)
					c.customizeDocumentCommand(getDocument(), command);
			}
		}

		@Override
		public boolean requestWidgetToken(IWidgetTokenKeeper requester) {
			if (PlatformUI.getWorkbench().getHelpSystem()
					.isContextHelpDisplayed())
				return false;
			return super.requestWidgetToken(requester);
		}

		@Override
		public boolean requestWidgetToken(IWidgetTokenKeeper requester,
				int priority) {
			if (PlatformUI.getWorkbench().getHelpSystem()
					.isContextHelpDisplayed())
				return false;
			return super.requestWidgetToken(requester, priority);
		}

		public void assistSessionEnded(ContentAssistEvent event) {
			fInCompletionSession = false;
		}

		public void assistSessionStarted(ContentAssistEvent event) {
			fInCompletionSession = true;
		}

		public void selectionChanged(ICompletionProposal proposal,
				boolean smartToggle) {
		}

		private IProject getProject() {
			final IModelElement input = getInputModelElement();
			if (input != null) {
				final IScriptProject scriptProject = input.getScriptProject();
				if (scriptProject != null) {
					return scriptProject.getProject();
				}
			}
			return null;
		}

		private ISourceModule getSourceModule() {
			final IModelElement input = getInputModelElement();
			if (input != null) {
				return (ISourceModule) input
						.getAncestor(IModelElement.SOURCE_MODULE);
			}
			return null;
		}

		@Override
		public IFormattingContext createFormattingContext() {
			final IFormattingContext context = super.createFormattingContext();
			context.setProperty(ScriptFormattingContextProperties.MODULE,
					getSourceModule());
			final IProject project = getProject();
			context.setProperty(
					ScriptFormattingContextProperties.CONTEXT_PROJECT, project);
			final IScriptFormatterFactory factory = ScriptFormatterManager
					.getSelected(getLanguageToolkit().getNatureId(), project);
			if (factory != null) {
				context.setProperty(
						ScriptFormattingContextProperties.CONTEXT_FORMATTER_ID,
						factory.getId());
				final Map<String, String> preferences = factory
						.retrievePreferences(new PreferencesLookupDelegate(
								project));
				context.setProperty(
						FormattingContextProperties.CONTEXT_PREFERENCES,
						preferences);
			}
			return context;
		}
	}

	/**
	 * Internal implementation class for a change listener.
	 * 
	 * 
	 */
	protected abstract class AbstractSelectionChangedListener implements
			ISelectionChangedListener {
		/**
		 * Installs this selection changed listener with the given selection
		 * provider. If the selection provider is a post selection provider,
		 * post selection changed events are the preferred choice, otherwise
		 * normal selection changed events are requested.
		 * 
		 * @param selectionProvider
		 */
		public void install(ISelectionProvider selectionProvider) {
			if (selectionProvider == null)
				return;
			if (selectionProvider instanceof IPostSelectionProvider) {
				IPostSelectionProvider provider = (IPostSelectionProvider) selectionProvider;
				provider.addPostSelectionChangedListener(this);
			} else {
				selectionProvider.addSelectionChangedListener(this);
			}
		}

		/**
		 * Removes this selection changed listener from the given selection
		 * provider.
		 * 
		 * @param selectionProvider
		 *            the selection provider
		 */
		public void uninstall(ISelectionProvider selectionProvider) {
			if (selectionProvider == null)
				return;
			if (selectionProvider instanceof IPostSelectionProvider) {
				IPostSelectionProvider provider = (IPostSelectionProvider) selectionProvider;
				provider.removePostSelectionChangedListener(this);
			} else {
				selectionProvider.removeSelectionChangedListener(this);
			}
		}
	}

	/**
	 * Updates the selection in the editor's widget with the selection of the
	 * outline page.
	 */
	class OutlineSelectionChangedListener extends
			AbstractSelectionChangedListener {
		public void selectionChanged(SelectionChangedEvent event) {
			doSelectionChanged(event);
		}
	}

	private ScriptOutlinePage fOutlinePage;

	private ProjectionSupport fProjectionSupport;

	/**
	 * This editor's projection model updater
	 */
	private IFoldingStructureProvider fProjectionModelUpdater;

	/**
	 * The action group for folding.
	 */
	private ActionGroup fFoldingGroup;

	/** The information presenter. */
	private InformationPresenter fInformationPresenter;

	private CompositeActionGroup fContextMenuGroup;

	// private SelectionHistory fSelectionHistory;

	private CompositeActionGroup fActionGroups;

	private AbstractSelectionChangedListener fOutlineSelectionChangedListener = new OutlineSelectionChangedListener();

	/**
	 * Updates the script outline page selection and this editor's range
	 * indicator.
	 * 
	 * 
	 */
	private class EditorSelectionChangedListener extends
			AbstractSelectionChangedListener {
		/*
		 * @see
		 * org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged
		 * (org.eclipse.jface.viewers.SelectionChangedEvent)
		 */
		public void selectionChanged(SelectionChangedEvent event) {
			// XXX: see https://bugs.eclipse.org/bugs/show_bug.cgi?id=56161
			ScriptEditor.this.selectionChanged();
		}
	}

	/**
	 * The editor selection changed listener.
	 */
	private EditorSelectionChangedListener fEditorSelectionChangedListener;

	public ScriptEditor() {
		super();
		setDocumentProvider(DLTKUIPlugin.getDefault()
				.getSourceModuleDocumentProvider());
		fScriptEditorErrorTickUpdater = new ScriptEditorErrorTickUpdater(this);
	}

	/**
	 * @see org.eclipse.ui.texteditor.StatusTextEditor#handleElementContentReplaced()
	 */
	protected void handleElementContentReplaced() {
		super.handleElementContentReplaced();

		IAnnotationModel annotationModel = getScriptSourceViewer()
				.getAnnotationModel();
		if (annotationModel instanceof IPersistableAnnotationModel) {
			try {
				((IPersistableAnnotationModel) annotationModel)
						.reinitialize(getScriptSourceViewer().getDocument());
			} catch (CoreException ex) {
				ex.printStackTrace();
			}
		}
	}

	public void dispose() {

		if (fProjectionModelUpdater != null) {
			fProjectionModelUpdater.uninstall();
			fProjectionModelUpdater = null;
		}
		if (occurrencesFinder != null) {
			occurrencesFinder.dispose();
			occurrencesFinder = null;
		}

		// ISourceViewer sourceViewer= getSourceViewer();
		// if (sourceViewer instanceof ITextViewerExtension)
		// ((ITextViewerExtension)
		// sourceViewer).removeVerifyKeyListener(fBracketInserter);

		if (fScriptEditorErrorTickUpdater != null) {
			fScriptEditorErrorTickUpdater.dispose();
			fScriptEditorErrorTickUpdater = null;
		}

		// if (fCorrectionCommands != null) {
		// fCorrectionCommands.deregisterCommands();
		// fCorrectionCommands= null;
		// }
		uninstallSemanticHighlighting();
		super.dispose();
	}

	@Override
	protected void initializeEditor() {
		occurrencesFinder = new OccurrencesFinder(this);
		if (!occurrencesFinder.isValid()) {
			occurrencesFinder = null;
		}
		IPreferenceStore store = createCombinedPreferenceStore(null);
		setPreferenceStore(store);
		ScriptTextTools textTools = getTextTools();
		if (textTools != null) {
			setSourceViewerConfiguration(textTools
					.createSourceViewerConfiguraton(store, this));
		}
	}

	/**
	 * Creates and returns the preference store for this editor with the given
	 * input.
	 * 
	 * @param input
	 *            The editor input for which to create the preference store
	 * @return the preference store for this editor
	 */
	private IPreferenceStore createCombinedPreferenceStore(IEditorInput input) {
		final List<IPreferenceStore> stores = new ArrayList<IPreferenceStore>(8);
		final IScriptProject project = EditorUtility.getScriptProject(input);
		final IDLTKLanguageToolkit toolkit = getLanguageToolkit();
		final String preferenceQualifier = toolkit.getPreferenceQualifier();
		if (project != null) {
			if (preferenceQualifier != null) {
				stores.add(new EclipsePreferencesAdapter(new ProjectScope(
						project.getProject()), preferenceQualifier));
			}
			stores.add(new EclipsePreferencesAdapter(new ProjectScope(project
					.getProject()), DLTKCore.PLUGIN_ID));
		}
		stores.add(getScriptPreferenceStore());
		if (preferenceQualifier != null) {
			stores.add(new EclipsePreferencesAdapter(new InstanceScope(),
					preferenceQualifier));
			stores.add(new EclipsePreferencesAdapter(new DefaultScope(),
					preferenceQualifier));
		}
		stores.add(new PreferencesAdapter(DLTKCore.getDefault()
				.getPluginPreferences()));
		stores.add(EditorsUI.getPreferenceStore());
		stores.add(PlatformUI.getPreferenceStore());
		return new ChainedPreferenceStore(
				stores.toArray(new IPreferenceStore[stores.size()]));
	}

	public IPreferenceStore getScriptPreferenceStore() {
		IDLTKLanguageToolkit toolkit = getLanguageToolkit();
		if (toolkit != null) {
			IDLTKUILanguageToolkit uiToolkit = DLTKUILanguageManager
					.getLanguageToolkit(toolkit.getNatureId());
			if (uiToolkit != null) {
				return uiToolkit.getPreferenceStore();
			}
		}
		return null;
	}

	public ScriptTextTools getTextTools() {
		return null;
	}

	protected void connectPartitioningToElement(IEditorInput input,
			IDocument document) {
	}

	protected void internalDoSetInput(IEditorInput input) throws CoreException {
		ISourceViewer sourceViewer = getSourceViewer();
		ScriptSourceViewer scriptSourceViewer = null;
		if (sourceViewer instanceof ScriptSourceViewer)
			scriptSourceViewer = (ScriptSourceViewer) sourceViewer;
		IPreferenceStore store = getPreferenceStore();

		if (scriptSourceViewer != null
				&& isFoldingEnabled()
				&& (store == null || !store
						.getBoolean(PreferenceConstants.EDITOR_SHOW_SEGMENTS)))
			scriptSourceViewer.prepareDelayedProjection();

		// correct connection code here.

		super.doSetInput(input);

		final IDocumentProvider docProvider = getDocumentProvider();
		final IAnnotationModel model = docProvider.getAnnotationModel(input);
		if (model instanceof SourceModuleAnnotationModel) {
			((SourceModuleAnnotationModel) model).problemFactory = DLTKLanguageManager
					.getProblemFactory(getNatureId());
		}
		final IDocument doc = docProvider.getDocument(input);
		connectPartitioningToElement(input, doc);

		if (scriptSourceViewer != null
				&& scriptSourceViewer.getReconciler() == null) {
			IReconciler reconciler = getSourceViewerConfiguration()
					.getReconciler(scriptSourceViewer);
			if (reconciler != null) {
				reconciler.install(scriptSourceViewer);
				scriptSourceViewer.setReconciler(reconciler);
			}
		}
		if (DLTKCore.DEBUG) {
			System.err
					.println("TODO: Add encoding support and overriding indicator support"); //$NON-NLS-1$
		}
		// if (fEncodingSupport != null)
		// fEncodingSupport.reset();
		// if (isShowingOverrideIndicators())
		// installOverrideIndicator(false);
		setOutlinePageInput(fOutlinePage, input);
	}

	private boolean isFoldingEnabled() {
		return getPreferenceStore().getBoolean(
				PreferenceConstants.EDITOR_FOLDING_ENABLED);
	}

	public boolean isSaveAsAllowed() {
		return true;
	}

	/**
	 * Returns the standard action group of this editor.
	 * 
	 * @return returns this editor's standard action group
	 */
	ActionGroup getActionGroup() {
		return fActionGroups;
	}

	/*
	 * @see AbstractTextEditor#editorContextMenuAboutToShow
	 */
	public void editorContextMenuAboutToShow(IMenuManager menu) {
		super.editorContextMenuAboutToShow(menu);

		menu.insertAfter(IContextMenuConstants.GROUP_OPEN, new GroupMarker(
				IContextMenuConstants.GROUP_SHOW));

		ActionContext context = new ActionContext(getSelectionProvider()
				.getSelection());
		context.setInput(getEditorInput());
		fContextMenuGroup.setContext(context);
		fContextMenuGroup.fillContextMenu(menu);
		fContextMenuGroup.setContext(null);
		// Quick views
		menu.appendToGroup(IContextMenuConstants.GROUP_OPEN,
				getAction(IScriptEditorActionDefinitionIds.SHOW_OUTLINE));
		menu.appendToGroup(IContextMenuConstants.GROUP_OPEN,
				getAction(IScriptEditorActionDefinitionIds.OPEN_HIERARCHY));
	}

	/**
	 * Information provider used to present focusable information shells.
	 * 
	 * 
	 */
	private static final class InformationProvider implements
			IInformationProvider, IInformationProviderExtension,
			IInformationProviderExtension2 {

		private IRegion fHoverRegion;
		private Object fHoverInfo;
		private IInformationControlCreator fControlCreator;

		InformationProvider(IRegion hoverRegion, Object hoverInfo,
				IInformationControlCreator controlCreator) {
			fHoverRegion = hoverRegion;
			fHoverInfo = hoverInfo;
			fControlCreator = controlCreator;
		}

		/*
		 * @see
		 * org.eclipse.jface.text.information.IInformationProvider#getSubject
		 * (org.eclipse.jface.text.ITextViewer, int)
		 */
		public IRegion getSubject(ITextViewer textViewer, int invocationOffset) {
			return fHoverRegion;
		}

		/*
		 * @see
		 * org.eclipse.jface.text.information.IInformationProvider#getInformation
		 * (org.eclipse.jface.text.ITextViewer, org.eclipse.jface.text.IRegion)
		 */
		public String getInformation(ITextViewer textViewer, IRegion subject) {
			return fHoverInfo.toString();
		}

		/*
		 * @see
		 * org.eclipse.jface.text.information.IInformationProviderExtension#
		 * getInformation2(org.eclipse.jface.text.ITextViewer,
		 * org.eclipse.jface.text.IRegion)
		 */
		public Object getInformation2(ITextViewer textViewer, IRegion subject) {
			return fHoverInfo;
		}

		/*
		 * @see
		 * org.eclipse.jface.text.information.IInformationProviderExtension2
		 * #getInformationPresenterControlCreator()
		 */
		public IInformationControlCreator getInformationPresenterControlCreator() {
			return fControlCreator;
		}
	}

	/**
	 * This action behaves in two different ways: If there is no current text
	 * hover, the documentation is displayed using information presenter. If
	 * there is a current text hover, it is converted into a information
	 * presenter in order to make it sticky.
	 */
	class InformationDispatchAction extends TextEditorAction {

		/** The wrapped text operation action. */
		private final TextOperationAction fTextOperationAction;

		/**
		 * Creates a dispatch action.
		 * 
		 * @param resourceBundle
		 *            the resource bundle
		 * @param prefix
		 *            the prefix
		 * @param textOperationAction
		 *            the text operation action
		 */
		public InformationDispatchAction(ResourceBundle resourceBundle,
				String prefix, final TextOperationAction textOperationAction) {
			super(resourceBundle, prefix, ScriptEditor.this);
			if (textOperationAction == null)
				throw new IllegalArgumentException();
			fTextOperationAction = textOperationAction;
		}

		/*
		 * @see org.eclipse.jface.action.IAction#run()
		 */
		public void run() {

			ISourceViewer sourceViewer = getSourceViewer();
			if (sourceViewer == null) {
				fTextOperationAction.run();
				return;
			}

			if (sourceViewer instanceof ITextViewerExtension4) {
				ITextViewerExtension4 extension4 = (ITextViewerExtension4) sourceViewer;
				if (extension4.moveFocusToWidgetToken())
					return;
			}

			if (sourceViewer instanceof ITextViewerExtension2) {
				// does a text hover exist?
				ITextHover textHover = ((ITextViewerExtension2) sourceViewer)
						.getCurrentTextHover();
				if (textHover != null
						&& makeTextHoverFocusable(sourceViewer, textHover))
					return;
			}

			/*
			 * if (sourceViewer instanceof ISourceViewerExtension3) { // does an
			 * annotation hover exist? IAnnotationHover annotationHover=
			 * ((ISourceViewerExtension3)
			 * sourceViewer).getCurrentAnnotationHover(); if (annotationHover !=
			 * null && makeAnnotationHoverFocusable(sourceViewer,
			 * annotationHover)) return; }
			 */

			// otherwise, just run the action
			fTextOperationAction.run();
		}

		/**
		 * Tries to make a text hover focusable (or "sticky").
		 * 
		 * @param sourceViewer
		 *            the source viewer to display the hover over
		 * @param textHover
		 *            the hover to make focusable
		 * @return <code>true</code> if successful, <code>false</code> otherwise
		 * 
		 */
		private boolean makeTextHoverFocusable(ISourceViewer sourceViewer,
				ITextHover textHover) {
			Point hoverEventLocation = ((ITextViewerExtension2) sourceViewer)
					.getHoverEventLocation();
			int offset = computeOffsetAtLocation(sourceViewer,
					hoverEventLocation.x, hoverEventLocation.y);
			if (offset == -1)
				return false;

			IRegion hoverRegion = textHover
					.getHoverRegion(sourceViewer, offset);
			if (hoverRegion == null)
				return false;

			String hoverInfo = textHover
					.getHoverInfo(sourceViewer, hoverRegion);

			IInformationControlCreator controlCreator = null;
			if (textHover instanceof IInformationProviderExtension2)
				controlCreator = ((IInformationProviderExtension2) textHover)
						.getInformationPresenterControlCreator();

			IInformationProvider informationProvider = new InformationProvider(
					hoverRegion, hoverInfo, controlCreator);

			fInformationPresenter.setOffset(offset);
			fInformationPresenter
					.setAnchor(AbstractInformationControlManager.ANCHOR_BOTTOM);
			fInformationPresenter.setMargins(6, 6); // default values from
			// AbstractInformationControlManager
			String contentType = IDocument.DEFAULT_CONTENT_TYPE;
			fInformationPresenter.setInformationProvider(informationProvider,
					contentType);
			fInformationPresenter.showInformation();

			return true;

		}

		// modified version from TextViewer
		private int computeOffsetAtLocation(ITextViewer textViewer, int x, int y) {

			StyledText styledText = textViewer.getTextWidget();
			IDocument document = textViewer.getDocument();

			if (document == null)
				return -1;

			try {
				int widgetOffset = styledText.getOffsetAtLocation(new Point(x,
						y));
				Point p = styledText.getLocationAtOffset(widgetOffset);
				if (p.x > x)
					widgetOffset--;

				if (textViewer instanceof ITextViewerExtension5) {
					ITextViewerExtension5 extension = (ITextViewerExtension5) textViewer;
					return extension.widgetOffset2ModelOffset(widgetOffset);
				} else {
					IRegion visibleRegion = textViewer.getVisibleRegion();
					return widgetOffset + visibleRegion.getOffset();
				}
			} catch (IllegalArgumentException e) {
				return -1;
			}

		}
	}

	@Override
	protected void createActions() {
		super.createActions();

		ActionGroup oeg = new OpenEditorActionGroup(this);
		ActionGroup ovg = new OpenViewActionGroup(this);
		ActionGroup dsg = new SearchActionGroup(this);

		fActionGroups = new CompositeActionGroup(new ActionGroup[] { oeg, ovg,
				dsg });

		// fSelectionHistory= new SelectionHistory(this);

		fContextMenuGroup = new CompositeActionGroup(new ActionGroup[] { oeg,
				ovg, dsg });
		loadContributedContextActionGroups();

		fFoldingGroup = createFoldingActionGroup();

		// ResourceAction resAction = new TextOperationAction(DLTKEditorMessages
		// .getBundleForConstructedKeys(), "ShowDocumentaion.", this,
		// ISourceViewer.INFORMATION, true);
		//
		// resAction = new InformationDispatchAction(DLTKEditorMessages
		// .getBundleForConstructedKeys(), "ShowDocumentation.",
		// (TextOperationAction) resAction);
		//
		// resAction
		// .setActionDefinitionId(IScriptEditorActionDefinitionIds.
		// SHOW_DOCUMENTATION);
		// setAction("ShowDocumentation", resAction);

		Action action = new GotoMatchingBracketAction(this);
		action.setActionDefinitionId(IScriptEditorActionDefinitionIds.GOTO_MATCHING_BRACKET);
		setAction(GotoMatchingBracketAction.GOTO_MATCHING_BRACKET, action);

		Action outlineAction = new TextOperationAction(
				DLTKEditorMessages.getBundleForConstructedKeys(),
				"ShowOutline.", this, //$NON-NLS-1$
				ScriptSourceViewer.SHOW_OUTLINE, true);
		outlineAction
				.setActionDefinitionId(IScriptEditorActionDefinitionIds.SHOW_OUTLINE);
		setAction(IScriptEditorActionDefinitionIds.SHOW_OUTLINE, outlineAction);

		action = new TextOperationAction(
				DLTKEditorMessages.getBundleForConstructedKeys(),
				"OpenHierarchy.", this, ScriptSourceViewer.SHOW_HIERARCHY, true); //$NON-NLS-1$
		action.setActionDefinitionId(IScriptEditorActionDefinitionIds.OPEN_HIERARCHY);
		setAction(IScriptEditorActionDefinitionIds.OPEN_HIERARCHY, action);

		// ContentAssistProposal
		action = new ContentAssistAction(
				DLTKEditorMessages.getBundleForConstructedKeys(),
				"ContentAssistProposal.", this); //$NON-NLS-1$
		action.setActionDefinitionId(ITextEditorActionDefinitionIds.CONTENT_ASSIST_PROPOSALS);
		setAction("ContentAssistProposal", action); //$NON-NLS-1$
		markAsStateDependentAction("ContentAssistProposal", true); //$NON-NLS-1$

		// ContentAssistContextInformation
		action = new TextOperationAction(
				DLTKEditorMessages.getBundleForConstructedKeys(),
				"ContentAssistContextInformation.", this, ISourceViewer.CONTENTASSIST_CONTEXT_INFORMATION); //$NON-NLS-1$
		action.setActionDefinitionId(ITextEditorActionDefinitionIds.CONTENT_ASSIST_CONTEXT_INFORMATION);
		setAction("ContentAssistContextInformation", action); //$NON-NLS-1$
		markAsStateDependentAction("ContentAssistContextInformation", true); //$NON-NLS-1$

		// GroupEdit
		ActionGroup rg = new RefactorActionGroup(this,
				ITextEditorActionConstants.GROUP_EDIT);
		fActionGroups.addGroup(rg);
		fContextMenuGroup.addGroup(rg);

		// GoToNextMember
		action = GoToNextPreviousMemberAction.newGoToNextMemberAction(this);
		action.setActionDefinitionId(IScriptEditorActionDefinitionIds.GOTO_NEXT_MEMBER);
		setAction(GoToNextPreviousMemberAction.NEXT_MEMBER, action);

		// GoToPreviousMember
		action = GoToNextPreviousMemberAction.newGoToPreviousMemberAction(this);
		action.setActionDefinitionId(IScriptEditorActionDefinitionIds.GOTO_PREVIOUS_MEMBER);
		setAction(GoToNextPreviousMemberAction.PREVIOUS_MEMBER, action);

		// Source menu actions
		action = new TextOperationAction(
				DLTKEditorMessages.getBundleForConstructedKeys(),
				"Comment.", this, ITextOperationTarget.PREFIX); //$NON-NLS-1$
		action.setActionDefinitionId(IScriptEditorActionDefinitionIds.COMMENT);
		setAction(DLTKActionConstants.COMMENT, action);
		markAsStateDependentAction(DLTKActionConstants.COMMENT, true);

		action = new TextOperationAction(
				DLTKEditorMessages.getBundleForConstructedKeys(),
				"Uncomment.", this, ITextOperationTarget.STRIP_PREFIX); //$NON-NLS-1$
		action.setActionDefinitionId(IScriptEditorActionDefinitionIds.UNCOMMENT);
		setAction(DLTKActionConstants.UNCOMMENT, action);
		markAsStateDependentAction(DLTKActionConstants.UNCOMMENT, true);

		action = new ToggleCommentAction(
				DLTKEditorMessages.getBundleForConstructedKeys(),
				"ToggleComment.", this); //$NON-NLS-1$
		action.setActionDefinitionId(IScriptEditorActionDefinitionIds.TOGGLE_COMMENT);
		setAction(DLTKActionConstants.TOGGLE_COMMENT, action);
		markAsStateDependentAction(DLTKActionConstants.TOGGLE_COMMENT, true);

		ISourceViewer sourceViewer = getSourceViewer();
		SourceViewerConfiguration configuration = getSourceViewerConfiguration();
		((ToggleCommentAction) action).configure(sourceViewer, configuration);

		final ActionGroup generateActions = createGenerateActionGroup();
		if (generateActions != null) {
			fActionGroups.addGroup(generateActions);
			fContextMenuGroup.addGroup(generateActions);
		}
	}

	/**
	 * @since 3.0
	 */
	protected ActionGroup createGenerateActionGroup() {
		return new GenerateActionGroup(this,
				ITextEditorActionConstants.GROUP_EDIT);
	}

	private static final String EXTENSION_EDITOR_CONTEXT_ACTION_GROUPS = "editorContextActionGroup"; //$NON-NLS-1$
	private static final String ATTR_NATURE = "nature"; //$NON-NLS-1$
	private static final String ATTR_CLASS = "class"; //$NON-NLS-1$

	private void loadContributedContextActionGroups() {
		final IConfigurationElement[] elements = Platform
				.getExtensionRegistry().getConfigurationElementsFor(
						DLTKUIPlugin.PLUGIN_ID,
						EXTENSION_EDITOR_CONTEXT_ACTION_GROUPS);
		final String natureId = getNatureId();
		for (int i = 0; i < elements.length; ++i) {
			final IConfigurationElement element = elements[i];
			final String elementNature = element.getAttribute(ATTR_NATURE);
			if (elementNature == null || elementNature.equals(natureId)) {
				try {
					final Object actionGroup = element
							.createExecutableExtension(ATTR_CLASS);
					if (actionGroup instanceof ActionGroup) {
						fContextMenuGroup.addGroup((ActionGroup) actionGroup);
					} else {
						DLTKUIPlugin.logErrorMessage(actionGroup.getClass()
								.getName() + " should extend ActionGroup"); //$NON-NLS-1$
					}
				} catch (CoreException e) {
					DLTKUIPlugin.log(e);
				}
			}
		}
	}

	/**
	 * Creates action group for folding.
	 * 
	 * @return
	 * @since 3.0
	 */
	protected ActionGroup createFoldingActionGroup() {
		return new FoldingActionGroup(this, getViewer(),
				getScriptPreferenceStore());
	}

	@Override
	protected IVerticalRulerColumn createAnnotationRulerColumn(
			CompositeRuler ruler) {
		if (!getPreferenceStore().getBoolean(
				PreferenceConstants.EDITOR_ANNOTATION_ROLL_OVER))
			return super.createAnnotationRulerColumn(ruler);

		AnnotationRulerColumn column = new AnnotationRulerColumn(
				VERTICAL_RULER_WIDTH, getAnnotationAccess());
		column.setHover(new ScriptExpandHover(ruler, getAnnotationAccess(),
				new IDoubleClickListener() {

					public void doubleClick(DoubleClickEvent event) {
						// for now: just invoke ruler double click action
						triggerAction(ITextEditorActionConstants.RULER_DOUBLE_CLICK);
					}

					private void triggerAction(String actionID) {
						IAction action = getAction(actionID);
						if (action != null) {
							if (action instanceof IUpdate)
								((IUpdate) action).update();
							// hack to propagate line change
							if (action instanceof ISelectionListener) {
								((ISelectionListener) action).selectionChanged(
										null, null);
							}
							if (action.isEnabled())
								action.run();
						}
					}

				}));

		return column;
	}

	/**
	 * Returns the folding action group, or <code>null</code> if there is none.
	 * 
	 * @return the folding action group, or <code>null</code> if there is none
	 * 
	 */
	ActionGroup getFoldingActionGroup() {
		return fFoldingGroup;
	}

	public final ISourceViewer getViewer() {
		return getSourceViewer();
	}

	protected void doSetInput(IEditorInput input) throws CoreException {
		ISourceViewer sourceViewer = getSourceViewer();
		if (!(sourceViewer instanceof ISourceViewerExtension2)) {
			setPreferenceStore(createCombinedPreferenceStore(input));
			try {
				internalDoSetInput(input);
			} catch (ModelException e) {
				DLTKUIPlugin.log(e);
				this.close(false);
			}
		} else {
			// uninstall & unregister preference store listener
			getSourceViewerDecorationSupport(sourceViewer).uninstall();
			((ISourceViewerExtension2) sourceViewer).unconfigure();
			setPreferenceStore(createCombinedPreferenceStore(input));
			// install & register preference store listener
			sourceViewer.configure(getSourceViewerConfiguration());
			getSourceViewerDecorationSupport(sourceViewer).install(
					getPreferenceStore());
			try {
				internalDoSetInput(input);
			} catch (ModelException e) {
				DLTKUIPlugin.log(e);
				this.close(false);
			}
		}

		if (fScriptEditorErrorTickUpdater != null)
			fScriptEditorErrorTickUpdater
					.updateEditorImage(getInputModelElement());
	}

	protected void setPreferenceStore(IPreferenceStore store) {
		super.setPreferenceStore(store);
		final SourceViewerConfiguration svConfiguration = getSourceViewerConfiguration();
		if (svConfiguration == null
				|| svConfiguration instanceof ScriptSourceViewerConfiguration) {
			final ScriptTextTools textTools = getTextTools();
			if (textTools != null) {
				setSourceViewerConfiguration(textTools
						.createSourceViewerConfiguraton(store, this));
			}
		}
		if (getSourceViewer() instanceof ScriptSourceViewer) {
			((ScriptSourceViewer) getSourceViewer()).setPreferenceStore(store);
		}
		if (occurrencesFinder != null) {
			occurrencesFinder.setPreferenceStore(store);
		}
	}

	private ScriptOutlinePage createOutlinePage() {
		final ScriptOutlinePage page = doCreateOutlinePage();
		fOutlineSelectionChangedListener.install(page);
		setOutlinePageInput(page, getEditorInput());
		return page;
	}

	/**
	 * Creates the outline page used with this editor.
	 * 
	 * @return the created script outline page
	 */
	protected ScriptOutlinePage doCreateOutlinePage() {
		return new ScriptOutlinePage(this, getPreferenceStore());
	}

	/**
	 * String identifiying concrete language editor. Used for ex. for fetching
	 * available filters
	 * 
	 * @return
	 */
	public abstract String getEditorId();

	/**
	 * Informs the editor that its outliner has been closed.
	 */
	public void outlinePageClosed() {
		if (fOutlinePage != null) {
			fOutlineSelectionChangedListener.uninstall(fOutlinePage);
			fOutlinePage = null;
			resetHighlightRange();
		}
	}

	private void setOutlinePageInput(ScriptOutlinePage page, IEditorInput input) {
		if (page == null) {
			return;
		}
		IModelElement me = getInputModelElement();
		if (me != null && me.exists()) {
			page.setInput(me);
		} else {
			page.setInput(null);
		}
	}

	/**
	 * The templates page.
	 * 
	 * @since 3.0
	 */
	private ScriptTemplatesPage fTemplatesPage;

	/**
	 * Creates the templates page used with this editor.
	 * 
	 * @return the created script templates page
	 * @since 3.0
	 */
	protected ScriptTemplatesPage createTemplatesPage() {
		final IDLTKUILanguageToolkit uiToolkit = getUILanguageToolkit();
		if (uiToolkit == null) {
			return null;
		}
		final ITemplateAccess templateAccess = uiToolkit.getEditorTemplates();
		if (templateAccess == null) {
			return null;
		}
		try {
			return new ScriptTemplatesPage(this, templateAccess);
		} catch (Throwable e) {
			return null;
		}
	}

	public Object getAdapter(@SuppressWarnings("rawtypes") Class required) {
		if (ITemplatesPage.class.equals(required)) {
			if (fTemplatesPage == null)
				fTemplatesPage = createTemplatesPage();
			return fTemplatesPage;
		}
		if (IContentOutlinePage.class.equals(required)) {
			if (fOutlinePage == null)
				fOutlinePage = createOutlinePage();
			return fOutlinePage;
		}
		if (required == IShowInTargetList.class) {
			return new IShowInTargetList() {
				public String[] getShowInTargetIds() {
					return new String[] { DLTKUIPlugin.ID_SCRIPT_EXPLORER,
							IPageLayout.ID_OUTLINE };
				}
			};
		}
		if (required == OccurrencesFinder.class) {
			return occurrencesFinder;
		}
		if (required == IFoldingStructureProvider.class)
			return fProjectionModelUpdater;
		if (required == IFoldingStructureProviderExtension.class)
			return fProjectionModelUpdater;

		if (fProjectionSupport != null) {
			Object adapter = fProjectionSupport.getAdapter(getSourceViewer(),
					required);
			if (adapter != null)
				return adapter;
		}

		return super.getAdapter(required);
	}

	/**
	 * Returns the mutex for the reconciler. See
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=63898 for a description of
	 * the problem.
	 * <p>
	 * XXX remove once the underlying problem
	 * (https://bugs.eclipse.org/bugs/show_bug.cgi?id=66176) is solved.
	 * </p>
	 * 
	 * @return the lock reconcilers may use to synchronize on
	 */
	public Object getReconcilerLock() {
		return fReconcilerLock;
	}

	protected void doSelectionChanged(SelectionChangedEvent event) {
		ISourceReference reference = null;
		ISelection selection = event.getSelection();
		Iterator<?> iter = ((IStructuredSelection) selection).iterator();
		while (iter.hasNext()) {
			Object o = iter.next();
			if (o instanceof ISourceReference) {
				reference = (ISourceReference) o;
				break;
			}
		}
		if (!isActivePart() && DLTKUIPlugin.getActivePage() != null)
			DLTKUIPlugin.getActivePage().bringToTop(this);
		setSelection(reference, !isActivePart());
		if (occurrencesFinder != null) {
			occurrencesFinder.updateOccurrenceAnnotations();
		}
	}

	protected boolean isActivePart() {
		IWorkbenchPart part = getActivePart();
		return part != null && part.equals(this);
	}

	private IWorkbenchPart getActivePart() {
		IWorkbenchWindow window = getSite().getWorkbenchWindow();
		IPartService service = window.getPartService();
		IWorkbenchPart part = service.getActivePart();
		return part;
	}

	protected void setSelection(ISourceReference reference, boolean moveCursor) {
		if (getSelectionProvider() == null)
			return;
		ISelection selection = getSelectionProvider().getSelection();
		if (selection instanceof TextSelection) {
			TextSelection textSelection = (TextSelection) selection;
			// PR 39995: [navigation] Forward history cleared after going back
			// in navigation history:
			// mark only in navigation history if the cursor is being moved
			// (which it isn't if
			// this is called from a PostSelectionEvent that should only update
			// the magnet)
			if (moveCursor
					&& (textSelection.getOffset() != 0 || textSelection
							.getLength() != 0))
				markInNavigationHistory();
		}
		if (reference != null) {
			StyledText textWidget = null;
			ISourceViewer sourceViewer = getSourceViewer();
			if (sourceViewer != null)
				textWidget = sourceViewer.getTextWidget();
			if (textWidget == null)
				return;
			try {
				ISourceRange range = null;
				range = reference.getSourceRange();
				if (range == null)
					return;
				int offset = range.getOffset();
				int length = range.getLength();
				if (offset < 0 || length < 0)
					return;
				setHighlightRange(offset, length, moveCursor);
				if (!moveCursor)
					return;
				offset = -1;
				length = -1;
				if (reference instanceof IMember) {
					range = ((IMember) reference).getNameRange();
					if (range != null) {
						offset = range.getOffset();
						length = range.getLength();
					}
				} else if (reference instanceof ILocalVariable) {
					range = ((ILocalVariable) reference).getNameRange();
					if (range != null) {
						offset = range.getOffset();
						length = range.getLength();
					}
				} else if (reference instanceof IImportDeclaration
						|| reference instanceof IPackageDeclaration) {
					// range is still getSourceRange()
					offset = range.getOffset();
					length = range.getLength();
				}
				if (offset > -1 && length > 0) {
					try {
						textWidget.setRedraw(false);
						sourceViewer.revealRange(offset, length);
						sourceViewer.setSelectedRange(offset, length);
					} finally {
						textWidget.setRedraw(true);
					}
					markInNavigationHistory();
				}
			} catch (ModelException x) {
			} catch (IllegalArgumentException x) {
			}
		} else if (moveCursor) {
			resetHighlightRange();
			markInNavigationHistory();
		}
	}

	protected void doSetSelection(ISelection selection) {
		super.doSetSelection(selection);
		synchronizeOutlinePageSelection();
	}

	public void setSelection(IModelElement element) {

		if (element == null || element instanceof ISourceModule) {
			/*
			 * If the element is an ISourceModule this unit is either the input
			 * of this editor or not being displayed. In both cases, nothing
			 * should happened.
			 * (http://dev.eclipse.org/bugs/show_bug.cgi?id=5128)
			 */
			return;
		}

		IModelElement corresponding = getCorrespondingElement(element);
		if (corresponding instanceof ISourceReference) {
			ISourceReference reference = (ISourceReference) corresponding;
			// set highlight range
			setSelection(reference, true);
			// set outliner selection
			if (fOutlinePage != null) {
				fOutlineSelectionChangedListener.uninstall(fOutlinePage);
				fOutlinePage.select(reference);
				fOutlineSelectionChangedListener.install(fOutlinePage);
			}
		}
	}

	/**
	 * Synchronizes the outliner selection with the given element position in
	 * the editor.
	 * 
	 * @param element
	 *            thescriptelement to select
	 */
	protected void synchronizeOutlinePage(ISourceReference element) {
		synchronizeOutlinePage(element, true);
	}

	/**
	 * Synchronizes the outliner selection with the given element position in
	 * the editor.
	 * 
	 * @param element
	 *            thescriptelement to select
	 * @param checkIfOutlinePageActive
	 *            <code>true</code> if check for active outline page needs to be
	 *            done
	 * @since 2.0
	 */
	public void synchronizeOutlinePage(ISourceReference element,
			boolean checkIfOutlinePageActive) {
		if (fOutlinePage != null && element != null
				&& !(checkIfOutlinePageActive && isOutlinePageActive())) {
			fOutlineSelectionChangedListener.uninstall(fOutlinePage);
			fOutlinePage.select(element);
			fOutlineSelectionChangedListener.install(fOutlinePage);
		}
	}

	/**
	 * Synchronizes the outliner selection with the actual cursor position in
	 * the editor.
	 */
	public void synchronizeOutlinePageSelection() {
		synchronizeOutlinePage(computeHighlightRangeSourceReference());
	}

	private boolean isOutlinePageActive() {
		IWorkbenchPart part = getActivePart();
		return part instanceof ContentOutline
				&& ((ContentOutline) part).getCurrentPage() == fOutlinePage;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Overrides the default implementation to handle {@link IJavaAnnotation}.
	 * </p>
	 * 
	 * @param offset
	 *            the region offset
	 * @param length
	 *            the region length
	 * @param forward
	 *            <code>true</code> for forwards, <code>false</code> for
	 *            backward
	 * @param annotationPosition
	 *            the position of the found annotation
	 * @return the found annotation
	 */
	protected Annotation findAnnotation(final int offset, final int length,
			boolean forward, Position annotationPosition) {

		Annotation nextAnnotation = null;
		Position nextAnnotationPosition = null;
		Annotation containingAnnotation = null;
		Position containingAnnotationPosition = null;
		boolean currentAnnotation = false;

		IDocument document = getDocumentProvider()
				.getDocument(getEditorInput());
		int endOfDocument = document.getLength();
		int distance = Integer.MAX_VALUE;

		IAnnotationModel model = getDocumentProvider().getAnnotationModel(
				getEditorInput());
		Iterator<Annotation> e = new ScriptAnnotationIterator(model, true);
		while (e.hasNext()) {
			Annotation a = e.next();
			if ((a instanceof IScriptAnnotation)
					&& ((IScriptAnnotation) a).hasOverlay()
					|| !isNavigationTarget(a))
				continue;

			Position p = model.getPosition(a);
			if (p == null)
				continue;

			if (forward && p.offset == offset || !forward
					&& p.offset + p.getLength() == offset + length) {// ||
				// p.includes(offset))
				// {
				if (containingAnnotation == null
						|| (forward
								&& p.length >= containingAnnotationPosition.length || !forward
								&& p.length >= containingAnnotationPosition.length)) {
					containingAnnotation = a;
					containingAnnotationPosition = p;
					currentAnnotation = p.length == length;
				}
			} else {
				int currentDistance = 0;

				if (forward) {
					currentDistance = p.getOffset() - offset;
					if (currentDistance < 0)
						currentDistance = endOfDocument + currentDistance;

					if (currentDistance < distance
							|| currentDistance == distance
							&& p.length < nextAnnotationPosition.length) {
						distance = currentDistance;
						nextAnnotation = a;
						nextAnnotationPosition = p;
					}
				} else {
					currentDistance = offset + length
							- (p.getOffset() + p.length);
					if (currentDistance < 0)
						currentDistance = endOfDocument + currentDistance;

					if (currentDistance < distance
							|| currentDistance == distance
							&& p.length < nextAnnotationPosition.length) {
						distance = currentDistance;
						nextAnnotation = a;
						nextAnnotationPosition = p;
					}
				}
			}
		}
		if (containingAnnotationPosition != null
				&& (!currentAnnotation || nextAnnotation == null)) {
			annotationPosition.setOffset(containingAnnotationPosition
					.getOffset());
			annotationPosition.setLength(containingAnnotationPosition
					.getLength());
			return containingAnnotation;
		}
		if (nextAnnotationPosition != null) {
			annotationPosition.setOffset(nextAnnotationPosition.getOffset());
			annotationPosition.setLength(nextAnnotationPosition.getLength());
		}

		return nextAnnotation;
	}

	/**
	 * Returns the annotation overlapping with the given range or
	 * <code>null</code>.
	 * 
	 * @param offset
	 *            the region offset
	 * @param length
	 *            the region length
	 * @return the found annotation or <code>null</code>
	 * @since 3.0
	 */
	private Annotation getAnnotation(int offset, int length) {
		IAnnotationModel model = getDocumentProvider().getAnnotationModel(
				getEditorInput());
		Iterator<Annotation> e = new ScriptAnnotationIterator(model, false);
		while (e.hasNext()) {
			Annotation a = e.next();
			Position p = model.getPosition(a);
			if (p != null && p.overlapsWith(offset, length))
				return a;
		}
		return null;
	}

	/*
	 * @see
	 * org.eclipse.ui.texteditor.AbstractDecoratedTextEditor#gotoAnnotation(
	 * boolean)
	 * 
	 * @since 3.2
	 */
	public Annotation gotoAnnotation(boolean forward) {
		fSelectionChangedViaGotoAnnotation = true;
		return super.gotoAnnotation(forward);
	}

	/**
	 * Computes and returns the source reference that includes the caret and
	 * serves as provider for the outline page selection and the editor range
	 * indication.
	 * 
	 * @return the computed source reference
	 * @since 2.0
	 */
	public ISourceReference computeHighlightRangeSourceReference() {
		ISourceViewer sourceViewer = getSourceViewer();
		if (sourceViewer == null)
			return null;
		StyledText styledText = sourceViewer.getTextWidget();
		if (styledText == null)
			return null;
		int caret = 0;
		if (sourceViewer instanceof ITextViewerExtension5) {
			ITextViewerExtension5 extension = (ITextViewerExtension5) sourceViewer;
			caret = extension.widgetOffset2ModelOffset(styledText
					.getCaretOffset());
		} else {
			int offset = sourceViewer.getVisibleRegion().getOffset();
			caret = offset + styledText.getCaretOffset();
		}
		IModelElement element = getElementAt(caret, false);
		if (!(element instanceof ISourceReference))
			return null;
		// if (element.getElementType() == IModelElement.IMPORT_DECLARATION) {
		//
		// IImportDeclaration declaration= (IImportDeclaration) element;
		// IImportContainer container= (IImportContainer)
		// declaration.getParent();
		// ISourceRange srcRange= null;
		//
		// try {
		// srcRange= container.getSourceRange();
		// } catch (ModelException e) {
		// }
		//
		// if (srcRange != null && srcRange.getOffset() == caret)
		// return container;
		// }
		return (ISourceReference) element;
	}

	@Override
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);

		IInformationControlCreator informationControlCreator = new IInformationControlCreator() {
			public IInformationControl createInformationControl(Shell shell) {
				boolean cutDown = false;
				//int style = cutDown ? SWT.NONE : (SWT.V_SCROLL | SWT.H_SCROLL);
				// return new DefaultInformationControl(shell, SWT.RESIZE
				// | SWT.TOOL, style, new HTMLTextPresenter(cutDown));
				if (BrowserInformationControl.isAvailable(shell))
					return new BrowserInformationControl(shell,
							JFaceResources.DIALOG_FONT, true);
				else
					return new DefaultInformationControl(shell,
							new HTMLTextPresenter(cutDown));
			}
		};

		fInformationPresenter = new InformationPresenter(
				informationControlCreator);
		fInformationPresenter.setSizeConstraints(60, 10, true, true);
		fInformationPresenter.install(getSourceViewer());
		fInformationPresenter
				.setDocumentPartitioning(IDocument.DEFAULT_CONTENT_TYPE);

		fEditorSelectionChangedListener = new EditorSelectionChangedListener();
		fEditorSelectionChangedListener.install(getSelectionProvider());
		if (true)
			installSemanticHighlighting();
		if (!isEditable()) {
			/*
			 * Manually call semantic highlighting for read only editor, since
			 * usually it's done from reconciler, but
			 * ScriptSourceViewerConfiguration.getReconciler(ISourceViewer)
			 * doesn't create reconciler for read only editor.
			 */
			updateSemanticHighlighting();
		}
		if (occurrencesFinder != null) {
			occurrencesFinder.install();
		}
	}

	/**
	 * React to changed selection.
	 * 
	 * 
	 */
	protected void selectionChanged() {
		if (getSelectionProvider() == null)
			return;
		ISourceReference element = computeHighlightRangeSourceReference();
		if (getPreferenceStore().getBoolean(
				PreferenceConstants.EDITOR_SYNC_OUTLINE_ON_CURSOR_MOVE))
			synchronizeOutlinePage(element);
		setSelection(element, false);
		if (!fSelectionChangedViaGotoAnnotation)
			updateStatusLine();
		fSelectionChangedViaGotoAnnotation = false;
	}

	protected void updateStatusLine() {
		ITextSelection selection = (ITextSelection) getSelectionProvider()
				.getSelection();
		Annotation annotation = getAnnotation(selection.getOffset(),
				selection.getLength());
		setStatusLineErrorMessage(null);
		setStatusLineMessage(null);
		if (annotation != null) {
			updateMarkerViews(annotation);
			if (annotation instanceof IScriptAnnotation
					&& ((IScriptAnnotation) annotation).isProblem())
				setStatusLineMessage(annotation.getText());
		}
	}

	/**
	 * Returns the model element wrapped by this editors input.
	 * 
	 * @return the model element wrapped by this editors input.
	 * 
	 */
	public IModelElement getInputModelElement() {
		return EditorUtility.getEditorInputModelElement(this, false);
	}

	/**
	 * Returns thescriptelement of this editor's input corresponding to the
	 * given IModelElement.
	 * 
	 * @param element
	 *            thescriptelement
	 * @return the corresponding model element
	 */
	protected IModelElement getCorrespondingElement(IModelElement element) {
		return element;
	}

	/**
	 * Returns the most narrow model element including the given offset.
	 * 
	 * @param offset
	 *            the offset inside of the requested element
	 * @return the most narrow model element
	 */
	public IModelElement getElementAt(int offset) {
		return getElementAt(offset, true);
	}

	/**
	 * Returns the most narrow element including the given offset. If
	 * <code>reconcile</code> is <code>true</code> the editor's input element is
	 * reconciled in advance. If it is <code>false</code> this method only
	 * returns a result if the editor's input element does not need to be
	 * reconciled.
	 * 
	 * @param offset
	 *            the offset included by the retrieved element
	 * @param reconcile
	 *            <code>true</code> if working copy should be reconciled
	 * @return the most narrow element which includes the given offset
	 */
	public IModelElement getElementAt(int offset, boolean reconcile) {
		ISourceModule unit = (ISourceModule) getInputModelElement();
		if (unit != null) {
			try {
				if (reconcile) {
					ScriptModelUtil.reconcile(unit);
					return unit.getElementAt(offset);
				} else if (unit.isConsistent())
					return unit.getElementAt(offset);
			} catch (ModelException x) {
				if (!x.isDoesNotExist())
					// DLTKUIPlugin.log(x.getStatus());
					System.err.println(x.getStatus());
				// nothing found, be tolerant and go on
			}
		}
		return null;
	}

	/**
	 * The folding runner.
	 * 
	 * 
	 */
	private ToggleFoldingRunner fFoldingRunner;

	/**
	 * Tells whether the selection changed event is caused by a call to
	 * {@link #gotoAnnotation(boolean)}.
	 * 
	 */
	private boolean fSelectionChangedViaGotoAnnotation;

	/**
	 * Runner that will toggle folding either instantly (if the editor is
	 * visible) or the next time it becomes visible. If a runner is started when
	 * there is already one registered, the registered one is canceled as
	 * toggling folding twice is a no-op.
	 * <p>
	 * The access to the fFoldingRunner field is not thread-safe, it is assumed
	 * that <code>runWhenNextVisible</code> is only called from the UI thread.
	 * </p>
	 * 
	 * 
	 */
	protected final class ToggleFoldingRunner implements IPartListener2 {
		public ToggleFoldingRunner() {
		}

		/**
		 * The workbench page we registered the part listener with, or
		 * <code>null</code>.
		 */
		private IWorkbenchPage fPage;

		/**
		 * Does the actual toggling of projection.
		 */
		private void toggleFolding() {
			ISourceViewer sourceViewer = getSourceViewer();
			if (sourceViewer instanceof ProjectionViewer) {
				ProjectionViewer pv = (ProjectionViewer) sourceViewer;
				if (pv.isProjectionMode() != isFoldingEnabled()) {
					if (pv.canDoOperation(ProjectionViewer.TOGGLE))
						pv.doOperation(ProjectionViewer.TOGGLE);
				}
			}
		}

		/**
		 * Makes sure that the editor's folding state is correct the next time
		 * it becomes visible. If it already is visible, it toggles the folding
		 * state. If not, it either registers a part listener to toggle folding
		 * when the editor becomes visible, or cancels an already registered
		 * runner.
		 */
		public void runWhenNextVisible() {
			// if there is one already: toggling twice is the identity
			if (fFoldingRunner != null) {
				fFoldingRunner.cancel();
				return;
			}
			IWorkbenchPartSite site = getSite();
			if (site != null) {
				IWorkbenchPage page = site.getPage();
				if (!page.isPartVisible(ScriptEditor.this)) {
					// if we're not visible - defer until visible
					fPage = page;
					fFoldingRunner = this;
					page.addPartListener(this);
					return;
				}
			}
			// we're visible - run now
			toggleFolding();
		}

		/**
		 * Remove the listener and clear the field.
		 */
		private void cancel() {
			if (fPage != null) {
				fPage.removePartListener(this);
				fPage = null;
			}
			if (fFoldingRunner == this)
				fFoldingRunner = null;
		}

		/*
		 * @seeorg.eclipse.ui.IPartListener2#partVisible(org.eclipse.ui.
		 * IWorkbenchPartReference)
		 */
		public void partVisible(IWorkbenchPartReference partRef) {
			if (ScriptEditor.this.equals(partRef.getPart(false))) {
				cancel();
				toggleFolding();
			}
		}

		/*
		 * @seeorg.eclipse.ui.IPartListener2#partClosed(org.eclipse.ui.
		 * IWorkbenchPartReference)
		 */
		public void partClosed(IWorkbenchPartReference partRef) {
			if (ScriptEditor.this.equals(partRef.getPart(false))) {
				cancel();
			}
		}

		public void partActivated(IWorkbenchPartReference partRef) {
		}

		public void partBroughtToTop(IWorkbenchPartReference partRef) {
		}

		public void partDeactivated(IWorkbenchPartReference partRef) {
		}

		public void partOpened(IWorkbenchPartReference partRef) {
		}

		public void partHidden(IWorkbenchPartReference partRef) {
		}

		public void partInputChanged(IWorkbenchPartReference partRef) {
		}
	}

	/**
	 * Creates folding structure provider to use in this editor. Default
	 * implementation queries the
	 * <code>org.eclipse.dltk.ui.folding/structureProvider</code> extension
	 * point.
	 * 
	 * @return folding structure provider or <code>null</code>.
	 */
	protected IFoldingStructureProvider createFoldingStructureProvider() {
		return getFoldingStructureProvider();
	}

	/**
	 * Returns folding structure provider.
	 * 
	 * @return
	 */
	@Deprecated
	protected IFoldingStructureProvider getFoldingStructureProvider() {
		return FoldingProviderManager.getStructureProvider(getNatureId());
	}

	private boolean isEditorHoverProperty(String property) {
		return PreferenceConstants.EDITOR_TEXT_HOVER_MODIFIERS.equals(property);
	}

	/*
	 * Update the hovering behavior depending on the preferences.
	 */
	private void updateHoverBehavior() {
		SourceViewerConfiguration configuration = getSourceViewerConfiguration();
		String[] types = configuration
				.getConfiguredContentTypes(getSourceViewer());

		for (int i = 0; i < types.length; i++) {

			String t = types[i];

			ISourceViewer sourceViewer = getSourceViewer();
			if (sourceViewer instanceof ITextViewerExtension2) {
				// Remove existing hovers
				((ITextViewerExtension2) sourceViewer).removeTextHovers(t);

				int[] stateMasks = configuration
						.getConfiguredTextHoverStateMasks(getSourceViewer(), t);

				if (stateMasks != null) {
					for (int j = 0; j < stateMasks.length; j++) {
						int stateMask = stateMasks[j];
						ITextHover textHover = configuration.getTextHover(
								sourceViewer, t, stateMask);
						((ITextViewerExtension2) sourceViewer).setTextHover(
								textHover, t, stateMask);
					}
				} else {
					ITextHover textHover = configuration.getTextHover(
							sourceViewer, t);
					((ITextViewerExtension2) sourceViewer).setTextHover(
							textHover, t,
							ITextViewerExtension2.DEFAULT_HOVER_STATE_MASK);
				}
			} else
				sourceViewer.setTextHover(
						configuration.getTextHover(sourceViewer, t), t);
		}
	}

	protected void handlePreferenceStoreChanged(PropertyChangeEvent event) {
		String property = event.getProperty();
		try {

			ISourceViewer sourceViewer = getSourceViewer();
			if (sourceViewer == null) {
				return;
			}
			boolean newBooleanValue = false;
			Object newValue = event.getNewValue();

			if (isEditorHoverProperty(property))
				updateHoverBehavior();

			if (newValue != null)
				newBooleanValue = Boolean.valueOf(newValue.toString())
						.booleanValue();
			if (PreferenceConstants.EDITOR_SYNC_OUTLINE_ON_CURSOR_MOVE
					.equals(property)) {
				if (newBooleanValue)
					selectionChanged();
				return;
			}

			if (occurrencesFinder != null
					&& occurrencesFinder.handlePreferenceStoreChanged(property,
							newBooleanValue)) {
				return;
			}

			if (CodeFormatterConstants.FORMATTER_TAB_SIZE.equals(property)
					|| CodeFormatterConstants.FORMATTER_INDENTATION_SIZE
							.equals(property)
					|| CodeFormatterConstants.FORMATTER_TAB_CHAR
							.equals(property)) {
				if (CodeFormatterConstants.FORMATTER_TAB_CHAR.equals(property)) {
					if (isTabsToSpacesConversionEnabled())
						installTabsToSpacesConverter();
					else
						uninstallTabsToSpacesConverter();
				}
				updateIndentPrefixes();
				StyledText textWidget = sourceViewer.getTextWidget();
				int tabWidth = getSourceViewerConfiguration().getTabWidth(
						sourceViewer);
				if (textWidget.getTabs() != tabWidth)
					textWidget.setTabs(tabWidth);
				return;
			}
			if (PreferenceConstants.EDITOR_SMART_TAB.equals(property)) {
				if (getPreferenceStore().getBoolean(
						PreferenceConstants.EDITOR_SMART_TAB)) {
					setActionActivationCode(DLTKActionConstants.INDENT_ON_TAB,
							SWT.TAB, -1, SWT.NONE);
				} else {
					removeActionActivationCode(DLTKActionConstants.INDENT_ON_TAB);
				}
			}

			if (isFoldingPropertyEvent(property)
					&& sourceViewer instanceof ProjectionViewer) {
				handleFoldingPropertyEvent(property);
			}

			final ScriptSourceViewerConfiguration ssvc = (ScriptSourceViewerConfiguration) getSourceViewerConfiguration();
			final IContentAssistant c = ((AdaptedSourceViewer) sourceViewer)
					.getContentAssistant();
			if (c instanceof ContentAssistant) {
				ssvc.changeContentAssistantConfiguration((ContentAssistant) c,
						event);
			}
			ssvc.handlePropertyChangeEvent(event);
		} finally {
			super.handlePreferenceStoreChanged(event);
		}
		if (AbstractDecoratedTextEditorPreferenceConstants.SHOW_RANGE_INDICATOR
				.equals(property)) {
			// superclass already installed the range indicator
			Object newValue = event.getNewValue();
			ISourceViewer viewer = getSourceViewer();
			if (newValue != null && viewer != null) {
				if (Boolean.valueOf(newValue.toString()).booleanValue()) {
					// adjust the highlightrange in order to get the magnet
					// right after changing the selection
					Point selection = viewer.getSelectedRange();
					adjustHighlightRange(selection.x, selection.y);
				}
			}
		}
	}

	protected boolean affectsTextPresentation(PropertyChangeEvent event) {
		return ((ScriptSourceViewerConfiguration) getSourceViewerConfiguration())
				.affectsTextPresentation(event)
				|| super.affectsTextPresentation(event);
	}

	protected void handleFoldingPropertyEvent(String property) {
		// NOTE: 'initially fold' preferences do not require handling
		if (PreferenceConstants.EDITOR_FOLDING_ENABLED.equals(property)) {
			ToggleFoldingRunner runner = new ToggleFoldingRunner();
			runner.runWhenNextVisible();
		} else {
			fProjectionModelUpdater.initialize(false);
		}
	}

	protected final boolean isFoldingPropertyEvent(String property) {
		if (isHandledPropertyEvent(property, GLOBAL_FOLDING_PROPERTIES)) {
			return true;
		}

		if (isHandledPropertyEvent(property, getFoldingEventPreferenceKeys())) {
			return true;
		}

		return false;
	}

	/**
	 * Returns a string array containing the language specific folding
	 * preference keys that should be handed when a property change event is
	 * fired.
	 * 
	 * <p>
	 * Default implementation returns an empty array. Subclasses should override
	 * this method to return folding keys that are language specific.
	 * </p>
	 */
	protected String[] getFoldingEventPreferenceKeys() {
		return CharOperation.NO_STRINGS;
	}

	/**
	 * Text navigation action to navigate to the next sub-word.
	 * 
	 * 
	 */
	protected abstract class NextSubWordAction extends TextNavigationAction {
		protected DLTKWordIterator fIterator = new DLTKWordIterator();

		/**
		 * Creates a new next sub-word action.
		 * 
		 * @param code
		 *            Action code for the default operation. Must be an action
		 *            code from
		 * @see org.eclipse.swt.custom.ST.
		 */
		protected NextSubWordAction(int code) {
			super(getSourceViewer().getTextWidget(), code);
		}

		/*
		 * @see org.eclipse.jface.action.IAction#run()
		 */
		public void run() {
			// Check whether we are in ascriptcode partition and the preference
			// is enabled
			final IPreferenceStore store = getPreferenceStore();
			if (!store
					.getBoolean(PreferenceConstants.EDITOR_SUB_WORD_NAVIGATION)) {
				super.run();
				return;
			}
			final ISourceViewer viewer = getSourceViewer();
			final IDocument document = viewer.getDocument();
			fIterator
					.setText((CharacterIterator) new DocumentCharacterIterator(
							document));
			int position = widgetOffset2ModelOffset(viewer, viewer
					.getTextWidget().getCaretOffset());
			if (position == -1)
				return;
			int next = findNextPosition(position);
			if (next != BreakIterator.DONE) {
				setCaretPosition(next);
				getTextWidget().showSelection();
				fireSelectionChanged();
			}
		}

		/**
		 * Finds the next position after the given position.
		 * 
		 * @param position
		 *            the current position
		 * @return the next position
		 */
		protected int findNextPosition(int position) {
			ISourceViewer viewer = getSourceViewer();
			int widget = -1;
			while (position != BreakIterator.DONE && widget == -1) { // TODO:
				// optimize
				position = fIterator.following(position);
				if (position != BreakIterator.DONE)
					widget = modelOffset2WidgetOffset(viewer, position);
			}
			return position;
		}

		/**
		 * Sets the caret position to the sub-word boundary given with
		 * <code>position</code>.
		 * 
		 * @param position
		 *            Position where the action should move the caret
		 */
		protected abstract void setCaretPosition(int position);
	}

	/**
	 * Text navigation action to navigate to the next sub-word.
	 */
	protected class NavigateNextSubWordAction extends NextSubWordAction {
		/**
		 * Creates a new navigate next sub-word action.
		 */
		public NavigateNextSubWordAction() {
			super(ST.WORD_NEXT);
		}

		@Override
		protected void setCaretPosition(final int position) {
			getTextWidget().setCaretOffset(
					modelOffset2WidgetOffset(getSourceViewer(), position));
		}
	}

	/**
	 * Text operation action to delete the next sub-word.
	 */
	protected class DeleteNextSubWordAction extends NextSubWordAction implements
			IUpdate {
		/**
		 * Creates a new delete next sub-word action.
		 */
		public DeleteNextSubWordAction() {
			super(ST.DELETE_WORD_NEXT);
		}

		@Override
		protected void setCaretPosition(final int position) {
			if (!validateEditorInputState())
				return;
			final ISourceViewer viewer = getSourceViewer();
			final int caret, length;
			Point selection = viewer.getSelectedRange();
			if (selection.y != 0) {
				caret = selection.x;
				length = selection.y;
			} else {
				caret = widgetOffset2ModelOffset(viewer, viewer.getTextWidget()
						.getCaretOffset());
				length = position - caret;
			}
			try {
				viewer.getDocument().replace(caret, length, ""); //$NON-NLS-1$
			} catch (BadLocationException exception) {
				// Should not happen
			}
		}

		@Override
		protected int findNextPosition(int position) {
			return fIterator.following(position);
		}

		/*
		 * @see org.eclipse.ui.texteditor.IUpdate#update()
		 */
		public void update() {
			setEnabled(isEditorInputModifiable());
		}
	}

	/**
	 * Text operation action to select the next sub-word.
	 * 
	 * 
	 */
	protected class SelectNextSubWordAction extends NextSubWordAction {
		/**
		 * Creates a new select next sub-word action.
		 */
		public SelectNextSubWordAction() {
			super(ST.SELECT_WORD_NEXT);
		}

		@Override
		protected void setCaretPosition(final int position) {
			final ISourceViewer viewer = getSourceViewer();
			final StyledText text = viewer.getTextWidget();
			if (text != null && !text.isDisposed()) {
				final Point selection = text.getSelection();
				final int caret = text.getCaretOffset();
				final int offset = modelOffset2WidgetOffset(viewer, position);
				if (caret == selection.x)
					text.setSelectionRange(selection.y, offset - selection.y);
				else
					text.setSelectionRange(selection.x, offset - selection.x);
			}
		}
	}

	/**
	 * Text navigation action to navigate to the previous sub-word.
	 * 
	 * 
	 */
	protected abstract class PreviousSubWordAction extends TextNavigationAction {
		protected DLTKWordIterator fIterator = new DLTKWordIterator();

		/**
		 * Creates a new previous sub-word action.
		 * 
		 * @param code
		 *            Action code for the default operation. Must be an action
		 *            code from
		 * @see org.eclipse.swt.custom.ST.
		 */
		protected PreviousSubWordAction(final int code) {
			super(getSourceViewer().getTextWidget(), code);
		}

		/*
		 * @see org.eclipse.jface.action.IAction#run()
		 */
		public void run() {
			// Check whether we are in ascriptcode partition and the preference
			// is enabled
			final IPreferenceStore store = getPreferenceStore();
			if (!store
					.getBoolean(PreferenceConstants.EDITOR_SUB_WORD_NAVIGATION)) {
				super.run();
				return;
			}
			final ISourceViewer viewer = getSourceViewer();
			final IDocument document = viewer.getDocument();
			fIterator
					.setText((CharacterIterator) new DocumentCharacterIterator(
							document));
			int position = widgetOffset2ModelOffset(viewer, viewer
					.getTextWidget().getCaretOffset());
			if (position == -1)
				return;
			int previous = findPreviousPosition(position);
			if (previous != BreakIterator.DONE) {
				setCaretPosition(previous);
				getTextWidget().showSelection();
				fireSelectionChanged();
			}
		}

		/**
		 * Finds the previous position before the given position.
		 * 
		 * @param position
		 *            the current position
		 * @return the previous position
		 */
		protected int findPreviousPosition(int position) {
			ISourceViewer viewer = getSourceViewer();
			int widget = -1;
			while (position != BreakIterator.DONE && widget == -1) { // TODO:
				// optimize
				position = fIterator.preceding(position);
				if (position != BreakIterator.DONE)
					widget = modelOffset2WidgetOffset(viewer, position);
			}
			return position;
		}

		/**
		 * Sets the caret position to the sub-word boundary given with
		 * <code>position</code>.
		 * 
		 * @param position
		 *            Position where the action should move the caret
		 */
		protected abstract void setCaretPosition(int position);
	}

	/**
	 * Text navigation action to navigate to the previous sub-word.
	 */
	protected class NavigatePreviousSubWordAction extends PreviousSubWordAction {
		/**
		 * Creates a new navigate previous sub-word action.
		 */
		public NavigatePreviousSubWordAction() {
			super(ST.WORD_PREVIOUS);
		}

		@Override
		protected void setCaretPosition(final int position) {
			getTextWidget().setCaretOffset(
					modelOffset2WidgetOffset(getSourceViewer(), position));
		}
	}

	/**
	 * Text operation action to delete the previous sub-word.
	 */
	protected class DeletePreviousSubWordAction extends PreviousSubWordAction
			implements IUpdate {
		/**
		 * Creates a new delete previous sub-word action.
		 */
		public DeletePreviousSubWordAction() {
			super(ST.DELETE_WORD_PREVIOUS);
		}

		@Override
		protected void setCaretPosition(int position) {
			if (!validateEditorInputState())
				return;
			final int length;
			final ISourceViewer viewer = getSourceViewer();
			Point selection = viewer.getSelectedRange();
			if (selection.y != 0) {
				position = selection.x;
				length = selection.y;
			} else {
				length = widgetOffset2ModelOffset(viewer, viewer
						.getTextWidget().getCaretOffset())
						- position;
			}
			try {
				viewer.getDocument().replace(position, length, ""); //$NON-NLS-1$
			} catch (BadLocationException exception) {
				// Should not happen
			}
		}

		@Override
		protected int findPreviousPosition(int position) {
			return fIterator.preceding(position);
		}

		public void update() {
			setEnabled(isEditorInputModifiable());
		}
	}

	/**
	 * Text operation action to select the previous sub-word.
	 */
	protected class SelectPreviousSubWordAction extends PreviousSubWordAction {
		/**
		 * Creates a new select previous sub-word action.
		 */
		public SelectPreviousSubWordAction() {
			super(ST.SELECT_WORD_PREVIOUS);
		}

		@Override
		protected void setCaretPosition(final int position) {
			final ISourceViewer viewer = getSourceViewer();
			final StyledText text = viewer.getTextWidget();
			if (text != null && !text.isDisposed()) {
				final Point selection = text.getSelection();
				final int caret = text.getCaretOffset();
				final int offset = modelOffset2WidgetOffset(viewer, position);
				if (caret == selection.x)
					text.setSelectionRange(selection.y, offset - selection.y);
				else
					text.setSelectionRange(selection.x, offset - selection.x);
			}
		}
	}

	/*
	 * @see AbstractTextEditor#createNavigationActions()
	 */
	@Override
	protected void createNavigationActions() {
		super.createNavigationActions();
		final StyledText textWidget = getSourceViewer().getTextWidget();

		IAction action = new NavigatePreviousSubWordAction();
		action.setActionDefinitionId(ITextEditorActionDefinitionIds.WORD_PREVIOUS);
		setAction(ITextEditorActionDefinitionIds.WORD_PREVIOUS, action);
		textWidget.setKeyBinding(SWT.CTRL | SWT.ARROW_LEFT, SWT.NULL);

		action = new NavigateNextSubWordAction();
		action.setActionDefinitionId(ITextEditorActionDefinitionIds.WORD_NEXT);
		setAction(ITextEditorActionDefinitionIds.WORD_NEXT, action);
		textWidget.setKeyBinding(SWT.CTRL | SWT.ARROW_RIGHT, SWT.NULL);

		action = new SelectPreviousSubWordAction();
		action.setActionDefinitionId(ITextEditorActionDefinitionIds.SELECT_WORD_PREVIOUS);
		setAction(ITextEditorActionDefinitionIds.SELECT_WORD_PREVIOUS, action);
		textWidget.setKeyBinding(SWT.CTRL | SWT.SHIFT | SWT.ARROW_LEFT,
				SWT.NULL);

		action = new SelectNextSubWordAction();
		action.setActionDefinitionId(ITextEditorActionDefinitionIds.SELECT_WORD_NEXT);
		setAction(ITextEditorActionDefinitionIds.SELECT_WORD_NEXT, action);
		textWidget.setKeyBinding(SWT.CTRL | SWT.SHIFT | SWT.ARROW_RIGHT,
				SWT.NULL);

		action = new DeletePreviousSubWordAction();
		action.setActionDefinitionId(ITextEditorActionDefinitionIds.DELETE_PREVIOUS_WORD);
		setAction(ITextEditorActionDefinitionIds.DELETE_PREVIOUS_WORD, action);
		textWidget.setKeyBinding(SWT.CTRL | SWT.BS, SWT.NULL);
		markAsStateDependentAction(
				ITextEditorActionDefinitionIds.DELETE_PREVIOUS_WORD, true);

		action = new DeleteNextSubWordAction();
		action.setActionDefinitionId(ITextEditorActionDefinitionIds.DELETE_NEXT_WORD);
		setAction(ITextEditorActionDefinitionIds.DELETE_NEXT_WORD, action);
		textWidget.setKeyBinding(SWT.CTRL | SWT.DEL, SWT.NULL);
		markAsStateDependentAction(
				ITextEditorActionDefinitionIds.DELETE_NEXT_WORD, true);
	}

	@Override
	protected final ISourceViewer createSourceViewer(Composite parent,
			IVerticalRuler verticalRuler, int styles) {

		IPreferenceStore store = getPreferenceStore();
		ISourceViewer viewer = createScriptSourceViewer(parent, verticalRuler,
				getOverviewRuler(), isOverviewRulerVisible(), styles, store);

		if (DLTKCore.DEBUG) {
			System.err.println("Create help contexts"); //$NON-NLS-1$
		}
		// ScriptUIHelp.setHelp(this, viewer.getTextWidget(),
		// IScriptHelpContextIds.JAVA_EDITOR);

		ScriptSourceViewer scriptSourceViewer = null;
		if (viewer instanceof ScriptSourceViewer)
			scriptSourceViewer = (ScriptSourceViewer) viewer;

		/*
		 * This is a performance optimization to reduce the computation of the
		 * text presentation triggered by {@link #setVisibleDocument(IDocument)}
		 */
		if (scriptSourceViewer != null
				&& isFoldingEnabled()
				&& (store == null || !store
						.getBoolean(PreferenceConstants.EDITOR_SHOW_SEGMENTS)))
			scriptSourceViewer.prepareDelayedProjection();

		ProjectionViewer projectionViewer = (ProjectionViewer) viewer;
		fProjectionSupport = new ProjectionSupport(projectionViewer,
				getAnnotationAccess(), getSharedColors());
		fProjectionSupport
				.addSummarizableAnnotationType("org.eclipse.ui.workbench.texteditor.error"); //$NON-NLS-1$
		fProjectionSupport
				.addSummarizableAnnotationType("org.eclipse.ui.workbench.texteditor.warning"); //$NON-NLS-1$
		final IDLTKLanguageToolkit toolkit = this.getLanguageToolkit();
		fProjectionSupport
				.setHoverControlCreator(new IInformationControlCreator() {
					public IInformationControl createInformationControl(
							Shell shell) {
						int shellStyle = SWT.TOOL | SWT.NO_TRIM
								| getOrientation();
						String statusFieldText = EditorsUI
								.getTooltipAffordanceString();
						return new SourceViewerInformationControl(shell,
								shellStyle, SWT.NONE, statusFieldText, toolkit);
					}
				});
		fProjectionSupport
				.setInformationPresenterControlCreator(new IInformationControlCreator() {
					public IInformationControl createInformationControl(
							Shell shell) {
						int shellStyle = SWT.RESIZE | SWT.TOOL
								| getOrientation();
						int style = SWT.V_SCROLL | SWT.H_SCROLL;
						return new SourceViewerInformationControl(shell,
								shellStyle, style, toolkit);
					}
				});

		fProjectionSupport.install();

		fProjectionModelUpdater = createFoldingStructureProvider();
		if (fProjectionModelUpdater != null)
			fProjectionModelUpdater.install(this, projectionViewer,
					getPreferenceStore());

		// ensure source viewer decoration support has been created and
		// configured
		getSourceViewerDecorationSupport(viewer);

		return viewer;
	}

	protected ISourceViewer createScriptSourceViewer(Composite parent,
			IVerticalRuler verticalRuler, IOverviewRuler overviewRuler,
			boolean isOverviewRulerVisible, int styles, IPreferenceStore store) {
		return new AdaptedSourceViewer(parent, verticalRuler,
				getOverviewRuler(), isOverviewRulerVisible(), styles, store);
	}

	/**
	 * Resets the foldings structure according to the folding preferences.
	 */
	public void resetProjection() {
		if (fProjectionModelUpdater != null) {
			fProjectionModelUpdater.initialize();
		}
	}

	/**
	 * Collapses all foldable members if supported by the folding structure
	 * provider.
	 * 
	 * 
	 */
	public void collapseMembers() {
		if (fProjectionModelUpdater instanceof IFoldingStructureProviderExtension) {
			IFoldingStructureProviderExtension extension = (IFoldingStructureProviderExtension) fProjectionModelUpdater;
			extension.collapseMembers();
		}
	}

	/**
	 * Collapses all foldable comments if supported by the folding structure
	 * provider.
	 * 
	 * 
	 */
	public void collapseComments() {
		if (fProjectionModelUpdater instanceof IFoldingStructureProviderExtension) {
			IFoldingStructureProviderExtension extension = (IFoldingStructureProviderExtension) fProjectionModelUpdater;
			extension.collapseComments();
		}
	}

	/*
	 * @see AbstractTextEditor#rulerContextMenuAboutToShow(IMenuManager)
	 */
	@Override
	protected void rulerContextMenuAboutToShow(IMenuManager menu) {
		super.rulerContextMenuAboutToShow(menu);
		IMenuManager foldingMenu = new MenuManager(
				DLTKEditorMessages.Editor_FoldingMenu_name, "projection"); //$NON-NLS-1$
		menu.appendToGroup(ITextEditorActionConstants.GROUP_RULERS, foldingMenu);

		IAction action = getAction("FoldingToggle"); //$NON-NLS-1$
		if (action != null) {
			foldingMenu.add(action);
		}
		action = getAction("FoldingExpandAll"); //$NON-NLS-1$
		if (action != null) {
			foldingMenu.add(action);
		}
		action = getAction("FoldingCollapseAll"); //$NON-NLS-1$
		if (action != null) {
			foldingMenu.add(action);
		}
		action = getAction("FoldingRestore"); //$NON-NLS-1$
		if (action != null) {
			foldingMenu.add(action);
		}
		action = getAction("FoldingCollapseMembers"); //$NON-NLS-1$
		if (action != null) {
			foldingMenu.add(action);
		}
		action = getAction("FoldingCollapseComments"); //$NON-NLS-1$
		if (action != null) {
			foldingMenu.add(action);
		}
	}

	/*
	 * @see org.eclipse.ui.texteditor.AbstractTextEditor#performRevert()
	 */
	@Override
	protected void performRevert() {
		ProjectionViewer projectionViewer = (ProjectionViewer) getSourceViewer();
		projectionViewer.setRedraw(false);
		try {

			boolean projectionMode = projectionViewer.isProjectionMode();
			if (projectionMode) {
				projectionViewer.disableProjection();
				if (fProjectionModelUpdater != null)
					fProjectionModelUpdater.uninstall();
			}

			super.performRevert();

			if (projectionMode) {
				if (fProjectionModelUpdater != null)
					fProjectionModelUpdater.install(this, projectionViewer,
							getPreferenceStore());
				projectionViewer.enableProjection();
			}

		} finally {
			projectionViewer.setRedraw(true);
		}
	}

	protected String getNatureId() {
		return getLanguageToolkit().getNatureId();
	}

	public abstract IDLTKLanguageToolkit getLanguageToolkit();

	protected IDLTKUILanguageToolkit getUILanguageToolkit() {
		return DLTKUILanguageManager.getLanguageToolkit(getNatureId());
	}

	/**
	 * Return identifier of call hierarchy. Used by call hierarchy actions.
	 * 
	 * @return
	 */
	public String getCallHierarchyID() {
		return null;
	}

	/*
	 * @see AbstractTextEditor#performSave(boolean, IProgressMonitor)
	 */
	@Override
	protected void performSave(boolean overwrite,
			IProgressMonitor progressMonitor) {
		IDocumentProvider p = getDocumentProvider();
		if (p instanceof ISourceModuleDocumentProvider) {
			ISourceModuleDocumentProvider cp = (ISourceModuleDocumentProvider) p;
			cp.setSavePolicy(fSavePolicy);
		}
		try {
			super.performSave(overwrite, progressMonitor);
		} finally {
			if (p instanceof ISourceModuleDocumentProvider) {
				ISourceModuleDocumentProvider cp = (ISourceModuleDocumentProvider) p;
				cp.setSavePolicy(null);
			}
		}
	}

	/*
	 * @see AbstractTextEditor#doSave(IProgressMonitor)
	 */
	@Override
	public void doSave(IProgressMonitor progressMonitor) {

		IDocumentProvider p = getDocumentProvider();
		if (p == null) {
			// editor has been closed
			return;
		}

		if (p.isDeleted(getEditorInput())) {

			if (isSaveAsAllowed()) {

				/*
				 * 1GEUSSR: ITPUI:ALL - User should never loose changes made in
				 * the editors. Changed Behavior to make sure that if called
				 * inside a regular save (because of deletion of input element)
				 * there is a way to report back to the caller.
				 */
				performSaveAs(progressMonitor);

			} else {

				/*
				 * 1GF5YOX: ITPJUI:ALL - Save of delete file claims it's still
				 * there Missing resources.
				 */
				Shell shell = getSite().getShell();
				MessageDialog
						.openError(
								shell,
								DLTKEditorMessages.SourceModuleEditor_error_saving_title1,
								DLTKEditorMessages.SourceModuleEditor_error_saving_message1);
			}

		} else {

			setStatusLineErrorMessage(null);

			updateState(getEditorInput());
			validateState(getEditorInput());

			IWorkingCopyManager manager = DLTKUIPlugin.getDefault()
					.getWorkingCopyManager();
			ISourceModule unit = manager.getWorkingCopy(getEditorInput());

			if (unit != null) {
				// synchronized (unit) {
				performSave(false, progressMonitor);
				// }
			} else
				performSave(false, progressMonitor);
		}
	}

	/**
	 * Returns the signed current selection. The length will be negative if the
	 * resulting selection is right-to-left (RtoL).
	 * <p>
	 * The selection offset is model based.
	 * </p>
	 * 
	 * @param sourceViewer
	 *            the source viewer
	 * @return a region denoting the current signed selection, for a resulting
	 *         RtoL selections length is < 0
	 */
	protected IRegion getSignedSelection(ISourceViewer sourceViewer) {
		StyledText text = sourceViewer.getTextWidget();
		Point selection = text.getSelectionRange();

		if (text.getCaretOffset() == selection.x) {
			selection.x = selection.x + selection.y;
			selection.y = -selection.y;
		}

		selection.x = widgetOffset2ModelOffset(sourceViewer, selection.x);

		return new Region(selection.x, selection.y);
	}

	protected final static char[] BRACKETS = { '{', '}', '(', ')', '[', ']' };

	protected static boolean isBracket(char character) {
		for (int i = 0; i != BRACKETS.length; ++i)
			if (character == BRACKETS[i])
				return true;
		return false;
	}

	protected static boolean isSurroundedByBrackets(IDocument document,
			int offset) {
		if (offset == 0 || offset == document.getLength())
			return false;

		try {
			return isBracket(document.getChar(offset - 1))
					&& isBracket(document.getChar(offset));

		} catch (BadLocationException e) {
			return false;
		}
	}

	private ICharacterPairMatcher fBracketMatcher;

	/**
	 * Returns the bracket matcher for this editor, delegates to
	 * {@link #createBracketMatcher()} to actually create it.
	 * 
	 * @return the bracket matcher or <code>null</code>
	 */
	protected final ICharacterPairMatcher getBracketMatcher() {
		if (fBracketMatcher == null) {
			fBracketMatcher = createBracketMatcher();
		}
		return fBracketMatcher;
	}

	/**
	 * Override in your editor class to create bracket matcher for your
	 * language.
	 * 
	 * @return
	 */
	protected ICharacterPairMatcher createBracketMatcher() {
		return null;
	}

	@Override
	protected void configureSourceViewerDecorationSupport(
			SourceViewerDecorationSupport support) {
		configureBracketMatcher(support);
		super.configureSourceViewerDecorationSupport(support);
	}

	protected void configureBracketMatcher(SourceViewerDecorationSupport support) {
		final ICharacterPairMatcher bracketMatcher = getBracketMatcher();
		if (bracketMatcher != null) {
			support.setCharacterPairMatcher(bracketMatcher);
			support.setMatchingCharacterPainterPreferenceKeys(
					MATCHING_BRACKETS, MATCHING_BRACKETS_COLOR);
		}
	}

	/**
	 * Jumps to the matching bracket.
	 */
	public void gotoMatchingBracket() {
		final ICharacterPairMatcher bracketMatcher = getBracketMatcher();
		if (bracketMatcher == null) {
			return;
		}
		ISourceViewer sourceViewer = getSourceViewer();
		IDocument document = sourceViewer.getDocument();
		if (document == null)
			return;

		IRegion selection = getSignedSelection(sourceViewer);

		int selectionLength = Math.abs(selection.getLength());
		if (selectionLength > 1) {
			setStatusLineErrorMessage(DLTKEditorMessages.ScriptEditor_nobracketSelected);
			sourceViewer.getTextWidget().getDisplay().beep();
			return;
		}

		// #26314
		int sourceCaretOffset = selection.getOffset() + selection.getLength();
		if (isSurroundedByBrackets(document, sourceCaretOffset))
			sourceCaretOffset -= selection.getLength();

		IRegion region = bracketMatcher.match(document, sourceCaretOffset);
		if (region == null) {
			setStatusLineErrorMessage(DLTKEditorMessages.ScriptEditor_noMatchingBracketFound);
			sourceViewer.getTextWidget().getDisplay().beep();
			return;
		}

		int offset = region.getOffset();
		int length = region.getLength();

		if (length < 1)
			return;

		int anchor = bracketMatcher.getAnchor();
		// http://dev.eclipse.org/bugs/show_bug.cgi?id=34195
		int targetOffset = (ICharacterPairMatcher.RIGHT == anchor) ? offset + 1
				: offset + length;

		boolean visible = false;
		if (sourceViewer instanceof ITextViewerExtension5) {
			ITextViewerExtension5 extension = (ITextViewerExtension5) sourceViewer;
			visible = (extension.modelOffset2WidgetOffset(targetOffset) > -1);
		} else {
			IRegion visibleRegion = sourceViewer.getVisibleRegion();
			// http://dev.eclipse.org/bugs/show_bug.cgi?id=34195
			visible = (targetOffset >= visibleRegion.getOffset() && targetOffset <= visibleRegion
					.getOffset() + visibleRegion.getLength());
		}

		if (!visible) {
			setStatusLineErrorMessage(DLTKEditorMessages.ScriptEditor_matchingBracketIsOutsideSelectedElement);
			sourceViewer.getTextWidget().getDisplay().beep();
			return;
		}

		if (selection.getLength() < 0)
			targetOffset -= selection.getLength();

		sourceViewer.setSelectedRange(targetOffset, selection.getLength());
		sourceViewer.revealRange(targetOffset, selection.getLength());
	}

	public void updatedTitleImage(Image image) {
		setTitleImage(image);
	}

	private ListenerList fReconcilingListeners = new ListenerList(
			ListenerList.IDENTITY);

	/**
	 * Mutex for the reconciler. See
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=63898 for a description of
	 * the problem.
	 * <p>
	 * XXX remove once the underlying problem
	 * (https://bugs.eclipse.org/bugs/show_bug.cgi?id=66176) is solved.
	 * </p>
	 */
	private final Object fReconcilerLock = new Object();

	public void aboutToBeReconciled() {

		// Notify AST provider
		// JavaPlugin.getDefault().getASTProvider().aboutToBeReconciled(
		// getInputJavaElement());

		// Notify listeners
		Object[] listeners = fReconcilingListeners.getListeners();
		for (int i = 0, length = listeners.length; i < length; ++i)
			((IScriptReconcilingListener) listeners[i]).aboutToBeReconciled();
	}

	/*
	 * @see
	 * org.eclipse.jdt.internal.ui.text.java.IJavaReconcilingListener#reconciled
	 * (CompilationUnit, boolean, IProgressMonitor)
	 * 
	 * @since 3.0
	 */
	public void reconciled(ISourceModule ast, boolean forced,
			IProgressMonitor progressMonitor) {

		// see: https://bugs.eclipse.org/bugs/show_bug.cgi?id=58245
		// JavaPlugin javaPlugin= JavaPlugin.getDefault();
		// if (javaPlugin == null)
		// return;
		//
		// // Always notify AST provider
		// javaPlugin.getASTProvider().reconciled(ast, getInputJavaElement(),
		// progressMonitor);

		// Notify listeners
		Object[] listeners = fReconcilingListeners.getListeners();
		for (int i = 0, length = listeners.length; i < length; ++i)
			((IScriptReconcilingListener) listeners[i]).reconciled(ast, forced,
					progressMonitor);

		// Update Outline page selection
		if (!forced && !progressMonitor.isCanceled()) {
			Shell shell = getSite().getShell();
			if (shell != null && !shell.isDisposed()) {
				shell.getDisplay().asyncExec(new Runnable() {
					public void run() {
						selectionChanged();
					}
				});
			}
		}
	}

	public void addReconcileListener(
			IScriptReconcilingListener semanticHighlightingReconciler) {
		fReconcilingListeners.add(semanticHighlightingReconciler);
	}

	public void removeReconcileListener(
			IScriptReconcilingListener semanticHighlightingReconciler) {
		fReconcilingListeners.remove(semanticHighlightingReconciler);
	}

	private SemanticHighlightingManager fSemanticManager;

	private void installSemanticHighlighting() {
		ScriptTextTools textTools = getTextTools();
		if (fSemanticManager == null && textTools != null) {
			final ISemanticHighlightingUpdater updater = textTools
					.getSemanticPositionUpdater(getNatureId());
			if (updater != null) {
				fSemanticManager = new SemanticHighlightingManager(updater);
				fSemanticManager.install(this,
						(ScriptSourceViewer) getSourceViewer(),
						textTools.getColorManager(), getPreferenceStore());
			}
		}
	}

	private void updateSemanticHighlighting() {
		final IModelElement element = getInputModelElement();
		if (!(element instanceof ISourceModule)) {
			return;
		}
		Job job = new Job(
				DLTKEditorMessages.ScriptEditor_InitializeSemanticHighlighting) {
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				if (fSemanticManager != null) {
					SemanticHighlightingReconciler reconciler = fSemanticManager
							.getReconciler();
					if (reconciler != null)
						reconciler.reconciled((ISourceModule) element, false,
								monitor);
				}
				return Status.OK_STATUS;
			}
		};
		job.setPriority(Job.DECORATE);
		job.setSystem(true);
		job.schedule();
	}

	/**
	 * Uninstall Semantic Highlighting.
	 * 
	 * @since 3.0
	 */
	private void uninstallSemanticHighlighting() {
		if (fSemanticManager != null) {
			fSemanticManager.uninstall();
			fSemanticManager = null;
		}
	}

	@Override
	public int getOrientation() {
		return SWT.LEFT_TO_RIGHT;
	}

	@Override
	protected String[] collectContextMenuPreferencePages() {
		final List<String> result = new ArrayList<String>();
		final IDLTKUILanguageToolkit uiToolkit = getUILanguageToolkit();
		addPages(result, uiToolkit.getEditorPreferencePages());
		addPages(result, super.collectContextMenuPreferencePages());
		return result.toArray(new String[result.size()]);
	}

	private void addPages(final List<String> result, final String[] pages) {
		if (pages != null) {
			for (int i = 0; i < pages.length; ++i) {
				if (!result.contains(pages[i])) {
					result.add(pages[i]);
				}
			}
		}
	}

	/*
	 * @see AbstractDecoratedTextEditor#isTabsToSpacesConversionEnabled()
	 */
	@Override
	protected boolean isTabsToSpacesConversionEnabled() {
		return getPreferenceStore() != null
				&& CodeFormatterConstants.SPACE.equals(getPreferenceStore()
						.getString(CodeFormatterConstants.FORMATTER_TAB_CHAR));
	}

	private boolean isHandledPropertyEvent(String property, String[] handled) {
		for (int i = 0; i < handled.length; i++) {
			if (handled[i].equals(property)) {
				return true;
			}
		}

		return false;
	}

	protected String getSymbolicFontName() {
		return getFontPropertyPreferenceKey();
	}

	/*
	 * Increase visibility for this package - called from {@link
	 * OccurrencesFinder}
	 */
	protected IProgressMonitor getProgressMonitor() {
		return super.getProgressMonitor();
	}

}
