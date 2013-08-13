package org.moparscape.msc.gs.model.dialog
import org.moparscape.msc.gs.model.Player
import org.moparscape.msc.gs.model.Npc
import org.moparscape.msc.gs.model.ChatMessage
import org.moparscape.msc.gs.model.MenuHandler
import org.moparscape.msc.gs.service.DialogService

abstract class NpcDialog(val optionText : String = "", var npc : Npc = null, var player : Player = null) extends Cloneable {

	var dialogOptions = Array[NpcDialog]()

	final def +(dialog : NpcDialog) {
		dialogOptions = dialogOptions :+ dialog
	}

	def init {}

	final def exit {
		player.setBusy(false)
		npc.unblock
	}

	final def >(msg : String) {
		if (player != null)
			player.informOfNpcMessage(new ChatMessage(npc, msg, player))
	}

	final def >>(msg : String) {
		if (player != null)
			player.getActionSender.sendMessage(msg)
	}

	final def <(msg : String) {
		if (player != null)
			player.informOfChatMessage(new ChatMessage(player, msg, npc))
	}

	final def end {
		player.setBusy(false)
		var opts = Array[String]()
		dialogOptions.foreach(o => if (o.optionText.length > 0) opts = opts :+ o.optionText)
		player.setMenuHandler(new MenuHandler(opts) {
			override def handleReply(option : Int, reply : String) {
				if (owner.isBusy) {
					return
				}
				owner.informOfChatMessage(new ChatMessage(owner, reply,
					npc))
				owner.setBusy(true)
				dialogOptions.find(e => e.optionText == reply) match {
					case Some(dialog) => DialogService.invoke(dialog, npc, owner)
					case None =>
				}
			}

			override def abort {
				npc.unblock()
			}
		})
		player.getActionSender().sendMenu(opts)
	}

	final def breath {
		Thread.sleep(1800)
	}

	final def pause(time : Long) {
		Thread.sleep(time)
	}

	def begin;

	override def clone = {
		val c = super.clone.asInstanceOf[NpcDialog]
		c.npc = this.npc
		c.player = this.player
		c
	}
}