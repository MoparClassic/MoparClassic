package org.moparscape.msc.gs.npchandler

import org.moparscape.msc.gs.model.dialog.NpcDialog
import org.moparscape.msc.gs.model.{ Npc, Player }

class Transact(msg : String, give : Array[(Int, Int)],
		receive : Array[(Int, Int)], failMsg : Array[String],
		_npc : Npc, _player : Player) extends NpcDialog(msg, _npc, _player) {

	protected var failed = false

	override def begin {
		if (hasItems(give)) {
			give foreach (i => player.getInventory.remove(i._1, i._2))
			receive foreach (i => player.getInventory.add(i._1, i._2))
			breath
			player.getActionSender.sendInventory
			success
		} else {
			failMsg foreach { e => breath; this < e }
			failed = true
			fail
		}
	}

	protected def success {
		exit // Defaults to exit so player's don't get stuck if you don't implement it.
	}

	protected def fail {
		exit // Defaults to exit so player's don't get stuck if you don't implement it.
	}

	private def hasItems(items : Array[(Int, Int)]) = {
		items forall {
			item =>
				if (player.getInventory.countId(item._1) >= item._2) true
				else false
		}
	}

}