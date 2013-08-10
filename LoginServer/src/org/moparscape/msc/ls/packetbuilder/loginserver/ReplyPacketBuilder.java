package org.moparscape.msc.ls.packetbuilder.loginserver;

import org.moparscape.msc.ls.net.LSPacket;
import org.moparscape.msc.ls.packetbuilder.LSPacketBuilder;

public class ReplyPacketBuilder {
    /**
     * Reply
     */
    private String reply;
    /**
     * Was the action successful
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
	if (reply != null) {
	    packet.addBytes(reply.getBytes());
	}
	return packet.toPacket();
    }

    /**
     * Sets the reply to send back
     */
    public void setReply(String reply) {
	this.reply = reply;
    }

    /**
     * Sets the status of the action
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
