package me.Aubli.Events;

import me.Aubli.SignManager;
import me.Aubli.StarLift;

import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class BlockBreakListener implements Listener{
	public BlockBreakListener(StarLift plugin){
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event){
		Player eventPlayer = event.getPlayer();
		if(eventPlayer.getItemInHand()!=null){
			if(eventPlayer.getItemInHand().equals(plugin.tool)){
				event.setCancelled(true);
				return;
			}
		}
		
		if(event.getBlock().getState() instanceof Sign){
			Sign sign = (Sign) event.getBlock().getState();
			
			if(ChatColor.stripColor(sign.getLine(0) + " ").equals(ChatColor.stripColor(plugin.messagePrefix))){
				if(SignManager.getManager().removeSign(event.getBlock().getLocation())){
					eventPlayer.sendMessage(plugin.messagePrefix + ChatColor.DARK_GREEN + "Schild entfernt!");
					return;
				}else{
					eventPlayer.sendMessage(plugin.messagePrefix + ChatColor.RED + "Schild konnte nicht entfernt werden!");
					return;
				}
			}
		}
	}
	
	private StarLift plugin;
}
