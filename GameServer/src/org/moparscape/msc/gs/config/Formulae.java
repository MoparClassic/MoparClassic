package org.moparscape.msc.gs.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.moparscape.msc.gs.model.Entity;
import org.moparscape.msc.gs.model.GameObject;
import org.moparscape.msc.gs.model.InvItem;
import org.moparscape.msc.gs.model.Mob;
import org.moparscape.msc.gs.model.Npc;
import org.moparscape.msc.gs.model.Player;
import org.moparscape.msc.gs.model.Point;
import org.moparscape.msc.gs.model.container.Shop;
import org.moparscape.msc.gs.model.definition.EntityHandler;
import org.moparscape.msc.gs.model.definition.entity.GameObjectLoc;
import org.moparscape.msc.gs.model.definition.entity.ItemLoc;
import org.moparscape.msc.gs.model.definition.entity.NPCLoc;
import org.moparscape.msc.gs.model.definition.skill.FiremakingDef;
import org.moparscape.msc.gs.model.definition.skill.ObjectFishDef;
import org.moparscape.msc.gs.model.definition.skill.ObjectMiningDef;
import org.moparscape.msc.gs.model.definition.skill.ObjectWoodcuttingDef;
import org.moparscape.msc.gs.model.definition.skill.SpellDef;
import org.moparscape.msc.gs.tools.DataConversions;

public class Formulae {
	public static final Point[] noremoveTiles = { new Point(341, 487),
			new Point(343, 581), new Point(92, 649), new Point(434, 682),
			new Point(660, 551), new Point(196, 3266), new Point(59, 573),
			new Point(560, 472), new Point(140, 180), new Point(285, 195),
			new Point(243, 178), new Point(394, 851), new Point(388, 851),
			new Point(512, 550) };
	public static final int[] arrowIDs = { 723, 647, 646, 645, 644, 643, 642,
			641, 640, 639, 638, 574, 11 };
	public static final int[] bodySprites = { 2, 5 };
	public static final int[] boltIDs = { 786, 592, 190 };
	public static final int[] bowIDs = { 188, 189, 648, 649, 650, 651, 652,
			653, 654, 655, 656, 657 };
	// spell
	public static final int[] experienceArray = { 83, 174, 276, 388, 512, 650,
			801, 969, 1154, 1358, 1584, 1833, 2107, 2411, 2746, 3115, 3523,
			3973, 4470, 5018, 5624, 6291, 7028, 7842, 8740, 9730, 10824, 12031,
			13363, 14833, 16456, 18247, 20224, 22406, 24815, 27473, 30408,
			33648, 37224, 41171, 45529, 50339, 55649, 61512, 67983, 75127,
			83014, 91721, 101333, 111945, 123660, 136594, 150872, 166636,
			184040, 203254, 224466, 247886, 273742, 302288, 333804, 368599,
			407015, 449428, 496254, 547953, 605032, 668051, 737627, 814445,
			899257, 992895, 1096278, 1210421, 1336443, 1475581, 1629200,
			1798808, 1986068, 2192818, 2421087, 2673114, 2951373, 3258594,
			3597792, 3972294, 4385776, 4842295, 5346332, 5902831, 6517253,
			7195629, 7944614, 8771558, 9684577, 10692629, 11805606, 13034431,
			14391160 };
	public static final int[] headSprites = { 1, 4, 6, 7, 8 };

	/**
	 * Cubic P2P boundaries. MinX, MinY - MaxX, MaxY
	 */
	public static final java.awt.Point[][] F2PWILD_LOCS = { {
			new java.awt.Point(48, 96), new java.awt.Point(335, 142) } };
	public static final java.awt.Point[][] P2P_LOCS = {
			{ new java.awt.Point(436, 432), new java.awt.Point(719, 906) },
			{ new java.awt.Point(48, 96), new java.awt.Point(335, 142) },
			{ new java.awt.Point(343, 567), new java.awt.Point(457, 432) },
			{ new java.awt.Point(203, 3206), new java.awt.Point(233, 3265) },
			{ new java.awt.Point(397, 525), new java.awt.Point(441, 579), },
			{ new java.awt.Point(431, 0), new java.awt.Point(1007, 1007) } };
	public static final int[] potions1Dose = { 224, 476, 479, 482, 485, 488,
			491, 494, 497, 500, 568, 571 };
	public static final int[] potions2Dose = { 223, 475, 478, 481, 484, 487,
			490, 493, 496, 499, 567, 570 };
	public static final int[] potions3Dose = { 222, 474, 477, 480, 483, 486,
			489, 492, 495, 498, 566, 569 };
	public static final int[] potionsUnfinished = { 454, 455, 456, 457, 458,
			459, 460, 461, 462, 463 };

	private static Random r = new Random();
	public static final int[] runeIDs = { 31, 32, 33, 34, 35, 36, 37, 38, 40,
			41, 42, 46, 619, 825 };
	/**
	 * Safe packets:<br>
	 * PlayerAppearanceUpdater<br>
	 * FollowRequest<br>
	 * InvUseOnItem<br>
	 */
	public static final int[] safePacketIDs = { 70, 123, 128, 255 };
	public static final String[] statArray = { "attack", "defense", "strength",
			"hits", "ranged", "prayer", "magic", "cooking", "woodcut",
			"fletching", "fishing", "firemaking", "crafting", "smithing",
			"mining", "herblaw", "agility", "thieving" };

	public static final int[] woodcuttingAxeIDs = { 405, 204, 203, 428, 88, 12,
			87 };

	public static final int[] xbowIDs = { 59, 60 };

	/**
	 * Adds the prayers together to calculate what perecntage the stat should be
	 * increased
	 */
	public static double addPrayers(boolean first, boolean second, boolean third) {
		if (third) {
			return 1.15D;
		}
		if (second) {
			return 1.1D;
		}
		if (first) {
			return 1.05D;
		}
		return 1.0D;
	}

