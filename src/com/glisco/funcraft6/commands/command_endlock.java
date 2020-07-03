package com.glisco.funcraft6.commands;

import com.glisco.funcraft6.Main;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;

public class command_endlock implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {

        Player p;
        File configFile = new File("plugins/Funcraft6/config.yml");

        if (command.getName().equalsIgnoreCase("endlock")) {
            if (args.length < 1) {
                commandSender.sendMessage("Missing argument!");
                return true;
            } else {
                if (args[0].equalsIgnoreCase("enable")) {
                    commandSender.sendMessage("End disabled!");
                    Main.config.set("endlock", true);
                    try {
                        Main.config.save(configFile);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return true;
                } else if (args[0].equalsIgnoreCase("disable")) {
                    commandSender.sendMessage("End enabled!");
                    Bukkit.broadcastMessage(Main.prefix + "§2§lThe §5§lEND §2§lhas been enabled!");
                    Main.config.set("endlock", false);
                    try {
                        Main.config.save(configFile);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return true;
                } else {
                    commandSender.sendMessage("Wrong arguments!");
                    return true;
                }
            }
        }
        return false;
    }
}
