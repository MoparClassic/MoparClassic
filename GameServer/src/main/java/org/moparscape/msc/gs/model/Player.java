package org.moparscape.msc.gs.model;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.mina.common.IoSession;
import org.moparscape.msc.config.Config;
import org.moparscape.msc.config.Formulae;
import org.moparscape.msc.gs.Instance;
import org.moparscape.msc.gs.builders.MiscPacketBuilder;
import org.moparscape.msc.gs.builders.ls.SavePacketBuilder;
import org.moparscape.msc.gs.connection.LSPacket;
import org.moparscape.msc.gs.connection.RSCPacket;
import org.moparscape.msc.gs.core.GameEngine;
import org.moparscape.msc.gs.event.DelayedEvent;
import org.moparscape.msc.gs.event.MiniEvent;
import org.moparscape.msc.gs.event.RangeEvent;
import org.moparscape.msc.gs.event.ShortEvent;
import org.moparscape.msc.gs.external.AgilityCourseDef;
import org.moparscape.msc.gs.external.EntityHandler;
import org.moparscape.msc.gs.external.PrayerDef;
import org.moparscape.msc.gs.model.snapshot.Activity;
import org.moparscape.msc.gs.phandler.client.WieldHandler;
import org.moparscape.msc.gs.quest.Quest;
import org.moparscape.msc.gs.states.Action;
import org.moparscape.msc.gs.states.CombatState;
import org.moparscape.msc.gs.tools.DataConversions;
import org.moparscape.msc.gs.util.DuelLog;
import org.moparscape.msc.gs.util.Logger;
import org.moparscape.msc.gs.util.StatefulEntityCollection;

import bsh.Interpreter;

/**
 * A single player.
 */
public final class Player extends Mob {

	/**
	 * Methods to send packets related to actions
	 */
	private MiscPacketBuilder actionSender;
	/**
	 * The current agility course the player's doing
	 */
	private AgilityCourseDef agilityCourseDef = null;
	/**
	 * The Players appearance
	 */
	private PlayerAppearance appearance;

	/**
	 * Players we have been attacked by signed login, used to check if we should
	 * get a skull for attacking back
	 */
	private HashMap<Long, Long> attackedBy = new HashMap<Long, Long>();

	private boolean badClient = false;
	/**
	 * Bank for banked items
	 */
	private Bank bank;

	private boolean blink = false;
	/**
	 * Bubbles needing displayed
	 */
	private ArrayList<Bubble> bubblesNeedingDisplayed = new ArrayList<Bubble>();
	/**
	 * Controls if were allowed to accept appearance updates
	 */
	private boolean changingAppearance = false;

	/**
	 * Chat messages needing displayed
	 */
	private ArrayList<ChatMessage> chatMessagesNeedingDisplayed = new ArrayList<ChatMessage>();

	/**
	 * List of chat messages to send
	 */
	private LinkedList<ChatMessage> chatQueue = new LinkedList<ChatMessage>();
	/**
	 * The name of the client class they are connecting from
	 */
	private String className = "NOT_SET";
	private int click = -1;

	private boolean clientWarn = false;
	/**
	 * Combat style: 0 - all, 1 - str, 2 - att, 3 - def
	 */
	private int combatStyle = 0;
	private int Combo = 0;

	/**
	 * Added by Zerratar: Correct sleepword we are looking for! Case SenSitIvE
	 */
	private String correctSleepword = "";
	/**
	 * The current agility course object the player's on
	 */
	private int currentCourseObject = -1;
	/**
	 * Stores the current IP address used
	 */
	private String currentIP = "0.0.0.0";
	/**
	 * Unix time when the player logged in
	 */
	private long currentLogin = 0;
	/**
	 * The current stat array
	 */
	private int[] curStat = new int[18];
	/**
	 * Should we destroy this player?
	 */
	private boolean destroy = false;
	private boolean doricDependency = false;
	/**
	 * DelayedEvent responsible for handling prayer drains
	 */
	private DelayedEvent drainer;
	private int drainerDelay = Integer.MAX_VALUE;

	/**
	 * The drain rate of the prayers currently enabled
	 */
	private int drainRate = 0;

	/**
	 * If the second duel screen has been accepted
	 */
	private boolean duelConfirmAccepted = false;

	/**
	 * List of items offered in the current duel
	 */
	private ArrayList<InvItem> duelOffer = new ArrayList<InvItem>();

	/**
	 * If the first duel screen has been accepted
	 */
	private boolean duelOfferAccepted = false;

	/**
	 * Duel options
	 */
	private boolean[] duelOptions = new boolean[4];

	private long eventcd = 0;

	/**
	 * The exp level array
	 */
	private int[] exp = new int[18];

	/**
	 * Amount of fatigue - 0 to 100
	 */
	private int fatigue = 0;

	/**
	 * Has the first major update for this player been sent? If not, we can't
	 * send them any minor updates.
	 */
	private boolean firstMajorUpdateSent = false;

	private boolean flagCarrier = false;

	/**
	 * Event to handle following
	 */
	private DelayedEvent followEvent;

	/**
	 * Who we are currently following (if anyone)
	 */
	private Mob following;

	/**
	 * Map of players on players friend list
	 */
	private TreeMap<Long, Integer> friendList = new TreeMap<Long, Integer>();

	/**
	 * Users game settings, camera rotation preference etc
	 */
	private boolean[] gameSettings = new boolean[7]; // Why is 1 empty?

	/**
	 * The main accounts group is
	 */
	private int groupID = 1;

	private boolean hasAnswered = false;

	/**
	 * List of usernameHash's of players on players ignore list
	 */
	private ArrayList<Long> ignoreList = new ArrayList<Long>();

	/**
	 * Is the player accessing their bank?
	 */
	private boolean inBank = false;

	private boolean infected = false;

	/**
	 * Quests
	 */
	/**
	 * Has the player been registered into the world?
	 */
	private boolean initialized = false;

	private boolean inQuiz = false;

	/**
	 * The npc we are currently interacting with
	 */
	private Npc interactingNpc = null;

	private Interpreter interpreter = new Interpreter();

	private Thread interpreterThread = null;

	private LinkedList<Long> intervals = new LinkedList<Long>();

	/**
	 * Inventory to hold items
	 */
	private Inventory inventory;

	private boolean invis = false;

	/**
	 * The IO session of this player
	 */
	private IoSession ioSession;

	/**
	 * If the player is currently in a duel
	 */
	private boolean isDueling = false;
	private boolean isMining = false;
	/**
	 * Added by Zerratar: Are we sleeping?
	 */
	private boolean isSleeping = false;
	/**
	 * If the player is currently in a trade
	 */
	private boolean isTrading = false;

	private int killStreak = 0;

	/**
	 * List of players this player 'knows' (recieved from the client) about
	 */
	private HashMap<Integer, Integer> knownPlayersAppearanceIDs = new HashMap<Integer, Integer>();

	private String lastAnswer = null;

	/**
	 * Last arrow fired
	 */
	private long lastArrow = 0;

	private long lastbanktime = 0;

	/**
	 * The last menu reply this player gave in a quest
	 */
	//
	private long lastCast = GameEngine.getTime();

	/**
	 * Time of last charge spell
	 */
	private long lastCharge = 0;

	private long lastCommandUsed = GameEngine.getTime();

	/**
	 * Last packet count time
	 */
	private long lastCount = 0;

	private long lastDeath = GameEngine.getTime();

	private int lastdepositedamount = 0;
	private int lastdepositeditem = 0;
	private long lastInfected = GameEngine.getTime();
	private long lastInterval = 0;
	private long lastinvtime = 0;
	/**
	 * Stores the last IP address used
	 */
	private String lastIP = "0.0.0.0";
	private int lastitemposition = 0;
	/**
	 * Unix time when the player last logged in
	 */
	private long lastLogin = 0;
	// Player(IoSession
	private long lastMineTimer = 0;
	private Npc lastNpcChasingYou = null;
	private long lastNPCChat = GameEngine.getTime();
	private int lastOption = -2;
	private String[] lastOptions = null;
	private long lastPacketRecTime = GameEngine.getTime() / 1000;
	/**
	 * Queue of last 100 packets, used for auto detection purposes
	 */
	private LinkedList<RSCPacket> lastPackets = new LinkedList<RSCPacket>();
	private long lastPacketTime = -1;
	/**
	 * Last time a 'ping' was received
	 */
	private long lastPing = GameEngine.getTime();
	private String lastPlayerInfo2 = null;
	private int lastQuestMenuReply = -1;
	// don't remove this. -xEnt
	private int lastRandom = 0;
	private long lastRange = GameEngine.getTime();
	/**
	 * Time last report was sent, used to throttle reports
	 */
	private long lastReport = 0;
	private long lastRun = GameEngine.getTime(); // Leave this here
	private long lastSaveTime = GameEngine.getTime()
			+ DataConversions.random(600000, 1800000);
	private long lastSleepTime = GameEngine.getTime();
	/**
	 * The time of the last spell cast, used as a throttle
	 */
	private long lastSpellCast = 0;
	/**
	 * Time of last trade/duel request
	 */
	private long lastTradeDuelRequest = 0;
	private int lastwithdrawamount = 0;
	private int lastwithdrawitem = 0;
	private long lastwithdrawtime = 0;
	/**
	 * Whether the player is currently logged in
	 */
	private boolean loggedIn = false;
	private long loginTime = -1;
	private int loops = 0;
	/**
	 * Is the character male?
	 */
	private boolean maleGender;
	/**
	 * The max stat array
	 */
	private int[] maxStat = new int[18];
	/**
	 * A handler for any menu we are currently in
	 */
	private MenuHandler menuHandler = null;
	/**
	 * How long is this player muted?
	 */
	private long muted = 0;
	// Player
	/**
	 * Added by Konijn
	 */
	private boolean noclip = false;
	// Do aggros attack?
	private boolean nonaggro = false;
	// Is the player PK-able?
	private boolean nopk = false;
	/**
	 * NPC messages needing displayed
	 */
	private ArrayList<ChatMessage> npcMessagesNeedingDisplayed = new ArrayList<ChatMessage>();
	/**
	 * List of players who have been hit
	 */
	private ArrayList<Npc> npcsNeedingHitsUpdate = new ArrayList<Npc>();
	/**
	 * Thieving
	 */
	private boolean[] npcThief = { false, false, false, false, false, false }; // Baker
	// ,
	// Silver,
	// Spices,
	// Gem.
	/**
	 * The ID of the owning account
	 */
	private int owner = 1;

