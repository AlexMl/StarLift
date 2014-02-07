package me.Aubli.Events;

import me.Aubli.StarLift.StarLift;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerMoveListener implements Listener{
	public PlayerMoveListener(StarLift plugin){
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event){
		Player eventPlayer = event.getPlayer();
		
		if(event.getFrom().distance(event.getTo())>0.18){
		//	Bukkit.broadcastMessage(eventPlayer.getName() + " true");
		}
	}
	
	private StarLift plugin;
}
