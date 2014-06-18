package org.moparscape.msc.gs.model.player.attribute

import org.moparscape.msc.gs.quest._
import scala.collection.JavaConversions._

/**
  * A class that contains a list of quests and their progress for a player.
  *
  * @author Joe Pritzel
  */
class Quests {
	val quests : java.util.List[Quest] = List()
	var points = calculatePoints

	/**
	  * Calculates the number of quest points and returns the value.
	  */
	def calculatePoints = quests.foldLeft(0)((tot, q) => if (q.stage == q.finished) tot + q.worth else tot)

	/**
	  * Sets the quest id to the given stage.
	  */
	def set(id : Int, stage : Int) = quests.find(q => q.id == id) match {
		case Some(x) => x.stage = stage
		case None =>
	}

	/**
	  * Returns the stage at which the given quest is, or null if the quest was not found.
	  */
	def get(id : Int) = quests.find(q => q.id == id) match {
		case Some(x) => x
		case None => null
	}
}