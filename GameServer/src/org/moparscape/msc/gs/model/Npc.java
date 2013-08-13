package org.moparscape.msc.gs.model;

import java.util.ArrayList;
import java.util.List;

import org.moparscape.msc.gs.Instance;
import org.moparscape.msc.gs.config.Constants;
import org.moparscape.msc.gs.config.Formulae;
import org.moparscape.msc.gs.core.GameEngine;
import org.moparscape.msc.gs.event.DelayedEvent;
import org.moparscape.msc.gs.event.FightEvent;
import org.moparscape.msc.gs.model.definition.EntityHandler;
import org.moparscape.msc.gs.model.definition.entity.ItemDropDef;
import org.moparscape.msc.gs.model.definition.entity.NPCDef;
import org.moparscape.msc.gs.model.definition.entity.NPCLoc;
import org.moparscape.msc.gs.model.landscape.ActiveTile;
import org.moparscape.msc.gs.states.Action;
import org.moparscape.msc.gs.states.CombatState;
import org.moparscape.msc.gs.tools.DataConversions;
import org.moparscape.msc.gs.util.Logger;

public class Npc extends Mob {

	private int stage = 0;

	public int getStage() {
		return stage;
	}

	public void setStage(int stage) {
		this.stage = stage;
	}

	public boolean isSpecial() {
		return special;
	}

	public void setSpecial(boolean special) {
		this.special = special;
	}

	public int getItemid() {
		return itemid;
	}

	public void setItemid(int itemid) {
		this.itemid = itemid;
	}

	public int getExp() {
		return exp;
	}

	public void setExp(int exp) {
		this.exp = exp;
	}

	public boolean isHasArmor() {
		return hasArmor;
	}

	public void setHasArmor(boolean hasArmor) {
		this.hasArmor = hasArmor;
	}

	public boolean isUndead() {
		return undead;
	}

	public void setUndead(boolean undead) {
		this.undead = undead;
	}

	public boolean isRan() {
		return ran;
	}

	private boolean ran = false;

	/**
	 * World instance
	 */
	private static final World world = Instance.getWorld();
	/**
	 * The player currently blocking this npc
	 */
	private Player blocker = null;
	/**
	 * DelayedEvent used for unblocking an npc after set time
	 */
	private DelayedEvent chaseTimeout = null;
	/**
	 * Player (if any) that this npc is chasing
	 */
	private Player chasing = null;
	public boolean confused = false;
	/**
	 * The npcs hitpoints
	 */
	private int curHits;
	public boolean cursed = false;
	/**
	 * The definition of this npc
	 */
	private NPCDef def;
	private Syndicate syndicate = new Syndicate();
	private boolean goingToAttack = false;
	/**
	 * The location of this npc
	 */
	private NPCLoc loc;

	public boolean hasRan() {
		return ran;
	}

	public void setRan(boolean ran) {
		this.ran = ran;
	}

	public Player getBlocker() {
		return blocker;
	}

	public void setBlocker(Player blocker) {
		this.blocker = blocker;
	}

	public DelayedEvent getChaseTimeout() {
		return chaseTimeout;
	}

	public void setChaseTimeout(DelayedEvent chaseTimeout) {
		this.chaseTimeout = chaseTimeout;
	}

	public boolean isConfused() {
		return confused;
	}

	public void setConfused(boolean confused) {
		this.confused = confused;
	}

	public int getCurHits() {
		return curHits;
	}

	public void setCurHits(int curHits) {
		this.curHits = curHits;
	}

	public boolean isCursed() {
		return cursed;
	}

	public void setCursed(boolean cursed) {
		this.cursed = cursed;
	}

	public boolean isGoingToAttack() {
		return goingToAttack;
	}

	public void setGoingToAttack(boolean goingToAttack) {
		this.goingToAttack = goingToAttack;
	}

	public boolean isShouldRespawn() {
		return shouldRespawn;
	}

	public void setShouldRespawn(boolean shouldRespawn) {
		this.shouldRespawn = shouldRespawn;
	}

	public boolean isWeakend() {
		return weakend;
	}

	public void setWeakend(boolean weakend) {
		this.weakend = weakend;
	}

	public void setDef(NPCDef def) {
		this.def = def;
	}

	public void setLoc(NPCLoc loc) {
		this.loc = loc;
	}

