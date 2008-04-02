/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.eclipse.dltk.launching;

import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.Launch;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.environment.IExecutionEnvironment;
import org.eclipse.dltk.core.environment.IFileHandle;
import org.eclipse.dltk.internal.launching.DLTKLaunchingPlugin;
import org.eclipse.dltk.internal.launching.EnvironmentResolver;

public class ScriptLaunchUtil {
	// Create file with script content
	// public static File createScriptFileWithContent(String scriptContent)
	// throws IOException {
	// File file = File.createTempFile("script", null); //$NON-NLS-1$
	//
	// FileWriter writer = null;
	// try {
	// writer = new FileWriter(file);
	// writer.write(scriptContent);
	// } finally {
	// if (writer != null) {
	// writer.close();
	// }
	// }
	//
	// return file;
	// }

	// Creating of InterpreterConfig
	public static InterpreterConfig createInterpreterConfig(
			IExecutionEnvironment exeEnv, IFileHandle scriptFile,
			IFileHandle workingDirectory) {
		return createInterpreterConfig(exeEnv, scriptFile, workingDirectory,
				null);
	}

	public static InterpreterConfig createInterpreterConfig(
			IExecutionEnvironment exeEnv, IFileHandle scriptFile,
			IFileHandle workingDirectory, EnvironmentVariable[] env) {
		InterpreterConfig config = new InterpreterConfig(scriptFile
				.getEnvironment(), new Path(scriptFile.getAbsolutePath()),
				new Path(workingDirectory.getAbsolutePath()));

		Map envVars = exeEnv.getEnvironmentVariables();
		config.addEnvVars(envVars);
		EnvironmentVariable[] resVars = EnvironmentResolver.resolve(envVars,
				env);
		if (resVars != null) {
			for (int i = 0; i < resVars.length; i++) {
				config.addEnvVar(resVars[i].getName(), resVars[i].getValue());
			}
		}

		return config;
	}

	// Useful run methods
	public static Process runScriptWithInterpreter(
			IExecutionEnvironment exeEnv, String interpreter,
			InterpreterConfig config) throws CoreException {
		String[] cmdLine = config.renderCommandLine(interpreter);

		String[] environmentAsStrings = config.getEnvironmentAsStrings();
		IPath workingDirectoryPath = config.getWorkingDirectoryPath();
		if (DLTKLaunchingPlugin.TRACE_EXECUTION) {
			traceExecution("runScript with interpreter", cmdLine, //$NON-NLS-1$
					environmentAsStrings);
		}
		return exeEnv.exec(cmdLine, workingDirectoryPath, environmentAsStrings);
	}

	private static void traceExecution(String processLabel,
			String[] cmdLineLabel, String[] environment) {
		StringBuffer sb = new StringBuffer();
		sb.append("-----------------------------------------------\n"); //$NON-NLS-1$
		sb.append("Running ").append(processLabel).append('\n'); //$NON-NLS-1$
		// sb.append("Command line: ").append(cmdLineLabel).append('\n');
		sb.append("Command line: "); //$NON-NLS-1$
		for (int i = 0; i < cmdLineLabel.length; i++) {
			sb.append(" " + cmdLineLabel[i]); //$NON-NLS-1$
		}
		sb.append("\n"); //$NON-NLS-1$
		sb.append("Environment:\n"); //$NON-NLS-1$
		for (int i = 0; i < environment.length; i++) {
			sb.append('\t').append(environment[i]).append('\n');
		}
		sb.append("-----------------------------------------------\n"); //$NON-NLS-1$
		System.out.println(sb);
	}

