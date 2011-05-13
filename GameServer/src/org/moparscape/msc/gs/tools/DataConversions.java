package org.moparscape.msc.gs.tools;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Random;

import org.moparscape.msc.gs.connection.RSCPacket;
import org.moparscape.msc.gs.core.GameEngine;
import org.moparscape.msc.gs.model.Point;
import org.moparscape.msc.gs.util.Logger;


import com.bombaydigital.vault.HexString;

public final class DataConversions {
    private static char characters[] = { ' ', 'e', 't', 'a', 'o', 'i', 'h', 'n', 's', 'r', 'd', 'l', 'u', 'm', 'w', 'c', 'y', 'f', 'g', 'p', 'b', 'v', 'k', 'x', 'j', 'q', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', ' ', '!', '?', '.', ',', ':', ';', '(', ')', '-', '&', '*', '\\', '\'', '@', '#', '+', '=', '\243', '$', '%', '"', '[', ']' };
    private static SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss dd-MM-yy");
    private static final BigInteger key = new BigInteger("730546719878348732291497161314617369560443701473303681965331739205703475535302276087891130348991033265134162275669215460061940182844329219743687403068279");
    private static MessageDigest md;
    private static final BigInteger modulus = new BigInteger("1549611057746979844352781944553705273443228154042066840514290174539588436243191882510185738846985723357723362764835928526260868977814405651690121789896823");
    private static Random rand = new Random();

    /**
     * Creates an instance of the message digest used for creating md5 hashes
     */
    static {
	try {
	    md = MessageDigest.getInstance("MD5");
	} catch (Exception e) {
	    Logger.error(e);
	}
    }

    /**
     * Calculates the average of all values in the array
     */
    public static int average(int[] values) {
	int total = 0;
	for (int value : values) {
	    total += value;
	}
	return (int) (total / values.length);
    }

    /**
     * Decodes a byte array back into a string
     */
    public static String byteToString(byte[] data, int offset, int length) {
	char[] buffer = new char[100];
	try {
	    int k = 0;
	    int l = -1;
	    for (int i1 = 0; i1 < length; i1++) {
		int j1 = data[offset++] & 0xff;
		int k1 = j1 >> 4 & 0xf;
		if (l == -1) {
		    if (k1 < 13) {
			buffer[k++] = characters[k1];
		    } else {
			l = k1;
		    }
		} else {
		    buffer[k++] = characters[((l << 4) + k1) - 195];
		    l = -1;
		}
		k1 = j1 & 0xf;
		if (l == -1) {
		    if (k1 < 13) {
			buffer[k++] = characters[k1];
		    } else {
			l = k1;
		    }
		} else {
		    buffer[k++] = characters[((l << 4) + k1) - 195];
		    l = -1;
		}
	    }
	    boolean flag = true;
	    for (int l1 = 0; l1 < k; l1++) {
		char c = buffer[l1];
		if (l1 > 4 && c == '@') {
		    buffer[l1] = ' ';
		}
		if (c == '%') {
		    buffer[l1] = ' ';
		}
		if (flag && c >= 'a' && c <= 'z') {
		    buffer[l1] += '\uFFE0';
		    flag = false;
		}
		if (c == '.' || c == '!' || c == ':') {
		    flag = true;
		}
	    }
	    return new String(buffer, 0, k);
	} catch (Exception e) {
	    return ".";
	}
    }

    /**
     * Decrypts an RSA encrypted packet using our private key
     */
    public static RSCPacket decryptRSA(byte[] pData) {
	try {
	    BigInteger bigInteger = new BigInteger(pData);
	    pData = bigInteger.modPow(key, modulus).toByteArray();
	    return new RSCPacket(null, 0, pData, true);
	} catch (Exception e) {
	    return null;
	}
    }

    /**
     * returns the code used to represent the given character in our byte array
     * encoding methods
     */
    private static int getCharCode(char c) {
	for (int x = 0; x < characters.length; x++) {
	    if (c == characters[x]) {
		return x;
	    }
	}
	return 0;
    }

    private static byte getMobCoordOffset(int coord1, int coord2) {
	byte offset = (byte) (coord1 - coord2);
	if (offset < 0) {
	    offset += 32;
	}
	return offset;
    }

    public static byte[] getMobPositionOffsets(Point p1, Point p2) {
	byte[] rv = new byte[2];
	rv[0] = getMobCoordOffset(p1.getX(), p2.getX());
	rv[1] = getMobCoordOffset(p1.getY(), p2.getY());
	return rv;
    }

    private static byte getObjectCoordOffset(int coord1, int coord2) {
	return (byte) (coord1 - coord2);
    }

    public static byte[] getObjectPositionOffsets(Point p1, Point p2) {
	byte[] rv = new byte[2];
	rv[0] = getObjectCoordOffset(p1.getX(), p2.getX());
	rv[1] = getObjectCoordOffset(p1.getY(), p2.getY());
	return rv;
    }

    /**
     * Returns the random number generator
     */
    public static Random getRandom() {
	return rand;
    }

    /**
     * Converts a usernames hash back to the username
     */
    public static String hashToUsername(long l) {
	if (l < 0L)
	    return "invalid_name";
	String s = "";
	while (l != 0L) {
	    int i = (int) (l % 37L);
	    l /= 37L;
	    if (i == 0)
		s = " " + s;
	    else if (i < 27) {
		if (l % 37L == 0L)
		    s = (char) ((i + 65) - 1) + s;
		else
		    s = (char) ((i + 97) - 1) + s;
	    } else {
		s = (char) ((i + 48) - 27) + s;
	    }
	}
	return s;
    }

