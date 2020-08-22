package com.glisco.funcraft6.commands;

import com.glisco.funcraft6.Main;
import com.glisco.funcraft6.items.ItemHelper;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class command_isneak implements CommandExecutor, TabCompleter {

    private final List<String> args0;
    private final List<String> args1;

    public command_isneak() {
        args0 = new ArrayList<>();
        args0.add("toggle");
        args0.add("whitelist");

        args1 = new ArrayList<>();
        args1.add("add");
        args1.add("remove");
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        Player p;
        if (command.getName().equalsIgnoreCase("isneak")) {
            if (commandSender instanceof Player) {
                p = (Player) commandSender;
                if (args.length < 1) {
                    p.sendMessage(Main.prefix + "§cMissing arguments!");
                    return true;
                } else if (args[0].equalsIgnoreCase("toggle")) {
                    PersistentDataContainer playerData = p.getPersistentDataContainer();
                    String sneakingEnabled = playerData.get(Main.key("itemsneaking"), PersistentDataType.STRING);
                    if (sneakingEnabled == null) {
                        playerData.set(Main.key("itemsneaking"), PersistentDataType.STRING, "false");
                        p.sendMessage(Main.prefix + "§aYou no longer need to sneak to pick up items!");
                        return true;
                    } else {
                        if (sneakingEnabled.equalsIgnoreCase("true")) {
                            playerData.set(Main.key("itemsneaking"), PersistentDataType.STRING, "false");
                            p.sendMessage(Main.prefix + "§aYou no longer need to sneak to pick up items!");
                        } else {
                            playerData.set(Main.key("itemsneaking"), PersistentDataType.STRING, "true");
                            p.sendMessage(Main.prefix + "§aYou now need to sneak to pick up items!");
                        }
                    }
                } else if (args[0].equalsIgnoreCase("whitelist")) {
                    if (args.length < 2) {
                        p.sendMessage(Main.prefix + "§cMissing arguments!");
                    } else if (args[1].equalsIgnoreCase("add")) {
                        List<Material> pickupList = new ArrayList<>();
                        if (p.hasMetadata("pickuplist")) {
                            pickupList = (List<Material>) p.getMetadata("pickuplist").get(0).value();
                        }
                        if (ItemHelper.doItemSanityChecks(p.getInventory().getItemInMainHand(), false, false)) {
                            pickupList.add(p.getInventory().getItemInMainHand().getType());
                        } else {
                            p.sendMessage(Main.prefix + "§cYou're not holding an item!");
                            return true;
                        }
                        MetadataValue pickupListValue = new FixedMetadataValue(Main.p, pickupList);
                        p.setMetadata("pickuplist", pickupListValue);
                        p.sendMessage(Main.prefix + "§aItem added to whitelist");
                    } else if (args[1].equalsIgnoreCase("remove")) {
                        List<Material> pickupList = new ArrayList<>();
                        if (p.hasMetadata("pickuplist")) {
                            pickupList = (List<Material>) p.getMetadata("pickuplist").get(0).value();
                        }
                        if (ItemHelper.doItemSanityChecks(p.getInventory().getItemInMainHand(), false, false) && pickupList.contains(p.getInventory().getItemInMainHand().getType())) {
                            pickupList.remove(p.getInventory().getItemInMainHand().getType());
                        } else {
                            p.sendMessage(Main.prefix + "§cYou're not holding an item!");
                            return true;
                        }
                        MetadataValue pickupListValue = new FixedMetadataValue(Main.p, pickupList);
                        p.setMetadata("pickuplist", pickupListValue);
                        p.sendMessage(Main.prefix + "§aItem removed from whitelist");
                    } else {
                        p.sendMessage(Main.prefix + "§cInvalid arguments!");
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

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length > 0) {
            if (args.length == 1) {
                List<String> matchingItems = new ArrayList<>();
                for (String s : args0) {
                    if (s.startsWith(args[0])) {
                        matchingItems.add(s);
                    }
                }
                return matchingItems;
            } else if (args.length == 2) {
                List<String> matchingItems = new ArrayList<>();
                for (String s : args1) {
                    if (s.startsWith(args[0])) {
                        matchingItems.add(s);
                    }
                }
                if (matchingItems.isEmpty()) {
                    return args1;
                }
                return matchingItems;
            } else {
                return new ArrayList<>();
            }
        } else {
            return args0;
        }
    }
}