	/**
	 * Amount of packets since last count
	 */
	private int packetCount = 0;
	private boolean packetSpam = false;
	/**
	 * The player's password
	 */
	private String password;
	/**
	 * List of players who have been hit
	 */
	private ArrayList<Player> playersNeedingHitsUpdate = new ArrayList<Player>();
	/**
	 * Users privacy settings, chat block etc.
	 */
	private boolean[] privacySettings = new boolean[4];
	/**
	 * List of all projectiles needing displayed
	 */
	private ArrayList<Projectile> projectilesNeedingDisplayed = new ArrayList<Projectile>();
	/**
	 * This player's quest points
	 */
	private int questPoints = 0;
	/**
	 * This player's quest stage array
	 */
	private HashMap<Integer, Integer> questStage = new HashMap<Integer, Integer>();
	private int quizPoints = 0;
	/**
	 * Ranging event
	 */
	private RangeEvent rangeEvent;
	/**
	 * If the player is reconnecting after connection loss
	 */
	private boolean reconnecting = false;
	/**
	 * Is a trade/duel update required?
	 */
	private boolean requiresOfferUpdate = false;
	private int sessionFlags = 0;
	/**
	 * Session keys for the players session
	 */
	private int[] sessionKeys = new int[4];
	private ShortEvent sEvent = null;
	/**
	 * The shop (if any) the player is currently accessing
	 */
	private Shop shop = null;
	/**
	 * DelayedEvent used for removing players skull after 20mins
	 */
	private DelayedEvent skullEvent = null;
	/**
	 * Sleeping shit
	 */
	private String sleepword;
	/**
	 * The current status of the player
	 */
	private Action status = Action.IDLE;
	/**
	 * When the users subscription expires (or 0 if they don't have one)
	 */
	private long subscriptionExpires = 0;
	/**
	 * If the player has been sending suscicious packets
	 */
	private boolean suspicious = false;
	private int tempx = -1;
	private int tempy = -1;
	/**
	 * If the second trade screen has been accepted
	 */
	private boolean tradeConfirmAccepted = false;
	/**
	 * List of items offered in the current trade
	 */
	private ArrayList<InvItem> tradeOffer = new ArrayList<InvItem>();
	/**
	 * If the first trade screen has been accepted
	 */
	private boolean tradeOfferAccepted = false;
	/**
	 * The player's username
	 */
	private String username;
	/**
	 * The player's username hash
	 */
	private long usernameHash;
	/**
	 * Nearby items that we should be aware of
	 */
	private StatefulEntityCollection<Item> watchedItems = new StatefulEntityCollection<Item>();
	/**
	 * Nearby npcs that we should be aware of
	 */
	private StatefulEntityCollection<Npc> watchedNpcs = new StatefulEntityCollection<Npc>();
	/**
	 * Nearby game objects that we should be aware of
	 */
	private StatefulEntityCollection<GameObject> watchedObjects = new StatefulEntityCollection<GameObject>();
	/**
	 * Nearby players that we should be aware of
	 */
	private StatefulEntityCollection<Player> watchedPlayers = new StatefulEntityCollection<Player>();
	/**
	 * The player we last requested to duel with, or null for none
	 */
	private Player wishToDuel = null;
	/**
	 * The player we last requested to trade with, or null for none
	 */
	private Player wishToTrade = null;
	/**
	 * The items being worn by the player
	 */
	private int[] wornItems = new int[12];
	private int wrongwords = 0;

	private int poisonPower = 0;
	private DelayedEvent poisonEvent;

	public boolean isPoisoned() {
		return poisonPower > 0;
	}

	public void poison(int power) {
		if (!isPoisoned()) {
			this.poisonPower = power;
			poisonEvent = new DelayedEvent(this, 19500) {
				public void run() {
					damagePoison(true);
				}
			};
			World.getWorld().getDelayedEventHandler().add(poisonEvent);
		} else
			this.poisonPower = power;
	}

	public void curePoison() {
		this.poisonPower = 0;
		if (poisonEvent != null)
			poisonEvent.stop();
	}

	public void startPoison(int power) {
		this.poison(power);
		this.poisonPower = power;

		damagePoison(false);
	}

	public void damagePoison(boolean decrease) {
		if (this.poisonPower > 0) {
			double calcDamage = Math.ceil(poisonPower / 5);
			int damage = (int) calcDamage + 1;

			if (decrease)
				poisonPower--;

			setLastDamage(damage);
			setCurStat(3, getCurStat(3) - damage);
			getActionSender().sendStat(3);
			getActionSender().sendMessage(
					"@gr3@You @gr2@are @gr1@Poisoned! @gr2@You @gr3@lose "
							+ damage + " @gr1@health.");

			for (Player p : getViewArea().getPlayersInView())
				p.informOfModifiedHits(this);
			if (getCurStat(3) <= 0) {
				killedBy(null, false);
				poisonEvent.stop();
			}
		} else {
			if (poisonEvent != null)
				poisonEvent.stop();
			this.poisonPower = 0;
		}
	}

	public Player(IoSession ios) {

		ioSession = ios;
		currentIP = ((InetSocketAddress) ios.getRemoteAddress()).getAddress()
				.getHostAddress();
		currentLogin = GameEngine.getTime();
		actionSender = new MiscPacketBuilder(this);
		setBusy(true);
		Instance.getWorld();
		for (int i : World.getQuestManager().getQuestIds()) {
			questStage.put(i, -1);
		}
	}

	public boolean accessingBank() {
		return inBank;
	}

	public boolean accessingShop() {
		return shop != null;
	}

	public void addAttackedBy(Player p) {
		attackedBy.put(p.getUsernameHash(), GameEngine.getTime());
	}

	public void addFriend(long id, int world) {
		friendList.put(id, world);
	}

	public void addIgnore(long id) {
		ignoreList.add(id);
	}

	public void addInterval() {
		if (lastInterval == 0) {
			lastInterval = GameEngine.getTime();
		} else {
			intervals.addFirst(GameEngine.getTime() - lastInterval);
			if (intervals.size() > 75) {
				intervals.removeLast();
			}
			lastInterval = GameEngine.getTime();
		}
	}

	public void addMessageToChatQueue(byte[] messageData) {
		if (chatQueue.size() <= 2) {
			chatQueue.add(new ChatMessage(this, messageData));
		}
	}

	/**
	 * This method acts as a throttle for packets, and adds them to a list.<br>
	 * If the player sends more than 20 packets per second they're disconnected
	 * (60 packets per 3000ms)
	 * 
	 * @param p
	 *            - the packet to add...
	 */
	public void addPacket(RSCPacket p) {
		long now = GameEngine.getTime();
		if (now - lastCount > 3000) {
			lastCount = now;
			packetCount = 0;
		}
		if (!DataConversions.inArray(Formulae.safePacketIDs, p.getID())
				&& ++packetCount >= 60) {
			destroy(false);
		}
		if (lastPackets.size() >= 60) {
			lastPackets.remove();
		}
		lastPackets.addLast(p);
	}

	public void addPlayersAppearanceIDs(int[] indicies, int[] appearanceIDs) {
		for (int x = 0; x < indicies.length; x++) {
			knownPlayersAppearanceIDs.put(indicies[x], appearanceIDs[x]);
		}
	}

	public void addPrayerDrain(int prayerID) {
		drainRate = 0;
		PrayerDef prayer = EntityHandler.getPrayerDef(prayerID);
		for (int x = 0; x <= 13; x++) {
			prayer = EntityHandler.getPrayerDef(x);
			if (super.isPrayerActivated(x)) {
				drainRate += prayer.getDrainRate() / 2;
			}
		}
		drainRate = drainRate - getPrayerPoints();
		if (drainRate > 0) {
			drainer.setDelay((int) (240000 / drainRate));
		} else if (drainRate <= 0) {
			drainRate = 0;
			drainer.setDelay(Integer.MAX_VALUE);
		}
	}

	public void addSkull(long timeLeft) {
		if (!isSkulled()) {
			skullEvent = new DelayedEvent(this, 1200000) {

				public void run() {
					removeSkull();
					this.stop();
				}
			};
			Instance.getDelayedEventHandler().add(skullEvent);
			super.setAppearnceChanged(true);
		}
		skullEvent.setLastRun(GameEngine.getTime() - (1200000 - timeLeft));
	}

	public void addToDuelOffer(InvItem item) {
		duelOffer.add(item);
	}

	public void addToTradeOffer(InvItem item) {
		tradeOffer.add(item);
	}

	public boolean blink() {
		return blink;
	}

	public boolean canLogout() {
		if (this != null && this.location != null
				&& this.location.inWilderness()) {
			if (GameEngine.getTime() - this.getLastMoved() < Config.WILD_STAND_STILL_TIME) {
				getActionSender().sendMessage(
						"You must stand peacefully in one place for "
								+ Config.WILD_STAND_STILL_TIME + " seconds!");
				return false;
			}
		}
		return !isBusy()
				&& GameEngine.getTime() - getCombatTimer() > Config.WILD_STAND_STILL_TIME;
	}

	public boolean canReport() {
		return GameEngine.getTime() - lastReport > 60000;
	}

	public boolean castTimer() {
		return GameEngine.getTime() - lastSpellCast > 1200;
	}

	public boolean checkAttack(Mob mob, boolean missile) {
		if (mob instanceof Player) {
			Player victim = (Player) mob;

			if (victim.isNoPK()) {
				actionSender
						.sendMessage("You cannot attack this staff members");
				return false;
			}

			// KO9 - Konijn - check if PK'ing is disabled in this area
			int i = 0;
			for (Point place[] : world.getPlaces()) {
				if (getLocation().inBounds(place[0].getX(), place[0].getY(),
						place[1].getX(), place[1].getY())
						&& !world.wildAttackable(i)) {
					actionSender
							.sendMessage("You cannot attack in this area at the moment.");
					return false;
				}
			}

			if ((inCombat() && isDueling())
					&& (victim.inCombat() && victim.isDueling())) {
				Player opponent = (Player) getOpponent();
				if (opponent != null && victim.equals(opponent)) {
					return true;
				}
			}
			if (GameEngine.getTime() - mob.getCombatTimer() < (mob
					.getCombatState() == CombatState.RUNNING
					|| mob.getCombatState() == CombatState.WAITING ? 3000 : 500)
					&& !mob.inCombat()) {
				return false;
			}
			int myWildLvl = getLocation().wildernessLevel();
			int victimWildLvl = victim.getLocation().wildernessLevel();
			if (myWildLvl < 1 || victimWildLvl < 1) {
				actionSender
						.sendMessage("You cannot attack other players outside of the wilderness!");
				return false;
			}
			int combDiff = Math.abs(getCombatLevel() - victim.getCombatLevel());
			if (combDiff > myWildLvl) {
				actionSender.sendMessage("You must move to at least level "
						+ combDiff + " wilderness to attack "
						+ victim.getUsername() + "!");
				return false;
			}
			if (combDiff > victimWildLvl) {
				actionSender
						.sendMessage(victim.getUsername()
								+ " is not in high enough wilderness for you to attack!");
				return false;
			}
			return true;
		} else if (mob instanceof Npc) {
			Npc victim = (Npc) mob;
			if (!victim.getDef().isAttackable()) {
				setSuspiciousPlayer(true);
				return false;
			}
			return true;
		}
		return true;
	}

