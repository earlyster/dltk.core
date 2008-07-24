package org.eclipse.dltk.debug.ui.tests.core;

import junit.framework.Assert;

import org.eclipse.dltk.core.tests.model.AbstractModelTests;
import org.eclipse.dltk.core.tests.model.TestConstants;
import org.eclipse.dltk.debug.ui.DLTKDebugUILanguageManager;
import org.eclipse.dltk.debug.ui.IDLTKDebugUILanguageToolkit;

public class DLTKDebugUILanguageManagerTests extends AbstractModelTests {

	public DLTKDebugUILanguageManagerTests(String name) {
		super("org.eclipse.dltk.core.tests.language", name);
	}

	public void testGetDebugUILanguageToolkit() {
		IDLTKDebugUILanguageToolkit toolkit = DLTKDebugUILanguageManager
				.getLanguageToolkit(TestConstants.NATURE_ID);

		Assert.assertNotNull(toolkit);
	}

	public void testGetDebugUILanguageToolkits() {
		IDLTKDebugUILanguageToolkit[] toolkits = DLTKDebugUILanguageManager
				.getLanguageToolkits();

		/*
		 * need at least 1, > 1 means multiple plugin implementations were
		 * installed in the pde when the test ran
		 */
		Assert.assertTrue(toolkits.length > 0);
	}
}
