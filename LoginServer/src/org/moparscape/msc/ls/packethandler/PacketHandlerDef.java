package org.moparscape.msc.ls.packethandler;

/**
 * The definition of a packet handler, for use with XStream.
 */
public class PacketHandlerDef {
	/**
	 * The name of the handler class
	 */
	public String className;
	/**
	 * The IDs of packets which the handler is responsible for
	 */
	public int[] ids;

	/**
	 * Constructs a new packet handler definition, entailing that packets with
	 * the given IDs are to be handled by the specified class.
	 * 
	 * @param ids
	 *            The IDs of packets to be handled
	 * @param className
	 *            The name of the packet handler class
	 */
	public PacketHandlerDef(int[] ids, String className) {
		this.ids = ids;
		this.className = className;
	}

	/**
	 * Returns the IDs of packets to be handled.
	 * 
	 * @return An <code>int</code> array containing the IDs of packets to be
	 *         handled.
	 */
	public int[] getAssociatedPackets() {
		return ids;
	}

	/**
	 * Returns the name of the packet handler class.
	 * 
	 * @return The name of the packet handler's class
	 */
	public String getClassName() {
		return className;
	}
}
