package com.glisco.funcraft6.commands;

import com.glisco.funcraft6.Main;
import com.glisco.funcraft6.utils.GlobalVars;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class command_public implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        Player p;
        if(command.getName().equalsIgnoreCase("public")){
            if(commandSender instanceof Player){
                p = (Player) commandSender;
                if(!GlobalVars.signPublishers.contains(p)){
                    GlobalVars.signPublishers.add(p);
                }
                p.sendMessage(Main.prefix + "§bNow rightclick the sign you want to make public!");
            } else {
                commandSender.sendMessage("§cPlayers only!");
            }
            return true;
        } else {
            return false;
        }
    }
}
