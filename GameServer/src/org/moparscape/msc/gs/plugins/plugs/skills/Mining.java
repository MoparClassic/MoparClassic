package org.moparscape.msc.gs.plugins.plugs.skills;

import org.moparscape.msc.config.Formulae;
import org.moparscape.msc.gs.Instance;
import org.moparscape.msc.gs.core.GameEngine;
import org.moparscape.msc.gs.event.ShortEvent;
import org.moparscape.msc.gs.event.SingleEvent;
import org.moparscape.msc.gs.external.EntityHandler;
import org.moparscape.msc.gs.external.ObjectMiningDef;
import org.moparscape.msc.gs.model.Bubble;
import org.moparscape.msc.gs.model.GameObject;
import org.moparscape.msc.gs.model.InvItem;
import org.moparscape.msc.gs.model.Player;
import org.moparscape.msc.gs.plugins.listeners.ObjectListener;
import org.moparscape.msc.gs.tools.DataConversions;

public class Mining implements ObjectListener {

	@Override
	public int[] getAssociatedIDS() {
		// rock ID's
		return new int[] { 176, 100, 101, 102, 103, 104, 105, 106, 107, 108,
				109, 110, 111, 112, 113, 114, 115, 195, 196, 210, 211 };
	}

	@Override
	public boolean onObjectAction(final GameObject object, String command,
			Player owner) {

		owner.setSkillLoops(0);
		handleMining(object, owner, owner.getClick());
		return true;
	}

	// just picks the best pickaxe the player can use.
	public int getAxe(Player p) {
		int lv = p.getCurStat(14);
		for (int i = 0; i < Formulae.miningAxeIDs.length; i++) {
			if (p.getInventory().countId(Formulae.miningAxeIDs[i]) > 0) {
				if (lv >= Formulae.miningAxeLvls[i]) {
					return Formulae.miningAxeIDs[i];
				}
			}
		}
		return -1;
	}

	public void handleMining(final GameObject object, Player owner, int click) {
		if (owner.isBusy() && !owner.inCombat()) {
			return;
		}
		if (!owner.withinRange(object, 1))
			return;
		final GameObject newobject = Instance.getWorld()
				.getTile(object.getX(), object.getY()).getGameObject();
		final ObjectMiningDef def = EntityHandler.getObjectMiningDef(newobject
				.getID());
		if (def == null || def.getRespawnTime() < 1) {
			owner.getActionSender().sendMessage(
					"There is currently no ore available in this rock.");
			return;
		}
		final InvItem ore = new InvItem(def.getOreId());
		if (owner.getClick() == 1) {
			owner.getActionSender().sendMessage(
					"This rock contains " + ore.getDef().getName() + ".");
			return;
		}

		if (owner.getCurStat(14) < def.getReqLevel()) {
			owner.getActionSender().sendMessage(
					"You need a mining level of " + def.getReqLevel()
							+ " to mine this rock.");
			return;
		}
		int axeId = getAxe(owner);

		if (axeId < 0) {
			owner.getActionSender().sendMessage(
					"You need a pickaxe to mine this rock.");
			return;
		}
		final int axeID = axeId;
		int retrytimes = -1;
		final int swings = owner.getSkillLoops();
		final int mineLvl = owner.getCurStat(14);
		int reqlvl = 1;
		switch (axeID) {
		case 1258:
			retrytimes = 2;
			break;
		case 1259:
			retrytimes = 4;
			reqlvl = 6;
			break;
		case 1260:
			retrytimes = 6;
			reqlvl = 21;
			break;
		case 1261:
			retrytimes = 8;
			reqlvl = 31;
			break;
		case 1262:
			retrytimes = 12;
			reqlvl = 41;
			break;

		}

		if (reqlvl > mineLvl) {
			owner.getActionSender().sendMessage(
					"You need to be level " + reqlvl + " to use this pick.");
			return;
		}
		owner.setBusy(true);

		owner.getActionSender().sendSound("mine");
		Bubble bubble = new Bubble(owner, axeId);
		for (Player p : owner.getViewArea().getPlayersInView()) {
			p.informOfBubble(bubble);
		}

		final int retrytime = retrytimes;
		owner.setLastMineTimer(GameEngine.getTime());
		owner.getActionSender().sendMessage(
				"You swing your pick at the rock...");
		Instance.getDelayedEventHandler().add(new ShortEvent(owner) {
			public void action() {
				if (Formulae.getOre(def, owner.getCurStat(14), axeID)) {
					if (DataConversions.random(0, 200) == 0) {
						InvItem gem = new InvItem(Formulae.getGem(), 1);
						owner.incExp(14, 100, true);
						owner.getInventory().add(gem);
						owner.getActionSender().sendMessage("You found a gem!");
					} else {
						owner.getInventory().add(ore);
						owner.getActionSender().sendMessage(
								"You manage to obtain some "
										+ ore.getDef().getName() + ".");
						owner.incExp(14, def.getExp(), true);
						owner.getActionSender().sendStat(14);
						world.registerGameObject(new GameObject(object
								.getLocation(), 98, object.getDirection(),
								object.getType()));
						world.delayedSpawnObject(newobject.getLoc(),
								def.getRespawnTime() * 1000);
					}
					owner.isMining(false);
					owner.setSkillLoops(0);
					owner.getActionSender().sendInventory();
				} else {
					boolean retry = false;
					if (retrytime - swings > 0)
						retry = true;
					owner.getActionSender().sendMessage(
							"You only succeed in scratching the rock.");
					if (retry) {
						world.getDelayedEventHandler().add(
								new SingleEvent(owner, 500) {
									public void action() {
										owner.setSkillLoops(swings + 1);
										handleMining(object, owner,
												owner.getClick());
									}
								});
					}
					if (!retry) {
						owner.isMining(false);
						owner.setSkillLoops(0);
					}
				}
				owner.setBusy(false);
			}
		});
	}

	@Override
	public boolean onObjectCreation(GameObject obj, Player player) {

		return true;
	}

	@Override
	public boolean onObjectRemoval(GameObject obj) {

		return true;
	}

}
