package org.moparscape.msc.gs.npchandler;

import org.moparscape.msc.config.Formulae;
import org.moparscape.msc.gs.Instance;
import org.moparscape.msc.gs.event.ShortEvent;
import org.moparscape.msc.gs.model.ChatMessage;
import org.moparscape.msc.gs.model.Npc;
import org.moparscape.msc.gs.model.Player;
import org.moparscape.msc.gs.model.World;

/**
 * 
 * @author xEnt
 * 
 */
public class OtherNPC implements NpcHandler {

	public static final World world = Instance.getWorld();

		

	public void handleNpc(final Npc npc, Player player) throws Exception {

		if (npc.getID() == 28) {
			player.informOfNpcMessage(new ChatMessage(npc,
					"Hello sir, spare me some money please?", player));
			Instance.getDelayedEventHandler().add(new ShortEvent(player) {
				public void action() {
					owner.informOfChatMessage(new ChatMessage(owner,
							"No sorry, i'm broke.", npc));
					owner.setBusy(false);
					npc.setBusy(false);
					npc.unblock();
					return;
				}
			});
		} else  {

			player.getActionSender().sendMessage("The " + npc.getDef().name + " does not appear interested in talking");
		}
	}

}