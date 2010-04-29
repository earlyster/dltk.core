package org.eclipse.dltk.core.builder;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.dltk.ast.declarations.ModuleDeclaration;
import org.eclipse.dltk.compiler.task.ITodoTaskPreferences;
import org.eclipse.dltk.compiler.task.TodoTaskAstParser;
import org.eclipse.dltk.core.IScriptProject;

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

	@Override
	public final IBuildParticipant createBuildParticipant(IScriptProject project) {
		final ITodoTaskPreferences prefs = getPreferences(project);
		if (prefs.isEnabled()) {
			return getBuildParticipant(prefs);
		}

		return null;
	}

	/**
	 * Returns the <code>Preferences</code> object that contains the settings
	 * for 'todo' tasks.
	 */
	protected abstract ITodoTaskPreferences getPreferences(
			IScriptProject project);

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

	protected static class TodoTaskBuildParticipant extends TodoTaskAstParser
			implements IBuildParticipant {

		public TodoTaskBuildParticipant(ITodoTaskPreferences preferences) {
			super(preferences);
		}

		public void build(IBuildContext context) throws CoreException {
			if (isValid()) {
				final ModuleDeclaration ast = (ModuleDeclaration) context
						.get(IBuildContext.ATTR_MODULE_DECLARATION);
				initialize(ast);
				parse(context.getTaskReporter(), context.getContents());
			}
		}
	}
}
