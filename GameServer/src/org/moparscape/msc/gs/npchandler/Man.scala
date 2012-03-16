package org.moparscape.msc.gs.npchandler

import org.moparscape.msc.gs.model.dialog.NpcDialog
import org.moparscape.msc.gs.model.InvItem

/**
 * An example of a 'NpcHandler'.
 */
class Man extends NpcDialog {

	// Reference to self, so we can call it from another dialog
	private val self = this

	// Adds a new dialog to the options
	// The text that shows up on the menu is "I'm going to kill you!"
	this + new NpcDialog("I'm going to kill you!") {

		// Called when dialog begins
		def begin {
			// Takes a pause of 1500ms
			breath
			
			// The Npc says this
			this > "Get away from me..."
			breath
			this > "NOW!"
			breath
			
			// The player says this to the npc
			this < "NEVER!"
			
			// Unblock and remove busy flag
			player.setBusy(false)
			npc.unblock
		}

	}

	// Adds a new dialog to the options
	this + new NpcDialog("Here is 5 gold.") {

		def begin {
			// 5 gold coins
			val gold = new InvItem(10, 5)
			
			// If the player has 5 coins
			if (player.getInventory.contains(gold)) {
				
				// Remove the coins
				player.getInventory.remove(gold)
				
				// Pause for 4500ms
				pause(4500)
				this > "Oh my God! Thank you so much."
				breath
				this > "Here is a reward for your good deed."
				breath
				
				// R2H
				val item = new InvItem(81)
				
				// Give R2H and update inventory
				player.getInventory.add(item)
				player.getActionSender.sendInventory

				// Unblock
				npc.unblock
			} else { // If the player doesn't have enough coins
				this > "..."
				pause(3000)
				this < "Sorry, I don't have enough on me."
				breath
				this > "Well, do you need anything else?"
				
				// Call the original dialog
				// Note: We don't unblock or remove the busy flag
				self.end
			}
			
			// Remove busy flag
			player.setBusy(false)
		}
	}

	override def begin {
		
		// Set busy flag and block
		player.setBusy(true)
		npc.blockedBy(player)
		
		this > "Hello!"
		end
	}
}