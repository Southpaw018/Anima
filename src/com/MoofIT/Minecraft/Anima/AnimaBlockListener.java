package com.MoofIT.Minecraft.Anima;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class AnimaBlockListener implements Listener {
	private Anima plugin;

	public AnimaBlockListener(Anima instance) {
		this.plugin = instance;
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onBlockBreak(BlockBreakEvent event) {
		Block block = event.getBlock();

		if (block.getType() != Material.WALL_SIGN && block.getType() != Material.SIGN_POST) return;

		//sign destruction check
	}
}
