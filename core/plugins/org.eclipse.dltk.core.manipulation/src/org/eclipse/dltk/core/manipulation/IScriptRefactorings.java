/*******************************************************************************
 * Copyright (c) 2006, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.dltk.core.manipulation;

import org.eclipse.ltk.core.refactoring.PerformRefactoringOperation;
import org.eclipse.ltk.core.refactoring.RefactoringContribution;
import org.eclipse.ltk.core.refactoring.RefactoringCore;

/**
 * Interface for refactoring ids offered by the JDT tooling.
 * <p>
 * This interface provides refactoring ids for refactorings offered by the JDT
 * tooling. Refactoring instances corresponding to such an id may be
 * instantiated by the refactoring framework using
 * {@link RefactoringCore#getRefactoringContribution(String)}. The resulting
 * refactoring instance may be executed on the workspace with a
 * {@link PerformRefactoringOperation}.
 * <p>
 * Clients may obtain customizable refactoring descriptors for a certain
 * refactoring by calling
 * {@link RefactoringCore#getRefactoringContribution(String)} with the
 * appropriate refactoring id and then calling
 * {@link RefactoringContribution#createDescriptor()} to obtain a customizable
 * refactoring descriptor. The concrete subtype of refactoring descriptors is
 * dependent from the <code>id</code> argument.
 * </p>
 * <p>
 * Note: this interface is not intended to be implemented by clients.
 * </p>
 *
 * @since 1.1
 * @noimplement This interface is not intended to be implemented by clients.
 * @noextend This interface is not intended to be extended by clients.
 */
public interface IScriptRefactorings {

	/**
	 * Refactoring id of the 'Change Method Signature' refactoring (value:
	 * <code>org.eclipse.dltk.core.manipulation.change.method.signature</code>).
	 * <p>
	 * Clients may safely cast the obtained refactoring descriptor to
	 * {@link ChangeMethodSignatureDescriptor}.
	 * </p>
	 */
	public static final String CHANGE_METHOD_SIGNATURE= "org.eclipse.dltk.core.manipulation.change.method.signature"; //$NON-NLS-1$

	/**
	 * Refactoring id of the 'Convert Anonymous To Nested' refactoring (value:
	 * <code>org.eclipse.dltk.core.manipulation.convert.anonymous</code>).
	 * <p>
	 * Clients may safely cast the obtained refactoring descriptor to
	 * {@link ConvertAnonymousDescriptor}.
	 * </p>
	 */
	public static final String CONVERT_ANONYMOUS= "org.eclipse.dltk.core.manipulation.convert.anonymous"; //$NON-NLS-1$

	/**
	 * Refactoring id of the 'Convert Local Variable to Field' refactoring
	 * (value: <code>org.eclipse.dltk.core.manipulation.promote.temp</code>).
	 * <p>
	 * Clients may safely cast the obtained refactoring descriptor to
	 * {@link ConvertLocalVariableDescriptor}.
	 * </p>
	 */
	public static final String CONVERT_LOCAL_VARIABLE= "org.eclipse.dltk.core.manipulation.promote.temp"; //$NON-NLS-1$

	/**
	 * Refactoring id of the 'Convert Member Type to Top Level' refactoring
	 * (value: <code>org.eclipse.dltk.core.manipulation.move.inner</code>).
	 * <p>
	 * Clients may safely cast the obtained refactoring descriptor to
	 * {@link ConvertMemberTypeDescriptor}.
	 * </p>
	 */
	public static final String CONVERT_MEMBER_TYPE= "org.eclipse.dltk.core.manipulation.move.inner"; //$NON-NLS-1$

	/**
	 * Refactoring id of the 'Copy' refactoring (value:
	 * <code>org.eclipse.dltk.core.manipulation.copy</code>).
	 * <p>
	 * Clients may safely cast the obtained refactoring descriptor to
	 * {@link CopyDescriptor}.
	 * </p>
	 */
	public static final String COPY= "org.eclipse.dltk.core.manipulation.copy"; //$NON-NLS-1$

