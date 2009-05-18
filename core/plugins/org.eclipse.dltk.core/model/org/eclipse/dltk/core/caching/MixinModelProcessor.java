package org.eclipse.dltk.core.caching;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

import org.eclipse.dltk.core.mixin.IMixinRequestor;
import org.eclipse.dltk.core.mixin.IMixinRequestor.ElementInfo;

public class MixinModelProcessor extends AbstractDataLoader {

	private IMixinRequestor requestor;

	public MixinModelProcessor(InputStream input, IMixinRequestor requestor) {
		super(input);
		this.requestor = requestor;
	}

	public void process() throws IOException {
		readStrings();
		while (true) {
			try {
				String key = readString();
				if (key != null) {
					ElementInfo elementInfo = new ElementInfo();
					elementInfo.key = key;
					requestor.reportElement(elementInfo);
				}
			} catch (EOFException e) {
				break;

			} catch (IOException e) {
				break;
			}
		}
	}
}
