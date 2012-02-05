package com.MoofIT.Minecraft.Anima;

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

public class AnimaPlayerListener implements Listener {
	private final Anima plugin;

	public AnimaPlayerListener(Anima instance) {
		this.plugin = instance;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (event.getAction() != Action.LEFT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
		Block block = event.getClickedBlock();
		if (block.getType() != Material.SIGN_POST && block.getType() != Material.WALL_SIGN) return;

		BlockState signBlockState = null;
		signBlockState = block.getState();
		final Sign sign = (Sign)signBlockState;

		if (!sign.getLine(0).equalsIgnoreCase("[Anima]")) return;

		Player player = event.getPlayer();
		if (sign.getLine(3).equalsIgnoreCase("Updating...")) {
			player.sendMessage("[Anima] This sign is still updating. Please wait a moment.");
			return;
		}
		if (!player.hasPermission("anima.use")) {
			player.sendMessage("[Anima] You do not have permission to use Anima signs.");
			return;
		}

		String name = player.getName();
		if (name.length() > 15) name = name.substring(0, 15);
		if (!sign.getLine(1).equals(name)) return;

		int xp = Integer.valueOf(sign.getLine(2));

		if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if (plugin.totalPlayerXP(player) < 1) {
				player.sendMessage("[Anima] You have no XP to deposit.");
				return;
			}

			if (xp > plugin.maxXP && !player.hasPermission("anima.maxbypass")) {
				player.sendMessage("[Anima] You cannot deposit more XP into this sign.");
				return;
			}
			//TODO economy
			player.giveExp(-1);
			sign.setLine(2, Float.toString(xp + 1));
			sign.setLine(3, "Updating...");
			
			plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
				public void run() {
					sign.setLine(3, "");
					sign.update();
				}
			});
		}
		if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
			if (xp < 1) {
				player.sendMessage("[Anima] This sign has an XP balance of 0. There's nothing to withdraw!");
				return;
			}
			//TODO economy
			player.giveExp(1);
			sign.setLine(2, Float.toString(xp - 1));
			sign.setLine(3, "Updating...");

			plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
				public void run() {
					sign.setLine(3, "");
					sign.update();
				}
			});
		}		
	}
}