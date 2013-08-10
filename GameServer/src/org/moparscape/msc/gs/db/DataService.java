package org.moparscape.msc.gs.db;

import java.util.List;

public interface DataService {
	public List<String> requestIPBans();

	public void unbanIP(String ip);

	public boolean banIP(String ip);
}
