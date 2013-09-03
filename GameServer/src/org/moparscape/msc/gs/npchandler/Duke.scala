package org.moparscape.msc.gs.npchandler
import org.moparscape.msc.gs.model.dialog.NpcDialog
import org.moparscape.msc.gs.model.InvItem

class Duke extends NpcDialog {

	override def init {
		this + option1
		this + option2
		this + option3
	}

	override def begin {
		this > "Hello, welcome to my castle"; breath
		end
	}

	lazy val option1 = new RespondEnd("Where I can find money",
			Array("I've heard that the blacksmiths are prosperous among the peasantry",
			"Maybe you could try your hand at that"), npc, player)

	lazy val option2 = new RespondEnd("Have you any quests for me?",
			Array("All is well for me"), npc, player)
			
	lazy val option3 = new Transact("I seek a shield that will protect me fro a dragon breath",
		Array[(Int, Int)](), Array((420, 1)), Array[String](), npc, player) {
		override def success {
			this > "A knight going on a dragon quest hmm?"; breath
			this > "A most worthy cause"; breath
			this > "Guard this well my friend"; pause(1500)
			this >> "The duke hands you a shield"
			super.success
		}
	}

}