	/**
	 * Returns a power to assosiate with each arrow
	 */
	private static double arrowPower(int arrowID) {
		switch (arrowID) {
		case 11: // bronze arrows
		case 574: // poison bronze arrows
		case 190: // crossbow bolts
		case 592: // poison cross bow bolts
		case 1013: // bronze throwing dart
		case 1122: // poison bronze throwing dart
			return 0;
		case 638:// iron arrows
		case 639:// poison iron arrows
		case 1015: // iron throwing dart
		case 1123:// poison iron throwing dart
			return 0.5;
		case 640:// steel arrows
		case 641:// poison steel arrows
		case 1024: // steel throwing dart
		case 1124: // poison steel throwing dart
		case 1076:// bronze throwing dart
		case 1128:// poison bronze throwing knife
		case 827:// bronze spear
		case 1135:// poison bronze spear
			return 1;
		case 642:// mith arrows
		case 643:// poison mith arrows
		case 786:// pearle crossbow bolts
		case 1068:// mith throwing dart
		case 1125: // poison mith throwing dart
		case 1075:// iron throwing dart
		case 1129:// poison iron throwing knife
		case 1088:// iron spear
		case 1136:// poison iron spear
			return 1.5;
		case 644:// addy arrows
		case 645:// poison addy arrows
		case 1069:// addy throwing dart
		case 1126:// poison addy throwing dart
		case 1077:// steel throwing knife
		case 1130:// poison steel throwing knife
		case 1089:// steel spear
		case 1137:// poison steel spear
			return 1.75;
		case 1081:// black throwing knife
		case 1132:// poison black throwing knife
			return 2;
		case 646:// rune arrows
		case 647:// poison rune arrows
		case 1070:// rune throwing dart
		case 1127:// poison rune throwing dart
		case 1078:// mith throwing knife
		case 1131:// poison mith throwing knife
		case 1090:// mith spear
		case 1138:// poison mith spear
			return 5;
		case 723:// ice arrows
		case 1079:// addy throwing knife
		case 1133:// poison addy throwing knife
		case 1091:// addy spear
		case 1139:// poison addy spear
			return 6;
		case 1080:// rune throwing knife
		case 1134:// poison rune throwing knife
		case 1092:// rune spear
		case 1140:// poison rune spear
			return 7;
		case 785:// lit arrow (not stackable, why not?)
			return 10;
		default:
			return 0;
		}
	}

	public static int bitToDoorDir(int bit) {
		switch (bit) {
		case 1:
			return 0;
		case 2:
			return 1;
		case 4:
			return -1;
		case 8:
			return -1;
		}
		return -1;
	}

	public static int bitToObjectDir(int bit) {
		switch (bit) {
		case 1:
			return 6;
		case 2:
			return 0;
		case 4:
			return 2;
		case 8:
			return 4;
		}
		return -1;
	}

	/**
	 * Decide if the food we are cooking should be burned or not
	 */
	public static boolean burnFood(int foodId, int cookingLevel) {
		int levelDiff = cookingLevel
				- EntityHandler.getItemCookingDef(foodId).getReqLevel();
		if (levelDiff < 0) {
			return true;
		}
		if (levelDiff >= 20) {
			return false;
		}
		return DataConversions.random(0, levelDiff + 1) == 0;
	}

	/**
	 * Calulates what one mob should hit on another with meelee
	 */
	public static int calcFightHit(Mob attacker, Mob defender) {
		int max = maxHit(attacker.getStrength(),
				attacker.getWeaponPowerPoints(), attacker.isPrayerActivated(1),
				attacker.isPrayerActivated(4), attacker.isPrayerActivated(10),
				styleBonus(attacker, 2));
		int newAtt = (int) (addPrayers(attacker.isPrayerActivated(2),
				attacker.isPrayerActivated(5), attacker.isPrayerActivated(11))
				* (attacker.getAttack() / 0.8D)
				+ ((DataConversions.random(0, 4) == 0 ? attacker
						.getWeaponPowerPoints() : attacker.getWeaponAimPoints()) / 2.5D)
				+ (attacker.getCombatStyle() == 1
						&& DataConversions.random(0, 2) == 0 ? 4 : 0)
				+ (DataConversions.random(0, 100) <= 10 ? (attacker
						.getStrength() / 5D) : 0) + (styleBonus(attacker, 0) * 2));
		int newDef = (int) (addPrayers(defender.isPrayerActivated(0),
				defender.isPrayerActivated(3), defender.isPrayerActivated(9))
				* ((DataConversions.random(0, 100) <= 5 ? 0 : defender
						.getDefense()) * 1.1D)
				+ ((DataConversions.random(0, 100) <= 5 ? 0 : defender
						.getArmourPoints()) / 2.75D)
				+ (defender.getStrength() / 4D) + (styleBonus(defender, 1) * 2));

		int hitChance = DataConversions.random(0, 100) + (newAtt - newDef);
		if (attacker instanceof Npc) {
			hitChance -= 5;
		}
		if (DataConversions.random(0, 100) <= 10) {
			hitChance += 20;
		}
		if (hitChance > (defender instanceof Npc ? 40 : 50)) {
			int maxProb = 5; // 5%
			int nearMaxProb = 7; // 7%
			int avProb = 73; // 73%
			// int lowHit = 10; // 15% // TODO: Should probably use lowHit...

			// Probablities are shifted up/down based on armour
			int shiftValue = (int) Math
					.round(defender.getArmourPoints() * 0.02D);
			maxProb -= shiftValue;
			nearMaxProb -= (int) Math.round(shiftValue * 1.5);
			avProb -= (int) Math.round(shiftValue * 2.0);
			// lowHit += (int) Math.round(shiftValue * 3.5);

			int hitRange = DataConversions.random(0, 100);

			if (hitRange >= (100 - maxProb)) {
				return max;
			} else if (hitRange >= (100 - nearMaxProb)) {
				return DataConversions
						.roundUp(Math.abs((max - (max * (DataConversions
								.random(0, 10) * 0.01D)))));
			} else if (hitRange >= (100 - avProb)) {
				int newMax = (int) DataConversions
						.roundUp((max - (max * 0.1D)));
				return DataConversions
						.roundUp(Math.abs((newMax - (newMax * (DataConversions
								.random(0, 50) * 0.01D)))));
			} else {
				int newMax = (int) DataConversions
						.roundUp((max - (max * 0.5D)));
				return DataConversions
						.roundUp(Math.abs((newMax - (newMax * (DataConversions
								.random(0, 95) * 0.01D)))));
			}
		}
		return 0;
	}