	/**
	 * Refactoring id of the 'Delete' refactoring (value:
	 * <code>org.eclipse.dltk.core.manipulation.delete</code>).
	 * <p>
	 * Clients may safely cast the obtained refactoring descriptor to
	 * {@link DeleteDescriptor}.
	 * </p>
	 */
	public static final String DELETE= "org.eclipse.dltk.core.manipulation.delete"; //$NON-NLS-1$

	/**
	 * Refactoring id of the 'Encapsulate Field' refactoring (value:
	 * <code>org.eclipse.dltk.core.manipulation.self.encapsulate</code>).
	 * <p>
	 * Clients may safely cast the obtained refactoring descriptor to
	 * {@link EncapsulateFieldDescriptor}.
	 * </p>
	 */
	public static final String ENCAPSULATE_FIELD= "org.eclipse.dltk.core.manipulation.self.encapsulate"; //$NON-NLS-1$

	/**
	 * Refactoring id of the 'Extract Class' refactoring (value:
	 * <code>"org.eclipse.dltk.core.manipulation.extract.class</code>).
	 * <p>
	 * Clients may safely cast the obtained refactoring descriptor to
	 * {@link ExtractClassDescriptor}.
	 * </p>
	 *
	 * @since 1.2
	 */
	public static final String EXTRACT_CLASS= "org.eclipse.dltk.core.manipulation.extract.class"; //$NON-NLS-1$

	/**
	 * Refactoring id of the 'Extract Constant' refactoring (value:
	 * <code>org.eclipse.dltk.core.manipulation.extract.constant</code>).
	 * <p>
	 * Clients may safely cast the obtained refactoring descriptor to
	 * {@link ExtractConstantDescriptor}.
	 * </p>
	 */
	public static final String EXTRACT_CONSTANT= "org.eclipse.dltk.core.manipulation.extract.constant"; //$NON-NLS-1$

	/**
	 * Refactoring id of the 'Extract Interface' refactoring (value:
	 * <code>org.eclipse.dltk.core.manipulation.extract.interface</code>).
	 * <p>
	 * Clients may safely cast the obtained refactoring descriptor to
	 * {@link ExtractInterfaceDescriptor}.
	 * </p>
	 */
	public static final String EXTRACT_INTERFACE= "org.eclipse.dltk.core.manipulation.extract.interface"; //$NON-NLS-1$

	/**
	 * Refactoring id of the 'Extract Local Variable' refactoring (value:
	 * <code>org.eclipse.dltk.core.manipulation.extract.temp</code>).
	 * <p>
	 * Clients may safely cast the obtained refactoring descriptor to
	 * {@link ExtractLocalDescriptor}.
	 * </p>
	 */
	public static final String EXTRACT_LOCAL_VARIABLE= "org.eclipse.dltk.core.manipulation.extract.temp"; //$NON-NLS-1$

	/**
	 * Refactoring id of the 'Extract Method' refactoring (value:
	 * <code>org.eclipse.dltk.core.manipulation.extract.method</code>).
	 * <p>
	 * Clients may safely cast the obtained refactoring descriptor to
	 * {@link ExtractMethodDescriptor}.
	 * </p>
	 */
	public static final String EXTRACT_METHOD= "org.eclipse.dltk.core.manipulation.extract.method"; //$NON-NLS-1$

	/**
	 * Refactoring id of the 'Extract Superclass' refactoring (value:
	 * <code>org.eclipse.dltk.core.manipulation.extract.superclass</code>).
	 * <p>
	 * Clients may safely cast the obtained refactoring descriptor to
	 * {@link ExtractSuperclassDescriptor}.
	 * </p>
	 */
	public static final String EXTRACT_SUPERCLASS= "org.eclipse.dltk.core.manipulation.extract.superclass"; //$NON-NLS-1$

