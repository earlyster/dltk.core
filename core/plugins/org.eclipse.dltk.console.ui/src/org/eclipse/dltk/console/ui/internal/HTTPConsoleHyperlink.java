/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.eclipse.dltk.console.ui.internal;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.dltk.console.ui.ScriptConsoleUIPlugin;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;
import org.eclipse.ui.console.IHyperlink;
import org.eclipse.ui.console.TextConsole;

/**
 * A hyper link implementation for http:// protocol hyper links.
 */
public class HTTPConsoleHyperlink implements IHyperlink {
	private TextConsole fConsole;

	public HTTPConsoleHyperlink(TextConsole console) {
		fConsole = console;
	}

	public void linkEntered() {
	}

	public void linkExited() {
	}

	public void linkActivated() {
		String uri = getLinkURI();
		IWorkbenchBrowserSupport browserSupport = PlatformUI.getWorkbench()
				.getBrowserSupport();
		IWebBrowser browser;
		try {
			browser = browserSupport.createBrowser(null);
			browser.openURL(new URL(uri));
		} catch (PartInitException e) {
			final String msg = NLS.bind(
					Messages.HTTPConsoleHyperlink_failedToInitializeBrowserFor,
					uri);
			ScriptConsoleUIPlugin.error(msg, e);
		} catch (MalformedURLException e) {
			final String msg = NLS.bind(
					Messages.HTTPConsoleHyperlink_failedToOpenInvalidUri, uri);
			ScriptConsoleUIPlugin.error(msg, e);
		}
	}

	protected TextConsole getConsole() {
		return fConsole;
	}

	protected String getLinkURI() {
		try {
			IDocument document = getConsole().getDocument();
			IRegion region = getConsole().getRegion(this);
			return document.get(region.getOffset(), region.getLength());
		} catch (BadLocationException e) {
		}
		return null;
	}

}
