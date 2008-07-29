/*******************************************************************************
 * Copyright (c) 2008 xored software, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     xored software, Inc. - initial API and Implementation (Alex Panchenko)
 *******************************************************************************/
package org.eclipse.dltk.core.tests.xml;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Own implementation to have the same output on any platform.
 */
public class DOMSerializer {

	/** Indentation to use (default is no indentation) */
	private String indent = "";

	/** Line separator to use */
	private String lineSeparator = "\n";

	/** Encoding for output (default is UTF-8) */
	private String encoding = "UTF8";

	/** Attributes will be displayed on separate lines */
	private boolean displayAttributesOnSeperateLine = true;

	private boolean skipEmptyText = true;

	public void setLineSeparator(String lineSeparator) {
		this.lineSeparator = lineSeparator;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public void setIndent(int numSpaces) {
		final char[] buffer = new char[numSpaces];
		Arrays.fill(buffer, ' ');
		this.indent = new String(buffer);
	}

	public void serialize(Document doc, OutputStream out) throws IOException {
		Writer writer = new OutputStreamWriter(out, encoding);
		serialize(doc, writer);
	}

	public void serialize(Document doc, File file) throws IOException {
		Writer writer = new FileWriter(file);
		serialize(doc, writer);
	}

	public void serialize(Document doc, Writer writer) throws IOException {
		doc.normalize();
		// Start serialization recursion with no indenting
		serializeNode(doc, writer, "");
		writer.flush();
	}

	public String serialize(Document doc) throws IOException {
		final StringWriter writer = new StringWriter();
		serialize(doc, writer);
		return writer.toString();
	}

	private void serializeNode(Node node, Writer writer, String indentLevel)
			throws IOException {
		// Determine action based on node type
		switch (node.getNodeType()) {
		case Node.DOCUMENT_NODE:
			// Document doc = (Document) node;
			// writer.write("<?xml version=\"");
			// writer.write(doc.getXmlVersion());
			// writer.write("\" encoding=\"UTF-8\" standalone=\"");
			// if (doc.getXmlStandalone())
			// writer.write("yes");
			// else
			// writer.write("no");
			// writer.write("\"");
			// writer.write("?>");
			// writer.write(lineSeparator);
			// recurse on each top-level node
			Node[] nodes = collectChildren(node);
			if (nodes != null) {
				for (int i = 0; i < nodes.length; i++) {
					serializeNode(nodes[i], writer, "");
				}
			}
			break;
		case Node.ELEMENT_NODE:
			final String name = node.getNodeName();
			writer.write(indentLevel + "<" + name);
			writeAttributes(node, writer, indentLevel);

			// recurse on each child
			final Node[] children = collectChildren(node);
			if (children != null) {
				// Close the open tag
				writer.write(">");
				if (children[0].getNodeType() == Node.ELEMENT_NODE) {
					writer.write(lineSeparator);
				}
				for (int i = 0; i < children.length; i++) {
					serializeNode(children[i], writer, indentLevel + indent);
				}
				if (children[children.length - 1].getNodeType() == Node.ELEMENT_NODE) {
					writer.write(indentLevel);
				}
				writer.write("</" + name + ">");
			} else {
				// Close this element without making a frivolous full close tag
				writer.write("/>");
			}
			writer.write(lineSeparator);
			break;
		case Node.TEXT_NODE:
			final String value = node.getNodeValue();
			if (!skipEmptyText || !isBlank(value)) {
				print(writer, value);
			}
			break;
		case Node.CDATA_SECTION_NODE:
			writer.write("<![CDATA[");
			print(writer, node.getNodeValue());
			writer.write("]]>");
			break;
		case Node.COMMENT_NODE:
			writer.write(indentLevel + "<!-- " + node.getNodeValue() + " -->");
			writer.write(lineSeparator);
			break;
		case Node.PROCESSING_INSTRUCTION_NODE:
			writer.write("<?" + node.getNodeName() + " " + node.getNodeValue()
					+ "?>");
			writer.write(lineSeparator);
			break;
		case Node.ENTITY_REFERENCE_NODE:
			writer.write("&" + node.getNodeName() + ";");
			break;
		case Node.DOCUMENT_TYPE_NODE:
			DocumentType docType = (DocumentType) node;
			String publicId = docType.getPublicId();
			String systemId = docType.getSystemId();
			String internalSubset = docType.getInternalSubset();
			writer.write("<!DOCTYPE " + docType.getName());
			if (publicId != null)
				writer.write(" PUBLIC \"" + publicId + "\" ");
			else
				writer.write(" SYSTEM ");
			writer.write("\"" + systemId + "\"");
			if (internalSubset != null)
				writer.write(" [" + internalSubset + "]");
			writer.write(">");
			writer.write(lineSeparator);
			break;
		}
	}

	private void writeAttributes(Node node, Writer writer, String indentLevel)
			throws IOException {
		NamedNodeMap attributes = node.getAttributes();
		final String[] attributeNames = new String[attributes.getLength()];
		for (int i = 0; i < attributes.getLength(); i++) {
			attributeNames[i] = attributes.item(i).getNodeName();
		}
		Arrays.sort(attributeNames);
		for (int i = 0; i < attributes.getLength(); i++) {
			Node current = attributes.getNamedItem(attributeNames[i]);
			String attributeSeperator = " ";
			// If we only have one attribute write it on the same line
			// otherwise indent
			if (displayAttributesOnSeperateLine && attributes.getLength() != 1) {
				attributeSeperator = lineSeparator + indentLevel + indent;
			}
			// Double indentLevel to match parent element and then one
			// indentation to format below parent
			String attributeStr = attributeSeperator + current.getNodeName()
					+ "=\"";
			writer.write(attributeStr);
			print(writer, current.getNodeValue());
			writer.write("\"");
		}
	}

	private Node[] collectChildren(Node parent) {
		final NodeList children = parent.getChildNodes();
		if (children != null && children.getLength() > 0) {
			final List result = new ArrayList();
			for (int i = 0; i < children.getLength(); i++) {
				final Node child = children.item(i);
				if (child == null) {
					continue;
				}
				if (skipEmptyText && child.getNodeType() == Node.TEXT_NODE
						&& isBlank(child.getNodeValue())) {
					continue;
				}
				result.add(child);
			}
			if (!result.isEmpty()) {
				return (Node[]) result.toArray(new Node[result.size()]);
			}
		}
		return null;
	}

	private static boolean isBlank(String str) {
		if (str == null) {
			return true;
		}
		final int strLen = str.length();
		if (strLen == 0) {
			return true;
		}
		for (int i = 0; i < strLen; i++) {
			if ((Character.isWhitespace(str.charAt(i)) == false)) {
				return false;
			}
		}
		return true;
	}

	private void print(Writer writer, String s) throws IOException {
		if (s == null)
			return;
		for (int i = 0, len = s.length(); i < len; i++) {
			char c = s.charAt(i);
			switch (c) {
			case '<':
				writer.write("&lt;");
				break;
			case '>':
				writer.write("&gt;");
				break;
			case '&':
				writer.write("&amp;");
				break;
			case '\r':
				writer.write("&#xD;");
				break;
			default:
				writer.write(c);
			}
		}
	}

	public boolean isDisplayAttributesOnSeperateLine() {
		return displayAttributesOnSeperateLine;
	}

	public void setDisplayAttributesOnSeperateLine(
			boolean displayAttributesOnSeperateLine) {
		this.displayAttributesOnSeperateLine = displayAttributesOnSeperateLine;
	}

}
