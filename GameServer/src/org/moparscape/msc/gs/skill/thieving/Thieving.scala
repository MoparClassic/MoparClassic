package org.moparscape.msc.gs.skill.thieving

import org.moparscape.msc.gs.model.Player
import org.moparscape.msc.gs.model.GameObject
import scala.util.Random

class Thieving(p : Player, o : GameObject) {

	protected val rand = new Random

	protected def chanceFormula(lvl : Int) = {
		val chance = List(27, 33, 35, 37, 40, 43, 47, 51, 54, 58, 62, 66, 71, 74, 78, 81, 84, 88, 93, 95)
		val maxLvl = List(1, 5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 55, 60, 65, 70, 75, 80, 85, 90, 95, 100)
		val diff = p.getMaxStat(17) - lvl

		rand.nextInt(100) < maxLvl.indexOf(maxLvl.filter(diff < _).max)
	}

	protected def >(msg : String) {
		p.getActionSender.sendMessage(msg)
	}

	protected def release {
		p.setSpam(false)
		p.setBusy(false)
	}
}