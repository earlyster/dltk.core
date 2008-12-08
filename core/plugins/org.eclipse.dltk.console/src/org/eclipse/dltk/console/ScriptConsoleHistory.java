/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.eclipse.dltk.console;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.dltk.compiler.util.Util;

public class ScriptConsoleHistory {

	/**
	 * History items. Items are added to the end. Always has at least one item,
	 * since empty line is added to keep the value of the currently selected
	 * line.
	 */
	private final List lines = new ArrayList();

	public ScriptConsoleHistory() {
		lines.add(Util.EMPTY_STRING);
	}

	/**
	 * The index of the current item.
	 * 
	 * Invariant:
	 * <code>selection &gt;= 0 &gt;&gt; selection < lines.size()</code>
	 */
	private int selection = 0;

	private void addToHistory(String line) {
		final int index = lines.indexOf(line);
		if (index >= 0) {
			if (index != lines.size() - 1) {
				lines.remove(index);
			}
		}
		lines.set(lines.size() - 1, line);
	}

	/**
	 * Adds the specified line to the top of the history
	 * 
	 * @param line
	 */
	public void add(String line) {
		if (line != null && line.length() != 0) {
			addToHistory(line);
			lines.add(Util.EMPTY_STRING);
			selection = lines.size() - 1;
		}
	}

	/**
	 * Moves the selection to the previous item. Returns <code>true</code> on
	 * success or <code>false</code> otherwise.
	 * 
	 * @return
	 */
	public boolean prev() {
		if (selection > 0) {
			--selection;
			return true;
		}
		return false;
	}

	/**
	 * Moves the selection to the next item. Returns <code>true</code> on
	 * success or <code>false</code> otherwise.
	 * 
	 * @return
	 */
	public boolean next() {
		if (selection < lines.size() - 1) {
			++selection;
			return true;
		}
		return false;
	}

	/**
	 * Returns the text of the currently selected line.
	 * 
	 * @return
	 */
	public String get() {
		return (String) lines.get(selection);
	}

	/**
	 * Updates the text of the currently selected line
	 * 
	 * @param line
	 */
	public void updateSelectedLine(String line) {
		if (selection >= 0 && selection < lines.size()) {
			/*
			 * TODO probably it should a temporary change until a command is
			 * executed, so the history will contain only commands which were
			 * actually executed.
			 */
			lines.set(selection, line);
		}
	}
}
