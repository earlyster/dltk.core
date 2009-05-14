package org.eclipse.dltk.ast.parser;

import org.eclipse.dltk.ast.declarations.ModuleDeclaration;
import org.eclipse.dltk.compiler.problem.ProblemCollector;
import org.eclipse.dltk.core.ISourceModule;

public interface IASTCache {
	public static class ASTCacheEntry {
		public ModuleDeclaration module;
		public ProblemCollector problems;
	}

	ASTCacheEntry restoreModule(ISourceModule module);

	void storeModule(ISourceModule module, ModuleDeclaration moduleDeclaration,
			ProblemCollector problems);
}
