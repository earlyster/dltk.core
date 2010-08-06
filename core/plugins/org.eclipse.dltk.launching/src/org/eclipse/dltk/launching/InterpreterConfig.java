package org.eclipse.dltk.launching;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.dltk.core.environment.IEnvironment;
import org.eclipse.dltk.core.environment.IExecutionEnvironment;
import org.eclipse.dltk.core.environment.IFileHandle;
import org.eclipse.dltk.internal.launching.EnvironmentResolver;
import org.eclipse.dltk.utils.PlatformFileUtils;

public class InterpreterConfig implements Cloneable {
	/**
	 * Script file to launch
	 */
	private IPath scriptFile;

	/**
	 * Working directory
	 */
	private IPath workingDirectory;

	/**
	 * Arguments for interpreter (Strings)
	 */
	private ArrayList<String> interpreterArgs;

	/**
	 * Arguments for script (Strings)
	 */
	private ArrayList<String> scriptArgs;

	/**
	 * Environment variables (String => String)
	 */
	private HashMap<String, String> environmentVariables;

	/**
	 * Additional properties (String => Object)
	 */
	private HashMap<String, Object> properties;

	private IEnvironment environment;

	private boolean noFile = false;

	protected void checkScriptFile(IPath file) {
		if (file == null) {
			throw new IllegalArgumentException(
					Messages.InterpreterConfig_scriptFileCannotBeNull);
		}
	}

	protected void checkWorkingDirectory(IPath directory) {
		if (directory == null) {
			throw new IllegalArgumentException(
					Messages.InterpreterConfig_workingDirectoryCannotBeNull);
		}
	}

	protected void init(IEnvironment environment, IPath scriptFile,
			IPath workingDirectory) {
		init(environment, scriptFile, workingDirectory, true);
	}

	protected void init(IEnvironment environment, IPath scriptFile,
			IPath workingDirectory, boolean isLocal) {
		this.environment = environment;

		// local debugger run
		if (isLocal) {
			// Script file
			this.scriptFile = scriptFile;

			// Working directory
			this.workingDirectory = workingDirectory != null ? workingDirectory
					: scriptFile.removeLastSegments(1);
		}

		this.interpreterArgs = new ArrayList<String>();
		this.scriptArgs = new ArrayList<String>();
		this.environmentVariables = new HashMap<String, String>();
		this.properties = new HashMap<String, Object>();
	}

	public InterpreterConfig() {
		init(null, null, null, false);
	}

	public InterpreterConfig(IEnvironment environment, IPath scriptFile) {
		this(environment, scriptFile, (IPath) null);
	}

	public InterpreterConfig(IEnvironment environment, IPath scriptFile,
			IPath workingDirectory) {
		if (scriptFile != null) {
			checkScriptFile(scriptFile);
		}
		init(environment, scriptFile, workingDirectory);
	}

	public IEnvironment getEnvironment() {
		return environment;
	}

	public IExecutionEnvironment getExecutionEnvironment() {
		IEnvironment environment = getEnvironment();
		if (environment != null) {
			return (IExecutionEnvironment) environment
					.getAdapter(IExecutionEnvironment.class);
		}
		return null;
	}

	public void setEnvironment(IEnvironment environment) {
		this.environment = environment;
	}

	public IPath getScriptFilePath() {
		return scriptFile;
	}

	public void setScriptFile(IPath file) {
		checkScriptFile(file);
		this.scriptFile = file;
	}

	public IPath getWorkingDirectoryPath() {
		return workingDirectory;
	}

	public void setWorkingDirectory(IPath directory) {
		checkWorkingDirectory(directory);
		this.workingDirectory = directory;
	}

	// Interpreter section
	public boolean addInterpreterArg(String arg) {
		if (arg == null) {
			throw new IllegalArgumentException(
					Messages.InterpreterConfig_interpreterArgumentCannotBeNull);
		}

		return interpreterArgs.add(arg);
	}

	public void addInterpreterArgs(String[] args) {
		for (int i = 0; i < args.length; ++i) {
			addInterpreterArg(args[i]);
		}
	}

	public void addInterpreterArgs(List<String> args) {
		interpreterArgs.addAll(args);
	}

	public boolean hasInterpreterArg(String arg) {
		return interpreterArgs.contains(arg);
	}

	public boolean hasMatchedInterpreterArg(String regex) {
		Iterator<String> it = interpreterArgs.iterator();
		while (it.hasNext()) {
			if (it.next().matches(regex)) {
				return true;
			}
		}

		return false;
	}

	public boolean removeInterpreterArg(String arg) {
		return interpreterArgs.remove(arg);
	}

	@SuppressWarnings("unchecked")
	public List<String> getInterpreterArgs() {
		return (List<String>) interpreterArgs.clone();
	}

	// Script section
	public boolean addScriptArg(String arg) {
		if (arg == null) {
			throw new IllegalArgumentException(
					Messages.InterpreterConfig_scriptArgumentCannotBeNull);
		}

		return scriptArgs.add(arg);
	}

	// Script section
	public void addScriptArg(String arg, int pos) {
		if (arg == null) {
			throw new IllegalArgumentException(
					Messages.InterpreterConfig_scriptArgumentCannotBeNull);
		}

		scriptArgs.add(pos, arg);
	}

	public void addScriptArgs(String[] args) {
		for (int i = 0; i < args.length; ++i) {
			addScriptArg(args[i]);
		}
	}

