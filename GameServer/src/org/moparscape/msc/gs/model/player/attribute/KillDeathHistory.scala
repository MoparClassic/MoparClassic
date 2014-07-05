package org.moparscape.msc.gs.model.player.attribute

/**
  * A class used to store the kill and death history of an entity.
  *
  * @author Joe Pritzel
  */
class KillDeathHistory(
	var npcKills : Long = 0, var npcDeaths : Long = 0,
	var playerKillsWild : Long = 0, var playerDeathsWild : Long = 0,
	var playerKillsDuel : Long = 0, var playerDeathsDuel : Long = 0) {

	/**
	  * Returns the total kills a player has made.
	  */
	def getTotalKills = npcKills + getPvPKills

	/**
	  * Returns the total deaths a player has had.
	  */
	def getTotalDeaths = npcDeaths + getPvPDeaths

	/**
	  * Returns the total number of PvP kills a player has made.
	  */
	def getPvPKills = playerKillsWild + playerKillsDuel

	/**
	  * Returns the total number of deaths a player has had due to PvP.
	  */
	def getPvPDeaths = playerDeathsWild + playerDeathsDuel
}