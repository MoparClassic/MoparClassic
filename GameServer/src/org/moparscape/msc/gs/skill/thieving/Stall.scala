package org.moparscape.msc.gs.skill.thieving

import org.moparscape.msc.gs.model.ChatMessage
import org.moparscape.msc.gs.model.GameObject
import org.moparscape.msc.gs.model.Player
import org.moparscape.msc.gs.util.Logger
import org.moparscape.msc.gs.model.Bubble
import org.moparscape.msc.gs.Instance
import org.moparscape.msc.gs.event.ShortEvent
import org.moparscape.msc.gs.model.InvItem

class Stall(p : Player, o : GameObject) extends Thieving(p, o) {

	private val stalls = List(
		List(5, 322, 16, 5, 330), // Bakers
		List(20, 323, 24, 11, 200), // Silk
		List(35, 324, 36, 18, 541), // Fur
		List(50, 325, 54, 31, 383), // Silver
		List(65, 326, 81, 45, 707), // Spice
		List(75, 327, 16, 80, 160, 159, 158, 157) // Gem  
	)

	private val stallsNpcs = List(
		List(325, 543, 546, 597, 602), // Baker
		List(-1, -1, -1, -1, -1),
		List(-1, -1, -1, -1, -1),
		List(328, 553, 558, 592, 595), // Silver merchant
		List(329, 542, 547, 588, 593), // Spice merchant
		List(330, 549, 553, 597, 602) // Gem merchant    
	)

	private val caughtMsg = List("Guards! Guards! Im being Robbed!",
		"Help Guards i am being Robbed please help!",
		"Someone help! My items are getting stolen!",
		"You'll wish you never did that, Thief!",
		"You are going to pay for that",
		"$name how could you steal from me? Guards!",
		"$name get your hands out of my stall!",
		"Hey $name thats not yours!",
		"Dont steal from me $name Im going to go tell a mod",
		"Oi! $name you deserve a spanking!")

	private var currStall = -1

	def stealFrom {
		try {
			if (o == null) {
				release
				return
			}

			var exists = false
			for (i <- 0 until stalls.length) {
				if (o.getID == stalls(i)(1)) {
					currStall = i
					exists = true
					if (p.getMaxStat(17) < stalls(i)(0)) {
						this > "Sorry, you need a thieving level of " + stalls(i)(0) + " to steal from that"
						release
						return
					}
				}
			}

			if (!exists) {
				this > "Sorry, this stall does not exist.. contact an admin?"
				Logger.println("Player " + p.getUsername + " found a stall not added, ID: " + o.getID + ", Coords: " + o.getLocation)
				release
				return
			}

			new Bubble(p, 609).broadcast
			this > "You attempt to steal from the " + o.getGameObjectDef.name
			p.setBusy(true)

			Instance.getDelayedEventHandler.add(new ShortEvent(p) {
				def action {
					val w = Instance.getWorld
					p.setSpam(false)
					if (o == null) {
						Stall.this > "There is nothing to steal at this time"
						release
					}

					if (!chanceFormula(stalls(currStall)(0))) {
						Stall.this > "You failed to steal from the " + o.getGameObjectDef.name
						release

						if (rand.nextInt(20) <= 3 && stallsNpcs(currStall)(0) != -1) {
							val d = stallsNpcs(currStall)
							val npc = w.getNpc(d(0), d(1), d(2), d(3), d(4))
							if (npc != null) {
								p.getNpcThief(currStall) = true
								val msg = caughtMsg(rand.nextInt(caughtMsg.size)).replaceAll("$name", p.getUsername)
								p.informOfNpcMessage(new ChatMessage(npc, msg, p))
								npc.resetPath
							}
						}
					} else {
						w.registerGameObject(new GameObject(o.getLocation, 341, o.getDirection, o.getType))
						w.delayedSpawnObject(o.getLoc, stalls(currStall)(3) * 1000)
						Stall.this > "You successfully stole from the " + o.getGameObjectDef.name
						release

						val loot = if (currStall == 5) new InvItem({
							val r = rand.nextInt(100)
							if (r <= 40) 160
							else if (r <= 70) 159
							else if (r <= 90) 158
							else 157
						}, 1)
						else new InvItem(stalls(currStall)(4), 1)
						p.getInventory.add(loot.id, loot.amount, false)
						p.getActionSender.sendInventory
						p.incExp(17, stalls(currStall)(2), true)
						p.getActionSender.sendStat(17)
					}
				}
			})

		} catch {
			case e : Throwable => Logger.error(e.getMessage)
		}
	}
}