	public void addScriptArgs(List<String> args) {
		scriptArgs.addAll(args);
	}

	public boolean hasScriptArg(String arg) {
		return scriptArgs.contains(arg);
	}

	public boolean removeScriptArg(String arg) {
		return scriptArgs.remove(arg);
	}

	@SuppressWarnings("unchecked")
	public List<String> getScriptArgs() {
		return (List<String>) scriptArgs.clone();
	}

	// Environment
	public String addEnvVar(String name, String value) {
		if (name == null || value == null) {
			throw new IllegalArgumentException();
		}

		return environmentVariables.put(name, value);
	}

	public void addEnvVars(Map<String, String> vars) {
		environmentVariables.putAll(vars);
	}

	public String removeEnvVar(String name) {
		if (name == null) {
			throw new IllegalArgumentException();
		}

		return environmentVariables.remove(name);
	}

	public String getEnvVar(String name) {
		if (name == null) {
			throw new IllegalArgumentException();
		}

		return environmentVariables.get(name);
	}

	public boolean hasEnvVar(String name) {
		if (name == null) {
			throw new IllegalArgumentException();
		}

		return environmentVariables.containsKey(name);
	}

	@SuppressWarnings("unchecked")
	public Map<String, String> getEnvVars() {
		return (Map<String, String>) environmentVariables.clone();
	}

	public String[] getEnvironmentAsStrings() {
		ArrayList<String> list = new ArrayList<String>();
		Iterator<String> it = environmentVariables.keySet().iterator();
		while (it.hasNext()) {
			String key = it.next();
			String value = environmentVariables.get(key);
			list.add(key + "=" + value); //$NON-NLS-1$
		}

		return list.toArray(new String[list.size()]);
	}

	public String[] getEnvironmentAsStringsIncluding(EnvironmentVariable[] vars) {

		EnvironmentVariable[] variables = EnvironmentResolver.resolve(
				getEnvVars(), vars, true);
		Set<String> pressentVars = new HashSet<String>();
		ArrayList<String> list = new ArrayList<String>();
		if (variables != null) {
			for (int i = 0; i < variables.length; i++) {
				String name = variables[i].getName();
				list.add(name + "=" + variables[i].getValue()); //$NON-NLS-1$
				pressentVars.add(name);
			}
		}

		Iterator<String> it = environmentVariables.keySet().iterator();
		while (it.hasNext()) {
			String key = it.next();
			if (!pressentVars.contains(key)) {
				String value = environmentVariables.get(key);
				list.add(key + "=" + value); //$NON-NLS-1$
			}
		}

		return list.toArray(new String[list.size()]);
	}

	// Properties
	public Object setProperty(String name, Object value) {
		return properties.put(name, value);
	}

	public void unsetProperty(String name) {
		properties.remove(name);
	}

	public Object getProperty(String name) {
		return properties.get(name);
	}

	public void addProperties(Map<String, Object> map) {
		properties.putAll(map);
	}

	@SuppressWarnings("unchecked")
	public Map<String, Object> getPropeties() {
		return (Map<String, Object>) properties.clone();
	}

	@Override
	public Object clone() {
		final InterpreterConfig config = new InterpreterConfig(environment,
				scriptFile, workingDirectory);
		config.addProperties(getPropeties());
		config.addEnvVars(getEnvVars());
		config.addInterpreterArgs(getInterpreterArgs());
		config.addScriptArgs(getScriptArgs());
		return config;
	}

	public String[] renderCommandLine(IInterpreterInstall interpreter) {
		final List<String> items = new ArrayList<String>();

		items.add(interpreter.getInstallLocation().toOSString());
		items.addAll(interpreterArgs);

		String[] interpreterOwnArgs = interpreter.getInterpreterArguments();
		if (interpreterOwnArgs != null) {
			items.addAll(Arrays.asList(interpreterOwnArgs));
		}

		items.add(interpreter.getEnvironment().convertPathToString(scriptFile));
		items.addAll(scriptArgs);

		return items.toArray(new String[items.size()]);
	}

	protected String[] renderCommandLine(IEnvironment environment,
			IPath interpreter) {
		final List<String> items = new ArrayList<String>();

		items.add(environment.convertPathToString(interpreter));
		items.addAll(interpreterArgs);
		items.add(environment.convertPathToString(scriptFile));
		items.addAll(scriptArgs);

		return items.toArray(new String[items.size()]);
	}

	public String[] renderCommandLine(IEnvironment environment,
			String interpreter) {
		IFileHandle interpreterPath = PlatformFileUtils
				.findAbsoluteOrEclipseRelativeFile(environment, new Path(
						interpreter));
		return renderCommandLine(environment, interpreterPath.getPath());
	}

	// TODO: make more real implementation
	@Override
	public String toString() {
		final List<String> items = new ArrayList<String>();
		items.add("<interpreter>"); //$NON-NLS-1$
		items.addAll(interpreterArgs);
		items.add(String.valueOf(scriptFile));
		items.addAll(scriptArgs);

		Iterator<String> it = items.iterator();
		StringBuffer sb = new StringBuffer();
		while (it.hasNext()) {
			sb.append(it.next());
			sb.append(' ');
		}

		return sb.toString();
	}

	public void clearScriptArgs() {
		this.scriptArgs.clear();
	}

	public void setNoFile(boolean value) {
		this.noFile = value;
	}

	public boolean isNoFile() {
		return this.noFile;
	}
}
