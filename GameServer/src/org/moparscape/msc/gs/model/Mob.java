package org.moparscape.msc.gs.model;

import org.moparscape.msc.gs.Instance;
import org.moparscape.msc.gs.core.GameEngine;
import org.moparscape.msc.gs.event.DelayedEvent;
import org.moparscape.msc.gs.event.DuelEvent;
import org.moparscape.msc.gs.event.FightEvent;
import org.moparscape.msc.gs.states.Action;
import org.moparscape.msc.gs.states.CombatState;
import org.moparscape.msc.gs.util.Logger;

public abstract class Mob extends Entity {

	/**
	 * Prayers that are currently turned on
	 */
	protected boolean[] activatedPrayers = new boolean[14];
	/**
	 * ID for our current appearance, used client side to detect changed
	 */
	protected int appearanceID = 0;
	/**
	 * Used to block new requests when we are in the middle of one
	 */
	private boolean busy = false;
	/**
	 * Our combat level
	 */
	protected int combatLevel = 3;
	/**
	 * Timer used to track start and end of combat
	 */
	private long combatTimer = 0;
	/**
	 * Who they are in combat with
	 */
	private Mob combatWith = null;
	/**
	 * Have we moved since last update?
	 */
	protected boolean hasMoved;
	/**
	 * How many times we have hit our opponent
	 */
	private int hitsMade = 0;
	/**
	 * The end state of the last combat encounter
	 */
	private CombatState lastCombatState = CombatState.WAITING;
	/**
	 * Amount of damage last dealt to the player
	 */
	private int lastDamage = 0;
	/**
	 * Time of last movement, used for timeout
	 */
	protected long lastMovement = GameEngine.getTime();
	public long lastTimeShot = GameEngine.getTime();
	protected int mobSprite = 1;
	private int[][] mobSprites = new int[][] { { 3, 2, 1 }, { 4, -1, 0 },
			{ 5, 6, 7 } };
	/**
	 * Has our appearance changed since last update?
	 */
	protected boolean ourAppearanceChanged = true;
	/**
	 * The path we are walking
	 */
	public PathHandler pathHandler = new PathHandler(this);
	/**
	 * Set when the mob has been destroyed to alert players
	 */
	protected boolean removed = false;
	/**
	 * Has the sprite changed?
	 */
	private boolean spriteChanged = false;
	/**
	 * Tiles around us that we can see
	 */
	protected ViewArea viewArea = new ViewArea(this);
	/**
	 * If we are warned to move
	 */
	protected boolean warnedToMove = false;

