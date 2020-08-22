package com.glisco.funcraft6.commands;

import com.glisco.funcraft6.items.FuncraftItems;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class command_getitem implements CommandExecutor, TabCompleter {

    Class<FuncraftItems> itemsClass;
    List<String> funcraftItems;

    public command_getitem() {
        itemsClass = FuncraftItems.class;
        funcraftItems = new ArrayList<>();
        for (Field f : itemsClass.getFields()) {
            funcraftItems.add(f.getName());
        }
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        Player p;
        if (command.getName().equalsIgnoreCase("getitem")) {
            if (commandSender instanceof Player) {
                p = (Player) commandSender;
                if (p.isOp()) {
                    if (args.length > 0) {
                        Integer amount = null;
                        if (args.length > 1) {
                            amount = Integer.parseInt(args[1]);
                        }
                        try {
                            Field f = itemsClass.getField(args[0]);
                            ItemStack toAdd = (ItemStack) f.get(itemsClass);
                            toAdd = toAdd.clone();
                            if (amount != null) {
                                toAdd.setAmount(amount);
                            }
                            p.getInventory().addItem(toAdd);
                        } catch (NoSuchFieldException | IllegalAccessException e) {
                            p.sendMessage("Invalid Arguments!");
                        }
                    } else {
                        p.sendMessage("Missing arguments!");
                    }
                } else {
                    p.sendMessage("Insufficient Permissions");
                }
                return true;
            } else {
                commandSender.sendMessage("§4Players only!");
                return true;
            }
        } else {
            return false;
        }
    }

    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length > 0) {
            List<String> matchingItems = new ArrayList<>();
            for (String s : funcraftItems) {
                if (s.startsWith(args[0])) {
                    matchingItems.add(s);
                }
            }
            return matchingItems;
        } else {
            return funcraftItems;
        }
    }

}
