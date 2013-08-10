package org.moparscape.msc.ls.net;

import java.net.InetSocketAddress;

import org.apache.mina.common.IoSession;
import org.moparscape.msc.ls.Server;


/**
 * An immutable packet object.
 */
public class Packet {
    /**
     * Whether this packet is without the standard packet header
     */
    protected boolean bare;
    /**
     * The current index into the payload buffer for reading
     */
    protected int caret = 0;
    /**
     * The payload
     */
    protected byte[] pData;
    /**
     * The length of the payload
     */
    protected int pLength;
    /**
     * The associated IO session
     */
    protected IoSession session;
    /**
     * The time this packet was created
     */
    protected long time;

    /**
     * Creates a new packet with the specified parameters. The packet is
     * considered not to be a bare packet.
     * 
     * @param session
     *            The session to associate with the packet
     * @param pData
     *            The payload the packet
     */
    public Packet(IoSession session, byte[] pData) {
	this(session, pData, false);
    }

    /**
     * Creates a new packet with the specified parameters.
     * 
     * @param session
     *            The session to associate with the packet
     * @param pData
     *            The payload of the packet
     * @param bare
     *            Whether this packet is bare, which means that it does not
     *            include the standard packet header
     */
    public Packet(IoSession session, byte[] pData, boolean bare) {
	this.session = session;
	this.pData = pData;
	this.pLength = pData.length;
	this.bare = bare;
	time = System.currentTimeMillis();
    }

    /**
     * Returns the time the packet was created.
     * 
     * @ return the time the packet was created
     */
    public long getCreated() {
	return time;
    }

    /**
     * Returns the entire payload data of this packet.
     * 
     * @return The payload <code>byte</code> array
     */
    public byte[] getData() {
	return pData;
    }

    /**
     * Returns the length of the payload of this packet.
     * 
     * @return The length of the packet's payload
     */
    public int getLength() {
	return pLength;
    }

    /**
     * Returns the remaining payload data of this packet.
     * 
     * @return The payload <code>byte</code> array
     */
    public byte[] getRemainingData() {
	byte[] data = new byte[pLength - caret];
	for (int i = 0; i < data.length; i++) {
	    data[i] = pData[i + caret];
	}
	caret += data.length;
	return data;

    }

    /**
     * Returns the IO session associated with the packet, if any.
     * 
     * @return The <code>IoSession</code> object, or <code>null</code> if none.
     */
    public IoSession getSession() {
	return session;
    }

    /**
     * Checks if this packet is considered to be a bare packet, which means that
     * it does not include the standard packet header (ID and length values).
     * 
     * @return Whether this packet is a bare packet
     */
    public boolean isBare() {
	return bare;
    }

    public String printData() {
	if (pLength == 0) {
	    return "";
	}
	String data = "";
	for (int i = 0; i < pLength; i++) {
	    data += " " + pData[i];
	}
	return data.substring(1);
    }

    /**
     * Reads the next <code>byte</code> from the payload.
     * 
     * @return A <code>byte</code>
     */
    public byte readByte() {
	return pData[caret++];
    }

    /**
     * Returns length of payload data of this packet.
     * 
     * @return The payload <code>byte</code> array
     */
    public byte[] readBytes(int length) {
	byte[] data = new byte[length];
	try {
	    for (int i = 0; i < length; i++) {
		data[i] = pData[i + caret];
	    }
	} catch (Exception e) {
	    Server.error(e.getMessage());
	}
	caret += length;
	return data;
    }

    /**
     * Reads the next <code>int</code> from the payload.
     * 
     * @return An <code>int</code>
     */
    public int readInt() {
	try {
	    return ((pData[caret++] & 0xff) << 24) | ((pData[caret++] & 0xff) << 16) | ((pData[caret++] & 0xff) << 8) | (pData[caret++] & 0xff);
	} catch (Exception e) {
	    Server.error(e.getMessage());
	    return 0;
	}
    }

    /**
     * Reads the next <code>long</code> from the payload.
     * 
     * @return A <code>long</code>
     */
    public long readLong() {
	try {
	    return (long) ((long) (pData[caret++] & 0xff) << 56) | ((long) (pData[caret++] & 0xff) << 48) | ((long) (pData[caret++] & 0xff) << 40) | ((long) (pData[caret++] & 0xff) << 32) | ((long) (pData[caret++] & 0xff) << 24) | ((long) (pData[caret++] & 0xff) << 16) | ((long) (pData[caret++] & 0xff) << 8) | ((long) (pData[caret++] & 0xff));
	} catch (Exception e) {
	    Server.error(e.getMessage());
	    return 0;
	}
    }

    /**
     * Reads the next <code>short</code> from the payload.
     * 
     * @return A <code>short</code>
     */
    public short readShort() {
	try {
	    return (short) ((short) ((pData[caret++] & 0xff) << 8) | (short) (pData[caret++] & 0xff));
	} catch (Exception e) {
	    Server.error(e.getMessage());
	    return 0;
	}
    }

    /**
     * Reads the string which is formed by the unread portion of the payload.
     * 
     * @return A <code>String</code>
     */
    public String readString() {
	return readString(pLength - caret);
    }

    /**
     * Reads a string of the specified length from the payload.
     * 
     * @param length
     *            The length of the string to be read
     * @return A <code>String</code>
     */
    public String readString(int length) {
	String rv = new String(pData, caret, length);
	caret += length;
	return rv;
    }

    public int remaining() {
	return pData.length - caret;
    }

    /**
     * Skips the specified number of bytes in the payload.
     * 
     * @param x
     *            The number of bytes to be skipped
     */
    public void skip(int x) {
	caret += x;
    }

    /**
     * Returns this packet in string form.
     * 
     * @return A <code>String</code> representing this packet
     */
    public String toString() {
	String origin = session == null ? "this" : ((InetSocketAddress) session.getRemoteAddress()).getAddress().getHostAddress();
	return "origin = " + origin + " length = " + pLength;
    }

}
