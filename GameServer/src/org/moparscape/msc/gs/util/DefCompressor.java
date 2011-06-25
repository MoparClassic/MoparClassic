package org.moparscape.msc.gs.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class DefCompressor {

	public static void compressFile(File f) throws IOException {
		File target = new File(f.toString() + ".gz");
		System.out.print("Compressing: " + f.getName() + ".. ");
		long initialSize = f.length();
		FileInputStream fis = new FileInputStream(f);
		GZIPOutputStream out = new GZIPOutputStream(
				new FileOutputStream(target));
		byte[] buf = new byte[1024];
		int read;
		while ((read = fis.read(buf)) != -1) {
			out.write(buf, 0, read);
		}
		Logger.println("Done.");
		fis.close();
		out.close();
		long endSize = target.length();
		Logger.println("Initial size: " + initialSize + "; Compressed size: "
				+ endSize);
	}

	public static void decompressFile(File f) throws IOException {
		File target = new File(f.toString().substring(0,
				f.toString().length() - 3));
		System.out.print("Decompressing: " + f.getName() + ".. ");
		long initialSize = f.length();
		GZIPInputStream in = new GZIPInputStream(new FileInputStream(f));
		FileOutputStream fos = new FileOutputStream(target);
		byte[] buf = new byte[1024];
		int read;
		while ((read = in.read(buf)) != -1) {
			fos.write(buf, 0, read);
		}
		Logger.println("Done.");
		fos.close();
		in.close();
		long endSize = target.length();
		Logger.println("Initial size: " + initialSize + "; Decompressed size: "
				+ endSize);
	}

	public static void main(String[] argv) throws Exception {
		if (argv[0].equals("compress"))
			compressFile(new File(argv[1]));
		if (argv[0].equals("decompress"))
			decompressFile(new File(argv[1]));
	}
}
