package org.moparscape.msc.gs.phandler.client;

import org.apache.mina.common.IoSession;
import org.moparscape.msc.gs.Instance;
import org.moparscape.msc.gs.connection.Packet;
import org.moparscape.msc.gs.event.DelayedEvent;
import org.moparscape.msc.gs.event.SingleEvent;
import org.moparscape.msc.gs.model.InvItem;
import org.moparscape.msc.gs.model.Item;
import org.moparscape.msc.gs.model.Player;
import org.moparscape.msc.gs.model.World;
import org.moparscape.msc.gs.model.snapshot.Activity;
import org.moparscape.msc.gs.phandler.PacketHandler;
import org.moparscape.msc.gs.states.Action;

public class DropHandler implements PacketHandler {
	/**
	 * World instance
	 */
	public static final World world = Instance.getWorld();

	public void handlePacket(Packet p, IoSession session) throws Exception {
		Player player = (Player) session.getAttachment();
		if (player.isBusy()) {
			player.resetPath();
			return;
		}
		player.resetAll();
		final int idx = (int) p.readShort();
		if (idx < 0 || idx >= player.getInventory().size()) {
			player.setSuspiciousPlayer(true);
			return;
		}
		final InvItem item = player.getInventory().getSlot(idx);
		if (item == null) {
			player.setSuspiciousPlayer(true);
			return;
		}
		if (player.isPMod() && !player.isMod())
			return;

		// drop item after a path has finished
		if (player.pathHandler != null && !player.pathHandler.finishedPath()) {
			waitAndDrop(player, item);
		} else {
			drop(player, item);
		}

	}

	public void waitAndDrop(final Player player, final InvItem item) {
		Instance.getDelayedEventHandler().add(new SingleEvent(player, 500) {

			@Override
			public void action() {

				if (owner.dropTickCount > 20) { // 10 seconds they are allowed
												// to walk for. anything longer
												// won't drop.
					owner.dropTickCount = 0;
					stop();
				} else {
					owner.dropTickCount++;
					if (owner.pathHandler != null
							&& !owner.pathHandler.finishedPath()) {
						waitAndDrop(owner, item);
					} else {
						drop(owner, item);
					}
				}

			}
		});

	}

	public void drop(Player player, final InvItem item) {
		player.setStatus(Action.DROPPING_GITEM);
		Instance.getDelayedEventHandler().add(new DelayedEvent(player, 500) {
			public void run() {
				if (owner.isBusy() || !owner.getInventory().contains(item.id)
						|| owner.getStatus() != Action.DROPPING_GITEM) {
					matchRunning = false;
					return;
				}
				if (owner.hasMoved()) {
					this.stop();
					return;
				}
				world.addEntryToSnapshots(new Activity(owner.getUsername(),
						owner.getUsername() + " dropped ID: " + item.id
								+ " amount: " + item.amount + " at: "
								+ owner.getX() + "/" + owner.getY()));

				owner.getActionSender().sendSound("dropobject");
				owner.getInventory().remove(item.id, item.amount, false);
				owner.getActionSender().sendInventory();
				if (item.getDef().isMembers() && !World.isMembers()) {
					owner.getActionSender().sendMessage(
							"The members item vanishes!");
					return;
				}
				world.registerItem(new Item(item.id, owner.getX(),
						owner.getY(), item.amount, owner));
				matchRunning = false;
			}
		});
	}
}