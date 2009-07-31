package org.eclipse.dltk.validators.internal.externalchecker.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.dltk.compiler.CharOperation;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.environment.IEnvironment;
import org.eclipse.dltk.core.environment.IExecutionEnvironment;
import org.eclipse.dltk.core.environment.IFileHandle;
import org.eclipse.dltk.internal.launching.EnvironmentResolver;
import org.eclipse.dltk.launching.EnvironmentVariable;
import org.eclipse.dltk.launching.IInterpreterInstall;
import org.eclipse.dltk.launching.ScriptRuntime;
import org.eclipse.dltk.validators.core.CommandLine;
import org.eclipse.dltk.validators.core.IValidatorOutput;
import org.eclipse.dltk.validators.core.IValidatorProblem;
import org.eclipse.dltk.validators.core.IValidatorReporter;
import org.eclipse.dltk.validators.core.ValidatorReporter;

/**
 * Delegate implementation of execution of external validators.
 */
class ExternalCheckerDelegate {

	public static final String MARKER_ID = ExternalCheckerPlugin.PLUGIN_ID
			+ ".externalcheckerproblem"; // $NON-NLS-1$

	private final String arguments;
	private final String command;

	private final IEnvironment environment;
	private final IExecutionEnvironment execEnvironment;
	private final String[] extensions;
	private final boolean passInterpreterEnvironmentVars;
	private final List<Rule> rules = new ArrayList<Rule>();

	static interface IExternalReporterDelegate {
		void report(IValidatorProblem problem) throws CoreException;
	}

	public ExternalCheckerDelegate(IEnvironment environment,
			ExternalChecker externalChecker) {
		this.environment = environment;
		this.execEnvironment = (IExecutionEnvironment) environment
				.getAdapter(IExecutionEnvironment.class);

		for (int i = 0; i < externalChecker.getNRules(); ++i) {
			rules.add(externalChecker.getRule(i));
		}

		this.arguments = externalChecker.getArguments();
		this.extensions = prepareExtensions(externalChecker.getExtensions());
		this.passInterpreterEnvironmentVars = externalChecker
				.isPassInterpreterEnvironmentVars();
		this.command = prepareCommand(externalChecker.getCommand(), environment);
	}

	public IValidatorReporter createValidatorReporter() {
		return new ValidatorReporter(getMarkerId(), false);
	}

	public String getMarkerId() {
		return MARKER_ID;
	}

	public boolean isValidatorConfigured() {
		if ((command == null) || (command.trim().length() == 0)) {
			return false;
		}

		return true;
	}

	public boolean isValidExtension(String extension) {
		if (extensions.length == 0) {
			return true;
		}

		for (int i = 0; i < extensions.length; ++i) {
			if (extension != null && extension.endsWith(extensions[i])) {
				return true;
			}
		}

		return false;
	}

	private static class EnvContainer {
		String[] environmentVars;
	}

	private final Map<IProject, EnvContainer> projectEnvs = new HashMap<IProject, EnvContainer>();

	public void runValidator(IResource resource, IValidatorOutput console,
			IExternalReporterDelegate delegate) throws CoreException {
		CommandLine cmdLine = new CommandLine(arguments);
		cmdLine.replaceSequence('f', getResourcePath(resource));
		cmdLine.add(0, command);
		final String[] env;
		if (passInterpreterEnvironmentVars) {
			final IProject project = resource.getProject();
			EnvContainer envContainer = projectEnvs.get(project);
			if (envContainer == null) {
				envContainer = new EnvContainer();
				IInterpreterInstall install = ScriptRuntime
						.getInterpreterInstall(DLTKCore.create(project));
				if (install != null) {
					EnvironmentVariable[] resolved = EnvironmentResolver
							.resolve(execEnvironment
									.getEnvironmentVariables(true), install
									.getEnvironmentVariables(), true);
					if (resolved != null) {
						envContainer.environmentVars = new String[resolved.length];
						for (int i = 0; i < resolved.length; ++i) {
							envContainer.environmentVars[i] = resolved[i]
									.toString();
						}
					}
				}
				projectEnvs.put(project, envContainer);
			}
			env = envContainer.environmentVars;
		} else {
			env = null;
		}
		Process process = execEnvironment.exec(cmdLine.toArray(), null, env);
		BufferedReader input = new BufferedReader(new InputStreamReader(process
				.getInputStream()));

		try {
			String line = null;
			while ((line = input.readLine()) != null) {
				console.println(line);

				IValidatorProblem problem = parseProblem(line);
				delegate.report(problem);
			}
		} catch (IOException e) {
			// throw new CoreException();
		}
	}

	private String getResourcePath(IResource resource) {
		if (resource.getLocation() != null) {
			return resource.getLocation().makeAbsolute().toOSString();
		}

		URI uri = resource.getLocationURI();
		IFileHandle file = environment.getFile(uri);
		return file.toOSString();
	}

	private IValidatorProblem parseProblem(String problem) {
		List wlist = ExternalCheckerWildcardManager.loadCustomWildcards();
		for (int i = 0; i < rules.size(); i++) {
			Rule rule = (Rule) this.rules.get(i);
			// String wcard = rule.getDescription();
			// List tlist = null;
			try {
				WildcardMatcher wmatcher = new WildcardMatcher(wlist);
				IValidatorProblem cproblem = wmatcher.match(rule, problem);
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

	private String prepareCommand(Map<IEnvironment, String> commands,
			IEnvironment environment) {
		String result = (String) commands.get(environment);
		if (result != null) {
			result = result.trim();
		}

		return result;
	}

	private String[] prepareExtensions(String extensions) {
		final String[] parts = extensions.split("[\\s;]+"); // $NON-NLS-1$
		for (int i = 0; i < parts.length; ++i) {
			if ("*".equals(parts[i])) // $NON-NLS-1$
			{
				return CharOperation.NO_STRINGS;
			}
		}

		return parts;
	}
}
