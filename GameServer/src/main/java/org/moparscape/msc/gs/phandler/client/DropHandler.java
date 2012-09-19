package org.moparscape.msc.gs.phandler.client;

import org.apache.mina.common.IoSession;
import org.moparscape.msc.gs.Instance;
import org.moparscape.msc.gs.connection.Packet;
import org.moparscape.msc.gs.event.DelayedEvent;
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
		final InvItem item = player.getInventory().get(idx);
		if (item == null) {
			player.setSuspiciousPlayer(true);
			return;
		}
		if (player.isPMod() && !player.isMod())
			return;
		if (item.getDef().isMembers() && !World.isMembers()) {
			player.getActionSender().sendMessage(
					"This feature is only avaliable on a members server");
			return;
		}

		player.setStatus(Action.DROPPING_GITEM);
		Instance.getDelayedEventHandler().add(new DelayedEvent(player, 500) {
			public void run() {
				if (owner.isBusy() || !owner.getInventory().contains(item)
						|| owner.getStatus() != Action.DROPPING_GITEM) {
					matchRunning = false;
					return;
				}
				if (owner.hasMoved()) {
					this.stop();
					return;
				}
				world.addEntryToSnapshots(new Activity(owner.getUsername(),
						owner.getUsername() + " dropped ID: " + item.getID()
								+ " amount: " + item.getAmount() + " at: "
								+ owner.getX() + "/" + owner.getY()));

				owner.getActionSender().sendSound("dropobject");
				owner.getInventory().remove(item);
				owner.getActionSender().sendInventory();
				world.registerItem(new Item(item.getID(), owner.getX(), owner
						.getY(), item.getAmount(), owner));
				matchRunning = false;
			}
		});
	}
}
