package org.moparscape.msc.gs.npchandler
import org.moparscape.msc.gs.model.dialog.NpcDialog
import org.moparscape.msc.gs.model.{ Npc, Player }

class GenericEnd(msg : String, _npc : Npc, _player : Player) extends NpcDialog(msg, _npc, _player) {
	override def begin {
		breath
		exit
	}
}