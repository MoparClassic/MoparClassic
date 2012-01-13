package org.moparscape.msc.gs.persistence.impl.bun.codec;

import java.nio.ByteBuffer;

import org.moparscape.msc.gs.model.definition.entity.NPCDefinition;
import org.moparscape.msc.gs.persistence.impl.bun.ByteBufferUtil;
import org.moparscape.msc.gs.persistence.impl.bun.Codec;

public class NpcDefCodec implements Codec<NPCDefinition[]> {

	@Override
	public ByteBuffer encode(NPCDefinition[] t) {
		
		return null;
	}
	
	private ByteBuffer encode(NPCDefinition d) {
		ByteBuffer buf = ByteBuffer.allocate(d.getName().length() + 1 + d.getDescription().length() + 1 + d.getCommand().length() + 2 + 4 * 2 + 2 + 13 + 8 + 8);
		
		ByteBufferUtil.putString(buf, d.getName());
		
		
		buf.flip();
		return buf;
	}

	@Override
	public NPCDefinition[] decode(ByteBuffer buf) {
		// TODO Auto-generated method stub
		return null;
	}
}
