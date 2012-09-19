package org.moparscape.msc.gs.npchandler;

import org.moparscape.msc.gs.model.Npc;
import org.moparscape.msc.gs.model.Player;

public interface NpcHandler {
	public void handleNpc(final Npc npc, Player player) throws Exception;
}
