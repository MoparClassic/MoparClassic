package org.moparscape.msc.gs.npchandler

import org.moparscape.msc.gs.model.dialog.NpcDialog
import org.moparscape.msc.gs.model.{ InvItem, Npc, Player }

class Baraek extends NpcDialog {

	override def init {
		val greeting = "Hello, I am in search of a quest"
		val response = "Sorry kiddo, I'm a fur trader not a damsel in distress"
		if (player.getInventory.contains(146)) {
			this + buy
			this + sell
			this + new RespondEnd(greeting, Array(response), npc, player)
		} else {
			this + buy
			this + new RespondEnd(greeting, Array(response), npc, player)
		}
	}

	override def begin {
		end
	}

	lazy val buy = new NpcDialog("Can you sell me some furs?", npc, player) {

		override def begin {
			breath
			this > "That'll be 20 gold"; breath
			end
		}

		this + new Transact("Yea, okay here you go", Array((10, 20)),
			Array((146, 1)), Array("What? You don't have enough money"), npc, player) {
			override def success {
				this >> "You buy a fur from Baraek"
				super.success
			}
			override def fail {
				this > "Then it's your loss mate"; breath
				this < "Ah well never mind"; pause(1200)
				super.fail
			}
		}

		this + new RespondEnd("20 gold coins that's an outrage",
			Array("I can't go any cheaper than that mate",
				"I have a family to feed"), npc, player)
	}

	lazy val sell = new NpcDialog("Would you like to buy my fur?") {
		override def begin {
			breath
			this > "Lets have a look at it"; breath
			this >> "Baraek examines a fur"; breath
			this > "It's not in the best of condition"; breath
			this > "I guess I could give you 12 coins to take it off your hands"; breath
			end
		}

		this + new GenericEnd("Yeah that'll do", npc, player) {
			override def begin {
				breath
				while (hasFur == 0) {
					player.getInventory.remove(146, 1)
					player.getActionSender.sendInventory
					this >> "You give the fur to Baraek"
					pause(1500)
					this >> "And he gives you twelve coins each"
					pause(1500)
					player.getInventory.add(10, 12)
					player.getActionSender.sendInventory
				}
				if (hasFur == 1) {
					this >> "You have run out of fur"
				}
				super.begin
			}

			private def hasFur = {
				if (player.getInventory.countId(146) < 1) 1
				else 0
			}
		}

		this + new RespondEnd("I think I'll keep hold of it actually",
			Array("Oh ok, Didn't want it anyway"), npc, player)

	}
}