/*******************************************************************************
 * Copyright (c) 2000, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.dltk.internal.corext.refactoring.rename;

import java.util.StringTokenizer;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.dltk.core.IField;
import org.eclipse.dltk.core.ILocalVariable;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.core.manipulation.IScriptRefactorings;
import org.eclipse.dltk.core.manipulation.SourceModuleChange;
import org.eclipse.dltk.internal.core.manipulation.Messages;
import org.eclipse.dltk.internal.core.manipulation.ScriptManipulationPlugin;
import org.eclipse.dltk.internal.core.refactoring.descriptors.RenameModelElementDescriptor;
import org.eclipse.dltk.internal.corext.refactoring.RefactoringCoreMessages;
import org.eclipse.dltk.internal.corext.refactoring.ScriptRefactoringArguments;
import org.eclipse.dltk.internal.corext.refactoring.ScriptRefactoringDescriptor;
import org.eclipse.dltk.internal.corext.refactoring.code.ScriptableRefactoring;
import org.eclipse.dltk.internal.corext.refactoring.participants.ScriptProcessors;
import org.eclipse.dltk.internal.corext.refactoring.tagging.IReferenceUpdating;
import org.eclipse.dltk.internal.corext.refactoring.util.ResourceUtil;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.RefactoringChangeDescriptor;
import org.eclipse.ltk.core.refactoring.RefactoringDescriptor;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.participants.RefactoringArguments;
import org.eclipse.ltk.core.refactoring.participants.RenameArguments;

public abstract class RenameModelElementProcessor extends ScriptRenameProcessor implements IReferenceUpdating {
	public static final String IDENTIFIER= "org.eclipse.dltk.javascript.renameLocalVariableProcessor"; //$NON-NLS-1$

	protected IModelElement fLocalVariable;
	protected ISourceModule fCu;

	//the following fields are set or modified after the construction
	protected boolean fUpdateReferences;
	protected String fCurrentName;
	//private CompilationUnit fCompilationUnitNode;
	//private VariableDeclaration fTempDeclarationNode;
	protected SourceModuleChange fChange;

	//private boolean fIsComposite is always false
	//private GroupCategorySet fCategorySet;
	//private TextChangeManager fChangeManager;
	//private RenameAnalyzeUtil.LocalAnalyzePackage fLocalAnalyzePackage;
	
	public RenameModelElementProcessor(IModelElement localVariable) {
		fLocalVariable = localVariable;
		fCu = (ISourceModule)fLocalVariable.getAncestor(IModelElement.SOURCE_MODULE);
	}


	public RefactoringStatus initialize(RefactoringArguments arguments) {
		if (!(arguments instanceof ScriptRefactoringArguments))
			return RefactoringStatus.createFatalErrorStatus(RefactoringCoreMessages.InitializableRefactoring_inacceptable_arguments);
		final ScriptRefactoringArguments extended= (ScriptRefactoringArguments) arguments;
		final String handle= extended.getAttribute(ScriptRefactoringDescriptor.ATTRIBUTE_INPUT);
		if (handle != null) {
			final IModelElement element= ScriptRefactoringDescriptor.handleToElement(extended.getProject(), handle, false);
			if (element != null && element.exists()) {
				if (element.getElementType() == IModelElement.SOURCE_MODULE) {
					fCu = (ISourceModule) element;
				} else if (element.getElementType() == IModelElement.LOCAL_VARIABLE) {
					fLocalVariable= (ILocalVariable) element;
					fCu = (ISourceModule) fLocalVariable.getAncestor(IModelElement.SOURCE_MODULE);
					if (fCu == null)
						return ScriptableRefactoring.createInputFatalStatus(element, getProcessorName(), IScriptRefactorings.RENAME_LOCAL_VARIABLE);
				} else
					return ScriptableRefactoring.createInputFatalStatus(element, getProcessorName(), IScriptRefactorings.RENAME_LOCAL_VARIABLE);
			} else
				return ScriptableRefactoring.createInputFatalStatus(element, getProcessorName(), IScriptRefactorings.RENAME_LOCAL_VARIABLE);
		} else
			return RefactoringStatus.createFatalErrorStatus(Messages.format(RefactoringCoreMessages.InitializableRefactoring_argument_not_exist, ScriptRefactoringDescriptor.ATTRIBUTE_INPUT));
		final String name= extended.getAttribute(ScriptRefactoringDescriptor.ATTRIBUTE_NAME);
		if (name != null && !"".equals(name)) //$NON-NLS-1$
			setNewElementName(name);
		else
			return RefactoringStatus.createFatalErrorStatus(Messages.format(RefactoringCoreMessages.InitializableRefactoring_argument_not_exist, ScriptRefactoringDescriptor.ATTRIBUTE_NAME));
		if (fCu != null && fLocalVariable == null) {
			final String selection= extended.getAttribute(ScriptRefactoringDescriptor.ATTRIBUTE_SELECTION);
			if (selection != null) {
				int offset= -1;
				int length= -1;
				final StringTokenizer tokenizer= new StringTokenizer(selection);
				if (tokenizer.hasMoreTokens())
					offset= Integer.valueOf(tokenizer.nextToken()).intValue();
				if (tokenizer.hasMoreTokens())
					length= Integer.valueOf(tokenizer.nextToken()).intValue();
				if (offset >= 0 && length >= 0) {
					try {
						final IModelElement[] elements= fCu.codeSelect(offset, length);
						if (elements != null) {
							for (int index= 0; index < elements.length; index++) {
								final IModelElement element= elements[index];
								if (element instanceof ILocalVariable)
									fLocalVariable= (ILocalVariable) element;
							}
						}
						if (fLocalVariable == null)
							return ScriptableRefactoring.createInputFatalStatus(null, getProcessorName(), IScriptRefactorings.RENAME_LOCAL_VARIABLE);
					} catch (ModelException exception) {
						ScriptManipulationPlugin.log(exception);
					}
				} else
					return RefactoringStatus.createFatalErrorStatus(Messages.format(RefactoringCoreMessages.InitializableRefactoring_illegal_argument, new Object[] { selection,
							ScriptRefactoringDescriptor.ATTRIBUTE_SELECTION }));
			} else
				return RefactoringStatus
						.createFatalErrorStatus(Messages.format(RefactoringCoreMessages.InitializableRefactoring_argument_not_exist, ScriptRefactoringDescriptor.ATTRIBUTE_SELECTION));
		}
		final String references= extended.getAttribute(ScriptRefactoringDescriptor.ATTRIBUTE_REFERENCES);
		if (references != null) {
			fUpdateReferences= Boolean.valueOf(references).booleanValue();
		} else
			return RefactoringStatus.createFatalErrorStatus(Messages.format(RefactoringCoreMessages.InitializableRefactoring_argument_not_exist, ScriptRefactoringDescriptor.ATTRIBUTE_REFERENCES));
		return new RefactoringStatus();
	}

	public String getCurrentElementName() {
		return fCurrentName;
	}

	public boolean canEnableUpdateReferences() {
		return true;
	}

	public void setUpdateReferences(boolean update) {
		fUpdateReferences = update;
	}

	public boolean getUpdateReferences() {
		return fUpdateReferences;
	}

	@Override
	protected RenameModifications computeRenameModifications()
			throws CoreException {
		RenameModifications result= new RenameModifications();
		if (fLocalVariable instanceof IField) {
			// TODO: add switching method in RenameModifications
			result.rename((IField)fLocalVariable, new RenameArguments(getNewElementName(), getUpdateReferences()));
		}
		return result;
	}

	@Override
	protected IFile[] getChangedFiles() throws CoreException {
		return new IFile[] {ResourceUtil.getFile(fCu)};
	}

	@Override
	protected String[] getAffectedProjectNatures() throws CoreException {
		return ScriptProcessors.computeAffectedNatures(fCu);
	}

	@Override
	public Object[] getElements() {
		return new Object[]{ fLocalVariable };
	}
	
	public String getNewElement() {
		return getNewElementName();
	}

	@Override
	public String getIdentifier() {
		return IDENTIFIER;
	}

	@Override
	public String getProcessorName() {
		return RefactoringCoreMessages.RenameTempRefactoring_rename;
	}

	@Override
	public RefactoringStatus checkInitialConditions(IProgressMonitor pm)
			throws CoreException, OperationCanceledException {
		//fCompilationUnitNode = RefactoringASTParser.parseWithASTProvider(fCu, true, null);
		//ISourceRange sourceRange= fLocalVariable.
		//fLocalVariable.get
		//ASTNode name= NodeFinder.perform(fCompilationUnitNode, sourceRange);
		//if (name == null)
		//	return;
		//if (name.getParent() instanceof VariableDeclaration)
		//	fTempDeclarationNode= (VariableDeclaration) name.getParent();
		//if (fTempDeclarationNode == null || fTempDeclarationNode.resolveBinding() == null)
		//	return RefactoringStatus.createFatalErrorStatus(RefactoringCoreMessages.RenameTempRefactoring_must_select_local);
		//if (! Checks.isDeclaredIn(fTempDeclarationNode, MethodDeclaration.class)
		// && ! Checks.isDeclaredIn(fTempDeclarationNode, Initializer.class))
		//	return RefactoringStatus.createFatalErrorStatus(RefactoringCoreMessages.RenameTempRefactoring_only_in_methods_and_initializers);

		//fCurrentName= fTempDeclarationNode.getName().getIdentifier();
		fCurrentName = fLocalVariable.getElementName();
		return new RefactoringStatus();
	}

/*	private AccumulatingProblemReporter getAccumulatingProblemReporter() {
		final PerWorkingCopyInfo perWorkingCopyInfo = getPerWorkingCopyInfo();
		if (perWorkingCopyInfo != null && perWorkingCopyInfo.isActive()) {
			final IScriptProject project = getScriptProject();

			// https://bugs.eclipse.org/bugs/show_bug.cgi?id=267008
			// Script nature check is not enough. It's possible that the
			// external project created
			// has a name of ExternalScriptProject.EXTERNAL_PROJECT_NAME, but no
			// script nature.
			// If script nature added during
			// WorkingCopyOwner.newWorkingCopy(...), this fix is not relevant.
			// Does script nature should be added in
			// WorkingCopyOwner.newWorkingCopy, or just script name checked?
			if (project != null
					&& (ExternalScriptProject.EXTERNAL_PROJECT_NAME
							.equals(project.getProject().getName()) || ScriptProject
							.hasScriptNature(project.getProject()))) {
				return new AccumulatingProblemReporter(perWorkingCopyInfo);
			}
		}
		return null;
	}*/

	@Override
	public Change createChange(IProgressMonitor pm) throws CoreException,
			OperationCanceledException {
		try {
			pm.beginTask(RefactoringCoreMessages.RenameTypeProcessor_creating_changes, 1);
			RenameModelElementDescriptor descriptor= createRefactoringDescriptor();
			fChange.setDescriptor(new RefactoringChangeDescriptor(descriptor));
			return fChange;
		} finally {
			pm.done();
		}
	}
	private RenameModelElementDescriptor createRefactoringDescriptor() {
		String project= null;
		IScriptProject scriptProject= fCu.getScriptProject();
		if (scriptProject != null)
			project= scriptProject.getElementName();
		//final String header= Messages.format(RefactoringCoreMessages.RenameLocalVariableProcessor_descriptor_description, new String[] { BasicElementLabels.getJavaElementName(fCurrentName), JavaElementLabels.getElementLabel(fLocalVariable.getParent(), JavaElementLabels.ALL_FULLY_QUALIFIED), BasicElementLabels.getJavaElementName(fNewName)});
		//final String description= Messages.format(RefactoringCoreMessages.RenameLocalVariableProcessor_descriptor_description_short, BasicElementLabels.getJavaElementName(fCurrentName));
		//final String comment= new JDTRefactoringDescriptorComment(project, this, header).asString();
		final RenameModelElementDescriptor descriptor= new RenameModelElementDescriptor(IScriptRefactorings.RENAME_LOCAL_VARIABLE);
		descriptor.setProject(project);
		//descriptor.setDescription(description);
		//descriptor.setComment(comment);
		descriptor.setFlags(RefactoringDescriptor.NONE);
		descriptor.setModelElement(fLocalVariable);
		descriptor.setNewName(getNewElementName());
		descriptor.setUpdateReferences(fUpdateReferences);
		return descriptor;
	}

}
