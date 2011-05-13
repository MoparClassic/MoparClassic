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
import java.util.TreeMap;
import java.util.Map.Entry;

import org.apache.mina.common.IoSession;
import org.moparscape.msc.config.Constants;
import org.moparscape.msc.config.Formulae;
import org.moparscape.msc.gs.Instance;
import org.moparscape.msc.gs.builders.MiscPacketBuilder;
import org.moparscape.msc.gs.builders.ls.SavePacketBuilder;
import org.moparscape.msc.gs.connection.LSPacket;
import org.moparscape.msc.gs.connection.RSCPacket;
import org.moparscape.msc.gs.db.DBConnection;
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
	// getX()

        private long eventcd = 0;
        public void setEventCD(long eventcd) {
                this.eventcd = eventcd;
        }
        public long getEventCD() {
                return eventcd;
        }

	private long lastbanktime = 0;
	private int lastdepositeditem = 0;
	private int lastdepositedamount = 0;
	
	private long lastwithdrawtime = 0;
	private int lastwithdrawitem = 0;
	private int lastwithdrawamount = 0;
	
	public void setLastWithdrawTime(int itemid, int amount) {
		this.lastwithdrawtime = System.currentTimeMillis();
		
		lastwithdrawitem = itemid;
		lastwithdrawamount = amount;
	}
	
	public void setLastDepositTime(int itemid, int amount) {
		this.lastbanktime = System.currentTimeMillis();
		
		lastdepositeditem = itemid;
		lastdepositedamount = amount;
	}
	public void setLastInvTime(int itemposition) {
		this.lastinvtime = System.currentTimeMillis();
		lastitemposition = itemposition;
	}
	public boolean shouldThrowDepositError(int itemid, int amount) {
		if(System.currentTimeMillis() - lastbanktime < 100 && lastdepositeditem == itemid && lastdepositedamount == amount)
			return false;
		if(amount == 0)
			return false;
		return true;
	}
	
	public boolean shouldThrowWithdrawError(int itemid, int amount) {
		if(System.currentTimeMillis() - lastwithdrawtime < 100 && lastwithdrawitem == itemid && lastwithdrawamount == amount)
			return false;
		if(amount == 0)
			return false;
		return true;
	}	
	private long lastinvtime = 0;
	private int lastitemposition = 0;
	
	public boolean shouldThrowInvError(int itemposition) {
		if(System.currentTimeMillis() - lastinvtime < 100 && lastitemposition == itemposition)
			return false;
		
		return true;
	}	
	public Thread interpreterThread = null;
	public int lastOption = -2;
	public String[] lastOptions = null;
	public Interpreter interpreter = new Interpreter();
	public int click = -1;
	public int tempx = -1;
	public boolean hasAdminPriv = false;
	public boolean hasModPriv = false;
	public boolean hasPmodPriv = false;
	public long lastCommandUsed = System.currentTimeMillis();
	/**
	 * Has the first major update for this player been sent? If not, we can't
	 * send them any minor updates.
	 */
	private boolean firstMajorUpdateSent = false;

	public boolean isFirstMajorUpdateSent() {
		return firstMajorUpdateSent;
	}

	public void setFirstMajorUpdateSent(boolean firstMajorUpdateSent) {
		this.firstMajorUpdateSent = firstMajorUpdateSent;
	}

	public Thread getInterpreterThread() {
		return interpreterThread;
	}

	public void setInterpreterThread(Thread interpreterThread) {
		this.interpreterThread = interpreterThread;
	}

	public int getLastOption() {
		return lastOption;
	}

	public void setLastOption(int lastOption) {
		this.lastOption = lastOption;
	}

	public String[] getLastOptions() {
		return lastOptions;
	}

	public void setLastOptions(String[] lastOptions) {
		this.lastOptions = lastOptions;
	}

	public Interpreter getInterpreter() {
		return interpreter;
	}

	public void setInterpreter(Interpreter interpreter) {
		this.interpreter = interpreter;
	}

	public int getClick() {
		return click;
	}

	public void setClick(int click) {
		this.click = click;
	}

	public int getTempx() {
		return tempx;
	}

	public void setTempx(int tempx) {
		this.tempx = tempx;
	}

	public int getTempy() {
		return tempy;
	}

	public void setTempy(int tempy) {
		this.tempy = tempy;
	}

	public Npc getLastNpcChasingYou() {
		return lastNpcChasingYou;
	}

	public void setLastNpcChasingYou(Npc lastNpcChasingYou) {
		this.lastNpcChasingYou = lastNpcChasingYou;
	}

	public long getLastNPCChat() {
		return lastNPCChat;
	}

	public void setLastNPCChat(long lastNPCChat) {
		this.lastNPCChat = lastNPCChat;
	}

	public boolean isDoricDependency() {
		return doricDependency;
	}

	public void setDoricDependency(boolean doricDependency) {
		this.doricDependency = doricDependency;
	}

	public LinkedList<Long> getIntervals() {
		return intervals;
	}

	public void setIntervals(LinkedList<Long> intervals) {
		this.intervals = intervals;
	}

	public long getLastInterval() {
		return lastInterval;
	}

	public void setLastInterval(long lastInterval) {
		this.lastInterval = lastInterval;
	}

	public int getKillStreak() {
		return killStreak;
	}

	public void setKillStreak(int killStreak) {
		this.killStreak = killStreak;
	}

	public int tempy = -1;
	public Npc lastNpcChasingYou = null;
	public long lastNPCChat = System.currentTimeMillis();
	public boolean doricDependency = false;

	public boolean flagCarrier = false;

	public boolean isFlagCarrier() {
		return flagCarrier;
	}

	public LinkedList<Long> intervals = new LinkedList<Long>();
	
	public long lastInterval = 0;

	public void addInterval() {
		if (lastInterval == 0) {
			lastInterval = System.currentTimeMillis();
		} else {
			intervals.addFirst(System.currentTimeMillis() - lastInterval);
			if (intervals.size() > 75) {
				intervals.removeLast();
			}
			lastInterval = System.currentTimeMillis();
		}
	}

	public void setFlagCarrier(boolean flagCarrier) {
		this.flagCarrier = flagCarrier;
		getActionSender().sendEquipmentStats();
	}

	/**
	 * How long is this player muted?
	 */
	private long muted = 0;

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

	/**
	 * Is this player muted?
	 * 
	 * @return
	 */
	public boolean isMuted() {
		return (muted - System.currentTimeMillis() > 0);
	}

	/**
	 * How many days is the user muted for?
	 * 
	 * @return day
	 */
	public int getDaysMuted() {
		return (int) ((muted - System.currentTimeMillis()) / 1000 / 3600 / 24);
	}

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
	// teleport
	public boolean badClient = false;
	/**
	 * Bank for banked items
	 */
	private Bank bank;
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
	private boolean clientWarn = false;
	/**
	 * Combat style: 0 - all, 1 - str, 2 - att, 3 - def
	 */
	private int combatStyle = 0;
	private int Combo = 0;
	/**
	 * Added by Zerratar: Correct sleepword we are looking for! Case SenSitIvE
	 */
	public String correctSleepword = "";
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
	/**
	 * DelayedEvent responsible for handling prayer drains
	 */
	private DelayedEvent drainer;
	int drainerDelay = Integer.MAX_VALUE;
	/**
	 * The drain rate of the prayers currently enabled
	 */
	private int drainRate = 0;
	/**
	 * If the second duel screen has been accepted
	 */
	private boolean duelConfirmAccepted = false;
	public int killStreak = 0;
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
	/**
	 * The exp level array
	 */
	private int[] exp = new int[18];
	/**
	 * Amount of fatigue - 0 to 100
	 */
	private int fatigue = 0;
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
	private int groupID = 4;
	public boolean hasAnswered = false;
	/**
	 * List of usernameHash's of players on players ignore list
	 */
	private ArrayList<Long> ignoreList = new ArrayList<Long>();
	/**
	 * Is the player accessing their bank?
	 */
	private boolean inBank = false;
	/**
	 * Quests
	 */
	/**
	 * Has the player been registered into the world?
	 */
	private boolean initialized = false;
	public boolean inQuiz = false;
	/**
	 * The npc we are currently interacting with
	 */
	private Npc interactingNpc = null;
	/**
	 * Inventory to hold items
	 */
	private Inventory inventory;
	private boolean invis = false;
        private boolean blink = false;

	/**
	 * The IO session of this player
	 */
	private IoSession ioSession;
	/**
	 * If the player is currently in a duel
	 */
	private boolean isDueling = false;
	public boolean isMining = false;
	/**
	 * Added by Zerratar: Are we sleeping?
	 */
	public boolean isSleeping = false;
	/**
	 * If the player is currently in a trade
	 */
	private boolean isTrading = false;
	/**
	 * List of players this player 'knows' (recieved from the client) about
	 */
	private HashMap<Integer, Integer> knownPlayersAppearanceIDs = new HashMap<Integer, Integer>();
	public String lastAnswer = null;
	/**
	 * Last arrow fired
	 */
	private long lastArrow = 0;
	/**
	 * The last menu reply this player gave in a quest
	 */
	//
	public long lastCast = System.currentTimeMillis();
	/**
	 * Time of last charge spell
	 */
	private long lastCharge = 0;
	/**
	 * Last packet count time
	 */
	private long lastCount = 0;
	public long lastDeath = System.currentTimeMillis();
	/**
	 * Stores the last IP address used
	 */
	private String lastIP = "0.0.0.0";
	/**
	 * Unix time when the player last logged in
	 */
	private long lastLogin = 0;
	// Player(IoSession
	public long lastMineTimer = 0;
	public long lastPacketRecTime = System.currentTimeMillis() / 1000;
	/**
	 * Queue of last 100 packets, used for auto detection purposes
	 */
	private LinkedList<RSCPacket> lastPackets = new LinkedList<RSCPacket>();
	public long lastPacketTime = -1;
	/**
	 * Last time a 'ping' was received
	 */
	private long lastPing = System.currentTimeMillis();
	public String lastPlayerInfo2 = null;
	private int lastQuestMenuReply = -1;
	// don't remove this. -xEnt
	public int lastRandom = 0;
	public long lastRange = System.currentTimeMillis();
	/**
	 * Time last report was sent, used to throttle reports
	 */
	private long lastReport = 0;
	public long lastRun = System.currentTimeMillis(); // Leave this here
	private long lastSaveTime = System.currentTimeMillis()
			+ DataConversions.random(600000, 1800000);
	private long lastSleepTime = System.currentTimeMillis();
	/**
	 * The time of the last spell cast, used as a throttle
	 */
	private long lastSpellCast = 0;
	/**
	 * Time of last trade/duel request
	 */
	private long lastTradeDuelRequest = 0;
	/**
	 * Whether the player is currently logged in
	 */
	private boolean loggedIn = false;
	public long loginTime = -1;
	public int loops = 0;
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
	public boolean[] npcThief = { false, false, false, false, false, false }; // Baker
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
	public int quizPoints = 0;
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
	public int sessionFlags = 0;
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
	String sleepword;
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

        /*
        * Used for the Infected Blood world event
        */

        public boolean infected = false;
        public long lastInfected = System.currentTimeMillis();

        /*
        * Informs the server that the player has just used Infected Blood and activate the cooldown.
        */

        public void setLastInfected() {
                lastInfected = System.currentTimeMillis();
        }

        /*
        * Returns the last time a player used Infected Blood.
        */

        public long lastInfected() {
                return lastInfected;
        }

        /*
        * Is this player infected?
        */

        public boolean isInfected() {
                return infected;
        }


        /*
        * This method is used for the Infected Blood world event
        * @author Ollie
        */

        public void setInfected() {
                infected = true;
                getActionSender().sendMessage("You have been afflicted by the plague.");
        }

	public Player(IoSession ios) {

		ioSession = ios;
		currentIP = ((InetSocketAddress) ios.getRemoteAddress()).getAddress()
				.getHostAddress();
		currentLogin = System.currentTimeMillis();
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
		attackedBy.put(p.getUsernameHash(), System.currentTimeMillis());
	}

	public void addFriend(long id, int world) {
		friendList.put(id, world);
	}

	public void addIgnore(long id) {
		ignoreList.add(id);
	}

	public void addMessageToChatQueue(byte[] messageData) {
		chatQueue.add(new ChatMessage(this, messageData));
		if (chatQueue.size() > 2) {
			destroy(false);
		}
	}

	public void addPacket(RSCPacket p) {
		long now = System.currentTimeMillis();
		if (now - lastCount > 3000) {
			lastCount = now;
			packetCount = 0;
		}
		if (!DataConversions.inArray(Formulae.safePacketIDs, p.getID())
				&& packetCount++ >= 60) {
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
				}
			};
			Instance.getDelayedEventHandler().add(skullEvent);
			super.setAppearnceChanged(true);
		}
		skullEvent
				.setLastRun(System.currentTimeMillis() - (1200000 - timeLeft));
	}

	public void addToDuelOffer(InvItem item) {
		duelOffer.add(item);
	}

	public void addToTradeOffer(InvItem item) {
		tradeOffer.add(item);
	}

	public boolean canLogout() {
		if(this.location.inWilderness()) {
			if(System.currentTimeMillis() - this.getLastMoved() < 10000) {
	    		getActionSender().sendMessage("You must stand peacefully in one place for 10 seconds!");
	    		return false;
	    	}
		}
		return !isBusy() && System.currentTimeMillis() - getCombatTimer() > 10000;
	}

	public boolean canReport() {
		return System.currentTimeMillis() - lastReport > 60000;
	}

	public boolean castTimer() {
		return System.currentTimeMillis() - lastSpellCast > 1200;
	}

	public boolean checkAttack(Mob mob, boolean missile) {
		if (mob instanceof Player) {
			Player victim = (Player) mob;
			
			if (victim.isNoPK()) {
				actionSender
						.sendMessage("You cannot attack this staff members");
				return false;
			}

                        //KO9 - Konijn - check if PK'ing is disabled in this area
                        int i = 0;
                        for(Point place[] : world.getPlaces()) {
                                if(getLocation().inBounds(place[0].getX(), place[0].getY(), place[1].getX(), place[1].getY()) && !world.wildAttackable(i)) {
                                        actionSender.sendMessage("You cannot attack in this area at the moment.");
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
			if (System.currentTimeMillis() - mob.getCombatTimer() < (mob
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

	public void destroy(boolean force) {
		if (destroy) {
			return;
		}
		if (force || canLogout()) {
			destroy = true;
			actionSender.sendLogout();
		} else {
			final long startDestroy = System.currentTimeMillis();
			Instance.getDelayedEventHandler().add(new DelayedEvent(this, 3000) {

				public void run() {
					if (owner.canLogout() || (!(owner.inCombat() && owner.isDueling()) && System.currentTimeMillis() - startDestroy > 600000*2)) {
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
			final long startDestroy = System.currentTimeMillis();
			Instance.getDelayedEventHandler().add(new DelayedEvent(this, 3000) {

				public void run() {
					if (owner.canLogout() || (!(owner.inCombat() && owner.isDueling()) && System.currentTimeMillis() - startDestroy > 60000)) {
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

	public Bank getBank() {
		return bank;
	}

	public List<Bubble> getBubblesNeedingDisplayed() {
		return bubblesNeedingDisplayed;
	}

	public long getCastTimer() {
		return lastSpellCast;
	}

	public List<ChatMessage> getChatMessagesNeedingDisplayed() {
		return chatMessagesNeedingDisplayed;
	}

	public String getClassName() {
		return className;
	}

	public int getCombatStyle() {
		return combatStyle;
	}

	public int getCombo() {
		return this.Combo;
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

	public int getCurStat(int id) {
		return curStat[id];
	}

	public int[] getCurStats() {
		return curStat;
	}

	// piru.sytes.net
	public int getDaysSinceLastLogin() {
		long now = Calendar.getInstance().getTimeInMillis() / 1000;
		return (int) ((now - lastLogin) / 86400);
	}

	public int getDaysSubscriptionLeft() {
		long now = (System.currentTimeMillis() / 1000);
		if (subscriptionExpires == 0 || now >= subscriptionExpires) {
			return 0;
		}
		return (int) ((subscriptionExpires - now) / 86400);
	}

	public int getDefense() {
		return getCurStat(1);
	}

	public int getDrainRate() {
		return drainRate;
	}

	public ArrayList<InvItem> getDuelOffer() {
		return duelOffer;
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

	public int getExp(int id) {
		return exp[id];
	}

	public int[] getExps() {
		return exp;
	}

	public int getFatigue() {
		return fatigue;
	}

	public Collection<Entry<Long, Integer>> getFriendList() {
		return friendList.entrySet();
	}

	public boolean getGameSetting(int i) {
		return gameSettings[i];
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

	public Inventory getInventory() {
		return inventory;
	}

	public String getLastIP() {
		return lastIP;
	}

	public long getLastLogin() {
		return lastLogin;
	}

	public long getLastPing() {
		return lastPing;
	}

	/**
	 * @return this player's last quest menu reply
	 */
	public int getLastQuestMenuReply() {
		return lastQuestMenuReply;
	}

	public long getLastSaveTime() {
		return lastSaveTime;
	}

	public long getLastSleepTime() {
		return lastSleepTime;
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

	public List<Npc> getNpcsRequiringHitsUpdate() {
		return npcsNeedingHitsUpdate;
	}

	public int getOwner() {
		return owner;
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

	public List<Projectile> getProjectilesNeedingDisplayed() {
		return projectilesNeedingDisplayed;
	}

	public int getQuestPoints() {
		return questPoints;
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
		return DataConversions.roundUp((double) (1200 - (System
				.currentTimeMillis() - lastSpellCast)) / 1000D);
	}

	public Action getStatus() {
		return status;
	}

	public int getStrength() {
		return getCurStat(2);
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

	public int getWrongWords() {
		return wrongwords;
	}

	// incExp
	public int ignoreCount() {
		return ignoreList.size();
	}

	public void incCurStat(int i, int amount) {
		curStat[i] += amount;
		if (curStat[i] < 0) {
			curStat[i] = 0;
		}
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
				&& (combatStyleToIndex() != i && getCombatStyle() != 0)) { // fix
			// for
			// accidental
			// exp
			// in
			// other
			// stats?
			return;
		}

		double exprate = Constants.GameServer.EXP_RATE;
		if (isSubscriber()) {
			exprate = Constants.GameServer.SUB_EXP_RATE;
		}

		if(getLocation().wildernessLevel() > 1) {
			if(combat) exprate = exprate*2;
			if(getLocation().wildernessLevel() > 10 && !combat) exprate = exprate*2;
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
			if (stat == 99) {
				for (Player p : world.getPlayers()) {
					if (p != null) {
						p
								.getActionSender()
								.sendMessage(
										"#adm##pmd#@gre@**********************************#pmd##adm#");
						p
								.getActionSender()
								.sendMessage(
										"@yel@Congratulations "
												+ this.getUsername()
												+ " has just reached the maximum level (99)!! in "
												+ Formulae.statArray[i]);
						p
								.getActionSender()
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

	public void incExp(int i, int amount, boolean useFatigue) {

		incExp(i, amount, useFatigue, false);
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
		return groupID == 1;
	}

	public boolean isChangingAppearance() {
		return changingAppearance;
	}

	public boolean isCharged() {
		return System.currentTimeMillis() - lastCharge < 600000;
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

	public boolean isFollowing() {
		return followEvent != null && following != null;
	}

	public boolean isFriendsWith(long usernameHash) {
		return friendList.containsKey(usernameHash);
	}

	public boolean isIgnoring(long usernameHash) {
		return ignoreList.contains(usernameHash);
	}

	public boolean isInvis() {
		return invis;
	}

        public boolean blink() {
                return blink;
        }

	public boolean isMale() {
		return maleGender;
	}

	public boolean isMining() {
		return isMining;
	}

	public void isMining(boolean arg) {
		isMining = arg;
	}

	public boolean isMod() {
		return groupID == 2 || isAdmin();
	}

	public boolean isNonaggro() {
		return nonaggro;
	}

	public boolean isNoPK() {
		return nopk;
	}

	public boolean isPMod() {
		return groupID == 6 || isMod() || isAdmin();
	}

	public boolean isRanging() {
		return rangeEvent != null;
	}

	public boolean isReconnecting() {
		return reconnecting;
	}

	public boolean isSkulled() {
		return skullEvent != null;
	}

	public boolean isSubscriber() {
		return groupID == 5;
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

	/**
	 * Restricts P2P stuff in wilderness.
	 */
	public void p2pWildy() {
		if (Constants.GameServer.F2P_WILDY) {

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

	public void killedBy(Mob mob, boolean stake) {
		if (!loggedIn) {
			Logger.error(username + " not logged in, but killed!");
			return;
		}
		if (mob instanceof Player) {
			Player player = (Player) mob;
			player.getActionSender().sendMessage("You have defeated " + getUsername() + "!");
			player.getActionSender().sendSound("victory");
			Instance.getDelayedEventHandler().add(new MiniEvent(player) {
				public void action() {
					owner.getActionSender().sendScreenshot();
				}
			});
			Instance.getServer().getLoginConnector().getActionSender().logKill(player.getUsernameHash(), usernameHash, stake);
		}
		Mob opponent = super.getOpponent();
		if (opponent != null) {
			opponent.resetCombat(CombatState.WON);
		}
		actionSender.sendSound("death");
		lastDeath = System.currentTimeMillis();
		actionSender.sendDied();
		for (int i = 0; i < 18; i++) {
			curStat[i] = maxStat[i];
			actionSender.sendStat(i);
		}

		Player player = mob instanceof Player ? (Player) mob : null;
		if (stake) {
			if (player == null) {
				Logger.println("Player is null (not dropping item): " + this.getUsername());
			}
			for (InvItem item : duelOffer) {
				InvItem affectedItem = getInventory().get(item);
				if (affectedItem == null) {
					setSuspiciousPlayer(true);
					Logger.error("Missing staked item [" + item.getID() + ", " + item.getAmount() + "] from = " + usernameHash + "; to = " + player.getUsernameHash() + ";");
					continue;
				}
				if (affectedItem.isWielded()) {
					affectedItem.setWield(false);
					updateWornItems(affectedItem.getWieldableDef().getWieldPos(), getPlayerAppearance().getSprite(affectedItem.getWieldableDef().getWieldPos()));
				}
				getInventory().remove(item);
				final long playerhash = DataConversions.usernameToHash(player.getUsername());
				DuelLog.sendlog(playerhash, usernameHash, item.getID(), item.getAmount(), getX(), getY(), 2);
				//newItem.setdroppedby(getUsernameHash());
				world.registerItem(new Item(item.getID(), getX(), getY(), item.getAmount(), player));
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
				world.registerItem(new Item(item.getID(), getX(), getY(), item.getAmount(), player));
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

	public void load(String username, String password, int uid, boolean reconnecting) {
		try {
			setID(uid);
			this.password = password;
			this.reconnecting = reconnecting;
			usernameHash = DataConversions.usernameToHash(username);
			this.username = DataConversions.hashToUsername(usernameHash);

			Instance.getServer().getLoginConnector().getActionSender().playerLogin(this);
			Instance.getDelayedEventHandler().add(new DelayedEvent(this, 60000) {

						private void checkStat(int statIndex) {
							if (statIndex != 3 && owner.getCurStat(statIndex) == owner.getMaxStat(statIndex)) {
								owner.getActionSender().sendMessage("Your " + Formulae.statArray[statIndex] + " ability has returned to normal.");
							}
						}
						
						public void run() {
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
							getActionSender().sendMessage("You have run out of prayer points. Return to a church to recharge");
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

	public void ping() {
		lastPing = System.currentTimeMillis();
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
		lastArrow = System.currentTimeMillis();
	}

	public void setBank(Bank b) {
		bank = b;
	}

	public void setCastTimer() {
		lastSpellCast = System.currentTimeMillis();
	}

	public void setChangingAppearance(boolean b) {
		changingAppearance = b;
	}

	public void setCharged() {
		lastCharge = System.currentTimeMillis();
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public void setCombatStyle(int style) {
		combatStyle = style;
	}

	public void setCombo(int combo) {
		this.Combo = combo;
	}

	/**
	 * Sets this player's current course object
	 */
	public void setCurrentCourseObject(int order) {
		currentCourseObject = order;
	}

	public void setCurStat(int id, int lvl) {
		if (lvl <= 0) {
			lvl = 0;
		}
		curStat[id] = lvl;
	}

	// p2p

	public void setDrainRate(int rate) {
		drainRate = rate;
	}

	public void setDuelConfirmAccepted(boolean b) {
		duelConfirmAccepted = b;
	}

	public void setDueling(boolean b) {
		isDueling = b;
	}

	public void setDuelOfferAccepted(boolean b) {
		duelOfferAccepted = b;
	}

	public void setDuelSetting(int i, boolean b) {
		duelOptions[i] = b;
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

	public void setGameSetting(int i, boolean b) {
		gameSettings[i] = b;
	}

	public void setGroupID(int id) {
		groupID = id;
	}

	public void setHits(int lvl) {
		setCurStat(3, lvl);
	}

	public void setInitialized() {
		initialized = true;
	}

	public void setInventory(Inventory i) {
		inventory = i;
	}

	public void setinvis(boolean arg) {
		invis = arg;
	}

        public void setBlink(boolean arg) {
                blink = arg;
        }


	public void setLastIP(String ip) {
		lastIP = ip;
	}

	public void setLastLogin(long l) {
		lastLogin = l;
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

	public void setLastReport() {
		lastReport = System.currentTimeMillis();
	}

	public void setLastSaveTime(long save) {
		lastSaveTime = save;
	}

	public void setLastSleepTime(long save) {
		lastSleepTime = save;
	}

	public void setLoggedIn(boolean loggedIn) {
		if (loggedIn) {
			currentLogin = System.currentTimeMillis();
		}
		this.loggedIn = loggedIn;
	}

	public void setMale(boolean male) {
		maleGender = male;
	}

	public void setMaxStat(int id, int lvl) {
		if (lvl < 0) {
			lvl = 0;
		}
		maxStat[id] = lvl;
	}

	public void setMenuHandler(MenuHandler menuHandler) {
		menuHandler.setOwner(this);
		this.menuHandler = menuHandler;
	}

	// Added by Konijn
	public void setNoclip(boolean noclip) {
		this.noclip = noclip;
	}

	public void setnonaggro(boolean arg) {
		nonaggro = arg;
	}

	public void setnopk(boolean arg) {
		nopk = arg;
	}

	public void setNpc(Npc npc) {
		// System.out.println("setNpc(npc)");
		interactingNpc = npc;
	}

	public void setOwner(int owner) {
		this.owner = owner;
	}

	public void setPrivacySetting(int i, boolean b) {
		privacySettings[i] = b;
	}

	public void setQuestMenuHandler(MenuHandler menuHandler) {
		this.menuHandler = menuHandler;
		menuHandler.setOwner(this);
		actionSender.sendMenu(menuHandler.getOptions());
	}

	public void setQuestPoints(int newquestPoints, boolean save) {
		int old = questPoints;
		questPoints = newquestPoints;
		int gained = questPoints - old;

		if (save) {
			// save();
			setLastSaveTime(System.currentTimeMillis());
			getActionSender().sendQuestInfo();
			getActionSender().sendMessage(
					"@gre@You just gained " + gained + " quest point"
							+ (gained > 1 ? "s" : "") + "!");
		}
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

	public void setRequiresOfferUpdate(boolean b) {
		requiresOfferUpdate = b;
	}

	public void setServerKey(long key) {
		sessionKeys[2] = (int) (key >> 32);
		sessionKeys[3] = (int) key;
	}

	public boolean setSessionKeys(int[] keys) {
		boolean valid = (sessionKeys[2] == keys[2] && sessionKeys[3] == keys[3]);
		sessionKeys = keys;
		return valid;
	}

	public void setSEvent(ShortEvent sEvent) {
		this.sEvent = sEvent;
		Instance.getDelayedEventHandler().add(sEvent);
	}

	public void setSkillLoops(int arg) {
		loops = arg;
	}

	public void setSkulledOn(Player player) {
		player.addAttackedBy(this);
		if (System.currentTimeMillis() - lastAttackedBy(player) > 1200000) {
			addSkull(1200000);
		}
	}
	public boolean shouldRangePass() {
		int percent = (int)((this.getMaxStat(5)-40) * 0.6);
		percent += 60;
		if(percent > 100) percent = 100;
		if(DataConversions.random(0, 100) < percent)
			return false;
		else
			return true;
	}
	public void setSleepword(String sleepword) {
		this.sleepword = sleepword;
	}

	public void setSpam(boolean spam) {
		packetSpam = spam;
	}

	public void setSpellFail() {
		lastSpellCast = System.currentTimeMillis() + 20000;
	}

	public void setStatus(Action a) {
		status = a;
	}

	public void setSubscriptionExpires(long expires) {
		subscriptionExpires = expires;
	}// 240000 / drainRate

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
		Logger.println(this.getUsername() + " was set suspicious! Stacktrace: \n" + stacktrace);
		Logger.systemerr(this.getUsername() + " was set suspicious! Stacktrace: \n" + stacktrace);
		world.addEntryToSnapshots(new Activity(this.getUsername(),this.getUsername() + " was set suspicious! Stacktrace: \n" + stacktrace));
	}
	public void setTradeConfirmAccepted(boolean b) {
		tradeConfirmAccepted = b;
	}

	public void setTradeOfferAccepted(boolean b) {
		tradeOfferAccepted = b;
	}

	public void setTrading(boolean b) {
		isTrading = b;
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

	public void setWrongWords() {
		wrongwords += 1;
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

	public boolean tradeDuelThrottling() {
		long now = System.currentTimeMillis();
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

        public List<Player> infectedBlood() {
                List<Player> playersInView = viewArea.getPlayersInView();
                List<Player> radiusPlayers = new ArrayList<Player>();
                for (Player p : playersInView) {
                        if((p.getX() - getX() <= 2  || p.getX() - getY() >= -2) && (p.getY() - getY() <= 2  || p.getY() - getY() >= -2) && !p.isInfected()) {
                                radiusPlayers.add(p);
                        }
                }
                return radiusPlayers;
        }

	public void updateWornItems(int index, int id) {
		wornItems[index] = id;
		super.ourAppearanceChanged = true;
	}

	public HashMap<Long, Long> getAttackedBy() {
		return attackedBy;
	}

	public void setAttackedBy(HashMap<Long, Long> attackedBy) {
		this.attackedBy = attackedBy;
	}

	public boolean isBadClient() {
		return badClient;
	}

	public void setBadClient(boolean badClient) {
		this.badClient = badClient;
	}

	public LinkedList<ChatMessage> getChatQueue() {
		return chatQueue;
	}

	public void setChatQueue(LinkedList<ChatMessage> chatQueue) {
		this.chatQueue = chatQueue;
	}

	public boolean isClientWarn() {
		return clientWarn;
	}

	public void setClientWarn(boolean clientWarn) {
		this.clientWarn = clientWarn;
	}

	public String getCorrectSleepword() {
		return correctSleepword;
	}

	public void setCorrectSleepword(String correctSleepword) {
		this.correctSleepword = correctSleepword;
	}

	public int[] getCurStat() {
		return curStat;
	}

	public void setCurStat(int[] curStat) {
		this.curStat = curStat;
	}

	public boolean isDestroy() {
		return destroy;
	}

	public void setDestroy(boolean destroy) {
		this.destroy = destroy;
	}

	public DelayedEvent getDrainer() {
		return drainer;
	}

	public void setDrainer(DelayedEvent drainer) {
		this.drainer = drainer;
	}

	public int getDrainerDelay() {
		return drainerDelay;
	}

	public void setDrainerDelay(int drainerDelay) {
		this.drainerDelay = drainerDelay;
	}

	public boolean[] getDuelOptions() {
		return duelOptions;
	}

	public void setDuelOptions(boolean[] duelOptions) {
		this.duelOptions = duelOptions;
	}

	public DelayedEvent getFollowEvent() {
		return followEvent;
	}

	public void setFollowEvent(DelayedEvent followEvent) {
		this.followEvent = followEvent;
	}

	public boolean[] getGameSettings() {
		return gameSettings;
	}

	public void setGameSettings(boolean[] gameSettings) {
		this.gameSettings = gameSettings;
	}

	public boolean isHasAnswered() {
		return hasAnswered;
	}

	public void setHasAnswered(boolean hasAnswered) {
		this.hasAnswered = hasAnswered;
	}

	public boolean isInBank() {
		return inBank;
	}

	public void setInBank(boolean inBank) {
		this.inBank = inBank;
	}

	public boolean isInitialized() {
		return initialized;
	}

	public void setInitialized(boolean initialized) {
		this.initialized = initialized;
	}

	public boolean isInQuiz() {
		return inQuiz;
	}

	public void setInQuiz(boolean inQuiz) {
		this.inQuiz = inQuiz;
	}

	public Npc getInteractingNpc() {
		return interactingNpc;
	}

	public void setInteractingNpc(Npc interactingNpc) {
		this.interactingNpc = interactingNpc;
	}

	public IoSession getIoSession() {
		return ioSession;
	}

	public void setIoSession(IoSession ioSession) {
		this.ioSession = ioSession;
	}

	public boolean isSleeping() {
		return isSleeping;
	}

	public void setSleeping(boolean isSleeping) {
		this.isSleeping = isSleeping;
	}

	public HashMap<Integer, Integer> getKnownPlayersAppearanceIDs() {
		return knownPlayersAppearanceIDs;
	}

	public void setKnownPlayersAppearanceIDs(
			HashMap<Integer, Integer> knownPlayersAppearanceIDs) {
		this.knownPlayersAppearanceIDs = knownPlayersAppearanceIDs;
	}

	public String getLastAnswer() {
		return lastAnswer;
	}

	public void setLastAnswer(String lastAnswer) {
		this.lastAnswer = lastAnswer;
	}

	public long getLastArrow() {
		return lastArrow;
	}

	public void setLastArrow(long lastArrow) {
		this.lastArrow = lastArrow;
	}

	public long getLastCast() {
		return lastCast;
	}

	public void setLastCast(long lastCast) {
		this.lastCast = lastCast;
	}

	public long getLastCharge() {
		return lastCharge;
	}

	public void setLastCharge(long lastCharge) {
		this.lastCharge = lastCharge;
	}

	public long getLastCount() {
		return lastCount;
	}

	public void setLastCount(long lastCount) {
		this.lastCount = lastCount;
	}

	public long getLastDeath() {
		return lastDeath;
	}

	public void setLastDeath(long lastDeath) {
		this.lastDeath = lastDeath;
	}

	public long getLastMineTimer() {
		return lastMineTimer;
	}

	public void setLastMineTimer(long lastMineTimer) {
		this.lastMineTimer = lastMineTimer;
	}

	public long getLastPacketRecTime() {
		return lastPacketRecTime;
	}

	public void setLastPacketRecTime(long lastPacketRecTime) {
		this.lastPacketRecTime = lastPacketRecTime;
	}

	public LinkedList<RSCPacket> getLastPackets() {
		return lastPackets;
	}


	public long getLastPacketTime() {
		return lastPacketTime;
	}

	public void setLastPacketTime(long lastPacketTime) {
		this.lastPacketTime = lastPacketTime;
	}

	public String getLastPlayerInfo2() {
		return lastPlayerInfo2;
	}

	public void setLastPlayerInfo2(String lastPlayerInfo2) {
		this.lastPlayerInfo2 = lastPlayerInfo2;
	}

	public int getLastRandom() {
		return lastRandom;
	}

	public void setLastRandom(int lastRandom) {
		this.lastRandom = lastRandom;
	}

	public long getLastRange() {
		return lastRange;
	}

	public void setLastRange(long lastRange) {
		this.lastRange = lastRange;
	}

	public long getLastReport() {
		return lastReport;
	}

	public void setLastReport(long lastReport) {
		this.lastReport = lastReport;
	}

	public long getLastRun() {
		return lastRun;
	}

	public void setLastRun(long lastRun) {
		this.lastRun = lastRun;
	}

	public long getLastSpellCast() {
		return lastSpellCast;
	}

	public void setLastSpellCast(long lastSpellCast) {
		this.lastSpellCast = lastSpellCast;
	}

	public long getLastTradeDuelRequest() {
		return lastTradeDuelRequest;
	}

	public void setLastTradeDuelRequest(long lastTradeDuelRequest) {
		this.lastTradeDuelRequest = lastTradeDuelRequest;
	}

	public long getLoginTime() {
		return loginTime;
	}

	public void setLoginTime(long loginTime) {
		this.loginTime = loginTime;
	}

	public int getLoops() {
		return loops;
	}

	public void setLoops(int loops) {
		this.loops = loops;
	}

	public boolean isMaleGender() {
		return maleGender;
	}

	public void setMaleGender(boolean maleGender) {
		this.maleGender = maleGender;
	}

	public int[] getMaxStat() {
		return maxStat;
	}// you have been granted

	public void setMaxStat(int[] maxStat) {
		this.maxStat = maxStat;
	}

	public boolean isNopk() {
		return nopk;
	}

	public void setNopk(boolean nopk) {
		this.nopk = nopk;
	}

	public ArrayList<Npc> getNpcsNeedingHitsUpdate() {
		return npcsNeedingHitsUpdate;
	}

	public void setNpcsNeedingHitsUpdate(ArrayList<Npc> npcsNeedingHitsUpdate) {
		this.npcsNeedingHitsUpdate = npcsNeedingHitsUpdate;
	}

	public boolean[] getNpcThief() {
		return npcThief;
	}

	public void setNpcThief(boolean[] npcThief) {
		this.npcThief = npcThief;
	}

	public int getPacketCount() {
		return packetCount;
	}

	public void setPacketCount(int packetCount) {
		this.packetCount = packetCount;
	}

	public boolean isPacketSpam() {
		return packetSpam;
	}

	public void setPacketSpam(boolean packetSpam) {
		this.packetSpam = packetSpam;
	}

	public ArrayList<Player> getPlayersNeedingHitsUpdate() {
		return playersNeedingHitsUpdate;
	}

	public void setPlayersNeedingHitsUpdate(
			ArrayList<Player> playersNeedingHitsUpdate) {
		this.playersNeedingHitsUpdate = playersNeedingHitsUpdate;
	}

	public boolean[] getPrivacySettings() {
		return privacySettings;
	}

	public void setPrivacySettings(boolean[] privacySettings) {
		this.privacySettings = privacySettings;
	}

	public HashMap<Integer, Integer> getQuestStage() {
		return questStage;
	}

	public void setQuestStage(HashMap<Integer, Integer> questStage) {
		this.questStage = questStage;
	}

	public int getQuizPoints() {
		return quizPoints;
	}

	public void setQuizPoints(int quizPoints) {
		this.quizPoints = quizPoints;
	}

	public int getSessionFlags() {
		return sessionFlags;
	}

	public void setSessionFlags(int sessionFlags) {
		this.sessionFlags = sessionFlags;
	}

	public ShortEvent getsEvent() {
		return sEvent;
	}

	public void setsEvent(ShortEvent sEvent) {
		this.sEvent = sEvent;
	}

	public DelayedEvent getSkullEvent() {
		return skullEvent;
	}

	public void setSkullEvent(DelayedEvent skullEvent) {
		this.skullEvent = skullEvent;
	}

	public int getWrongwords() {
		return wrongwords;
	}

	public void setWrongwords(int wrongwords) {
		this.wrongwords = wrongwords;
	}

	public PlayerAppearance getAppearance() {
		return appearance;
	}// destroy

	public int[] getExp() {
		return exp;
	}

	public Mob getFollowing() {
		return following;
	}

	public boolean isLoggedIn() {
		return loggedIn;
	}

	public boolean isNoclip() {
		return noclip;
	}

	public RangeEvent getRangeEvent() {
		return rangeEvent;
	}

	public boolean isRequiresOfferUpdate() {
		return requiresOfferUpdate;
	}

	public int[] getSessionKeys() {
		return sessionKeys;
	}

	public long getSubscriptionExpires() {
		return subscriptionExpires;
	}

	public void setActionSender(MiscPacketBuilder actionSender) {
		this.actionSender = actionSender;
	}

	public void setBubblesNeedingDisplayed(
			ArrayList<Bubble> bubblesNeedingDisplayed) {
		this.bubblesNeedingDisplayed = bubblesNeedingDisplayed;
	}

	public void setChatMessagesNeedingDisplayed(
			ArrayList<ChatMessage> chatMessagesNeedingDisplayed) {
		this.chatMessagesNeedingDisplayed = chatMessagesNeedingDisplayed;
	}

	public void setCurrentIP(String currentIP) {
		this.currentIP = currentIP;
	}

	public void setCurrentLogin(long currentLogin) {
		this.currentLogin = currentLogin;
	}

	public void setDuelOffer(ArrayList<InvItem> duelOffer) {
		this.duelOffer = duelOffer;
	}

	public void setFriendList(TreeMap<Long, Integer> friendList) {
		this.friendList = friendList;
	}

	public void setIgnoreList(ArrayList<Long> ignoreList) {
		this.ignoreList = ignoreList;
	}

	public void setInvis(boolean invis) {
		this.invis = invis;
	}

	public void setMining(boolean isMining) {
		this.isMining = isMining;
	}

	public void setLastPing(long lastPing) {
		this.lastPing = lastPing;
	}

	public void setNonaggro(boolean nonaggro) {
		this.nonaggro = nonaggro;
	}

	public void setNpcMessagesNeedingDisplayed(
			ArrayList<ChatMessage> npcMessagesNeedingDisplayed) {
		this.npcMessagesNeedingDisplayed = npcMessagesNeedingDisplayed;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setProjectilesNeedingDisplayed(
			ArrayList<Projectile> projectilesNeedingDisplayed) {
		this.projectilesNeedingDisplayed = projectilesNeedingDisplayed;
	}

	public void setQuestPoints(int questPoints) {
		this.questPoints = questPoints;
	}

	public void setReconnecting(boolean reconnecting) {
		this.reconnecting = reconnecting;
	}

	public void setShop(Shop shop) {
		this.shop = shop;
	}

	public void setSuspicious(boolean suspicious) {
		this.suspicious = suspicious;
	}

	public void setTradeOffer(ArrayList<InvItem> tradeOffer) {
		this.tradeOffer = tradeOffer;
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

	public boolean withinRange(Entity e) {
		int xDiff = location.getX() - e.getLocation().getX();
		int yDiff = location.getY() - e.getLocation().getY();
		return xDiff <= 16 && xDiff >= -15 && yDiff <= 16 && yDiff >= -15;
	}

	@Override
	public String toString() {
		return "[Player:" + username + "]";
	}
	private int smithingbar = -1;
	public void setSmithingBar(int id) {
		this.smithingbar = id;
		}
	public int getSmithingBar() {
		return smithingbar;
	}
}
