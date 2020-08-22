package com.glisco.funcraft6.commands;

import com.glisco.funcraft6.Main;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class command_invsee implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {

        Player p;

        if (command.getName().equalsIgnoreCase("invsee")) {
            if (!(commandSender instanceof Player)) {
                commandSender.sendMessage("Players only!");
                return true;
            }
            p = (Player) commandSender;
            if (!p.isOp()) {
                p.sendMessage(Main.prefix + "§cInsufficient permissions!");
                return true;
            }
            if (args.length < 1) {
                commandSender.sendMessage("Missing arguments!");
                return true;
            }
            if (Bukkit.getPlayer(args[0]) == null) {
                commandSender.sendMessage("Not a valid player!");
                return true;
            }
            p.openInventory(Bukkit.getPlayer(args[0]).getInventory());
            return true;
        }
        return false;
    }
}
