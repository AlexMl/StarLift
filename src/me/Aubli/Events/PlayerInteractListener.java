package me.Aubli.Events;

import java.util.HashMap;

import me.Aubli.LiftManager;
import me.Aubli.StarLift;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class PlayerInteractListener implements Listener{
	public PlayerInteractListener(StarLift plugin){
		this.plugin = plugin;
	}
	
	private HashMap<String, Location> locs = new HashMap<String, Location>();
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event){
		Player eventPlayer = event.getPlayer();
		
		if(eventPlayer.getItemInHand()!=null && eventPlayer.getItemInHand().equals(plugin.tool)){
			if(eventPlayer.hasPermission("sl.create")){
				event.setCancelled(true);
				
				if(event.getAction().equals(Action.LEFT_CLICK_BLOCK)){
					locs.put("left", event.getClickedBlock().getLocation());
					eventPlayer.sendMessage(plugin.messagePrefix + "linker Click gespeichert");					
				}
				
				if(event.getAction().equals(Action.RIGHT_CLICK_BLOCK)){
					locs.put("right", event.getClickedBlock().getLocation());
					eventPlayer.sendMessage(plugin.messagePrefix + "rechter Click gespeichert");	
				}
				
				if(locs.containsKey("left") && locs.containsKey("right")){
					LiftManager.getManager().addLift(eventPlayer, locs.get("left"), locs.get("right"), true);
					locs.clear();
					return;
				}
			}
		}
		
		if(event.getAction()==Action.RIGHT_CLICK_BLOCK){
			if(event.getClickedBlock().getState() instanceof Sign){
				Sign sign = (Sign) event.getClickedBlock().getState();
				
				if(ChatColor.stripColor(sign.getLine(0) + " ").equals(ChatColor.stripColor(plugin.messagePrefix))){
					int liftID = Integer.parseInt(sign.getLine(1).split("t: ")[1]);
					LiftManager.getManager().callLift(eventPlayer, liftID, Integer.parseInt(sign.getLine(3).split(": ")[1]));
					return;
				}
			}
		}
	}
	
	private StarLift plugin;
}
