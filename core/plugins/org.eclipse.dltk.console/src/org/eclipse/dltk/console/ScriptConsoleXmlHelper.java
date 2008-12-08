/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.eclipse.dltk.console;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.dltk.core.DLTKCore;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public final class ScriptConsoleXmlHelper {
	private ScriptConsoleXmlHelper() {
	}

	protected static int convertState(String state) {
		if (state.equals("new")) { //$NON-NLS-1$
			return IScriptConsoleInterpreter.WAIT_NEW_COMMAND;
		} else if (state.equals("continue")) { //$NON-NLS-1$
			return IScriptConsoleInterpreter.WAIT_CONTINUE_COMMAND;
		} else if (state.equals("user")) { //$NON-NLS-1$
			return IScriptConsoleInterpreter.WAIT_USER_INPUT;
		}

		return -1;
	}

	protected static boolean isElement(Node node, String name) {
		return node.getNodeType() == Node.ELEMENT_NODE
				&& node.getNodeName().equals(name);
	}

	protected static Document parse(String xml) {
		if ((xml == null) || (xml.length() == 0))
			return null;

		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			StringReader reader = new StringReader(xml);
			InputSource source = new InputSource(reader);
			source.setEncoding("UTF-8"); //$NON-NLS-1$
			return builder.parse(source);
		} catch (ParserConfigurationException e) {
			if (DLTKCore.DEBUG) {
				e.printStackTrace();
			}
		} catch (SAXException e) {
			if (DLTKCore.DEBUG) {
				e.printStackTrace();
			}
		} catch (IOException e) {
			if (DLTKCore.DEBUG) {
				e.printStackTrace();
			}
		}

		return null;
	}

	protected static List parseCompletionList(Node completionNode) {
		NodeList children = completionNode.getChildNodes();

		List completions = new ArrayList();
		for (int i = 0; i < children.getLength(); ++i) {
			Node node = children.item(i);

			if (!isElement(node, "case")) { //$NON-NLS-1$
				continue;
			}

			Element element = (Element) node;

			String display = element.getAttribute("display"); //$NON-NLS-1$
			String insert = element.getAttribute("insert"); //$NON-NLS-1$
			String type = element.getAttribute("type"); //$NON-NLS-1$

			completions.add(new ScriptConsoleCompletionProposal(insert,
					display, type));
		}

		return completions;
	}

	public static String parseInfoXml(String xml) {
		Document doc = parse(xml);

		if (doc == null) {
			return null;
		}

		NodeList list = doc.getElementsByTagName("console"); //$NON-NLS-1$
		Node node = list.item(0);

		list = node.getChildNodes();
		for (int i = 0; i < list.getLength(); ++i) {
			Node n = list.item(i);
			if (isElement(n, "info")) { //$NON-NLS-1$
				return ((Element) n).getAttribute("id"); //$NON-NLS-1$
			}
		}

		return null;
	}

	public static ShellResponse parseShellXml(String xml) {
		Document doc = parse(xml);

		if (doc == null) {
			return null;
		}

		NodeList list1 = doc.getElementsByTagName("console"); //$NON-NLS-1$
		Node node = list1.item(0);

		list1 = node.getChildNodes();
		for (int i = 0; i < list1.getLength(); ++i) {
			Node n1 = list1.item(i);
			if (isElement(n1, "shell")) { //$NON-NLS-1$
				NodeList list2 = n1.getChildNodes();
				for (int j = 0; j < list2.getLength(); ++j) {
					Node n2 = list2.item(j);
					if (isElement(n2, "completion")) { //$NON-NLS-1$
						List completions = parseCompletionList(n2);
						return new ShellResponse(completions);
					} else if (isElement(n2, "description")) { //$NON-NLS-1$
						if (n2.getChildNodes().getLength() == 0) {
							return new ShellResponse(""); //$NON-NLS-1$
						} else {
							return new ShellResponse(n2.getFirstChild()
									.getNodeValue());
						}
					} else if (isElement(n2, "close")) { //$NON-NLS-1$
						return new ShellResponse();
					}
				}
			}
		}

		return null;
	}

	public static InterpreterResponse parseInterpreterXml(String xml) {
		Document doc = parse(xml);

		if (doc == null) {
			return null;
		}

		NodeList list = doc.getElementsByTagName("console").item(0) //$NON-NLS-1$
				.getChildNodes();

		for (int i = 0; i < list.getLength(); ++i) {
			Node n = list.item(i);
			if (isElement(n, "interpreter")) { //$NON-NLS-1$
				Element element = (Element) n;
				String state = element.getAttribute("state"); //$NON-NLS-1$
				String response = ""; //$NON-NLS-1$

				// Check for empty response
				if (n.getChildNodes().getLength() > 0) {
					response = n.getFirstChild().getNodeValue();
				}

				final String stream = element.getAttribute("stream"); //$NON-NLS-1$
				final boolean isError = "stderr".equals(stream); //$NON-NLS-1$
				return new InterpreterResponse(convertState(state), isError,
						response);
			}
		}

		return null;
	}
}