	public void clearBubblesNeedingDisplayed() {
		bubblesNeedingDisplayed.clear();
	}

	public void clearChatMessagesNeedingDisplayed() {
		chatMessagesNeedingDisplayed.clear();
	}

	public void clearDuelOptions() {
		for (int i = 0; i < 4; i++) {
			duelOptions[i] = false;
		}
	}

	public void clearNpcMessagesNeedingDisplayed() {
		npcMessagesNeedingDisplayed.clear();
	}

	public void clearNpcsNeedingHitsUpdate() {
		npcsNeedingHitsUpdate.clear();
	}

	public void clearPlayersNeedingHitsUpdate() {
		playersNeedingHitsUpdate.clear();
	}

	public void clearProjectilesNeedingDisplayed() {
		projectilesNeedingDisplayed.clear();
	}

	public boolean clientWarn() {
		return clientWarn;
	}

	public void clientWarn(boolean cw) {
		clientWarn = cw;
	}

	public int combatStyleToIndex() {
		if (getCombatStyle() == 1) {
			return 2;
		}
		if (getCombatStyle() == 2) {
			return 0;
		}
		if (getCombatStyle() == 3) {
			return 1;
		}
		return -1;
	}

	public void destroy(boolean force) {
		if (destroy) {
			return;
		}
		if (force || canLogout()) {
			destroy = true;
			actionSender.sendLogout();
		} else {
			final long startDestroy = GameEngine.getTime();
			Instance.getDelayedEventHandler().add(new DelayedEvent(this, 3000) {

				public void run() {
					if (owner.canLogout()
							|| (!(owner.inCombat() && owner.isDueling()) && GameEngine
									.getTime() - startDestroy > 600000 * 2)) {
						owner.destroy(true);
						matchRunning = false;
					}
				}
			});
		}
	}

	public void destroy(boolean force, boolean logout) {
		if (destroy) {
			return;
		}
		if (force || canLogout()) {
			destroy = true;
			actionSender.sendLogout();
		} else {
			final long startDestroy = GameEngine.getTime();
			Instance.getDelayedEventHandler().add(new DelayedEvent(this, 3000) {

				public void run() {
					if (owner.canLogout()
							|| (!(owner.inCombat() && owner.isDueling()) && GameEngine
									.getTime() - startDestroy > 60000)) {
						owner.destroy(true);
						matchRunning = false;
					}
				}
			});
		}
	}

	public boolean destroyed() {
		return destroy;
	}

	public boolean equals(Object o) {
		if (o instanceof Player) {
			Player p = (Player) o;
			return usernameHash == p.getUsernameHash();
		}
		return false;
	}

	public int friendCount() {
		return friendList.size();
	}

	// getArmourPoints
	public MiscPacketBuilder getActionSender() {
		return actionSender;
	}

	/**
	 * @return this player's current agility course
	 */
	public AgilityCourseDef getAgilityCourseDef() {
		return agilityCourseDef;
	}

	public PlayerAppearance getAppearance() {
		return appearance;
	}// destroy

	public int getArmourPoints() {
		int points = 1;
		for (InvItem item : inventory.getItems()) {
			if (item.isWielded()) {
				points += item.getWieldableDef().getArmourPoints();
			}
		}
		if (this.isFlagCarrier()) {
			points = (int) (points * 0.25);
		}
		return points < 1 ? 1 : points;
	}

	public int getAttack() {
		return getCurStat(0);
	}

	public HashMap<Long, Long> getAttackedBy() {
		return attackedBy;
	}

	/*
	 * Used for the Infected Blood world event
	 */

	public Bank getBank() {
		return bank;
	}

	public List<Bubble> getBubblesNeedingDisplayed() {
		return bubblesNeedingDisplayed;
	}

	/*
	 * Informs the server that the player has just used Infected Blood and
	 * activate the cooldown.
	 */

	public long getCastTimer() {
		return lastSpellCast;
	}

	/*
	 * Returns the last time a player used Infected Blood.
	 */

	public List<ChatMessage> getChatMessagesNeedingDisplayed() {
		return chatMessagesNeedingDisplayed;
	}

	/*
	 * Is this player infected?
	 */

	public LinkedList<ChatMessage> getChatQueue() {
		return chatQueue;
	}

	/*
	 * This method is used for the Infected Blood world event
	 * 
	 * @author Ollie
	 */

	public String getClassName() {
		return className;
	}

	public int getClick() {
		return click;
	}

	public int getCombatStyle() {
		return combatStyle;
	}

	public int getCombo() {
		return this.Combo;
	}

	public String getCorrectSleepword() {
		return correctSleepword;
	}

	/**
	 * @return this player's current agility course
	 */
	public int getCurrentCourseObject() {
		return currentCourseObject;
	}

	public String getCurrentIP() {
		return currentIP;
	}

	public long getCurrentLogin() {
		return currentLogin;
	}

	public int[] getCurStat() {
		return curStat;
	}

	public int getCurStat(int id) {
		return curStat[id];
	}

	public int[] getCurStats() {
		return curStat;
	}

	/**
	 * How many days is the user muted for?
	 * 
	 * @return day
	 */
	public int getDaysMuted() {
		return (int) ((muted - GameEngine.getTime()) / 1000 / 3600 / 24);
	}

	// piru.sytes.net
	public int getDaysSinceLastLogin() {
		long now = Calendar.getInstance().getTimeInMillis() / 1000;
		return (int) ((now - lastLogin) / 86400);
	}

	public int getDaysSubscriptionLeft() {
		long now = (GameEngine.getTime() / 1000);
		if (subscriptionExpires == 0 || now >= subscriptionExpires) {
			return 0;
		}
		return (int) ((subscriptionExpires - now) / 86400);
	}

	public int getDefense() {
		return getCurStat(1);
	}

	public DelayedEvent getDrainer() {
		return drainer;
	}

	public int getDrainerDelay() {
		return drainerDelay;
	}

	public int getDrainRate() {
		return drainRate;
	}

	public ArrayList<InvItem> getDuelOffer() {
		return duelOffer;
	}

	public boolean[] getDuelOptions() {
		return duelOptions;
	}

	public boolean getDuelSetting(int i) {
		try {
			for (InvItem item : duelOffer) {
				if (DataConversions.inArray(Formulae.runeIDs, item.getID())) {
					setDuelSetting(1, true);
					break;
				}
			}
			for (InvItem item : wishToDuel.getDuelOffer()) {
				if (DataConversions.inArray(Formulae.runeIDs, item.getID())) {
					setDuelSetting(1, true);
					break;
				}
			}
		} catch (Exception e) {
		}
		return duelOptions[i];
	}

	public long getEventCD() {
		return eventcd;
	}

	public int[] getExp() {
		return exp;
	}

	public int getExp(int id) {
		return exp[id];
	}

	public int[] getExps() {
		return exp;
	}

	public int getFatigue() {
		return fatigue;
	}

	public DelayedEvent getFollowEvent() {
		return followEvent;
	}

	public Mob getFollowing() {
		return following;
	}

	public Collection<Entry<Long, Integer>> getFriendList() {
		return friendList.entrySet();
	}

	public boolean getGameSetting(int i) {
		return gameSettings[i];
	}

	public boolean[] getGameSettings() {
		return gameSettings;
	}

	public int getGroupID() {
		return groupID;
	}

	public int getHits() {
		return getCurStat(3);
	}

	public ArrayList<Long> getIgnoreList() {
		return ignoreList;
	}

	public Npc getInteractingNpc() {
		return interactingNpc;
	}

	public Interpreter getInterpreter() {
		return interpreter;
	}

	public Thread getInterpreterThread() {
		return interpreterThread;
	}

	public LinkedList<Long> getIntervals() {
		return intervals;
	}

	public Inventory getInventory() {
		return inventory;
	}

	public IoSession getIoSession() {
		return ioSession;
	}

	public int getKillStreak() {
		return killStreak;
	}

	public HashMap<Integer, Integer> getKnownPlayersAppearanceIDs() {
		return knownPlayersAppearanceIDs;
	}

	public String getLastAnswer() {
		return lastAnswer;
	}

	public long getLastArrow() {
		return lastArrow;
	}

	public long getLastCast() {
		return lastCast;
	}

	public long getLastCharge() {
		return lastCharge;
	}

	public long getLastCommandUsed() {
		return lastCommandUsed;
	}

	public long getLastCount() {
		return lastCount;
	}

	public long getLastDeath() {
		return lastDeath;
	}

	public long getLastInterval() {
		return lastInterval;
	}

	public String getLastIP() {
		return lastIP;
	}

	public long getLastLogin() {
		return lastLogin;
	}

	public long getLastMineTimer() {
		return lastMineTimer;
	}

	public Npc getLastNpcChasingYou() {
		return lastNpcChasingYou;
	}

	public long getLastNPCChat() {
		return lastNPCChat;
	}

	public int getLastOption() {
		return lastOption;
	}

	public String[] getLastOptions() {
		return lastOptions;
	}

	public long getLastPacketRecTime() {
		return lastPacketRecTime;
	}

	public LinkedList<RSCPacket> getLastPackets() {
		return lastPackets;
	}

	public long getLastPacketTime() {
		return lastPacketTime;
	}

	public long getLastPing() {
		return lastPing;
	}

	public String getLastPlayerInfo2() {
		return lastPlayerInfo2;
	}

	/**
	 * @return this player's last quest menu reply
	 */
	public int getLastQuestMenuReply() {
		return lastQuestMenuReply;
	}

	public int getLastRandom() {
		return lastRandom;
	}

	public long getLastRange() {
		return lastRange;
	}

	public long getLastReport() {
		return lastReport;
	}

	public long getLastRun() {
		return lastRun;
	}

	public long getLastSaveTime() {
		return lastSaveTime;
	}

	public long getLastSleepTime() {
		return lastSleepTime;
	}

	public long getLastSpellCast() {
		return lastSpellCast;
	}

	public long getLastTradeDuelRequest() {
		return lastTradeDuelRequest;
	}

	public long getLoginTime() {
		return loginTime;
	}

	public int getLoops() {
		return loops;
	}

