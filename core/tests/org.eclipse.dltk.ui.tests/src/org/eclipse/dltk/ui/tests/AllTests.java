package org.eclipse.dltk.ui.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.dltk.ui.tests.core.DLTKUILanguageManagerTests;
import org.eclipse.dltk.ui.tests.core.ScriptElementLabelsTest;
import org.eclipse.dltk.ui.tests.navigator.scriptexplorer.PackageExplorerTests;
import org.eclipse.dltk.ui.tests.refactoring.ModelElementUtilTests;
import org.eclipse.dltk.ui.tests.templates.ScriptTemplateContextTest;
import org.eclipse.dltk.ui.tests.text.TodoHighlightingTest;

public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("org.eclipse.dltk.ui.tests");
		// $JUnit-BEGIN$
		suite.addTest(ScriptElementLabelsTest.suite());
		suite.addTestSuite(DLTKUILanguageManagerTests.class);
		suite.addTestSuite(ModelElementUtilTests.class);

		suite.addTest(PackageExplorerTests.suite());
		suite.addTest(ScriptTemplateContextTest.suite());
		suite.addTest(TodoHighlightingTest.suite());
		// $JUnit-END$
		return suite;
	}

}
