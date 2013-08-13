package org.moparscape.msc.gs.config

import org.moparscape.msc.gs.model.Mob
import scala.util.Random
import org.moparscape.msc.gs.model.Player

object CombatFormulae {

	private val rand = new Random

	def getNextHit(a : Mob, d : Mob) = {
		if (isSuccessful(a, d)) math.round(rand.nextDouble * maxHit(a)).toInt
		else 0
	}

	private def isSuccessful(a : Mob, d : Mob) = {
		(rand.nextDouble * 100) <= (attack(a) + a.getWeaponAimPoints) / ((attack(a) + a.getWeaponAimPoints + defense(d) + d.getArmourPoints) / 100D)
	}

	private def maxHit(a : Mob) = {
		(strength(a) * ((a.getWeaponPowerPoints * 0.00175) + 0.1) + 1.05).toInt
	}

	private def attack(m : Mob) = (m.getAttack + (m.getCombatStyle match {
		case 0 => 1
		case 2 => 3
		case _ => 0
	})) * (if (m.isPrayerActivated(11)) 1.15 else if (m.isPrayerActivated(5)) 1.1 else if (m.isPrayerActivated(2)) 1.05 else 1)

	private def defense(m : Mob) = (m.getAttack + (m.getCombatStyle match {
		case 0 => 1
		case 3 => 3
		case _ => 0
	})) * (if (m.isPrayerActivated(9)) 1.15 else if (m.isPrayerActivated(3)) 1.1 else if (m.isPrayerActivated(0)) 1.05 else 1)

	private def strength(m : Mob) = (m.getStrength + (m.getCombatStyle match {
		case 0 => 1
		case 1 => 3
		case _ => 0
	})) * (if (m.isPrayerActivated(10)) 1.15 else if (m.isPrayerActivated(4)) 1.1 else if (m.isPrayerActivated(1)) 1.05 else 1)
}