package org.moparscape.msc.gs.model.extra.elo

class Elo(var rating: Int, var games: Int) {

  def recalculateForWin(opponent: Elo) {
    rating = (rating + getKFactor * (1 - expectedScore(opponent))).round.toInt
    opponent.rating = (opponent.rating + opponent.getKFactor * (0 - opponent.expectedScore(this))).round.toInt
    games += 1
    opponent.games += 1
  }

  def expectedScore(opponent: Elo) = 1 / (1 + math.pow(10, (opponent.rating - rating) / 400))

  def getKFactor = {
    if (games < 30) 30
    else if (rating < 2400) 15
    else 16
  }
}