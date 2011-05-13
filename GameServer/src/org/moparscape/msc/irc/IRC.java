package org.moparscape.msc.irc;

import java.io.IOException;

import org.jibble.pircbot.IrcException;
import org.jibble.pircbot.NickAlreadyInUseException;
import org.jibble.pircbot.PircBot;
import org.moparscape.msc.config.Constants;
import org.moparscape.msc.gs.util.Logger;


/**
 * GameServer <> IRC Integration. Using PircBot v1.46 IRC Framework.
 * 
 * @author xEnt
 * 
 */
public class IRC extends PircBot implements Runnable {

    /**
     * Thread entry point, also IRC initialization process.
     */
    public void run() {

	Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
	Thread.currentThread().setName("IRC");
	handler = new CommandHandler(this);

	try {
	    // setVerbose(true);
	    setName(Constants.IRC.NICK);
	    setLogin(Constants.IRC.USER);
	    for (String server : Constants.IRC.HOSTS)
		connect(server);

	    for (int i = 0; i < Constants.IRC.CHANNELS.length; i++) {
		if (Constants.IRC.CHANNELS[i][1] != null) { // has password
		    joinChannel(Constants.IRC.CHANNELS[i][0], Constants.IRC.CHANNELS[i][1]);
		    continue;
		}
		joinChannel(Constants.IRC.CHANNELS[i][0]);
	    }

	} catch (NickAlreadyInUseException e) {
	    changeNick(Constants.IRC.NICK + "A");
	} catch (IOException e) {
	    Logger.error(e);
	} catch (IrcException e) {
	    Logger.error(e);
	} finally {
	    Logger.print("IRC Connected. [" + Constants.IRC.NICK + "]");
	}
    }

    /**
     * When someone uses Report Abuse ingame, this relays it to IRC.
     * 
     * @param sender
     *            - the person complaining
     * @param target
     *            - the bad person
     * @param id
     *            - the report ID
     */
    public void handleReport(String sender, String target, int id) {
	sendMessage(IRCTools.bold() + IRCTools.getColor("red") + sender + IRCTools.bold() + IRCTools.getColor("lightgreen") + " has reported " + IRCTools.bold() + IRCTools.getColor("red") + target + IRCTools.bold() + IRCTools.getColor("lightgreen") + " for " + IRCTools.bold() + IRCTools.getColor("blue") + IRCTools.getReportName(id));
    }

    /**
     * This will get sent to each "Administrator" that accepts PMs for Moderator
     * logs.
     * 
     * @param user
     *            - (Optional) the user getting logged
     * @param message
     *            - the message
     */
    public void notifyAdmin(String user, String message) {
	if (Constants.IRC.USE_IRC) {
	    message = Filter.translate(message);
	    for (String admin : Constants.IRC.ADMINISTRATORS) {
		sendMessage(admin, IRCTools.bold() + IRCTools.getColor("blue") + user + IRCTools.bold() + IRCTools.getColor("lightgreen") + " " + message);
	    }
	}
    }

    /**
     * Fired off upon a message recieved by our IRC Channel
     */
    @Override
    public void onMessage(String channel, String sender, String login, String hostname, String message) {
	for (int i = 0; i < Constants.IRC.CHANNELS.length; i++) {
	    if (Constants.IRC.CHANNELS[i][0].equalsIgnoreCase(channel)) {
		if (message.startsWith("!")) {
		    handler.handleCommand(message.substring(1), sender);
		}
		break;
	    }
	}
    }

    /**
     * Let's send a message to all IRC channels, and check for filtered words.
     * 
     * @param message
     */
    public void sendMessage(String message) {
	if (Constants.IRC.USE_IRC) {
	    message = Filter.translate(message);
	    for (int i = 0; i < Constants.IRC.CHANNELS.length; i++) {
		sendMessage(Constants.IRC.CHANNELS[i][0], message);
	    }
	}
    }

    /**
     * Instance to our IRC command handler.
     */
    private CommandHandler handler;

    public CommandHandler getHandler() {
	return handler;
    }

    public void setHandler(CommandHandler handler) {
	this.handler = handler;
    }

    /**
     * Misc IRC dependencies
     */
    public static class IRCTools {

	/**
	 * Starts/stops bold in a IRC string.
	 * 
	 * @return
	 */
	public static String bold() {
	    return "";
	}

	/**
	 * @return the correct IRC color-code.
	 */
	public static String getColor(String s) {

	    String[] colorCode = { "2", "8", "9", "10", "6", "1", "3", "7" };
	    String[] colors = { "red", "yellow", "lightgreen", "blue", "purple", "white", "darkgreen", "orange" };
	    for (int i = 0; i < colors.length; i++) {
		if (colors[i].equalsIgnoreCase(s))
		    return "" + colorCode[i];
	    }
	    return null;
	}

	/**
	 * Returns the ID > String of the Report detail list.
	 * 
	 * @param id
	 *            - the report id
	 * @return the report name (String)
	 */
	public static String getReportName(int id) {
	    id--;
	    String[] reports = { "Offensive Language", "Item Scamming", "Password Scamming", "Bug Abuse", "Staff Impersonation", "Account sharing/trading", "Autoing/Macroing", "Multiple logging in", "Encouraging others to break rules", "Misuse of customer support", "Advertising / Website", "Real world item trading" };
	    if (id < 0 || id > reports.length)
		return "ERROR?";
	    return reports[id];
	}
    }

    /**
     * Filters anything relaying to IRC
     */
    public static class Filter {
	/**
	 * Let's swap the filtered words with their given masks.
	 * 
	 * @param old
	 *            - the old String.
	 * @return - the newly filtered string.
	 */
	public static String translate(String old) {
	    String newStr = old;
	    for (int i = 0; i < Constants.IRC.BANNED_WORDS.length; i++) {
		if (newStr.contains(Constants.IRC.BANNED_WORDS[i][0])) {
		    newStr = newStr.replace(Constants.IRC.BANNED_WORDS[i][0], Constants.IRC.BANNED_WORDS[i][1]);
		}
	    }
	    return newStr;
	}
    }
}
