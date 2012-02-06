package com.MoofIT.Minecraft.Anima;

import me.desht.scrollingmenusign.util.ExperienceUtils;

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
		if (!event.getLine(0).equalsIgnoreCase("[Anima]")) {
			return;
		}
		Player player = event.getPlayer();
		if (!player.hasPermission("anima.create")) {
			plugin.sendMessage(player,"You do not have permission to create Anima signs.");
			return;
		}
		if (Anima.econ != null && plugin.signCost > 0) {
			if (Anima.econ.getBalance(player.getName()) < plugin.signCost) {
				plugin.sendMessage(player,"You need " + Anima.econ.format(plugin.signCost) + " for an Anima sign.");
				return;
			}
			else {
				Anima.econ.withdrawPlayer(player.getName(), plugin.signCost);
				plugin.sendMessage(player,Anima.econ.format(plugin.signCost) + " has been withdrawn from your account.");
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

		if (block.getType() != Material.WALL_SIGN && block.getType() != Material.SIGN_POST) return;

		BlockState signBlockState = null;
		signBlockState = block.getState();
		final Sign sign = (Sign)signBlockState;

		if (!sign.getLine(0).equalsIgnoreCase(ChatColor.BLUE + "[Anima]")) return;

		Player player = event.getPlayer();
		String name = player.getName();
		if (name.length() > 15) name = name.substring(0, 15);

		if (!sign.getLine(1).equalsIgnoreCase(name) && !player.hasPermission("anima.admin")) {
			plugin.sendMessage(player,"You cannot break this Anima sign.");

			//org.bukkit.material.Sign signData = (org.bukkit.material.Sign)block.getState().getData();
			event.setCancelled(true);
			sign.update();
			return;
		}
		//TODO economy check
		int xp = Integer.valueOf(sign.getLine(2));
		ExperienceUtils.awardExperience(player, xp);
	}
}

//TODO add breakage protection