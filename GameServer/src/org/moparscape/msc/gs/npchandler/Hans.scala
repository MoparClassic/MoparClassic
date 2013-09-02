package org.moparscape.msc.gs.npchandler
import org.moparscape.msc.gs.model.dialog.NpcDialog
import org.moparscape.msc.gs.model.InvItem

class Hans extends NpcDialog {

	override def init {
		this + option1
		this + option2
		this + option3
	}

	override def begin {
		this > "Hello, what are you doing here?"; breath
		end
	}

	lazy val option1 = new RespondEnd("I'm looking for whoever is in charge of this place",
			Array("The person in charge here is Duke Horacio", 
			"You can usually find him upstairs in this castle"), npc, player)

	lazy val option2 = new RespondEnd("I have come to kill everyone in this castle!",
			Array("Help! Help!"), npc, player)
			
	lazy val option3 = new RespondEnd("I don't know. I'm lost. Where am I?",
			Array("You are at the Lumbridge Castle"), npc, player)

}
