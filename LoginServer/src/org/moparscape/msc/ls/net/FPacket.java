package org.moparscape.msc.ls.net;

import org.apache.mina.common.IoSession;

/**
 * An immutable packet object.
 */
public final class FPacket extends Packet {
    /**
     * The headers of the packet
     */
    private String[] parameters;
    /**
     * The ID of the packet
     */
    private int pID;

    public FPacket(IoSession session, int pID, String[] parameters) {
	this(session, pID, parameters, false);
    }

    public FPacket(IoSession session, int pID, String[] parameters, boolean bare) {
	super(session, new byte[0], bare);
	this.pID = pID;
	this.parameters = parameters;
    }

    /**
     * Returns the amount of parameters
     */
    public int countParameters() {
	return parameters.length;
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
     * Returns the parameter list.
     * 
     * @return The parameter list
     */
    public String[] getParameters() {
	return parameters;
    }

    /**
     * Returns this packet in string form.
     * 
     * @return A <code>String</code> representing this packet
     */
    public String toString() {
	return super.toString() + " pid = " + pID + " parameter count = " + parameters.length;
    }

}
