package org.moparscape.msc.ls.auth.impl;

import org.moparscape.msc.ls.auth.Auth;

class DummyAuth implements Auth {

	@Override
	public boolean validate(long hash, String pass, StringBuilder stringBuilder) {
		return true;
	}

}