	/**
	 * Refactoring id of the 'Generalize Declared Type' refactoring (value:
	 * <code>org.eclipse.dltk.core.manipulation.change.type</code>).
	 * <p>
	 * Clients may safely cast the obtained refactoring descriptor to
	 * {@link GeneralizeTypeDescriptor}.
	 * </p>
	 */
	public static final String GENERALIZE_TYPE= "org.eclipse.dltk.core.manipulation.change.type"; //$NON-NLS-1$

	/**
	 * Refactoring id of the 'Infer Type Arguments' refactoring (value:
	 * <code>org.eclipse.dltk.core.manipulation.infer.typearguments</code>).
	 * <p>
	 * Clients may safely cast the obtained refactoring descriptor to
	 * {@link InferTypeArgumentsDescriptor}.
	 * </p>
	 */
	public static final String INFER_TYPE_ARGUMENTS= "org.eclipse.dltk.core.manipulation.infer.typearguments"; //$NON-NLS-1$

	/**
	 * Refactoring id of the 'Inline Constant' refactoring (value:
	 * <code>org.eclipse.dltk.core.manipulation.inline.constant</code>).
	 * <p>
	 * Clients may safely cast the obtained refactoring descriptor to
	 * {@link InlineConstantDescriptor}.
	 * </p>
	 */
	public static final String INLINE_CONSTANT= "org.eclipse.dltk.core.manipulation.inline.constant"; //$NON-NLS-1$

	/**
	 * Refactoring id of the 'Inline Local Variable' refactoring (value:
	 * <code>org.eclipse.dltk.core.manipulation.inline.temp</code>).
	 * <p>
	 * Clients may safely cast the obtained refactoring descriptor to
	 * {@link InlineLocalVariableDescriptor}.
	 * </p>
	 */
	public static final String INLINE_LOCAL_VARIABLE= "org.eclipse.dltk.core.manipulation.inline.temp"; //$NON-NLS-1$

	/**
	 * Refactoring id of the 'Inline Method' refactoring (value:
	 * <code>org.eclipse.dltk.core.manipulation.inline.method</code>).
	 * <p>
	 * Clients may safely cast the obtained refactoring descriptor to
	 * {@link InlineMethodDescriptor}.
	 * </p>
	 */
	public static final String INLINE_METHOD= "org.eclipse.dltk.core.manipulation.inline.method"; //$NON-NLS-1$

	/**
	 * Refactoring id of the 'Introduce Factory' refactoring (value:
	 * <code>org.eclipse.dltk.core.manipulation.introduce.factory</code>).
	 * <p>
	 * Clients may safely cast the obtained refactoring descriptor to
	 * {@link IntroduceFactoryDescriptor}.
	 * </p>
	 */
	public static final String INTRODUCE_FACTORY= "org.eclipse.dltk.core.manipulation.introduce.factory"; //$NON-NLS-1$

	/**
	 * Refactoring id of the 'Introduce Indirection' refactoring (value:
	 * <code>org.eclipse.dltk.core.manipulation.introduce.indirection</code>).
	 * <p>
	 * Clients may safely cast the obtained refactoring descriptor to
	 * {@link IntroduceIndirectionDescriptor}.
	 * </p>
	 */
	public static final String INTRODUCE_INDIRECTION= "org.eclipse.dltk.core.manipulation.introduce.indirection"; //$NON-NLS-1$

	/**
	 * Refactoring id of the 'Introduce Parameter' refactoring (value:
	 * <code>org.eclipse.dltk.core.manipulation.introduce.parameter</code>).
	 * <p>
	 * Clients may safely cast the obtained refactoring descriptor to
	 * {@link IntroduceParameterDescriptor}.
	 * </p>
	 */
	public static final String INTRODUCE_PARAMETER= "org.eclipse.dltk.core.manipulation.introduce.parameter"; //$NON-NLS-1$

