package org.moparscape.msc.gs.model.player.attribute

class KillDeathHistory(
	var npcKills : Long = 0, var npcDeaths : Long = 0,
	var playerKillsWild : Long = 0, var playerDeathsWild : Long = 0,
	var playerKillsDuel : Long = 0, var playerDeathsDuel : Long = 0)