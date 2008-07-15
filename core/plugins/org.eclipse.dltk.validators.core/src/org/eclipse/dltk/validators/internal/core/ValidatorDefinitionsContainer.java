/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     xored software, Inc. - initial API and Implementation
 *******************************************************************************/
package org.eclipse.dltk.validators.internal.core;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.validators.core.IValidator;
import org.eclipse.dltk.validators.core.IValidatorType;
import org.eclipse.dltk.validators.core.ValidatorRuntime;
import org.eclipse.osgi.util.NLS;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class ValidatorDefinitionsContainer {

	private static final String NODE_VALIDATOR_SETTINGS = "validatorSettings"; //$NON-NLS-1$
	private static final String NODE_VALIDATOR_TYPE = "validatorType"; //$NON-NLS-1$
	private static final String NODE_VALIDATOR = "validator"; //$NON-NLS-1$

	private static final String ATTR_ID = "id"; //$NON-NLS-1$

	private final Map fValidatorsByType = new HashMap(10);
	private final List fValidatorList = new ArrayList(10);

	public void addValidator(IValidator validator) {
		if (!fValidatorList.contains(validator)) {
			fValidatorList.add(validator);
			IValidatorType type = validator.getValidatorType();
			List typeValidators = (List) fValidatorsByType.get(type);
			if (typeValidators == null) {
				typeValidators = new ArrayList(3);
				fValidatorsByType.put(type, typeValidators);
			}
			typeValidators.add(validator);
		}
	}

	public void addValidators(List validatorList) {
		for (Iterator i = validatorList.iterator(); i.hasNext();) {
			final IValidator validator = (IValidator) i.next();
			addValidator(validator);
		}
	}

	public void addValidators(IValidator[] validators) {
		for (int i = 0; i < validators.length; ++i) {
			addValidator(validators[i]);
		}
	}

	/**
	 * Returns unmodifiable list of all {@link IValidator}s
	 * 
	 * @return
	 */
	public List getValidatorList() {
		return Collections.unmodifiableList(fValidatorList);
	}

	/**
	 * Returns unmodifiable list of {@link IValidator}s of the specified nature,
	 * validators contributed to all natures are also returned.
	 * 
	 * @param nature
	 * @return
	 */
	public List getValidatorList(String nature) {
		final List result = new ArrayList(fValidatorList.size());
		for (Iterator i = fValidatorsByType.entrySet().iterator(); i.hasNext();) {
			final Map.Entry entry = (Map.Entry) i.next();
			final IValidatorType type = (IValidatorType) entry.getKey();
			final String typeNature = type.getNature();
			if (nature.equals(typeNature)
					|| ValidatorRuntime.ANY_NATURE.equals(typeNature)) {
				result.addAll((List) entry.getValue());
			}
		}
		return Collections.unmodifiableList(result);
	}

	public String getAsXML() throws ParserConfigurationException, IOException,
			TransformerException {

		// Create the Document and the top-level node
		Document doc = ValidatorsCore.getDocument();
		Element config = doc.createElement(NODE_VALIDATOR_SETTINGS);
		doc.appendChild(config);

		// Create a node for each validator type represented in this container
		for (Iterator i = fValidatorsByType.entrySet().iterator(); i.hasNext();) {
			final Map.Entry entry = (Map.Entry) i.next();
			final IValidatorType validatorType = (IValidatorType) entry
					.getKey();
			if (validatorType.isConfigurable()) {
				Element valiatorTypeElement = validatorTypeAsElement(doc,
						validatorType, (List) entry.getValue());
				config.appendChild(valiatorTypeElement);
			}
		}

		// Serialize the Document and return the resulting String
		return ValidatorsCore.serializeDocument(doc);
	}

	private Element validatorTypeAsElement(Document doc,
			IValidatorType validatorType, List validatorList) {

		// Create a node for the Interpreter type and set its 'id' attribute
		Element element = doc.createElement(NODE_VALIDATOR_TYPE);
		element.setAttribute(ATTR_ID, validatorType.getID());

		// For each validator of the specified type, create a subordinate node
		// for it
		for (Iterator i = validatorList.iterator(); i.hasNext();) {
			IValidator validator = (IValidator) i.next();
			Element validatorElement = validatorAsElement(doc, validator);
			element.appendChild(validatorElement);
		}

		return element;
	}

	private Element validatorAsElement(Document doc, IValidator validator) {
		// Create the node for the validator and set its 'id' & 'name'
		// attributes
		Element element = doc.createElement(NODE_VALIDATOR);
		element.setAttribute(ATTR_ID, validator.getID());

		validator.storeTo(doc, element);
		return element;
	}

	public static ValidatorDefinitionsContainer createFromXML(Reader input)
			throws IOException {
		ValidatorDefinitionsContainer container = new ValidatorDefinitionsContainer();
		container.parseXML(new InputSource(input));
		return container;
	}

	public void parseXML(InputSource input) throws IOException {

		// Do the parsing and obtain the top-level node
		Element config = null;
		try {
			DocumentBuilder parser = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder();
			parser.setErrorHandler(new DefaultHandler());
			config = parser.parse(input).getDocumentElement();
		} catch (SAXException e) {
			throw new IOException(ValidatorMessages.ValidatorRuntime_badFormat);
		} catch (ParserConfigurationException e) {
			throw new IOException(ValidatorMessages.ValidatorRuntime_badFormat);
		}

		// If the top-level node wasn't what we expected, bail out
		if (!config.getNodeName().equalsIgnoreCase(NODE_VALIDATOR_SETTINGS)) {
			throw new IOException(ValidatorMessages.ValidatorRuntime_badFormat);
		}

		// Traverse the parsed structure and populate the InterpreterType to
		// Interpreter Map
		NodeList list = config.getChildNodes();
		int length = list.getLength();
		for (int i = 0; i < length; ++i) {
			Node node = list.item(i);
			short type = node.getNodeType();
			if (type == Node.ELEMENT_NODE) {
				Element validatorTypeElement = (Element) node;
				if (validatorTypeElement.getNodeName().equalsIgnoreCase(
						NODE_VALIDATOR_TYPE)) {
					populateValidatorType(validatorTypeElement);
				}
			}
		}
	}

	/**
	 * For the specified Interpreter type node, parse all subordinate
	 * Interpreter definitions and add them to the specified container.
	 */
	private void populateValidatorType(Element validatorTypeElement) {

		// Retrieve the 'id' attribute and the corresponding Interpreter type
		// object
		String id = validatorTypeElement.getAttribute(ATTR_ID);
		IValidatorType validatorType = ValidatorManager
				.getValidatorTypeFromID(id);
		if (validatorType != null) {

			// For each validator child node, populate the container with a
			// subordinate node
			NodeList validatorNodeList = validatorTypeElement.getChildNodes();
			for (int i = 0; i < validatorNodeList.getLength(); ++i) {
				Node childNode = validatorNodeList.item(i);
				short type = childNode.getNodeType();
				if (type == Node.ELEMENT_NODE) {
					Element validatorElement = (Element) childNode;
					if (validatorElement.getNodeName().equalsIgnoreCase(
							NODE_VALIDATOR)) {
						populateValidator(validatorType, validatorElement);
					}
				}
			}
		} else {
			final String msg = ValidatorMessages.ValidatorDefinitionsContainer_unknownValidatorType;
			ValidatorsCore.warn(NLS.bind(msg, id));
		}
	}

	/**
	 * Parse the specified Interpreter node, create a InterpreterStandin for it,
	 * and add this to the specified container.
	 */
	private void populateValidator(IValidatorType type, Element element) {
		String id = element.getAttribute(ATTR_ID);
		if (id != null) {
			try {
				final IValidator validator;
				if (type.isBuiltin()) {
					validator = type.findValidator(id);
				} else {
					validator = type.createValidator(id);
				}
				if (validator != null) {
					if (type.isConfigurable()) {
						validator.loadFrom(element);
					}
					addValidator(validator);
				}
			} catch (DOMException e) {
				final String msg = ValidatorMessages.ValidatorDefinitionsContainer_failedToLoadValidatorFromXml;
				ValidatorsCore.error(msg, e);
			}
		} else {
			if (DLTKCore.DEBUG) {
				System.err
						.println("id attribute missing from validator element specification."); //$NON-NLS-1$
			}
		}
	}

	/**
	 * Removes the specified {@link IValidator} from this container.
	 * 
	 * @param validator
	 *            validator instance
	 */
	public void removeValidator(IValidator validator) {
		fValidatorList.remove(validator);
		List list = (List) fValidatorsByType.get(validator.getValidatorType());
		if (list != null) {
			list.remove(validator);
		}
	}

}
