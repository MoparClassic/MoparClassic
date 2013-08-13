package org.moparscape.msc.gs.event.handler.objectaction

import scala.Array.canBuildFrom
import scala.collection.JavaConversions.seqAsJavaList
import scala.language.implicitConversions

import org.moparscape.msc.gs.event.handler.objectaction.impl._
import org.moparscape.msc.gs.model.definition.EntityHandler
import org.moparscape.msc.gs.model.definition.entity.GameObjectDef
import org.moparscape.msc.gs.model.event.ChainManager

class ObjectActionManager extends ChainManager[Int, ObjectActionChain, ObjectActionParam](new ObjectActionChain(new MembersObjectAction, new TelePoint, new DefaultObjectAction)) {

	override def fire(chainType : ObjectActionChain, param : ObjectActionParam) {
		chainType.trigger(param)
	}

	private implicit def values(l : List[(GameObjectDef, Int)]) : java.util.List[Int] = l.map(_._2)
	private implicit def eventToChain(o : ObjectEvent) = new ObjectActionChain(o)
	private implicit def int2List(i : Int) = List(i)

	{
		val objects = EntityHandler.getGameObjectDefs.zipWithIndex.toList

		bind(new Door, 213)

		bind(new Cupboard, objects.filter(_._1.name == "cupboard").map(_._2))

		bind(new ObjectActionChain(new ChestOpen, new OpenOrClose),
			filterByCommands(objects.filter(_._1.name == "Chest"), "open"))

		bind(new ObjectActionChain(new GoUp),
			filterByCommands(objects, "climb up", "climb-up", "go up"))

		bind(new ObjectActionChain(new GoDown),
			filterByCommands(objects, "climb down", "climb-down", "go down"))

		bind(new StealFrom, filterByCommands(objects, "steal from"))

		bind(new SearchForTraps, filterByCommands(objects, "search for traps"))

		bind(new Rest, filterByCommands(objects, "rest"))

		bind(new Hit, filterByCommands(objects, "hit"))

		bind(new Pick, filterByCommands(objects, "pick", "pick banana"))

		bind(new ObjectActionChain(new DamagingApproach, new NormalApproach, new NothingApproach), filterByCommands(objects, "approach"))

		bind(new RockSlide, 982)

		bind(new Hopper, List(52, 173))

		bind(new Fishing, filterByCommands(objects, "lure", "bait", "net", "harpoon", "cage"))

		bind(new Woodcutting, filterByCommands(objects, "chop"))

		bind(new Recharge, filterByCommands(objects, "recharge at"))

		bind(new Board, filterByCommands(objects, "board"))

		bind(new Agility, objects.filter(i => EntityHandler.getAgilityDef(i._2) != null).map(_._2))

		bind(new ShiloCart, 613)

		bind(new GnomeStoneTile, 643)

		bind(new GnomeCaveRoots, List(638, 639))

		bind(new Mining, filterByCommands(objects, "mine", "prospect"))

		// Bind OpenOrClose to all non-bound objects that have the command open or close.
		bind(new OpenOrClose, {

			val map = mapping.map(_._1)
			filterByCommands(objects, "open", "close").filterNot(map.contains)
		})

		val tp = new TelePoint
		this.mapping.foreach {
			t =>
				t._2.addLast(tp)
		}
	}

	private def filterByCommands(objects : List[(GameObjectDef, Int)], commands : String*) = {
		objects.filter(
			m =>
				commands.contains(m._1.command1.toLowerCase) || commands.contains(m._1.command2.toLowerCase)).map(_._2)
	}

}