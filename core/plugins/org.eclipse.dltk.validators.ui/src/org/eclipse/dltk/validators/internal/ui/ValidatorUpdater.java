/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.eclipse.dltk.validators.internal.ui;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.dltk.validators.core.IValidator;
import org.eclipse.dltk.validators.core.ValidatorRuntime;
import org.eclipse.dltk.validators.internal.core.ValidatorDefinitionsContainer;
import org.eclipse.jface.operation.IRunnableWithProgress;

/**
 * Saves validator settings.
 */
public class ValidatorUpdater {

	/**
	 * Updates Validator settings and returns whether the update was successful.
	 * 
	 * @param validators
	 *            new installed ValidatorEnvironments
	 * @param defaultInterp
	 *            new default Validator
	 * @return whether the update was successful
	 */
	public boolean updateValidatorSettings(IValidator[] validators) {
		// Create a Validator definition container
		final ValidatorDefinitionsContainer container = new ValidatorDefinitionsContainer();
		// Set the Validators on the container
		container.addValidators(validators);
		// Generate XML for the Validator defs and save it as the new value of
		// the Validator preference
		saveValidatorDefinitions(container);
		return true;
	}

	private void saveValidatorDefinitions(
			final ValidatorDefinitionsContainer container) {
		IRunnableWithProgress runnable = new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor)
					throws InvocationTargetException, InterruptedException {
				try {
					monitor
							.beginTask(ValidatorMessages.ValidatorUpdater_0,
									100);
					String xml = container.getAsXML();
					monitor.worked(40);
					ValidatorRuntime.getPreferences().setValue(
							ValidatorRuntime.PREF_VALIDATOR_XML,
							xml);
					monitor.worked(30);
					ValidatorRuntime.savePreferences();
					monitor.worked(30);
				} catch (IOException ioe) {
					ValidatorsUI.log(ioe);
				} catch (ParserConfigurationException e) {
					ValidatorsUI.log(e);
				} catch (TransformerException e) {
					ValidatorsUI.log(e);
				} finally {
					monitor.done();
				}
			}
		};
		try {
			ValidatorsUI.getDefault().getWorkbench().getProgressService()
					.busyCursorWhile(runnable);
		} catch (InvocationTargetException e) {
			ValidatorsUI.log(e);
		} catch (InterruptedException e) {
			ValidatorsUI.log(e);
		}
	}
}
