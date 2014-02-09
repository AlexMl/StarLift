package me.Aubli.StarLift;

import java.io.File;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StarLiftCommands implements CommandExecutor{
	public StarLiftCommands(StarLift plugin){
		this.plugin = plugin;
	}
	
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String cmdLabel, String[] args) {
		
		if(!(sender instanceof Player)){
			sender.sendMessage("Only Players can use this command");
			return true;
		}
		
		Player playerSender = (Player) sender;
		
		if(cmd.getName().equalsIgnoreCase("sl")){
			if(args.length==0){
				printCommands(playerSender);
				return true;
			}
			
			if(args.length==1){
				if(args[0].equalsIgnoreCase("tool")){
					if(playerSender.hasPermission("sl.tool")){
						playerSender.getInventory().addItem(plugin.tool);					
						playerSender.sendMessage("Markier den Fahrstuhl mit links- und rechtsclick!");
						return true;
					}else{
						playerSender.sendMessage(ChatColor.DARK_RED + "You don't have permissions for that command!");
						return true;
					}
				}else{
					printCommands(playerSender);
					return true;
				}
			}
			
			if(args.length==2){
				if(args[0].equalsIgnoreCase("remove")){
					if(playerSender.hasPermission("sl.remove")){
						File liftFile = new File(plugin.liftPath + args[1] + ".yml");
						
						if(liftFile.exists()){
							liftFile.delete();
							plugin.liftStats.remove(Integer.parseInt(args[1]));
							plugin.saveLifts();
						}else{
							playerSender.sendMessage(plugin.messagePrefix + ChatColor.RED + "Der Lift " + args[1] + " existiert nicht!");
							return true;
						}
					}else{
						playerSender.sendMessage(ChatColor.DARK_RED + "You don't have permissions for that command!");
						return true;
					}
				}else{
					printCommands(playerSender);
					return true;
				}
			}
			
			if(args.length==3){
				if(args[0].equalsIgnoreCase("lift")){
					plugin.lift(playerSender, Integer.parseInt(args[1]), Integer.parseInt(args[2]));
					return true;
				}else{
					printCommands(playerSender);
					return true;
				}
			}
			
		}
		
		return true;
	}
	
	private void printCommands(Player playerSender){
		
		if(playerSender.hasPermission("sl")){
			String pluginVersion = plugin.getDescription().getVersion();
			String pluginName = plugin.getDescription().getName();
			
			playerSender.sendMessage(ChatColor.YELLOW + "|---------- " + pluginName + " v" + pluginVersion + " ----------|");
			playerSender.sendMessage(ChatColor.YELLOW + "|" + ChatColor.DARK_GRAY + " sl tool");
			playerSender.sendMessage(ChatColor.YELLOW + "|" + ChatColor.DARK_GRAY + " sl remove [LiftID]");
			playerSender.sendMessage(ChatColor.YELLOW + "|" + ChatColor.DARK_GRAY + " sl list");
		}else{
			playerSender.sendMessage(ChatColor.DARK_RED + "You don't have permissions for that command!");
			return;
		}
	}
	
	private StarLift plugin;
}
