package org.moparscape.msc.gs.service
import scala.collection.JavaConversions.asScalaBuffer

import org.moparscape.msc.config.{ Formulae, Config }
import org.moparscape.msc.gs.Instance.{ getWorld => w, getDataStore => ds }
import org.moparscape.msc.gs.model.definition.entity.{ NPCLoc, ItemLoc, GameObjectLoc }
import org.moparscape.msc.gs.model.definition.EntityHandler
import org.moparscape.msc.gs.model.{ World, Npc, Item, GameObject }

/**
  *
  * This service populates the world.
  *
  * @author Joe Pritzel
  */
object WorldPopulationService {

	private implicit def toGOL(o : Any) = o.asInstanceOf[GameObjectLoc]
	private implicit def toNL(o : Any) = o.asInstanceOf[NPCLoc]
	private implicit def toIL(o : Any) = o.asInstanceOf[ItemLoc]
	private implicit def toScalaList(o : java.util.List[_]) = o.toList

	/**
	  * Populates the world.
	  */
	def run {

		filter(ds.loadGameObjectLocs).foreach(o =>
			w.registerGameObject(new GameObject(o)))

		var items = filter(ds.loadItemLocs)

		// Remove P2P items that might spawn in F2P areas

			// Checks if the item is P2P
			def isMem(i : ItemLoc) = EntityHandler.getItemDef(i.id).isMembers

		if (!World.isMembers)
			items = items.filterNot(isMem(_))

		items.foreach(i => w.registerItem(new Item(i)))

		filter(ds.loadNPCLocs).foreach(n =>
			w.registerNpc(new Npc(n)))
	}

	/**
	  * Filters out P2P 'things' when needed, based on their location.
	  */
	private def filter(data : List[_]) = {
		if (!World.isMembers)
			data.filterNot(o => Formulae.isP2P(Config.f2pWildy, o.asInstanceOf[Object]))
		else data
	}

}