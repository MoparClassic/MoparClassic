package org.moparscape.msc.ls.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.moparscape.msc.ls.Server;
import org.moparscape.msc.ls.util.DataConversions;

public class PlayerSave {

	public static PlayerSave loadPlayer(long user) {
		return Server.storage.loadPlayer(user);
	}

	private long eventcd = 0;
	private long muted;
	private ArrayList<BankItem> bankItems = new ArrayList<BankItem>();
	private boolean blockChat, blockPrivate, blockTrade, blockDuel;
	private boolean cameraAuto, oneMouse, soundOff, showRoof, autoScreenshot,
			combatWindow;
	private int combat, skillTotal;
	private byte combatStyle;
	private int[] exp = new int[18];
	private int fatigue;
	private ArrayList<Long> friendList = new ArrayList<Long>();
	private byte hairColour, topColour, trouserColour, skinColour, headSprite,
			bodySprite;

	private ArrayList<Long> ignoreList = new ArrayList<Long>();
	private ArrayList<InvItem> invItems = new ArrayList<InvItem>();
	private long lastUpdate = 0;
	private long loginDate, loginIP;
	private int[] lvl = new int[18];
	private boolean male;
	private int owner, group;
	private int questPoints;
	private HashMap<Integer, Integer> questStage = new HashMap<Integer, Integer>();
	private long skulled;
	private long subExpires;
	private long user;

	private int x, y;

	public PlayerSave(long user) {
		this.user = user;
	}

	public void addBankItem(int id, int amount) {
		bankItems.add(new BankItem(id, amount));
	}

	public void addFriend(long friend) {
		friendList.add(friend);
	}
	
	public void addFriends(List<Long> friends) {
		friendList.addAll(friends);
	}
	public void addIgnore(long friend) {
		ignoreList.add(friend);
	}
	public void addIgnore(List<Long> ignored) {
		ignoreList.addAll(ignored);
	}

	public void addInvItem(int id, int amount, boolean wielded) {
		invItems.add(new InvItem(id, amount, wielded));
	}

	public boolean autoScreenshot() {
		return autoScreenshot;
	}

	public boolean blockChat() {
		return blockChat;
	}

	public boolean blockDuel() {
		return blockDuel;
	}

	public boolean blockPrivate() {
		return blockPrivate;
	}

	public boolean blockTrade() {
		return blockTrade;
	}

	public boolean cameraAuto() {
		return cameraAuto;
	}

	public void clearBankItems() {
		bankItems.clear();
	}

	public void clearInvItems() {
		invItems.clear();
	}

	public void clearQuestStages() {
		questStage.clear();
	}

	public boolean combatWindow() {
		return combatWindow;
	}

	public int getBankCount() {
		return bankItems.size();
	}

	public BankItem getBankItem(int i) {
		return bankItems.get(i);
	}
	
	public List<BankItem> getBankItems() {
		return bankItems;
	}

	public int getBodySprite() {
		return bodySprite;
	}

	public byte getCombatStyle() {
		return combatStyle;
	}

	public long getExp(int i) {
		return exp[i];
	}

	public int getFatigue() {
		return fatigue;
	}

	public long getFriend(int i) {
		return friendList.get(i);
	}

	public int getFriendCount() {
		return friendList.size();
	}

	public int getGroup() {
		return group;
	}

	public int getHairColour() {
		return hairColour;
	}

	public int getHeadSprite() {
		return headSprite;
	}

	public long getIgnore(int i) {
		return ignoreList.get(i);
	}

	public int getIgnoreCount() {
		return ignoreList.size();
	}

	public int getInvCount() {
		return invItems.size();
	}

	public InvItem getInvItem(int i) {
		return invItems.get(i);
	}

	public long getLastIP() {
		return loginIP;
	}

	public long getLastLogin() {
		return loginDate;
	}

	public long getLastUpdate() {
		return lastUpdate;
	}

	public int getOwner() {
		return owner;
	}

	public int getQuestPoints() {
		return questPoints;
	}

	public int getQuestStage(int id) {
		return questStage.get(id);
	}

	public HashMap<Integer, Integer> getQuestStages() {
		return questStage;
	}

	public int getSkinColour() {
		return skinColour;
	}

	public long getSkullTime() {
		return skulled;
	}

	public int getStat(int i) {
		return lvl[i];
	}

	public long getSubscriptionExpires() {
		return subExpires;
	}

