/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.eclipse.dltk.validators.core.tests;

import java.io.StringReader;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.dltk.validators.core.IValidator;
import org.eclipse.dltk.validators.core.ValidatorRuntime;
import org.eclipse.dltk.validators.internal.core.ValidatorDefinitionsContainer;

public class ValidatorContainerTests extends TestCase {
	public void testValidatorContainer001() throws Exception {
		ValidatorDefinitionsContainer co = new ValidatorDefinitionsContainer();
		assertNotNull(co);
		SimpleValidatorType vt = SimpleValidatorUtils.find();
		assertNotNull(vt);
		assertTrue(vt.isConfigurable());
		IValidator v1 = vt.createValidator("v1");
		assertNotNull(v1);
		co.addValidator(v1);
		IValidator v2 = vt.createValidator("v2");
		((SimpleValidator) v2).setValid(false);
		assertNotNull(v2);
		co.addValidator(v2);
		String xml = co.getAsXML();
		assertNotNull(xml);
		System.out.println(xml);
		List validValidatorsList = co.getValidatorList();
		List validatorList = co.getValidatorList();
		assertNotNull(validValidatorsList);
		assertNotNull(validatorList);
		assertTrue(validValidatorsList.contains(v1));
		assertTrue(validValidatorsList.contains(v2));
		assertTrue(validatorList.contains(v1));
		assertTrue(validatorList.contains(v2));

		ValidatorDefinitionsContainer co2 = ValidatorDefinitionsContainer
				.createFromXML(new StringReader(xml));
		assertNotNull(co2);
		List validatorList2 = co2.getValidatorList();
		assertNotNull(validatorList2);
		assertEquals(2, validatorList.size());
		List validatorList3 = co2.getValidatorList("#");
		assertNotNull(validatorList3);
		assertEquals(2, validatorList.size());
		for (int i = 0; i < validatorList2.size(); i++) {
			IValidator v = (IValidator) validatorList2.get(i);
			assertNotNull(v);
			assertTrue(v.getID().equals("v1") || v.getID().equals("v2"));
			if (v.getID().equals("v1")) {
				assertTrue(((SimpleValidator) v).isValid());
			}
			if (v.getID().equals("v2")) {
				assertFalse(((SimpleValidator) v).isValid());
			}
		}
	}

	public void testValidatorContainerNatures() throws CoreException {
		ValidatorDefinitionsContainer container = new ValidatorDefinitionsContainer();
		final SimpleValidatorType type = SimpleValidatorUtils.find();
		final IValidator v = type.createValidator("v");
		final SimpleValidatorType2 type2 = new SimpleValidatorType2();
		final IValidator v2 = type2.createValidator("v2");
		container.addValidator(v);
		container.addValidator(v2);

		List all = container.getValidatorList();
		assertEquals(2, all.size());
		assertTrue(all.contains(v));
		assertTrue(all.contains(v2));

		List byNature = container.getValidatorList(SimpleValidatorType2.NATURE);
		assertEquals(2, byNature.size());
		assertTrue(byNature.contains(v));
		assertTrue(byNature.contains(v2));

		List otherNature = container.getValidatorList("ABC");
		assertEquals(1, otherNature.size());
		assertTrue(otherNature.contains(v));

		List universal = container
				.getValidatorList(ValidatorRuntime.ANY_NATURE);
		assertEquals(1, universal.size());
		assertTrue(universal.contains(v));
	}

}
