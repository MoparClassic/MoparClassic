package org.moparscape.msc.ls.auth.impl;

import org.moparscape.msc.ls.Server;
import org.moparscape.msc.ls.auth.Auth;

public class PersistenceAuth implements Auth {

	@Override
	public boolean validate(long hash, String pass,
			StringBuilder stringBuilder) {
		return Server.storage.getPass(hash).equals(pass);
	}

}