	/**
	 * Refactoring id of the 'Introduce Parameter Object' refactoring (value:
	 * <code>org.eclipse.dltk.core.manipulation.introduce.parameter.object</code>).
	 * <p>
	 * Clients may safely cast the obtained refactoring descriptor to
	 * {@link IntroduceParameterObjectDescriptor}.
	 * </p>
	 * @since 1.2
	 */
	public static final String INTRODUCE_PARAMETER_OBJECT= "org.eclipse.dltk.core.manipulation.introduce.parameter.object"; //$NON-NLS-1$

	/**
	 * Refactoring id of the 'Move' refactoring (value:
	 * <code>org.eclipse.dltk.core.manipulation.move</code>).
	 * <p>
	 * Clients may safely cast the obtained refactoring descriptor to
	 * {@link MoveDescriptor}.
	 * </p>
	 */
	public static final String MOVE= "org.eclipse.dltk.core.manipulation.move"; //$NON-NLS-1$

	/**
	 * Refactoring id of the 'Move Method' refactoring (value:
	 * <code>org.eclipse.dltk.core.manipulation.move.method</code>).
	 * <p>
	 * Clients may safely cast the obtained refactoring descriptor to
	 * {@link MoveMethodDescriptor}.
	 * </p>
	 */
	public static final String MOVE_METHOD= "org.eclipse.dltk.core.manipulation.move.method"; //$NON-NLS-1$

	/**
	 * Refactoring id of the 'Move Static Members' refactoring (value:
	 * <code>org.eclipse.dltk.core.manipulation.move.static</code>).
	 * <p>
	 * Clients may safely cast the obtained refactoring descriptor to
	 * {@link MoveStaticMembersDescriptor}.
	 * </p>
	 */
	public static final String MOVE_STATIC_MEMBERS= "org.eclipse.dltk.core.manipulation.move.static"; //$NON-NLS-1$

	/**
	 * Refactoring id of the 'Pull Up' refactoring (value:
	 * <code>org.eclipse.dltk.core.manipulation.pull.up</code>).
	 * <p>
	 * Clients may safely cast the obtained refactoring descriptor to
	 * {@link PullUpDescriptor}.
	 * </p>
	 */
	public static final String PULL_UP= "org.eclipse.dltk.core.manipulation.pull.up"; //$NON-NLS-1$

	/**
	 * Refactoring id of the 'Push Down' refactoring (value:
	 * <code>org.eclipse.dltk.core.manipulation.push.down</code>).
	 * <p>
	 * Clients may safely cast the obtained refactoring descriptor to
	 * {@link PushDownDescriptor}.
	 * </p>
	 */
	public static final String PUSH_DOWN= "org.eclipse.dltk.core.manipulation.push.down"; //$NON-NLS-1$

	/**
	 * Refactoring id of the 'Rename Compilation Unit' refactoring (value:
	 * <code>org.eclipse.dltk.core.manipulation.rename.compilationunit</code>).
	 * <p>
	 * Clients may safely cast the obtained refactoring descriptor to
	 * {@link RenameJavaElementDescriptor}.
	 * </p>
	 */
	public static final String RENAME_COMPILATION_UNIT= "org.eclipse.dltk.core.manipulation.rename.compilationunit"; //$NON-NLS-1$

	/**
	 * Refactoring id of the 'Rename Enum Constant' refactoring (value:
	 * <code>org.eclipse.dltk.core.manipulation.rename.enum.constant</code>).
	 * <p>
	 * Clients may safely cast the obtained refactoring descriptor to
	 * {@link RenameJavaElementDescriptor}.
	 * </p>
	 */
	public static final String RENAME_ENUM_CONSTANT= "org.eclipse.dltk.core.manipulation.rename.enum.constant"; //$NON-NLS-1$

	/**
	 * Refactoring id of the 'Rename Field' refactoring (value:
	 * <code>org.eclipse.dltk.core.manipulation.rename.field</code>).
	 * <p>
	 * Clients may safely cast the obtained refactoring descriptor to
	 * {@link RenameJavaElementDescriptor}.
	 * </p>
	 */
	public static final String RENAME_FIELD= "org.eclipse.dltk.core.manipulation.rename.field"; //$NON-NLS-1$

