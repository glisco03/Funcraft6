package com.glisco.funcraft6.commands;

import com.glisco.funcraft6.miniblocks.*;
import org.bukkit.Location;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class command_miniblocks implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        Player p;
        if (command.getName().equalsIgnoreCase("miniblocks")) {
            if (commandSender instanceof Player) {
                p = (Player) commandSender;
                if (p.isOp()) {
                    if (args.length > 0) {
                        switch (args[0]) {
                            case "nature":
                                MiniInventoryHelper.openMerchantInventory(p, Nature.class);
                                break;
                            case "ores":
                                MiniInventoryHelper.openMerchantInventory(p, Ore.class);
                                break;
                            case "stone":
                                MiniInventoryHelper.openMerchantInventory(p, Stone.class);
                                break;
                            case "nether":
                                MiniInventoryHelper.openMerchantInventory(p, Nether.class);
                                break;
                            case "deco1":
                                MiniInventoryHelper.openMerchantInventory(p, Decoration_1.class);
                                break;
                            case "deco2":
                                MiniInventoryHelper.openMerchantInventory(p, Decoration_2.class);
                                break;
                            default:
                                p.sendMessage("Wrong arguments!");

                        }
                    } else {
                        p.sendMessage("Missing arguments!");
                    }
                    return true;
                } else {
                    p.sendMessage("Insufficient Permissions");
                    return true;
                }
            } else if (commandSender instanceof BlockCommandSender) {
                BlockCommandSender block = (BlockCommandSender) commandSender;
                if (args.length > 0) {
                    p = getNearestPlayer(block.getBlock().getLocation());
                    switch (args[0]) {
                        case "nature":
                            MiniInventoryHelper.openMerchantInventory(p, Nature.class);
                            break;
                        case "ores":
                            MiniInventoryHelper.openMerchantInventory(p, Ore.class);
                            break;
                        case "stone":
                            MiniInventoryHelper.openMerchantInventory(p, Stone.class);
                            break;
                        case "nether":
                            MiniInventoryHelper.openMerchantInventory(p, Nether.class);
                            break;
                        case "deco1":
                            MiniInventoryHelper.openMerchantInventory(p, Decoration_1.class);
                            break;
                        case "deco2":
                            MiniInventoryHelper.openMerchantInventory(p, Decoration_2.class);
                            break;
                        default:
                            p.sendMessage("Wrong arguments!");

                    }
                } else {
                    block.sendMessage("Missing arguments!");
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

    public Player getNearestPlayer(Location loc) {
        Player nearest = null;
        double range = 4;
        double lastDistance = 100;
        for (Entity e : loc.getWorld().getNearbyEntities(loc, range, range, range)) {
            if (e instanceof Player) {
                Player p = (Player) e;
                double distance = p.getLocation().distance(loc);
                if (distance < lastDistance) {
                    lastDistance = distance;
                    nearest = p;
                }
            }
        }
        return nearest;
    }
}
