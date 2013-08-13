package org.moparscape.msc.gs.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.moparscape.msc.gs.Instance;
import org.moparscape.msc.gs.config.Config;
import org.moparscape.msc.gs.model.World;

public class Logger {
	/**
	 * World instance
	 */
	private static final World getWorld() {
		return Instance.getWorld();
	}

	private static SimpleDateFormat formatter;

	/**
	 * Simple date formatter to keep a date on outputs
	 */
	private static SimpleDateFormat getFormatter() {
		try {
			if (formatter == null) {
				formatter = new SimpleDateFormat(Config.DATE_FORMAT);
			}
			return formatter;
		} catch (Exception e) {
			return new SimpleDateFormat("[yyyy.MM.dd HH:mm:ss]");
		}
	}

	public static void connection(Object o) {
		// Logging.debug(o.toString());
	}

	public static void error(Object o, boolean terminate) {
		if (o instanceof Exception) {
			Exception e = (Exception) o;
			e.printStackTrace();
			Instance.loggingService().tell(e, null);
			if (terminate) {
				if (getWorld() == null || !Instance.getServer().isInitialized()) {
					System.exit(1);
				} else {
					Instance.getServer().kill();
				}
			}
		}
	}

	public static void event(Object o) {
		Instance.getServer().getLoginConnector().getActionSender()
				.logAction(o.toString(), 1);
	}

	public static void mod(Object o) {
		Instance.getServer().getLoginConnector().getActionSender()
				.logAction(o.toString(), 3);
	}

	/**
	 * Sends s to loginserver and prints to stdout
	 * 
	 * @param s
	 */
	public static void systemerr(String s) {
		Instance.getServer().getLoginConnector().getActionSender()
				.logAction(s, 4);
		print(s);
	}

	/**
	 * Prints to console with timestamp
	 * 
	 * @param o
	 *            Object to print
	 */
	public static void print(Object o) {
		Instance.loggingService().tell(
				getFormatter().format(new Date()) + " " + o.toString(), null);
	}

	/**
	 * Prints to console with timestamp and newline
	 * 
	 * @param o
	 *            Object to print
	 */
	public static void println(Object o) {
		Instance.loggingService().tell(
				getFormatter().format(new Date()) + " " + o.toString(), null);
	}

	public static void error(Object e) {
		error(e, true);
	}
}
