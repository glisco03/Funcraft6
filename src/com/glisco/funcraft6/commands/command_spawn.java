package com.glisco.funcraft6.commands;

import com.glisco.funcraft6.Main;
import com.glisco.funcraft6.utils.GlobalVars;
import com.glisco.funcraft6.utils.InsultManager;
import org.bukkit.Bukkit;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class command_spawn implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        Player p;
        if (command.getName().equalsIgnoreCase("spawn")) {
            if (commandSender instanceof Player) {
                p = (Player) commandSender;
                PersistentDataContainer playerData = p.getPersistentDataContainer();
                if(p.getWorld().getName().equalsIgnoreCase("spawn")){
                    if(playerData.has(Main.key("coordinates"), PersistentDataType.INTEGER_ARRAY)){
                        int[] location = playerData.get(Main.key("coordinates"), PersistentDataType.INTEGER_ARRAY);
                        String world = playerData.get(Main.key("world"), PersistentDataType.STRING);
                        Location target = new Location(Bukkit.getWorld(world), location[0], location[1], location[2]);
                        p.teleport(target);
                    } else {
                        p.teleport(Bukkit.getWorld("world").getSpawnLocation());
                    }
                } else {
                    int[] location = new int[3];
                    location[0] = (int) p.getLocation().getX();
                    location[1] = (int) p.getLocation().getY();
                    location[2] = (int) p.getLocation().getZ();
                    playerData.set(Main.key("world"), PersistentDataType.STRING, p.getWorld().getName());
                    playerData.set(Main.key("coordinates"), PersistentDataType.INTEGER_ARRAY, location);
                    p.teleport(GlobalVars.spawn);
                    p.sendMessage(Main.prefix + "§bWelcome at spawn!");
                    p.sendMessage(Main.prefix + "§bUse /spawn again to return to your previous location");
                }
            } else {
                commandSender.sendMessage("§cPlayers only!");
            }
            return true;
        } else {
            return false;
        }
    }
}
