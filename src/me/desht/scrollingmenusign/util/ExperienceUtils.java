package me.desht.scrollingmenusign.util;

import java.util.Arrays;

import org.bukkit.entity.Player;

/**
 * @author desht
 *
 */
public class ExperienceUtils {
	public static final int MAX_LEVEL_SUPPORTED = 101;

	private static final int xpLookup[] = new int[MAX_LEVEL_SUPPORTED];
	static {
		int curTotal = 0;
		for (int i = 0; i < xpLookup.length; i++) {
			xpLookup[i] = curTotal;
			curTotal += 7 + (i * 7 >> 1);
		}
	}

	/**
	 * Give the player some experience (possibly negative) and ensure player's level and client XP
	 * bar is correctly updated too.
	 * 
	 * @param player	The player to grant XP to
	 * @param xp		The amount of XP to grant
	 */
	public static void awardExperience(Player player, int xp) {
		if (xp < 0 && -xp > player.getTotalExperience())
			return;
		
		int curLevel = player.getLevel();
		player.giveExp(xp);
		//System.out.println("gave " + player.getName() + " " + xp + " exp, new totalexp = " + player.getTotalExperience() + " new getExp() = " + player.getExp());
		int newLevel = getRealLevel(player);
		if (curLevel != newLevel) {
			// recalculate the player's actual level and distance through the level
			//System.out.println("levelled from " + curLevel + " to " + newLevel);
			player.setLevel(newLevel);
			int xpForThisLevel = experienceNeeded(newLevel);
			float neededForThisLevel = experienceNeeded(newLevel + 1) - xpForThisLevel;
			float distanceThruLevel = player.getTotalExperience() -  xpForThisLevel;
			player.setExp(distanceThruLevel / neededForThisLevel);
		}
	}

	/**
	 * Return the amount of experience needed to get to the given level.
	 * 
	 * @param level		The level wanted 
	 * @return			The amount of experience needed
	 */
	public static int experienceNeeded(int level) {
		return xpLookup[level];
	}

	/**
	 * Return the level that the given player should be.  (Note: the player's current level might not 
	 * be this, because Minecraft's experience system is just plain bizarre).
	 * 
	 * @param player	The player to check for
	 * @return			The level the player should be
	 */
	public static int getRealLevel(Player player) {
		return getRealLevel(player.getTotalExperience());
	}

	/**
	 * Return the level for the given amount of experience.
	 * 
	 * @param exp	The experience amount
	 * @return		The level for the experience amount
	 */
	public static int getRealLevel(int exp) {
		if (exp <= 0)
			return 0;
		// since we know the xp lookup table is sorted...
		int pos = Arrays.binarySearch(xpLookup, exp);
		return pos < 0 ? -pos - 2 : pos;
	}
}