package com.MoofIT.Minecraft.Anima;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class AnimaBlockListener implements Listener {
	private Anima plugin;

	public AnimaBlockListener(Anima instance) {
		this.plugin = instance;
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		Block block = event.getBlock();

		if (block.getType() != Material.WALL_SIGN && block.getType() != Material.SIGN_POST) return;
		//are we an anima sign?
		//clear lines 2-4
		//set line 2 to player name
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onBlockBreak(BlockBreakEvent event) {
		Block block = event.getBlock();

		if (block.getType() != Material.WALL_SIGN && block.getType() != Material.SIGN_POST) return;
		//are we an anima sign?
		//are we the player who owns this sign or do we have permission to destroy it?
		//break it and give player XP
	}
}
