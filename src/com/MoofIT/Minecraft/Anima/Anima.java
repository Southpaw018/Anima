package com.MoofIT.Minecraft.Anima;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;


public class Anima extends JavaPlugin {
	private final AnimaPlayerListener playerListener = new AnimaPlayerListener(this);
	private final AnimaBlockListener blockListener = new AnimaBlockListener(this);

	public static Logger log;
	public PluginManager pm;
	public PluginDescriptionFile pdfFile;
	private FileConfiguration config;

	public static Economy econ = null;

	//Config defaults
	public int storageAmount = 25;
	public int maxXP = 4625;
	public boolean versionCheck = true;

	public double depositCost = 0;
	public double withdrawCost = 0;

	//Config versioning
	private int configVer = 0;
	private final int configCurrent = 1;

	public void onEnable() {
		log = Logger.getLogger("Minecraft");
		pm = getServer().getPluginManager();

		loadConfig();
		setupEconomy();
		if (versionCheck) versionCheck();

		pm.registerEvents(playerListener, this);
		pm.registerEvents(blockListener,this);

		log.info("Anima v." + getDescription().getVersion() + ": storing souls since 2012!");
	}

	public void onDisable() {
		log.info("[Anima] Shutting down.");
		pdfFile = null;
		pm = null;
	}

	private void loadConfig() {
		this.reloadConfig();
		config = this.getConfig();

		configVer = config.getInt("configVer", configVer);
		if (configVer == 0) {
			saveDefaultConfig();
			log.info("[Anima] Configuration error or no config file found. Copying default config file from JAR.");
		}
		else if (configVer < configCurrent) {
			log.warning("[Anima] Your config file is out of date! Delete your config and reload to see the new options. Proceeding using set options from config file and defaults for new options..." );
		}

		storageAmount = Math.max(config.getInt("Core.storageAmount", storageAmount),1);
		maxXP = config.getInt("Core.maxXP", maxXP);
		versionCheck = config.getBoolean("Core.versionCheck", versionCheck);

		depositCost = config.getDouble("Economy.depositCost", depositCost);
		withdrawCost = config.getDouble("Economy.withdrawCost", withdrawCost);
	}

	 private boolean setupEconomy() {
		if (pm.getPlugin("Vault") == null) {
			return false;
		}
		RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
		econ = rsp.getProvider();
		return econ != null;
	}

	public void versionCheck() {
		String thisVersion = getDescription().getVersion();
		URL url = null;
		try {
			url = new URL("http://www.moofit.com/minecraft/Anima.ver?v=" + thisVersion);
			BufferedReader in = null;
			in = new BufferedReader(new InputStreamReader(url.openStream()));
			String newVersion = "";
			String line;
			while ((line = in.readLine()) != null) {
				newVersion += line;
			}
			in.close();
			if (!newVersion.equals(thisVersion)) {
				log.warning("[Anima] Anima is out of date! This version: " + thisVersion + "; latest version: " + newVersion + ".");
			}
			else {
				log.info("[Anima] Anima is up to date at version " + thisVersion + ".");
			}
		}
		catch (MalformedURLException ex) {
			log.warning("[Anima] Error accessing update URL.");
		}
		catch (IOException ex) {
			log.warning("[Anima] Error checking for update.");
		}
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) return false;
		Player p = (Player)sender;
		String cmd = command.getName();
		if (cmd.equalsIgnoreCase("anima")) {
			if (args.length < 1) return false;
			if (args[0].equalsIgnoreCase("xp")) {
				sendMessage(p,"You have " + p.getTotalExperience() + " XP.");
				return true;
			}
			if (args[0].equalsIgnoreCase("levelxp")) {
				if (args.length != 2) return false;
				try {
					Integer.parseInt(args[1]);
				} catch (NumberFormatException e) {
					return false;
				}
				sendMessage(p,"The total XP required for level " + args[1] + " is " + xpLevelTotal(Integer.valueOf(args[1])) + ".");
				return true;
			}
			if (args[0].equalsIgnoreCase("help")) {
				sendMessage(p,"Place a sign with the first line [Anima]. Left click to withdraw, right click to deposit.");
				return true;
			}
			if (args[0].equalsIgnoreCase("admin")) {
				if (!p.hasPermission("anima.admin")) {
					sendMessage(p,"You do not have permissions to use Anima admin commands.");
					return true;
				}
				if (args[1].equalsIgnoreCase("reload")) {
					loadConfig();
					sendMessage(p,"Configuration reloaded from file.");
				}
			}
		}
		return true;
	}

	public void sendMessage(Player player, String message) {
		player.sendMessage(ChatColor.BLUE + "[Anima] " + ChatColor.WHITE + message);
	}

	public static int xpLevelTotal(int level) {
		int xp = 0;

		for (int x = 0; x < level; x++) {
			xp += 7 + Math.floor(x * 3.5);
		}

		return xp;
	}
	public static int levelTotalXP(int xp) { //OH GOD IT'S SO UGLY MY EYES
		int level = 0;

		do {
			level++;
		} while (xpLevelTotal(level) < xp);

		return level;
	}
	//Holy shit, major salute to desht for figuring this insanity out.
	public static void awardExperience(Player player, int xp) {
		player.giveExp(xp);

		int newXp = player.getTotalExperience();
		//int newLevel = (int) (Math.sqrt(newXp / 3.5 + 0.25) - 0.5);
		int newLevel = levelTotalXP(newXp); 
		player.setLevel(newLevel);
		int xpForThisLevel = xpNeeded(newLevel);
		float neededForThisLevel = xpNeeded(newLevel + 1) - xpForThisLevel;
		float distanceThru = player.getTotalExperience() - xpForThisLevel;
		player.setExp(distanceThru / neededForThisLevel);
	}

	private static int xpNeeded(int level) {
		if (level <= 0) {
			return 0;
			} else {
			//return (int)(3.5 * level * (level + 1));
			return (int)(7 + Math.floor(level * 3.5));
		}
	}
}