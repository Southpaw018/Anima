package com.MoofIT.Minecraft.Anima;

import com.feildmaster.lib.expeditor.Editor;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class AnimaPlayerListener implements Listener {
	private final Anima plugin;

	public AnimaPlayerListener(Anima instance) {
		this.plugin = instance;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (event.getAction() != Action.LEFT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
		Block block = event.getClickedBlock();
		if (block.getType() != Material.WALL_SIGN) return;

		BlockState signBlockState = null;
		signBlockState = block.getState();
		final Sign sign = (Sign)signBlockState;

		if (!sign.getLine(0).equalsIgnoreCase(ChatColor.BLUE + "[Anima]")) return;

		Player player = event.getPlayer();
		if (sign.getLine(3).equalsIgnoreCase("Updating...")) {
			plugin.sendMessage(player,"This sign is still updating. Please wait a moment.");
			return;
		}
		if (!player.hasPermission("anima.use")) {
			plugin.sendMessage(player,"You do not have permission to use Anima signs.");
			return;
		}

		String name = player.getName();
		if (name.length() > 15) name = name.substring(0, 15);
		if (!sign.getLine(1).equals(name)) return;

		int signXP = Integer.valueOf(sign.getLine(2));
		int changeAmount = plugin.storageAmount;

		Editor expeditor = new Editor(player);
		for (String recalcList : Anima.xpRecalcList) {
			if (recalcList.equalsIgnoreCase(name)) {
				expeditor.recalcTotalExp();
				break;
			}
		}


		//Depositing XP
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if (signXP >= plugin.maxXP && !player.hasPermission("anima.maxbypass")) {
				plugin.sendMessage(player,"You cannot deposit more XP into this sign.");
				return;
			}

			if (player.isSneaking()) changeAmount = Math.max(plugin.storageAmount, player.getTotalExperience()); //TODO make this a multiple of changeAmount
			if (signXP + changeAmount > plugin.maxXP) changeAmount = plugin.maxXP - signXP;

			if (player.getTotalExperience() < changeAmount) {
				plugin.sendMessage(player,"You need " + changeAmount + " XP to make a deposit.");
				return;
			}

			double cashCost = plugin.depositCashCost * changeAmount;
			double xpCost = Math.ceil(plugin.depositXPCostPercent / 100 * changeAmount);
			if (Anima.econ != null && cashCost > 0 && !player.hasPermission("anima.free")) {
				if (Anima.econ.getBalance(name) < cashCost) {
					plugin.sendMessage(player,"You need " + Anima.econ.format(cashCost) + " to make a deposit.");
					return;
				}
				else {
					Anima.econ.withdrawPlayer(name, cashCost);
					plugin.sendMessage(player,Anima.econ.format(cashCost) + " has been withdrawn from your account.");
				}
			}
			if (xpCost > 0 && !player.hasPermission("anima.free")) { //TODO test
				if (xpCost + changeAmount < expeditor.getTotalExp()) {
					plugin.sendMessage(player,"You don't have enough XP to cover the fee for this deposit.");
					return;
				}
				else {
					expeditor.takeExp((int)xpCost);
					plugin.sendMessage(player, xpCost + "XP has been deducted from your balance.");
				}
			}
			expeditor.takeExp(changeAmount);
			
			sign.setLine(2, Integer.toString(signXP + changeAmount));
			sign.setLine(3, "Updating...");
			
			plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
				public void run() {
					sign.setLine(3, "");
					sign.update();
				}
			});
		}

		//Withdrawing XP
		if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
			if (player.isSneaking()) changeAmount = Math.max(plugin.storageAmount, signXP);
			if (signXP - changeAmount <= 0) changeAmount = signXP;

			/*int levelMax = ExperienceUtils.MAX_LEVEL_SUPPORTED - 1;
			if (player.getTotalExperience() + changeAmount > ExperienceUtils.experienceNeeded(levelMax)) {
				plugin.sendMessage(player, "Anima can only raise you to level " + (levelMax  -1) + ".");
				return;
			}*/

			double cost = plugin.withdrawCashCost * changeAmount;
			double xpCost = Math.ceil(plugin.withdrawXPCostPercent / 100 * changeAmount);
			if (Anima.econ != null && cost > 0 && !player.hasPermission("anima.free")) {
				if (Anima.econ.getBalance(name) < cost) {
					plugin.sendMessage(player,"You need " + Anima.econ.format(cost) + " to make a withdrawal.");
					return;
				}
				else {
					Anima.econ.withdrawPlayer(name, cost);
					plugin.sendMessage(player,Anima.econ.format(cost) + " has been withdrawn from your account.");
				}
			}
			if (xpCost > 0 && !player.hasPermission("anima.free")) { //TODO test
				if (xpCost + changeAmount < signXP) {
					plugin.sendMessage(player,"You don't have enough XP to cover the fee for this withdrawal.");
					return;
				}
				else {
					expeditor.takeExp((int)xpCost);
					plugin.sendMessage(player, xpCost + "XP has been deducted from your balance.");
				}
			}

			expeditor.giveExp(changeAmount);
			sign.setLine(2, Integer.toString(signXP - changeAmount));
			sign.setLine(3, "Updating...");

			plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
				public void run() {
					sign.setLine(3, "");
					sign.update();
				}
			});
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerQuit(PlayerQuitEvent event) {
		Anima.xpRecalcList.remove(event.getPlayer().getName());
	}
}