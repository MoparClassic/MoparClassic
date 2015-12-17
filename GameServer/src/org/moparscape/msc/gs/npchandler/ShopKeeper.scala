package org.moparscape.msc.gs.npchandler
import org.moparscape.msc.gs.model.dialog.NpcDialog
import org.moparscape.msc.gs.Instance
import org.moparscape.msc.gs.util.Logger

class ShopKeeper extends NpcDialog {

  override def init {
    val shop = Instance.getWorld.getShop(npc.getLocation)
    var options = shop.options
    this + new GenericEnd(options(0).split("\n")(0), npc, player) {
      override def begin {
        breath
        options(0) split "\n" drop 1 foreach { text => this > text; breath }
        player.setAccessingShop(shop)
        player.getActionSender.showShop(shop)
        shop.addPlayer(player)
        super.begin
      }
    }

    options drop 1 foreach (e => this + new GenericEnd(e, npc, player) {
      override def begin {
        breath
        this.optionText split "\n" drop 1 foreach { text => this > text; breath }
        super.begin
      }
    })
  }

  override def begin {
    val shop = Instance.getWorld.getShop(npc.getLocation)

    shop.greeting filterNot ("" == _) split "\n" foreach { text => this > text; breath }

    end
  }
}