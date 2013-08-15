package org.moparscape.msc.gs.builders.ls;

import org.moparscape.msc.gs.Instance;
import org.moparscape.msc.gs.config.Config;
import org.moparscape.msc.gs.connection.LSPacket;
import org.moparscape.msc.gs.model.World;

public class StatRequestPacketBuilder {
	/**
	 * World instance
	 */
	public static final World world = Instance.getWorld();
	/**
	 * Packets uID
	 */
	private long uID;

	public LSPacket getPacket() {
		LSPacketBuilder packet = new LSPacketBuilder();
		packet.setUID(uID);
		packet.addInt(world.countPlayers());
		packet.addInt(world.countNpcs());
		packet.addLong(Config.START_TIME);
		return packet.toPacket();
	}

	/**
	 * Sets the packet to reply to
	 */
	public void setUID(long uID) {
		this.uID = uID;
	}
}
