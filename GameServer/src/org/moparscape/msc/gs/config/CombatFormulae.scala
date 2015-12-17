package org.moparscape.msc.gs.config

import org.moparscape.msc.gs.model.Mob
import org.moparscape.msc.gs.model.Player
import java.security.SecureRandom

/**
  * The combat formula used to calculate melee damage dealt from one <code>Mob</code> to another.
  * @author object - Devised formula.
  * @author Joe Pritzel - Implemented and very slightly modified the formula to account for additional things.
  */
object CombatFormulae {

	/**
	  * An instance of a secure random number generator to prevent people from
	  * finding the seed and using it to their advantage on servers with a small
	  * player base.
	  */
	private val rand = new SecureRandom

	/**
	  * @return Returns an int representing how much damage the first <code>Mob</code> should do to the other.
	  */
	def getNextHit(a : Mob, d : Mob) = {
		if (isSuccessful(a, d)) math.round(rand.nextDouble * maxHit(a)).toInt
		else 0
	}

	/**
	  * Determines if the first <code>Mob</code> is successful in hitting the second.
	  */
	private def isSuccessful(a : Mob, d : Mob) = {
		(rand.nextDouble * 100) <= (attack(a) + a.getWeaponAimPoints) / ((attack(a) + a.getWeaponAimPoints + defense(d) + d.getArmourPoints) / 100D)
	}

	/**
	  * Determines the maximum damage the given <code>Mob</code> can do.
	  */
	private def maxHit(a : Mob) = {
		(strength(a) * ((a.getWeaponPowerPoints * 0.00175) + 0.1) + 1.05).toInt
	}

	/**
	  * Calculates the adjusted attack power of the given <code>Mob</code>.
	  */
	private def attack(m : Mob) = (m.getAttack + (m.getCombatStyle match {
		case 0 => 1
		case 2 => 3
		case _ => 0
	})) * (if (m.isPrayerActivated(11)) 1.15 else if (m.isPrayerActivated(5)) 1.1 else if (m.isPrayerActivated(2)) 1.05 else 1)

	/**
	  * Calculates the adjusted defense power of the given <code>Mob</code>.
	  */
	private def defense(m : Mob) = (m.getAttack + (m.getCombatStyle match {
		case 0 => 1
		case 3 => 3
		case _ => 0
	})) * (if (m.isPrayerActivated(9)) 1.15 else if (m.isPrayerActivated(3)) 1.1 else if (m.isPrayerActivated(0)) 1.05 else 1)

	/**
	  * Calculates the adjusted strength power of the given <code>Mob</code>.
	  */
	private def strength(m : Mob) = (m.getStrength + (m.getCombatStyle match {
		case 0 => 1
		case 1 => 3
		case _ => 0
	})) * (if (m.isPrayerActivated(10)) 1.15 else if (m.isPrayerActivated(4)) 1.1 else if (m.isPrayerActivated(1)) 1.05 else 1)
}