package org.eclipse.dltk.compiler.problem;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.PreferencesLookupDelegate;

public class DefaultProblemSeverityTranslator extends PreferencesLookupDelegate
		implements IProblemSeverityTranslator {

	public DefaultProblemSeverityTranslator(IScriptProject project) {
		super(project);
	}

	private Map<IProblemIdentifier, ProblemSeverity> cache = new HashMap<IProblemIdentifier, ProblemSeverity>();

	public ProblemSeverity getSeverity(IProblemIdentifier problemId,
			ProblemSeverity defaultSeverity) {
		if (problemId instanceof IProblemIdentifierExtension2) {
			final IProblemIdentifier prime = ((IProblemIdentifierExtension2) problemId)
					.getPrimeIdentifier();
			if (prime != null) {
				problemId = prime;
			}
		}
		final ProblemSeverity cached = cache.get(problemId);
		if (cached != null) {
			if (cached != ProblemSeverity.DEFAULT) {
				return cached;
			}
		} else {
			final ProblemSeverity evaluated = evaluate(problemId);
			if (evaluated != null && evaluated != ProblemSeverity.DEFAULT) {
				cache.put(problemId, evaluated);
				return evaluated;
			} else {
				cache.put(problemId, ProblemSeverity.DEFAULT);
			}
		}
		return IDENTITY.getSeverity(problemId, defaultSeverity);
	}

	private ProblemSeverity evaluate(IProblemIdentifier problemId) {
		final String value = getString(problemId.contributor(),
				DefaultProblemIdentifier.encode(problemId));
		if (value != null && value.length() != 0) {
			try {
				return ProblemSeverity.valueOf(value);
			} catch (IllegalArgumentException e) {
				// fall thru
			}
		}
		return null;
	}

}
