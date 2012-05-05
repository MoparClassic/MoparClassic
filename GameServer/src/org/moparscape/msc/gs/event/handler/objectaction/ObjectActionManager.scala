package org.moparscape.msc.gs.event.handler.objectaction
import org.moparscape.msc.gs.model.event._
import org.moparscape.msc.gs.event.handler.objectaction.impl._
import org.moparscape.msc.gs.model.definition.EntityHandler
import scala.collection.JavaConversions._
import org.moparscape.msc.gs.model.definition.entity.GameObjectDef
import org.moparscape.msc.gs.Instance

class ObjectActionManager extends ChainManager[Int, ObjectActionChain, ObjectActionParam] {

	override def fire(chainType : ObjectActionChain, param : ObjectActionParam) {
		chainType.trigger(param)
	}

	private implicit def values(l : List[(GameObjectDef, Int)]) : java.util.List[Int] = l.map(_._2)
	private implicit def eventToChain(o : ObjectEvent) = new ObjectActionChain(o)

	override def init {

		val objects = EntityHandler.getGameObjectDefs.zipWithIndex.toList
		//val locs = Instance.getWorld.
		
		//def byLoc(x:Int, y:Int) = objects.filter(o)

		// Bind go up
		bind(new ObjectActionChain(new GoUp),
			objects.filter(m => m == "climb up" || m == "climb-up" || m == "go up"))

		// Bind go down
		bind(new ObjectActionChain(new GoDown),
			objects.filter(m => m == "climb down" || m == "climb-down" || m == "go down"))

		bind(new RockSlide, 982)

	}

}