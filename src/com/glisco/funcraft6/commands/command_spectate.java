package com.glisco.funcraft6.commands;

import com.glisco.funcraft6.Main;
import com.glisco.funcraft6.utils.GlobalVars;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class command_spectate implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        Player p;
        if(command.getName().equalsIgnoreCase("spectate")){
            if(commandSender instanceof Player){
                p = (Player) commandSender;
                if(!GlobalVars.specators.containsKey(p)){
                    GlobalVars.specators.put(p, p.getLocation());
                    p.sendMessage(Main.prefix + "§aYou are now spectating!");
                    p.setGameMode(GameMode.SPECTATOR);
                } else{
                    p.sendMessage(Main.prefix + "§aYou are no longer spectating!");
                    p.setGameMode(GameMode.SURVIVAL);
                    p.teleport(GlobalVars.specators.get(p));
                    GlobalVars.specators.remove(p);
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
