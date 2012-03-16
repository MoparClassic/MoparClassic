package org.moparscape.msc.gs.service
import org.moparscape.msc.gs.model.{ Player, Npc }
import org.moparscape.msc.gs.model.dialog.NpcDialog
import java.util.concurrent.Executors
import org.moparscape.msc.gs.npchandler._

object DialogService {

	private var mapping = Map[Int, NpcDialog]()
	private val executor = Executors.newCachedThreadPool
	
	addMapping(11, new Man)
	addMapping(125, new Aggie)

	def addMapping(id : Int, dialog : NpcDialog) {
		mapping = mapping + ((id, dialog))
	}

	def talk(npc : Npc, player : Player) {
		mapping.get(npc.getID) match {
			case Some(dialog) => {
				dialog.npc = npc
				dialog.player = player
				invoke(dialog.clone, npc, player)
			}
			case None =>
		}

	}

	def invoke(dialog : NpcDialog, npc : Npc, player : Player) {
		executor.execute(new Runnable {
			override def run {
				dialog.npc = npc
				dialog.player = player
				dialog.begin
			}
		})
	}
}