	public static int calcFightHitWithNPC(Mob attacker, Mob defender) {

		int max = maxHit(attacker.getStrength(),
				attacker.getWeaponPowerPoints(), attacker.isPrayerActivated(1),
				attacker.isPrayerActivated(4), attacker.isPrayerActivated(10),
				styleBonus(attacker, 2));
		if (attacker instanceof Npc) {
			Npc n = (Npc) attacker;
			if (n.getID() == 3) // Chickens only doing 1 damage.
				max = 1;
		}

		// int newAtt = (int) (addPrayers(attacker.isPrayerActivated(2),
		// attacker.isPrayerActivated(5), attacker.isPrayerActivated(11)) *
		// (attacker.getAttack() / 0.7D) + ((DataConversions.random(0, 4) == 0 ?
		// attacker.getWeaponPowerPoints() : attacker.getWeaponAimPoints()) /
		// 3D) + (attacker.getCombatStyle() == 1 && DataConversions.random(0, 2)
		// == 0 ? 4 : 0) + (styleBonus(attacker, 0) * 2));

		int newAtt = (int) (addPrayers(attacker.isPrayerActivated(2),
				attacker.isPrayerActivated(5), attacker.isPrayerActivated(11))
				* (attacker.getAttack())
				+ ((DataConversions.random(0, 4) == 0 ? attacker
						.getWeaponPowerPoints() : attacker.getWeaponAimPoints()) / 3D)
				+ (attacker.getCombatStyle() == 1
						&& DataConversions.random(0, 2) == 0 ? 4 : 0) + (styleBonus(
				attacker, 0) * 2));

		int newDef = (int) (addPrayers(defender.isPrayerActivated(0),
				defender.isPrayerActivated(3), defender.isPrayerActivated(9))
				* defender.getDefense()
				+ (defender.getArmourPoints() / 4D)
				+ (defender.getStrength() / 4D) + (styleBonus(defender, 1) * 2));

		/*
		 * if(defender instanceof Player) { if(defender.getCombatLevel() < 70 &&
		 * defender.getCombatLevel() > 45) newDef = newDef + (int)(newDef *
		 * 0.25); else if(defender.getCombatLevel() > 25 &&
		 * defender.getCombatLevel() < 45) newDef = newDef + (int)(newDef *
		 * 0.35);
		 * 
		 * else if(defender.getCombatLevel() > 8 && defender.getCombatLevel() <
		 * 25) newDef = newDef + (int)(newDef * 0.45);
		 * 
		 * else if(defender.getCombatLevel() > 1 && defender.getCombatLevel() <
		 * 8) newDef = newDef + (int)(newDef * 0.55); }
		 */
		if (attacker instanceof Player) {
			// newDef += newDef / 8;
			newDef -= newDef / 8;
		}

		int hitChance = DataConversions.random(0, 100) + (newAtt - newDef);
		// Added this
		if (attacker instanceof Player)
			hitChance += (int) (DataConversions.random(0, attacker.getAttack()) + 1) / 1.33;

		if (attacker instanceof Npc) {
			hitChance -= 5;
		}
		if (hitChance > (defender instanceof Npc ? 40 : 50)) {
			int maxProb = 5; // 5%
			int nearMaxProb = 10; // 10%
			int avProb = 80; // 70%
			// int lowHit = 10; // 15% // TODO: Should probably use lowHit...

			// Probablities are shifted up/down based on armour
			int shiftValue = (int) Math
					.round(defender.getArmourPoints() * 0.02D);
			maxProb -= shiftValue;
			nearMaxProb -= (int) Math.round(shiftValue * 1.5);
			avProb -= (int) Math.round(shiftValue * 2.0);
			// lowHit += (int) Math.round(shiftValue * 3.5);

			int hitRange = DataConversions.random(0, 100);

			if (hitRange >= (100 - maxProb)) {
				return max;
			} else if (hitRange >= (100 - nearMaxProb)) {
				return DataConversions
						.roundUp(Math.abs((max - (max * (DataConversions
								.random(0, 10) * 0.01D)))));
			} else if (hitRange >= (100 - avProb)) {
				int newMax = (int) DataConversions
						.roundUp((max - (max * 0.1D)));
				return DataConversions
						.roundUp(Math.abs((newMax - (newMax * (DataConversions
								.random(0, 50) * 0.01D)))));
			} else {
				int newMax = (int) DataConversions
						.roundUp((max - (max * 0.5D)));
				return DataConversions
						.roundUp(Math.abs((newMax - (newMax * (DataConversions
								.random(0, 95) * 0.01D)))));
			}
		}
		return 0;
	}

