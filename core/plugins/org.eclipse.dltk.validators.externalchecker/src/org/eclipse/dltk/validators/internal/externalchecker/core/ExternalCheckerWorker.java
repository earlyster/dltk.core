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
package org.eclipse.dltk.validators.internal.externalchecker.core;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.dltk.compiler.CharOperation;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.ISourceRange;
import org.eclipse.dltk.core.builder.ISourceLineTracker;
import org.eclipse.dltk.core.environment.IEnvironment;
import org.eclipse.dltk.core.environment.IExecutionEnvironment;
import org.eclipse.dltk.core.environment.IFileHandle;
import org.eclipse.dltk.utils.TextUtils;
import org.eclipse.dltk.validators.core.AbstractExternalValidator;
import org.eclipse.dltk.validators.core.CommandLine;
import org.eclipse.dltk.validators.core.IResourceValidator;
import org.eclipse.dltk.validators.core.ISourceModuleValidator;
import org.eclipse.dltk.validators.core.IValidatorOutput;

public class ExternalCheckerWorker extends AbstractExternalValidator implements
		ISourceModuleValidator, IResourceValidator {

	public static final String PROBLEM_ID = ExternalCheckerPlugin.PLUGIN_ID
			+ ".externalcheckerproblem"; //$NON-NLS-1$

	private final IEnvironment environment;
	private final IExecutionEnvironment execEnvironment;
	private final List rules = new ArrayList();
	private final String arguments;
	private final String[] extensions;
	private final String command;

	/**
	 * @param console
	 */
	public ExternalCheckerWorker(IEnvironment environment,
			ExternalChecker externalChecker) {
		this.environment = environment;
		this.execEnvironment = (IExecutionEnvironment) environment
				.getAdapter(IExecutionEnvironment.class);
		for (int i = 0; i < externalChecker.getNRules(); ++i) {
			rules.add(externalChecker.getRule(i));
		}
		this.arguments = externalChecker.getArguments();
		this.extensions = prepareExtensions(externalChecker.getExtensions());
		this.command = prepareCommand(externalChecker.getCommand(), environment);
	}

	/**
	 * @param commands
	 * @param environment
	 * @return
	 */
	private static String prepareCommand(Map commands, IEnvironment environment) {
		String result = (String) commands.get(environment);
		if (result != null) {
			result = result.trim();
		}
		return result;
	}

	/**
	 * @param extensions
	 * @return
	 */
	private static String[] prepareExtensions(String extensions) {
		final String[] parts = extensions.split("[\\s;]+"); //$NON-NLS-1$
		for (int i = 0; i < parts.length; ++i) {
			if ("*".equals(parts[i])) { //$NON-NLS-1$
				return CharOperation.NO_STRINGS;
			}
		}
		return parts;
	}

	protected String getMarkerType() {
		return PROBLEM_ID;
	}

	public IStatus validate(ISourceModule[] modules, IValidatorOutput console,
			IProgressMonitor monitor) {
		monitor.beginTask(
				Messages.ExternalChecker_checkingWithExternalExecutable,
				modules.length);
		try {
			for (int i = 0; i < modules.length; i++) {
				if (monitor.isCanceled())
					return Status.CANCEL_STATUS;
				validate(modules[i], console);
				monitor.worked(1);
			}
		} finally {
			monitor.done();
		}
		return Status.OK_STATUS;
	}

	public IStatus validate(IResource[] resources, IValidatorOutput output,
			IProgressMonitor monitor) {
		// TODO should be implemented
		return Status.CANCEL_STATUS;
	}

	protected boolean checkExtension(ISourceModule module) {
		if (extensions.length == 0) {
			return true;
		}
		final String elementName = module.getElementName();
		for (int i = 0; i < extensions.length; ++i) {
			if (elementName.endsWith(extensions[i])) {
				return true;
			}
		}
		return false;
	}

	private IStatus validate(ISourceModule module, IValidatorOutput console) {
		if (!checkExtension(module)) {
			return Status.OK_STATUS;
		}
		IResource resource = module.getResource();
		if (resource == null) {
			return new Status(IStatus.ERROR, ExternalCheckerPlugin.PLUGIN_ID,
					Messages.ExternalChecker_sourceModuleResourceIsNull);
		}

		clean(resource);

		if (command == null || command.trim().length() == 0) {
			return Status.CANCEL_STATUS;
		}
		CommandLine cmdLine = new CommandLine(arguments);
		cmdLine.replaceSequence('f', getResourcePath(resource));
		cmdLine.add(0, command);

		BufferedReader input = null;
		Process process = null;
		try {
			try {
				process = execEnvironment.exec(cmdLine.toArray(), null, null);
			} catch (Throwable e) {
				if (DLTKCore.DEBUG) {
					System.out.println(e.toString());
				}
				return Status.CANCEL_STATUS;
			}
			input = new BufferedReader(new InputStreamReader(process
					.getInputStream()));

			ISourceLineTracker model = null;
			String line = null;
			while ((line = input.readLine()) != null) {
				console.println(line);
				ExternalCheckerProblem problem = parseProblem(line);
				if (problem != null) {
					if (model == null) {
						model = TextUtils.createLineTracker(module
								.getSourceAsCharArray());
					}
					ISourceRange bounds = model.getLineInformation(problem
							.getLineNumber() - 1);
					if (problem.getType().indexOf(
							Messages.ExternalChecker_error) != -1) {
						reportError(resource, problem.getLineNumber(), bounds
								.getOffset(), (bounds.getOffset() + bounds
								.getLength()), problem.getDescription());
					} else if (problem.getType().indexOf(
							Messages.ExternalChecker_warning) != -1) {
						reportWarning(resource, problem.getLineNumber(), bounds
								.getOffset(), (bounds.getOffset() + bounds
								.getLength()), problem.getDescription());
					}
				}
			}
		} catch (Exception e) {
			if (DLTKCore.DEBUG) {
				System.out.println(e.toString());
			}
		}
		return Status.OK_STATUS;
	}

	private String getResourcePath(IResource resource) {
		if (resource.getLocation() != null) {
			return resource.getLocation().makeAbsolute().toOSString();
		} else {
			URI uri = resource.getLocationURI();
			IFileHandle file = environment.getFile(uri);
			return file.toOSString();
		}
	}

	public ExternalCheckerProblem parseProblem(String problem) {
		List wlist = ExternalCheckerWildcardManager.loadCustomWildcards();
		for (int i = 0; i < rules.size(); i++) {
			Rule rule = (Rule) this.rules.get(i);
			// String wcard = rule.getDescription();
			// List tlist = null;
			try {
				WildcardMatcher wmatcher = new WildcardMatcher(wlist);
				ExternalCheckerProblem cproblem = wmatcher.match(rule, problem);
				if (cproblem != null) {
					return cproblem;
				}
			} catch (Exception x) {
				if (DLTKCore.DEBUG) {
					System.out.println(x.toString());
				}
				continue;
			}
		}
		return null;
	}

}
