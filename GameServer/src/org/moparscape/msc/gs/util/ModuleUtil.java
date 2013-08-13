package org.moparscape.msc.gs.util;

import java.io.File;
import java.io.FilenameFilter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

public class ModuleUtil {
	public static final File moduleFolder = new File(
			System.getProperty("user.dir") + File.separator + "module");

	private static final ClassLoader classLoader = createClassLoader();

	private static ClassLoader createClassLoader() {
		try {
			String[] files = moduleFolder.list(new FilenameFilter() {

				@Override
				public boolean accept(File arg0, String arg1) {
					return arg1.endsWith(".jar");
				}

			});

			URL[] urls = new URL[files.length];

			for (int i = 0; i < urls.length; i++) {
				urls[i] = new File(moduleFolder, files[i]).toURI().toURL();
			}

			return URLClassLoader.newInstance(urls);
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static ClassLoader getClassLoader() {
		return classLoader;
	}
}
