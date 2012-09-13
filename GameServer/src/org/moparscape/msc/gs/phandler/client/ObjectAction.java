package org.moparscape.msc.gs.phandler.client;

import org.moparscape.msc.gs.event.handler.objectaction.ObjectActionManager;
import org.moparscape.msc.gs.event.handler.objectaction.ObjectActionParam;

import org.apache.mina.common.IoSession;
import org.moparscape.msc.gs.Instance;
import org.moparscape.msc.gs.connection.Packet;
import org.moparscape.msc.gs.connection.RSCPacket;
import org.moparscape.msc.gs.event.WalkToObjectEvent;
import org.moparscape.msc.gs.model.GameObject;
import org.moparscape.msc.gs.model.Player;
import org.moparscape.msc.gs.model.World;
import org.moparscape.msc.gs.model.landscape.ActiveTile;
import org.moparscape.msc.gs.model.snapshot.Activity;
import org.moparscape.msc.gs.phandler.PacketHandler;
import org.moparscape.msc.gs.states.Action;

public class ObjectAction implements PacketHandler {
	/**
	 * World instance
	 */
	public static final World world = Instance.getWorld();

	private static final ObjectActionManager oam = new ObjectActionManager();

	public void handlePacket(Packet p, IoSession session) {
		Player player = (Player) session.getAttachment();
		int pID = ((RSCPacket) p).getID();
		if (player.isBusy()) {
			if (player.getStatus() != Action.AGILITYING)
				player.resetPath();

			return;
		}

		player.resetAll();
		ActiveTile t = world.getTile(p.readShort(), p.readShort());
		final GameObject object = t.getGameObject();
		final int click = pID == 51 ? 0 : 1;
		player.setClick(click);
		if (object == null) {
			t.cleanItself();
			player.setSuspiciousPlayer(true);
			return;
		}
		world.addEntryToSnapshots(new Activity(player.getUsername(), player
				.getUsername()
				+ " clicked on a object ("
				+ object.getID()
				+ ") at: "
				+ player.getX()
				+ "/"
				+ player.getY()
				+ "|"
				+ object.getX() + "/" + object.getY()));

		player.setStatus(Action.USING_OBJECT);
		Instance.getDelayedEventHandler().add(
				new WalkToObjectEvent(player, object, false) {
					public void arrived() {
						if (owner.isBusy() || owner.isRanging()
								|| !owner.nextTo(object)
								|| owner.getStatus() != Action.USING_OBJECT) {
							return;
						}
						world.addEntryToSnapshots(new Activity(owner
								.getUsername(), owner.getUsername()
								+ " used an Object (" + object.getID() + ") @ "
								+ object.getX() + ", " + object.getY()));
						owner.resetAll();
						oam.trigger(object.getID(), new ObjectActionParam(
								owner, object, click));
						return;
						/*
						 * case 643: // Gnome
						 * tree stone if (object.getX() != 416 || object.getY()
						 * != 161) { return; }// getCurStat(14
						 * owner.setBusy(true); owner.getActionSender()
						 * .sendMessage(
						 * "You twist the stone tile to one side");
						 * Instance.getDelayedEventHandler().add( new
						 * ShortEvent(owner) { public void action() {
						 * owner.getActionSender() .sendMessage(
						 * "It reveals a ladder, you climb down");
						 * owner.teleport(703, 3284, false);
						 * owner.setBusy(false); } }); break; case 638: // First
						 * roots in gnome cave if (object.getX() != 701 ||
						 * object.getY() != 3280) { return; } // door
						 * owner.setBusy(true);
						 * owner.getActionSender().sendMessage(
						 * "You push the roots");
						 * Instance.getDelayedEventHandler().add( new
						 * ShortEvent(owner) { public void action() {
						 * owner.getActionSender() .sendMessage(
						 * "They wrap around you and drag you forwards");
						 * owner.teleport(701, 3278, false);
						 * owner.setBusy(false); } }); case 639: // Second roots
						 * in gnome cave if (object.getX() != 701 ||
						 * object.getY() != 3279) { return; }
						 * owner.setBusy(true);
						 * owner.getActionSender().sendMessage(
						 * "You push the roots");
						 * Instance.getDelayedEventHandler().add( new
						 * ShortEvent(owner) { public void action() {
						 * owner.getActionSender() .sendMessage(
						 * "They wrap around you and drag you forwards");
						 * owner.teleport(701, 3281, false);
						 * owner.setBusy(false); } }); break; default:
						 * owner.getActionSender().sendMessage(
						 * "Nothing interesting happens."); return; } } } catch
						 * (Exception e) { e.printStackTrace(); }
						 */

					}
				});
	}
}
