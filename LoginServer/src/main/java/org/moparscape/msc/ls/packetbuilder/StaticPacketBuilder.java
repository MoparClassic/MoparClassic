package org.moparscape.msc.ls.packetbuilder;

import org.moparscape.msc.ls.Server;
import org.moparscape.msc.ls.net.Packet;

/**
 * A mutable sequence of bytes used to construct the immutable
 * <code>RSCPacket</code> objects.
 */
public class StaticPacketBuilder {
    /**
     * Bitmasks for <code>addBits()</code>
     */
    protected static int bitmasks[] = { 0, 0x1, 0x3, 0x7, 0xf, 0x1f, 0x3f, 0x7f, 0xff, 0x1ff, 0x3ff, 0x7ff, 0xfff, 0x1fff, 0x3fff, 0x7fff, 0xffff, 0x1ffff, 0x3ffff, 0x7ffff, 0xfffff, 0x1fffff, 0x3fffff, 0x7fffff, 0xffffff, 0x1ffffff, 0x3ffffff, 0x7ffffff, 0xfffffff, 0x1fffffff, 0x3fffffff, 0x7fffffff, -1 };
    /**
     * Default capacity
     */
    protected static final int DEFAULT_SIZE = 32;
    /**
     * Whether this packet does not use the standard packet header
     */
    protected boolean bare = false;
    /**
     * Current index into the buffer by bits
     */
    protected int bitPosition = 0;
    /**
     * Current number of bytes used in the buffer
     */
    protected int curLength;

    /**
     * The payload buffer
     */
    protected byte[] payload;

    /**
     * Constructs a packet builder with no data and an initial capacity of
     * <code>DEFAULT_SIZE</code>.
     * 
     * @see DEFAULT_SIZE
     */
    public StaticPacketBuilder() {
	this(DEFAULT_SIZE);
    }

    /**
     * Constructs a packet builder with no data and an initial capacity of
     * <code>capacity</code>.
     * 
     * @param capacity
     *            The initial capacity of the buffer
     */
    public StaticPacketBuilder(int capacity) {
	payload = new byte[capacity];
    }

    /**
     * TODO needs a proper description.
     */
    public StaticPacketBuilder addBits(int value, int numBits) {
	int bytePos = bitPosition >> 3;
	int bitOffset = 8 - (bitPosition & 7);
	bitPosition += numBits;
	curLength = (bitPosition + 7) / 8;
	ensureCapacity(curLength);
	for (; numBits > bitOffset; bitOffset = 8) {
	    payload[bytePos] &= ~bitmasks[bitOffset]; // mask out the desired
	    // area
	    payload[bytePos++] |= (value >> (numBits - bitOffset)) & bitmasks[bitOffset];

	    numBits -= bitOffset;
	}
	if (numBits == bitOffset) {
	    payload[bytePos] &= ~bitmasks[bitOffset];
	    payload[bytePos] |= value & bitmasks[bitOffset];
	} else {
	    payload[bytePos] &= ~(bitmasks[numBits] << (bitOffset - numBits));
	    payload[bytePos] |= (value & bitmasks[numBits]) << (bitOffset - numBits);
	}
	return this;
    }

    /**
     * Adds a <code>byte</code> to the data buffer. The size of this packet will
     * grow by one byte.
     * 
     * @param val
     *            The <code>byte</code> value to add
     * @return A reference to this object
     */
    public StaticPacketBuilder addByte(byte val) {
	return addByte(val, true);
    }

    /**
     * Adds a <code>byte</code> to the data buffer. The size of this packet will
     * grow by one byte.
     * 
     * @param val
     *            The <code>byte</code> value to add
     * @param checkCapacity
     *            Whether the buffer capacity should be checked
     * @return A reference to this object
     */
    private StaticPacketBuilder addByte(byte val, boolean checkCapacity) {
	if (checkCapacity)
	    ensureCapacity(curLength + 1);
	payload[curLength++] = val;
	return this;
    }

