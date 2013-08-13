package org.moparscape.msc.gs.npchandler
import org.moparscape.msc.gs.model.{ Npc, Player }

class Thrander(_npc : Npc, _player : Player) extends RespondEnd("",
	Array(
		"Hello I'm Thrander the smith, I'm an expert in armour modification.",
		"Give me your armour designed for men and I can convert it",
		"Into something more comfortable for a woman, and vice versa."
	), _npc, _player) {
}