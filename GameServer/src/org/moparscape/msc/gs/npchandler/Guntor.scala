package org.moparscape.msc.gs.npchandler
import org.moparscape.msc.gs.model.dialog.NpcDialog

class Guntor extends NpcDialog {

	override def begin {
		this < "Hello"; breath
		this > "Who are you?"; breath
		this < "I'm a bold adventurer"; breath
		this > "You don't look very strong"; pause(1200)
		exit
	}
	
}
