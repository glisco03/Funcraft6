package com.glisco.funcraft6.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class command_diminfo implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {

        Player p;

        if (command.getName().equalsIgnoreCase("diminfo")) {
            if (!(commandSender instanceof Player)) {
                commandSender.sendMessage("Players only!");
                return true;
            }
            p = (Player) commandSender;
            p.sendMessage(p.getWorld().getName());
            return true;
        }
        return false;
    }
}