	public int getMagicPoints() {
		int points = 1;
		for (InvItem item : inventory.getItems()) {
			if (item.isWielded()) {
				points += item.getWieldableDef().getMagicPoints();
			}
		}
		return points < 1 ? 1 : points;
	}

	public int[] getMaxStat() {
		return maxStat;
	}// you have been granted

	public int getMaxStat(int id) {
		return maxStat[id];
	}

	public int[] getMaxStats() {
		return maxStat;
	}

	public MenuHandler getMenuHandler() {
		return menuHandler;
	}

	public ChatMessage getNextChatMessage() {
		return chatQueue.poll();
	}

	public Npc getNpc() {
		return interactingNpc;
	}

	public List<ChatMessage> getNpcMessagesNeedingDisplayed() {
		return npcMessagesNeedingDisplayed;
	}

	public ArrayList<Npc> getNpcsNeedingHitsUpdate() {
		return npcsNeedingHitsUpdate;
	}

	public List<Npc> getNpcsRequiringHitsUpdate() {
		return npcsNeedingHitsUpdate;
	}

	public boolean[] getNpcThief() {
		return npcThief;
	}

	public int getOwner() {
		return owner;
	}

	public int getPacketCount() {
		return packetCount;
	}

	public List<RSCPacket> getPackets() {
		return lastPackets;
	}

	public String getPassword() {
		return password;
	}

	public PlayerAppearance getPlayerAppearance() {
		return appearance;
	}

	public ArrayList<Player> getPlayersNeedingHitsUpdate() {
		return playersNeedingHitsUpdate;
	}

	public List<Player> getPlayersRequiringAppearanceUpdate() {
		List<Player> needingUpdates = new ArrayList<Player>();
		needingUpdates.addAll(watchedPlayers.getNewEntities());
		if (super.ourAppearanceChanged) {
			needingUpdates.add(this);
		}
		for (Player p : watchedPlayers.getKnownEntities()) {
			if (needsAppearanceUpdateFor(p)) {
				needingUpdates.add(p);
			}
		}
		return needingUpdates;
	}

	public List<Player> getPlayersRequiringHitsUpdate() {
		return playersNeedingHitsUpdate;
	}

	public int getPrayerPoints() {
		int points = 1;
		for (InvItem item : inventory.getItems()) {
			if (item.isWielded()) {
				points += item.getWieldableDef().getPrayerPoints();
			}
		}
		return points < 1 ? 1 : points;
	}

	public boolean getPrivacySetting(int i) {
		return privacySettings[i];
	}

	public boolean[] getPrivacySettings() {
		return privacySettings;
	}

	public List<Projectile> getProjectilesNeedingDisplayed() {
		return projectilesNeedingDisplayed;
	}

	public int getQuestPoints() {
		return questPoints;
	}

	public HashMap<Integer, Integer> getQuestStage() {
		return questStage;
	}

	public int getQuestStage(int questId) {
		return questStage.get(questId);
	}

	public int getQuestStage(Quest quest) {
		return getQuestStage(quest.getUniqueID());
	}

	public HashMap<Integer, Integer> getQuestStages() {
		return questStage;
	}

	public int getQuizPoints() {
		return quizPoints;
	}

	public int getRangeEquip() {
		for (InvItem item : inventory.getItems()) {
			if (item.isWielded()
					&& (DataConversions.inArray(Formulae.bowIDs, item.getID()) || DataConversions
							.inArray(Formulae.xbowIDs, item.getID()))) {
				return item.getID();
			}
		}
		return -1;
	}

	public RangeEvent getRangeEvent() {
		return rangeEvent;
	}

	public int getRangePoints() {
		int points = 1;
		for (InvItem item : inventory.getItems()) {
			if (item.isWielded()) {
				points += item.getWieldableDef().getRangePoints();
			}
		}
		return points < 1 ? 1 : points;
	}

	public IoSession getSession() {
		return ioSession;
	}

	public int getSessionFlags() {
		return sessionFlags;
	}

	public int[] getSessionKeys() {
		return sessionKeys;
	}

	public ShortEvent getsEvent() {
		return sEvent;
	}

	public Shop getShop() {
		return shop;
	}

	public int getSkillLoops() {
		return loops;
	}

	public int getSkillTotal() {
		int total = 0;
		for (int stat : maxStat) {
			total += stat;
		}
		return total;
	}

	public DelayedEvent getSkullEvent() {
		return skullEvent;
	}

	public int getSkullTime() {
		if (isSkulled()) {
			return skullEvent.timeTillNextRun();
		}
		return 0;
	}

	public String getSleepword() {
		return sleepword;
	}

	public boolean getSpam() {
		return packetSpam;
	}

	public int getSpellWait() {
		return DataConversions
				.roundUp((double) (1200 - (GameEngine.getTime() - lastSpellCast)) / 1000D);
	}

	public Action getStatus() {
		return status;
	}

	public int getStrength() {
		return getCurStat(2);
	}

	public long getSubscriptionExpires() {
		return subscriptionExpires;
	}

	public int getTempx() {
		return tempx;
	}

	public int getTempy() {
		return tempy;
	}

	public ArrayList<InvItem> getTradeOffer() {
		return tradeOffer;
	}

	public String getUsername() {
		return username;
	}

	public long getUsernameHash() {
		return usernameHash;
	}

	public StatefulEntityCollection<Item> getWatchedItems() {
		return watchedItems;
	}

	public StatefulEntityCollection<Npc> getWatchedNpcs() {
		return watchedNpcs;
	}

	public StatefulEntityCollection<GameObject> getWatchedObjects() {
		return watchedObjects;
	}

	public StatefulEntityCollection<Player> getWatchedPlayers() {
		return watchedPlayers;
	}

	public int getWeaponAimPoints() {
		int points = 1;
		for (InvItem item : inventory.getItems()) {
			if (item.isWielded()) {
				points += item.getWieldableDef().getWeaponAimPoints();
			}
		}
		points -= 1;
		return points < 1 ? 1 : points;
	}

	public int getWeaponPowerPoints() {
		int points = 1;
		for (InvItem item : inventory.getItems()) {
			if (item.isWielded()) {
				points += item.getWieldableDef().getWeaponPowerPoints();
			}
		}
		points -= 1;
		return points < 1 ? 1 : points;
	}

	public Player getWishToDuel() {
		return wishToDuel;
	}

	public Player getWishToTrade() {
		return wishToTrade;
	}

	public int[] getWornItems() {
		return wornItems;
	}

	public int getWrongwords() {
		return wrongwords;
	}

	public int getWrongWords() {
		return wrongwords;
	}

	public int ignoreCount() {
		return ignoreList.size();
	}

	public void incCurStat(int i, int amount) {
		curStat[i] += amount;
		if (curStat[i] < 0) {
			curStat[i] = 0;
		}
	}

	public void incExp(int i, int amount, boolean useFatigue) {

		incExp(i, amount, useFatigue, false);
	}

	public void incExp(int i, int amount, boolean useFatigue, boolean combat) {
		if (isPMod())
			return;
		if (useFatigue) {
			if (fatigue >= 100) {
				actionSender
						.sendMessage("@gre@You are too tired to gain experience, get some rest!");
				return;
			}
			if (fatigue >= 96) {
				actionSender
						.sendMessage("@gre@You start to feel tired, maybe you should rest soon.");
			}
			if (i >= 3 && useFatigue) {
				fatigue++;
				actionSender.sendFatigue();
			}
		}
		if (combat && i < 3
				&& (combatStyleToIndex() != i && getCombatStyle() != 0)) {
			// fix for accidental exp in other stats?
			return;
		}

		double exprate = Config.expRate;
		if (isSubscriber()) {
			exprate = Config.subExpRate;
		}

		if (getLocation().wildernessLevel() > 1) {
			if (combat)
				exprate += Config.WILD_COMBAT_BONUS;
			if (getLocation().wildernessLevel() > Config.WILD_LEVEL_FOR_NON_COMBAT_BONUS
					&& !combat)
				exprate += Config.WILD_NON_COMBAT_BONUS;
		}

		exp[i] += amount * exprate;
		if (exp[i] < 0) {
			exp[i] = 0;
		}

		int level = Formulae.experienceToLevel(exp[i]);
		if (level != maxStat[i]) {
			int advanced = level - maxStat[i];
			incCurStat(i, advanced);
			incMaxStat(i, advanced);
			int stat = this.getMaxStat(i);
			if (stat == 99 && Config.CONGRATS_FOR_MAX_LEVEL) {
				for (Player p : world.getPlayers()) {
					if (p != null) {
						p.getActionSender()
								.sendMessage(
										"#adm##pmd#@gre@**********************************#pmd##adm#");
						p.getActionSender()
								.sendMessage(
										"@yel@Congratulations "
												+ this.getUsername()
												+ " has just reached the maximum level (99)!! in "
												+ Formulae.statArray[i]);
						p.getActionSender()
								.sendMessage(
										"#adm##pmd#@gre@*********************************#pmd##adm#");
					}
				}
			}
			actionSender.sendStat(i);
			actionSender.sendMessage("@gre@You just advanced " + advanced + " "
					+ Formulae.statArray[i] + " level"
					+ (advanced > 1 ? "s" : "") + "!");
			actionSender.sendSound("advance");
			Instance.getDelayedEventHandler().add(new MiniEvent(this) {

				public void action() {
					owner.getActionSender().sendScreenshot();
				}
			});
			int comb = Formulae.getCombatlevel(maxStat);
			if (comb != getCombatLevel()) {
				setCombatLevel(comb);
			}
		}
	}

	public void incMaxStat(int i, int amount) {
		maxStat[i] += amount;
		if (maxStat[i] < 0) {
			maxStat[i] = 0;
		}
	}

	public void incQuestPoints(int amount) {
		setQuestPoints(getQuestPoints() + amount, true);
	}

	public List<Player> infectedBlood() {
		List<Player> playersInView = viewArea.getPlayersInView();
		List<Player> radiusPlayers = new ArrayList<Player>();
		for (Player p : playersInView) {
			if ((p.getX() - getX() <= 2 || p.getX() - getY() >= -2)
					&& (p.getY() - getY() <= 2 || p.getY() - getY() >= -2)
					&& !p.isInfected()) {
				radiusPlayers.add(p);
			}
		}
		return radiusPlayers;
	}

	public void informOfBubble(Bubble b) {
		bubblesNeedingDisplayed.add(b);
	}

	public void informOfChatMessage(ChatMessage cm) {
		chatMessagesNeedingDisplayed.add(cm);
	}

	public void informOfModifiedHits(Mob mob) {
		if (mob instanceof Player) {
			playersNeedingHitsUpdate.add((Player) mob);
		} else if (mob instanceof Npc) {
			npcsNeedingHitsUpdate.add((Npc) mob);
		}
	}