	/**
	 * Should this npc respawn once it has been killed?
	 **/
	private boolean shouldRespawn = true;

	public boolean weakend = false;
	public boolean special = false;
	public int itemid = -1;
	public int exp = -1; // used for events.

	public Npc(int id, int startX, int startY, int minX, int maxX, int minY,
			int maxY) {
		this(new NPCLoc(id, startX, startY, minX, maxX, minY, maxY));
	}

	public Npc(NPCLoc loc) {
		for (int i : Constants.GameServer.UNDEAD_NPCS) {
			if (loc.getId() == i) {
				this.undead = true;
			}
		}
		for (int i : Constants.GameServer.ARMOR_NPCS) {
			if (loc.getId() == i) {
				this.hasArmor = true;
			}
		}

		def = EntityHandler.getNpcDef(loc.getId());
		curHits = def.getHits();
		this.loc = loc;
		super.setID(loc.getId());
		super.setLocation(Point.location(loc.startX(), loc.startY()), true);
		super.setCombatLevel(Formulae.getCombatLevel(def.getAtt(),
				def.getDef(), def.getStr(), def.getHits(), 0, 0, 0));
		if (this.loc.getId() == 189 || this.loc.getId() == 53) {
			this.def.aggressive = true;
		}
	}

	public Syndicate getSyndicate() {
		return syndicate;
	}

	public void setSyndicate(Syndicate syndicate) {
		this.syndicate = syndicate;
	}

	public synchronized void blockedBy(Player player) {
		blocker = player;
		player.setNpc(this);
		setBusy(true);
	}

	private Player findVictim() {
		if (goingToAttack) {
			return null;
		}
		if (hasRan()) {
			return null;
		}
		long now = GameEngine.getTime();
		if (getChasing() != null) {
			return null;
		}
		ActiveTile[][] tiles = getViewArea().getViewedArea(2, 2, 2, 2);
		for (int x = 0; x < tiles.length; x++) {
			for (int y = 0; y < tiles[x].length; y++) {
				ActiveTile t = tiles[x][y];
				if (t != null) {
					for (Player p : t.getPlayers()) {
						if (p.inCombat()) {
							continue;
						}
						/*
						 * if(p.isBusy() || p.isNonaggro() || now -
						 * p.getCombatTimer() < (p.getCombatState() ==
						 * CombatState.RUNNING || p.getCombatState() ==
						 * CombatState.WAITING ? 3000 : 500) ||
						 * !p.nextTo(this))) { return p; }
						 */

						if (p.isBusy()
								|| p.isNonaggro()
								|| now - p.getCombatTimer() < (p
										.getCombatState() == CombatState.RUNNING
										|| p.getCombatState() == CombatState.WAITING ? 3000
											: 500)
								|| !p.nextTo(this)
								|| !p.getLocation().inBounds(loc.minX - 4,
										loc.minY - 4, loc.maxX + 4,
										loc.maxY + 4)) {
							continue;
						}

						if (getLocation().inWilderness()
								|| p.getCombatLevel() < (getCombatLevel() * 2) + 1) {
							return p;
						}
					}
				}
			}
		}
		return null;
	}

	public int getArmourPoints() {
		return 1;
	}

	public int getAttack() {
		return def.getAtt();
	}

	public Player getChasing() {
		return chasing;
	}

	public int getCombatStyle() {
		return 0;
	}

	public NPCDef getDef() {
		return EntityHandler.getNpcDef(getID());
	}

	public int getDefense() {
		return def.getDef();
	}

	public int getHits() {
		return curHits;
	}

	public NPCLoc getLoc() {
		return loc;
	}

	public int getStrength() {
		return def.getStr();
	}

	public int getWeaponAimPoints() {
		return 1;
	}

	public int getWeaponPowerPoints() {
		return 1;
	}

	public void killedBy(Mob mob, boolean stake) {
		if (mob instanceof Player) {
			Player player = (Player) mob;
			player.getActionSender().sendSound("victory");
		}

		Mob opponent = super.getOpponent();
		if (opponent != null) {
			opponent.resetCombat(CombatState.WON);
		}

		resetCombat(CombatState.LOST);
		world.unregisterNpc(this);
		remove();

		Player owner = mob instanceof Player ? (Player) mob : null;

		drop(owner);
	}

