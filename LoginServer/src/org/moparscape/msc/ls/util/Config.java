package org.moparscape.msc.ls.util;

/**
 * A class to handle loading configuration from XML
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Config {
	public static String STORAGE_MEDIUM;

	public static int LS_PORT, QUERY_PORT;

	public static String RSCDLS_HOME, CONF_DIR, LOG_DIR, MYSQL_HOST, MYSQL_DB,
			MYSQL_USER, MYSQL_PASS, LS_IP, QUERY_IP, AUTH_META_DATA;

	public static long START_TIME;

	public static String AUTH_CLASS;

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

		MYSQL_HOST = props.getProperty("mysqlhost");
		MYSQL_DB = props.getProperty("mysqldb");
		MYSQL_USER = props.getProperty("mysqluser");
		MYSQL_PASS = props.getProperty("mysqlpass");

		LS_IP = props.getProperty("lsip");
		LS_PORT = Integer.parseInt(props.getProperty("lsport"));
		QUERY_IP = props.getProperty("queryip");
		QUERY_PORT = Integer.parseInt(props.getProperty("queryport"));
		STORAGE_MEDIUM = props.getProperty("storage-medium",
				"org.moparscape.msc.ls.persistence.impl.MySQL");
		AUTH_CLASS = props.getProperty("auth-class", "org.moparscape.msc.ls.auth.impl.WebsiteAuth");
		AUTH_META_DATA = props.getProperty("auth-meta-data",
				"https://www.moparscape.org/auth.php?field=");

		props.clear();
	}

	/**
	 * Called to load RSCDLS_HOME and CONF_DIR Used to be situated in
	 * PersistenceManager
	 */
	private static void loadEnv() {
		String home = System.getenv("RSCDLS_HOME");
		if (home == null) { // the env var hasnt been set, fall back to .
			home = ".";
		}
		CONF_DIR = home + File.separator + "conf";
		LOG_DIR = home + File.separator + "logs";
		RSCDLS_HOME = home;
	}

	public static final String[] statArray = { "attack", "defense", "strength",
			"hits", "ranged", "prayer", "magic", "cooking", "woodcut",
			"fletching", "fishing", "firemaking", "crafting", "smithing",
			"mining", "herblaw", "agility", "thieving" };
}
