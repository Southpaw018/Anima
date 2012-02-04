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
import org.bukkit.event.block.BlockPlaceEvent;

public class AnimaBlockListener implements Listener {
	private final Anima plugin;

	public AnimaBlockListener(Anima instance) {
		this.plugin = instance;
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		Block block = event.getBlock();

		if (block.getType() != Material.WALL_SIGN && block.getType() != Material.SIGN_POST) return;

		BlockState signBlockState = null;
		signBlockState = block.getState();
		final Sign sign = (Sign)signBlockState;

		if (!sign.getLine(0).equalsIgnoreCase("[Anima]")) return;

		Player player = event.getPlayer();
		if (!player.hasPermission("anima.create")) {
			player.sendMessage("[Anima] You do not have permission to create Anima signs.");
			return;
		}

		//no cheaters!
		sign.setLine(1, "");
		sign.setLine(2, "");
		sign.setLine(3, "");

		String name = player.getName();
		if (name.length() > 15) name = name.substring(0, 15);
		sign.setLine(1,name);
		sign.setLine(2, "0");
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
		player.giveExp(xp);
	}
}