	public static int calcGodSpells(Mob attacker, Mob defender) {
		if (attacker instanceof Player) {
			Player owner = (Player) attacker;
			int newAtt = (int) ((owner.getMagicPoints()) + owner.getCurStat(6));
			int newDef = (int) ((addPrayers(defender.isPrayerActivated(0),
					defender.isPrayerActivated(3),
					defender.isPrayerActivated(9))
					* defender.getDefense() / 4D) + (defender.getArmourPoints() / 4D));
			int hitChance = DataConversions.random(0, 150 + (newAtt - newDef));
			// int hitChance = (int)(50D + (double)owner.getMagicPoints() -
			// newDef);

			if (hitChance > (defender instanceof Npc ? 50 : 60)) {
				// int max = owner.isCharged() ? Rand(15, 25) : Rand(0, 10);
				int max;
				if (owner.isCharged()) {
					max = Rand(14, 25);

				} else {
					max = Rand(0, 10);
				}
				int maxProb = 5; // 5%
				int nearMaxProb = 10; // 10%
				int avProb = 80; // 80%
				// int lowHit = 5; // 5% // TODO: Should probably use lowHit...

				// Probablities are shifted up/down based on armour
				int shiftValue = (int) Math
						.round(defender.getArmourPoints() * 0.02D);
				maxProb -= shiftValue;
				nearMaxProb -= (int) Math.round(shiftValue * 1.5);
				avProb -= (int) Math.round(shiftValue * 2.0);
				// lowHit += (int) Math.round(shiftValue * 3.5);

				int hitRange = DataConversions.random(0, 100);

				if (hitRange >= (100 - maxProb)) {
					return max;
				} else if (hitRange >= (100 - nearMaxProb)) {
					return DataConversions
							.roundUp(Math.abs((max - (max * (DataConversions
									.random(0, 10) * 0.01D)))));
				} else if (hitRange >= (100 - avProb)) {
					int newMax = (int) DataConversions
							.roundUp((max - (max * 0.1D)));
					return DataConversions.roundUp(Math
							.abs((newMax - (newMax * (DataConversions.random(0,
									50) * 0.01D)))));
				} else {
					int newMax = (int) DataConversions
							.roundUp((max - (max * 0.5D)));
					return DataConversions.roundUp(Math
							.abs((newMax - (newMax * (DataConversions.random(0,
									95) * 0.01D)))));
				}
			}
		}
		return 0;
	}

	/**
	 * Calculates what one mob should hit on another with range
	 */
	public static int calcRangeHit(int rangeLvl, int rangeEquip,
			int armourEquip, int arrowID) {
		int armourRatio = (int) (60D + ((double) ((rangeEquip * 3D) - armourEquip) / 300D) * 40D);

		if (DataConversions.random(0, 100) > armourRatio
				&& DataConversions.random(0, 1) == 0) {
			return 0;
		}

		int max = (int) (((double) rangeLvl * 0.15D) + 0.85D + arrowPower(arrowID));
		int peak = (int) (((double) max / 100D) * (double) armourRatio);
		int dip = (int) (((double) peak / 3D) * 2D);
		return DataConversions.randomWeighted(0, dip, peak, max);
	}

	/**
	 * Calculates what a spell should hit based on its strength and the magic
	 * equipment stats of the caster
	 */
	public static int calcSpellHit(int spellStr, int magicEquip) {
		int mageRatio = (int) (45D + (double) magicEquip);
		int max = spellStr;
		int peak = (int) (((double) spellStr / 100D) * (double) mageRatio);
		int dip = (int) ((peak / 3D) * 2D);
		return DataConversions.randomWeighted(0, dip, peak, max);
	}

	/**
	 * Should the spell cast or fail?
	 */
	public static boolean castSpell(SpellDef def, int magicLevel, int magicEquip) {
		int levelDiff = magicLevel - def.getReqLevel();

		if (magicEquip >= 30 && levelDiff >= 5)
			return true;
		if (magicEquip >= 25 && levelDiff >= 6)
			return true;
		if (magicEquip >= 20 && levelDiff >= 7)
			return true;
		if (magicEquip >= 15 && levelDiff >= 8)
			return true;
		if (magicEquip >= 10 && levelDiff >= 9)
			return true;
		if (levelDiff < 0) {
			return false;
		}
		if (levelDiff >= 10) {
			return true;
		}
		return DataConversions.random(0, (levelDiff + 2) * 2) != 0;
	}

	/**
	 * Calculate how much experience a Mob gives
	 */
	public static int combatExperience(Mob mob) {
		double exp = ((mob.getCombatLevel() * 2) + 10) * 1.5D;
		return (int) (mob instanceof Player ? (exp / 4D) : exp);
	}

	/*
	 * Should the pot crack?
	 */
	public static boolean crackPot(int requiredLvl, int craftingLvl) {
		int levelDiff = craftingLvl - requiredLvl;
		if (levelDiff < 0) {
			return true;
		}
		if (levelDiff >= 20) {
			return false;
		}
		return DataConversions.random(0, levelDiff + 1) == 0;
	}

	/**
	 * Should the web be cut?
	 */
	public static boolean cutWeb() {
		return DataConversions.random(0, 4) != 0;
	}

	public static boolean doorAtFacing(Entity e, int x, int y, int dir) {
		if (dir >= 0 && e instanceof GameObject) {
			GameObject obj = (GameObject) e;
			return obj.getType() == 1 && obj.getDirection() == dir
					&& obj.isOn(x, y);
		}
		return false;
	}

	/**
	 * Check what level the given experience corresponds to
	 */
	public static int experienceToLevel(int exp) {
		for (int level = 0; level < 98; level++) {
			if (exp >= experienceArray[level]) {
				continue;
			}
			return (level + 1);
		}
		return 99;
	}

	/**
	 * Decide if we fall off the obstacle or not
	 */
	public static int failObstacle(Player player, int reqLvl) {
		int levelDiff = player.getCurStat(16) - reqLvl;
		if (levelDiff < 0)
			return 1;

		if (levelDiff >= (reqLvl * 2))
			return -1;

		if (DataConversions.random(0, levelDiff + 1) == 0)
			return DataConversions.roundUp(player.getMaxStat(3)
					* DataConversions.random(0.02, 0.04));
		else
			return -1;
	}

	public static int firemakingExp(int level, int baseExp) {
		return DataConversions.roundUp(baseExp + (level * 1.75D));
	}

	/**
	 * Generates a session id
	 */
	public static long generateSessionKey(byte userByte) {
		return DataConversions.getRandom().nextLong();
	}

	/**
	 * Gets the type of bar we have
	 */
	public static int getBarType(int barID) {
		switch (barID) {
		case 169:
			return 0;
		case 170:
			return 1;
		case 171:
			return 2;
		case 173:
			return 3;
		case 174:
			return 4;
		case 408:
			return 5;
		}
		return -1;
	}

