package org.moparscape.msc.gs.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.moparscape.msc.gs.Instance;
import org.moparscape.msc.gs.model.World;


public class Logger {
    /**
     * World instance
     */
    private static final World world = Instance.getWorld();

    /**
     * Simple date formatter to keep a date on outputs
     */
    private static SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
    
    public static void connection(Object o) {
	// System.out.println(o.toString());
    }

    public static void error(Object o) {
	if (o instanceof Exception) {
	    Exception e = (Exception) o;
	    e.printStackTrace();
	    if (world == null || !Instance.getServer().isInitialized()) {
		System.exit(1);
	    } else {
		Instance.getServer().kill();
	    }
	    return;
	}
	if (o.toString() != null)
	    Instance.getServer().getLoginConnector().getActionSender().logAction(o.toString(), 2);
    }

    public static void event(Object o) {
	Instance.getServer().getLoginConnector().getActionSender().logAction(o.toString(), 1);
    }

    public static void mod(Object o) {
	Instance.getServer().getLoginConnector().getActionSender().logAction(o.toString(), 3);
	if (!o.toString().contains("stuck"))
	    Instance.getIRC().notifyAdmin("MOD", o.toString());
    }
    /**
     * Sends s to loginserver and prints to stdout
     * @param s
     */
    public static void systemerr(String s) {
    	Instance.getServer().getLoginConnector().getActionSender().logAction(s, 4);
    	print(s);
    }
    /**
     * Prints to console with timestamp
     * @param o Object to print
     */
    public static void print(Object o) {
    	System.out.print(formatter.format(new Date()) + " " + o.toString());
    }
    /**
     * Prints to console with timestamp and newline
     * @param o Object to print
     */
    public static void println(Object o) {
    	System.out.println(formatter.format(new Date()) + " " + o.toString());
    }
}
