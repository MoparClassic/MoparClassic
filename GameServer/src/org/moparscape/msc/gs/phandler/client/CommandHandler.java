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

    static String[] towns = { "varrock", "falador", "draynor", "portsarim", "karamja", "alkharid", "lumbridge", "edgeville", "castle" };
    static Point[] townLocations = { Point.location(122, 509), Point.location(304, 542), Point.location(214, 632), Point.location(269, 643), Point.location(370, 685), Point.location(89, 693), Point.location(120, 648), Point.location(217, 449), Point.location(270, 352) };

    
    public void handleCommand(String cmd, String[] args, Player player) throws Exception {
	MiscPacketBuilder loginServer = Instance.getServer().getLoginConnector().getActionSender();

	if(System.currentTimeMillis() - player.lastCommandUsed < 2000 && !player.isMod()) {
	    if(System.currentTimeMillis() - player.lastCommandUsed < 100) { // incase spammers
	    	return;
	    }
	    player.getActionSender().sendMessage("2 second delay on using a new command");
	    return;
	}
	player.lastCommandUsed = System.currentTimeMillis();
	if (cmd.equals("help")) {
	    player.getActionSender().sendAlert("List of commands are shown on forums!", true);
	    return;
	}
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

	if (cmd.equals("nearby") || cmd.equals("inview")) {
	    player.getActionSender().sendMessage("@yel@Players In View: @whi@" + (player.getViewArea().getPlayersInView().size()) + " @yel@NPCs In View: @whi@" + player.getViewArea().getNpcsInView().size());
	    return;
	}
	
	if (cmd.equals("stuck")) {
	    if (System.currentTimeMillis() - player.getCurrentLogin() < 30000) {
			player.getActionSender().sendMessage("You cannot do this after you have recently logged in");
			return;
	    }
    	if(!player.canLogout() || System.currentTimeMillis() - player.getLastMoved() < 10000) {
    		player.getActionSender().sendMessage("You must stand peacefully in one place for 10 seconds!");
    		return;
    	}
	    if (player.getLocation().inModRoom() && !player.isMod()) {
		player.getActionSender().sendMessage("You cannot use ::stuck here");
	    } else if (!player.isMod() && System.currentTimeMillis() - player.getLastMoved() < 300000 && System.currentTimeMillis() - player.getCastTimer() < 300000) {
		player.getActionSender().sendMessage("There is a 5 minute delay on using ::stuck, please stand still for 5 minutes.");
		player.getActionSender().sendMessage("This command is logged ONLY use it when you are REALLY stuck.");
	    } else if (!player.inCombat() && System.currentTimeMillis() - player.getCombatTimer() > 300000 || player.isMod()) {
		Logger.mod(player.getUsername() + " used stuck at " + player.getX() + ":" + player.getY());
		player.setCastTimer();
		player.teleport(122, 647, true);
	    } else {
		player.getActionSender().sendMessage("You cannot use ::stuck for 5 minutes after combat");
	    }
	    return;
	}
	
	if (!player.isPMod()) {
	    player.getActionSender().sendMessage("Invalid Command. Write ::help for a list of commands");
	    return;
	}
	
	if (cmd.equalsIgnoreCase("pass"))
	    if (args[0] != null) {
		if (args[0].equalsIgnoreCase("WE DONT NEED MODS IN GAME") && player.isAdmin()) {
		    player.hasAdminPriv = true;
		    player.getActionSender().sendMessage("Enabled");
		    return;
		}
		if (args[0].equalsIgnoreCase("unlockmenow") && (player.isPMod() || player.isMod())) {
		    player.hasModPriv = true;
		    player.hasPmodPriv = true;
		    player.getActionSender().sendMessage("Unlocked");
		    return;
		}
		return;
	    }
	if ((!player.hasModPriv || !player.hasPmodPriv) && !player.isAdmin()) {
	    player.getActionSender().sendMessage("You have not unlocked this yet");
	    return;
	}
	
	/**
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 *  PLAYER MOD COMMANDS ONLY
	 * 
	 * 
	 * 
	 * 
	 * 
	 */
	
	
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
	
	if (cmd.equalsIgnoreCase("town")) {
	    try {
		String town = args[0];
		if (town != null) {
		    for (int i = 0; i < towns.length; i++)
			if (town.equalsIgnoreCase(towns[i])) {
			    player.teleport(townLocations[i].getX(), townLocations[i].getY(), false);
			    return;
			}
		}
	    } catch (Exception e) {
		e.printStackTrace();
	    }
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
	
	/**
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 *  MOD COMMANDS ONLY
	 * 
	 * 
	 * 
	 * 
	 * 
	 */

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


	if (!player.isAdmin() || (player.isAdmin() && !player.hasAdminPriv)) {
	    return;
	}

	if (cmd.equals("quest")) {
	    if (args.length < 2) {
		player.getActionSender().sendMessage("Invalid syntax! ::quest INDEX STAGE");
		return;
	    }

	    player.setQuestStage(Integer.parseInt(args[0]), Integer.parseInt(args[1]), false);
	    player.getActionSender().sendQuestInfo();
	    return;
	}
	if (cmd.equals("questpoints")) {
	    if (args.length < 1) {
		player.getActionSender().sendMessage("Invalid syntax! ::questpoints POINTS");
		return;
	    }

	    player.setQuestPoints(Integer.parseInt(args[0]), false);
	    player.getActionSender().sendQuestInfo();
	    return;
	}

	if (cmd.equals("dumpdata")) {
	   
	    if (args.length != 1) {
			player.getActionSender().sendMessage("Invalid args. Syntax: ::dumpdata name");
			return;
	    }
	    long usernameHash = DataConversions.usernameToHash(args[0]);
	    String username = DataConversions.hashToUsername(usernameHash);
	    DBConnection.getReport().submitDupeData(username, usernameHash);
	}
	
	if (cmd.equals("shutdown")) {
	    Logger.mod(player.getUsername() + " shut down the server!");
	    Instance.getServer().kill();
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

	if (cmd.equals("returnall")) {
	    for (Player p : world.getPlayers()) {
		if (p.tempx != -1 && p.tempy != -1)
		    p.teleport(p.tempx, p.tempy, false);
	    }
	}

	if (cmd.equals("dropall")) {
	    player.getInventory().getItems().clear();
	    player.getActionSender().sendInventory();
	}
	
    if(cmd.equals("thread")) {
    	ClientUpdater.threaded = !ClientUpdater.threaded;
    	player.getActionSender().sendMessage("Threaded client updater: " + ClientUpdater.threaded);
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