	/**
	 * Calculate a mobs combat level based on their stats
	 */
	public static int getCombatlevel(int[] stats) {
		return getCombatLevel(stats[0], stats[1], stats[2], stats[3], stats[6],
				stats[5], stats[4]);
	}

	/**
	 * Calculate a mobs combat level based on their stats
	 */
	public static int getCombatLevel(int att, int def, int str, int hits,
			int magic, int pray, int range) {
		double attack = att + str;
		double defense = def + hits;
		double mage = pray + magic;
		mage /= 8D;

		if (attack < ((double) range * 1.5D)) {
			return (int) ((defense / 4D) + ((double) range * 0.375D) + mage);
		} else {
			return (int) ((attack / 4D) + (defense / 4D) + mage);
		}
	}

	/**
	 * gets the new sprite direction to face
	 * 
	 */
	public static int getDirection(Mob you, Mob them) {

		if (you.getX() == them.getX() + 1 && you.getY() == them.getY() + 1) // bottom
			// left
			return 3;
		else if (you.getX() == them.getX() + 1 && you.getY() == them.getY() - 1) // top
			// left
			return 1;
		else if (you.getX() == them.getX() - 1 && you.getY() == them.getY() - 1) // right
			// up
			return 7;
		else if (you.getX() == them.getX() - 1 && you.getY() == them.getY() + 1) // right/down
			return 5;
		else if (you.getX() == them.getX() - 1) // face right
			return 6;
		else if (you.getX() == them.getX() + 1) // face left
			return 2;
		else if (you.getY() == them.getY() + 1) // face down
			return 4;
		else if (you.getY() == them.getY() - 1) // face up
			return 0;

		return -1;
	}

	/**
	 * Gets the empty jug ID
	 */
	public static int getEmptyJug(int fullJug) {
		switch (fullJug) {
		case 50:
			return 21;
		case 141:
			return 140;
		case 342:
			return 341;
		}
		return -1;
	}

	/**
	 * Decide what fish, if any, we should get from the water
	 */
	public static ObjectFishDef getFish(int waterId, int fishingLevel, int click) {
		ArrayList<ObjectFishDef> fish = new ArrayList<ObjectFishDef>();
		for (ObjectFishDef def : EntityHandler.getObjectFishingDef(waterId,
				click).getFishDefs()) {
			if (fishingLevel >= def.getReqLevel()) {
				fish.add(def);
			}
		}
		if (fish.size() <= 0) {
			return null;
		}
		ObjectFishDef thisFish = fish.get(DataConversions.random(0,
				fish.size() - 1));
		int levelDiff = fishingLevel - thisFish.getReqLevel();
		if (levelDiff < 0) {
			return null;
		}
		return DataConversions.percentChance(offsetToPercent(levelDiff)) ? thisFish
				: null;
	}

	/**
	 * Returns a gem ID
	 */
	public static int getGem() {
		int rand = DataConversions.random(0, 100);
		if (rand < 10) {
			return 157;
		} else if (rand < 30) {
			return 158;
		} else if (rand < 60) {
			return 159;
		} else {
			return 160;
		}
	}

	/**
	 * Check what height we are currently at on the map
	 */
	public static int getHeight(int y) {
		return (int) (y / 944);
	}

	/**
	 * Check what height we are currently at on the map
	 */
	public static int getHeight(Point location) {
		return getHeight(location.getY());
	}

	public static int getItemPos(Shop shop, int id) {
		return shop.getLastItemSlot(id);
	}

	public static List<InvItem> getKeyChestLoot() {
		@SuppressWarnings("unchecked")
		List<InvItem>[] possibleLoots = (List<InvItem>[]) EntityHandler
				.getKeyChestLoots();
		return possibleLoots[DataConversions
				.random(0, possibleLoots.length - 1)];
	}

	/**
	 * Should we get a log from the tree?
	 */
	public static boolean getLog(ObjectWoodcuttingDef def, int woodcutLevel,
			int axeId) {
		int levelDiff = woodcutLevel - def.getReqLevel();
		if (levelDiff < 0) {
			return false;
		}
		switch (axeId) {
		case 87:
			levelDiff += 0;
			break;
		case 12:
			levelDiff += 2;
			break;
		case 428:
			levelDiff += 4;
			break;
		case 88:
			levelDiff += 6;
			break;
		case 203:
			levelDiff += 8;
			break;
		case 204:
			levelDiff += 10;
			break;
		case 405:
			levelDiff += 12;
			break;
		}
		if (def.getReqLevel() == 1 && levelDiff >= 40) {
			return true;
		}
		return DataConversions.percentChance(offsetToPercent(levelDiff));
	}

