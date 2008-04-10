package org.eclipse.dltk.core.tests.launching;

import org.eclipse.dltk.core.environment.IFileHandle;

public interface IFileVisitor {

	boolean visit(IFileHandle file);

}
