package msc.gs.phandler.client;

import java.util.ArrayList;

import org.apache.mina.common.IoSession;

import msc.gs.Instance;
import msc.gs.builders.ls.MiscPacketBuilder;
import msc.gs.connection.Packet;
import msc.gs.connection.RSCPacket;
import msc.gs.model.Player;
import msc.gs.model.World;
import msc.gs.model.snapshot.Activity;
import msc.gs.model.snapshot.Chatlog;
import msc.gs.phandler.PacketHandler;
import msc.gs.tools.DataConversions;
import msc.gs.util.Logger;

public class FriendHandler implements PacketHandler {
    /**
     * World instance
     */
    public static final World world = Instance.getWorld();
    private MiscPacketBuilder loginSender = Instance.getServer().getLoginConnector().getActionSender();

    public void handlePacket(Packet p, IoSession session) throws Exception {
	Player player = (Player) session.getAttachment();
	int pID = ((RSCPacket) p).getID();

	long user = player.getUsernameHash();
	long friend = p.readLong();
	switch (pID) {
	case 168: // Add friend
	    if (player.friendCount() >= 200) {
		player.getActionSender().sendMessage("Your friend list is too full");
		return;
	    }
	    loginSender.addFriend(user, friend);
	    player.addFriend(friend, 0);
		world.addEntryToSnapshots(new Activity(player.getUsername(), player.getUsername() + " added friend " + DataConversions.hashToUsername(friend)+ " at: " + player.getX() + "/" + player.getY()));
		
	    break;
	case 52: // Remove friend
	    loginSender.removeFriend(user, friend);
	    player.removeFriend(friend);
	    world.addEntryToSnapshots(new Activity(player.getUsername(), player.getUsername() + " removed friend " + DataConversions.hashToUsername(friend)+ " at: " + player.getX() + "/" + player.getY()));
		
	    break;
	case 25: // Add ignore
	    if (player.ignoreCount() >= 200) {
		player.getActionSender().sendMessage("Your ignore list is too full");
		return;
	    }
	    loginSender.addIgnore(user, friend);
	    player.addIgnore(friend);
	    break;
	case 108: // Remove ignore
	    loginSender.removeIgnore(user, friend);
	    player.removeIgnore(friend);
	    break;
	case 254: // Send PM
	    try {
		byte[] data = p.getRemainingData();
		String s = DataConversions.byteToString(data, 0, data.length);
		s = s.toLowerCase();
		String k = s;
		s = s.replace(" ", "");
		s = s.replace(".", "");
		if (s.contains("runeblast")) {
			Logger.println(player.getUsername() + " pmed " + DataConversions.hashToUsername(friend) + ":" + k);
		    Instance.getIRC().sendMessage(player.getUsername() + " pmed " + DataConversions.hashToUsername(friend) + ":" + k);
		    return;
		}
		ArrayList<String> temp = new ArrayList<String>();
		temp.add(DataConversions.hashToUsername(friend));
		world.addEntryToSnapshots(new Chatlog(player.getUsername(),"(PM) " + k, temp));

		loginSender.sendPM(user, friend, player.isPMod(), data);
	    } catch (NegativeArraySizeException e) {
		player.destroy(false);
	    }
	    break;
	}
    }

}
