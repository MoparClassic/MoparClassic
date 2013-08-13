package org.moparscape.msc.gs.db.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;

import org.moparscape.msc.gs.db.DataService;

public class NIODataService implements DataService {

	NIODataService() {

	}

	private static final File data = new File("ip-bans.txt");
	private static final ArrayList<String> ipBans = getIPBans();

	@SuppressWarnings("unchecked")
	@Override
	public List<String> requestIPBans() {
		return (List<String>) ipBans.clone();
	}

	private static ArrayList<String> getIPBans() {
		ArrayList<String> d = new ArrayList<String>();

		if (!data.exists()) {
			return d;
		}

		FileInputStream fin = null;
		FileChannel chan = null;
		ByteBuffer buf = null;
		try {
			fin = new FileInputStream(data.getPath());
			chan = fin.getChannel();

			long size = chan.size();
			buf = ByteBuffer.allocateDirect((int) size);

			chan.read(buf);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				fin.close();
				chan.close();
			} catch (IOException e) {
			}
		}

		buf.rewind();

		StringBuilder sb = new StringBuilder(15);
		for (int i = 0; i < buf.capacity(); i++) {
			char c = (char) buf.get();
			if (c == '\r') {
				continue;
			}
			if (c == '\n') {
				d.add(sb.toString());
				sb = new StringBuilder(15);
				continue;
			}

			sb.append(c);
		}

		return d;
	}

	@Override
	public void unbanIP(String ip) {
		synchronized (ipBans) {
			ipBans.remove(ip);
		}
		save();
	}

	private void save() {
		List<String> ipBans = requestIPBans();
		if (ipBans.isEmpty()) {
			data.delete();
		}
		StringBuilder sb = new StringBuilder(16 * ipBans.size());
		for (String ip : ipBans) {
			sb.append(ip).append('\n');
		}
		String d = sb.toString();

		ByteBuffer buf = ByteBuffer.allocateDirect(d.length());
		for (char c : d.toCharArray()) {
			buf.put((byte) c);
		}

		buf.flip();

		if (!data.exists()) {
			try {
				data.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		FileOutputStream fos = null;
		FileChannel chan = null;
		try {
			fos = new FileOutputStream(data);
			chan = fos.getChannel();

			chan.write(buf);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				chan.close();
				fos.close();
			} catch (IOException e) {
			}
		}
	}

	@Override
	public boolean banIP(String ip) {
		synchronized (ipBans) {
			if (!ipBans.contains(ip)) {
				ipBans.add(ip);
			}
		}
		save();
		return true;
	}

}
