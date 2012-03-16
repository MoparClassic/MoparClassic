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
	addMapping(172, new Tanner)
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