	public int getTopColour() {
		return topColour;
	}

	public int getTrouserColour() {
		return trouserColour;
	}

	public long getUser() {
		return user;
	}

	public String getUsername() {
		return DataConversions.hashToUsername(user);
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public boolean isMale() {
		return male;
	}

	public boolean oneMouse() {
		return oneMouse;
	}

	public void removeFriend(long friend) {
		friendList.remove(friend);
	}

	public void removeIgnore(long friend) {
		ignoreList.remove(friend);
	}

	public boolean save() {
		return Server.storage.savePlayer(this);
	}

	public void setAppearance(byte hairColour, byte topColour,
			byte trouserColour, byte skinColour, byte headSprite,
			byte bodySprite, boolean male, long skulled) {
		this.hairColour = hairColour;
		this.topColour = topColour;
		this.trouserColour = trouserColour;
		this.skinColour = skinColour;
		this.headSprite = headSprite;
		this.bodySprite = bodySprite;
		this.male = male;
		this.skulled = skulled;
	}
	public void setCombatStyle(byte combatStyle) {
		this.combatStyle = combatStyle;
	}

	public void setExp(int stat, int exp) {
		this.exp[stat] = exp;
	}
	
	public void setExp(int[] is) {
		this.exp = is;
	}

	public void setFatigue(int fatigue) {
		this.fatigue = fatigue;
	}

	public void setEventCD(long eventcd) {
		this.eventcd = eventcd;
	}

	public long getEventCD() {
		return eventcd;
	}

	public void setGameSetting(int idx, boolean on) {
		switch (idx) {
		case 0:
			cameraAuto = on;
			break;
		case 2:
			oneMouse = on;
			break;
		case 3:
			soundOff = on;
			break;
		case 4:
			showRoof = on;
			break;
		case 5:
			autoScreenshot = on;
			break;
		case 6:
			combatWindow = on;
			break;
		}
	}

	public void setLastUpdate(long lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	public void setLocation(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public void setLogin(long loginDate, long loginIP) {
		this.loginDate = loginDate;
		this.loginIP = loginIP;
	}

	public void setLvl(int stat, int lvl) {
		this.lvl[stat] = lvl;
	}
	
	public void setCurStats(int[] stats) {
		this.lvl = stats;
	}

	public void setOwner(int owner) {
		this.owner = owner;
	}

	public void setOwner(int owner, int group, long subExpires) {
		this.owner = owner;
		this.group = group;
		this.subExpires = subExpires;
	}

	public void setPrivacy(boolean blockChat, boolean blockPrivate,
			boolean blockTrade, boolean blockDuel) {
		this.blockChat = blockChat;
		this.blockPrivate = blockPrivate;
		this.blockTrade = blockTrade;
		this.blockDuel = blockDuel;
	}

	public void setPrivacySetting(int idx, boolean on) {
		switch (idx) {
		case 0:
			blockChat = on;
			break;
		case 1:
			blockPrivate = on;
			break;
		case 2:
			blockTrade = on;
			break;
		case 3:
			blockDuel = on;
			break;
		}
	}

	public void setQuestPoints(int i) {
		questPoints = i;
	}

	public void setQuestStage(int index, int stage) {
		questStage.put(index, stage);
	}

	public void setSettings(boolean cameraAuto, boolean oneMouse,
			boolean soundOff, boolean showRoof, boolean autoScreenshot,
			boolean combatWindow) {
		this.cameraAuto = cameraAuto;
		this.oneMouse = oneMouse;
		this.soundOff = soundOff;
		this.showRoof = showRoof;
		this.autoScreenshot = autoScreenshot;
		this.combatWindow = combatWindow;
	}

	public void setStat(int stat, int exp, int lvl) {
		this.exp[stat] = exp;
		this.lvl[stat] = lvl;
	}

	public void setTotals(int combat, int skillTotal) {
		this.combat = combat;
		this.skillTotal = skillTotal;
	}

	public boolean showRoof() {
		return showRoof;
	}

	public boolean soundOff() {
		return soundOff;
	}

	public void setMuted(long muted) {
		this.muted = muted;
	}

	public long getMuted() {
		return muted;
	}

	public List<InvItem> getInvItems() {
		return invItems;
	}

	public int getCombat() {
		return combat;
	}

	public int getSkillTotal() {
		return skillTotal;
	}
}
