package org.moparscape.msc.gs.model.player.attribute

/**
  * A class that implements the Elo system.
  *
  * @author Joe Pritzel
  */
class Elo(var rating : Int, var games : Int) {

	/**
	  * Recalculates both player's elo, assuming the winner is <code>this</code>.
	  */
	def recalculateForWin(opponent : Elo) {
		rating = (rating + getKFactor * (1 - expectedScore(opponent))).round.toInt
		opponent.rating = (opponent.rating + opponent.getKFactor * (0 - opponent.expectedScore(this))).round.toInt
		games += 1
		opponent.games += 1
	}

	private def expectedScore(opponent : Elo) = 1 / (1 + math.pow(10, (opponent.rating - rating) / 400))

	private def getKFactor = {
		if (games < 30) 30
		else if (rating < 2400) 15
		else 16
	}
}