    /**
     * Adds the contents of <code>byte</code> array <code>data</code> to the
     * packet. The size of this packet will grow by the length of the provided
     * array.
     * 
     * @param data
     *            The bytes to add to this packet
     * @return A reference to this object
     */
    public StaticPacketBuilder addBytes(byte[] data) {
	return addBytes(data, 0, data.length);
    }

    /**
     * Adds the contents of <code>byte</code> array <code>data</code>, starting
     * at index <code>offset</code>. The size of this packet will grow by
     * <code>len</code> bytes.
     * 
     * @param data
     *            The bytes to add to this packet
     * @param offset
     *            The index of the first byte to append
     * @param len
     *            The number of bytes to append
     * @return A reference to this object
     */
    public StaticPacketBuilder addBytes(byte[] data, int offset, int len) {
	int newLength = curLength + len;
	ensureCapacity(newLength);
	System.arraycopy(data, offset, payload, curLength, len);
	curLength = newLength;
	return this;
    }

    /**
     * Adds a <code>int</code> to the data stream. The size of this packet will
     * grow by four bytes.
     * 
     * @param val
     *            The <code>int</code> value to add
     * @return A reference to this object
     */
    public StaticPacketBuilder addInt(int val) {
	ensureCapacity(curLength + 4);
	addByte((byte) (val >> 24), false);
	addByte((byte) (val >> 16), false);
	addByte((byte) (val >> 8), false);
	addByte((byte) val, false);
	return this;
    }

    /**
     * Adds a <code>long</code> to the data stream. The size of this packet will
     * grow by eight bytes.
     * 
     * @param val
     *            The <code>long</code> value to add
     * @return A reference to this object
     */
    public StaticPacketBuilder addLong(long val) {
	addInt((int) (val >> 32));
	addInt((int) (val & -1L));
	return this;
    }

    /**
     * Adds a <code>short</code> to the data stream. The size of this packet
     * will grow by two bytes.
     * 
     * @param val
     *            The <code>short</code> value to add
     * @return A reference to this object
     */
    public StaticPacketBuilder addShort(int val) {
	ensureCapacity(curLength + 2);
	addByte((byte) (val >> 8), false);
	addByte((byte) val, false);
	return this;
    }

    /**
     * Ensures that the buffer is at least <code>minimumBytes</code> bytes.
     * 
     * @param minimumCapacity
     *            The size needed
     */
    private void ensureCapacity(int minimumCapacity) {
	if (minimumCapacity >= payload.length)
	    expandCapacity(minimumCapacity);
    }

    /**
     * Expands the buffer to the specified size.
     * 
     * @param minimumCapacity
     *            The minimum capacity to which to expand
     * @see java.lang.AbstractStringBuilder#expandCapacity(int)
     */
    private void expandCapacity(int minimumCapacity) {
	int newCapacity = (payload.length + 1) * 2;
	if (newCapacity < 0) {
	    newCapacity = Integer.MAX_VALUE;
	} else if (minimumCapacity > newCapacity) {
	    newCapacity = minimumCapacity;
	}
	int oldLength = curLength;
	if (oldLength > payload.length) {
	    oldLength = payload.length;
	}
	byte[] newPayload = new byte[newCapacity];
	try {
	    System.arraycopy(payload, 0, newPayload, 0, oldLength);
	} catch (Exception e) {
	    Server.error(e);
	}
	payload = newPayload;
    }

    /**
     * Sets this packet as bare. A bare packet will contain only the payload
     * data, rather than having the standard packet header prepended.
     * 
     * @param bare
     *            Whether this packet is to be sent bare
     */
    public StaticPacketBuilder setBare(boolean bare) {
	this.bare = bare;
	return this;
    }

    /**
     * Returns a <code>Packet</code> object for the data contained in this
     * builder.
     * 
     * @return A <code>Packet</code> object
     */
    public Packet toPacket() {
	byte[] data = new byte[curLength];
	System.arraycopy(payload, 0, data, 0, curLength);
	return new Packet(null, data, bare);
    }
}
