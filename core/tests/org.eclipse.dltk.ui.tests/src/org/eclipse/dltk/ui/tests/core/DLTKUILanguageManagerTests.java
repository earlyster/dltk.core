package org.eclipse.dltk.ui.tests.core;

import junit.framework.Assert;

import org.eclipse.dltk.core.tests.model.AbstractModelTests;
import org.eclipse.dltk.core.tests.model.TestConstants;
import org.eclipse.dltk.ui.DLTKUILanguageManager;
import org.eclipse.dltk.ui.IDLTKUILanguageToolkit;

public class DLTKUILanguageManagerTests extends AbstractModelTests {

	public DLTKUILanguageManagerTests(String name) {
		super("org.eclipse.dltk.ui.tests.core", name);
	}

	public void testGetUILanguageToolkit() {
		IDLTKUILanguageToolkit toolkit = DLTKUILanguageManager
				.getLanguageToolkit(TestConstants.NATURE_ID);

		Assert.assertNotNull(toolkit);
	}

	public void testUILanguageToolkits() {
		IDLTKUILanguageToolkit[] toolkits = DLTKUILanguageManager
				.getLanguageToolkits();

		/*
		 * need at least 1, > 1 means multiple plugin implementations were 
		 * installed in the pde when the test ran 
		 */
		Assert.assertTrue(toolkits.length > 0);
	}

}
