package org.moparscape.msc.gs.service
import org.moparscape.msc.gs.model.{ Player, Npc }
import org.moparscape.msc.gs.model.dialog.NpcDialog
import java.util.concurrent.Executors
import org.moparscape.msc.gs.npchandler._

object DialogService {

	private var mapping = Map[Int, NpcDialog]()
	private val executor = Executors.newCachedThreadPool

	addMapping(125, new Aggie)
	addMapping(33, new Apothecary)
	addMapping(164, new Bananas)

	addMapping(Array(95, 224, 268, 485, 540, 617, 792), new Bankers(null, null))

	addMapping(90, new KebabSeller)
	addMapping(339, new MakeOverMage)
	addMapping(93, new MonkHealer(null, null))

	addMapping(
		Array(51, 183, 112, 55, 56, 12, 82, 83, 44, 233, 87,
			235, 88, 105, 106, 130, 143, 145, 146, 168, 169, 185,
			186, 222, 223, 371, 391, 528, 58, 54, 173, 250, 155, 289,
			149, 131, 129, 167, 141, 115, 69, 59, 48, 228, 230, 101,
			773, 75, 157, 228, 1, 84, 85, 103, 165, 336, 337, 331,
			330, 328, 325, 329, 514, 522, 620, 282, 793, 113, 156,
			297, 788, 779, 269),
		new ShopKeeper
	)

	addMapping(172, new Tanner)
	addMapping(160, new Thrander(null, null))
	addMapping(28, new Tramp(null, null))

	def addMapping(id : Int, dialog : NpcDialog) {
		mapping = mapping + ((id, dialog))
	}

	def addMapping(id : Array[Int], dialog : NpcDialog) {
		id foreach (e => mapping = mapping + ((e, dialog)))
	}

	def talk(npc : Npc, player : Player) {
		mapping.get(npc.getID) match {
			case Some(dialog) => {
				invoke(dialog.clone, npc, player)
			}
			case None => invoke(new DefaultNpc(npc, player), npc, player)
		}

	}

	def invoke(dialog : NpcDialog, npc : Npc, player : Player) {
		executor.execute(new Runnable {
			override def run {
				dialog.npc = npc
				dialog.player = player
				dialog.init
				// Set busy flag and block
				player.setBusy(true)
				npc.blockedBy(player)
				dialog.begin
			}
		})
	}
}