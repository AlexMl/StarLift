package me.Aubli.StarLift.Events;

import java.util.HashMap;

import me.Aubli.StarLift.LiftManager;
import me.Aubli.StarLift.StarLift;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class PlayerInteractListener implements Listener{
	public PlayerInteractListener(StarLift plugin){
		this.plugin = plugin;
		piListener = this;
	}
	
	private HashMap<String, Location> locs = new HashMap<String, Location>();
	
	public boolean closeDoors = true;
	public Material door;
	public Material ground;
	
	public static PlayerInteractListener piListener;
	
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
					if(door!=null && ground !=null){
						LiftManager.getManager().addLift(eventPlayer, locs.get("left"), locs.get("right"), closeDoors, door, ground);
						locs.clear();
						door = null;
						ground = null;
						return;
					}else{
						LiftManager.getManager().addLift(eventPlayer, locs.get("left"), locs.get("right"), closeDoors);
						locs.clear();
						return;
					}
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
	
	public static PlayerInteractListener getInstance(){
		return piListener;
	}
	
	public void setMaterial(Material door, Material ground){
		this.door = door;
		this.ground = ground;
	}
	
	public void setBoolean(boolean close){
		this.closeDoors = close;
	}
	
	private StarLift plugin;
}
