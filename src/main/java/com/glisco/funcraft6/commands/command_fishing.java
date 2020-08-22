package com.glisco.funcraft6.commands;

import com.glisco.funcraft6.Main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class command_fishing implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {

        Player p;

        if (command.getName().equalsIgnoreCase("fishing")) {
            if (!(commandSender instanceof Player)) {
                commandSender.sendMessage("Players only!");
                return true;
            }
            p = (Player) commandSender;
            if (!p.getDisplayName().contains("Fishing")) {
                p.sendMessage(Main.prefix + "§2You are now marked as fishing!");
                p.setDisplayName("§7[§2Fishing§7]§r" + p.getName());
                p.setPlayerListName("§7[§2Fishing§7]§r" + p.getName());
            } else {
                p.sendMessage(Main.prefix + "§2You are no longer marked as fishing!");
                p.setDisplayName(p.getName());
                p.setPlayerListName(p.getName());
            }
            return true;
        }
        return false;
    }
}
