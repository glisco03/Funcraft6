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
                            ItemFactory.addCustomItemID(item, "recall_potion");
                            break;
                        case "§7Unbound Warp Potion":
                            ItemFactory.addCustomItemID(item, "unbound_warp_potion");
                            break;
                        case "§aPotion of Acceptance":
                            ItemFactory.addCustomItemID(item, "acceptance_potion");
                            break;
                        case "§fCoffee":
                            ItemFactory.addCustomItemID(item, "coffee");
                            break;
                        case "§dDraconic Wings":
                            ItemFactory.addCustomItemID(item, "dragon_wings");
                            break;
                        case "§eXP Tome":
                            item.setType(Material.KNOWLEDGE_BOOK);
                            ItemFactory.addCustomItemID(item, "xptome");
                            break;
                        case "§8Pickaxe of the Excavator":
                            ItemFactory.addCustomItemID(item, "excavator_pickaxe");
                            break;
                        case "§bHardening Crystal":
                            ItemFactory.addCustomItemID(item, "hardening_crystal");
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
