package com.MoofIT.Minecraft.Anima;

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
			player.sendMessage("[Anima] You do not have permission to create Anima signs.");
			return;
		}

		//no cheaters!
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

		if (!sign.getLine(0).equalsIgnoreCase("[Anima]")) return;

		Player player = event.getPlayer();
		String name = player.getName();
		if (name.length() > 15) name = name.substring(0, 15);

		if (!sign.getLine(1).equalsIgnoreCase(name) && !player.hasPermission("anima.admin")) {
			player.sendMessage("[Anima] You cannot break this Anima sign.");

			//org.bukkit.material.Sign signData = (org.bukkit.material.Sign)block.getState().getData();
			event.setCancelled(true);
			sign.update();
			return;
		}

		int xp = Integer.valueOf(sign.getLine(3));
		plugin.awardExperience(player, xp);
	}
}
