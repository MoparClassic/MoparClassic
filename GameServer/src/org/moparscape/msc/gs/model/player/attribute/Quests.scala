package org.moparscape.msc.gs.model.player.attribute

import org.moparscape.msc.gs.quest._
import scala.collection.JavaConversions._

class Quests {
	val quests : java.util.List[Quest] = List()
	var points = calculatePoints

	def calculatePoints = quests.foldLeft(0)((tot, q) => if (q.stage == q.finished) tot + q.worth else tot)

	def set(id : Int, stage : Int) = quests.find(q => q.id == id) match {
		case Some(x) => x.stage = stage
		case None =>
	}

	def get(id : Int) = quests.find(q => q.id == id) match {
		case Some(x) => x
		case None => null
	}
}