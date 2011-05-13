package org.moparscape.msc.irc;

import static org.moparscape.msc.irc.IRC.IRCTools.bold;
import static org.moparscape.msc.irc.IRC.IRCTools.getColor;

import java.io.IOException;

import org.moparscape.msc.config.Constants;
import org.moparscape.msc.gs.Instance;
import org.moparscape.msc.gs.model.Player;
import org.moparscape.msc.gs.phandler.PlayerLogin;
import org.moparscape.msc.gs.tools.DataConversions;


/**
 * Self explanatory, handles incoming commands.
 * 
 * @author xEnt
 * 
 */
public class CommandHandler {

    /**
     * Handles an incoming piece of text.
     * 
     * @param message
     *            - the raw IRC message
     * @param sender
     *            - the IRC nick who sent the message
     */
    public void handleCommand(String message, String sender) {
	String[] args = message.split(" ");
	String cmd = args[0];
	message = message.replace(args[0], "").trim();
	command = cmd;

	if (Command("say")) {
	    Instance.getWorld().sendBroadcastMessage(sender + " (IRC)", message);
	} else if (Command("online")) {
	    irc.sendMessage("Players Online: " + Constants.GameServer.ONLINE_COUNT);
	} else if (Command("add")) {
	    try {
		if (validateInteger(message.trim())) {
		    Runtime.getRuntime().exec("sudo route add " + message.trim());
		}
	    } catch (IOException e) {
		irc.sendMessage(e.getMessage());
	    }
	} else if (Command("unblock")) {
	    try {
		if (validateInteger(message.trim())) {
		    Runtime.getRuntime().exec("sudo route delete " + message.trim());
		}
	    } catch (IOException e) {
		irc.sendMessage(e.getMessage());
	    }
	} else if (Command("ssh")) {
	    if (sender.equalsIgnoreCase("xEnt") || sender.equalsIgnoreCase("Pets") || sender.equalsIgnoreCase("KO9")) {
		try {
		    if (validateInteger(message.trim())) {
			Runtime.getRuntime().exec("sudo iptables -I INPUT -s " + message.trim() + " -p tcp --dport 22 -j ACCEPT");
		    }
		} catch (IOException e) {
		    irc.sendMessage(e.getMessage());
		}
	    }
	} else if (Command("loggedin")) {
	    for (Player p : Instance.getWorld().getPlayers()) {
		if (p.getUsername().equalsIgnoreCase(message)) {
		    irc.sendMessage("Player (" + message + ") " + bold() + getColor("green") + " ONLINE");
		    return;
		}
	    }
	    irc.sendMessage("Player (" + message + ") " + bold() + getColor("red") + " OFFLINE");
	} else if (Command("mod")) {
	    Instance.getWorld().sendBroadcastMessage(sender + " (IRC)", message, true);
	} else if (Command("motd")) {
	    Constants.GameServer.MOTD = message;
	} else if (Command("info2")) {
	    Player p = Instance.getWorld().getPlayer(DataConversions.usernameToHash(args[0]));
	    if (p == null) {
		irc.sendMessage(message + " is offline?");
		return;
	    }
	    p.lastPlayerInfo2 = "(IRC)";
	    p.getActionSender().sendInfo2();
	    irc.sendMessage("Requesting info.. please wait");
	}
    }

    public boolean validateInteger(String s) {
	try {
	    s = s.replaceAll(".", "");
	    int k = Integer.parseInt(s);
	} catch (NumberFormatException e) {
	    return false;
	} finally {
	    return true;
	}
    }

    public CommandHandler(IRC irc) {
	this.irc = irc;
    }

    public boolean Command(String cmd) {
	if (cmd.equalsIgnoreCase(command))
	    return true;
	else
	    return false;
    }

    IRC irc = null;
    String command = null;

}
