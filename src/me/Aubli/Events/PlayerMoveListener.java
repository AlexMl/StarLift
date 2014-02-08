package me.Aubli.Events;

import me.Aubli.StarLift.StarLift;

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
		//	Bukkit.broadcastMessage(Math.abs(event.getFrom().getY()-event.getTo().getY()) + "");
			if(Math.abs(event.getFrom().getY()-event.getTo().getY())<1){
				plugin.enterLift(eventPlayer, event.getFrom(), event.getTo());
				return;
			}
		//	Bukkit.broadcastMessage(eventPlayer.getName() + " true");
		}
	}
	
	private StarLift plugin;
}
