package org.moparscape.msc.gs.npchandler
import org.moparscape.msc.gs.model.{ Npc, Player }

class Banker(_npc : Npc, _player : Player) extends GenericEnd("", _npc, _player) {

	override def begin {
		this < "I'd like to access my bank account please"; breath
		this > ("Certainly " + (if (player.isMale()) "sir" else "miss")); breath
		player.setAccessingBank(true)
		player.getActionSender.showBank

		super.begin
	}

}