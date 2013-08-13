package org.moparscape.msc.gs.service

import scala.collection.JavaConversions.asScalaBuffer
import scala.language.implicitConversions

import org.moparscape.msc.gs.config.{ Config, Formulae }
import org.moparscape.msc.gs.Instance.{ dataStore => ds, getWorld => w }
import org.moparscape.msc.gs.model.{ GameObject, Item, Npc, Point3D, World }
import org.moparscape.msc.gs.model.definition.EntityHandler
import org.moparscape.msc.gs.model.definition.entity.{ GameObjectLoc, ItemLoc, NPCLoc }

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
	def run(_sections : java.util.List[Point3D]) {

		val sections = _sections.toList

		filterP2P(filterOutOfBoundsG(sections, ds.loadGameObjectLocs.toList)).foreach(o =>
			w.registerGameObject(new GameObject(o)))

		var items = filterP2P(filterOutOfBoundsI(sections, ds.loadItemLocs.toList))

		// Remove P2P items that might spawn in F2P areas

		// Checks if the item is P2P
		def isMem(i : ItemLoc) = EntityHandler.getItemDef(i.id).isMembers

		if (!World.isMembers)
			items = items.filterNot(isMem(_))

		items.foreach(i => w.registerItem(new Item(i)))

		filterP2P(filterOutOfBoundsN(sections, ds.loadNPCLocs.toList)).foreach(n =>
			w.registerNpc(new Npc(n)))
	}

	/**
	 * Filters out P2P 'things' when needed, based on their location.
	 */
	private def filterP2P(data : List[Any]) = {
		if (!World.isMembers)
			data.filterNot(o => Formulae.isP2P(Config.f2pWildy, o.asInstanceOf[Object]))
		else data
	}

	private def filterOutOfBoundsI(sections : List[Point3D], data : List[ItemLoc]) = {
		data.filter {
			i =>
				val regionX = i.getX / 48 + 48
				val regionY = ((i.getY % 944) / 48) + 37
				val regionZ = Formulae.getHeight(i.getY)

				find(sections, new Point3D(regionX, regionY, regionZ))
		}
	}

	private def filterOutOfBoundsN(sections : List[Point3D], data : List[NPCLoc]) = {
		data.filter {
			i =>
				val regionX = i.startX / 48 + 48
				val regionY = ((i.startY % 944) / 48) + 37
				val regionZ = Formulae.getHeight(i.startY)

				find(sections, new Point3D(regionX, regionY, regionZ))
		}

	}

	private def filterOutOfBoundsG(sections : List[Point3D], data : List[GameObjectLoc]) = {
		data.filter {
			i =>
				val regionX = i.getX / 48 + 48
				val regionY = ((i.getY % 944) / 48) + 37
				val regionZ = Formulae.getHeight(i.getY)

				find(sections, new Point3D(regionX, regionY, regionZ))
		}
	}

	private def find(sections : List[Point3D], p : Point3D) = sections.find(_ == p) match {
		case Some(x) => true
		case None => false
	}

}