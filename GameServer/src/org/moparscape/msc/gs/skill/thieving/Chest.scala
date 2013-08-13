package org.moparscape.msc.gs.skill.thieving

import org.moparscape.msc.gs.model.GameObject
import org.moparscape.msc.gs.model.Player
import scala.collection.JavaConversions._
import org.moparscape.msc.gs.Instance
import org.moparscape.msc.gs.event.MiniEvent
import org.moparscape.msc.gs.util.Logger
import org.moparscape.msc.gs.model.Bubble
import org.moparscape.msc.gs.model.InvItem

class Chest(p : Player, o : GameObject) extends Thieving(p, o) {

	def open {
		val damage = p.getCurStat(3) / 9
		this > "You have activated a trap on the chest"
		p.setCurStat(3, p.getCurStat(3) - damage)
		p.getActionSender.sendStat(3)

		p.getViewArea.getPlayersInView.foreach(_.informOfModifiedHits(p))

		if (p.getCurStat(3) - damage <= 0) p.killedBy(null)

		Instance.getDelayedEventHandler.add(new MiniEvent(p, 1200) {
			def action {
				release
			}
		})

	}

	private val chests = List(
		List(340, 59, 250, 45000, 619, 2), // Blood rune chest.
		List(334, 18, 8, 10000, 10, 50), // Next 2 nature chest.
		List(336, 72, 500, 180000, 546, 1, 154, 1, 160, 1, 10, 1000), // Good chest,  ardy...
		List(339) // Dont add anything to this, its a dummy chest.
	)

	def stealFrom {
		if (o == null) {
			release
			return
		}

		if (o.getID == 338) {
			this > "It looks like that chest has already been looted."
			release
			return
		}

		val chest = try ((for (chest <- chests; if (o.getID == chest(1))) yield chest).head) catch { case e : NoSuchElementException => null }
		if (chest == null) {
			release
			this > "This chest has not yet been added to the chest thieving list."
			Logger.println("Player " + p.getUsername + " found a chest not added, ID: " + o.getID + ", Coords: " + o.getLocation)
			return
		}

		if (p.getMaxStat(17) < chest(1)) {
			release
			this > "You are not high enough level to steal from this chest"
			return
		}

		this > "You search for traps on the chest"
		p.setBusy(true)

		val w = Instance.getWorld
		Instance.getDelayedEventHandler.add(new MiniEvent(p, 300) {
			def action {
				Chest.this > "You find a trap on the chest..."
				new Bubble(p, 549).broadcast
				Instance.getDelayedEventHandler.add(new MiniEvent(p, 1000) {
					def action {
						Chest.this > "You disarm the trap"
						Instance.getDelayedEventHandler.add(new MiniEvent(p, 1000) {
							def action {
								Chest.this > "You open the chest"
								try {

									w.registerGameObject(new GameObject(o.getLocation, 339,
										o.getDirection, o.getType))
									w.delayedSpawnObject(o.getLoc, 900)
								} catch {
									case e : Exception => e.printStackTrace
								}

								Instance.getDelayedEventHandler.add(new MiniEvent(p, 1200) {
									def action {
										try {
											w.registerGameObject(new GameObject(o.getLocation, 338,
												o.getDirection, o.getType));
											w.delayedSpawnObject(o.getLoc, delay)
										} catch {
											case e : Exception => e.printStackTrace
										}

										Chest.this > "You find treasure inside!"
										for (i <- 4 until chest.size - 1 by 2) {
											val item = new InvItem(chest(i), chest(i + 1))
											p.getInventory.add(item.id, item.amount)
										}

										if (chest(0) == 340) { // Blood rune chest
											Chest.this > "You triggered the trap!"
											p.teleport(612, 568, true)
										}

										p.getActionSender.sendInventory
										p.incExp(17, chest(2), true)
										p.getActionSender.sendStat(17)
										release
									}
								})

							}
						})
					}
				})
			}

		})
	}
}