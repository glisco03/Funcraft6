package com.glisco.funcraft6.commands;

import com.glisco.funcraft6.Main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class command_itemsneaktoggle implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        Player p;
        if (command.getName().equalsIgnoreCase("itemsneaktoggle")) {
            if (commandSender instanceof Player) {
                p = (Player) commandSender;
                PersistentDataContainer playerData = p.getPersistentDataContainer();
                String sneakingEnabled = playerData.get(Main.key("itemsneaking"), PersistentDataType.STRING);
                if(sneakingEnabled == null){
                    playerData.set(Main.key("itemsneaking"), PersistentDataType.STRING, "false");
                    p.sendMessage(Main.prefix + "§aYou no longer need to sneak to pick up items!");
                    return true;
                } else {
                    if(sneakingEnabled.equalsIgnoreCase("true")){
                        playerData.set(Main.key("itemsneaking"), PersistentDataType.STRING, "false");
                        p.sendMessage(Main.prefix + "§aYou no longer need to sneak to pick up items!");
                    } else {
                        playerData.set(Main.key("itemsneaking"), PersistentDataType.STRING, "true");
                        p.sendMessage(Main.prefix + "§aYou now need to sneak to pick up items!");
                    }
                }
                return true;
            } else {
                commandSender.sendMessage("§cPlayers only!");
            }
            return true;
        }
        return false;

    }
}
