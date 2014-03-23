package me.Aubli.StarLift;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class Lift {
	
	private int liftID;
	private World liftWorld;
	private boolean closeDoor;
	
	private Material liftGround;
	private Material doorMaterial;
	
	private File liftFile;
	private FileConfiguration liftConfig;
	
	private Location wall1Loc;
	private Location wall2Loc;
	
	private int liftHeight;
	private int liftX;
	private int liftZ;
	private int doorHeight;
	private int position;
	private int stations;
	
	private ArrayList<LiftFloor> floors;
	
	public Lift(String liftPath, int liftID, World liftWorld, Location wall1Loc, Location wall2Loc, int liftHeight, int liftX, int liftZ, int doorHeight, int stations, int position, ArrayList<LiftFloor> floors, boolean close, Material ground, Material door){
		
		this.liftID = liftID;
		this.liftWorld = liftWorld;
		this.wall1Loc = wall1Loc.clone();
		this.wall2Loc = wall2Loc.clone();
		
		this.liftHeight = liftHeight;
		this.doorHeight = doorHeight;
		this.liftX = liftX;
		this.liftZ = liftZ;
		this.position = position;
		this.stations = stations;
		
		this.closeDoor = close;
		
		this.floors = floors;
		
		this.liftGround = ground;
		this.doorMaterial = door;
		
		for(int i=0;i<floors.size();i++){
			floors.get(i).setLift(this);
		}		
		
		try {
			liftFile = new File(liftPath);
			liftFile.createNewFile();
			liftConfig = YamlConfiguration.loadConfiguration(liftFile);		
			
			saveLift();
		} catch (IOException e) {			
			e.printStackTrace();
		}
	}
	
	public Lift(File liftFile){
		this.liftFile = liftFile;
		this.liftConfig = YamlConfiguration.loadConfiguration(this.liftFile);
		
		this.liftID = liftConfig.getInt("StarLift.Allgemein.ID");
		
		this.liftHeight = liftConfig.getInt("StarLift.Lift.Höhe");
		this.doorHeight = liftConfig.getInt("StarLift.Lift.Türhöhe");
		
		this.liftX = liftConfig.getInt("StarLift.Lift.Breite");
		this.liftZ = liftConfig.getInt("StarLift.Lift.Länge");
		this.position = liftConfig.getInt("StarLift.Lift.Position");
		this.stations = liftConfig.getInt("StarLift.Lift.Stationen");
		this.closeDoor = liftConfig.getBoolean("StarLift.Lift.Türen");
		
		this.liftGround = Material.valueOf(liftConfig.getString("StarLift.Lift.Boden"));
		this.doorMaterial = Material.valueOf(liftConfig.getString("StarLift.Lift.Tür"));
		
		this.floors = new ArrayList<LiftFloor>();		
		this.liftWorld = Bukkit.getWorld(liftConfig.getString("StarLift.Location.Welt"));
		
		this.wall1Loc = new Location(liftWorld, liftConfig.getInt("StarLift.Location.Wand1.X"), liftConfig.getInt("StarLift.Location.Wand1.Y"), liftConfig.getInt("StarLift.Location.Wand1.Z"));
		this.wall2Loc = new Location(liftWorld, liftConfig.getInt("StarLift.Location.Wand2.X"), liftConfig.getInt("StarLift.Location.Wand2.Y"), liftConfig.getInt("StarLift.Location.Wand2.Z"));
			
		for(int i=1;liftConfig.get("StarLift.Stationen." + i)!=null;i++){				
			floors.add(new LiftFloor(this, liftConfig.getInt("StarLift.Stationen." + i), i));				
		}
	}
	
	void saveLift() throws IOException{	
		
		liftConfig.set("StarLift.Allgemein.ID", liftID);
		liftConfig.set("StarLift.Location.Welt", liftWorld.getName());
		liftConfig.set("StarLift.Location.Wand1.X", wall1Loc.getBlockX());
		liftConfig.set("StarLift.Location.Wand1.Y", wall1Loc.getBlockY());
		liftConfig.set("StarLift.Location.Wand1.Z", wall1Loc.getBlockZ());
		liftConfig.set("StarLift.Location.Wand2.X", wall2Loc.getBlockX());
		liftConfig.set("StarLift.Location.Wand2.Y", wall2Loc.getBlockY());
		liftConfig.set("StarLift.Location.Wand2.Z", wall2Loc.getBlockZ());		
		liftConfig.set("StarLift.Lift.Boden", liftGround.toString());
		liftConfig.set("StarLift.Lift.Tür", doorMaterial.toString());
		liftConfig.set("StarLift.Lift.Türen", closeDoor);
		liftConfig.set("StarLift.Lift.Türhöhe", doorHeight);	
		liftConfig.set("StarLift.Lift.Breite", liftX);
		liftConfig.set("StarLift.Lift.Länge", liftZ);
		liftConfig.set("StarLift.Lift.Höhe", liftHeight);
		
		liftConfig.set("StarLift.Lift.Position", position);
		liftConfig.set("StarLift.Lift.Stationen", stations);
		
		liftConfig.set("StarLift.Stationen", "");
		
		for(int i=0;i<floors.size();i++){				
			liftConfig.set("StarLift.Stationen." + (i+1), floors.get(i).getY());				
		}
		
		liftConfig.save(liftFile);		
	}
	
	public void delete(){
		liftFile.delete();
	}
	
	public boolean closeDoor(){
		return closeDoor;
	}
	
	public int getLiftID(){
		return liftID;
	}
	
	public int getLiftPosition(){
		return position;
	}
	
	public int getLiftX(){
		return liftX;
	}
	
	public int getLiftZ(){
		return liftZ;
	}
	
	public int getHeight(){
		return liftHeight;
	}
	
	public int getDoorHeight(){
		return doorHeight;
	}
	
	public ArrayList<LiftFloor> getStations(){
		return floors;
	}
	
	public World getWorld(){
		return liftWorld;
	}
	
	public Location getWall1Loc(){
		return wall1Loc.clone();
	}
	
	public Location getWall2Loc(){
		return wall2Loc.clone();
	}
	
	public Material getDoorMaterial(){
		return doorMaterial;
	}
	
	public Material getGroundMaterial(){
		return liftGround;
	}
	
	public int getY(int floor){
		for(int i=0;i<floors.size();i++){			
			if(floors.get(i).getFloor()==floor){
				return floors.get(i).getY();
			}
		}
		return -1;
	}
	
	public int getFloor(int Y){
		for(int i=0;i<floors.size();i++){			
			if(floors.get(i).getY()==Y){
				return floors.get(i).getFloor();
			}
		}
		return -1;
	}
	
	public void setPosition(int position){
		this.position = position;
	}
	
	
	@Override
	public String toString(){
		return "LIFT=[" + liftID + ", " + liftX + "x" + liftZ + "] Y=" + position + " [" + wall1Loc.getBlockX() + ":" + wall1Loc.getBlockY() + ":" + wall1Loc.getBlockZ() + "][" + wall2Loc.getBlockX() + ":" + wall2Loc.getBlockY() + ":" + wall2Loc.getBlockZ() + "]";  
	}
}
