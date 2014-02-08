package me.Aubli.StarLift;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;

public class GuiRunnable extends BukkitRunnable {
	public GuiRunnable(Player player, Inventory inv){	
		this.player = player;
		this.inv = inv;		
	}	

	private Player player;
	private Inventory inv;
	
	@Override
	public void run() {	
		player.openInventory(inv);
		this.cancel();
	}

}
