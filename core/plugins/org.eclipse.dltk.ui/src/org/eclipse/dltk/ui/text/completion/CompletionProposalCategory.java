/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.eclipse.dltk.ui.text.completion;

import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.InvalidRegistryObjectException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.dltk.internal.corext.util.Messages;
import org.eclipse.dltk.ui.DLTKUIPlugin;
import org.eclipse.jface.action.LegacyActionTools;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.osgi.framework.Bundle;


/**
 * Describes a category extension to the "javaCompletionProposalComputer" extension point.
 */
public final class CompletionProposalCategory {
	/** The extension schema name of the icon attribute. */
	private static final String ICON= "icon"; //$NON-NLS-1$

	private final String fId;
	private final String fName;
	private final IConfigurationElement fElement;
	/** The image descriptor for this category, or <code>null</code> if none specified. */
	private final ImageDescriptor fImage;
	
	private boolean fIsSeparateCommand= true;
	private boolean fIsEnabled= true;
	private boolean fIsIncluded= true;
	private final CompletionProposalComputerRegistry fRegistry;
	
	private int fSortOrder= 0x10000;
	private String fLastError= null;
	
	private String fToolkitID = null;

	CompletionProposalCategory(IConfigurationElement element, CompletionProposalComputerRegistry registry) {
		fElement= element;
		fRegistry= registry;
		IExtension parent= (IExtension) element.getParent();
		fId= parent.getUniqueIdentifier();
		checkNotNull(fId, "id"); //$NON-NLS-1$
		String name= parent.getLabel();
		if (name == null)
			fName= fId;
		else
			fName= name;
		
		String icon= element.getAttribute(ICON);
		ImageDescriptor img= null;
		if (icon != null) {
			Bundle bundle= getBundle();
			if (bundle != null) {
				Path path= new Path(icon);
				URL url= FileLocator.find(bundle, path, null);
				img= ImageDescriptor.createFromURL(url);
			}
		}
		fImage= img;
		fToolkitID = element.getAttribute(CompletionProposalComputerDescriptor.TOOLKITID);
		
	}

	CompletionProposalCategory(String id, String name, CompletionProposalComputerRegistry registry, String toolkitID) {
		fRegistry= registry;
		fId= id;
		fName= name;
		fElement= null;
		fImage= null;
		fToolkitID = toolkitID;
	}

	private Bundle getBundle() {
		String namespace= fElement.getDeclaringExtension().getContributor().getName();
		Bundle bundle= Platform.getBundle(namespace);
		return bundle;
	}

	/**
	 * Checks an element that must be defined according to the extension
	 * point schema. Throws an
	 * <code>InvalidRegistryObjectException</code> if <code>obj</code>
	 * is <code>null</code>.
	 */
	private void checkNotNull(Object obj, String attribute) throws InvalidRegistryObjectException {
		if (obj == null) {
			Object[] args= { getId(), fElement.getContributor().getName(), attribute };
			String message= Messages.format(ScriptTextMessages.CompletionProposalComputerDescriptor_illegal_attribute_message, args);
			IStatus status= new Status(IStatus.WARNING, DLTKUIPlugin.PLUGIN_ID, IStatus.OK, message, null);
			DLTKUIPlugin.log(status);
			throw new InvalidRegistryObjectException();
		}
	}

	/**
	 * Returns the identifier of the described extension.
	 *
	 * @return Returns the id
	 */
	public String getId() {
		return fId;
	}

	/**
	 * Returns the name of the described extension.
	 * 
	 * @return Returns the name
	 */
	public String getName() {
		return fName;
	}
	
	/**
	 * Returns the name of the described extension
	 * without mnemonic hint in order to be displayed
	 * in a message.
	 * 
	 * @return Returns the name
	 */
	public String getDisplayName() {
		return LegacyActionTools.removeMnemonics(fName);
	}
	
	/**
	 * Returns the image descriptor of the described category.
	 * 
	 * @return the image descriptor of the described category
	 */
	public ImageDescriptor getImageDescriptor() {
		return fImage;
	}
	
	/**
	 * Sets the separate command state of the category.
	 * 
	 * @param enabled the new enabled state.
	 */
	public void setSeparateCommand(boolean enabled) {
		fIsSeparateCommand= enabled;
	}
	
	/**
	 * Returns the enablement state of the category.
	 * 
	 * @return the enablement state of the category
	 */
	public boolean isSeparateCommand() {
		return fIsSeparateCommand;
	}
	
	/**
	 * @param included the included
	 */
	public void setIncluded(boolean included) {
		fIsIncluded= included;
	}
	
	/**
	 * @return included
	 */
	public boolean isIncluded() {
		return fIsIncluded;
	}

	public boolean isEnabled() {
		return fIsEnabled;
	}

	public void setEnabled(boolean isEnabled) {
		fIsEnabled= isEnabled;
	}

	/**
	 * Returns <code>true</code> if the category contains any computers, <code>false</code>
	 * otherwise.
	 * 
	 * @return <code>true</code> if the category contains any computers, <code>false</code>
	 *         otherwise
	 */
	public boolean hasComputers() {
		List<CompletionProposalComputerDescriptor> descriptors = fRegistry
				.getProposalComputerDescriptors();
		for (Iterator<CompletionProposalComputerDescriptor> it = descriptors
				.iterator(); it.hasNext();) {
			CompletionProposalComputerDescriptor desc = it.next();
			if (desc.getCategory() == this)
				return true;
		}
		return false;
	}
	
