package msc.gs.npchandler;

import msc.gs.Instance;
import msc.gs.event.ShortEvent;
import msc.gs.model.ChatMessage;
import msc.gs.model.Npc;
import msc.gs.model.Player;
import msc.gs.model.World;

public class Bankers implements NpcHandler {
    /**
     * World instance
     */
    public static final World world = Instance.getWorld();

    public void handleNpc(final Npc npc, Player player) throws Exception {
	player.setBusy(true);
	player.informOfChatMessage(new ChatMessage(player, "I'd like to access my bank account please", npc));
	Instance.getDelayedEventHandler().add(new ShortEvent(player) {
	    public void action() {
		owner.informOfNpcMessage(new ChatMessage(npc, "Certainly " + (owner.isMale() ? "sir" : "miss"), owner));
		Instance.getDelayedEventHandler().add(new ShortEvent(owner) {
		    public void action() {
			owner.setBusy(false);
			owner.setAccessingBank(true);
			owner.getActionSender().showBank();
		    }
		});
		npc.unblock();
	    }
	});
	npc.blockedBy(player);
    }

}