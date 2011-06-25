package org.moparscape.msc.gs.plugins.dependencies;

import java.util.ArrayList;

import org.moparscape.msc.gs.model.ChatMessage;
import org.moparscape.msc.gs.model.Mob;
import org.moparscape.msc.gs.model.Npc;
import org.moparscape.msc.gs.model.Player;

public class NpcScript {

	public void shootPlayer(Npc n, Player player, int damage, int sprite) {

		Mob affectedMob = player;
		affectedMob.setLastDamage(damage);
		int newHp = affectedMob.getHits() - damage;
		affectedMob.setHits(newHp);
		ArrayList<Player> playersToInform = new ArrayList<Player>();
		playersToInform.addAll(n.getViewArea().getPlayersInView());
		playersToInform.addAll(affectedMob.getViewArea().getPlayersInView());
		for (Player p : playersToInform) {
			p.informOfModifiedHits(affectedMob);
		}
		if (affectedMob instanceof Player) {
			Player affectedPlayer = (Player) affectedMob;
			affectedPlayer.getActionSender().sendStat(3);
		}
	}

	public void sendNpcChat(Npc n, Player p, String msg, boolean area) {
		p.informOfNpcMessage(new ChatMessage(n, msg, p));
		if (area) {
			for (Player pl : p.getViewArea().getPlayersInView()) {
				if (pl.equals(p))
					continue;
				pl.getActionSender().sendMessage(
						"@yel@" + n.getDef().name + ": " + msg);
			}
		}
	}

}
