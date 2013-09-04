package org.moparscape.msc.gs.npchandler
import org.moparscape.msc.gs.model.dialog.NpcDialog
import org.moparscape.msc.gs.model.{ Npc, Player }
import org.moparscape.msc.gs.tools.DataConversions;

class Man(_npc : Npc, _player : Player) extends NpcDialog("", _npc, _player) {

	override def begin {
		val greetings = "Hello, How's it going?"
		val diag = DataConversions.random(0, 6)
		diag match {
			case 0 =>
				this < greetings; breath
				this >> "The " + npc.getDef.name + " ignores you"
			case 1 =>
				this < greetings; breath
				this > "I'm fine"; breath
				this > "How are you?"; breath
				this < "Very well, thank you"; pause(1200)
			case 2 =>
				this < greetings; breath
				this > "Who are you?"; breath
				this < "I am a bold adventurer"; breath
				this > "A very noble profession"; pause(1200)
			case 3 =>
				this < greetings; breath
				this > "Get out of my way"; breath
				this > "I'm in a hurry"; pause(1200)
			case 4 =>
				this < greetings; breath
				this > "I'm a little worried"; breath
				this > "I've heard there's lots of people going about"; breath
				this > "killing citizens at random"; pause(1200)
			case 5 =>
				this < "Hello, Do you wish to trade?"; breath
				this > "No, I have nothing I wish to get rid of"; breath
				this > "If you want some trading,"; breath
				this > "there are plenty of shops and market stalls around though"; pause(1200)
			case 6 =>
				this < "Hello, I'm in search of enemies to kill"; breath
				this > "I've heard there are many fearsome creatures under the ground"; pause(1200)
		}
		exit
	}
}
