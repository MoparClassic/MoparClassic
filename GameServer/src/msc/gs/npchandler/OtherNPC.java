package msc.gs.npchandler;

import msc.config.Formulae;
import msc.gs.Instance;
import msc.gs.event.ShortEvent;
import msc.gs.model.ChatMessage;
import msc.gs.model.Npc;
import msc.gs.model.Player;
import msc.gs.model.World;

/**
 * 
 * @author xEnt
 * 
 */
public class OtherNPC implements NpcHandler {

    public static final World world = Instance.getWorld();

    String[][] chats = { { "I have nothing to say, leave me be!", "Ok sorry to bother you" }, { "Hi there, do you have a quest for me?", "No, I have no need of your time." }, { "Can you tell me where I can find catherby", "I'm sorry, but I am not familiar with this area either" }, { "I'm not your buddy, pal", "I'm not your pal, guy." }, { "Be gone, ye haughty, swill-fed baggage!", "Uh, okay." }, { "I've lost my marbles!", "Okay I'll just leave you alone then Tootles." }, { "I'm captain jack sparrow, leader of the black perl!", "really? thats so 3 years ago.." }, { "Hi, are you selling any godswords??", "What the hell are you doing here mate?" }, { "This great town needs a makeover don't you think?", "I like it the way it is" }, { "Huh was that you talkin to me?", "No, of course not." }, { "Your name is -name- huh? i have heard of a legend by that name", "Clearly your not mistaken" }, { "-name- please go away", "Fine" } };

    public void handleNpc(final Npc npc, Player player) throws Exception {

	if (npc.getID() == 28) {
	    player.informOfNpcMessage(new ChatMessage(npc, "Hello sir, spare me some money please?", player));
	    Instance.getDelayedEventHandler().add(new ShortEvent(player) {
		public void action() {
		    owner.informOfChatMessage(new ChatMessage(owner, "No sorry, i'm broke.", npc));
		    owner.setBusy(false);
		    npc.setBusy(false);
		    npc.unblock();
		    return;
		}
	    });
	} else if (!npc.getDef().isAttackable()) {

	    /**
	     * ALL NPC's will get this, random chats.
	     */
	    int rnd = Formulae.Rand(0, chats.length);
	    player.lastRandom = rnd;
	    String chat = chats[rnd][0];
	    chat = chat.replace("-name-", player.getUsername());
	    player.informOfNpcMessage(new ChatMessage(npc, chat, player));
	    Instance.getDelayedEventHandler().add(new ShortEvent(player) {
		public void action() {
		    String chat2 = chats[owner.lastRandom][1];
		    chat2 = chat2.replace("-name-", owner.getUsername());
		    owner.informOfChatMessage(new ChatMessage(owner, chat2, npc));
		    owner.setBusy(false);
		    npc.setBusy(false);
		    npc.unblock();
		    return;
		}
	    });
	}
    }

}