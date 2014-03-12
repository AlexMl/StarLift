package me.Aubli.StarLift.Events;

import me.Aubli.StarLift.LiftManager;
import me.Aubli.StarLift.SignManager;
import me.Aubli.StarLift.StarLift;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

public class SignChangeListener implements Listener{
	public SignChangeListener(StarLift plugin){
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onSignChange(SignChangeEvent event){
		Player eventPlayer = event.getPlayer();
		
		if(event.getLine(0).equalsIgnoreCase("[starlift]")){
			if(!event.getLine(1).isEmpty()){
				if(!event.getLine(2).isEmpty()){
					
					int liftID = Integer.parseInt(event.getLine(1));
					int floor = Integer.parseInt(event.getLine(2));
				
					if(LiftManager.getManager().exists(liftID)){
						
						event.setLine(0, "[" + ChatColor.GOLD + "StarLift" + ChatColor.RESET + "]");
						event.setLine(1, "Lift: " + liftID);
						event.setLine(2, ChatColor.MAGIC + "Status hier");
						event.setLine(3, "Stockwerk: " + floor);
						
						SignManager.getManager().saveSign(event.getBlock().getLocation(), liftID, floor);
						eventPlayer.sendMessage(plugin.messagePrefix + ChatColor.DARK_GREEN + "Stockwerk gespeichert!");
						return;
					}else{
						eventPlayer.sendMessage(plugin.messagePrefix + ChatColor.RED + "Lift " + liftID + " existiert nicht!");
						event.setCancelled(true);
						return;
					}
				}else{
					eventPlayer.sendMessage(plugin.messagePrefix + ChatColor.RED + "Das Schild ist nicht korrekt!");
					event.setCancelled(true);
					return;
				}
			}else{
				eventPlayer.sendMessage(plugin.messagePrefix + ChatColor.RED + "Das Schild ist nicht korrekt!");
				event.setCancelled(true);
				return;
			}
		}
	}
	
	private StarLift plugin;
}
