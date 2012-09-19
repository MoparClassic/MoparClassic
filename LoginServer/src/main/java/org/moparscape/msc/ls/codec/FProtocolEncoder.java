package org.moparscape.msc.ls.codec;

import java.net.URLEncoder;

import org.apache.mina.common.ByteBuffer;
import org.apache.mina.common.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.moparscape.msc.ls.Server;
import org.moparscape.msc.ls.net.FPacket;


/**
 * Encodes the high level <code>FPacket</code> class into the proper protocol
 * data required for transmission.
 */
public class FProtocolEncoder implements ProtocolEncoder {
    /**
     * Releases all resources used by this encoder.
     * 
     * @param session
     *            The IO session
     */
    public void dispose(IoSession session) {
    }

    /**
     * Converts a <code>FPacket</code> object into the raw data needed for
     * transmission.
     * 
     * @param session
     *            The IO session associated with the packet
     * @param message
     *            A <code>FPacket</code> to encode
     * @param out
     *            The output stream to which to write the data
     */
    public void encode(IoSession session, Object message, ProtocolEncoderOutput out) {
	if (!(message instanceof FPacket)) {
	    Server.error(new Exception("Wrong packet type! " + message.toString()));
	    return;
	}
	FPacket p = (FPacket) message;

	try {
	    String s = String.valueOf(p.getID());
	    if (p.countParameters() > 0) {
		for (String param : p.getParameters()) {
		    s += " " + URLEncoder.encode(param, "UTF-8");
		}
	    }
	    byte[] data = s.getBytes();

	    ByteBuffer buffer = ByteBuffer.allocate(data.length);
	    buffer.put(data, 0, data.length);
	    buffer.flip();
	    out.write(buffer);
	} catch (Exception e) {
	    Server.error(e);
	}
    }
}
