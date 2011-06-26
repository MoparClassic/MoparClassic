package org.moparscape.msc.gs.connection;

import org.apache.mina.common.ByteBuffer;
import org.apache.mina.common.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

/**
 * A decoder for the LS protocol. Parses the incoming data from an IoSession and
 * outputs it as a <code>LSPacket</code> object.
 */
public class LSProtocolDecoder extends CumulativeProtocolDecoder {
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
	 * <code>out</code> as a <code>LSPacket</code>.
	 * 
	 * @param session
	 *            The IoSession the data was read from
	 * @param in
	 *            The buffer
	 * @param out
	 *            The decoder output stream to which to write the
	 *            <code>LSPacket</code>
	 * @return Whether enough data was available to create a packet
	 */
	protected boolean doDecode(IoSession session, ByteBuffer in,
			ProtocolDecoderOutput out) {
		if (in.remaining() >= 13) {
			int length = in.getInt();
			if (length <= in.remaining()) {
				byte[] payload = new byte[length - 9];

				int id = in.getUnsigned();
				long uid = in.getLong();
				in.get(payload);

				out.write(new LSPacket(session, id, uid, payload));
				return true;
			} else {
				in.rewind();
				return false;
			}
		}
		return false;
	}
}
