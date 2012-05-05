package org.moparscape.msc.gs.event.handler.objectaction
import org.moparscape.msc.gs.model.event._
import org.moparscape.msc.gs.event.handler.objectaction.impl._
import org.moparscape.msc.gs.model.definition.EntityHandler
import scala.collection.JavaConversions._
import org.moparscape.msc.gs.model.definition.entity.GameObjectDef
import org.moparscape.msc.gs.Instance

class ObjectActionManager extends ChainManager[Int, ObjectActionChain, ObjectActionParam](new ObjectActionChain(new MembersObjectAction, new DefaultObjectAction)) {

	override def fire(chainType : ObjectActionChain, param : ObjectActionParam) {
		chainType.trigger(param)
	}

	private implicit def values(l : List[(GameObjectDef, Int)]) : java.util.List[Int] = l.map(_._2)
	private implicit def eventToChain(o : ObjectEvent) = new ObjectActionChain(o)

	override def init {

		val objects = EntityHandler.getGameObjectDefs.zipWithIndex.toList

		// Bind go up
		bind(new ObjectActionChain(new GoUp),
			filterByCommands(objects, "climb up", "climb-up", "go up"))

		// Bind go down
		bind(new ObjectActionChain(new GoDown),
			filterByCommands(objects, "climb down", "climb-down", "go down"))

		// Bind cuboards
		bind(new Cuboard, objects.filter(_._1.name == "cupboard"))

		bind(new RockSlide, 982)
		
		bind(new Hopper, List(52, 173))

	}

	private def filterByCommands(objects : List[(GameObjectDef, Int)], commands : String*) = {
		objects.filter(
			m =>
				commands.contains(m._1.command1) || commands.contains(m._1.command2)
		)
	}

}