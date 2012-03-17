package org.moparscape.msc.gs.npchandler
import org.moparscape.msc.gs.model.{ Npc, Player }

class Thrander(_npc : Npc, _player : Player) extends GenericEnd("", _npc, _player) {
	override def begin {
		this > "Hello I'm Thrander the smith, I'm an expert in armour modification."
		breath
		this > "Give me your armour designed for men and I can convert it"
		breath
		this > "Into something more comfortable for a woman, and vice versa."
		super.begin
	}
}