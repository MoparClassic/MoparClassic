package org.moparscape.msc.ls.packetbuilder.loginserver;

import org.moparscape.msc.ls.net.LSPacket;
import org.moparscape.msc.ls.packetbuilder.LSPacketBuilder;

public class WorldRegisteredPacketBuilder {
    /**
     * Was the registering successful?
     */
    private boolean success;
    /**
     * Packets uID
     */
    private long uID;

    public LSPacket getPacket() {
	LSPacketBuilder packet = new LSPacketBuilder();
	packet.setUID(uID);
	packet.addByte((byte) (success ? 1 : 0));
	return packet.toPacket();
    }

    /**
     * Sets whether or not we were successful
     */
    public void setSuccess(boolean success) {
	this.success = success;
    }

    /**
     * Sets the packet to reply to
     */
    public void setUID(long uID) {
	this.uID = uID;
    }
}