	public void informOfNpcMessage(ChatMessage cm) {
		npcMessagesNeedingDisplayed.add(cm);
	}

	/**
	 * This is a 'another player has tapped us on the shoulder' method.
	 * 
	 * If we are in another players viewArea, they should in theory be in ours.
	 * So they will call this method to let the player know they should probably
	 * be informed of them.
	 */
	public void informOfPlayer(Player p) {
		if ((!watchedPlayers.contains(p) || watchedPlayers.isRemoving(p))
				&& withinRange(p)) {
			watchedPlayers.add(p);
		}
	}

	public void informOfProjectile(Projectile p) {
		projectilesNeedingDisplayed.add(p);
	}

	public boolean initialized() {
		return initialized;
	}

	public boolean isAdmin() {
		return groupID == 10;
	}

	public boolean isBadClient() {
		return badClient;
	}

	public boolean isChangingAppearance() {
		return changingAppearance;
	}

	public boolean isCharged() {
		return GameEngine.getTime() - lastCharge < 600000;
	}

	public boolean isClientWarn() {
		return clientWarn;
	}

	public boolean isDestroy() {
		return destroy;
	}

	public boolean isDoricDependency() {
		return doricDependency;
	}

	public boolean isDuelConfirmAccepted() {
		return duelConfirmAccepted;
	}

	public boolean isDueling() {
		return isDueling;
	}

	public boolean isDuelOfferAccepted() {
		return duelOfferAccepted;
	}

	public boolean isFirstMajorUpdateSent() {
		return firstMajorUpdateSent;
	}

	public boolean isFlagCarrier() {
		return flagCarrier;
	}

	public boolean isFollowing() {
		return followEvent != null && following != null;
	}

	public boolean isFriendsWith(long usernameHash) {
		return friendList.containsKey(usernameHash);
	}

	public boolean isHasAnswered() {
		return hasAnswered;
	}

	public boolean isIgnoring(long usernameHash) {
		return ignoreList.contains(usernameHash);
	}

	public boolean isInBank() {
		return inBank;
	}

	public boolean isInfected() {
		return infected;
	}

	public boolean isInitialized() {
		return initialized;
	}

	public boolean isInQuiz() {
		return inQuiz;
	}

	public boolean isInvis() {
		return invis;
	}

	public boolean isLoggedIn() {
		return loggedIn;
	}

	public boolean isMale() {
		return maleGender;
	}

	public boolean isMaleGender() {
		return maleGender;
	}

	public boolean isMining() {
		return isMining;
	}

	public void isMining(boolean arg) {
		isMining = arg;
	}

	public boolean isMod() {
		return groupID == 7 || isAdmin();
	}

	/**
	 * Is this player muted?
	 * 
	 * @return
	 */
	public boolean isMuted() {
		return (muted - GameEngine.getTime() > 0);
	}

	public boolean isNoclip() {
		return noclip;
	}

	public boolean isNonaggro() {
		return nonaggro;
	}

	public boolean isNopk() {
		return nopk;
	}

	public boolean isNoPK() {
		return nopk;
	}

	public boolean isPacketSpam() {
		return packetSpam;
	}

	public boolean isPMod() {
		return groupID == 5 || isMod() || isAdmin();
	}

	public boolean isRanging() {
		return rangeEvent != null;
	}

	public boolean isReconnecting() {
		return reconnecting;
	}

	public boolean isRequiresOfferUpdate() {
		return requiresOfferUpdate;
	}

	public boolean isSkulled() {
		return skullEvent != null;
	}

	public boolean isSleeping() {
		return isSleeping;
	}

	public boolean isSubscriber() {
		return groupID == 2;
	}

	public boolean isSuspicious() {
		return suspicious;
	}

	public boolean isTradeConfirmAccepted() {
		return tradeConfirmAccepted;
	}

	public boolean isTradeOfferAccepted() {
		return tradeOfferAccepted;
	}

	public boolean isTrading() {
		return isTrading;
	}

	public void killedBy(Mob mob) {
		killedBy(mob, false);
	}

	public void killedBy(Mob mob, boolean stake) {
		if (!loggedIn) {
			Logger.error(username + " not logged in, but killed!");
			return;
		}
		if (mob instanceof Player) {
			Player player = (Player) mob;
			player.getActionSender().sendMessage(
					"You have defeated " + getUsername() + "!");
			player.getActionSender().sendSound("victory");
			Instance.getDelayedEventHandler().add(new MiniEvent(player) {
				public void action() {
					owner.getActionSender().sendScreenshot();
				}
			});
			Instance.getServer().getLoginConnector().getActionSender()
					.logKill(player.getUsernameHash(), usernameHash, stake);
		}
		Mob opponent = super.getOpponent();
		if (opponent != null) {
			opponent.resetCombat(CombatState.WON);
		}
		actionSender.sendSound("death");
		lastDeath = GameEngine.getTime();
		actionSender.sendDied();
		for (int i = 0; i < 18; i++) {
			curStat[i] = maxStat[i];
			actionSender.sendStat(i);
		}

		Player player = mob instanceof Player ? (Player) mob : null;
		if (stake) {
			if (player == null) {
				Logger.println("Player is null (not dropping item): "
						+ this.getUsername());
			}
			for (InvItem item : duelOffer) {
				InvItem affectedItem = getInventory().get(item);
				if (affectedItem == null) {
					setSuspiciousPlayer(true);
					Logger.error("Missing staked item [" + item.getID() + ", "
							+ item.getAmount() + "] from = " + usernameHash
							+ "; to = " + player.getUsernameHash() + ";");
					continue;
				}
				if (affectedItem.isWielded()) {
					affectedItem.setWield(false);
					updateWornItems(
							affectedItem.getWieldableDef().getWieldPos(),
							getPlayerAppearance().getSprite(
									affectedItem.getWieldableDef()
											.getWieldPos()));
				}
				getInventory().remove(item);
				final long playerhash = DataConversions.usernameToHash(player
						.getUsername());
				DuelLog.sendlog(playerhash, usernameHash, item.getID(),
						item.getAmount(), getX(), getY(), 2);
				// newItem.setdroppedby(getUsernameHash());
				world.registerItem(new Item(item.getID(), getX(), getY(), item
						.getAmount(), player));
			}
		} else {
			inventory.sort();
			ListIterator<InvItem> iterator = inventory.iterator();
			if (!isSkulled()) {
				for (int i = 0; i < 3 && iterator.hasNext(); i++) {
					if ((iterator.next()).getDef().isStackable()) {
						iterator.previous();
						break;
					}
				}
			}
			if (activatedPrayers[8] && iterator.hasNext()) {
				if (((InvItem) iterator.next()).getDef().isStackable()) {
					iterator.previous();
				}
			}
			for (int slot = 0; iterator.hasNext(); slot++) {
				InvItem item = (InvItem) iterator.next();
				if (item.isWielded()) {
					item.setWield(false);
					updateWornItems(item.getWieldableDef().getWieldPos(),
							appearance.getSprite(item.getWieldableDef()
									.getWieldPos()));
				}
				iterator.remove();
				world.registerItem(new Item(item.getID(), getX(), getY(), item
						.getAmount(), player));
			}
			removeSkull();
		}
		world.registerItem(new Item(20, getX(), getY(), 1, player));

		for (int x = 0; x < activatedPrayers.length; x++) {
			if (activatedPrayers[x]) {
				activatedPrayers[x] = false;
			}
			removePrayerDrain(x);
		}
		actionSender.sendPrayers();

		setLocation(Point.location(122, 647), true);
		Collection<Player> allWatched = watchedPlayers.getAllEntities();
		for (Player p : allWatched) {
			p.removeWatchedPlayer(this);
		}

		resetPath();
		resetCombat(CombatState.LOST);
		actionSender.sendWorldInfo();
		actionSender.sendEquipmentStats();
		actionSender.sendInventory();
	}

	public long lastAttackedBy(Player p) {
		Long time = attackedBy.get(p.getUsernameHash());
		if (time != null) {
			return time;
		}
		return 0;
	}

	public long lastInfected() {
		return lastInfected;
	}

