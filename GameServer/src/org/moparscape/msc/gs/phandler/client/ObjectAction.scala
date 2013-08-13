package org.moparscape.msc.gs.phandler.client

import org.moparscape.msc.gs.phandler.PacketHandler
import org.moparscape.msc.gs.connection.Packet
import org.apache.mina.common.IoSession
import org.moparscape.msc.gs.event.handler.objectaction.ObjectActionManager
import org.moparscape.msc.gs.model.Player
import org.moparscape.msc.gs.connection.RSCPacket
import org.moparscape.msc.gs.states.Action
import org.moparscape.msc.gs.model.World
import org.moparscape.msc.gs.event.handler.objectaction.ObjectActionParam
import org.moparscape.msc.gs.Instance
import org.moparscape.msc.gs.model.snapshot.Activity
import org.moparscape.msc.gs.event.WalkToObjectEvent

object ObjectAction {
	val oam = new ObjectActionManager
}

class ObjectAction extends PacketHandler {

	override def handlePacket(p : Packet, session : IoSession) {
		val player = session.getAttachment.asInstanceOf[Player]
		val opcode = p.asInstanceOf[RSCPacket].getID

		if (player.isBusy) {
			if (player.getStatus != Action.AGILITYING) player.resetPath
			return
		}

		player.resetAll

		val t = Instance.getWorld.getTile(p.readShort, p.readShort)
		val o = t.getGameObject
		val click = if (opcode == 51) 0 else 1
		player.setClick(click)

		if (o == null) {
			t.cleanItself
			player.setSuspicious(true)
			return
		}

		Instance.getWorld.addEntryToSnapshots(new Activity(
			player.getUsername,
			player.getUsername + " clicked on a object (" + o.getID + ") at: "
				+ player.getX + "/" + player.getY + "|" + o.getX + "/" + o.getY
		))

		player.setStatus(Action.USING_OBJECT)

		Instance.getDelayedEventHandler.add(new WalkToObjectEvent(player, o, false) {
			override def arrived {
				if (player.isBusy || player.isRanging || !player.nextTo(o) || player.getStatus != Action.USING_OBJECT)
					return

				Instance.getWorld.addEntryToSnapshots(new Activity(
					player.getUsername,
					player.getUsername + " used an Object (" + o.getID + ") @ "
						+ o.getX + ", " + o.getY)
				)
				player.resetAll
				ObjectAction.oam.trigger(o.getID, new ObjectActionParam(player, o, click))
			}
		})

	}

}

