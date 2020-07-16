package com.glisco.funcraft6.commands;

import com.glisco.funcraft6.utils.FuncraftItems;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Field;

public class command_getitem implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        Player p;
        if (command.getName().equalsIgnoreCase("getitem")) {
            if (commandSender instanceof Player) {
                p = (Player) commandSender;
                if (p.isOp()) {
                    if (args.length > 0) {
                        Class<FuncraftItems> itemsClass = FuncraftItems.class;
                        try {
                            Field f = itemsClass.getField(args[0]);
                            ItemStack toAdd = (ItemStack) f.get(itemsClass);
                            p.getInventory().addItem(toAdd);
                        } catch (NoSuchFieldException | IllegalAccessException e) {
                            p.sendMessage("Invalid Arguments!");
                        }
                    } else {
                        p.sendMessage("Missing arguments!");
                    }
                    return true;
                } else {
                    p.sendMessage("Insufficient Permissions");
                    return true;
                }
            } else {
                commandSender.sendMessage("§4Players only!");
                return true;
            }
        } else {
            return false;
        }
    }
}
