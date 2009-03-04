package org.eclipse.dltk.validators.internal.externalchecker.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.eclipse.core.runtime.Path;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.environment.EnvironmentManager;
import org.eclipse.dltk.core.environment.IEnvironment;
import org.eclipse.dltk.core.environment.IFileHandle;
import org.eclipse.dltk.validators.core.AbstractValidator;
import org.eclipse.dltk.validators.core.IResourceValidator;
import org.eclipse.dltk.validators.core.ISourceModuleValidator;
import org.eclipse.dltk.validators.core.IValidatorType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ExternalChecker extends AbstractValidator {

	private static final String EXTENSIONS = "scriptPattrn"; //$NON-NLS-1$

	private static final String ARGUMENTS = "arguments"; //$NON-NLS-1$
	private static final String PATH_TAG = "path"; //$NON-NLS-1$
	private static final String ENVIRONMENT_ATTR = "environment"; //$NON-NLS-1$
	private static final String PATH_ATTR = "path"; //$NON-NLS-1$

	private String arguments;
	private Map paths;
	private List rules = new ArrayList();
	private String extensions;

	public void setCommand(Map command) {
		this.paths = command;
		fireChanged();
	}

	public void setRules(Vector list) {
		rules.clear();
		rules.addAll(list);
		fireChanged();
	}

	public Map getCommand() {
		return paths;
	}

	public ExternalChecker(String id, String name, IValidatorType type) {
		super(id, name, type);
		this.arguments = "%f"; //$NON-NLS-1$
		this.paths = newEmptyPath();
		this.extensions = "*"; //$NON-NLS-1$
	}

	private Map newEmptyPath() {
		Map result = new HashMap();
		IEnvironment[] environments = EnvironmentManager.getEnvironments();
		for (int i = 0; i < environments.length; i++) {
			result.put(environments[i], ""); //$NON-NLS-1$
		}
		return result;
	}

	protected void load(Element element) {
		super.load(element);
		paths = newEmptyPath();
		// this.path = new Path(element.getAttribute(PATHS_TAG));
		NodeList childNodes = element.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); i++) {
			Node item = childNodes.item(i);
			if (item.getNodeType() == Node.ELEMENT_NODE) {
				Element elementNode = (Element) item;
				if (elementNode.getTagName().equalsIgnoreCase(PATH_TAG)) {
					String environment = elementNode
							.getAttribute(ENVIRONMENT_ATTR);
					String path = elementNode.getAttribute(PATH_ATTR);
					IEnvironment env = EnvironmentManager
							.getEnvironmentById(environment);
					if (env != null) {
						this.paths.put(env, path);
					}
				}
			}
		}
		this.arguments = element.getAttribute(ARGUMENTS);
		this.extensions = element.getAttribute(EXTENSIONS);

		NodeList nodes = element.getChildNodes();
		rules.clear();
		for (int i = 0; i < nodes.getLength(); i++) {
			if (nodes.item(i).getNodeName() == "rule") { //$NON-NLS-1$
				NamedNodeMap map = nodes.item(i).getAttributes();
				String ruletext = map.getNamedItem("TEXT").getNodeValue(); //$NON-NLS-1$
				String ruletype = map.getNamedItem("TYPE").getNodeValue(); //$NON-NLS-1$
				Rule r = new Rule(ruletext, ruletype);
				rules.add(r);
			}
		}
	}

	public void storeTo(Document doc, Element element) {
		super.storeTo(doc, element);
		element.setAttribute(ARGUMENTS, this.arguments);
		element.setAttribute(EXTENSIONS, this.extensions);

		for (int i = 0; i < rules.size(); i++) {
			Element elem = doc.createElement("rule"); //$NON-NLS-1$
			elem.setAttribute("TEXT", ((Rule) rules.get(i)).getDescription()); //$NON-NLS-1$
			elem.setAttribute("TYPE", ((Rule) rules.get(i)).getType()); //$NON-NLS-1$
			element.appendChild(elem);
		}

		for (Iterator iterator = paths.keySet().iterator(); iterator.hasNext();) {
			IEnvironment env = (IEnvironment) iterator.next();
			if (env != null) {
				Element elem = doc.createElement(PATH_TAG);
				elem.setAttribute(ENVIRONMENT_ATTR, env.getId());
				elem.setAttribute(PATH_ATTR, (String) paths.get(env));
				element.appendChild(elem);
			}
		}
	}

	public void setArguments(String arguments) {
		this.arguments = arguments;
		fireChanged();
	}

	public String getArguments() {
		return arguments;
	}

	public void setNewRule(Rule s) {
		rules.add(s);
	}

	public Rule getRule(int index) {
		if (index < rules.size())
			return (Rule) rules.get(index);
		return null;
	}

	public int getNRules() {
		return rules.size();
	}

	public boolean isValidatorValid(IScriptProject project) {
		final IEnvironment environment = getEnvrironment(project);
		String path = (String) this.paths.get(environment);
		if (path == null || path.trim().length() == 0) {
			return false;
		}
		IFileHandle file = environment.getFile(new Path(path));

		if (!file.exists()) {
			return false;
		}

		return true;
	}

	public String getExtensions() {
		return extensions;
	}

	public void setExtensions(String scriptPattern) {
		this.extensions = scriptPattern;
		fireChanged();
	}

	public Object getValidator(IScriptProject project, Class validatorType) {
		if (validatorType == IResourceValidator.class) {
			return new ExternalResourceWorker(getEnvrironment(project), this);
		}
		
		if (validatorType == ISourceModuleValidator.class) {		
			return new ExternalSourceModuleWorker(getEnvrironment(project), this);
		}
		
		// safety incase new validator types are introduced
		return null;
	}

	protected Object clone() {
		try {
			final ExternalChecker clone = (ExternalChecker) super.clone();
			clone.paths = new HashMap(paths);
			clone.rules = new ArrayList(rules);
			return clone;
		} catch (CloneNotSupportedException e) {
			// should not happen
			return null;
		}
	}

}
