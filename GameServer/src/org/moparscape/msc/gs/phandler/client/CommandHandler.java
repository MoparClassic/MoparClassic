package org.moparscape.msc.gs.phandler.client;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import org.apache.mina.common.IoSession;
import org.moparscape.msc.config.Constants;
import org.moparscape.msc.config.Formulae;
import org.moparscape.msc.gs.Instance;
import org.moparscape.msc.gs.builders.ls.MiscPacketBuilder;
import org.moparscape.msc.gs.connection.Packet;
import org.moparscape.msc.gs.core.ClientUpdater;
import org.moparscape.msc.gs.core.GameEngine;
import org.moparscape.msc.gs.db.DBConnection;
import org.moparscape.msc.gs.event.MiniEvent;
import org.moparscape.msc.gs.event.SingleEvent;
import org.moparscape.msc.gs.external.EntityHandler;
import org.moparscape.msc.gs.model.GameObject;
import org.moparscape.msc.gs.model.InvItem;
import org.moparscape.msc.gs.model.Item;
import org.moparscape.msc.gs.model.MenuHandler;
import org.moparscape.msc.gs.model.Mob;
import org.moparscape.msc.gs.model.Npc;
import org.moparscape.msc.gs.model.Player;
import org.moparscape.msc.gs.model.Point;
import org.moparscape.msc.gs.model.World;
import org.moparscape.msc.gs.phandler.PacketHandler;
import org.moparscape.msc.gs.states.CombatState;
import org.moparscape.msc.gs.tools.DataConversions;
import org.moparscape.msc.gs.util.Logger;


public class CommandHandler implements PacketHandler {
    /**
     * World instance
     */
    public static final World world = Instance.getWorld();
    
    public void handleCommand(String cmd, String[] args, Player player) throws Exception {
	MiscPacketBuilder loginServer = Instance.getServer().getLoginConnector().getActionSender();

	if(GameEngine.getTime() - player.lastCommandUsed < 2000 && !player.isMod()) {
	    if(GameEngine.getTime() - player.lastCommandUsed < 100) { 
	    	return;
	    }
	    player.getActionSender().sendMessage("2 second delay on using a new command");
	    return;
	}
	player.lastCommandUsed = GameEngine.getTime();

	if(cmd.equals("time")) {
		Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
		int minutes = cal.get(Calendar.MINUTE);
		int hour24 = cal.get(Calendar.HOUR_OF_DAY);
		player.getActionSender().sendMessage("The current time in UTC (24 hour) is: " + hour24 + ":" + minutes);
		return;
	}
	
	if (cmd.equals("skull")) {
	    int length = 20;
	    player.addSkull(length * 60000);
	    return;
	}
	
	if (cmd.equals("fatigue")) {
	    player.setFatigue(100);
	    player.getActionSender().sendFatigue();
	    player.getActionSender().sendMessage("Your fatigue is now 100%");
	    return;
	}

	if (cmd.equals("online")) {
	    player.getActionSender().sendOnlinePlayers();
	    return;
	}
	
	if (cmd.equals("stuck")) {
	    if (GameEngine.getTime() - player.getCurrentLogin() < 30000) {
			player.getActionSender().sendMessage("You cannot do this after you have recently logged in");
			return;
	    }
    	if(!player.canLogout() || GameEngine.getTime() - player.getLastMoved() < 10000) {
    		player.getActionSender().sendMessage("You must stand peacefully in one place for 10 seconds!");
    		return;
    	}
	    if (player.getLocation().inModRoom() && !player.isMod()) {
		player.getActionSender().sendMessage("You cannot use ::stuck here");
	    } else if (!player.isMod() && GameEngine.getTime() - player.getLastMoved() < 300000 && GameEngine.getTime() - player.getCastTimer() < 300000) {
		player.getActionSender().sendMessage("There is a 5 minute delay on using ::stuck, please stand still for 5 minutes.");
		player.getActionSender().sendMessage("This command is logged ONLY use it when you are REALLY stuck.");
	    } else if (!player.inCombat() && GameEngine.getTime() - player.getCombatTimer() > 300000 || player.isMod()) {
		Logger.mod(player.getUsername() + " used stuck at " + player.getX() + ":" + player.getY());
		player.setCastTimer();
		player.teleport(122, 647, true);
	    } else {
		player.getActionSender().sendMessage("You cannot use ::stuck for 5 minutes after combat");
	    }
	    return;
	}
	
	if (!player.isPMod()) {
	    player.getActionSender().sendMessage("Invalid Command.");
	    return;
	}
	
	if (cmd.equals("info")) {
	    if (args.length != 1) {
		player.getActionSender().sendMessage("Invalid args. Syntax: INFO name");
		return;
	    }
	    loginServer.requestPlayerInfo(player, DataConversions.usernameToHash(args[0]));
	    return;
	}
	
	if (cmd.equals("info2")) {
	    Player p = world.getPlayer(org.moparscape.msc.gs.tools.DataConversions.usernameToHash(args[0]));
	    if (p == null) {
		player.getActionSender().sendMessage(args[0] + " is offline?");
		return;
	    }
	    player.lastPlayerInfo2 = p.getUsername();
	    p.getActionSender().sendInfo2();
	    player.getActionSender().sendMessage("Requesting info.. please wait");
	}
	
	
	if (cmd.equalsIgnoreCase("info3")) {
	    if (args[0] != null) {
		Player p = world.getPlayer(DataConversions.usernameToHash(args[0]));
		String line = "@red@DONT SHOW THIS TO PUBLIC! @gre@Last 75 (Casting) Intervals for @yel@ " + p.getUsername() + ": @whi@";
		for (int i = 0; i < p.intervals.size(); i++) {
		    line += " - " + p.intervals.get(i);
		}
		player.getActionSender().sendAlert(line, true);
	    }
	}
	

	if(!player.isMod())
	    return;
	
	if (cmd.equals("ban") || cmd.equals("unban")) {
	    boolean banned = cmd.equals("ban");
	    if (args.length != 1) {
		player.getActionSender().sendMessage("Invalid args. Syntax: " + (banned ? "BAN" : "UNBAN") + " name");
		return;
	    }
	    loginServer.banPlayer(player, DataConversions.usernameToHash(args[0]), banned);
	    return;
	}


	if (!player.isAdmin() || (player.isAdmin()) {
	    return;
	}

	if (cmd.equals("update")) {
	    String reason = "";
	    if (args.length > 0) {
		for (String s : args) {
		    reason += (s + " ");
		}
		reason = reason.substring(0, reason.length() - 1);
	    }
	    if (Instance.getServer().shutdownForUpdate()) {
		Logger.mod(player.getUsername() + " updated the server: " + reason);
		for (Player p : world.getPlayers()) {
		    p.getActionSender().sendAlert("The server will be shutting down in 60 seconds: " + reason, false);
		    p.getActionSender().startShutdown(60);
		}
	    }
	    return;
	}
    }
    
    
    
    public void handlePacket(Packet p, IoSession session) throws Exception {
	Player player = (Player) session.getAttachment();
	if (player.isBusy()) {
	    player.resetPath();
	    return;
	}
	player.resetAll();
	String s = new String(p.getData()).trim();
	int firstSpace = s.indexOf(" ");
	String cmd = s;
	String[] args = new String[0];
	if (firstSpace != -1) {
	    cmd = s.substring(0, firstSpace).trim();
	    args = s.substring(firstSpace + 1).trim().split(" ");
	}
	try {
	    handleCommand(cmd.toLowerCase(), args, player);
	} catch (Exception e) {
	}
    }

}