	/*
	 * public static int calcFightHitWithNPC(Mob attacker, Mob defender) { int
	 * newAtt = (int)((addPrayers(attacker.isPrayerActivated(2),
	 * attacker.isPrayerActivated(5), attacker.isPrayerActivated(11)) *
	 * attacker.getAttack()) + (attacker.getWeaponAimPoints() / 4D) +
	 * styleBonus(attacker, 0)); int newDef =
	 * (int)((addPrayers(defender.isPrayerActivated(0),
	 * defender.isPrayerActivated(3), defender.isPrayerActivated(9)) *
	 * defender.getDefense()) + (defender.getArmourPoints() / 4D) +
	 * styleBonus(attacker, 1));
	 * 
	 * int hitChance = DataConversions.random(0, 100) + (newAtt - newDef);
	 * 
	 * if(hitChance > (defender instanceof Npc ? 50 : 60)) { int max =
	 * maxHit(attacker.getStrength(), attacker.getWeaponPowerPoints(),
	 * attacker.isPrayerActivated(1), attacker.isPrayerActivated(4),
	 * attacker.isPrayerActivated(10), styleBonus(attacker, 2));
	 * 
	 * int maxProb = 5; // 5% int nearMaxProb = 10; // 10% int avProb = 80; //
	 * 80% int lowHit = 5; // 5%
	 * 
	 * // Probablities are shifted up/down based on armour int shiftValue =
	 * (int)Math.round(defender.getArmourPoints() * 0.02D); maxProb -=
	 * shiftValue; nearMaxProb -= (int)Math.round(shiftValue * 1.5); avProb -=
	 * (int)Math.round(shiftValue * 2.0); lowHit += (int)Math.round(shiftValue *
	 * 3.5);
	 * 
	 * int hitRange = DataConversions.random(0, 100);
	 * 
	 * if(hitRange >= (100 - maxProb)) { return max; } else if(hitRange >= (100
	 * - nearMaxProb)) { return DataConversions.roundUp(Math.abs((max - (max *
	 * (DataConversions.random(0, 10) * 0.01D))))); } else if(hitRange >= (100 -
	 * avProb)) { int newMax = (int)DataConversions.roundUp((max - (max *
	 * 0.1D))); return DataConversions.roundUp(Math.abs((newMax - (newMax *
	 * (DataConversions.random(0, 50) * 0.01D))))); } else { int newMax =
	 * (int)DataConversions.roundUp((max - (max * 0.5D))); return
	 * DataConversions.roundUp(Math.abs((newMax - (newMax *
	 * (DataConversions.random(0, 95) * 0.01D))))); } } return 0; }
	 */

	public static String getLvlDiffColour(int lvlDiff) {
		if (lvlDiff < -9) {
			return "@red@";
		} else if (lvlDiff < -6) {
			return "@or3@";
		} else if (lvlDiff < -3) {
			return "@or2@";
		} else if (lvlDiff < 0) {
			return "@or1@";
		} else if (lvlDiff > 9) {
			return "@gre@";
		} else if (lvlDiff > 6) {
			return "@gr3@";
		} else if (lvlDiff > 3) {
			return "@gr2@";
		} else if (lvlDiff > 0) {
			return "@gr1@";
		}
		return "@whi@";
	}

	public static int getNewY(int currentY, boolean up) {
		int height = getHeight(currentY);
		int newHeight;
		if (up) {
			if (height == 3) {
				newHeight = 0;
			} else if (height >= 2) {
				return currentY;
			} else {
				newHeight = height + 1;
			}
		} else {
			if (height == 0) {
				newHeight = 3;
			} else if (height >= 3) {
				return currentY;
			} else {
				newHeight = height - 1;
			}
		}
		return (newHeight * 944) + (currentY % 944);
	}

	/**
	 * Should we can get an ore from the rock?
	 */
	public static boolean getOre(ObjectMiningDef def, int miningLevel, int axeId) {

		int levelDiff = miningLevel - def.getReqLevel();
		if (levelDiff > 50)
			return Formulae.Rand(0, 9) != 1;
		if (levelDiff < 0) {
			return false;
		}
		int bonus = 0;
		switch (axeId) {
		case 156:
			bonus = 0;
			break;
		case 1258:
			bonus = 2;
			break;
		case 1259:
			bonus = 6;
			break;
		case 1260:
			bonus = 8;
			break;
		case 1261:
			bonus = 10;
			break;
		case 1262:
			bonus = 12;
			break;
		}
		return DataConversions
				.percentChance(offsetToPercent(levelDiff + bonus));
	}

	public static int getPotionDose(int id) {
		if (DataConversions.inArray(potions1Dose, id)) {
			return 1;
		}
		if (DataConversions.inArray(potions2Dose, id)) {
			return 2;
		}
		if (DataConversions.inArray(potions3Dose, id)) {
			return 3;
		}
		return 0;
	}

