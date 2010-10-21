/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.eclipse.dltk.core.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.dltk.core.tests.buildpath.BuildpathTests;
import org.eclipse.dltk.core.tests.compiler.CompilerCharOperationTests;
import org.eclipse.dltk.core.tests.compiler.CompilerUtilTests;
import org.eclipse.dltk.core.tests.ddp.CoreDDPTests;
import org.eclipse.dltk.core.tests.launching.EnvironmentResolverTests;
import org.eclipse.dltk.core.tests.launching.InterpreterConfigTests;
import org.eclipse.dltk.core.tests.mixin.MixinIndexTests;
import org.eclipse.dltk.core.tests.mixin.MixinModelTests;
import org.eclipse.dltk.core.tests.model.BufferTests;
import org.eclipse.dltk.core.tests.model.ModelMembersTests;
import org.eclipse.dltk.core.tests.model.NamespaceTests;
import org.eclipse.dltk.core.tests.model.WorkingCopyTests;
import org.eclipse.dltk.core.tests.util.CharacterStackTests;
import org.eclipse.dltk.core.tests.utils.CharOperationTests;
import org.eclipse.dltk.core.tests.utils.InternalCoreUtilTest;
import org.eclipse.dltk.core.tests.utils.TextUtilsTest;

public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("org.eclipse.dltk.core.tests.model");
		// $JUnit-BEGIN$
		suite.addTest(new TestSuite(CompilerUtilTests.class));
		suite.addTest(new TestSuite(CompilerCharOperationTests.class));
		suite.addTest(new TestSuite(InternalCoreUtilTest.class));
		suite.addTest(new TestSuite(MixinIndexTests.class));
		suite.addTest(new TestSuite(MixinModelTests.class));
		suite.addTest(BuildpathTests.suite());

		suite.addTest(CoreDDPTests.suite());

		suite.addTestSuite(NamespaceTests.class);
		suite.addTest(BufferTests.suite());
		suite.addTest(ModelMembersTests.suite());
		suite.addTest(WorkingCopyTests.suite());

		suite.addTest(InterpreterConfigTests.suite());

		suite.addTest(EnvironmentResolverTests.suite());

		suite.addTest(TextUtilsTest.suite());
		suite.addTest(CharOperationTests.suite());
		suite.addTestSuite(CharacterStackTests.class);
		// $JUnit-END$
		return suite;
	}
}
