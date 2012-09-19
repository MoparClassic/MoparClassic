package org.moparscape.msc.gs.persistence.impl.bun;

import java.nio.ByteBuffer;

public interface Codec<T> {
	public ByteBuffer encode(T t);
	public T decode(ByteBuffer buf);
}
