package org.eclipse.dltk.ui.actions;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.dltk.core.IDLTKLanguageToolkit;
import org.eclipse.dltk.ui.DLTKExecuteExtensionHelper;
import org.eclipse.dltk.ui.DLTKUILanguageManager;
import org.eclipse.dltk.ui.IDLTKUILanguageToolkit;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.text.source.IVerticalRulerInfo;
import org.eclipse.ui.texteditor.AbstractRulerActionDelegate;
import org.eclipse.ui.texteditor.ITextEditor;

/**
 * Delegate used to handle 'single clicks' that occur within the vertical ruler
 * of a script editor when an annotation is present.
 * 
 * <p>
 * Implementations need only addd the following snippet in their
 * <code>plugin.xml</code> to add support.
 * </p>
 * 
 * <pre>
 * &lt;extension point=&quot;org.eclipse.ui.editorActions&quot;&gt;
 *   &lt;editorContribution
 *     targetID=&quot;editor_id&quot;
 *     id=&quot;...&quot;&gt;     
 *     &lt;action
 *       label=&quot;...&quot;
 *       class=&quot;org.eclipse.dltk.ui.actions.ScriptEditorRulerActionDelegate:nature_id&quot;
 *       actionID=&quot;RulerClick&quot;
 *       id=&quot;...&quot; /&gt;
 *   &lt;/editorContribution&gt;
 * &lt;/extension&gt;
 * </pre>
 */
public class ScriptEditorRulerActionDelegate extends
		AbstractRulerActionDelegate implements IExecutableExtension {

	private IDLTKUILanguageToolkit uiToolkit;

	protected IAction createAction(ITextEditor editor,
			IVerticalRulerInfo rulerInfo) {
		return new ScriptSelectAnnotationRulerAction(editor, rulerInfo,
				uiToolkit);
	}

	public void setInitializationData(IConfigurationElement config,
			String propertyName, Object data) {
		IDLTKLanguageToolkit toolkit = DLTKExecuteExtensionHelper
				.getLanguageToolkit(config, propertyName, data);
		uiToolkit = DLTKUILanguageManager.getLanguageToolkit(toolkit
				.getNatureId());
	}
}
