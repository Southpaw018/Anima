package com.MoofIT.Minecraft.Anima;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class AnimaPlayerListener implements Listener {
	private final Anima plugin;

	public AnimaPlayerListener(Anima instance) {
		this.plugin = instance;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
		Block b = event.getClickedBlock();
		if (b.getType() != Material.SIGN_POST && b.getType() != Material.WALL_SIGN) return;
		//test to see if were an Anima sign
	}
}