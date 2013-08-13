package org.moparscape.msc.gs.skill.thieving

import org.moparscape.msc.gs.model.GameObject
import org.moparscape.msc.gs.model.Player
import org.moparscape.msc.gs.util.Logger
import org.moparscape.msc.gs.model.Bubble
import org.moparscape.msc.gs.Instance
import org.moparscape.msc.gs.event.ShortEvent

class Door(p : Player, o : GameObject) extends Thieving(p, o) {

	private val doors = List(
		List(586, 581, 585, 581, 10, 13), // Nat rune West
		// house (In)
		List(585, 581, 586, 581, 10, 13), // Nat rune West house (Out)
		List(539, 598, 539, 599, 10, 13), // Nat Rune East House (Out)
		List(539, 599, 539, 598, 10, 13), // Nat Rune East House (Out)
		List(609, 1547, 609, 1548, 61, 43), // Paladin Door (In)
		List(609, 1548, 609, 1547, 61, 43), // Paladin Door (Out)
		List(537, 3425, 536, 3425, 31, 25), // Ardy Door
		List(536, 3425, 537, 3425, 31, 25), // Ardy Door
		List(617, 556, 617, 555, 46, 37), // Blood rune door
		List(617, 555, 617, 556, 46, 37), // Blood rune door
		List(593, 3590, 593, 3589, 61, 41), // yanile door
		List(593, 3589, 593, 3590, 61, 41), // yanile door
		List(266, 100, 266, 99, 39, 35), // pirate doors wildy
		List(266, 99, 266, 100, 39, 35), // pirate doors wildy
		List(160, 103, 160, 102, 30, 28), // Axe hut wildy
		List(160, 102, 160, 103, 30, 28), // Axe hut wildy
		List(581, 580, 580, 580, 10, 30),
		List(580, 580, 581, 580, 10, 30),
		List(538, 592, 538, 591, 8, 10), // Some ardy door
		List(538, 591, 538, 592, 8, 10) // some ardy door
	)

	def pickLock {
		try {
			if (p == null || p.isBusy || p.inCombat || o == null) {
				release
				return
			}

			p.setBusy(true)

			val door = try (doors.filter(d => d(0) == p.getX && d(1) == p.getY).head) catch { case e : NoSuchElementException => null }

			if (door == null) {
				this > "This door has not been added"
				Logger.println("Player " + p.getUsername + " found a door(lockpick) not added, ID: " + o.getID + ", Coords: " + o.getLocation)
				release
				return
			}

			if (p.getMaxStat(17) < door(4)) {
				this > "Sorry, you don't have a high enough thieving level to unlock this"
				release
				return
			}

			new Bubble(p, 714).broadcast
			this > "You attempt to pick the lock on the " + o.getDoorDef.name

			Instance.getDelayedEventHandler.add(new ShortEvent(p) {
				def action {
					if (!chanceFormula(door(4))) {
						Door.this > "You failed to unlock the door"
						release
					} else {
						Door.this > "You successfully unlocked the " + o.getDoorDef.name
						try {
							p.getActionSender.sendSound("opendoor")
							val w = Instance.getWorld
							w.registerGameObject(new GameObject(o.getLocation, 11, o.getDirection, o.getType))
							w.delayedSpawnObject(o.getLoc, 1000)
						} catch {
							case e : Throwable => e.printStackTrace
						}

						p.incExp(17, door(5), true)
						p.getActionSender.sendStat(17)
						release
						p.teleport(door(2), door(3), false)
					}
				}
			})
		} catch {
			case e : Throwable => e.printStackTrace
		}
	}
}