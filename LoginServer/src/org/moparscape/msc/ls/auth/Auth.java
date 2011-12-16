package org.moparscape.msc.ls.auth;

public interface Auth {
	
	boolean validate(long hash, String pass, StringBuilder stringBuilder);

}