	/**
	 * @author xEnt
	 * 
	 *         This method will calculate the new price for all items in the
	 *         shop there are 3 different types of calculations. 1. Buy price -
	 *         if the item is sold in the shop by default, use the base quantity
	 *         for the calculations in the new price.
	 * 
	 *         2. Buy price - if a player has sold an item to a general store,
	 *         leaving it to have no base quantity and player driven, we perform
	 *         some secondary calculations.
	 * 
	 *         3. Sell price (this works like the second buy price, calculates
	 *         depending on a static number of stock and does not use any type
	 *         of base quantity calculations, may do a second one in future.
	 * 
	 * @param i
	 *            - the selected Shop item, represented as an InvItem
	 * @param shop
	 *            - the shop model object
	 * @param buy
	 *            - if the item is for sale, or being sold
	 * @return the item's calculated price of value
	 */
	public static int getPrice(InvItem i, Shop shop, boolean buy) {
		final double GENERAL_STORE_BUY_MODIFIER = 0.685;

		int newPrice = -1; // the newly given price.
		boolean playerSoldItem = false; // If true, there is no base quantity (a
										// // player has sold this item to
										// general // store)
		int curAmount = i.amount; // current quantity the selected item has
		int maxStockAmount = shop.equilibriumCount(i.id);// the base // quantity
															// of // the
															// selected // shop
															// item (if // has
															// one)

		if (maxStockAmount == 0 && shop.general())
			playerSoldItem = true;
		// This item is an item that has no base // quantity, was sold by a
		// player in general // store

		if (buy) { // Decide if this item is being brought
			if (maxStockAmount == 0 && !shop.general()) // this should not //
														// happen
				return 999999999; // rofl error price
			if (playerSoldItem) { // General store, no maximum quantity for a //
									// player sold item. (requires different //
									// calculations) // cost 15% more buying a
									// 3rd party item
				// from a general store
				int basePrice = shop.general() ? i.getDef().basePrice
						+ (int) (i.getDef().basePrice * 0.15)
						: i.getDef().basePrice;
				if (basePrice > 10000) // forget any items that are worth past
										// // 10k
					return basePrice;
				if (curAmount > 28) // after 28 quantity from a player sold //
									// item, stick to a static price
					newPrice = basePrice
							- (int) (basePrice * GENERAL_STORE_BUY_MODIFIER);
				else { // do calculations to decide a price depending on the //
						// quantity
					newPrice = basePrice - (int) (curAmount * 5);
					if (newPrice < basePrice
							- (int) (basePrice * GENERAL_STORE_BUY_MODIFIER))
						newPrice = basePrice
								- (int) (basePrice * GENERAL_STORE_BUY_MODIFIER);
				}
			} else { // Has a base quantity
				if (curAmount >= maxStockAmount) // leave base price is full //
													// stock isavaliable
					newPrice = i.getDef().basePrice;
				else // 75-100% quantity is in stock
				if (curAmount > maxStockAmount * 0.75
						&& curAmount < maxStockAmount)
					newPrice = i.getDef().basePrice
							+ (int) (i.getDef().basePrice * getShopPercentage(
									i, 0, true));
				else // 50-75% quantity is in stock
				if (curAmount > maxStockAmount * 0.50
						&& curAmount <= maxStockAmount * 0.75)
					newPrice = i.getDef().basePrice
							+ (int) (i.getDef().basePrice * getShopPercentage(
									i, 1, true));
				else
				// 25-50% quantity is in stock
				if (curAmount > maxStockAmount * 0.25
						&& curAmount <= maxStockAmount * 0.50)
					newPrice = i.getDef().basePrice
							+ (int) (i.getDef().basePrice * getShopPercentage(
									i, 2, true));
				else // 0-25% quantity is in stock
				if (curAmount > maxStockAmount * 0.0
						&& curAmount <= maxStockAmount * 0.25)
					newPrice = i.getDef().basePrice
							+ (int) (i.getDef().basePrice * getShopPercentage(
									i, 3, true));
			}
			if (newPrice == -1)
				return 99999999; // error?
			return newPrice;
		} else { // Sell
			int base = i.getDef().basePrice
					- (int) (i.getDef().basePrice / 2.5); // Sell price is 125%
															// // lower than
															// base // price to
															// begin // with
			if (shop.general()) // 3rd party item (player sold)
				base = base - (int) (base * 0.10); // 10% less value, if general
													// // store.

			if (curAmount < 1)
				return base;

			int price; // new price
			if (curAmount > 12) {
				price = base - (int) (base * 0.75); // 75% loss (believe it or
													// // not, thats how it was)
			} else {
				price = base;

				if (curAmount > 1)
					price = base - (int) (curAmount * (base * 0.045));
				if (price < base - (int) (base * 0.75))
					base = base - (int) (base * 0.75);
			}
			if (price < 1) // should not happen
				return 0; // error price
			else
				return price;
		}

	}

	public static int getRangeDirection(Mob you, Mob them) {
		if (you.getX() > them.getX() && you.getY() == them.getY()) // face right
			return 6;
		else if (you.getX() < them.getX() && you.getY() == them.getY()) // face
			// left
			return 2;
		else if (you.getY() < them.getY() && you.getX() == them.getX()) // face
			// down
			return 4;
		else if (you.getY() > them.getY() && you.getX() == them.getX()) // face
			// up
			return 0;
		else if (you.getX() <= them.getX() && you.getY() <= them.getY()) // bottom
			// left
			return 3;
		else if (you.getX() <= them.getX() && you.getY() >= them.getY()) // top
			// left
			return 1;
		else if (you.getX() >= them.getX() && you.getY() >= them.getY()) // right
			// up
			return 7;
		else if (you.getX() >= them.getX() && you.getY() <= them.getY()) // right/down
			return 5;

		return -1;
	}

	// This is needed for the above method
	public static double getShopPercentage(InvItem item, int pos, boolean buy) {
		int[] prices = { 0, 10, 100, 1000, 10000 }; // base prices of the items.
		double[][] percentages = { { 0.5, 1.0, 1.5, 2.0 }, // in between 0 and
				// 10GP.
				{ 0.1, 0.3, 0.45, 0.6 }, // 10-100gp
				{ 0.1, 0.2, 0.3, 0.4 }, // 100-1000gp
				{ 0.5, 0.1, 0.15, 0.2 }, // 1000-10000gp
		};
		int key = -1;
		for (int i = 0; i < prices.length - 1; i++)
			if (item.getDef().basePrice > prices[i]
					&& item.getDef().basePrice < prices[i + 1])
				key = i;
		if (key == -1)
			return -1;
		return percentages[key][pos];
	}

	/**
	 * Gets the smithing exp for the given amount of the right bars
	 */
	public static int getSmithingExp(int barID, int barCount) {
		int[] exps = { 13, 25, 37, 50, 83, 74 };
		int type = getBarType(barID);
		if (type < 0) {
			return 0;
		}
		return exps[type] * barCount;
	}

	public static int getStat(String stat) {
		for (int i = 0; i < statArray.length; i++) {
			if (statArray[i].equalsIgnoreCase(stat))
				return i;
		}

		return -1;
	}

	/**
	 * Given a stat string get its index returns -1 on failure
	 */
	public static int getStatIndex(String stat) {
		for (int index = 0; index < statArray.length; index++) {
			if (stat.equalsIgnoreCase(statArray[index])) {
				return index;
			}
		}
		return -1;
	}

	public static boolean isP2P(Object... objs) {
		return isP2P(false, objs);
	}

