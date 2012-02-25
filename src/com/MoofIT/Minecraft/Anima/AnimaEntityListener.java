package com.MoofIT.Minecraft.Anima;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

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
			//TODO 1.0 also check if a block being blown up has an anima sign attached
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerDeath(PlayerDeathEvent event) {
		if (!(event.getEntity() instanceof Player)) return;
		Player p = (Player)event.getEntity();
		Anima.xpRecalcList.remove(p.getName());
	}
}