package org.moparscape.msc.gs.connection;

import org.apache.mina.common.ByteBuffer;
import org.apache.mina.common.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.moparscape.msc.gs.util.Logger;

/**
 * Encodes the high level <code>RSCPacket</code> class into the proper protocol
 * data required for transmission.
 */
public class RSCProtocolEncoder implements ProtocolEncoder {
	/**
	 * Releases all resources used by this encoder.
	 * 
	 * @param session
	 *            The IO session
	 */
	public void dispose(IoSession session) {
	}

	/**
	 * Converts a <code>RSCPacket</code> object into the raw data needed for
	 * transmission.
	 * 
	 * @param session
	 *            The IO session associated with the packet
	 * @param message
	 *            A <code>RSCPacket</code> to encode
	 * @param out
	 *            The output stream to which to write the data
	 */
	public synchronized void encode(IoSession session, Object message,
			ProtocolEncoderOutput out) {
		if (!(message instanceof RSCPacket)) {
			Logger.error(new Exception("Wrong packet type! "
					+ message.toString()));
			return;
		}
		try {
			RSCPacket p = (RSCPacket) message;
			byte[] data = p.getData();
			int dataLength = data.length;
			ByteBuffer buffer;
			// byte[] debug = new byte[dataLength + 4];
			if (!p.isBare()) {
				buffer = ByteBuffer.allocate(dataLength + 3);
				byte[] outlen = { (byte) (dataLength >> 8), (byte) (dataLength) };
				buffer.put(outlen);
				// debug[0] = outlen[0]; debug[1] = outlen[1];
				int id = p.getID();
				// debug[2] = (byte)offset; debug[3] = (byte)id;
				buffer.put((byte) id);
			} else {
				buffer = ByteBuffer.allocate(dataLength);
			}
			// System.arraycopy(data, 0, debug, 4, dataLength);
			// Logging.debug(java.util.Arrays.toString(data));
			buffer.put(data, 0, dataLength);
			buffer.flip();
			out.write(buffer);
			return;

			/*
			 * byte[] data = p.getData(); int packetLength = data.length; int
			 * dataLength = data.length; ByteBuffer buffer; if (!p.isBare()) {
			 * buffer = ByteBuffer.allocate(dataLength + 3); packetLength++; if
			 * (data.length >= 160) { buffer.put((byte) (160 + (packetLength /
			 * 256))); buffer.put((byte) (packetLength & 0xff)); } else {
			 * buffer.put((byte) (packetLength)); if (dataLength > 0) {
			 * dataLength--; buffer.put((byte) data[dataLength]); } }
			 * buffer.put((byte) p.getID()); } else { buffer =
			 * ByteBuffer.allocate(dataLength); } buffer.put(data, 0,
			 * dataLength); buffer.flip();
			 * 
			 * out.write(buffer);
			 */

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
