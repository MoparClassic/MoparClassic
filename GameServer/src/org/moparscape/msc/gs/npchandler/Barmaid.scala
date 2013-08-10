package org.moparscape.msc.gs.npchandler
import org.moparscape.msc.gs.model.dialog.NpcDialog
import org.moparscape.msc.gs.model.{ InvItem, Npc, Player }

class Barmaid extends NpcDialog {

	override def init {
		this + new BuyAle(267, 2, "One Asgarnian Ale please", "n Asgarnian ale", npc, player)
		this + new BuyAle(268, 2, "I'll try the mind bomb", " pint of Wizard's Mind Bomb", npc, player)
		this + new BuyAle(269, 3, "Can i have a dwarven stout?", " pint of Dwarven Stout", npc, player)
		this + new GenericEnd("I don't feel like any of those", npc, player)
	}

	override def begin {
		this < "Hi, what ales are you serving?"; breath
		this > "Well you can either have a nice asgarnian ale or a wizards mind bomb"; breath
		this > "Or a dwarven stout"; breath

		end
	}

	private class BuyAle(id : Int, cost : Int, msg : String, msg1 : String, _npc : Npc, _player : Player)
			extends Transact(msg, Array(10 -> cost), Array(id -> 1), Array("Oh dear. I don't seem to have enough money"), _npc, _player) {

		override def begin {
			breath
			this > "That'll be " + cost + " gold"; breath
			super.begin
		}

		override def success {
			this >> "You buy a " + msg1
			super.success
		}

	}
}