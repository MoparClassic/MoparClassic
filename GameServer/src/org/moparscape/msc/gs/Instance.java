package org.moparscape.msc.gs;

import org.moparscape.msc.config.Config;
import org.moparscape.msc.gs.core.DelayedEventHandler;
import org.moparscape.msc.gs.db.DBConnection;
import org.moparscape.msc.gs.db.ReportHandlerQueries;
import org.moparscape.msc.gs.model.World;
import org.moparscape.msc.gs.persistence.DataStore;
import org.moparscape.msc.gs.persistence.impl.DataStoreFactory;
import org.moparscape.msc.gs.plugins.dependencies.PluginHandler;

/**
 * Holds instances to commonly used Objects.
 * 
 * @author xEnt
 * 
 */
public class Instance {

	public static Server getServer() {
		return World.getWorld().getServer();
	}

	public static World getWorld() {
		return World.getWorld();
	}

	public static DelayedEventHandler getDelayedEventHandler() {
		return getWorld().getDelayedEventHandler();
	}

	public static PluginHandler getPluginHandler() {
		return PluginHandler.getPluginHandler();
	}

	public static ReportHandlerQueries getReport() {
		return DBConnection.getReport();
	}

	private static DataStore dataStore;

	private static void createDataStore() {
		try {
			dataStore = DataStoreFactory.create(Config.DATA_STORE);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Could not create DataStore - " + Config.DATA_STORE);
			System.exit(0);
		}
	}
	
	public static DataStore getDataStore() {
		if(dataStore == null) {
			createDataStore();
		}
		return dataStore;
	}
}
