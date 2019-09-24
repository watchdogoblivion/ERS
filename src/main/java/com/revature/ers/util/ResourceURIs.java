package com.revature.ers.util;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

public class ResourceURIs {

	private static final Map<String, String> HTMLURIS = new HashMap<>();
	private static final Map<String, String> CSSURIS = new HashMap<>();
	private static final Map<String, String> JSURIS = new HashMap<>();
	private static final Map<String, String> DIRECTORYURIS = new HashMap<>();
	private static final FileManager FILE_MANAGER = new FileManager();

	private ResourceURIs() {
	}
	
	public static Map<String, String> getHTMLURIS(HttpServletRequest request, String root) {
		final Map<String, String> directoryUris = getDIRECTORYURIS(request, root, 1);
		List<File> htmlFiles = FILE_MANAGER.getFiles(request, directoryUris.get("html"));
		for (File file : htmlFiles) {
			String key = file.getName().substring(0, file.getName().indexOf('.'));
			HTMLURIS.put(key, file.getPath().substring(file.getPath().lastIndexOf(root)));
		}
		Map<String, String> shallowCopy = new HashMap<>();
		shallowCopy.putAll(HTMLURIS);
		return shallowCopy;
	}

	public static Map<String, String> getCSSURIS(HttpServletRequest request, String root) {
		final Map<String, String> directoryUris = getDIRECTORYURIS(request, root, 1);
		List<File> cssFiles = FILE_MANAGER.getFiles(request, directoryUris.get("css"));
		for (File file : cssFiles) {
			String key = file.getName().substring(0, file.getName().indexOf('.'));
			CSSURIS.put(key, file.getPath().substring(file.getPath().lastIndexOf(root)));
		}
		Map<String, String> shallowCopy = new HashMap<>();
		shallowCopy.putAll(CSSURIS);
		return shallowCopy;
	}

	public static Map<String, String> getJSURIS(HttpServletRequest request, String root) {
		final Map<String, String> directoryUris = getDIRECTORYURIS(request, root, 1);
		List<File> jsFiles = FILE_MANAGER.getFiles(request, directoryUris.get("js"));
		for (File file : jsFiles) {
			String key = file.getName().substring(0, file.getName().indexOf('.'));
			JSURIS.put(key, file.getPath().substring(file.getPath().lastIndexOf(root)));
		}
		Map<String, String> shallowCopy = new HashMap<>();
		shallowCopy.putAll(JSURIS);
		return shallowCopy;
	}

	public static Map<String, String> getDIRECTORYURIS(HttpServletRequest request, String root, int depth) {
		List<String> directories = FILE_MANAGER.getDirectories(request, root, depth);
		//directories.forEach(d->{System.out.println(d);});
		for (String d : directories) {
			if (d != null && d.equals(root)) {
				//DIRECTORYURIS.put(root.substring(root.indexOf('\\') + 1), root);
			} else if (d != null) {
				DIRECTORYURIS.put(d.substring(d.indexOf('\\') + 1), d);
			}
		}
		//System.out.println(DIRECTORYURIS);
		Map<String, String> shallowCopy = new HashMap<>();
		shallowCopy.putAll(DIRECTORYURIS);
		return shallowCopy;
	}

}
