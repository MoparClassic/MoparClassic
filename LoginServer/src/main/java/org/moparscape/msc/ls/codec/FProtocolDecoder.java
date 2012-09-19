package org.moparscape.msc.ls.codec;

import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

import org.apache.mina.common.ByteBuffer;
import org.apache.mina.common.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.moparscape.msc.ls.Server;
import org.moparscape.msc.ls.net.FPacket;


/**
 * A decoder for the Frontend protocol. Parses the incoming data from an
 * IoSession and outputs it as a <code>FPacket</code> object.
 */
public class FProtocolDecoder extends CumulativeProtocolDecoder {
    private static CharsetDecoder stringDecoder;

    static {
	try {
	    stringDecoder = Charset.forName("UTF-8").newDecoder();
	} catch (Exception e) {
	    Server.error(e);
	}
    }

    /**
     * Releases the buffer used by the given session.
     * 
     * @param session
     *            The session for which to release the buffer
     * @throws Exception
     *             if failed to dispose all resources
     */
    public void dispose(IoSession session) throws Exception {
	super.dispose(session);
    }

    /**
     * Parses the data in the provided byte buffer and writes it to
     * <code>out</code> as a <code>FPacket</code>.
     * 
     * @param session
     *            The IoSession the data was read from
     * @param in
     *            The buffer
     * @param out
     *            The decoder output stream to which to write the
     *            <code>FPacket</code>
     * @return Whether enough data was available to create a packet
     */
    protected boolean doDecode(IoSession session, ByteBuffer in, ProtocolDecoderOutput out) {
	try {
	    String s = in.getString(stringDecoder).trim();
	    int delim = s.indexOf(" ");

	    int id;
	    String[] params;
	    if (delim > -1) {
		id = Integer.parseInt(s.substring(0, delim));
		params = s.substring(delim + 1).split(" ");
	    } else {
		id = Integer.parseInt(s);
		params = new String[0];
	    }
	    for (int i = 0; i < params.length; i++) {
		params[i] = URLDecoder.decode(params[i], "UTF-8");
	    }
	    out.write(new FPacket(session, id, params));
	    return true;
	} catch (Exception e) {
	    Server.error(e);
	}
	return false;
    }
}
