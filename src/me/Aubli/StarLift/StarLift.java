package me.Aubli.StarLift;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import me.Aubli.StarLift.Events.BlockBreakListener;
import me.Aubli.StarLift.Events.InventoryClickListener;
import me.Aubli.StarLift.Events.PlayerInteractListener;
import me.Aubli.StarLift.Events.PlayerMoveListener;
import me.Aubli.StarLift.Events.SignChangeListener;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class StarLift extends JavaPlugin{
	
	public final Logger logger = Bukkit.getLogger();
	public static StarLift instance;
	
	public String messagePrefix = ChatColor.DARK_GRAY + "[" + ChatColor.GOLD + "StarLift" + ChatColor.DARK_GRAY + "] " + ChatColor.RESET;
	
	public boolean enable = false;
	
	public ItemStack tool;
	
	@Override
	public void onDisable(){
		clearToolItem(null);		
		Bukkit.getScheduler().cancelAllTasks();
		LiftManager.getManager().saveLifts();
		
		logger.info("[StarLift] Plugin is disabled!");
	}
	
	@Override
	public void onEnable(){
		
		loadConfig();		
		instance = this;
		
		new LiftManager();
		new SignManager();
		
		registerCommands();
		registerListeners();			
		
		if(enable==false){
			Bukkit.getPluginManager().disablePlugin(this);
		}else{		
			logger.info("[StarLift] Plugin is enabled!");
		}
	}
	
	public static StarLift getInstance(){
		return instance;
	}
	
	private void registerListeners(){
		PluginManager pm = Bukkit.getPluginManager();
		
		pm.registerEvents(new PlayerInteractListener(this), this);
		pm.registerEvents(new BlockBreakListener(this), this);
		pm.registerEvents(new SignChangeListener(this), this);
		pm.registerEvents(new PlayerMoveListener(), this);
		pm.registerEvents(new InventoryClickListener(), this);
	}
	
	private void registerCommands(){
		this.getCommand("sl").setExecutor(new StarLiftCommands(this));
	}
		
	private void loadConfig(){
		
		File liftFolder = new File(getDataFolder().getPath() + "/elevators/");
		File signFile = new File(getDataFolder().getPath() + "/signs.yml");
		
		if(!liftFolder.exists() || !signFile.exists()){
			try{
				liftFolder.mkdirs();
				signFile.createNewFile();
			}catch(IOException e){
				e.printStackTrace();
			}
		}
		
		this.getConfig().addDefault("config.general.enable", true);
		
		this.getConfig().addDefault("config.lift.material.ground", Material.GLASS.name());
		this.getConfig().addDefault("config.lift.material.door", Material.FENCE.name());
		
		this.getConfig().options().copyDefaults(true);
		this.saveConfig();
	
		enable = this.getConfig().getBoolean("config.general.enable");
		
		tool = new ItemStack(Material.STICK);		
		ItemMeta toolMeta = tool.getItemMeta();		
		toolMeta.setDisplayName(messagePrefix + "Tool");	
		toolMeta.addEnchant(Enchantment.DURABILITY, 5, true);
		tool.setItemMeta(toolMeta);				
	}
	
	public Material getDefaultDoor(){
		return Material.valueOf(this.getConfig().getString("config.lift.material.door"));
	}
	
	public Material getDefaultGround(){
		return Material.valueOf(this.getConfig().getString("config.lift.material.ground"));
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
		
}
