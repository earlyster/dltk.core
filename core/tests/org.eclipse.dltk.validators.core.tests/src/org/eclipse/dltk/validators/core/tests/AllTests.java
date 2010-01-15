package org.eclipse.dltk.validators.core.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite(
				"org.eclipse.dltk.validators.core.tests");
		// $JUnit-BEGIN$
		suite.addTestSuite(ValidatorContainerTests.class);
		suite.addTestSuite(CommandLineTests.class);
		// $JUnit-END$
		return suite;
	}

}
