package org.moparscape.msc.gs.db.impl;

import java.lang.reflect.Constructor;

import org.moparscape.msc.gs.db.DataService;
import org.moparscape.msc.gs.db.ReportHandler;
import org.moparscape.msc.gs.util.JarUtil;
import org.moparscape.msc.gs.util.ModuleUtil;

public class DataServiceFactory {

	public static DataService createDataRequestService(String className)
			throws Exception {
		try {
			return Class.forName(className).asSubclass(DataService.class)
					.newInstance();
		} catch (Exception e) {
			Class<?> clss = JarUtil.loadClassFromJar(
					ModuleUtil.getClassLoader(), className,
					ModuleUtil.moduleFolder.getPath());
			Class<? extends DataService> c = clss.asSubclass(DataService.class);
			Constructor<? extends DataService> con = c.getDeclaredConstructor();
			con.setAccessible(true);
			return con.newInstance(new Object[0]);
		}
	}

	public static ReportHandler createReportHandler(String className)
			throws Exception {
		try {
			return Class.forName(className).asSubclass(ReportHandler.class)
					.newInstance();
		} catch (Exception e) {
			Class<?> clss = JarUtil.loadClassFromJar(
					ModuleUtil.getClassLoader(), className,
					ModuleUtil.moduleFolder.getPath());
			Class<? extends ReportHandler> c = clss
					.asSubclass(ReportHandler.class);
			Constructor<? extends ReportHandler> con = c
					.getDeclaredConstructor();
			con.setAccessible(true);
			return con.newInstance(new Object[0]);
		}
	}
}
