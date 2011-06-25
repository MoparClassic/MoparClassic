package org.moparscape.msc.config;

/**
 * A class to handle loading configuration from XML
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Config {
	/**
	 * User info for the database
	 */
	public static String MYSQL_HOST;
	public static String MYSQL_DB;
	public static String MYSQL_USER;
	public static String MYSQL_PASS;

	public static String SERVER_IP, SERVER_NAME, RSCD_HOME, CONF_DIR,
			SERVER_LOCATION, LS_IP;

	public static int SERVER_PORT, SERVER_VERSION, MAX_PLAYERS, LS_PORT,
			SERVER_NUM;

	public static long START_TIME;

	public static boolean members, f2pWildy;

	public static double expRate, subExpRate;

	public static String[] pmods, mods, admins;
	public static int IP_BAN_REMOVAL_DELAY;
	public static int GARBAGE_COLLECT_INTERVAL;
	public static int SAVE_INTERVAL;
	public static String DATE_FORMAT;
	public static String BLOCK_COMMAND;
	public static String UNBLOCK_COMMAND;
	public static String ALERT_CONFIG;
	public static String COMMAND_CONFIG;

	static {
		loadEnv();
	}

	/**
	 * Called to load config settings from the given file
	 * 
	 * @param file
	 *            the xml file to load settings from
	 * @throws IOException
	 *             if an i/o error occurs
	 */
	public static void initConfig(String file) throws IOException {
		START_TIME = System.currentTimeMillis();

		Properties props = new Properties();
		props.loadFromXML(new FileInputStream(file));

		SERVER_VERSION = Integer.parseInt(props.getProperty("version"));
		SERVER_NAME = props.getProperty("name");
		SERVER_IP = props.getProperty("ip");
		SERVER_PORT = Integer.parseInt(props.getProperty("port"));
		SERVER_LOCATION = props.getProperty("location");

		MYSQL_USER = props.getProperty("mysqluser");
		MYSQL_PASS = props.getProperty("mysqlpass");
		MYSQL_HOST = props.getProperty("mysqlhost");
		MYSQL_DB = props.getProperty("mysqldb");

		MAX_PLAYERS = Integer.parseInt(props.getProperty("maxplayers"));

		LS_IP = props.getProperty("lsip");
		LS_PORT = Integer.parseInt(props.getProperty("lsport"));
		SERVER_NUM = Integer.parseInt(props.getProperty("servernum"));

		members = Boolean.parseBoolean(props.getProperty("members", "false"));
		f2pWildy = Boolean.parseBoolean(props.getProperty("f2pWildy", "true"));
		expRate = Double.parseDouble(props.getProperty("expRate"));
		subExpRate = Double.parseDouble(props.getProperty("subExpRate"));

		pmods = props.getProperty("pmods").replaceAll(", +", ",").split(",");
		mods = props.getProperty("mods").replaceAll(", +", ",").split(",");
		admins = props.getProperty("admins").replaceAll(", +", ",").split(",");

		IP_BAN_REMOVAL_DELAY = Integer.parseInt(props
				.getProperty("ip-ban-removal-delay"));
		BLOCK_COMMAND = props.getProperty("block-command");
		UNBLOCK_COMMAND = props.getProperty("unblock-command");

		GARBAGE_COLLECT_INTERVAL = Integer.parseInt(props
				.getProperty("garbage-collect-interval"));
		SAVE_INTERVAL = Integer.parseInt(props.getProperty("save-interval"));

		DATE_FORMAT = props.getProperty("date-format");

		ALERT_CONFIG = props.getProperty("alert-config");
		COMMAND_CONFIG = props.getProperty("command-config");

		props.clear();

		Constants.GameServer.MOTD = "@yel@Welcome to @whi@"
				+ Config.SERVER_NAME + "@yel@ - World @whi@"
				+ (Config.SERVER_NUM == 0 ? 2 : Config.SERVER_NUM) + " ("
				+ (Config.members ? "P2P" : "F2P") + ")";
	}

	/**
	 * Called to load RSCD_HOME and CONF_DIR Used to be situated in
	 * PersistenceManager
	 */
	private static void loadEnv() {
		String home = System.getenv("RSCD_HOME");
		if (home == null) { // the env var hasnt been set, fall back to .
			home = ".";
		}
		CONF_DIR = home + File.separator + "conf";
		RSCD_HOME = home;
	}
}
