package org.moparscape.msc.gs.event.handler.objectaction.impl

import scala.collection.JavaConversions.asScalaBuffer

import org.moparscape.msc.gs.config.Formulae
import org.moparscape.msc.gs.event.handler.objectaction.ObjectEvent
import org.moparscape.msc.gs.event.{ EventHandler, DelayedEvent }
import org.moparscape.msc.gs.model.definition.skill.AgilityDef
import org.moparscape.msc.gs.model.definition.EntityHandler
import org.moparscape.msc.gs.model.Path
import org.moparscape.msc.gs.states.Action

class Agility extends ObjectEvent with MembersOnly {

	private val AGILITY = 16

	override def fire = {
		if (this.p2pCheck(player)) false
		val d = EntityHandler.getAgilityDef(o.getID)

		if (player.getCurStat(AGILITY) < d.getReqLevel) {
			player.getActionSender.sendMessage("You need an agility level of " + d.getReqLevel + " to try this obstacle")
			false
		}

		player.setBusy(true)
		if (Formulae.getHeight(d.getY) == Formulae.getHeight(d.getToY))
			player.setPath(new Path(d.getX, d.getY, d.getToX, d.getToY, true))
		else
			player.teleport(d.getToX, d.getToY, false)
		EventHandler.add(new Event(d))
		false
	}

	private class Event(d : AgilityDef) extends DelayedEvent(player, 100) {

		private var testedFail = false

		override def run {
			try {
				if (d.canFail && !testedFail &&
					owner.getX >= ((d.getToX + d.getX) / 2) &&
					owner.getY >= ((d.getToY + d.getY) / 2)) {

					val damage = Formulae.failObstacle(owner, d.getReqLevel)
					if (damage != -1) {
						owner.getActionSender.sendMessage("You slip off the obstacle!")
						owner.teleport(d.getFailX, d.getFailY, false)
						owner.setLastDamage(damage)
						val newHp = owner.getHits - damage
						owner.setHits(newHp)

						owner.getViewArea.getPlayersInView.foreach(_.informOfModifiedHits(owner))
						stop
						return
					}
					testedFail = true
				}

				if (owner.getX == d.getToX && owner.getY == d.getToY) {
					owner.getActionSender.sendMessage("You successfully make it to the other side of the obstacle")
					owner.incExp(16, d.getExp, true)
					owner.getActionSender.sendStat(16)

					var course = EntityHandler.getAgilityCourseDef(o.getID)

					if (owner.getAgilityCourseDef != null) {
						course = owner.getAgilityCourseDef
						if (d.getOrder == (owner.getCurrentCourseObject + 1)) {
							if (o.getID == course.getEndID && o.getX == course.getEndX && o.getY == course.getEndY) {
								owner.getActionSender.sendMessage("You have completed the " + course.getName + " obstacle course!")
								owner.incExp(16, course.getExp, true)
								owner.setAgilityCourseDef(null)
								owner.setCurrentCourseObject(-1)
							} else owner.setCurrentCourseObject(d.getOrder)
						} else {
							owner.setAgilityCourseDef(null)
							owner.setCurrentCourseObject(-1)
						}
					} else {
						if (course != null) {
							owner.setAgilityCourseDef(course)
							owner.setCurrentCourseObject(d.getOrder)
						}
					}

					owner.getActionSender.sendStat(16)
					stop
				}
			} finally {
				owner.setBusy(false)
				owner.setStatus(Action.IDLE)
			}
		}
	}

}