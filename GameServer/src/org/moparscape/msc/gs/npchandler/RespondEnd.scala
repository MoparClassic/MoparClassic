package org.moparscape.msc.gs.npchandler
import org.moparscape.msc.gs.model.{ Npc, Player }

class RespondEnd(msg : String, resp : Array[String], _npc : Npc, _player : Player) extends GenericEnd(msg, _npc, _player) {
	override def begin {
		resp foreach { resp =>
			this > resp; breath
		}
		super.begin
	}
}