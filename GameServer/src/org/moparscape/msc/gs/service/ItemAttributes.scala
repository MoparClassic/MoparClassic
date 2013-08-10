package org.moparscape.msc.gs.service
import org.moparscape.msc.gs.model.definition.EntityHandler

object ItemAttributes {

	def getItemDef(id : Int) = EntityHandler.getItemDef(id)
	def getItemName(id : Int) = getItemDef(id).getName

	def isWieldable(id : Int) = getWieldable(id) != null

	def getWieldable(id : Int) = EntityHandler.getItemWieldableDef(id)

	def wieldingAffectsItem(id : Int, id2 : Int) = {
		if (isWieldable(id) && isWieldable(id2)) {
			val d = getWieldable(id).getAffectedTypes
			val d2 = getWieldable(id2).getType
			if (d == null)
				false
			d.contains(d2)
		} else false
	}

	def getUnIdentHerbDef(id : Int) = EntityHandler.getItemUnIdentHerbDef(id)

	def isEdible(id : Int) = EntityHandler.getItemEdibleHeals(id) > 0

	def getSmeltingDef(id : Int) = EntityHandler.getItemSmeltingDef(id)

	def getCookingDef(id : Int) = EntityHandler.getItemCookingDef(id)

	def isStackable(id : Int) = EntityHandler.getItemDef(id).isStackable
}