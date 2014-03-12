package me.Aubli.StarLift.Events;

import me.Aubli.StarLift.LiftManager;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class InventoryClickListener implements Listener{
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event){
		
		if(event.getInventory().getHolder()!=null && event.getInventory().getHolder() instanceof Player){
			
			
			Player eventPlayer = (Player)event.getInventory().getHolder();
			
			if(event.getInventory().getTitle().contains("Welches Stockwerk?")){			
				if(event.getCurrentItem().getType() == Material.ITEM_FRAME){
					if(event.getCurrentItem().hasItemMeta() && event.getCurrentItem().getItemMeta().getDisplayName().contains("Stockwerk")){
						event.setCancelled(true);
						eventPlayer.closeInventory();
						
						int floor = Integer.parseInt(event.getCurrentItem().getItemMeta().getDisplayName().split("werk ")[1]);
						LiftManager.getManager().lift(eventPlayer, Integer.parseInt(event.getInventory().getTitle().split("Fahrstuhl ")[1].split(". Welches")[0]), floor);
						return;
					}
				}
			}
		}		
	}
}
