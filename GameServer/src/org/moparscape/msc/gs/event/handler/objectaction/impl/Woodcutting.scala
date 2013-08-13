package org.moparscape.msc.gs.event.handler.objectaction.impl

import java.lang.{ IllegalArgumentException => IAE }

import org.moparscape.msc.gs.config.Formulae
import org.moparscape.msc.gs.event.handler.objectaction.ObjectEvent
import org.moparscape.msc.gs.event.EventHandler
import org.moparscape.msc.gs.model.definition.skill.ObjectWoodcuttingDef
import org.moparscape.msc.gs.model.definition.EntityHandler
import org.moparscape.msc.gs.model.{ GameObject, Bubble }
import org.moparscape.msc.gs.service.ItemAttributes
import org.moparscape.msc.gs.tools.DataConversions
import org.moparscape.msc.gs.Instance

class Woodcutting extends ObjectEvent {

	private val WOODCUTTING = 8

	override def fire : Boolean = {
		val d = EntityHandler.getObjectWoodcuttingDef(o.getID())
		if (d == null) true
		else {
			try { chop(d) } catch { case e : IAE => player.getActionSender.sendMessage(e.getMessage) }
			false
		}
	}

	private def chop(d : ObjectWoodcuttingDef) {
		if (player.isBusy || !player.withinRange(o, 2)) return
		if (player.getCurStat(WOODCUTTING) < d.getReqLevel) throw new IAE("You need a woodcutting level of " + d.getReqLevel + " to axe this tree.")

		val axeId = {
			Formulae.woodcuttingAxeIDs.find(i => player.getInventory.contains(i)) match {
				case Some(x) => x
				case None => throw new IAE("You need an axe to chop this tree down.")
			}
		}

		player.setBusy(true)

		new Bubble(player, axeId).broadcast

		player.getActionSender.sendMessage("You swing your " + ItemAttributes.getItemName(axeId) + " at the tree...")

		EventHandler.addShort {
			try {
				if (Formulae.getLog(d, player.getCurStat(WOODCUTTING), axeId)) {
					player.getActionSender.sendMessage("You get some wood.")
					player.getInventory.doThenSend(player.getInventory.add(d.getLogId))

					player.incExp(WOODCUTTING, d.getExp, true)
					player.getActionSender.sendStat(WOODCUTTING)

					if (DataConversions.random(1, 100) <= d.getFell) {

						Instance.getWorld.registerGameObject(
							new GameObject(o.getLocation, 4, o.getDirection, o.getType)
						)

						Instance.getWorld.delayedSpawnObject(o.getLoc, d.getRespawnTime * 1000)
					}

				} else player.getActionSender.sendMessage("You slip and fail to hit the tree.")
			} finally {
				player.setBusy(false)
			}
		}
	}

}