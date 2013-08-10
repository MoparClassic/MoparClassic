package org.moparscape.msc.gs.npchandler
import org.moparscape.msc.gs.model.dialog.NpcDialog
import org.moparscape.msc.gs.model.InvItem

class BrotherJered extends NpcDialog {
	override def init {
		this + option1
		this + new RespondEnd("Praise be to Saradomin", Array("Yes praise he who brings life to this world"), npc, player)
	}

	override def begin {
		breath
		end
	}

	lazy val option1 = new NpcDialog("What can you do to help a bold adventurer like myself?", npc, player) {
		override def begin {
			breath
			if (player.getInventory.contains(45)) {
				this > "Well I can bless them star of saradomin you have"
				breath
				end
			} else {
				this > "If you have a silver star"; breath
				this > "Which is the holy symbol of Saradomin"; breath
				this > "Then I can bless it"; breath
				this > "Then if you are wearing it"; breath
				this > "It will help you when you are praying"; breath
				player.setBusy(false)
				npc.unblock
			}
		}

		this + new GenericEnd("Yes please", npc, player) {
			override def begin {
				val count = player.getInventory.countId(45)
				val s = if (count > 1) "s" else ""
				for (i <- (0 until count)) player.getInventory.remove(45, 1)
				pause(1500)
				player.getActionSender.sendInventory
				this >> "You give Jered the symbol" + s; pause(1300)
				this >> "Jered closes his eyes and places his hands on the symbol" + s; pause(1300)
				this >> "He softly chants"; pause(800)
				for (i <- (0 until count)) player.getInventory.add(385, count)
				this >> "Jered passes you the holy symbol" + s; pause(800)
				player.getActionSender.sendInventory
			}
		}

		this + new GenericEnd("No thankyou", npc, player)
	}
}