package org.moparscape.msc.ls.codec;

import org.apache.mina.common.ByteBuffer;
import org.apache.mina.common.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.moparscape.msc.ls.Server;
import org.moparscape.msc.ls.net.LSPacket;


/**
 * Encodes the high level <code>LSPacket</code> class into the proper protocol
 * data required for transmission.
 */
public class LSProtocolEncoder implements ProtocolEncoder {
    /**
     * Releases all resources used by this encoder.
     * 
     * @param session
     *            The IO session
     */
    public void dispose(IoSession session) {
    }

    /**
     * Converts a <code>LSPacket</code> object into the raw data needed for
     * transmission.
     * 
     * @param session
     *            The IO session associated with the packet
     * @param message
     *            A <code>LSPacket</code> to encode
     * @param out
     *            The output stream to which to write the data
     */
    public void encode(IoSession session, Object message, ProtocolEncoderOutput out) {
	if (!(message instanceof LSPacket)) {
	    Server.error(new Exception("Wrong packet type! " + message.toString()));
	    return;
	}
	LSPacket p = (LSPacket) message;
	byte[] data = p.getData();
	int packetLength = data.length;
	ByteBuffer buffer;
	if (!p.isBare()) {
	    buffer = ByteBuffer.allocate(data.length + 13);
	    packetLength += 9;

	    buffer.putInt(packetLength);
	    buffer.put((byte) p.getID());
	    buffer.putLong(p.getUID());

	} else {
	    buffer = ByteBuffer.allocate(data.length);
	}
	buffer.put(data, 0, data.length);
	buffer.flip();
	out.write(buffer);
    }
}
