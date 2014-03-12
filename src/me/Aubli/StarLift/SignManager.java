package me.Aubli.StarLift;

import java.io.File;
import java.io.IOException;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class SignManager {
	
	private File signFile;
	private FileConfiguration signConfig;
	
	private static SignManager instance;
	
	public SignManager(){
		instance = this;
		signFile = new File(StarLift.getInstance().getDataFolder().getPath() + "/signs.yml");
		signConfig = YamlConfiguration.loadConfiguration(signFile);
	}
	
	public static SignManager getManager(){
		return instance;
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