	public final boolean atObject(GameObject o) {
		int dir = o.getDirection();
		int width, height;
		if (o.getType() == 1) {
			width = height = 1;
		} else if (dir == 0 || dir == 4) {
			width = o.getGameObjectDef().getWidth();
			height = o.getGameObjectDef().getHeight();
		} else {
			height = o.getGameObjectDef().getWidth();
			width = o.getGameObjectDef().getHeight();
		}
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				Point p = Point.location(o.getX() + x, o.getY() + y);
				int xDist = Math.abs(location.getX() - p.getX());
				int yDist = Math.abs(location.getY() - p.getY());
				int tDist = xDist + yDist;
				if (tDist <= 1) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean finishedPath() {
		return pathHandler.finishedPath();
	}

	public int getAppearanceID() {
		return appearanceID;
	}

	public abstract int getArmourPoints();

	public abstract int getAttack();

	public int getCombatLevel() {
		return combatLevel;
	}

	public CombatState getCombatState() {
		return lastCombatState;
	}

	public abstract int getCombatStyle();

	public long getCombatTimer() {
		return combatTimer;
	}

	public abstract int getDefense();

	public abstract int getHits();

	public int getHitsMade() {
		return hitsMade;
	}

	public int getLastDamage() {
		return lastDamage;
	}

	public long getLastMoved() {
		return lastMovement;
	}

	public Mob getOpponent() {
		return combatWith;
	}

	public int getSprite() {
		return mobSprite;
	}

	public abstract int getStrength();

	public ViewArea getViewArea() {
		return viewArea;
	}

	public abstract int getWeaponAimPoints();

	public abstract int getWeaponPowerPoints();

	public boolean hasMoved() {
		return hasMoved;
	}

	public void incHitsMade() {
		hitsMade++;
	}

	public boolean inCombat() {
		return (mobSprite == 8 || mobSprite == 9) && combatWith != null;
	}

	public boolean isBusy() {
		return busy;
	}

	public boolean isPrayerActivated(int pID) {
		return activatedPrayers[pID];
	}

	public boolean isRemoved() {
		return removed;
	}

	public abstract void killedBy(Mob mob, boolean stake);

	public abstract void remove();

	public void resetCombat(CombatState state) {
		for (DelayedEvent event : Instance.getDelayedEventHandler().getEvents()) {
			if (event instanceof FightEvent) {
				FightEvent fighting = (FightEvent) event;
				if (fighting.getOwner().equals(this)
						|| fighting.getAffectedMob().equals(this)) {
					fighting.stop();
					break;
				}
			} else if (event instanceof DuelEvent) {
				DuelEvent dueling = (DuelEvent) event;
				if (dueling.getOwner().equals(this)
						|| dueling.getAffectedPlayer().equals(this)) {
					dueling.stop();
					break;
				}
			}
		}
		setBusy(false);
		setSprite(4);
		setOpponent(null);
		setCombatTimer();
		hitsMade = 0;
		if (this instanceof Player) {
			Player player = (Player) this;
			player.setStatus(Action.IDLE);
		}
		lastCombatState = state;
	}

	public void resetMoved() {
		hasMoved = false;
	}

	public void resetPath() {
		pathHandler.resetPath();

	}

	public void resetSpriteChanged() {
		spriteChanged = false;
	}

	public void setAppearnceChanged(boolean b) {
		ourAppearanceChanged = b;
	}

	public void setBusy(boolean busy) {
		this.busy = busy;
	}

	public void setCombatLevel(int level) {
		combatLevel = level;
		ourAppearanceChanged = true;
	}

	public void setCombatTimer() {
		combatTimer = GameEngine.getTime();
	}

	public abstract void setHits(int lvl);

	public void setLastDamage(int d) {
		lastDamage = d;
	}

	public void setLastMoved() {
		lastMovement = GameEngine.getTime();
	}

	public void setLocation(Point p) {
		setLocation(p, false);
	}

	public void setLocation(Point p, boolean teleported) {
		if (!teleported) {
			updateSprite(p);
			hasMoved = true;
		}
		setLastMoved();
		warnedToMove = false;
		super.setLocation(p);
	}

	public void setOpponent(Mob opponent) {
		combatWith = opponent;
	}

	public void setPath(Path path) {
		pathHandler.setPath(path);
	}

	public void setPrayer(int pID, boolean b) {
		activatedPrayers[pID] = b;
	}

	public void setSprite(int x) {
		spriteChanged = true;
		mobSprite = x;
	}

	public boolean spriteChanged() {
		return spriteChanged;
	}

	public void updateAppearanceID() {
		if (ourAppearanceChanged) {
			appearanceID++;
		}
	}

	public void updatePosition() {
		pathHandler.updatePosition();
	}

	protected void updateSprite(Point newLocation) {
		try {
			int xIndex = getLocation().getX() - newLocation.getX() + 1;
			int yIndex = getLocation().getY() - newLocation.getY() + 1;
			setSprite(mobSprites[xIndex][yIndex]);
		} catch (Exception e) {
			Logger.error(e.getMessage());
		}
	}

	public boolean warnedToMove() {
		return warnedToMove;
	}

	public void warnToMove() {
		warnedToMove = true;
	}
}
