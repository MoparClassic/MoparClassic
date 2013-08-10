package org.moparscape.msc.gs.service
import org.moparscape.msc.gs.model.Player

object AppearanceService {
	def updateWornItems(p : Player) {
		p.updateAppearanceID()
	}
}