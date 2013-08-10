package org.moparscape.msc.gs.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class JarUtil {
	/**
	 * Just gets all the classes inside the a folder, leeched from Java forums.
	 * 
	 * @param pckgname
	 * @param baseDirPath
	 * @return
	 * @throws ClassNotFoundException
	 */
	public static Class<?>[] getClassesFromFileJarFile(String pckgname,
			String baseDirPath) throws ClassNotFoundException {
		ArrayList<Class<?>> classes = new ArrayList<Class<?>>();
		String path = pckgname.replace('.', '/') + "/";
		File mF = new File(baseDirPath);
		String[] files = mF.list();
		ArrayList<String> jars = new ArrayList<String>();
		for (int i = 0; i < files.length; i++)
			if (files[i].endsWith(".jar"))
				jars.add(files[i]);

		for (int i = 0; i < jars.size(); i++) {
			try (JarFile currentFile = new JarFile(jars.get(i).toString())) {
				for (Enumeration<?> e = currentFile.entries(); e
						.hasMoreElements();) {
					JarEntry current = (JarEntry) e.nextElement();
					if (current.getName().contains("$"))
						continue;
					if (current.getName().length() > path.length()
							&& current.getName().substring(0, path.length())
									.equals(path)
							&& current.getName().endsWith(".class"))
						classes.add(Class.forName(current.getName()
								.replaceAll("/", ".").replace(".class", "")));
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		Class<?>[] classesA = new Class<?>[classes.size()];
		classes.toArray(classesA);
		return classesA;
	}

	public static Class<?> loadClassFromJar(ClassLoader classLoader,
			String className, String baseDirPath) throws Exception {
		File mF = new File(baseDirPath);
		String[] files = mF.list();
		List<String> jars = new ArrayList<String>();
		for (int i = 0; i < files.length; i++)
			if (files[i].endsWith(".jar"))
				jars.add(files[i]);

		for (int i = 0; i < jars.size(); i++) {
			try (JarFile currentFile = new JarFile(baseDirPath + File.separator
					+ jars.get(i))) {

				for (Enumeration<?> e = currentFile.entries(); e
						.hasMoreElements();) {
					JarEntry current = (JarEntry) e.nextElement();
					if (current.getName().contains("$"))
						continue;
					if (className.equals(current.getName().replaceAll("/", ".")
							.replace(".class", ""))) {
						return classLoader.loadClass(current.getName()
								.replaceAll("/", ".").replace(".class", ""));
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
}