    /**
     * Checks if the given int is in the array
     */
    public static boolean inArray(int[] haystack, int needle) {
	for (int option : haystack) {
	    if (needle == option) {
		return true;
	    }
	}
	return false;
    }
    /**
     * Checks if the given point is in the array
     */
    public static boolean inPointArray(Point[] haystack, Point needle) {
	for (Point option : haystack) {
	    if (needle.getX() == option.getX() && needle.getY() == option.getY()) {
	    	return true;
	    }
	}
	return false;
    }
    public static long IPToLong(String ip) {
	String[] octets = ip.split("\\.");
	long result = 0L;
	for (int x = 0; x < 4; x++) {
	    result += Integer.parseInt(octets[x]) * Math.pow(256, 3 - x);
	}
	return result;
    }

    public static String IPToString(long ip) {
	String result = "0.0.0.0";
	for (int x = 0; x < 4; x++) {
	    int octet = (int) (ip / Math.pow(256, 3 - x));
	    ip -= octet * Math.pow(256, 3 - x);
	    if (x == 0) {
		result = String.valueOf(octet);
	    } else {
		result += ("." + octet);
	    }
	}
	return result;
    }

    public static void main(String[] argv) throws Exception {
	if (argv[0].equals("encode"))
		Logger.println(usernameToHash(argv[1]));
	if (argv[0].equals("decode"))
		Logger.println(hashToUsername(Long.parseLong(argv[1])));
    }

    /**
     * returns the max of the 2 values
     */
    public static int max(int i1, int i2) {
	return i1 > i2 ? i1 : i2;
    }

    /**
     * returns the md5 hash of a string
     */
    public static String md5(String s) {
	md.reset();
	md.update(s.getBytes());
	return HexString.bufferToHex(md.digest());
    }

    /**
     * Returns true percent% of the time
     */
    public static boolean percentChance(int percent) {
	return random(1, 100) <= percent;
    }

    /**
     * returns a random number within the given bounds
     */
    public static double random(double low, double high) {
	return high - (rand.nextDouble() * low);
    }

    /**
     * returns a random number within the given bounds
     */
    public static int random(int low, int high) {
	return low + rand.nextInt(high - low + 1);
    }

    /**
     * returns a random number within the given bounds, but allows for certain
     * values to be weighted
     */
    public static int randomWeighted(int low, int dip, int peak, int max) {
	int total = 0;
	int probability = 100;
	int[] probArray = new int[max + 1];
	for (int x = 0; x < probArray.length; x++) {
	    total += probArray[x] = probability;
	    if (x < dip || x > peak) {
		probability -= 3;
	    } else {
		probability += 3;
	    }
	}
	int hit = random(0, total);
	total = 0;
	for (int x = 0; x < probArray.length; x++) {
	    if (hit >= total && hit < (total + probArray[x])) {
		return x;
	    }
	    total += probArray[x];
	}
	return 0;
    }

    public static double round(double value, int decimalPlace) {
	BigDecimal bd = new BigDecimal(value);
	bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
	return (bd.doubleValue());
    }

    public static int roundUp(double val) {
	return (int) Math.round(val + 0.5D);
    }

    /**
     * Returns a ByteBuffer containing everything available from the given
     * InputStream
     */
    public static final ByteBuffer streamToBuffer(BufferedInputStream in) throws IOException {
	byte[] buffer = new byte[in.available()];
	in.read(buffer, 0, buffer.length);
	return ByteBuffer.wrap(buffer);
    }

    /**
     * Encodes a string into a byte array
     */
    public static byte[] stringToByteArray(String message) {
	byte[] buffer = new byte[100];
	if (message.length() > 80) {
	    message = message.substring(0, 80);
	}
	message = message.toLowerCase();
	int length = 0;
	int j = -1;
	for (int k = 0; k < message.length(); k++) {
	    int code = getCharCode(message.charAt(k));
	    if (code > 12) {
		code += 195;
	    }
	    if (j == -1) {
		if (code < 13)
		    j = code;
		else
		    buffer[length++] = (byte) code;
	    } else if (code < 13) {
		buffer[length++] = (byte) ((j << 4) + code);
		j = -1;
	    } else {
		buffer[length++] = (byte) ((j << 4) + (code >> 4));
		j = code & 0xf;
	    }
	}
	if (j != -1) {
	    buffer[length++] = (byte) (j << 4);
	}
	byte[] string = new byte[length];
	System.arraycopy(buffer, 0, string, 0, length);
	return string;
    }

    public static String timeFormat(long l) {
	return formatter.format(l);
    }

    public static String timeSince(long time) {
	int seconds = (int) ((GameEngine.getTime() - time) / 1000);
	int minutes = (int) (seconds / 60);
	int hours = (int) (minutes / 60);
	int days = (int) (hours / 24);
	return days + " days " + (hours % 24) + " hours " + (minutes % 60) + " mins";
    }

    /**
     * Converts a username to a unique hash
     */
    public static long usernameToHash(String s) {
	s = s.toLowerCase();
	String s1 = "";
	for (int i = 0; i < s.length(); i++) {
	    char c = s.charAt(i);
	    if (c >= 'a' && c <= 'z')
		s1 = s1 + c;
	    else if (c >= '0' && c <= '9')
		s1 = s1 + c;
	    else
		s1 = s1 + ' ';
	}

	s1 = s1.trim();
	if (s1.length() > 12)
	    s1 = s1.substring(0, 12);
	long l = 0L;
	for (int j = 0; j < s1.length(); j++) {
	    char c1 = s1.charAt(j);
	    l *= 37L;
	    if (c1 >= 'a' && c1 <= 'z')
		l += (1 + c1) - 97;
	    else if (c1 >= '0' && c1 <= '9')
		l += (27 + c1) - 48;
	}
	return l;
    }

}
