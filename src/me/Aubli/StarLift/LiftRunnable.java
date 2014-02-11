package me.Aubli.StarLift;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class LiftRunnable extends BukkitRunnable{
	public LiftRunnable(StarLift plugin, Player playerSender, Location wall1Loc, int liftX, int liftZ, int destStation, int originStation, int temp, int liftID){
		this.plugin = plugin;
		this.player = playerSender;
		this.liftLoc = wall1Loc.clone();
		this.wallLoc = wall1Loc.clone();
		
		this.liftID = liftID;
		this.liftX = liftX;
		this.liftZ = liftZ;
		this.dest = destStation;
		this.orig = originStation;
		this.i = temp;
		
		liftLoc.add(1, 0, 1);
	}
	
	private StarLift plugin;
	private Player player;
	private Location liftLoc;
	private Location wallLoc;
	private int dest;
	private int orig;
	private int liftX;
	private int liftZ;
	private int liftID;
	
	private int i;
	
	@Override
	public void run() {
		
		
		if(player.getLocation().getBlockY()!=dest+1){
			if(dest>orig){ // Fahrstuhl fährt hoch	
			
				player.teleport(player.getLocation().add(0, 1, 0), TeleportCause.PLUGIN);
				
				
				Location tempLoc = liftLoc.clone();
				tempLoc.setY(player.getLocation().getBlockY()-2);
				
				for(int x=0;x<liftX;x++){
					for(int z=0;z<liftZ;z++){
						if(tempLoc.clone().add(x, 0, z).getBlock().getType()==plugin.liftGround){
							tempLoc.clone().add(x, 0, z).getBlock().setType(Material.AIR);
						}
						tempLoc.clone().add(x, 1, z).getBlock().setType(plugin.liftGround);
					}
				}
			
				System.out.println(i);
				if(dest-orig>65){
					if(i==5){
						Bukkit.broadcastMessage("5 Ticks");
						new LiftRunnable(plugin, player, wallLoc.clone(), liftX, liftZ, dest, player.getLocation().getBlockY(), i+1, liftID).runTaskTimer(plugin, 2L, 5L);
						this.cancel();	
					}
					
					if(i==15){
						Bukkit.broadcastMessage("1 Tick");
						new LiftRunnable(plugin, player, wallLoc.clone(), liftX, liftZ, dest, player.getLocation().getBlockY(), i+1, liftID).runTaskTimer(plugin, 0L, 1L);
						this.cancel();		
					}
					
					if(dest-player.getLocation().getBlockY()<=5){
						Bukkit.broadcastMessage("10 Ticks zurück");
						new LiftRunnable(plugin, player, wallLoc.clone(), liftX, liftZ, dest, player.getLocation().getBlockY(), i+1, liftID).runTaskTimer(plugin, 7L, 10L);
						this.cancel();					
					}
					
					if(dest-player.getLocation().getBlockY()<=20){
						Bukkit.broadcastMessage("5 Ticks zurück");
						new LiftRunnable(plugin, player, wallLoc.clone(), liftX, liftZ, dest, player.getLocation().getBlockY(), i+1, liftID).runTaskTimer(plugin, 2L, 5L);
						this.cancel();					
					}
				}else if(dest-orig<66 && dest-orig>15){
					if(i==3){
						Bukkit.broadcastMessage("5 Ticks");
						new LiftRunnable(plugin, player, wallLoc.clone(), liftX, liftZ, dest, player.getLocation().getBlockY(), i+1, liftID).runTaskTimer(plugin, 2L, 5L);
						this.cancel();	
					}
					
					if(dest-player.getLocation().getBlockY()<=5){
						Bukkit.broadcastMessage("10 Ticks zurück");
						new LiftRunnable(plugin, player, wallLoc.clone(), liftX, liftZ, dest, player.getLocation().getBlockY(), i+1, liftID).runTaskTimer(plugin, 7L, 10L);
						this.cancel();					
					}
				}
			
			}else if(orig>dest){ //Fahrstuhl fährt runter
				Location tempLoc = liftLoc.clone();
				tempLoc.setY(player.getLocation().getBlockY()-1);
				
				for(int x=0;x<liftX;x++){
					for(int z=0;z<liftZ;z++){
						tempLoc.clone().add(x, -1, z).getBlock().setType(plugin.liftGround);
						if(tempLoc.clone().add(x, 0, z).getBlock().getType()==plugin.liftGround){
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
						new LiftRunnable(plugin, player, wallLoc.clone(), liftX, liftZ, dest, player.getLocation().getBlockY(), i+1, liftID).runTaskTimer(plugin, 2L, 5L);
						this.cancel();	
					}
					
					if(i==15){
						Bukkit.broadcastMessage("1 Tick");
						new LiftRunnable(plugin, player, wallLoc.clone(), liftX, liftZ, dest, player.getLocation().getBlockY(), i+1, liftID).runTaskTimer(plugin, 0L, 1L);
						this.cancel();		
					}
					
					if(Math.abs(dest-player.getLocation().getBlockY())<=5){
						Bukkit.broadcastMessage("10 Ticks zurück");
						new LiftRunnable(plugin, player, wallLoc.clone(), liftX, liftZ, dest, player.getLocation().getBlockY(), i+1, liftID).runTaskTimer(plugin, 7L, 10L);
						this.cancel();					
					}
					
					if(Math.abs(dest-player.getLocation().getBlockY())<=20){
						Bukkit.broadcastMessage("5 Ticks zurück");
						new LiftRunnable(plugin, player, wallLoc.clone(), liftX, liftZ, dest, player.getLocation().getBlockY(), i+1, liftID).runTaskTimer(plugin, 2L, 5L);
						this.cancel();					
					}
				}else if(Math.abs(dest-orig)<66 && Math.abs(dest-orig)>15){
					if(i==3){
						Bukkit.broadcastMessage("5 Ticks");
						new LiftRunnable(plugin, player, wallLoc.clone(), liftX, liftZ, dest, player.getLocation().getBlockY(), i+1, liftID).runTaskTimer(plugin, 2L, 5L);
						this.cancel();	
					}
					
					if(Math.abs(dest-player.getLocation().getBlockY())<=5){
						Bukkit.broadcastMessage("10 Ticks zurück");
						new LiftRunnable(plugin, player, wallLoc.clone(), liftX, liftZ, dest, player.getLocation().getBlockY(), i+1, liftID).runTaskTimer(plugin, 7L, 10L);
						this.cancel();					
					}
				}	
			}
		}else{
			plugin.liftStats.put(liftID, dest);
			
			File liftFile = new File(plugin.liftPath + liftID + ".yml");
			FileConfiguration liftConfig = YamlConfiguration.loadConfiguration(liftFile);
			
			player.playSound(player.getLocation(), Sound.ANVIL_LAND, (float)65, (float)0);
			player.sendMessage("DING! Stockwerk " + plugin.getFloor(liftID, dest));
			plugin.saveLifts();
						
			Location wall1Loc = new Location(Bukkit.getWorld(liftConfig.getString("StarLift.Location.Welt")), liftConfig.getInt("StarLift.Location.Wand1.X"), liftConfig.getInt("StarLift.Location.Wand1.Y"), liftConfig.getInt("StarLift.Location.Wand1.Z"));
			Location wall2Loc = new Location(Bukkit.getWorld(liftConfig.getString("StarLift.Location.Welt")), liftConfig.getInt("StarLift.Location.Wand2.X"), liftConfig.getInt("StarLift.Location.Wand2.Y"), liftConfig.getInt("StarLift.Location.Wand2.Z"));
			
			int liftX = liftConfig.getInt("StarLift.Lift.Breite");
			int liftZ = liftConfig.getInt("StarLift.Lift.Länge");		
			int doorHeight = liftConfig.getInt("StarLift.Lift.Türhöhe");				

			wall1Loc.setY(dest+1);
			wall2Loc.setY(dest+1);
				
			for(int y=0;y<doorHeight;y++){
				for(int x=0;x<(liftX+2);x++){	
					for(int z=0;z<(liftZ+2);z++){
						if(wall1Loc.clone().add(x, y, z).getBlock().getType()==plugin.doorMaterial){
							wall1Loc.clone().add(x, y, z).getBlock().setType(Material.AIR);
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
