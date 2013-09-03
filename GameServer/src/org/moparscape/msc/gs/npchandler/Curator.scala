package org.moparscape.msc.gs.npchandler
import org.moparscape.msc.gs.model.dialog.NpcDialog

class Curator extends NpcDialog {

	override def init {
		this + option1
		this + option2
	}

	override def begin {
		this > "Welcome to the Museum of Varrock"; breath
		end
	}

	lazy val option1 = new RespondEnd("Have you any interesting news?",
			Array("No, I'm only interested in old stuff"), npc, player)

	lazy val option2 = new GenericEnd("Do you know where I could find any treasure?",
			npc, player) {
		override def begin {
			breath
			this > "This museum is full of treasure"; breath
			this < "No, I meant treasure for me"; breath
			this > "Any treasures this museum knows about"; breath
			this > "It aquires"
			exit
		}
	}	

}
