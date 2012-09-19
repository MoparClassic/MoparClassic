package org.moparscape.msc.gs.persistence.impl.bun;

import java.nio.ByteBuffer;

public class ByteBufferUtil {
	public static ByteBuffer putString(ByteBuffer buf, String s) {
		buf.put((byte) s.length());
		for (byte c : s.getBytes()) {
			buf.put(c);
		}
		return buf;
	}

	public static String getString(ByteBuffer buf) {
		int bytes = buf.get();
		StringBuilder sb = new StringBuilder(bytes);
		for (int i = 0; i < bytes; i++) {
			sb.append((char) buf.get());
		}
		return sb.toString();
	}
}
