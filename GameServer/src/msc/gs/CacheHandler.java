package msc.gs;

import java.util.HashMap;

import msc.gs.model.mini.Cache;
import msc.gs.tools.DataConversions;

/**
 * Created to hold variables server-side to a player even when they log out.
 * Needed for certain minor things that don't need SQL.
 * 
 * @author xEnt
 * 
 */
public class CacheHandler {

    public HashMap<Long, Cache> playerNames = new HashMap<Long, Cache>();
    public static CacheHandler cache = null;

    protected CacheHandler() {
    }

    public static CacheHandler getCache() {
	if (cache == null)
	    cache = new CacheHandler();
	return cache;
    }

    public boolean hasData(String name) {
	if (playerNames.get(DataConversions.usernameToHash(name)) != null)
	    return true;
	return false;
    }

    public Cache getCache(String name) {
	return playerNames.get(DataConversions.usernameToHash(name));
    }

    public void addCache(String name, Cache c) {
	if (hasData(name))
	    playerNames.remove(DataConversions.usernameToHash(name));
	playerNames.put(DataConversions.usernameToHash(name), c);
    }

}
