package msc.gs.npchandler;

import msc.gs.Instance;
import msc.gs.event.ShortEvent;
import msc.gs.model.ChatMessage;
import msc.gs.model.InvItem;
import msc.gs.model.MenuHandler;
import msc.gs.model.Npc;
import msc.gs.model.Player;
import msc.gs.model.World;

public class KebabSeller implements NpcHandler {
    /**
     * World instance
     */
    public static final World world = Instance.getWorld();

    public void handleNpc(final Npc npc, Player player) throws Exception {
	player.informOfNpcMessage(new ChatMessage(npc, "Would you like to buy a nice kebab? Only 1 gold", player));
	player.setBusy(true);
	Instance.getDelayedEventHandler().add(new ShortEvent(player) {
	    public void action() {
		owner.setBusy(false);
		String[] options = new String[] { "I think I'll give it a miss", "Yes please" };
		owner.setMenuHandler(new MenuHandler(options) {
		    public void handleReply(final int option, final String reply) {
			if (owner.isBusy()) {
			    return;
			}
			owner.informOfChatMessage(new ChatMessage(owner, reply, npc));
			owner.setBusy(true);
			Instance.getDelayedEventHandler().add(new ShortEvent(owner) {
			    public void action() {
				owner.setBusy(false);
				if (option == 1) {
				    if (owner.getInventory().remove(10, 1) > -1) {
					owner.getActionSender().sendMessage("You buy a kebab");
					owner.getInventory().add(new InvItem(210, 1));
					owner.getActionSender().sendInventory();
					npc.unblock();
				    } else {
					owner.informOfChatMessage(new ChatMessage(owner, "Oops I forgot to bring any money with me", npc));
					owner.setBusy(true);
					Instance.getDelayedEventHandler().add(new ShortEvent(owner) {
					    public void action() {
						owner.setBusy(false);
						owner.informOfNpcMessage(new ChatMessage(npc, "Come back when you have some", owner));
						npc.unblock();
					    }
					});
				    }
				} else {
				    npc.unblock();
				}
			    }
			});
		    }
		});
		owner.getActionSender().sendMenu(options);
	    }
	});
	npc.blockedBy(player);
    }

}