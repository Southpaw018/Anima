package com.MoofIT.Minecraft.Anima;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;

public class AnimaItemEnchantListener implements Listener {
	//private final Anima plugin;

	public AnimaItemEnchantListener(Anima instance) {
		//this.plugin = instance;
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onEnchantItem(EnchantItemEvent event) {
		if (event.isCancelled()) return;
		Anima.xpRecalcList.add(event.getEnchanter().getName());
	}
}
