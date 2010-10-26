/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.eclipse.dltk.ui;

import org.eclipse.dltk.core.IDLTKLanguageToolkit;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.ui.text.ScriptSourceViewerConfiguration;
import org.eclipse.dltk.ui.text.ScriptTextTools;
import org.eclipse.dltk.ui.text.templates.ITemplateAccess;
import org.eclipse.dltk.ui.viewsupport.ScriptUILabelProvider;
import org.eclipse.jface.preference.IPreferenceStore;

public interface IDLTKUILanguageToolkit {
	ScriptElementLabels getScriptElementLabels();

	ScriptUILabelProvider createScriptUILabelProvider();

	IDLTKLanguageToolkit getCoreToolkit();

	IPreferenceStore getPreferenceStore();

	IPreferenceStore getCombinedPreferenceStore();

	String getPartitioningId();

	String getEditorId(Object inputElement);

	String getInterpreterContainerId();

	ScriptTextTools getTextTools();

	ScriptSourceViewerConfiguration createSourceViewerConfiguration();

	// Per module script explorer show children way.
	boolean getProvideMembers(ISourceModule element);

	String getInterpreterPreferencePage();

	String getDebugPreferencePage();

	String[] getEditorPreferencePages();

	String getEditorTemplatesPreferencePageId();

	ITemplateAccess getEditorTemplates();

	/**
	 * Returns the current value of the boolean-valued preference with the given
	 * name. Returns the default-default value (<code>false</code>) if there is
	 * no preference with the given name, or if the current value cannot be
	 * treated as a boolean.
	 * 
	 * @param name
	 *            the name of the preference
	 * @return the boolean-valued preference
	 * @since 2.0
	 */
	public boolean getBoolean(String name);

	/**
	 * Returns the current value of the integer-valued preference with the given
	 * name. Returns the default-default value (<code>0</code>) if there is no
	 * preference with the given name, or if the current value cannot be treated
	 * as an integer.
	 * 
	 * @param name
	 *            the name of the preference
	 * @return the int-valued preference
	 * @since 2.0
	 */
	public int getInt(String name);

	/**
	 * Returns the current value of the string-valued preference with the given
	 * name. Returns the default-default value (the empty string <code>""</code>
	 * ) if there is no preference with the given name, or if the current value
	 * cannot be treated as a string.
	 * 
	 * @param name
	 *            the name of the preference
	 * @return the string-valued preference
	 * @since 2.0
	 */
	public String getString(String name);

}
