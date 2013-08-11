package org.moparscape.msc.ls.auth.impl;

import java.util.Arrays;

import org.moparscape.msc.ls.Server;
import org.moparscape.msc.ls.auth.Auth;

public class PersistenceAuth implements Auth {

	@Override
	public boolean validate(long hash, byte[] pass, StringBuilder stringBuilder) {
		return Arrays.equals(Server.storage.getPass(hash), pass);
	}

}
