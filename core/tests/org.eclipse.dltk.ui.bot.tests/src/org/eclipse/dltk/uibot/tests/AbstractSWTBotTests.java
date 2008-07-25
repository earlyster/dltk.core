package org.eclipse.dltk.uibot.tests;

import net.sf.swtbot.eclipse.finder.SWTEclipseBot;
import net.sf.swtbot.widgets.SWTBotShell;

import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.tests.model.AbstractModelTests;
import org.eclipse.dltk.internal.ui.editor.EditorUtility;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

public abstract class AbstractSWTBotTests extends AbstractModelTests {

	protected final SWTEclipseBot bot = new SWTEclipseBot();

	protected AbstractSWTBotTests(String testProjectName, String name) {
		super(testProjectName, name);
	}

	protected void waitForProjectToBuild() throws CoreException {
		waitUntilIndexesReady();
		ResourcesPlugin.getWorkspace()
				.build(IncrementalProjectBuilder.FULL_BUILD,
						new NullProgressMonitor());
		waitForAutoBuild();
	}

	protected void closeEditorPart(IEditorPart part) {
		part.getSite().getWorkbenchWindow().getActivePage().closeEditor(part,
				false);
	}

	protected IEditorPart openSourceFileInEditor(String project,
			String sourceFolder, String fileName, String perspectiveId)
			throws Exception {
		ISourceModule cu = getSourceModule(project, sourceFolder, fileName);

		IWorkbenchWindow[] ww = PlatformUI.getWorkbench().getWorkbenchWindows();
		PlatformUI.getWorkbench().showPerspective(perspectiveId, ww[0]);

		SWTBotShell[] sh = bot.shells();
		sh[0].activate();

		return EditorUtility.openInEditor(cu);
	}

}
