package org.moparscape.msc.ls.util;

public class DataConversions {
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