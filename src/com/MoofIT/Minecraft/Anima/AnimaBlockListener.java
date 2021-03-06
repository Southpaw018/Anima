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
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;

public class AnimaBlockListener implements Listener {
	private final Anima plugin;

	public AnimaBlockListener(Anima instance) {
		this.plugin = instance;
	}

	@EventHandler
	public void signChangeEvent(SignChangeEvent event) {
		if (event.getBlock().getType() != Material.WALL_SIGN) return;
		if (!event.getLine(0).equalsIgnoreCase("[Anima]")) return;

		Player player = event.getPlayer();
		if (!player.hasPermission("anima.create")) {
			plugin.sendMessage(player,"You do not have permission to create Anima signs.");
			return;
		}
		if (Anima.econ != null && plugin.signCashCost > 0 && !player.hasPermission("anima.free")) {
			if (Anima.econ.getBalance(player.getName()) < plugin.signCashCost) {
				plugin.sendMessage(player,"You need " + Anima.econ.format(plugin.signCashCost) + " for an Anima sign.");
				return;
			}
			else {
				Anima.econ.withdrawPlayer(player.getName(), plugin.signCashCost);
				plugin.sendMessage(player,Anima.econ.format(plugin.signCashCost) + " has been withdrawn from your account.");
			}
		}
		if (plugin.signXPCost > 0 && !player.hasPermission("anima.free")) {
			Editor expeditor = new Editor(player);
			for (String recalcList : Anima.xpRecalcList) {
				if (recalcList.equalsIgnoreCase(player.getName())) {
					expeditor.recalcTotalExp();
					break;
				}
			}
			if (expeditor.getTotalExp() < plugin.signXPCost) {
				plugin.sendMessage(player,"You need " + plugin.signXPCost + " XP for an Anima sign.");
				return;
			}
			else {
				//expeditor.takeExp(plugin.signXPCost);
				plugin.sendMessage(player,"You paid " + plugin.signXPCost + " XP for an Anima sign.");
			}
		}

		//no cheaters!
		event.setLine(0, ChatColor.BLUE + "[Anima]");
		event.setLine(1, "");
		event.setLine(2, "");
		event.setLine(3, "");

		String name = player.getName();
		if (name.length() > 15) name = name.substring(0, 15);
		event.setLine(1,name);
		event.setLine(2, "0");
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onBlockBreak(BlockBreakEvent event) {
		Block block = event.getBlock();

		//TODO 1.0 check to see if block being broken has an anima sign attached
		if (block.getType() != Material.WALL_SIGN) return;

		BlockState signBlockState = null;
		signBlockState = block.getState();
		final Sign sign = (Sign)signBlockState;

		if (!sign.getLine(0).equalsIgnoreCase(ChatColor.BLUE + "[Anima]")) return;

		Player player = event.getPlayer();
		String name = player.getName();
		if (name.length() > 15) name = name.substring(0, 15);

		if (!sign.getLine(1).equalsIgnoreCase(name) && !player.hasPermission("anima.admin")) {
			plugin.sendMessage(player,"You cannot break this Anima sign.");

			event.setCancelled(true);
			sign.update();
			return;
		}

		int xp = Integer.valueOf(sign.getLine(2));
		double cashCost = plugin.withdrawCashCost * xp;
		double xpCost = Math.ceil(plugin.withdrawXPCostPercent / 100 * xp);
		if (Anima.econ != null && cashCost > 0 && !player.hasPermission("anima.free")) {
			if (Anima.econ.getBalance(name) < cashCost) {
				plugin.sendMessage(player,"You need " + Anima.econ.format(cashCost) + " to break this sign.");
				event.setCancelled(true);
				sign.update();
				return;
			}
			else {
				Anima.econ.withdrawPlayer(name, cashCost);
				plugin.sendMessage(player,Anima.econ.format(cashCost) + " has been withdrawn from your account.");
			}
		}
		Editor expeditor = new Editor(player);
		for (String recalcList : Anima.xpRecalcList) {
			if (recalcList.equalsIgnoreCase(name)) {
				expeditor.recalcTotalExp();
				break;
			}
		}
		if (xpCost > 0 && !player.hasPermission("anima.free")) { //TODO test
			if (expeditor.getTotalExp() < xpCost) {
				plugin.sendMessage(player,"You need " + xpCost + " XP to break this sign.");
				event.setCancelled(true);
				sign.update();
				return;
			}
			else {
				expeditor.takeExp((int)xpCost);
				plugin.sendMessage(player, xpCost + "XP has been deducted from your balance.");
			}
		}
		expeditor.giveExp(xp);
	}
}