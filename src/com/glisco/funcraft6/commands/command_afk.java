package com.glisco.funcraft6.commands;

import com.glisco.funcraft6.Main;
import com.sun.istack.internal.NotNull;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class command_afk implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {

        Player p;

        if (command.getName().equalsIgnoreCase("afk")) {
            if (!(commandSender instanceof Player)) {
                commandSender.sendMessage("Players only!");
                return true;
            }
            p = (Player) commandSender;
            if (!p.getDisplayName().contains("AFK")) {
                p.sendMessage(Main.prefix + "§2You are now marked as AFK!");
                p.setDisplayName("§7[§4AFK§7]§r" + p.getName());
                p.setPlayerListName("§7[§4AFK§7]§r" + p.getName());
            } else {
                p.sendMessage(Main.prefix + "§2You are no longer marked as AFK!");
                p.setDisplayName(p.getName());
                p.setPlayerListName(p.getName());
            }
            return true;
        }
        return false;
    }
}
