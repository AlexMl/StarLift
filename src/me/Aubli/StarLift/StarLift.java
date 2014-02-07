package me.Aubli.StarLift;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.logging.Logger;

import me.Aubli.Events.BlockBreakListener;
import me.Aubli.Events.PlayerInteractListener;
import me.Aubli.Events.PlayerMoveListener;
import me.Aubli.Events.SignChangeListener;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class StarLift extends JavaPlugin{
	
	public final Logger logger = Bukkit.getLogger();
	
	public String liftPath = getDataFolder().getPath() + "/elevators/"; 
	public String messagePrefix = ChatColor.DARK_GRAY + "[" + ChatColor.GOLD + "StarLift" + ChatColor.DARK_GRAY + "] " + ChatColor.RESET;
	
	public boolean enable = false;
	
	public File signFile;
	public FileConfiguration signConfig;
	
	public ItemStack tool;
	public Material liftGround;
	
	public HashMap<Integer, Integer> liftStats = new HashMap<Integer, Integer>(); //Hashmap mit fahrstuhlID und aktueller y koordinate!
	
	@Override
	public void onDisable(){
		clearToolItem(null);
		System.out.println(liftStats.toString());
		//saveLifts();
		logger.info("[StarLift] Plugin is disabled!");
	}
	
	@Override
	public void onEnable(){
		
		loadConfig();
		registerCommands();
		registerListeners();	
		loadLifts();
		
		if(enable==false){
			Bukkit.getPluginManager().disablePlugin(this);
		}else{		
			logger.info("[StarLift] Plugin is enabled!");
		}
	}
	
	private void registerListeners(){
		PluginManager pm = Bukkit.getPluginManager();
		
		pm.registerEvents(new PlayerInteractListener(this), this);
		pm.registerEvents(new BlockBreakListener(this), this);
		pm.registerEvents(new SignChangeListener(this), this);
		pm.registerEvents(new PlayerMoveListener(this), this);
	}
	
	private void registerCommands(){
		this.getCommand("sl").setExecutor(new StarLiftCommands(this));
	}
		
	private void loadConfig(){
		
		File liftFolder = new File(liftPath);
		signFile = new File(getDataFolder().getPath() + "/signs.yml");
		signConfig = YamlConfiguration.loadConfiguration(signFile);
		
		if(!liftFolder.exists() || !signFile.exists()){
			try{
				liftFolder.mkdirs();
				signFile.createNewFile();
			}catch(IOException e){
				e.printStackTrace();
			}
		}
		
		
		this.getConfig().addDefault("config.general.enable", true);
		
		this.getConfig().addDefault("config.lift.material", Material.GLASS.name());
		
		this.getConfig().options().copyDefaults(true);
		this.saveConfig();
	
		enable = this.getConfig().getBoolean("config.general.enable");
		
		tool = new ItemStack(Material.STICK);		
		ItemMeta toolMeta = tool.getItemMeta();		
		toolMeta.setDisplayName("Fahrstuhl tool");		
		tool.setItemMeta(toolMeta);		
		
		liftGround = Material.valueOf(this.getConfig().getString("config.lift.material"));
	}
	
	public void clearToolItem(Player playerSender){
		
		if(playerSender!=null){
			if(playerSender.getInventory().contains(tool)){
				playerSender.getInventory().remove(tool);
			}
		}else{
			for(int i=0;i<Bukkit.getOnlinePlayers().length;i++){
				if(Bukkit.getOnlinePlayers()[i].getInventory().contains(tool)){
					Bukkit.getOnlinePlayers()[i].getInventory().remove(tool);
				}
			}
		}
		
	}
	
	public void callElevator(Player playerSender, int liftID){
		
		File liftFile = new File(liftPath + liftID + ".yml");
		FileConfiguration liftConfig = YamlConfiguration.loadConfiguration(liftFile);
		Bukkit.broadcastMessage(liftStats.toString());
		int orig = liftStats.get(liftID);
		int dest = playerSender.getLocation().getBlockY();
		
		int liftX = liftConfig.getInt("StarLift.Lift.Breite");
		int liftZ = liftConfig.getInt("StarLift.Lift.Länge");		
		
		Location wall1Loc = new Location(Bukkit.getWorld(liftConfig.getString("StarLift.Location.Welt")), liftConfig.getInt("StarLift.Location.Wand1.X"), liftConfig.getInt("StarLift.Location.Wand1.Y"), liftConfig.getInt("StarLift.Location.Wand1.Z"));
		wall1Loc.add(1, 0, 1);
		
		Location tempLoc = wall1Loc.clone();
		Location temp2Loc = wall1Loc.clone();
		
		tempLoc.setY(orig);
		temp2Loc.setY(dest);
		for(int z=0;z<liftZ;z++){
			for(int x=0;x<liftX;x++){
				if(tempLoc.clone().add(x, 0, z).getBlock().getType()==liftGround){
					tempLoc.clone().add(x, 0, z).getBlock().setType(Material.AIR);
				}
				temp2Loc.clone().add(x, 0, z).getBlock().setType(liftGround);
			}
		}
		
		liftStats.put(liftID, dest);
		saveLifts();
	}
	
	public void saveElevator(Player playerSender, Location loc1, Location loc2){
		
		clearToolItem(playerSender);
		
		File liftFile = null;
		
		if(new File(liftPath).listFiles().length==0){
			liftFile = new File(liftPath + "1.yml");
		}else{
			int[] lifts = new int[new File(liftPath).listFiles().length];
			
			for(int i=0;i<lifts.length;i++){
				lifts[i] = Integer.parseInt(new File(liftPath).listFiles()[i].toString().split(".y")[0].split("elevators/")[1]);
			}		
			
			Arrays.sort(lifts);
				
			for(int k=0;k<lifts.length;k++){
				if(lifts[k]!=k+1){
					liftFile = new File(liftPath + (k+1) + ".yml");
					break;
				}
					
				if(k+1==lifts.length){
					liftFile = new File(liftPath + (lifts.length+1) + ".yml");					
					break;
				}
			}			
		}
		
		FileConfiguration liftConfig = YamlConfiguration.loadConfiguration(liftFile);
		
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
		
		HashMap<Integer, Integer> stationY = new HashMap<Integer, Integer>();
		
		liftConfig.set("StarLift.Allgemein.ID", liftFile.getName().split(".y")[0]);
		liftConfig.set("StarLift.Location.Welt", leftclickLocation.getWorld().getName());
		liftConfig.set("StarLift.Location.Wand1", "");
		liftConfig.set("StarLift.Location.Wand2", "");
		liftConfig.set("StarLift.Lift.Höhe", liftHeight);
		liftConfig.set("StarLift.Lift.Breite", liftX);
		liftConfig.set("StarLift.Lift.Länge", liftZ);
		liftConfig.set("StarLift.Lift.Position", "");
		liftConfig.set("StarLift.Lift.Stationen", "");
		liftConfig.set("StarLift.Stationen", "");
		
		Bukkit.broadcastMessage("Fahrstuhlhöhe: " + liftHeight);
		Bukkit.broadcastMessage("Fahrstuhlbreite: " + liftX + "x" + liftZ);		
		
		if(leftclickLocation.clone().subtract(1, 0, 1).getBlock().getType()!=Material.AIR && rightclickLocation.clone().add(1, -1, 1).getBlock().getType()!=Material.AIR){
			Location wall1Loc = leftclickLocation.clone().subtract(1, 0, 1);
			Location wall2Loc = rightclickLocation.clone().add(1, -liftHeight, 1);
			
			for(int z=0;z<liftZ;z++){
				for(int x=0;x<liftX;x++){
					leftclickLocation.clone().add(x, 0, z).getBlock().setType(liftGround);
				}
			}
			
			for(int y=0;y<liftHeight;y++){
				for(int x=1;x<(liftX+1);x++){					
					if(wall1Loc.clone().add(x, y, 0).getBlock().getType()==Material.AIR){
						//Bukkit.broadcastMessage(ChatColor.GREEN + "Station @" + wall1Loc.clone().add(x, y, 0).getBlockX() + ":" +  wall1Loc.clone().add(x, y, 0).getBlockY() + ":" +  wall1Loc.clone().add(x, y, 0).getBlockZ());
						Location tempLoc = wall1Loc.clone().add(x, y, 0);
						if(tempLoc.clone().add(0, 1, 0).getBlock().getType()==Material.AIR && tempLoc.clone().subtract(0, 1, 0).getBlock().getType()!=Material.AIR){
							if(tempLoc.clone().subtract(1, 0, 0).getBlock().getType()!=Material.AIR){
          
          for(int k=0;k<liftX;k++){
             tempLoc.clone().add(k, 1, 0).getBlock().setType(Material.IRON_FENCE);
          }
         
								stations++;
								stationY.put(stations, tempLoc.getBlockY());
							}
						}
					}
				}
				
				for(int z=1;z<(liftZ+1);z++){
					if(wall1Loc.clone().add(0, y, z).getBlock().getType()==Material.AIR){
						Location tempLoc = wall1Loc.clone().add(0, y, z);
						if(tempLoc.clone().add(0, 1, 0).getBlock().getType()==Material.AIR && tempLoc.clone().subtract(0, 1, 0).getBlock().getType()!=Material.AIR){
							if(tempLoc.clone().subtract(0, 0, 1).getBlock().getType()!=Material.AIR){
						
          for(int k=0;k<liftX;k++){
             tempLoc.clone().add(0, 1, k).getBlock().setType(Material.IRON_FENCE);
          }

        		stations++;
								stationY.put(stations, tempLoc.getBlockY());
							}
						}
					}
				}
				
				for(int x=1;x<(liftX+1);x++){					
					if(wall2Loc.clone().add(-x, y, 0).getBlock().getType()==Material.AIR){
						Location tempLoc = wall2Loc.clone().add(-x, y, 0);
						if(tempLoc.clone().add(0, 1, 0).getBlock().getType()==Material.AIR && tempLoc.clone().subtract(0, 1, 0).getBlock().getType()!=Material.AIR){
							if(tempLoc.clone().add(1, 0, 0).getBlock().getType()!=Material.AIR){
							
          for(int k=0;k<liftX;k++){
             tempLoc.clone().add(-k, 1, 0).getBlock().setType(Material.IRON_FENCE);
          }

         	stations++;
								stationY.put(stations, tempLoc.getBlockY());
							}
						}
					}
				}
				
				for(int z=1;z<(liftZ+1);z++){
					if(wall2Loc.clone().add(0, y, -z).getBlock().getType()==Material.AIR){
						Location tempLoc = wall2Loc.clone().add(0, y, -z);
						if(tempLoc.clone().add(0, 1, 0).getBlock().getType()==Material.AIR && tempLoc.clone().subtract(0, 1, 0).getBlock().getType()!=Material.AIR){
							if(tempLoc.clone().subtract(0, 0, -1).getBlock().getType()!=Material.AIR){
								

          for(int k=0;k<liftX;k++){
             tempLoc.clone().add(0, 1, -k).getBlock().setType(Material.IRON_FENCE);
          }
          stations++;
								stationY.put(stations, tempLoc.getBlockY());
							}
						}
					}
				}
			}
			
			liftConfig.set("StarLift.Lift.Stationen", stations);			
			liftConfig.set("StarLift.Location.Wand1.X", wall1Loc.getBlockX());
			liftConfig.set("StarLift.Location.Wand1.Y", wall1Loc.getBlockY());
			liftConfig.set("StarLift.Location.Wand1.Z", wall1Loc.getBlockZ());
			liftConfig.set("StarLift.Location.Wand2.X", wall2Loc.getBlockX());
			liftConfig.set("StarLift.Location.Wand2.Y", wall2Loc.getBlockY());
			liftConfig.set("StarLift.Location.Wand2.Z", wall2Loc.getBlockZ());
			
			liftStats.put(liftConfig.getInt("StarLift.Allgemein.ID"), stationY.get(1));
			
			Bukkit.broadcastMessage(ChatColor.DARK_PURPLE + stationY.toString());
			
			
			for(int i=0;i<stationY.size();i++){
				if(stationY.containsKey(i+1)){
					liftConfig.set("StarLift.Stationen." + (i+1), stationY.get(i+1));
				}
			}
			
			try {
				liftConfig.save(liftFile);
			} catch (IOException e) {				
				e.printStackTrace();
			}
			
			playerSender.sendMessage("Fahrstuhl mit " + stationY.size() + " Stationen gefunden! LiftID=" + liftFile.getName().split(".y")[0]);
			saveLifts();
		}else{
			playerSender.sendMessage(ChatColor.RED + "Etwas stimmt mit den Koordinaten nicht!");
			return;
		}
	}
	
	public void lift(Player playerSender, int liftID, int destination){
		File liftFile = new File(liftPath + liftID + ".yml");
		FileConfiguration liftConfig = YamlConfiguration.loadConfiguration(liftFile);
		
		Location wall1Loc = new Location(Bukkit.getWorld(liftConfig.getString("StarLift.Location.Welt")), liftConfig.getInt("StarLift.Location.Wand1.X"), liftConfig.getInt("StarLift.Location.Wand1.Y"), liftConfig.getInt("StarLift.Location.Wand1.Z"));
		
		int liftX = liftConfig.getInt("StarLift.Lift.Breite");
		int liftZ = liftConfig.getInt("StarLift.Lift.Länge");
		
		new LiftRunnable(this, playerSender, wall1Loc, liftX, liftZ, destination, playerSender.getLocation().getBlockY(), 0, liftID).runTaskTimer(this, 25L, 10L);
		playerSender.playSound(playerSender.getLocation(), Sound.CLICK, 90, 0);
		
		liftStats.put(liftID, destination);
	}
	
	public boolean enterLift(Location playerLoc){
		
	//	for(int i=1;)
		
		return false;
	}
	
	public void saveLifts(){
		
		Object[] lifts = liftStats.keySet().toArray();
		Object[] station = liftStats.values().toArray();
		
		for(int i=0;i<lifts.length;i++){
			File liftFile = new File(liftPath + lifts[i].toString() + ".yml");
			FileConfiguration liftConfig = YamlConfiguration.loadConfiguration(liftFile);
			
			liftConfig.set("StarLift.Lift.Position", station[i].toString());
			
			try {
				liftConfig.save(liftFile);
			} catch (IOException e) {				
				e.printStackTrace();
			}
		}	
	}
	
	public void loadLifts(){
		
		for(int i=0;i<new File(liftPath).listFiles().length;i++){
			File liftFile = new File(liftPath).listFiles()[i];
			FileConfiguration liftConfig = YamlConfiguration.loadConfiguration(liftFile);
			Bukkit.broadcastMessage(liftFile.getName() + " : " + liftConfig.getInt("StarLift.Allgemein.ID") + " " + liftConfig.getInt("StarLift.Lift.Position") + "\n" + liftStats.toString());
			liftStats.put(liftConfig.getInt("StarLift.Allgemein.ID"), liftConfig.getInt("StarLift.Lift.Position"));
		}		
	}
	
	public boolean removeSign(Location signLoc){		
		
		for(int i=1;signConfig.get("signs." + i)!=null;i++){
			if(signConfig.get("signs." + i + ".liftID")!=null){
				if(signLoc.getWorld().getName().equals(signConfig.getString("signs." + i + ".location.world"))){
					if(signLoc.getBlockX()==signConfig.getInt("signs." + i + ".location.X")){
						if(signLoc.getBlockY()==signConfig.getInt("signs." + i + ".location.Y")){
							if(signLoc.getBlockZ()==signConfig.getInt("signs." + i + ".location.Z")){
								signConfig.set("signs." + i, "");		
								
								try {
									signConfig.save(signFile);									
								} catch (IOException e) {
									e.printStackTrace();										
								}	
								return true;
							}
						}
					}
				}								
			}			
		}
		return false;
	}

	public boolean saveSign(Location signLoc, int liftID, int floor){

		int i=1;
		while(signConfig.get("signs." + i)!=null){
			if(signConfig.get("signs." + i + ".liftID")!=null){
				i++;				
			}else{
				break;
			}
		}
	
		signConfig.set("signs." + i + ".liftID", liftID);	
		signConfig.set("signs." + i + ".stockwerk", floor);	
		signConfig.set("signs." + i + ".location.world", signLoc.getWorld().getName());
		signConfig.set("signs." + i + ".location.X", signLoc.getBlockX());
		signConfig.set("signs." + i + ".location.Y", signLoc.getBlockY());
		signConfig.set("signs." + i + ".location.Z", signLoc.getBlockZ());		
		
		try {
			signConfig.save(signFile);
		} catch (IOException e) {
			e.printStackTrace();	
			return false;
		}	
		return true;
	}
}
