package me.Aubli.Tasks;

import me.Aubli.Lift;
import me.Aubli.StarLift;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class LiftRunnable extends BukkitRunnable{
	
	public LiftRunnable(Lift lift, Player playerSender, int destStation, int originStation, int temp){
		this.lift = lift;
		this.player = playerSender;
		this.liftLoc = lift.getWall1Loc();

		this.liftX = lift.getLiftX();
		this.liftZ = lift.getLiftZ();
		this.dest = destStation;
		this.orig = originStation;
		this.i = temp;
		
		liftLoc.add(1, 0, 1);
	}
	
	private StarLift plugin = StarLift.getInstance();
	private Lift lift;
	private Player player;
	private Location liftLoc;
	private int dest;
	private int orig;
	private int liftX;
	private int liftZ;
	
	private int i;
	
	@Override
	public void run() {		
		
		if(player.getLocation().getBlockY()!=dest+1){
			
			lift.setPosition(player.getLocation().getBlockY()-1);
			
			if(dest>orig){ // Fahrstuhl fährt hoch	
			
				player.teleport(player.getLocation().add(0, 1, 0), TeleportCause.PLUGIN);			
				
				Location tempLoc = liftLoc.clone();
				tempLoc.setY(player.getLocation().getBlockY()-2);
				
				for(int x=0;x<liftX;x++){
					for(int z=0;z<liftZ;z++){
						if(tempLoc.clone().add(x, 0, z).getBlock().getType()==lift.getGroundMaterial()){
							tempLoc.clone().add(x, 0, z).getBlock().setType(Material.AIR);
						}
						tempLoc.clone().add(x, 1, z).getBlock().setType(lift.getGroundMaterial());
					}
				}
			
				System.out.println(i);
				if(dest-orig>65){
					if(i==5){
						Bukkit.broadcastMessage("5 Ticks");
						new LiftRunnable(lift, player, dest, player.getLocation().getBlockY(), i+1).runTaskTimer(plugin, 2L, 5L);
						this.cancel();	
					}
					
					if(i==15){
						Bukkit.broadcastMessage("1 Tick");
						new LiftRunnable(lift, player, dest, player.getLocation().getBlockY(), i+1).runTaskTimer(plugin, 0L, 1L);
						this.cancel();		
					}
					
					if(dest-player.getLocation().getBlockY()<=5){
						Bukkit.broadcastMessage("10 Ticks zurück");
						new LiftRunnable(lift, player, dest, player.getLocation().getBlockY(), i+1).runTaskTimer(plugin, 7L, 10L);
						this.cancel();					
					}
					
					if(dest-player.getLocation().getBlockY()<=20){
						Bukkit.broadcastMessage("5 Ticks zurück");
						new LiftRunnable(lift, player, dest, player.getLocation().getBlockY(), i+1).runTaskTimer(plugin, 2L, 5L);
						this.cancel();					
					}
				}else if(dest-orig<66 && dest-orig>15){
					if(i==3){
						Bukkit.broadcastMessage("5 Ticks");
						new LiftRunnable(lift, player, dest, player.getLocation().getBlockY(), i+1).runTaskTimer(plugin, 2L, 5L);
						this.cancel();	
					}
					
					if(dest-player.getLocation().getBlockY()<=5){
						Bukkit.broadcastMessage("10 Ticks zurück");
						new LiftRunnable(lift, player, dest, player.getLocation().getBlockY(), i+1).runTaskTimer(plugin, 7L, 10L);
						this.cancel();					
					}
				}
			
			}else if(orig>dest){ //Fahrstuhl fährt runter
				Location tempLoc = liftLoc.clone();
				tempLoc.setY(player.getLocation().getBlockY()-1);
				
				for(int x=0;x<liftX;x++){
					for(int z=0;z<liftZ;z++){
						tempLoc.clone().add(x, -1, z).getBlock().setType(lift.getGroundMaterial());
						if(tempLoc.clone().add(x, 0, z).getBlock().getType()==lift.getGroundMaterial()){
							tempLoc.clone().add(x, 0, z).getBlock().setType(Material.AIR);
						}						
					}
				}
				
				player.teleport(player.getLocation().subtract(0, 0.85, 0), TeleportCause.PLUGIN);			
				player.setVelocity(new Vector(0, 0, 0));
				
				System.out.println(i);
				if(Math.abs(dest-orig)>65){
					if(i==5){
						Bukkit.broadcastMessage("5 Ticks");
						new LiftRunnable(lift, player, dest, player.getLocation().getBlockY(), i+1).runTaskTimer(plugin, 2L, 5L);
						this.cancel();	
					}
					
					if(i==15){
						Bukkit.broadcastMessage("1 Tick");
						new LiftRunnable(lift, player, dest, player.getLocation().getBlockY(), i+1).runTaskTimer(plugin, 0L, 1L);
						this.cancel();		
					}
					
					if(Math.abs(dest-player.getLocation().getBlockY())<=5){
						Bukkit.broadcastMessage("10 Ticks zurück");
						new LiftRunnable(lift, player, dest, player.getLocation().getBlockY(), i+1).runTaskTimer(plugin, 7L, 10L);
						this.cancel();					
					}
					
					if(Math.abs(dest-player.getLocation().getBlockY())<=20){
						Bukkit.broadcastMessage("5 Ticks zurück");
						new LiftRunnable(lift, player, dest, player.getLocation().getBlockY(), i+1).runTaskTimer(plugin, 2L, 5L);
						this.cancel();					
					}
				}else if(Math.abs(dest-orig)<66 && Math.abs(dest-orig)>15){
					if(i==3){
						Bukkit.broadcastMessage("5 Ticks");
						new LiftRunnable(lift, player, dest, player.getLocation().getBlockY(), i+1).runTaskTimer(plugin, 2L, 5L);
						this.cancel();	
					}
					
					if(Math.abs(dest-player.getLocation().getBlockY())<=5){
						Bukkit.broadcastMessage("10 Ticks zurück");
						new LiftRunnable(lift, player, dest, player.getLocation().getBlockY(), i+1).runTaskTimer(plugin, 7L, 10L);
						this.cancel();					
					}
				}	
			}
		}else{
			player.playSound(player.getLocation(), Sound.ANVIL_LAND, (float)65, (float)0);
			player.sendMessage("DING! Stockwerk " + lift.getFloor(dest));
			lift.setPosition(dest);
						
			Location wall1Loc = lift.getWall1Loc();
			Location wall2Loc = lift.getWall2Loc();
			
			int liftX = lift.getLiftX();
			int liftZ = lift.getLiftZ();	
			int doorHeight = lift.getDoorHeight();		

			wall1Loc.setY(dest+1);
			wall2Loc.setY(dest+1);
			
			if(lift.closeDoor()){	
				for(int y=0;y<doorHeight;y++){
					for(int x=0;x<(liftX+2);x++){	
						for(int z=0;z<(liftZ+2);z++){
							if(wall1Loc.clone().add(x, y, z).getBlock().getType()==lift.getDoorMaterial()){
								wall1Loc.clone().add(x, y, z).getBlock().setType(Material.AIR);
							}
						}				
					}
				}
			}
			
			player.playSound(player.getLocation(), Sound.PISTON_RETRACT, 80, 0);			
			this.cancel();
		}
				
		i++;
	}

}
