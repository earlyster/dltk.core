package org.eclipse.dltk.ast.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.SimpleClassDLTKExtensionManager;
import org.eclipse.dltk.core.SimpleDLTKExtensionManager.ElementInfo;

public class ASTCacheManager {
	private static SimpleClassDLTKExtensionManager manager = new SimpleClassDLTKExtensionManager(
			DLTKCore.PLUGIN_ID + ".astCache");

	private static Map<String, IASTCache[]> providers = null;

	public synchronized static IASTCache[] getProviders(String lang) {
		if (providers == null) {
			providers = new HashMap<String, IASTCache[]>();

			ElementInfo[] infos = manager.getElementInfos();
			Map<String, List<IASTCache>> langToElementList = new HashMap<String, List<IASTCache>>();
			// Fill element names and sort elements by language
			for (int i = 0; i < infos.length; i++) {
				String langauge = infos[i].getConfig().getAttribute("language");
				List<IASTCache> elements = langToElementList.get(langauge);
				if (elements == null) {
					elements = new ArrayList<IASTCache>();
					langToElementList.put(langauge, elements);
				}
				elements.add((IASTCache) manager.getInitObject(infos[i]));
			}
			for (Map.Entry<String, List<IASTCache>> entry : langToElementList
					.entrySet()) {
				List<IASTCache> list = entry.getValue();
				IASTCache[] result = list.toArray(new IASTCache[list.size()]);
				providers.put(entry.getKey(), result);
			}
		}
		return providers.get(lang);
	}
}
