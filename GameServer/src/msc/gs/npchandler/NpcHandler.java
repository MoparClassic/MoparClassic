package msc.gs.npchandler;

import msc.gs.model.Npc;
import msc.gs.model.Player;

public interface NpcHandler {
    public void handleNpc(final Npc npc, Player player) throws Exception;
}
