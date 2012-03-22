package org.moparscape.msc.gs.service
import org.moparscape.msc.gs.model.definition.EntityHandler

object ItemAttributes {
	def isWieldable(id : Int) = getWieldable(id) != null

	def getWieldable(id : Int) = EntityHandler.getItemWieldableDef(id)

	def wieldingAffectsItem(id : Int, id2 : Int) = {
		if (!isWieldable(id) || !isWieldable(id2))
			false
		val d = getWieldable(id)
		val d2 = getWieldable(id2)
		d.getAffectedTypes.contains(d2.getType)
	}

	def getUnIdentHerbDef(id : Int) = EntityHandler.getItemUnIdentHerbDef(id)

	def isEdible(id : Int) = EntityHandler.getItemEdibleHeals(id) > 0

	def getSmeltingDef(id : Int) = EntityHandler.getItemSmeltingDef(id)

	def getCookingDef(id : Int) = EntityHandler.getItemCookingDef(id)
}