	/**
	 * Performs coordinate checks on the locations in the P2P_LOCS array to
	 * decide whether items/object/npc's/general x,y coordinates are in P2P area
	 * 
	 * @param objs
	 *            - ItemLoc, GameObjectLoc, NPCLoc or an Integer.
	 * @return - true if inside P2P area, otherwise false.
	 */
	public static boolean isP2P(Boolean f2pwildy, Object... objs) {
		int x = -1;
		int y = -1;
		if (objs.length == 1) {
			Object obj = objs[0];
			if (obj instanceof GameObjectLoc) {
				x = ((GameObjectLoc) obj).x;
				y = ((GameObjectLoc) obj).y;
			} else if ((obj instanceof ItemLoc)) {
				x = ((ItemLoc) obj).x;
				y = ((ItemLoc) obj).y;
			} else if (obj instanceof NPCLoc) {
				x = ((NPCLoc) obj).startX;
				y = ((NPCLoc) obj).startY;
			}
		} else {
			if (objs[0] instanceof Integer && objs[1] instanceof Integer) {
				x = (Integer) objs[0];
				y = (Integer) objs[1];
			}
		}

		if (x == -1)
			return false;
		if (!f2pwildy) {
			for (int i = 0; i < P2P_LOCS.length; i++) {
				for (int ele = 0; ele < 4; ele++) {
					if (x >= P2P_LOCS[i][0].getX()
							&& x <= P2P_LOCS[i][1].getX()
							&& y >= P2P_LOCS[i][0].getY() + ((ele) * 944)
							&& y <= P2P_LOCS[i][1].getY() + ((ele) * 944))
						return true;
				}
			}
		} else {
			for (int i = 0; i < F2PWILD_LOCS.length; i++) {
				for (int ele = 0; ele < 4; ele++) {
					if (x >= F2PWILD_LOCS[i][0].getX()
							&& x <= F2PWILD_LOCS[i][1].getX()
							&& y >= F2PWILD_LOCS[i][0].getY() + ((ele) * 944)
							&& y <= F2PWILD_LOCS[i][1].getY() + ((ele) * 944))
						return true;
				}
			}
		}
		return false;
	}

	public static int levelToExperience(int level) {
		if (level <= 1)
			return 0;

		return experienceArray[level];
	}

	/**
	 * Should the fire light or fail?
	 */
	public static boolean lightLogs(FiremakingDef def, int firemakingLvl) {
		int levelDiff = firemakingLvl - def.getRequiredLevel();
		if (levelDiff < 0) {
			return false;
		}
		if (levelDiff >= 20) {
			return true;
		}
		return DataConversions.random(0, levelDiff + 1) != 0;
	}

	// maxHit
	/**
	 * Should the arrow be dropped or disappear
	 */
	public static boolean looseArrow(int damage) {
		return DataConversions.random(0, 6) == 0;
	}

	/**
	 * Calculate the max hit possible with the given stats
	 */
	public static int maxHit(int strength, int weaponPower, boolean burst,
			boolean superhuman, boolean ultimate, int bonus) {
		double newStrength = (double) ((strength * addPrayers(burst,
				superhuman, ultimate)) + bonus);

		int fin = (int) ((newStrength
				* ((((double) weaponPower * 0.00175D) + 0.1D)) + 1.05D) * 0.95D);
		// if (fin > 31)
		// fin = 31;
		return fin;

	}

	/**
	 * Gets the min level required to smith a bar
	 */
	public static int minSmithingLevel(int barID) {
		int[] levels = { 1, 15, 30, 50, 70, 85 };
		int type = getBarType(barID);
		if (type < 0) {
			return -1;
		}
		return levels[type];
	}

	public static boolean objectAtFacing(Entity e, int x, int y, int dir) {
		if (dir >= 0 && e instanceof GameObject) {
			GameObject obj = (GameObject) e;
			return obj.getType() == 0 && obj.getDirection() == dir
					&& obj.isOn(x, y);
		}
		return false;
	}

	private static int offsetToPercent(int levelDiff) {
		return levelDiff > 40 ? 70 : 30 + levelDiff;
	}

	/**
	 * Calulates what one mob should hit on another with meelee
	 */
	public static double parseDouble(double number) {
		String numberString = String.valueOf(number);
		return Double.valueOf(numberString.substring(0,
				numberString.indexOf(".") + 2));
	}

	public static int Rand(int low, int high) {
		return low + r.nextInt(high - low);
	}

	public static int styleBonus(Mob mob, int skill) {
		int style = mob.getCombatStyle();
		if (style == 0) {
			return 1;
		}
		return (skill == 0 && style == 2) || (skill == 1 && style == 3)
				|| (skill == 2 && style == 1) ? 3 : 0;
	}

	public static int[] rares = new int[] { 828, 831, 832, 575, 576, 577, 578,
			579, 580, 581, 971, 1316, 1315, 1314, 422, 1289, 1156, 677 };

	public static boolean isRareItem(int id) {
		return DataConversions.inArray(rares, id);
	}

	public static int getBarIdFromItem(int itemID) {
		if (DataConversions.inArray(BRONZE, itemID))
			return 169;
		if (DataConversions.inArray(IRON, itemID))
			return 170;
		if (DataConversions.inArray(STEEL, itemID))
			return 171;
		if (DataConversions.inArray(MITH, itemID))
			return 173;
		if (DataConversions.inArray(ADDY, itemID))
			return 174;
		if (DataConversions.inArray(RUNE, itemID))
			return 408;
		return -1;
	}

	public final static int[] IRON = { 6, 5, 7, 8, 2, 3, 9, 28, 1075, 1, 71,
			83, 77, 12, 1258, 89, 0, 670, 1063 };
	public final static int[] RUNE = { 112, 399, 400, 401, 404, 403, 402, 396,
			1080, 397, 75, 398, 81, 405, 1262, 93, 98, 674, 1067 };
	public final static int[] ADDY = { 111, 107, 116, 120, 131, 127, 123, 65,
			1079, 69, 74, 86, 80, 204, 1261, 92, 97, 673, 1066 };
	public final static int[] MITH = { 110, 106, 115, 119, 130, 126, 122, 64,
			1078, 68, 73, 85, 79, 203, 1260, 91, 96, 672, 1065 };
	public final static int[] STEEL = { 109, 105, 114, 118, 129, 125, 121, 63,
			1077, 67, 72, 84, 78, 88, 1259, 90, 95, 671, 1064 };
	public final static int[] BRONZE = { 108, 104, 113, 117, 128, 124, 206, 62,
			1076, 66, 70, 82, 76, 87, 156, 87, 205, 669, 1062 };

}
