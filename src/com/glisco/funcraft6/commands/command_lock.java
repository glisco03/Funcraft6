package com.glisco.funcraft6.commands;

import com.glisco.funcraft6.Main;
import com.glisco.funcraft6.utils.GlobalVars;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class command_lock implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        Player p;
        if (command.getName().equalsIgnoreCase("lock")) {
            if (commandSender instanceof Player) {
                p = (Player) commandSender;
                if (!GlobalVars.signLocker.contains(p)) {
                    GlobalVars.signLocker.add(p);
                }
                p.sendMessage(Main.prefix + "§bNow rightclick the sign you want to lock!");
            } else {
                commandSender.sendMessage("§cPlayers only!");
            }
            return true;
        } else {
            return false;
        }
    }
}
