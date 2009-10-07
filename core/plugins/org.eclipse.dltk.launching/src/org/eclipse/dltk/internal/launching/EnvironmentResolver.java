package org.eclipse.dltk.internal.launching;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.dltk.launching.EnvironmentVariable;

public final class EnvironmentResolver {
	private static class REnvironmentVariable {
		EnvironmentVariable var;
		final Set<String> dependencies = new HashSet<String>();

		public REnvironmentVariable(EnvironmentVariable var) {
			this.var = var;
		}
	}

	/*
	 * Resolves specified set of environment variables with system environment
	 */
	public static EnvironmentVariable[] resolve(Map<String, String> penv,
			EnvironmentVariable[] variables) {
		return resolve(penv, variables, false);
	}

	/*
	 * Resolves specified set of environment variables with system environment
	 */
	public static EnvironmentVariable[] resolve(Map<String, String> penv,
			EnvironmentVariable[] variables, boolean keepUnresolved) {
		if (variables == null) {
			return null;
		}
		Map<String, String> env = new HashMap<String, String>();
		Map<String, String> selfDep = new HashMap<String, String>();
		for (Map.Entry<String, String> entry : penv.entrySet()) {
			String name = entry.getKey();
			String value = entry.getValue();
			env.put(name, value);
		}

		for (int i = 0; i < variables.length; i++) {
			String name = variables[i].getName();
			if (env.containsKey(name)) {
				selfDep.put(name, env.get(name));
				env.remove(name);
			}
		}
		Map<String, String> resolved = new HashMap<String, String>();
		List<EnvironmentVariable> result = new ArrayList<EnvironmentVariable>();
		// 1) replace all top level environment variables
		List<REnvironmentVariable> unresolved = new ArrayList<REnvironmentVariable>();
		for (int i = 0; i < variables.length; i++) {
			REnvironmentVariable var = new REnvironmentVariable(
					new EnvironmentVariable(variables[i]));
			fillDependencies(var, variables);
			unresolved.add(var);
		}
		// To be sure we exit while loop
		int maxCycles = 1000;
		while (unresolved.size() > 0) {
			maxCycles--;
			if (maxCycles < 0) {
				break;
			}
			for (Iterator<REnvironmentVariable> iterator = unresolved
					.iterator(); iterator.hasNext();) {
				REnvironmentVariable var = iterator.next();
				if (isResolved(var.var)) {
					result.add(var.var);
					resolved.put(var.var.getName(), var.var.getValue());
					iterator.remove();
				} else {
					if (isCyclic(var, unresolved)) {
						// Resolve self cycles to environment
						if (isSelfCyclic(var)) {
							resolveVariable(var, env);
							resolveVariable(var, selfDep);
							if (isResolved(var.var)) {
								continue;
							}
						}

						if (keepUnresolved) {
							result.add(var.var);
						}
						iterator.remove();
						continue;
					}
					resolveVariable(var, resolved);
					resolveVariable(var, env);
					if (isResolved(var.var)) {
						continue;
					}
					if (isUnresolvable(var, unresolved)) {
						if (keepUnresolved) {
							result.add(var.var);
						}
						iterator.remove();
					}
				}
			}
		}

		return result.toArray(new EnvironmentVariable[result.size()]);
	}

	private static boolean isSelfCyclic(REnvironmentVariable var) {
		if (var.dependencies.isEmpty()) {
			return false;
		}
		if (var.dependencies.contains(var.var.getName())) {
			return true;
		}
		return false;
	}

	private static void fillDependencies(REnvironmentVariable var,
			EnvironmentVariable[] variables) {
		for (int j = 0; j < variables.length; j++) {
			if (containVar(var.var, variables[j].getName())) {
				var.dependencies.add(variables[j].getName());
			}
		}
	}

	private static boolean isUnresolvable(REnvironmentVariable var,
			List<REnvironmentVariable> unresolved) {
		EnvironmentVariable t = var.var;
		while (true) {
			boolean step = false;
			for (REnvironmentVariable rvar : unresolved) {
				if (!rvar.var.getName().equals(t.getName())
						&& containVar(t, rvar.var.getName())) {
					t = resolveVariable(t, rvar.var.getName(), rvar.var
							.getValue());
					step = true;
				}
			}
			if (!step) {
				break;
			}
		}
		if (!isResolved(t)) {
			return true;
		}
		return false;
	}

	private static EnvironmentVariable resolveVariable(EnvironmentVariable var,
			String name, String value) {
		String result = var.getValue();
		String pattern = "$" + name; //$NON-NLS-1$
		if (value.indexOf(pattern) != -1) {
			return null;
		}
		int pos = result.indexOf(pattern);
		while (pos != -1) {
			result = result.substring(0, pos) + value
					+ result.substring(pos + pattern.length());
			pos = result.indexOf(pattern, pos);
		}
		return new EnvironmentVariable(var.getName(), result);
	}

	private static boolean isCyclic(REnvironmentVariable var,
			List<REnvironmentVariable> unresolved) {
		// Detect direct cycles
		if (var.dependencies.size() == 0) {
			return false;
		}
		for (REnvironmentVariable env2 : unresolved) {
			if (var.dependencies.contains(env2.var.getName())
					&& env2.dependencies.contains(var.var.getName())) {
				return true;
			}
		}
		return false;
	}

	private static void resolveVariable(REnvironmentVariable var,
			Map<String, String> env) {
		EnvironmentVariable v = var.var;
		for (Map.Entry<String, String> entry : env.entrySet()) {
			final String varName = entry.getKey();
			if (containVar(v, varName)) {
				v = resolveVariable(v, varName, entry.getValue());
			}
		}
		var.var = v;
	}

	public static boolean isResolved(EnvironmentVariable var) {
		if (var == null) {
			throw new IllegalArgumentException();
		}
		String name = var.getValue();
		if (name.indexOf("$") == -1) { //$NON-NLS-1$
			return true;
		}
		return false;
	}

	public static boolean containVar(EnvironmentVariable var, String vName) {
		if (var == null) {
			throw new IllegalArgumentException();
		}
		final String value = var.getValue();
		final String ref = "$" + vName; //$NON-NLS-1$
		final int pos = value.indexOf(ref);
		if (pos != -1
				&& (pos + ref.length() >= value.length() || !Character
						.isLetterOrDigit(value.charAt(pos + ref.length())))) {
			return true;
		}
		return false;
	}

	/**
	 * Finds the variable with the specified name
	 * 
	 * @param vars
	 * @param name
	 * @return
	 * @since 2.0
	 */
	public static EnvironmentVariable find(EnvironmentVariable[] vars,
			String name) {
		if (vars != null && name != null) {
			for (EnvironmentVariable var : vars) {
				if (name.equals(var.getName())) {
					return var;
				}
			}
		}
		return null;
	}
}
