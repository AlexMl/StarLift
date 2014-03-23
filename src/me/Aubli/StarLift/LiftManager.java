package me.Aubli.StarLift;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import me.Aubli.StarLift.Tasks.LiftRunnable;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class LiftManager {
	
	private static LiftManager instance;
	
	private ArrayList<Lift> lifts = new ArrayList<Lift>();
	
	String liftPath = StarLift.getInstance().getDataFolder().getPath() + "/elevators/"; 
	
	public LiftManager(){
		instance = this;
		loadLifts();	
	}
	
	public static LiftManager getManager(){
		return instance;
	}
	
	private int getNewLiftID(){
		if(new File(liftPath).listFiles().length==0){
			return 1;
		}else{
			int[] lifts = new int[new File(liftPath).listFiles().length];
			
			for(int i=0;i<lifts.length;i++){
				lifts[i] = Integer.parseInt(new File(liftPath).listFiles()[i].toString().split(".y")[0].split("elevators/")[1]);
			}		
			
			Arrays.sort(lifts);
				
			for(int k=0;k<lifts.length;k++){
				if(lifts[k]!=k+1){
					return k+1;
				}
					
				if(k+1==lifts.length){
					return lifts.length+1;
				}
			}			
		}
		return -1;
	}
	
	private void loadLifts(){
		File folder = new File(liftPath);
		for(int i=0;i<folder.listFiles().length;i++){
			Lift lift = new Lift(folder.listFiles()[i]);
			if(lift.getWorld()!=null){
				lifts.add(lift);
			//	System.out.println("load " + lifts.get(lifts.size()-1).getLiftID());
			}
		//	System.out.println("load " + lifts.get(lifts.size()-1).getLiftID());					
		}	
	}
	
	public void saveLifts(){		
		try{
			for(int i=0;i<lifts.size();i++){
				//System.out.println("save " + lifts.get(i).getLiftID());
				lifts.get(i).saveLift();
			}
		}catch (IOException e){
			e.printStackTrace();
		}
	}
	
	public void addLift(Player playerSender, Location loc1, Location loc2, boolean closeDoors){
		addLift(playerSender, loc1, loc2, closeDoors, StarLift.getInstance().getDefaultDoor(), StarLift.getInstance().getDefaultGround());
	}
	
	public void addLift(Player playerSender, Location loc1, Location loc2, boolean closeDoors, Material door, Material ground){
		StarLift.getInstance().clearToolItem(playerSender);
		
		Location rightclickLocation = loc2.clone();
		Location leftclickLocation = loc1.clone();
		
		int tempX;
		int tempZ;
		
		if(rightclickLocation.getBlockX()<leftclickLocation.getBlockX()){
			tempX = rightclickLocation.getBlockX();
			rightclickLocation.setX(leftclickLocation.getBlockX());
			leftclickLocation.setX(tempX);
		}								
		
		if(rightclickLocation.getBlockZ()<leftclickLocation.getBlockZ()){
			tempZ = rightclickLocation.getBlockZ();
			rightclickLocation.setZ(leftclickLocation.getBlockZ());
			leftclickLocation.setZ(tempZ);
		}	
		
		if(leftclickLocation.clone().add(0, 1, 0).getBlock().getType().equals(Material.AIR)){
			leftclickLocation.add(0, 1, 0);
		}else{
			playerSender.sendMessage(ChatColor.RED + "Etwas stimmt mit links nicht! :(");
			return;
		}
		
		if(rightclickLocation.clone().add(0, 1, 0).getBlock().getType().equals(Material.AIR)){
			rightclickLocation.add(0, 1, 0);
		}else{
			playerSender.sendMessage(ChatColor.RED + "Etwas stimmt mit rechts nicht! :(");
			return;
		}
		
		while(rightclickLocation.add(0, 1, 0).getBlock().getType()==Material.AIR){}
		
		int liftHeight = (rightclickLocation.getBlockY()-leftclickLocation.getBlockY());
		int liftZ = (Math.abs(rightclickLocation.getBlockZ()-leftclickLocation.getBlockZ()))+1;
		int liftX = (Math.abs(rightclickLocation.getBlockX()-leftclickLocation.getBlockX()))+1;
		
		int stations = 0;
		int liftID = getNewLiftID();
		
		HashMap<Integer, Integer> stationY = new HashMap<Integer, Integer>();

		Bukkit.broadcastMessage("Fahrstuhlhöhe: " + liftHeight);
		Bukkit.broadcastMessage("Fahrstuhlbreite: " + liftX + "x" + liftZ);		
		
		if(leftclickLocation.clone().subtract(1, 0, 1).getBlock().getType()!=Material.AIR && rightclickLocation.clone().add(1, -1, 1).getBlock().getType()!=Material.AIR){
			Location wall1Loc = leftclickLocation.clone().subtract(1, 0, 1);
			Location wall2Loc = rightclickLocation.clone().add(1, -liftHeight, 1);
			int doorHeight = 0;
			
			for(int z=0;z<liftZ;z++){
				for(int x=0;x<liftX;x++){
					leftclickLocation.clone().add(x, 0, z).getBlock().setType(ground);
				}
			}
						
			for(int y=0;y<liftHeight;y++){
				for(int x=1;x<(liftX+1);x++){					
					if(wall1Loc.clone().add(x, y, 0).getBlock().getType()==Material.AIR){
						Location tempLoc = wall1Loc.clone().add(x, y, 0);
						if(tempLoc.clone().add(0, 1, 0).getBlock().getType()==Material.AIR && tempLoc.clone().subtract(0, 1, 0).getBlock().getType()!=Material.AIR){
							if(tempLoc.clone().subtract(1, 0, 0).getBlock().getType()!=Material.AIR){
								
								if(doorHeight==0){
									while(tempLoc.clone().add(0, doorHeight, 0).getBlock().getType()==Material.AIR){
										System.out.println(tempLoc.clone().add(0, doorHeight, 0).getBlock().toString());
										doorHeight++;										
									}
								}
								
								if(closeDoors){
									for(int h=0;h<doorHeight;h++){
										int k = 0;									
										while(tempLoc.clone().add(k, h, 0).getBlock().getType()==Material.AIR){
											tempLoc.clone().add(k, h, 0).getBlock().setType(door);
											k++;
										}
									}
								}
         
								stations++;
								stationY.put(stations, tempLoc.getBlockY()-1);
							}
						}
					}
					
					if(wall1Loc.clone().add(0, y, x).getBlock().getType()==Material.AIR){
						Location tempLoc = wall1Loc.clone().add(0, y, x);
						if(tempLoc.clone().add(0, 1, 0).getBlock().getType()==Material.AIR && tempLoc.clone().subtract(0, 1, 0).getBlock().getType()!=Material.AIR){
							if(tempLoc.clone().subtract(0, 0, 1).getBlock().getType()!=Material.AIR){
						
								if(doorHeight==0){
									while(tempLoc.clone().add(0, doorHeight, 0).getBlock().getType()==Material.AIR){
										doorHeight++;
										System.out.println(tempLoc.clone().add(0, doorHeight, 0).getBlock().toString());
									}
								}
								
								if(closeDoors){
									for(int h=0;h<doorHeight;h++){
										int k = 0;									
										while(tempLoc.clone().add(0, h, k).getBlock().getType()==Material.AIR){
											tempLoc.clone().add(0, h, k).getBlock().setType(door);
											k++;
										}
									}
								}
				
								stations++;
								stationY.put(stations, tempLoc.getBlockY()-1);
							}
						}
					}
			
					if(wall2Loc.clone().add(-x, y, 0).getBlock().getType()==Material.AIR){
						Location tempLoc = wall2Loc.clone().add(-x, y, 0);
						if(tempLoc.clone().add(0, 1, 0).getBlock().getType()==Material.AIR && tempLoc.clone().subtract(0, 1, 0).getBlock().getType()!=Material.AIR){
							if(tempLoc.clone().add(1, 0, 0).getBlock().getType()!=Material.AIR){
							
								if(doorHeight==0){
									while(tempLoc.clone().add(0, doorHeight, 0).getBlock().getType()==Material.AIR){
										doorHeight++;
										System.out.println(tempLoc.clone().add(0, doorHeight, 0).getBlock().toString());
									}
								}
								
								if(closeDoors){
									for(int h=0;h<doorHeight;h++){
										int k = 0;									
										while(tempLoc.clone().add(-k, h, 0).getBlock().getType()==Material.AIR){
											tempLoc.clone().add(-k, h, 0).getBlock().setType(door);
											k++;
										}
									}
								}
								
								stations++;
								stationY.put(stations, tempLoc.getBlockY()-1);
							}
						}
					}
			
					if(wall2Loc.clone().add(0, y, -x).getBlock().getType()==Material.AIR){
						Location tempLoc = wall2Loc.clone().add(0, y, -x);
						if(tempLoc.clone().add(0, 1, 0).getBlock().getType()==Material.AIR && tempLoc.clone().subtract(0, 1, 0).getBlock().getType()!=Material.AIR){
							if(tempLoc.clone().subtract(0, 0, -1).getBlock().getType()!=Material.AIR){								
								
								if(doorHeight==0){
									while(tempLoc.clone().add(0, doorHeight, 0).getBlock().getType()==Material.AIR){
										doorHeight++;
										System.out.println(tempLoc.clone().add(0, doorHeight, 0).getBlock().toString());
									}
								}
								
								if(closeDoors){
									for(int h=0;h<doorHeight;h++){
										int k = 0;									
										while(tempLoc.clone().add(0, h, -k).getBlock().getType()==Material.AIR){
											tempLoc.clone().add(0, h, -k).getBlock().setType(door);
											k++;
										}
									}
								}
								
								stations++;
								stationY.put(stations, tempLoc.getBlockY()-1);
							}
						}
					}
				}
			}
		
			Bukkit.broadcastMessage("Fahrstuhltür: " + doorHeight);
			Bukkit.broadcastMessage(ChatColor.DARK_PURPLE + stationY.toString());
			
			ArrayList<LiftFloor> floors = new ArrayList<LiftFloor>();
			
			for(int i=0;i<stationY.size();i++){
				if(stationY.containsKey(i+1)){
					floors.add(new LiftFloor(stationY.get(i+1), i+1));	
				}
			}
			
			Lift lift = new Lift(liftPath + liftID + ".yml", liftID, loc1.getWorld(), wall1Loc.clone(), wall2Loc.clone(), liftHeight, liftX, liftZ, doorHeight, stations, leftclickLocation.getBlockY(), floors, closeDoors, ground, door);
			lifts.add(lift);		
			playerSender.sendMessage(StarLift.getInstance().messagePrefix + ChatColor.GREEN + "Fahrstuhl mit " + ChatColor.GOLD + stations + ChatColor.GREEN + " Stationen gefunden! LiftID=" + liftID);
		}else{
			playerSender.sendMessage(StarLift.getInstance().messagePrefix + ChatColor.RED + "Etwas stimmt mit den Koordinaten nicht!");
			return;
		}
	}
	
	public boolean removeLift(int LiftID){		
		for(int i=0;i<lifts.size();i++){
			if(lifts.get(i).getLiftID()==LiftID){
				lifts.get(i).delete();
				lifts.remove(i);
				return true;
			}
		}
		return false;
	}
	
	public void callLift(Player playerSender, int liftID, int floor){
		
		Lift lift = getLift(liftID);
	
		int orig = lift.getLiftPosition();
		int dest = lift.getY(floor);
		
		int liftX = lift.getLiftX();
		int liftZ = lift.getLiftZ();
		int doorHeight = lift.getDoorHeight();
		
		Location wall1Loc = lift.getWall1Loc();
		Location wall2Loc = lift.getWall2Loc();
		
		Location tempLoc = wall1Loc.clone().add(1, 0, 1);
		Location temp2Loc = wall1Loc.clone().add(1, 0, 1);
		
		tempLoc.setY(orig);
		temp2Loc.setY(dest);
		for(int z=0;z<liftZ;z++){
			for(int x=0;x<liftX;x++){
				if(tempLoc.clone().add(x, 0, z).getBlock().getType()==lift.getGroundMaterial()){
					tempLoc.clone().add(x, 0, z).getBlock().setType(Material.AIR);
				}
				temp2Loc.clone().add(x, 0, z).getBlock().setType(lift.getGroundMaterial());
			}
		}
		
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
		
		playerSender.playSound(playerSender.getLocation(), Sound.PISTON_RETRACT, (float)75, (float)0);		
		lift.setPosition(dest);
	}

	public void lift(Player playerSender, int liftID, int floor){
		
		Lift lift = getLift(liftID);
		
		Location wall1Loc = lift.getWall1Loc();
		Location wall2Loc = lift.getWall2Loc();
		
		Location wallTemp = wall1Loc.clone();
		wallTemp.setY(playerSender.getLocation().getBlockY());
		wall2Loc.setY(wallTemp.getY());
		
		int destination = lift.getY(floor);
		int doorHeight = lift.getDoorHeight();
		int liftX = lift.getLiftX();
			
		if(lift.closeDoor()){
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
		Bukkit.broadcastMessage(lift.toString() + " " + playerSender.getLocation().getBlockY() + "-" + destination);
		new LiftRunnable(lift, playerSender, destination, playerSender.getLocation().getBlockY(), 0).runTaskTimer(StarLift.getInstance(), 25L, 10L);
		playerSender.playSound(playerSender.getLocation(), Sound.CLICK, 90, 0);		
	}
	
	
	public Lift getLift(int LiftID){
		for(int i=0;i<lifts.size();i++){
			if(lifts.get(i).getLiftID()==LiftID){
				return lifts.get(i);
			}
		}
		return null;
	}
	
	public Lift[] getLifts(){
		Lift[] liftArray = new Lift[lifts.size()];
		for(int i=0;i<lifts.size();i++){
			liftArray[i] = lifts.get(i);
		//	System.out.println(lifts.get(i).getLiftID());
		}
		
		return liftArray;
	}
	
	public boolean exists(int liftID){
		return (getLift(liftID)!=null);
	}
}