	/**
	 * Returns <code>true</code> if the category contains any computers in the given partition, <code>false</code>
	 * otherwise.
	 * 
	 * @param partition the partition
	 * @return <code>true</code> if the category contains any computers, <code>false</code>
	 *         otherwise
	 */
	public boolean hasComputers(String partition) {
		List<CompletionProposalComputerDescriptor> descriptors = fRegistry
				.getProposalComputerDescriptors(partition);
		for (Iterator<CompletionProposalComputerDescriptor> it = descriptors
				.iterator(); it.hasNext();) {
			CompletionProposalComputerDescriptor desc = it.next();
			if (desc.getCategory() == this)
				return true;
		}
		return false;
	}
	
	/**
	 * @return sortOrder
	 */
	public int getSortOrder() {
		return fSortOrder;
	}
	
	/**
	 * @param sortOrder the sortOrder
	 */
	public void setSortOrder(int sortOrder) {
		fSortOrder= sortOrder;
	}

	/**
	 * Safely computes completion proposals of all computers of this category through their
	 * extension. If an extension is disabled, throws an exception or otherwise does not adhere to
	 * the contract described in {@link IScriptCompletionProposalComputer}, it is disabled.
	 * 
	 * @param context the invocation context passed on to the extension
	 * @param partition the partition type where to invocation occurred
	 * @param monitor the progress monitor passed on to the extension
	 * @return the list of computed completion proposals (element type:
	 *         {@link org.eclipse.jface.text.contentassist.ICompletionProposal})
	 */
	public List<ICompletionProposal> computeCompletionProposals(
			ContentAssistInvocationContext context, String partition,
			SubProgressMonitor monitor) {
		fLastError= null;
		
		List<ICompletionProposal> result = new ArrayList<ICompletionProposal>();
		List<CompletionProposalComputerDescriptor> descriptors = new ArrayList<CompletionProposalComputerDescriptor>(
				fRegistry.getProposalComputerDescriptors(partition));
		for (Iterator<CompletionProposalComputerDescriptor> it = descriptors
				.iterator(); it.hasNext();) {
			CompletionProposalComputerDescriptor desc = it.next();
			if(context instanceof ScriptContentAssistInvocationContext ) {
				ScriptContentAssistInvocationContext scriptContext = (ScriptContentAssistInvocationContext)context;
				if( !scriptContext.getLanguageNatureID().equals(desc.getLanguageToolkitID())) {
					continue;
				}
			}
			if (desc.getCategory() == this)
				result.addAll(desc.computeCompletionProposals(context, monitor));
			if (fLastError == null)
				fLastError= desc.getErrorMessage();
		}
		return result;
	}

	/**
	 * Safely computes context information objects of all computers of this category through their
	 * extension. If an extension is disabled, throws an exception or otherwise does not adhere to
	 * the contract described in {@link IScriptCompletionProposalComputer}, it is disabled.
	 * 
	 * @param context the invocation context passed on to the extension
	 * @param partition the partition type where to invocation occurred
	 * @param monitor the progress monitor passed on to the extension
	 * @return the list of computed context information objects (element type:
	 *         {@link org.eclipse.jface.text.contentassist.IContextInformation})
	 */
	public List<IContextInformation> computeContextInformation(
			ContentAssistInvocationContext context, String partition,
			SubProgressMonitor monitor) {
		fLastError= null;
		List<IContextInformation> result = new ArrayList<IContextInformation>();
		List<CompletionProposalComputerDescriptor> descriptors = new ArrayList<CompletionProposalComputerDescriptor>(
				fRegistry.getProposalComputerDescriptors(partition));
		for (Iterator<CompletionProposalComputerDescriptor> it = descriptors
				.iterator(); it.hasNext();) {
			CompletionProposalComputerDescriptor desc = it.next();
			if(context instanceof ScriptContentAssistInvocationContext ) {
				ScriptContentAssistInvocationContext scriptContext = (ScriptContentAssistInvocationContext)context;
				if( !scriptContext.getLanguageNatureID().equals(desc.getLanguageToolkitID())) {
					continue;
				}
			}
			if (desc.getCategory() == this)
				result.addAll(desc.computeContextInformation(context, monitor));
			if (fLastError == null)
				fLastError= desc.getErrorMessage();
		}
		return result;
	}

	/**
	 * Returns the error message from the computers in this category.
	 * 
	 * @return the error message from the computers in this category
	 */
	public String getErrorMessage() {
		return fLastError;
	}

	/**
	 * Notifies the computers in this category of a proposal computation session start.
	 */
	public void sessionStarted() {
		List<CompletionProposalComputerDescriptor> descriptors = new ArrayList<CompletionProposalComputerDescriptor>(
				fRegistry.getProposalComputerDescriptors());
		for (Iterator<CompletionProposalComputerDescriptor> it = descriptors
				.iterator(); it.hasNext();) {
			CompletionProposalComputerDescriptor desc = it.next();
			if (desc.getCategory() == this)
				desc.sessionStarted();
			if (fLastError == null)
				fLastError= desc.getErrorMessage();
		}
	}
	
	/**
	 * Notifies the computers in this category of a proposal computation session end.
	 */
	public void sessionEnded() {
		List<CompletionProposalComputerDescriptor> descriptors = new ArrayList<CompletionProposalComputerDescriptor>(
				fRegistry.getProposalComputerDescriptors());
		for (Iterator<CompletionProposalComputerDescriptor> it = descriptors
				.iterator(); it.hasNext();) {
			CompletionProposalComputerDescriptor desc = it.next();
			if (desc.getCategory() == this)
				desc.sessionEnded();
			if (fLastError == null)
				fLastError= desc.getErrorMessage();
		}
	}
	public String getToolkitID() {
		return fToolkitID;
	}
}
