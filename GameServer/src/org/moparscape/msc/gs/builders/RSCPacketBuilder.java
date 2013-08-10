package org.moparscape.msc.gs.builders;

import org.moparscape.msc.gs.connection.RSCPacket;

public class RSCPacketBuilder extends StaticPacketBuilder {
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
	public RSCPacketBuilder setID(int pID) {
		this.pID = pID;
		return this;
	}

	/**
	 * Returns a <code>RSCPacket</code> object for the data contained in this
	 * builder.
	 * 
	 * @return A <code>RSCPacket</code> object
	 */
	public RSCPacket toPacket() {
		byte[] data = new byte[curLength];
		System.arraycopy(payload, 0, data, 0, curLength);
		return new RSCPacket(null, pID, data, bare);
	}

}