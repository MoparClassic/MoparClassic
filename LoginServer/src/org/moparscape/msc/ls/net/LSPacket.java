package org.moparscape.msc.ls.net;

import org.apache.mina.common.IoSession;

/**
 * An immutable packet object.
 */
public final class LSPacket extends Packet {
    /**
     * The ID of the packet
     */
    private int pID;
    /**
     * The uID of the packet
     */
    private long uID;

    public LSPacket(IoSession session, int pID, long uID, byte[] pData) {
	this(session, pID, uID, pData, false);
    }

    public LSPacket(IoSession session, int pID, long uID, byte[] pData, boolean bare) {
	super(session, pData, bare);
	this.pID = pID;
	this.uID = uID;
    }

    /**
     * Returns the packet ID.
     * 
     * @return The packet ID
     */
    public int getID() {
	return pID;
    }

    /**
     * Returns the unique ID.
     * 
     * @return The unique ID
     */
    public long getUID() {
	return uID;
    }

    /**
     * Returns this packet in string form.
     * 
     * @return A <code>String</code> representing this packet
     */
    public String toString() {
	return super.toString() + " pid = " + pID + " uid = " + uID;
    }

}
