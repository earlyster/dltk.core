package org.eclipse.dltk.debug.core.model;

import java.net.URI;

public interface IScriptBreakpointPathMapper {

	URI map(URI uri);

}