	public void load(String username, String password, int uid,
			boolean reconnecting) {
		try {
			setID(uid);
			this.password = password;
			this.reconnecting = reconnecting;
			usernameHash = DataConversions.usernameToHash(username);
			this.username = DataConversions.hashToUsername(usernameHash);

			Instance.getServer().getLoginConnector().getActionSender()
					.playerLogin(this);
			final Player p = this;
			Instance.getDelayedEventHandler().add(
					new DelayedEvent(this, 60000) {

						private void checkStat(int statIndex) {
							if (statIndex != 3
									&& owner.getCurStat(statIndex) == owner
											.getMaxStat(statIndex)) {
								owner.getActionSender()
										.sendMessage(
												"Your "
														+ Formulae.statArray[statIndex]
														+ " ability has returned to normal.");
							}
						}

						public void run() {
							if(p == null || p.isDestroy()) {
								this.stop();
							}
							for (int statIndex = 0; statIndex < 18; statIndex++) {
								if (statIndex == 5) {
									continue;
								}// addByte(-1
								int curStat = getCurStat(statIndex);
								int maxStat = getMaxStat(statIndex);
								if (curStat > maxStat) {
									setCurStat(statIndex, curStat - 1);
									getActionSender().sendStat(statIndex);
									checkStat(statIndex);
								}// sendAppear
								else if (curStat < maxStat) {
									setCurStat(statIndex, curStat + 1);
									getActionSender().sendStat(statIndex);
									checkStat(statIndex);
								}
							}
						}
					});
			drainer = new DelayedEvent(this, Integer.MAX_VALUE) {

				public void run() {
					if(p == null || p.isDestroy()) {
						this.stop();
					}
					int curPrayer = getCurStat(5);
					if (getDrainRate() > 0 && curPrayer > 0) {
						incCurStat(5, -1);
						getActionSender().sendStat(5);
						if (curPrayer <= 1) {
							for (int prayerID = 0; prayerID < 14; prayerID++) {
								setPrayer(prayerID, false);
							}
							setDrainRate(0);
							setDelay(Integer.MAX_VALUE);
							getActionSender()
									.sendMessage(
											"You have run out of prayer points. Return to a church to recharge");
							getActionSender().sendPrayers();
						}
					}
					if (drainRate != 0) {
						setDelay((int) (240000 / drainRate));
					}
				}
			};
			Instance.getDelayedEventHandler().add(drainer);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean loggedIn() {
		return loggedIn;
	}

	private boolean needsAppearanceUpdateFor(Player p) {
		int playerServerIndex = p.getIndex();
		if (knownPlayersAppearanceIDs.containsKey(playerServerIndex)) {
			int knownPlayerAppearanceID = knownPlayersAppearanceIDs
					.get(playerServerIndex);
			if (knownPlayerAppearanceID != p.getAppearanceID()) {
				return true;
			}
		} else {
			return true;
		}
		return false;
	}

	/**
	 * Restricts P2P stuff in wilderness.
	 */
	public void p2pWildy() {
		if (Config.f2pWildy) {

			boolean found = false;
			for (InvItem i : getInventory().getItems()) {
				if (i.isWielded() && i.getDef().isMembers()) {
					WieldHandler.unWieldItem(this, i, true);
					getActionSender()
							.sendMessage(
									"Your "
											+ i.getDef().name
											+ " has been un-equipped. (P2P Item not allowed in wilderness)");
					found = true;
				}
			}
			if (found) {
				getActionSender().sendInventory();
				getActionSender().sendEquipmentStats();
			}
			for (int i = 0; i < 3; i++) {
				int min = getCurStat(i);
				int max = getMaxStat(i);
				int baseStat = getCurStat(i) > getMaxStat(i) ? getMaxStat(i)
						: getCurStat(i);
				int newStat = baseStat
						+ DataConversions.roundUp((getMaxStat(i) / 100D) * 10)
						+ 2;
				if (min > newStat || (min > max && (i == 1 || i == 0))) {
					setCurStat(i, max);
					getActionSender()
							.sendMessage(
									"Your super/p2p potion effect has been removed, F2P Wilderness items only");
					getActionSender().sendStat(i);
				}
			}
			if (getCurStat(4) > getMaxStat(4)) {
				setCurStat(4, getMaxStat(4));
				getActionSender()
						.sendMessage(
								"Your super/p2p potion effect has been removed, F2P Wilderness items only");
				getActionSender().sendStat(4);
			}
		}
	}

	public void ping() {
		lastPing = GameEngine.getTime();
	}

	public void remove() {
		removed = true;
	}

	public void removeFriend(long id) {
		friendList.remove(id);
	}

	public void removeIgnore(long id) {
		ignoreList.remove(id);
	}

	// p2p

	public void removePrayerDrain(int prayerID) {
		addPrayerDrain(prayerID);
	}

	public void removeSkull() {
		if (!isSkulled()) {
			return;
		}
		super.setAppearnceChanged(true);
		skullEvent.stop();
		skullEvent = null;
	}

	public void removeWatchedNpc(Npc n) {
		watchedNpcs.remove(n);
	}

	public void removeWatchedPlayer(Player p) {
		watchedPlayers.remove(p);
	}

	public boolean requiresOfferUpdate() {
		return requiresOfferUpdate;
	}

	public void resetAll() {
		resetAllExceptTradeOrDuel();
		resetTrade();
		resetDuel();
	}

	public void resetAllExceptDueling() {
		resetAllExceptTradeOrDuel();
		resetTrade();
	}

	private void resetAllExceptTradeOrDuel() {
		if (getMenuHandler() != null) {
			resetMenuHandler();
		}
		if (accessingBank()) {
			resetBank();
		}
		if (accessingShop()) {
			resetShop();
		}
		if (interactingNpc != null) {
			interactingNpc.unblock();
		}
		if (isFollowing()) {
			resetFollowing();
		}
		if (isRanging()) {
			resetRange();
		}
		setStatus(Action.IDLE);
	}

	public void resetAllExceptTrading() {
		resetAllExceptTradeOrDuel();
		resetDuel();
	}

	public void resetBank() {
		setAccessingBank(false);
		actionSender.hideBank();
	}

	public void resetDuel() {
		Player opponent = getWishToDuel();
		if (opponent != null) {
			opponent.resetDueling();
		}
		resetDueling();
	}

	public void resetDueling() {
		if (isDueling()) {
			actionSender.sendDuelWindowClose();
			setStatus(Action.IDLE);
		}
		setWishToDuel(null);
		setDueling(false);
		setDuelOfferAccepted(false);
		setDuelConfirmAccepted(false);
		resetDuelOffer();
		clearDuelOptions();
	}

	public void resetDuelOffer() {
		duelOffer.clear();
	}

	public void resetFollowing() {
		following = null;
		if (followEvent != null) {
			followEvent.stop();
			followEvent = null;
		}
		resetPath();
	}

	public void resetMenuHandler() {
		menuHandler = null;
		actionSender.hideMenu();
	}

	public void resetRange() {
		if (rangeEvent != null) {
			rangeEvent.stop();
			// Instance.getDelayedEventHandler().remove(rangeEvent);
			rangeEvent = null;
		}
		setStatus(Action.IDLE);
	}

	public void resetShop() {
		if (shop != null) {
			shop.removePlayer(this);
			shop = null;
			actionSender.hideShop();
		}
	}

	public void resetTrade() {
		Player opponent = getWishToTrade();
		if (opponent != null) {
			opponent.resetTrading();
		}
		resetTrading();
	}

	public void resetTradeOffer() {
		tradeOffer.clear();
	}

	// drain
	public void resetTrading() {
		if (isTrading()) {
			actionSender.sendTradeWindowClose();
			setStatus(Action.IDLE);
		}
		setWishToTrade(null);
		setTrading(false);
		setTradeOfferAccepted(false);
		setTradeConfirmAccepted(false);
		resetTradeOffer();
	}

	public void revalidateWatchedItems() {
		for (Item i : watchedItems.getKnownEntities()) {
			if (!withinRange(i) || i.isRemoved() || !i.visibleTo(this)) {
				watchedItems.remove(i);
			}
		}
	}

	public void revalidateWatchedNpcs() {
		for (Npc n : watchedNpcs.getKnownEntities()) {
			if (!withinRange(n) || n.isRemoved()) {
				watchedNpcs.remove(n);
			}
		}
	}

	public void revalidateWatchedObjects() {
		for (GameObject o : watchedObjects.getKnownEntities()) {
			if (!withinRange(o) || o.isRemoved()) {
				watchedObjects.remove(o);
			}
		}
	}

	public void revalidateWatchedPlayers() {
		for (Player p : watchedPlayers.getKnownEntities()) {
			if (!withinRange(p) || !p.loggedIn()) {
				watchedPlayers.remove(p);
				knownPlayersAppearanceIDs.remove(p.getIndex());
			}
		}
	}// destroy

	public void save() {

		SavePacketBuilder builder = new SavePacketBuilder();
		builder.setPlayer(this);
		LSPacket temp = builder.getPacket();
		if (temp != null) {
			Instance.getServer().getLoginConnector().getSession().write(temp);
		}

	}

	public void sayMessage(String msg, Mob to) {
		ChatMessage cm = new ChatMessage(this, msg, to);
		chatMessagesNeedingDisplayed.add(cm);
	}

	public void setAccessingBank(boolean b) {
		inBank = b;
	}

	public void setAccessingShop(Shop shop) {
		this.shop = shop;
		if (shop != null) {
			shop.addPlayer(this);
		}
	}

	public void setActionSender(MiscPacketBuilder actionSender) {
		this.actionSender = actionSender;
	}

	/**
	 * Sets this player's current agility course
	 */
	public void setAgilityCourseDef(AgilityCourseDef def) {
		agilityCourseDef = def;
	}

	public void setAppearance(PlayerAppearance appearance) {
		this.appearance = appearance;
	}

	public void setArrowFired() {
		lastArrow = GameEngine.getTime();
	}

	public void setAttackedBy(HashMap<Long, Long> attackedBy) {
		this.attackedBy = attackedBy;
	}

	public void setBadClient(boolean badClient) {
		this.badClient = badClient;
	}

	public void setBank(Bank b) {
		bank = b;
	}

	public void setBlink(boolean arg) {
		blink = arg;
	}

	public void setBubblesNeedingDisplayed(
			ArrayList<Bubble> bubblesNeedingDisplayed) {
		this.bubblesNeedingDisplayed = bubblesNeedingDisplayed;
	}

	public void setCastTimer() {
		lastSpellCast = GameEngine.getTime();
	}

	public void setChangingAppearance(boolean b) {
		changingAppearance = b;
	}

	public void setCharged() {
		lastCharge = GameEngine.getTime();
	}

	public void setChatMessagesNeedingDisplayed(
			ArrayList<ChatMessage> chatMessagesNeedingDisplayed) {
		this.chatMessagesNeedingDisplayed = chatMessagesNeedingDisplayed;
	}

	public void setChatQueue(LinkedList<ChatMessage> chatQueue) {
		this.chatQueue = chatQueue;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public void setClick(int click) {
		this.click = click;
	}

	public void setClientWarn(boolean clientWarn) {
		this.clientWarn = clientWarn;
	}

	public void setCombatStyle(int style) {
		combatStyle = style;
	}

	public void setCombo(int combo) {
		this.Combo = combo;
	}

	public void setCorrectSleepword(String correctSleepword) {
		this.correctSleepword = correctSleepword;
	}

	/**
	 * Sets this player's current course object
	 */
	public void setCurrentCourseObject(int order) {
		currentCourseObject = order;
	}

	public void setCurrentIP(String currentIP) {
		this.currentIP = currentIP;
	}

	public void setCurrentLogin(long currentLogin) {
		this.currentLogin = currentLogin;
	}

	public void setCurStat(int id, int lvl) {
		if (lvl <= 0) {
			lvl = 0;
		}
		curStat[id] = lvl;
	}

	public void setCurStat(int[] curStat) {
		this.curStat = curStat;
	}

	public void setDestroy(boolean destroy) {
		this.destroy = destroy;
	}

	public void setDoricDependency(boolean doricDependency) {
		this.doricDependency = doricDependency;
	}

	public void setDrainer(DelayedEvent drainer) {
		this.drainer = drainer;
	}

	public void setDrainerDelay(int drainerDelay) {
		this.drainerDelay = drainerDelay;
	}

	public void setDrainRate(int rate) {
		drainRate = rate;
	}

	public void setDuelConfirmAccepted(boolean b) {
		duelConfirmAccepted = b;
	}

	public void setDueling(boolean b) {
		isDueling = b;
	}

	public void setDuelOffer(ArrayList<InvItem> duelOffer) {
		this.duelOffer = duelOffer;
	}

	public void setDuelOfferAccepted(boolean b) {
		duelOfferAccepted = b;
	}

	public void setDuelOptions(boolean[] duelOptions) {
		this.duelOptions = duelOptions;
	}

	public void setDuelSetting(int i, boolean b) {
		duelOptions[i] = b;
	}

	public void setEventCD(long eventcd) {
		this.eventcd = eventcd;
	}

	public void setExp(int id, int lvl) {
		if (lvl < 0) {
			lvl = 0;
		}
		exp[id] = lvl;
	}

	public void setExp(int[] lvls) {
		exp = lvls;
	}

	public void setFatigue(int fatigue) {
		this.fatigue = fatigue;
	}

	public void setFirstMajorUpdateSent(boolean firstMajorUpdateSent) {
		this.firstMajorUpdateSent = firstMajorUpdateSent;
	}

	public void setFlagCarrier(boolean flagCarrier) {
		this.flagCarrier = flagCarrier;
		getActionSender().sendEquipmentStats();
	}

	public void setFollowEvent(DelayedEvent followEvent) {
		this.followEvent = followEvent;
	}

	public void setFollowing(Mob mob) {
		setFollowing(mob, 0);
	}

	public void setFollowing(final Mob mob, final int radius) {
		if (isFollowing()) {
			resetFollowing();
		}
		following = mob;
		followEvent = new DelayedEvent(this, 500) {

			public void run() {
				if (!owner.withinRange(mob) || mob.isRemoved()
						|| (owner.isBusy() && !owner.isDueling())) {
					resetFollowing();
					this.stop();
				} else if (!owner.finishedPath()
						&& owner.withinRange(mob, radius)) {
					owner.resetPath();
				} else if (owner.finishedPath()
						&& !owner.withinRange(mob, radius + 1)) {
					owner.setPath(new Path(owner.getX(), owner.getY(), mob
							.getX(), mob.getY()));
				}
			}
		};
		Instance.getDelayedEventHandler().add(followEvent);
	}

	public void setFriendList(TreeMap<Long, Integer> friendList) {
		this.friendList = friendList;
	}

	public void setGameSetting(int i, boolean b) {
		gameSettings[i] = b;
	}

	public void setGameSettings(boolean[] gameSettings) {
		this.gameSettings = gameSettings;
	}

	public void setGroupID(int id) {
		groupID = id;
	}

	public void setHasAnswered(boolean hasAnswered) {
		this.hasAnswered = hasAnswered;
	}

	public void setHits(int lvl) {
		setCurStat(3, lvl);
	}

	public void setIgnoreList(ArrayList<Long> ignoreList) {
		this.ignoreList = ignoreList;
	}

	public void setInBank(boolean inBank) {
		this.inBank = inBank;
	}

	public void setInfected() {
		infected = true;
		getActionSender().sendMessage("You have been afflicted by the plague.");
	}

	public void setInitialized() {
		initialized = true;
	}

	public void setInitialized(boolean initialized) {
		this.initialized = initialized;
	}

	public void setInQuiz(boolean inQuiz) {
		this.inQuiz = inQuiz;
	}

	public void setInteractingNpc(Npc interactingNpc) {
		this.interactingNpc = interactingNpc;
	}

	public void setInterpreter(Interpreter interpreter) {
		this.interpreter = interpreter;
	}

	public void setInterpreterThread(Thread interpreterThread) {
		this.interpreterThread = interpreterThread;
	}

	public void setIntervals(LinkedList<Long> intervals) {
		this.intervals = intervals;
	}

	public void setInventory(Inventory i) {
		inventory = i;
	}

	public void setInvis(boolean invis) {
		this.invis = invis;
	}

	public void setIoSession(IoSession ioSession) {
		this.ioSession = ioSession;
	}

	public void setKillStreak(int killStreak) {
		this.killStreak = killStreak;
	}

	public void setKnownPlayersAppearanceIDs(
			HashMap<Integer, Integer> knownPlayersAppearanceIDs) {
		this.knownPlayersAppearanceIDs = knownPlayersAppearanceIDs;
	}

	public void setLastAnswer(String lastAnswer) {
		this.lastAnswer = lastAnswer;
	}

	public void setLastArrow(long lastArrow) {
		this.lastArrow = lastArrow;
	}

	public void setLastCast(long lastCast) {
		this.lastCast = lastCast;
	}

	public void setLastCharge(long lastCharge) {
		this.lastCharge = lastCharge;
	}

	public void setLastCommandUsed(long lastCommandUsed) {
		this.lastCommandUsed = lastCommandUsed;
	}

	public void setLastCount(long lastCount) {
		this.lastCount = lastCount;
	}

	public void setLastDeath(long lastDeath) {
		this.lastDeath = lastDeath;
	}

	public void setLastDepositTime(int itemid, int amount) {
		this.lastbanktime = GameEngine.getTime();

		lastdepositeditem = itemid;
		lastdepositedamount = amount;
	}

	public void setLastInfected() {
		lastInfected = GameEngine.getTime();
	}

	public void setLastInterval(long lastInterval) {
		this.lastInterval = lastInterval;
	}

	public void setLastInvTime(int itemposition) {
		this.lastinvtime = GameEngine.getTime();
		lastitemposition = itemposition;
	}

	public void setLastIP(String ip) {
		lastIP = ip;
	}

	public void setLastLogin(long l) {
		lastLogin = l;
	}

	public void setLastMineTimer(long lastMineTimer) {
		this.lastMineTimer = lastMineTimer;
	}

	public void setLastNpcChasingYou(Npc lastNpcChasingYou) {
		this.lastNpcChasingYou = lastNpcChasingYou;
	}

	public void setLastNPCChat(long lastNPCChat) {
		this.lastNPCChat = lastNPCChat;
	}

	public void setLastOption(int lastOption) {
		this.lastOption = lastOption;
	}

	public void setLastOptions(String[] lastOptions) {
		this.lastOptions = lastOptions;
	}

	public void setLastPacketRecTime(long lastPacketRecTime) {
		this.lastPacketRecTime = lastPacketRecTime;
	}

	public void setLastPacketTime(long lastPacketTime) {
		this.lastPacketTime = lastPacketTime;
	}

	public void setLastPing(long lastPing) {
		this.lastPing = lastPing;
	}

	public void setLastPlayerInfo2(String lastPlayerInfo2) {
		this.lastPlayerInfo2 = lastPlayerInfo2;
	}

	// Players Online
	// sendPlayers
	// killedby
	/**
	 * Sets this player's last quest menu reply
	 */
	public void setLastQuestMenuReply(int i) {
		lastQuestMenuReply = i;
	}

	public void setLastRandom(int lastRandom) {
		this.lastRandom = lastRandom;
	}

	public void setLastRange(long lastRange) {
		this.lastRange = lastRange;
	}

	public void setLastReport() {
		lastReport = GameEngine.getTime();
	}

	public void setLastReport(long lastReport) {
		this.lastReport = lastReport;
	}

	public void setLastRun(long lastRun) {
		this.lastRun = lastRun;
	}

	public void setLastSaveTime(long save) {
		lastSaveTime = save;
	}

	public void setLastSleepTime(long save) {
		lastSleepTime = save;
	}

	public void setLastSpellCast(long lastSpellCast) {
		this.lastSpellCast = lastSpellCast;
	}

	public void setLastTradeDuelRequest(long lastTradeDuelRequest) {
		this.lastTradeDuelRequest = lastTradeDuelRequest;
	}

	public void setLastWithdrawTime(int itemid, int amount) {
		this.lastwithdrawtime = GameEngine.getTime();

		lastwithdrawitem = itemid;
		lastwithdrawamount = amount;
	}

	public void setLoggedIn(boolean loggedIn) {
		if (loggedIn) {
			currentLogin = GameEngine.getTime();
		}
		this.loggedIn = loggedIn;
	}

	public void setLoginTime(long loginTime) {
		this.loginTime = loginTime;
	}

	public void setLoops(int loops) {
		this.loops = loops;
	}

	public void setMale(boolean male) {
		maleGender = male;
	}

	public void setMaleGender(boolean maleGender) {
		this.maleGender = maleGender;
	}

	public void setMaxStat(int id, int lvl) {
		if (lvl < 0) {
			lvl = 0;
		}
		maxStat[id] = lvl;
	}

	public void setMaxStat(int[] maxStat) {
		this.maxStat = maxStat;
	}

	public void setMenuHandler(MenuHandler menuHandler) {
		menuHandler.setOwner(this);
		this.menuHandler = menuHandler;
	}

	public void setMining(boolean isMining) {
		this.isMining = isMining;
	}

	/**
	 * Sets the mute time.
	 * 
	 * @param muted
	 *            EPOCH time how long is the player muted. (Multiplied by 1000
	 *            in this)
	 */
	public void setMuted(long muted) {
		this.muted = (muted * 1000);
	}

	// Added by Konijn
	public void setNoclip(boolean noclip) {
		this.noclip = noclip;
	}

	public void setnonaggro(boolean arg) {
		nonaggro = arg;
	}

	public void setNonaggro(boolean nonaggro) {
		this.nonaggro = nonaggro;
	}

	public void setnopk(boolean arg) {
		nopk = arg;
	}

	public void setNopk(boolean nopk) {
		this.nopk = nopk;
	}

	public void setNpc(Npc npc) {
		// Logging.debug("setNpc(npc)");
		interactingNpc = npc;
	}

	public void setNpcMessagesNeedingDisplayed(
			ArrayList<ChatMessage> npcMessagesNeedingDisplayed) {
		this.npcMessagesNeedingDisplayed = npcMessagesNeedingDisplayed;
	}

	public void setNpcsNeedingHitsUpdate(ArrayList<Npc> npcsNeedingHitsUpdate) {
		this.npcsNeedingHitsUpdate = npcsNeedingHitsUpdate;
	}

	public void setNpcThief(boolean[] npcThief) {
		this.npcThief = npcThief;
	}

	public void setOwner(int owner) {
		this.owner = owner;
	}

	public void setPacketCount(int packetCount) {
		this.packetCount = packetCount;
	}

	public void setPacketSpam(boolean packetSpam) {
		this.packetSpam = packetSpam;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setPlayersNeedingHitsUpdate(
			ArrayList<Player> playersNeedingHitsUpdate) {
		this.playersNeedingHitsUpdate = playersNeedingHitsUpdate;
	}

	public void setPrivacySetting(int i, boolean b) {
		privacySettings[i] = b;
	}

	public void setPrivacySettings(boolean[] privacySettings) {
		this.privacySettings = privacySettings;
	}

	public void setProjectilesNeedingDisplayed(
			ArrayList<Projectile> projectilesNeedingDisplayed) {
		this.projectilesNeedingDisplayed = projectilesNeedingDisplayed;
	}

	public void setQuestMenuHandler(MenuHandler menuHandler) {
		this.menuHandler = menuHandler;
		menuHandler.setOwner(this);
		actionSender.sendMenu(menuHandler.getOptions());
	}

	public void setQuestPoints(int questPoints) {
		this.questPoints = questPoints;
	}

	public void setQuestPoints(int newquestPoints, boolean save) {
		int old = questPoints;
		questPoints = newquestPoints;
		int gained = questPoints - old;

		if (save) {
			// save();
			setLastSaveTime(GameEngine.getTime());
			getActionSender().sendQuestInfo();
			getActionSender().sendMessage(
					"@gre@You just gained " + gained + " quest point"
							+ (gained > 1 ? "s" : "") + "!");
		}
	}

	public void setQuestStage(HashMap<Integer, Integer> questStage) {
		this.questStage = questStage;
	}

	public void setQuestStage(int qid, int stage) {
		setQuestStage(qid, stage, true);
	}

	public void setQuestStage(int questId, int stage, boolean save) {
		setQuestStage(questId, stage, save, true);
	}

	public void setQuestStage(int questId, int stage, boolean save,
			boolean verbose) {
		Instance.getWorld();
		Quest q = World.getQuestManager().getQuestById(questId);

		if (q == null) {
			return;
		}

		questStage.put(questId, stage);

		if (save) {
			// save();
			getActionSender().sendQuestInfo();
		}

		if (verbose) {
			if (stage == 1) {
				getActionSender().sendMessage(
						"You have started the " + q.getName() + " quest!");
			} else if (stage == Quest.COMPLETE) {
				getActionSender().sendMessage(
						"You have completed the " + q.getName() + " quest!");
			}
		}
	}

	public void setQuestStage(Quest quest, int stage) {
		setQuestStage(quest.getUniqueID(), stage, true);
	}

	public void setQuizPoints(int quizPoints) {
		this.quizPoints = quizPoints;
	}

	// 335000
	public void setRangeEvent(RangeEvent event) {
		if (isRanging()) {
			resetRange();
		}
		rangeEvent = event;
		rangeEvent.setLastRun(lastArrow);
		setStatus(Action.RANGING_MOB);
		Instance.getDelayedEventHandler().add(rangeEvent);
	}

	public void setReconnecting(boolean reconnecting) {
		this.reconnecting = reconnecting;
	}

	public void setRequiresOfferUpdate(boolean b) {
		requiresOfferUpdate = b;
	}

	public void setServerKey(long key) {
		sessionKeys[2] = (int) (key >> 32);
		sessionKeys[3] = (int) key;
	}

	public void setSessionFlags(int sessionFlags) {
		this.sessionFlags = sessionFlags;
	}

	public boolean setSessionKeys(int[] keys) {
		boolean valid = (sessionKeys[2] == keys[2] && sessionKeys[3] == keys[3]);
		sessionKeys = keys;
		return valid;
	}

	public void setsEvent(ShortEvent sEvent) {
		this.sEvent = sEvent;
	}

	public void setSEvent(ShortEvent sEvent) {
		this.sEvent = sEvent;
		Instance.getDelayedEventHandler().add(sEvent);
	}

	public void setShop(Shop shop) {
		this.shop = shop;
	}

	public void setSkillLoops(int arg) {
		loops = arg;
	}

	public void setSkulledOn(Player player) {
		player.addAttackedBy(this);
		if (GameEngine.getTime() - lastAttackedBy(player) > 1200000) {
			addSkull(1200000);
		}
	}

	public void setSkullEvent(DelayedEvent skullEvent) {
		this.skullEvent = skullEvent;
	}

	public void setSleeping(boolean isSleeping) {
		this.isSleeping = isSleeping;
	}

	public void setSleepword(String sleepword) {
		this.sleepword = sleepword;
	}

	public void setSpam(boolean spam) {
		packetSpam = spam;
	}

	public void setSpellFail() {
		lastSpellCast = GameEngine.getTime() + 20000;
	}

	public void setStatus(Action a) {
		status = a;
	}

	public void setSubscriptionExpires(long expires) {
		subscriptionExpires = expires;
	}// 240000 / drainRate

	public void setSuspicious(boolean suspicious) {
		this.suspicious = suspicious;
	}

	public void setSuspiciousPlayer(boolean suspicious) {
		String stacktrace = "";
		Exception e = new Exception();
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw, true);
		e.printStackTrace(pw);
		pw.flush();
		sw.flush();
		stacktrace = sw.toString();
		this.suspicious = suspicious;
		Logger.println(this.getUsername()
				+ " was set suspicious! Stacktrace: \n" + stacktrace);
		Logger.systemerr(this.getUsername()
				+ " was set suspicious! Stacktrace: \n" + stacktrace);
		world.addEntryToSnapshots(new Activity(this.getUsername(), this
				.getUsername()
				+ " was set suspicious! Stacktrace: \n"
				+ stacktrace));
	}

	public void setTempx(int tempx) {
		this.tempx = tempx;
	}

	public void setTempy(int tempy) {
		this.tempy = tempy;
	}

	public void setTradeConfirmAccepted(boolean b) {
		tradeConfirmAccepted = b;
	}

	public void setTradeOffer(ArrayList<InvItem> tradeOffer) {
		this.tradeOffer = tradeOffer;
	}

	public void setTradeOfferAccepted(boolean b) {
		tradeOfferAccepted = b;
	}

	public void setTrading(boolean b) {
		isTrading = b;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setUsernameHash(long usernameHash) {
		this.usernameHash = usernameHash;
	}

	public void setWatchedItems(StatefulEntityCollection<Item> watchedItems) {
		this.watchedItems = watchedItems;
	}

	public void setWatchedNpcs(StatefulEntityCollection<Npc> watchedNpcs) {
		this.watchedNpcs = watchedNpcs;
	}

	public void setWatchedObjects(
			StatefulEntityCollection<GameObject> watchedObjects) {
		this.watchedObjects = watchedObjects;
	}

	public void setWatchedPlayers(
			StatefulEntityCollection<Player> watchedPlayers) {
		this.watchedPlayers = watchedPlayers;
	}

	public void setWishToDuel(Player p) {
		wishToDuel = p;
	}

	public void setWishToTrade(Player p) {
		wishToTrade = p;
	}

	public void setWornItems(int[] worn) {
		wornItems = worn;
		super.ourAppearanceChanged = true;
	}

	public void setWrongwords(int wrongwords) {
		this.wrongwords = wrongwords;
	}

	public void setWrongWords() {
		wrongwords += 1;
	}

	public boolean shouldRangePass() {
		int percent = (int) ((this.getMaxStat(5) - 40) * 0.6);
		percent += 60;
		if (percent > 100)
			percent = 100;
		if (DataConversions.random(0, 100) < percent)
			return false;
		else
			return true;
	}

	public boolean shouldThrowDepositError(int itemid, int amount) {
		if (GameEngine.getTime() - lastbanktime < 100
				&& lastdepositeditem == itemid && lastdepositedamount == amount)
			return false;
		if (amount == 0)
			return false;
		return true;
	}

	public boolean shouldThrowInvError(int itemposition) {
		if (GameEngine.getTime() - lastinvtime < 100
				&& lastitemposition == itemposition)
			return false;

		return true;
	}

	public boolean shouldThrowWithdrawError(int itemid, int amount) {
		if (GameEngine.getTime() - lastwithdrawtime < 100
				&& lastwithdrawitem == itemid && lastwithdrawamount == amount)
			return false;
		if (amount == 0)
			return false;
		return true;
	}

	public void teleport(int x, int y, boolean bubble) {
		Mob opponent = super.getOpponent();
		if (inCombat()) {
			resetCombat(CombatState.ERROR);
		}
		int count = getInventory().countId(318);
		if (count > 0) {
			for (int i = 0; i < count; i++) {
				getActionSender().sendMessage(
						"a mysterious force steals your Karamaja rum");
				if (getInventory().remove(new InvItem(318)) > -1) {
					continue;
				} else {
					break;
				}
			}
			getActionSender().sendInventory();
		}
		if (opponent != null) {
			opponent.resetCombat(CombatState.ERROR);
		}
		for (Object o : getWatchedPlayers().getAllEntities()) {
			Player p = ((Player) o);
			if (bubble) {
				p.getActionSender().sendTeleBubble(getX(), getY(), false);
			}
			p.removeWatchedPlayer(this);
		}
		if (bubble) {
			actionSender.sendTeleBubble(getX(), getY(), false);
		}
		setLocation(Point.location(x, y), true);
		resetPath();
		actionSender.sendWorldInfo();
	}

	@Override
	public String toString() {
		return "[Player:" + username + "]";
	}

	public boolean tradeDuelThrottling() {
		long now = GameEngine.getTime();
		if (now - lastTradeDuelRequest > 1000) {
			lastTradeDuelRequest = now;
			return false;
		}
		return true;
	}

	public void updateViewedItems() {
		List<Item> itemsInView = viewArea.getItemsInView();
		for (Item i : itemsInView) {
			if (!watchedItems.contains(i) && !i.isRemoved() && withinRange(i)
					&& i.visibleTo(this)) {
				watchedItems.add(i);
			}
		}
	}

	public void updateViewedNpcs() {
		List<Npc> npcsInView = viewArea.getNpcsInView();
		for (Npc n : npcsInView) {
			if (watchedNpcs.contains(n)) {
				if (!World.getQuestManager().isNpcVisible(n, this)
						&& !n.inCombat()) {
					watchedNpcs.remove(n);
				}
			}

			if ((!watchedNpcs.contains(n) || watchedNpcs.isRemoving(n))
					&& withinRange(n)) {
				if (World.getQuestManager().isNpcVisible(n, this)
						|| n.inCombat()) {
					watchedNpcs.add(n);
				}
			}
		}
	}

	// killed
	public void updateViewedObjects() {
		List<GameObject> objectsInView = viewArea.getGameObjectsInView();
		for (GameObject o : objectsInView) {
			if (!watchedObjects.contains(o) && !o.isRemoved() && withinRange(o)) {
				watchedObjects.add(o);
			}
		}
	}

	public void updateViewedPlayers() {
		List<Player> playersInView = viewArea.getPlayersInView();
		for (Player p : playersInView) {
			if (p.getIndex() != getIndex() && p.loggedIn()) {
				if (!p.isInvis()) {
					this.informOfPlayer(p);
				}
				if (p.isInvis() && isMod()) {
					this.informOfPlayer(p);
				}
				if (!this.isInvis()) {
					p.informOfPlayer(this);
				}
			}
		}
	}

	public void updateWornItems(int index, int id) {
		wornItems[index] = id;
		super.ourAppearanceChanged = true;
	}

	public boolean withinRange(Entity e) {
		int xDiff = location.getX() - e.getLocation().getX();
		int yDiff = location.getY() - e.getLocation().getY();
		return xDiff <= 16 && xDiff >= -15 && yDiff <= 16 && yDiff >= -15;
	}
}
