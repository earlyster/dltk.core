/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.eclipse.dltk.core.tests.model;

import java.util.Vector;

import junit.framework.Assert;

import org.eclipse.dltk.compiler.problem.IProblem;
import org.eclipse.dltk.core.CompletionProposal;
import org.eclipse.dltk.core.CompletionRequestor;

public class CompletionTestsRequestor extends CompletionRequestor {
	private Vector<String> fElements = new Vector<String>();
	private Vector<String> fCompletions = new Vector<String>();
	private Vector<String> fRelevances = new Vector<String>();
	private Vector<String> fCompletionStart = new Vector<String>();
	private Vector<String> fCompletionEnd = new Vector<String>();

	public boolean fDebug = false;

	@Override
	public void completionFailure(IProblem problem) {
		Assert.fail("completionFailure("
				+ (problem != null ? problem.getMessage() : "Unknown problem")
				+ ")");
	}

	private void acceptCommon(CompletionProposal proposal) {
		fCompletions.addElement(proposal.getCompletion());
		fRelevances.addElement(String.valueOf(proposal.getRelevance()));
		fCompletionStart.addElement(String.valueOf(proposal.getReplaceStart()));
		fCompletionEnd.addElement(String.valueOf(proposal.getReplaceEnd()));
	}

	@Override
	public void accept(CompletionProposal proposal) {
		String typeName = null;
		switch (proposal.getKind()) {

		case CompletionProposal.TYPE_REF:
			// typeName =
			// Signature.getSignatureSimpleName(proposal.getSignature());
			typeName = proposal.getName();
			fElements.addElement(typeName);
			this.acceptCommon(proposal);
			break;

		case CompletionProposal.FIELD_REF:
			fElements.addElement(proposal.getName());
			this.acceptCommon(proposal);
			if (fDebug)
				System.out.println("Field " + proposal.getName());
			break;

		case CompletionProposal.KEYWORD:
			fElements.addElement(proposal.getName());
			this.acceptCommon(proposal);
			if (fDebug)
				System.out.println("Keyword " + proposal.getName());
			break;

		case CompletionProposal.LABEL_REF:
			fElements.addElement(proposal.getName());
			this.acceptCommon(proposal);
			if (fDebug)
				System.out.println("Label " + proposal.getName());
			break;

		case CompletionProposal.LOCAL_VARIABLE_REF:
			fElements.addElement(proposal.getName());
			this.acceptCommon(proposal);
			if (fDebug)
				System.out.println("Local variable " + proposal.getName());
			break;

		case CompletionProposal.METHOD_REF:
			String resultString = proposal.getName() + "(";

			String[] parameterNames = proposal.findParameterNames(null);
			if (parameterNames != null) {
				for (int i = 0; i < parameterNames.length; i++) {
					if (i > 0)
						resultString += ",";
					resultString += parameterNames[i];
				}
			}
			resultString += ")";
			fElements.addElement(resultString);

			this.acceptCommon(proposal);
			if (fDebug) {
				System.out.println("method " + proposal.getName());
			}
			break;

		case CompletionProposal.METHOD_DECLARATION:
			fElements.addElement(proposal.getName());
			this.acceptCommon(proposal);
			if (fDebug)
				System.out.println("method declaration " + proposal.getName());
			break;

		case CompletionProposal.VARIABLE_DECLARATION:
			fElements.addElement(proposal.getName());
			this.acceptCommon(proposal);
			if (fDebug)
				System.out.println("variable name " + proposal.getName());
			break;
		}

	}

	public String getResults() {
		return getResults(true, false);
	}

	public String getResultsWithPosition() {
		return getResults(true, true);
	}

	public String getResults(boolean relevance, boolean position) {
		StringBuffer result = new StringBuffer();
		int size = fElements.size();

		if (size == 1) {
			result.append(getResult(0, relevance, position));
		} else if (size > 1) {
			String[] sortedBucket = new String[size];
			for (int i = 0; i < size; i++) {
				sortedBucket[i] = getResult(i, relevance, position);
			}
			quickSort(sortedBucket, 0, size - 1);
			for (int j = 0; j < sortedBucket.length; j++) {
				if (result.length() > 0)
					result.append("\n");
				result.append(sortedBucket[j]);
			}
		}

		return result.toString();
	}

	private String getResult(int i, boolean relevance, boolean position) {
		if (i < 0 || i >= fElements.size())
			return "";

		StringBuffer buffer = new StringBuffer();
		buffer.append("element:");
		buffer.append(fElements.elementAt(i));
		buffer.append("    completion:");
		buffer.append(fCompletions.elementAt(i));
		if (position) {
			buffer.append("    position:[");
			buffer.append(fCompletionStart.elementAt(i));
			buffer.append(",");
			buffer.append(fCompletionEnd.elementAt(i));
			buffer.append("]");
		}
		if (relevance) {
			buffer.append("    relevance:");
			buffer.append(fRelevances.elementAt(i));
		}
		return buffer.toString();
	}

	protected String[] quickSort(String[] collection, int left, int right) {
		int original_left = left;
		int original_right = right;
		String mid = collection[(left + right) / 2];
		do {
			while (mid.compareTo(collection[left]) > 0)
				// s[left] >= mid
				left++;
			while (mid.compareTo(collection[right]) < 0)
				// s[right] <= mid
				right--;
			if (left <= right) {
				String tmp = collection[left];
				collection[left] = collection[right];
				collection[right] = tmp;
				left++;
				right--;
			}
		} while (left <= right);
		if (original_left < right)
			collection = quickSort(collection, original_left, right);
		if (left < original_right)
			collection = quickSort(collection, left, original_right);
		return collection;
	}

	@Override
	public String toString() {
		return getResults();
	}
}
