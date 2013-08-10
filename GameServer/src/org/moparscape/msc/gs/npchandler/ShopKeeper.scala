package org.moparscape.msc.gs.npchandler
import org.moparscape.msc.gs.model.dialog.NpcDialog
import org.moparscape.msc.gs.Instance

class ShopKeeper extends NpcDialog {

	override def init {
		val shop = Instance.getWorld.getShop(npc.getLocation)
		var options = shop.options

		this + new GenericEnd(options(0), npc, player) {
			override def begin {
				breath
				player.setAccessingShop(shop)
				player.getActionSender.showShop(shop)
				shop.addPlayer(player)
				super.begin
			}
		}

		options = options.drop(1)

		options foreach (e => this + new GenericEnd(e, npc, player))
	}

	override def begin {
		val shop = Instance.getWorld.getShop(npc.getLocation)

		this > shop.greeting

		breath

		end
	}
}