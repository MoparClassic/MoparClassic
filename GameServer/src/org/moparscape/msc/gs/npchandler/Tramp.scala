package org.moparscape.msc.gs.npchandler
import org.moparscape.msc.gs.model.{ Npc, Player }

class Tramp(_npc : Npc, _player : Player) extends GenericEnd("", _npc, _player) {
	override def begin {

		this > "Hello sir, spare me some money please?"; breath
		this < "No sorry, i'm broke."

		super.begin
	}
}