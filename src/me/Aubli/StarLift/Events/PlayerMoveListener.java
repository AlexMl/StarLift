package me.Aubli.StarLift.Events;

import me.Aubli.StarLift.Lift;
import me.Aubli.StarLift.LiftManager;
import me.Aubli.StarLift.StarLift;
import me.Aubli.StarLift.Tasks.GuiRunnable;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerMoveListener implements Listener{
	
	private int guiTaskID;
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event){
		Player eventPlayer = event.getPlayer();
		
		if(event.getFrom().distance(event.getTo())>0.05){
			if(Math.abs(event.getFrom().getY()-event.getTo().getY())<1){
				if(enterLift(eventPlayer, event.getFrom(), event.getTo())==false){
					leaveLift(eventPlayer, event.getFrom(), event.getTo());
				}				
				return;
			}
		}
	}
	
	private boolean enterLift(Player playerSender, Location from, Location to){
		
		for(int i=0;i<LiftManager.getManager().getLifts().length;i++){
			
			Lift lift = LiftManager.getManager().getLifts()[i];
		//	Bukkit.broadcastMessage(ChatColor.RED + lift.toString());
			Location wall1Loc = lift.getWall1Loc();
			Location wall2Loc = lift.getWall2Loc();
			
			wall1Loc.add(0.8, 0, 0.8);
			wall2Loc.add(-0.2, lift.getHeight(), -0.2);
			
		/*	Bukkit.broadcastMessage(wall1Loc.toString() + "  " + wall2Loc.toString());
			
			Bukkit.broadcastMessage("FromX > wall2LocX : " + ChatColor.AQUA + from.getX() + ChatColor.RESET + ">" + ChatColor.GOLD + wall2Loc.getX() + ChatColor.RESET + " = " + ChatColor.GREEN + (from.getX()>wall2Loc.getX()));
			Bukkit.broadcastMessage("FromX < wall1LocX : " + ChatColor.AQUA + from.getX() + ChatColor.RESET + "<" + ChatColor.GOLD + wall1Loc.getX() + ChatColor.RESET + " = " + ChatColor.GREEN + (from.getX()<wall1Loc.getX()));

			Bukkit.broadcastMessage("FromZ > wall2LocZ : " + ChatColor.AQUA + from.getZ() + ChatColor.RESET + ">" + ChatColor.GOLD + wall2Loc.getZ() + ChatColor.RESET + " = " + ChatColor.GREEN + (from.getZ()>wall2Loc.getZ()));
			Bukkit.broadcastMessage("FromZ < wall1LocZ : " + ChatColor.AQUA + from.getZ() + ChatColor.RESET + "<" + ChatColor.GOLD + wall1Loc.getZ() + ChatColor.RESET + " = " + ChatColor.GREEN + (from.getZ()<wall1Loc.getZ()) + "\n\n");
		*/	
			
			if(from.getX()>wall2Loc.getX() || from.getX()<wall1Loc.getX() || from.getZ()>wall2Loc.getZ() || from.getZ()<wall1Loc.getZ()){
				if(to.getX()<wall2Loc.getX() && to.getX()>wall1Loc.getX()){
					if(to.getZ()<wall2Loc.getZ() && to.getZ()>wall1Loc.getZ()){
						if(to.getY()<wall2Loc.getY() && to.getY()>wall1Loc.getY()){
							
							playerSender.sendMessage("im fahrstuhl Nummer " + lift.getLiftID());
							Inventory liftGui = Bukkit.createInventory(playerSender, ((int)Math.ceil((double)lift.getStations().size()/9))*9, "Fahrstuhl " + lift.getLiftID() + ". Welches Stockwerk?");
							liftGui.clear();
							
							ItemStack liftitem = new ItemStack(Material.ITEM_FRAME);
							ItemMeta liftMeta = liftitem.getItemMeta();
							
							for(int k=0;k<lift.getStations().size();k++){							
								liftMeta.setDisplayName("Stockwerk " + (k+1));
								liftitem.setItemMeta(liftMeta);
								liftGui.addItem(liftitem);
							}
							
							guiTaskID = new GuiRunnable(playerSender, liftGui).runTaskLater(StarLift.getInstance(), 1*20L).getTaskId();
							return true;
						}
					}
				}
			}		
		}
		return false;
	}
	
	private boolean leaveLift(final Player playerSender, Location from, Location to){
		
		for(int i=0;i<LiftManager.getManager().getLifts().length;i++){
			
			final Lift lift = LiftManager.getManager().getLifts()[i];
			
			
			Location wall1Loc = lift.getWall1Loc();
			Location wall2Loc = lift.getWall2Loc();		
			wall1Loc.add(0.8, 0, 0.8);
			wall2Loc.add(-0.2, lift.getHeight(), -0.2);
				
			/*	Bukkit.broadcastMessage(wall1Loc.toString() + "  " + wall2Loc.toString());
				
				Bukkit.broadcastMessage("FromX < wall2LocX : " + ChatColor.AQUA + from.getX() + ChatColor.RESET + "<" + ChatColor.GOLD + wall2Loc.getX() + ChatColor.RESET + " = " + ChatColor.GREEN + (from.getX()<wall2Loc.getX()));
				Bukkit.broadcastMessage("FromX > wall1LocX : " + ChatColor.AQUA + from.getX() + ChatColor.RESET + ">" + ChatColor.GOLD + wall1Loc.getX() + ChatColor.RESET + " = " + ChatColor.GREEN + (from.getX()>wall1Loc.getX()));
	
				Bukkit.broadcastMessage("FromZ < wall2LocZ : " + ChatColor.AQUA + from.getZ() + ChatColor.RESET + "<" + ChatColor.GOLD + wall2Loc.getZ() + ChatColor.RESET + " = " + ChatColor.GREEN + (from.getZ()<wall2Loc.getZ()));
				Bukkit.broadcastMessage("FromZ > wall1LocZ : " + ChatColor.AQUA + from.getZ() + ChatColor.RESET + ">" + ChatColor.GOLD + wall1Loc.getZ() + ChatColor.RESET + " = " + ChatColor.GREEN + (from.getZ()>wall1Loc.getZ()) + "\n\n");
			*/	
				
			if(to.getX()>wall2Loc.getX() || to.getX()<wall1Loc.getX() || to.getZ()>wall2Loc.getZ() || to.getZ()<wall1Loc.getZ()){
				if(from.getX()<wall2Loc.getX() && from.getX()>wall1Loc.getX()){
					if(from.getZ()<wall2Loc.getZ() && from.getZ()>wall1Loc.getZ()){
						if(from.getY()<wall2Loc.getY() && from.getY()>wall1Loc.getY()){
							playerSender.sendMessage(ChatColor.RED + "aus fahrstuhl " + lift.getLiftID());
							Bukkit.getScheduler().cancelTask(guiTaskID);
							final Location fromLoc = from.clone();
							if(lift.closeDoor()){
								new BukkitRunnable() {
									
									@Override
									public void run() {
										
										Location wall1Loc = lift.getWall1Loc();
										Location wall2Loc = lift.getWall2Loc();
										
										Location wallTemp = wall1Loc.clone();
										wallTemp.setY(fromLoc.getBlockY());
										wall2Loc.setY(wallTemp.getY());									
										
										int doorHeight = lift.getDoorHeight();
										int liftX = lift.getLiftX();
										
										for(int x=1;x<(liftX+1);x++){					
											if(wallTemp.clone().add(x, 0, 0).getBlock().getType()==Material.AIR){
												Location tempLoc = wallTemp.clone().add(x, 0, 0);
												if(tempLoc.clone().add(0, 1, 0).getBlock().getType()==Material.AIR && tempLoc.clone().subtract(0, 1, 0).getBlock().getType()!=Material.AIR){
													if(tempLoc.clone().subtract(1, 0, 0).getBlock().getType()!=Material.AIR){
														
														for(int j=0;j<doorHeight;j++){
															
															int k = 0;
															
															while(tempLoc.clone().add(k,j,0).getBlock().getType()==Material.AIR){
																tempLoc.clone().add(k, j, 0).getBlock().setType(lift.getDoorMaterial());
																k++;
															}
														}
													}
												}
											}
											
											if(wallTemp.clone().add(0, 0, x).getBlock().getType()==Material.AIR){
												Location tempLoc = wallTemp.clone().add(0, 0, x);
												if(tempLoc.clone().add(0, 1, 0).getBlock().getType()==Material.AIR && tempLoc.clone().subtract(0, 1, 0).getBlock().getType()!=Material.AIR){
													if(tempLoc.clone().subtract(0, 0, 1).getBlock().getType()!=Material.AIR){
														
														for(int j=0;j<doorHeight;j++){
	
															int k = 0;
															
															while(tempLoc.clone().add(0, j, k).getBlock().getType()==Material.AIR){
																tempLoc.clone().add(0, j, k).getBlock().setType(lift.getDoorMaterial());
																k++;
															}
														}		
													}
												}
											}
											
											if(wall2Loc.clone().add(-x, 0, 0).getBlock().getType()==Material.AIR){
												Location tempLoc = wall2Loc.clone().add(-x, 0, 0);
												if(tempLoc.clone().add(0, 1, 0).getBlock().getType()==Material.AIR && tempLoc.clone().subtract(0, 1, 0).getBlock().getType()!=Material.AIR){
													if(tempLoc.clone().add(1, 0, 0).getBlock().getType()!=Material.AIR){
														
														for(int j=0;j<doorHeight;j++){
	
															int k = 0;
															
															while(tempLoc.clone().add(-k, j, 0).getBlock().getType()==Material.AIR){
																tempLoc.clone().add(-k, j, 0).getBlock().setType(lift.getDoorMaterial());
																k++;
															}
														}
													}
												}
											}
											
											if(wall2Loc.clone().add(0, 0, -x).getBlock().getType()==Material.AIR){
												Location tempLoc = wall2Loc.clone().add(0, 0, -x);
												if(tempLoc.clone().add(0, 1, 0).getBlock().getType()==Material.AIR && tempLoc.clone().subtract(0, 1, 0).getBlock().getType()!=Material.AIR){
													if(tempLoc.clone().subtract(0, 0, -1).getBlock().getType()!=Material.AIR){	
														
														for(int j=0;j<doorHeight;j++){
	
															int k = 0;
															
															while(tempLoc.clone().add(0, j, -k).getBlock().getType()==Material.AIR){
																tempLoc.clone().add(0, j, -k).getBlock().setType(lift.getDoorMaterial());
																k++;
															}
														}
													}
												}
											}
										}
										playerSender.playSound(playerSender.getLocation(), Sound.PISTON_EXTEND, 80, 0);
										
									}
								}.runTaskLater(StarLift.getInstance(), 1*20L);
								
								return true;
							}
						}
					}
				}
			}		
		}
			return false;
	}
	
}
