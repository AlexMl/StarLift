package me.Aubli;

import me.Aubli.Events.PlayerInteractListener;

import org.bukkit.ChatColor;
import org.bukkit.Material;
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
				}else if(args[0].equalsIgnoreCase("list")){
					if(playerSender.hasPermission("sl.list")){
						String pluginVersion = plugin.getDescription().getVersion();
						String pluginName = plugin.getDescription().getName();
					
						playerSender.sendMessage(ChatColor.YELLOW + "|---------- " + pluginName + " v" + pluginVersion + " Lifts ----------|");
					
						for(int i=0;i<LiftManager.getManager().getLifts().length;i++){
							Lift lift = LiftManager.getManager().getLifts()[i];
							playerSender.sendMessage(ChatColor.YELLOW + "| ID:" + ChatColor.DARK_GRAY + lift.getLiftID() + ChatColor.YELLOW + "  Höhe:" + ChatColor.DARK_GRAY + lift.getHeight() + ChatColor.YELLOW + "  Türen schließen:" + ChatColor.DARK_GRAY + lift.closeDoor());
						}
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
						boolean success = LiftManager.getManager().removeLift(Integer.parseInt(args[1]));
						
						if(success==true){
							playerSender.sendMessage(plugin.messagePrefix + ChatColor.GREEN + "Der Lift " + args[1] + " wurde entfernt!");
							return true;
						}else{
							playerSender.sendMessage(plugin.messagePrefix + ChatColor.RED + "Der Lift " + args[1] + " existiert nicht!");
							return true;
						}
					}else{
						playerSender.sendMessage(ChatColor.DARK_RED + "You don't have permissions for that command!");
						return true;
					}
				}else if(args[0].equalsIgnoreCase("add")){					
					if(playerSender.hasPermission("sl.add")){
						if(args[1].equalsIgnoreCase("true") || args[1].equalsIgnoreCase("false")){
							boolean close = Boolean.parseBoolean(args[1]);
							PlayerInteractListener.getInstance().setBoolean(close);
								
							playerSender.getInventory().addItem(plugin.tool);					
							playerSender.sendMessage("Markier den Fahrstuhl mit links- und rechtsclick!");
							return true;
						}else{
							playerSender.sendMessage(ChatColor.RED + "Eines der Argumente ist falsch!");
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
					LiftManager.getManager().lift(playerSender, Integer.parseInt(args[1]), Integer.parseInt(args[2]));
					return true;
				}else{
					printCommands(playerSender);
					return true;
				}
			}
			
			if(args.length==4){
				if(args[0].equalsIgnoreCase("add")){
					if(playerSender.hasPermission("sl.add")){
						if(args[1].equalsIgnoreCase("true") || args[1].equalsIgnoreCase("false")){
							boolean close = Boolean.parseBoolean(args[1]);
							String door = args[2];
							String ground = args[3];
							
							try{
								Material doorMaterial = Material.valueOf(door);
								Material groundMaterial = Material.valueOf(ground);
								
								PlayerInteractListener.getInstance().setMaterial(doorMaterial, groundMaterial);
								PlayerInteractListener.getInstance().setBoolean(close);
								
								playerSender.getInventory().addItem(plugin.tool);					
								playerSender.sendMessage("Markier den Fahrstuhl mit links- und rechtsclick!");
								return true;
							}catch(IllegalArgumentException e){
								playerSender.sendMessage(ChatColor.RED + "Die Materialien existieren nicht!");
								return true;
							}							
						}else{
							playerSender.sendMessage(ChatColor.RED + "Eines der Argumente ist falsch!");
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
			
		}
		
		return true;
	}
	
	private void printCommands(Player playerSender){
		
		if(playerSender.hasPermission("sl")){
			String pluginVersion = plugin.getDescription().getVersion();
			String pluginName = plugin.getDescription().getName();
			
			playerSender.sendMessage(ChatColor.YELLOW + "|---------- " + pluginName + " v" + pluginVersion + " ----------|");
			playerSender.sendMessage(ChatColor.YELLOW + "|" + ChatColor.DARK_GRAY + " sl tool");
			playerSender.sendMessage(ChatColor.YELLOW + "|" + ChatColor.DARK_GRAY + " sl add [closeDoors] [DoorMaterial] [GroundMaterial]");
			playerSender.sendMessage(ChatColor.YELLOW + "|" + ChatColor.DARK_GRAY + " sl add [closeDoors]");
			playerSender.sendMessage(ChatColor.YELLOW + "|" + ChatColor.DARK_GRAY + " sl remove [LiftID]");
			playerSender.sendMessage(ChatColor.YELLOW + "|" + ChatColor.DARK_GRAY + " sl list");
		}else{
			playerSender.sendMessage(ChatColor.DARK_RED + "You don't have permissions for that command!");
			return;
		}
	}
	
	private StarLift plugin;
}