	private boolean drop(Player owner) {
		ItemDropDef[] drops = def.getDrops();

		int total = 0;
		List<ItemDropDef> possibleDrops = new ArrayList<ItemDropDef>();
		for (ItemDropDef drop : drops) {
			if (drop == null) {
				continue;
			}
			try {
				if (EntityHandler.getItemDef(drop.getID()).members
						&& !World.isMembers()) {
					continue;
				}
			} catch (NullPointerException e) {
				// -1 is designated for only adding weight
				if (drop.id != -1) {
					Logger.println("Invalid drop id " + drop.id + " for NPC id " + this.id);
				}
			}
			total += drop.getWeight();
			possibleDrops.add(drop);
		}
		int hit = DataConversions.random(0, total);
		total = 0;
		if (!this.getDef().name.equalsIgnoreCase("ghost")) {

			for (ItemDropDef drop : possibleDrops) {
				if (drop.getWeight() == 0) {
					world.registerItem(new Item(drop.getID(), getX(), getY(),
							drop.getAmount(), owner));
					continue;
				}

				if (hit >= total && hit < (total + drop.getWeight())) {
					if (drop.getID() != -1) {
						world.registerItem(new Item(drop.getID(), getX(),
								getY(), drop.getAmount(), owner));
						break;
					}
				}
				total += drop.getWeight();
			}
		}
		return true;
	}

	public void remove() {
		if (!removed && shouldRespawn && def.respawnTime() > 0) {
			Instance.getDelayedEventHandler().add(
					new DelayedEvent(null, def.respawnTime() * 1000) {

						public void run() {
							world.registerNpc(new Npc(loc));
							matchRunning = false;
						}
					});
		}

		removed = true;

	}

	public void setChasing(Player player) {

		this.chasing = player;
		goingToAttack = true;

		if (player == null) {
			this.chasing = null;
			goingToAttack = false;
			return;
		}

		chaseTimeout = new DelayedEvent(null, 15000) {

			public void run() {

				goingToAttack = false;
				setChasing(null);
				matchRunning = false;
			}
		};

		Instance.getDelayedEventHandler().add(chaseTimeout);
	}

	public void setHits(int lvl) {
		if (lvl <= 0) {
			lvl = 0;
		}

		curHits = lvl;
	}

	public void setRespawn(boolean respawn) {
		shouldRespawn = respawn;
	}

	public void unblock() {
		if (blocker != null) {
			blocker.setNpc(null);
			blocker = null;
		}

		goingToAttack = false;
		setBusy(false);
	}

	public void updatePosition() {
		long now = GameEngine.getTime();
		Player victim = null;
		if (!isBusy() && def.isAggressive() && now - getCombatTimer() > 3000
				&& (victim = findVictim()) != null) {
			resetPath();
			victim.resetPath();
			victim.resetAll();
			victim.setStatus(Action.FIGHTING_MOB);
			/*
			 * Do not want if (victim.isSleeping()) {
			 * victim.getActionSender().sendWakeUp(false); }
			 */
			victim.getActionSender().sendSound("underattack");
			victim.getActionSender().sendMessage("You are under attack!");

			setLocation(victim.getLocation(), true);
			for (Player p : getViewArea().getPlayersInView()) {
				p.removeWatchedNpc(this);
			}

			victim.setBusy(true);
			victim.setSprite(9);
			victim.setOpponent(this);
			victim.setCombatTimer();

			setBusy(true);
			setSprite(8);
			setOpponent(victim);
			setCombatTimer();
			FightEvent fighting = new FightEvent(victim, this, true);
			fighting.setLastRun(0);
			Instance.getDelayedEventHandler().add(fighting);
		}

		if (now - lastMovement > 2200) {
			if (now - getCombatTimer() < (getCombatState() == CombatState.WAITING ? 5000
					: 500)) {
			} else {
				lastMovement = now;
				int rand = DataConversions.random(0, 1);
				if (!isBusy() && finishedPath() && rand == 1
						&& !this.isRemoved()) {
					int newX = DataConversions.random(loc.minX(), loc.maxX());
					int newY = DataConversions.random(loc.minY(), loc.maxY());
					super.setPath(new Path(getX(), getY(), newX, newY));
				}
			}
		}

		super.updatePosition();
	}

	@Override
	public String toString() {
		return "[NPC:" + EntityHandler.getNpcDef(id).getName() + "]";
	}

	public boolean hasArmor = false;
	public boolean undead = false;
}
