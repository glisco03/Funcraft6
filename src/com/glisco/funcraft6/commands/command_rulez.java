package com.glisco.funcraft6.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class command_rulez implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {

        Player p;
        BufferedReader rulesFile = null;
        try {
            rulesFile = new BufferedReader(new FileReader("plugins/Funcraft6/rules.txt"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        if (command.getName().equalsIgnoreCase("rulez")) {
            for (Object line : rulesFile.lines().toArray()) {
                commandSender.sendMessage((String)line);
            }
            return true;
        }
        return false;
    }
}
