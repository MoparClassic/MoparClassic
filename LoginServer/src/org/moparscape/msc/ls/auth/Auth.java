package org.moparscape.msc.ls.auth;

public interface Auth {

	boolean validate(long hash, byte[] pass, StringBuilder stringBuilder);

}
