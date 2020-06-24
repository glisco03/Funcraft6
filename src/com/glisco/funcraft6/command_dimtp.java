package com.glisco.funcraft6;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class command_dimtp implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {

        Player p;

        if (command.getName().equalsIgnoreCase("dimtp")) {
            if (!(commandSender instanceof Player)) {
                commandSender.sendMessage("Players only!");
                return true;
            }
            p = (Player) commandSender;
            if(args.length < 1){
                commandSender.sendMessage("Missing arguments!");
                return true;
            }
            if(Bukkit.getWorld(args[0]) == null){
                commandSender.sendMessage("Not a valid dimension!");
                return true;
            }
            Location target = p.getLocation();
            target.setWorld(Bukkit.getWorld(args[0]));
            p.teleport(target);
            return true;
        }
        return false;
    }
}
