/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.eclipse.dltk.validators.internal.core;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.Preferences.IPropertyChangeListener;
import org.eclipse.core.runtime.Preferences.PropertyChangeEvent;
import org.eclipse.dltk.validators.core.IValidator;
import org.eclipse.dltk.validators.core.ValidatorRuntime;
import org.osgi.framework.BundleContext;
import org.w3c.dom.Document;

/**
 * The activator class controls the plug-in life cycle
 */
public class ValidatorsCore extends Plugin implements IPropertyChangeListener {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.eclipse.dltk.validators.core"; //$NON-NLS-1$

	// The shared instance
	private static ValidatorsCore plugin;

	private boolean fIgnoreValidatorDefPropertyChangeEvents = false;

	/**
	 * The constructor
	 */
	public ValidatorsCore() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.core.runtime.Plugins#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;

		getPluginPreferences().addPropertyChangeListener(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		getPluginPreferences().removePropertyChangeListener(this);
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static ValidatorsCore getDefault() {
		return plugin;
	}

	/**
	 * Returns a Document that can be used to build a DOM tree
	 * 
	 * @return the Document
	 * @throws ParserConfigurationException
	 *             if an exception occurs creating the document builder
	 */
	public static Document getDocument() throws ParserConfigurationException {
		DocumentBuilderFactory dfactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = dfactory.newDocumentBuilder();
		Document doc = docBuilder.newDocument();
		return doc;
	}

	public static String serializeDocument(Document doc) throws IOException,
			TransformerException {
		StringWriter s = new StringWriter();

		TransformerFactory factory = TransformerFactory.newInstance();
		Transformer transformer = factory.newTransformer();
		transformer.setOutputProperty(OutputKeys.METHOD, "xml"); //$NON-NLS-1$
		transformer.setOutputProperty(OutputKeys.INDENT, "yes"); //$NON-NLS-1$

		DOMSource source = new DOMSource(doc);
		StreamResult outputTarget = new StreamResult(s);
		transformer.transform(source, outputTarget);

		return s.toString();
	}

	public void setIgnoreValidatorDefPropertyChangeEvents(boolean ignore) {
		fIgnoreValidatorDefPropertyChangeEvents = ignore;
	}

	public boolean isIgnoreValidatorDefPropertyChangeEvents() {
		return fIgnoreValidatorDefPropertyChangeEvents;
	}

	public void propertyChange(PropertyChangeEvent event) {
		String property = event.getProperty();
		if (property.equals(ValidatorRuntime.PREF_VALIDATOR_XML)) {
			if (!isIgnoreValidatorDefPropertyChangeEvents()) {
				processValidatorPrefsChanged((String) event.getOldValue(),
						(String) event.getNewValue());
			}
		}
	}

	private ValidatorDefinitionsContainer getValidatorDefinitions(String xml) {
		if (xml != null && xml.length() > 0) {
			try {
				return ValidatorDefinitionsContainer
						.createFromXML(new StringReader(xml));
			} catch (IOException e) {
				getLog().log(
						new Status(IStatus.ERROR, PLUGIN_ID, 0,
								ValidatorMessages.ValidatorsCore_exception, e));
			}
		}
		return new ValidatorDefinitionsContainer();
	}

	protected void processValidatorPrefsChanged(String oldValue, String newValue) {
		if (oldValue == null && newValue == null) {
			return;
		}
		if (oldValue != null && oldValue.equals(newValue)) {
			return;
		}

		// Generate the previous Validators
		ValidatorDefinitionsContainer oldResults = getValidatorDefinitions(oldValue);

		// Generate the current
		ValidatorDefinitionsContainer newResults = getValidatorDefinitions(newValue);

		// Determine the deleted validators
		List deleted = new ArrayList(oldResults.getValidatorList());
		deleted.removeAll(newResults.getValidatorList());

		// Dispose ALL but built-in validators
		for (Iterator i = deleted.iterator(); i.hasNext();) {
			IValidator validator = (IValidator) i.next();
			validator.getValidatorType().disposeValidator(validator.getID());
		}

		// fire event and reset initialized flag - during next call new
		// validators would be loaded and added to the validatorType
		ValidatorRuntime.fireValidatorChanged();
	}

	public static void log(IStatus status) {
		getDefault().getLog().log(status);
	}

	public static void error(String message) {
		log(new Status(IStatus.ERROR, PLUGIN_ID, IStatus.OK, message, null));
	}

	public static void error(String message, Throwable e) {
		log(new Status(IStatus.ERROR, PLUGIN_ID, IStatus.OK, message, e));
	}

	public static void warn(String message) {
		warn(message, null);
	}

	public static void warn(String message, Exception e) {
		log(new Status(IStatus.WARNING, PLUGIN_ID, IStatus.OK, message, e));
	}
}
