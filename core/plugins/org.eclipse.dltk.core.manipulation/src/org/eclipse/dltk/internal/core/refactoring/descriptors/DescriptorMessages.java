package org.eclipse.dltk.internal.core.refactoring.descriptors;

import org.eclipse.osgi.util.NLS;

public class DescriptorMessages extends NLS {

	private static final String BUNDLE_NAME= "org.eclipse.dltk.internal.core.refactoring.descriptors.DescriptorMessages"; //$NON-NLS-1$

	public static String ScriptRefactoringDescriptor_no_description;

	public static String ScriptRefactoringDescriptor_no_resulting_descriptor;

	public static String ScriptRefactoringDescriptor_not_available;

	public static String MoveDescriptor_no_destination_set;

	public static String MoveDescriptor_no_elements_set;

	public static String MoveStaticMembersDescriptor_invalid_members;

	public static String MoveStaticMembersDescriptor_no_members;

	public static String MoveStaticMembersDescriptor_no_type;

	public static String RenameModelElementDescriptor_accessor_constraint;

	public static String RenameModelElementDescriptor_delegate_constraint;

	public static String RenameModelElementDescriptor_deprecation_constraint;

	public static String RenameModelElementDescriptor_hierarchical_constraint;

	public static String RenameModelElementDescriptor_no_Script_element;

	public static String RenameModelElementDescriptor_patterns_constraint;

	public static String RenameModelElementDescriptor_patterns_qualified_constraint;

	public static String RenameModelElementDescriptor_project_constraint;

	public static String RenameModelElementDescriptor_qualified_constraint;

	public static String RenameModelElementDescriptor_reference_constraint;

	public static String RenameModelElementDescriptor_similar_constraint;

	public static String RenameModelElementDescriptor_textual_constraint;

	public static String RenameLocalVariableDescriptor_no_compilation_unit;

	public static String RenameLocalVariableDescriptor_no_selection;

	public static String RenameResourceDescriptor_no_new_name;

	public static String RenameResourceDescriptor_no_resource;

	public static String RenameResourceDescriptor_project_constraint;

	public static String RenameResourceRefactoringContribution_error_cannot_access;

	public static String UseSupertypeDescriptor_no_subtype;

	public static String UseSupertypeDescriptor_no_supertype;

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, DescriptorMessages.class);
	}

	private DescriptorMessages() {
	}
}
