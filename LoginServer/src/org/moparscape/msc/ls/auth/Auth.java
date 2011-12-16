package org.moparscape.msc.ls.auth;

public interface Auth {

	public boolean validate(String hashToUsername, String pass,
			StringBuilder stringBuilder);

}
