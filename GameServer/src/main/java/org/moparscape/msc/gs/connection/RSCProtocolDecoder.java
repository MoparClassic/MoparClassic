package org.moparscape.msc.gs.connection;

import org.apache.mina.common.ByteBuffer;
import org.apache.mina.common.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.moparscape.msc.gs.util.Logger;

/**
 * A decoder for the RSC protocol. Parses the incoming data from an IoSession
 * and outputs it as a <code>RSCPacket</code> object.
 */
public class RSCProtocolDecoder extends CumulativeProtocolDecoder {
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
	 * <code>out</code> as a <code>RSCPacket</code>.
	 * 
	 * @param session
	 *            The IoSession the data was read from
	 * @param in
	 *            The buffer
	 * @param out
	 *            The decoder output stream to which to write the
	 *            <code>RSCPacket</code>
	 * @return Whether enough data was available to create a packet
	 */
	protected synchronized boolean doDecode(IoSession session, ByteBuffer in,
			ProtocolDecoderOutput out) {
		try {
			if (in.remaining() >= 2) {
				byte[] buf = new byte[] { in.get(), in.get() };
				int length = ((short) ((buf[0] & 0xff) << 8) | (short) (buf[1] & 0xff));
				// Logger.log("len="+length);
				if (length <= in.remaining()) {
					if (length - 1 < 0) {
						Logger.println("Negative array length! id="
								+ in.getUnsigned() + ",len=" + length);
						session.close();
						return true;
					}
					byte[] payload = new byte[length - 1];
					int id = in.get() & 0xff;
					in.get(payload);
					RSCPacket p = new RSCPacket(session, id, payload);
					out.write(p);
					return true;
				} else {
					in.rewind();
					return false;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}
