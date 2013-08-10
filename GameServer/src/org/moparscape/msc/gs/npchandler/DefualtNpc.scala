package org.moparscape.msc.gs.npchandler
import org.moparscape.msc.gs.model.{ Npc, Player }

class DefaultNpc(_npc : Npc, _player : Player) extends GenericEnd("", _npc, _player) {

	override def begin {

		this >> ("The " + npc.getDef.name + " does not appear interested in talking")

		super.begin
	}
}