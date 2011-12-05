package org.moparscape.msc.gs.persistence.impl.bun;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class IO {
	public static void write(File file, ByteBuffer buf) throws Exception {
		if(!file.exists()) {
			file.createNewFile();
		}
		
		FileOutputStream fos = new FileOutputStream(file);
		
		FileChannel chan = fos.getChannel();
		
		chan.write(buf);
		
		chan.close();
		fos.close();
	}
	
	public static ByteBuffer read(File file) throws Exception {
		FileInputStream fis = new FileInputStream(file);
		FileChannel chan = fis.getChannel();
		
		long size = chan.size();
		if(size > Integer.MAX_VALUE) {
			throw new IndexOutOfBoundsException("File too large");
		}
		
		ByteBuffer buf = ByteBuffer.allocate((int) size);
		
		chan.read(buf);
		
		chan.close();
		fis.close();
		
		buf.flip();

		return buf;		
	}
}
