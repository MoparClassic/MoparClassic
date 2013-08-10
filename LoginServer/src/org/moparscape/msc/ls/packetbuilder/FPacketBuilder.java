package org.moparscape.msc.ls.packetbuilder;

import org.moparscape.msc.ls.net.FPacket;

public class FPacketBuilder extends StaticPacketBuilder {
    /**
     * The headers of the packet
     */
    private String[] parameters = new String[0];
    /**
     * ID of the packet
     */
    private int pID = 0;

    /**
     * Sets the ID for this packet.
     * 
     * @param id
     *            The ID of the packet
     */
    public FPacketBuilder setID(int pID) {
	this.pID = pID;
	return this;
    }

    /**
     * Sets the parameters for this packet.
     * 
     * @param id
     *            The parameters of the packet
     */
    public FPacketBuilder setParameters(String[] parameters) {
	this.parameters = parameters;
	return this;
    }

    /**
     * Returns a <code>FPacket</code> object for the data contained in this
     * builder.
     * 
     * @return A <code>FPacket</code> object
     */
    public FPacket toPacket() {
	return new FPacket(null, pID, parameters, bare);
    }

}