	/**
	 * Refactoring id of the 'Rename Java Project' refactoring (value:
	 * <code>org.eclipse.dltk.core.manipulation.rename.java.project</code>).
	 * <p>
	 * Clients may safely cast the obtained refactoring descriptor to
	 * {@link RenameJavaElementDescriptor}.
	 * </p>
	 */
	public static final String RENAME_JAVA_PROJECT= "org.eclipse.dltk.core.manipulation.rename.java.project"; //$NON-NLS-1$

	/**
	 * Refactoring id of the 'Rename Local Variable' refactoring (value:
	 * <code>org.eclipse.dltk.core.manipulation.rename.local.variable</code>).
	 * <p>
	 * Clients may safely cast the obtained refactoring descriptor to
	 * {@link RenameJavaElementDescriptor}.
	 * </p>
	 */
	public static final String RENAME_LOCAL_VARIABLE= "org.eclipse.dltk.core.manipulation.rename.local.variable"; //$NON-NLS-1$

	/**
	 * Refactoring id of the 'Rename Method' refactoring (value:
	 * <code>org.eclipse.dltk.core.manipulation.rename.method</code>).
	 * <p>
	 * Clients may safely cast the obtained refactoring descriptor to
	 * {@link RenameJavaElementDescriptor}.
	 * </p>
	 */
	public static final String RENAME_METHOD= "org.eclipse.dltk.core.manipulation.rename.method"; //$NON-NLS-1$

	/**
	 * Refactoring id of the 'Rename Package' refactoring (value:
	 * <code>org.eclipse.dltk.core.manipulation.rename.package</code>).
	 * <p>
	 * Clients may safely cast the obtained refactoring descriptor to
	 * {@link RenameJavaElementDescriptor}.
	 * </p>
	 */
	public static final String RENAME_PACKAGE= "org.eclipse.dltk.core.manipulation.rename.package"; //$NON-NLS-1$

	/**
	 * Refactoring id of the 'Rename Source Folder' refactoring (value:
	 * <code>org.eclipse.dltk.core.manipulation.rename.source.folder</code>).
	 * <p>
	 * Clients may safely cast the obtained refactoring descriptor to
	 * {@link RenameJavaElementDescriptor}.
	 * </p>
	 */
	public static final String RENAME_SOURCE_FOLDER= "org.eclipse.dltk.core.manipulation.rename.source.folder"; //$NON-NLS-1$

	/**
	 * Refactoring id of the 'Rename Type' refactoring (value:
	 * <code>org.eclipse.dltk.core.manipulation.rename.type</code>).
	 * <p>
	 * Clients may safely cast the obtained refactoring descriptor to
	 * {@link RenameJavaElementDescriptor}.
	 * </p>
	 */
	public static final String RENAME_TYPE= "org.eclipse.dltk.core.manipulation.rename.type"; //$NON-NLS-1$

	/**
	 * Refactoring id of the 'Rename Type Parameter' refactoring (value:
	 * <code>org.eclipse.dltk.core.manipulation.rename.type.parameter</code>).
	 * <p>
	 * Clients may safely cast the obtained refactoring descriptor to
	 * {@link RenameJavaElementDescriptor}.
	 * </p>
	 */
	public static final String RENAME_TYPE_PARAMETER= "org.eclipse.dltk.core.manipulation.rename.type.parameter"; //$NON-NLS-1$

	/**
	 * Refactoring id of the 'Use Supertype Where Possible' refactoring (value:
	 * <code>org.eclipse.dltk.core.manipulation.use.supertype</code>).
	 * <p>
	 * Clients may safely cast the obtained refactoring descriptor to
	 * {@link UseSupertypeDescriptor}.
	 * </p>
	 */
	public static final String USE_SUPER_TYPE= "org.eclipse.dltk.core.manipulation.use.supertype"; //$NON-NLS-1$
}