package org.eclipse.dltk.core.tests.model;

import org.eclipse.dltk.ast.declarations.ModuleDeclaration;
import org.eclipse.dltk.ast.parser.AbstractSourceParser;
import org.eclipse.dltk.compiler.env.IModuleSource;
import org.eclipse.dltk.compiler.problem.IProblemReporter;

public class TestSourceParser extends AbstractSourceParser {

	public ModuleDeclaration parse(IModuleSource input,
			IProblemReporter reporter) {
		return new ModuleDeclaration(input.getSourceContents().length());
	}

}
