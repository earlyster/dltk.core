package org.eclipse.dltk.core.builder;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Preferences;
import org.eclipse.dltk.ast.declarations.ModuleDeclaration;
import org.eclipse.dltk.compiler.problem.IProblemReporter;
import org.eclipse.dltk.compiler.task.ITodoTaskPreferences;
import org.eclipse.dltk.compiler.task.TodoTaskAstParser;
import org.eclipse.dltk.compiler.task.TodoTaskPreferences;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.ISourceModule;

/**
 * Abstract class that may be used to add build support for 'todo' type task
 * tags.
 * 
 * <p>
 * Subclasses should be sure to include an empty argument public constructor as
 * part of their implementation. The following snippet may be used in the
 * <code>plugin.xml</code> file.
 * </p>
 * 
 * <pre>
 * &lt;extension point=&quot;org.eclipse.dltk.core.buildParticipant&quot;&gt;
 *   &lt;buildParticipant
 *     class=&quot;...&quot;
 *     id=&quot;...&quot;
 *     nature=&quot;...&quot; /&gt;  
 * &lt;extension&gt;
 * </pre>
 */
public abstract class AbstractTodoTaskBuildParticipantType extends
		AbstractBuildParticipantType {

	protected AbstractTodoTaskBuildParticipantType(String id, String name) {
		super(id, name);
	}

	protected final IBuildParticipant createBuildParticipant(
			IScriptProject project) {
		final ITodoTaskPreferences prefs = new TodoTaskPreferences(
				getPreferences());
		if (prefs.isEnabled()) {
			return getBuildParticipant(prefs);
		}

		return null;
	}

	/**
	 * Returns the <code>Preferences</code> object that contains the settings
	 * for 'todo' tasks.
	 */
	protected abstract Preferences getPreferences();

	/**
	 * Returns the build participant that will be used to report 'todo' task
	 * tags.
	 * 
	 * <p>
	 * Default implementation returns an instance of
	 * <code>TodoTaskBuildParticipant</code>. Subclasses are free to override
	 * this method if they wish to provide a different implementation.
	 * </p>
	 */
	protected IBuildParticipant getBuildParticipant(
			ITodoTaskPreferences preferences) {
		return new TodoTaskBuildParticipant(preferences);
	}

	private class TodoTaskBuildParticipant implements IBuildParticipant {

		private TodoTaskAstParser parser;

		public TodoTaskBuildParticipant(ITodoTaskPreferences preferences) {
			parser = new TodoTaskAstParser(preferences);
		}

		public void build(ISourceModule module, ModuleDeclaration ast,
				IProblemReporter reporter) throws CoreException {
			parser.build(module, ast, reporter);
		}
	}
}