	public static Process runScriptWithInterpreter(
			IExecutionEnvironment exeEnv, String interpreter,
			IFileHandle scriptFile, IFileHandle workingDirectory,
			String[] interpreterArgs, String[] scriptArgs,
			EnvironmentVariable[] environment) throws CoreException {
		InterpreterConfig config = createInterpreterConfig(exeEnv, scriptFile,
				workingDirectory, environment);

		if (scriptArgs != null) {
			config.addScriptArgs(scriptArgs);
		}

		if (interpreterArgs != null) {
			config.addInterpreterArgs(interpreterArgs);
		}

		return runScriptWithInterpreter(exeEnv, interpreter, config);
	}

	// public static Process runScriptWithInterpreter(String interpreter,
	// String scriptContent, IFileHandle workingDirectory,
	// String[] interpreterArgs, String[] scriptArgs,
	// EnvironmentVariable[] environment) throws CoreException,
	// IOException {
	// return runScriptWithInterpreter(interpreter,
	// createScriptFileWithContent(scriptContent), workingDirectory,
	// interpreterArgs, scriptArgs, environment);
	// }

	// 
	public static IInterpreterInstall getDefaultInterpreterInstall(
			String natureId) {
		return ScriptRuntime.getDefaultInterpreterInstall(natureId);
	}

	public static IInterpreterInstall getProjectInterpreterInstall(
			IScriptProject project) throws CoreException {
		return ScriptRuntime.getInterpreterInstall(project);
	}

	// General run method
	public static ILaunch runScript(IInterpreterInstall install,
			InterpreterConfig config, IProgressMonitor monitor)
			throws CoreException {

		if (install == null) {
			// TODO: Handle this error!!!
		}

		ILaunch launch = new Launch(null, ILaunchManager.RUN_MODE, null);

		// will use 'instance scoped' interpreter here
		IInterpreterRunner runner = install.getInterpreterRunner(launch
				.getLaunchMode());

		runner.run(config, launch, monitor);

		return launch;
	}

	// Run by interpreter form project
	public static ILaunch runScript(IScriptProject project,
			InterpreterConfig config, IProgressMonitor monitor)
			throws CoreException {
		return runScript(getProjectInterpreterInstall(project), config, monitor);
	}

	// Run by default interpreter
	public static ILaunch runScript(String natureId, InterpreterConfig config,
			IProgressMonitor monitor) throws CoreException {
		IInterpreterInstall install = getDefaultInterpreterInstall(natureId);
		EnvironmentVariable[] variables = EnvironmentResolver.resolve(config
				.getEnvVars(), install.getEnvironmentVariables());
		if (variables != null) {
			for (int i = 0; i < variables.length; i++) {
				config.addEnvVar(variables[i].getName(), variables[i]
						.getValue());
			}
		}
		return runScript(install, config, monitor);
	}

	// Script file
	public static ILaunch runScript(String natureId, IFileHandle scriptFile,
			IFileHandle workingDirectory, String[] interpreterArgs,
			String[] scriptArgs, IProgressMonitor monitor) throws CoreException {
		InterpreterConfig config = createInterpreterConfig(null, scriptFile,
				workingDirectory);

		if (interpreterArgs != null) {
			config.addInterpreterArgs(interpreterArgs);
		}

		if (scriptArgs != null) {
			config.addScriptArgs(scriptArgs);
		}

		return runScript(natureId, config, monitor);
	}

	// public static ILaunch runScript(String natureId, IFileHandle scriptFile)
	// throws CoreException {
	// return runScript(natureId, scriptFile, null, null, null, null);
	// }
	//
	// // Script content
	// public static ILaunch runScript(String natureId, String scriptContent,
	// IFileHandle workingDirectory, String[] interpreterArgs,
	// String[] scriptArgs, IProgressMonitor monitor)
	// throws CoreException, IOException {
	// return runScript(natureId, createScriptFileWithContent(scriptContent),
	// workingDirectory, interpreterArgs, scriptArgs, monitor);
	// }

	// public static ILaunch runScript(String natureId, String scriptContent)
	// throws CoreException, IOException {
	// return runScript(natureId, createScriptFileWithContent(scriptContent));
	// }
}
