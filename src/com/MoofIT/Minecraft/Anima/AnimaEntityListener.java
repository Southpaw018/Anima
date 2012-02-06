package com.MoofIT.Minecraft.Anima;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;

public class AnimaEntityListener implements Listener {
	//private final Anima plugin;

	public AnimaEntityListener(Anima instance) {
		//this.plugin = instance;
	}

	@EventHandler
	public void onEntityExplode(EntityExplodeEvent event)
	{
		if (event.isCancelled()) return;
		for (Block block : event.blockList()) {
			if (block.getType() == Material.WALL_SIGN) {
				BlockState signBlockState = null;
				signBlockState = block.getState();
				final Sign sign = (Sign)signBlockState;

				if (!sign.getLine(0).equalsIgnoreCase(ChatColor.BLUE + "[Anima]")) {
					event.setCancelled(true);
					continue;
				}
			}
			//TODO do we also need to check if we're blowing up the block to which the sign is attached?
		}
	}
}