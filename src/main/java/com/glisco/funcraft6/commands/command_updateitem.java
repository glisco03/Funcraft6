package com.glisco.funcraft6.commands;

import com.glisco.funcraft6.Main;
import com.glisco.funcraft6.items.ItemFactory;
import com.glisco.funcraft6.items.ItemHelper;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class command_updateitem implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        Player p;
        if (command.getName().equalsIgnoreCase("updateitem")) {
            if (commandSender instanceof Player) {
                p = (Player) commandSender;
                if (ItemHelper.doItemSanityChecks(p.getInventory().getItemInMainHand(), true, false)) {
                    ItemStack item = p.getInventory().getItemInMainHand();
                    String name = item.getItemMeta().getDisplayName();
                    switch (name) {
                        case "§bRecall Potion":
                            ItemFactory.setCustomItemID(item, "recall_potion");
                            break;
                        case "§7Unbound Warp Potion":
                            ItemFactory.setCustomItemID(item, "unbound_warp_potion");
                            break;
                        case "§aPotion of Acceptance":
                            ItemFactory.setCustomItemID(item, "acceptance_potion");
                            break;
                        case "§fCoffee":
                            ItemFactory.setCustomItemID(item, "coffee");
                            break;
                        case "§dDraconic Wings":
                            ItemFactory.setCustomItemID(item, "dragon_wings");
                            break;
                        case "§eXP Tome":
                            item.setType(Material.CARROT_ON_A_STICK);
                            ItemFactory.setCustomItemID(item, "xptome");
                            ItemFactory.setModelData(item, 1);
                            break;
                        case "§8Pickaxe of the Excavator":
                            ItemFactory.setCustomItemID(item, "excavator_hammer");
                            break;
                        case "§bHardening Crystal":
                            ItemFactory.setCustomItemID(item, "hardening_crystal");
                            break;
                        case "§5Ender Queen Scale":
                            ItemFactory.setModelData(item, 1);
                            break;
                    }
                    p.sendMessage(Main.prefix + "§aItem updated!");
                } else {
                    p.sendMessage(Main.prefix + "§cYou're not holding an item of type FUNCRAFT_CUSTOM");
                }
                return true;
            } else {
                commandSender.sendMessage("§cPlayers only!");
            }
            return true;
        } else {
            return false;
        }
    }
}
