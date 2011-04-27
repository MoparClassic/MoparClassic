package msc.gs.builders.ls;

import msc.gs.Instance;
import msc.gs.builders.LSPacketBuilder;
import msc.gs.connection.LSPacket;
import msc.gs.model.Player;
import msc.gs.model.World;
import msc.gs.util.EntityList;

public class PlayerListRequestPacketBuilder {
	/**
	 * World instance
	 */
	public static final World world = Instance.getWorld();
	/**
	 * Packets uID
	 */
	private long uID;

	public LSPacket getPacket() {
		EntityList<Player> players = world.getPlayers();

		LSPacketBuilder packet = new LSPacketBuilder();
		packet.setUID(uID);
		packet.addInt(players.size());
		for (Player p : players) {
			packet.addLong(p.getUsernameHash());
			packet.addShort(p.getX());
			packet.addShort(p.getY());
		}
		return packet.toPacket();
	}

	/**
	 * Sets the packet to reply to
	 */
	public void setUID(long uID) {
		this.uID = uID;
	}
}
