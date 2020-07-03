package com.glisco.funcraft6.commands;

import com.glisco.funcraft6.Main;
import com.glisco.funcraft6.utils.GlobalVars;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class command_freeze implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {

        Player p = null;

        if (command.getName().equalsIgnoreCase("freeze")) {
            if(commandSender instanceof Player){
                p = (Player) commandSender;
            }
            if(p != null){
                if(!p.isOp()){
                    p.sendMessage(Main.prefix + "§cInsufficient permissions!");
                    return true;
                }
            }
            if(args.length < 1){
                commandSender.sendMessage(Main.prefix + "§cMissing arguments!");
                return true;
            }
            Player target = Bukkit.getPlayer(args[0]);
            if(target == null){
                commandSender.sendMessage(Main.prefix + "§cNot a player!");
            }
            if(GlobalVars.freezedPlayers.contains(target)){
                GlobalVars.freezedPlayers.remove(target);
                commandSender.sendMessage(Main.prefix + "§aUn-Froze " + target.getName());
            } else {
                GlobalVars.freezedPlayers.add(target);
                commandSender.sendMessage(Main.prefix + "§Froze " + target.getName());
            }
            return true;
        }
        return false;
    }
}
