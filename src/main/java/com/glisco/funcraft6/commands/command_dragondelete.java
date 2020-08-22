package com.glisco.funcraft6.commands;

import com.glisco.funcraft6.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class command_dragondelete implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {

        Player p;

        if (command.getName().equalsIgnoreCase("dragondelete")) {
            if (!(commandSender instanceof Player)) {
                commandSender.sendMessage("Players only!");
                return true;
            }
            p = (Player) commandSender;
            if (!p.isOp()) {
                p.sendMessage(Main.prefix + "§cInsufficient permissions!");
                return true;
            }
            Bukkit.getWorld("world_the_end").getEnderDragonBattle().getEnderDragon().teleport(new Location(Bukkit.getWorld("world_the_end"), 0, 128, 0));
            return true;
        }
        return false;
    }
}
