package org.moparscape.msc.gs.skill.mining

import org.moparscape.msc.gs.model.GameObject
import org.moparscape.msc.gs.model.Player
import org.moparscape.msc.gs.model.definition.EntityHandler
import org.moparscape.msc.gs.model.InvItem
import org.moparscape.msc.gs.Instance
import org.moparscape.msc.gs.config.Formulae
import scala.collection.JavaConversions._
import org.moparscape.msc.gs.model.Bubble
import org.moparscape.msc.gs.event.ShortEvent
import org.moparscape.msc.gs.tools.DataConversions
import org.moparscape.msc.gs.event.SingleEvent

class Ore(p : Player, o : GameObject) {

	private val axes = List(
		(1262, 41, 12),
		(1261, 31, 8),
		(1260, 21, 6),
		(1259, 6, 4),
		(1258, 1, 2),
		(156, 1, -1))

	def mine {
		p.setSkillLoops(0)
		_mine
	}

	private def _mine {
		if (!canMine || !hasOre) return

		if (miningLvl < oreDef().getReqLevel) {
			this > "You need a mining level of " + oreDef().getReqLevel + " to mine this rock."
			return
		}

		if (axe == null) {
			val inInv = axes.filter(i => p.getInventory.contains(i._1))
			if (inInv.size > 0) this > "You need to be level " + inInv.head._2 + " to use this pick"
			else this > "You need a pickaxe to mine this rock."
			return
		}

		p.setBusy(true)
		p.getActionSender.sendSound("mine")
		new Bubble(p, axe._1).broadcast

		this > "You swing your pick at the rock..."

		Instance.getDelayedEventHandler.add(new ShortEvent(p) {
			def action {
				if (Formulae.getOre(oreDef(), miningLvl, axe._1)) {
					if (DataConversions.random(0, 200) == 0) {
						p.incExp(14, 100, true)
						p.getInventory.add(Formulae.getGem)
						Ore.this > "You found a gem!"
					} else {
						p.getInventory.add(oreDef().getOreId)
						Ore.this > "You manage to obtain some " + new InvItem(oreDef(ore).getOreId).getDef.name + "."
						p.incExp(14, oreDef().getExp, true)
						p.getActionSender.sendStat(14)
						val w = Instance.getWorld
						w.delayedSpawnObject(ore.getLoc, oreDef().getRespawnTime * 1000)
						w.registerObject(new GameObject(o.getLocation, 98, o.getDirection, o.getType))
					}
					p.getActionSender.sendInventory
				} else {
					Ore.this > "You only succeed in scratching the rock."
					if (axe._3 - p.getSkillLoops > 0) {
						Instance.getDelayedEventHandler.add(new SingleEvent(p, 500) {
							def action {
								if (!p.isMining || p.inCombat) return
								p.setSkillLoops(p.getSkillLoops + 1)
								_mine
							}
						})
					}
				}
				p.setBusy(false)
			}
		})
	}

	def prospect {
		if (!canMine || !hasOre) return
		this > "This rock contains " + new InvItem(oreDef(ore).getOreId).getDef.name + "."
	}

	private def axe = axes.find(i => p.getInventory.contains(i._1) && miningLvl >= i._2) match {
		case Some(x) => x
		case None => null
	}

	private def miningLvl = p.getCurStat(14)

	private def canMine = !(p.isBusy && !p.inCombat) || !p.withinRange(o, 1)

	private def ore = Instance.getWorld.getTile(o.getX, o.getY).getGameObject

	private def oreDef(o : GameObject = o) = EntityHandler.getObjectMiningDef(o.getID)

	private def hasOre = if (oreDef(ore) == null) {
		this > "There is currently no ore available in this rock."
		false
	} else true

	private def >(msg : String) = p.getActionSender.sendMessage(msg)
}