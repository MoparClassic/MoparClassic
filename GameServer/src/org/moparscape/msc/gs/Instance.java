package org.moparscape.msc.gs;

import org.moparscape.msc.gs.core.DelayedEventHandler;
import org.moparscape.msc.gs.db.DBConnection;
import org.moparscape.msc.gs.db.ReportHandlerQueries;
import org.moparscape.msc.gs.model.World;
import org.moparscape.msc.gs.plugins.dependencies.PluginHandler;
import org.moparscape.msc.irc.IRC;

/**
 * Holds instances to commonly used Objects.
 * 
 * @author xEnt
 * 
 */
public class Instance {

    public static IRC getIRC() {
    	return getServer().getIRC();
    }

    public static Server getServer() {
    	return World.getWorld().getServer();
    }

    public static World getWorld() {
    	return World.getWorld();
    }

    public static DelayedEventHandler getDelayedEventHandler() {
    	return getWorld().getDelayedEventHandler();
    }

    public static CacheHandler getCacheHandler() {
    	return CacheHandler.getCache();
    }

    public static PluginHandler getPluginHandler() {
    	return PluginHandler.getPluginHandler();
    }
    
    public static ReportHandlerQueries getReport() {
    	return DBConnection.getReport();
    }
}
