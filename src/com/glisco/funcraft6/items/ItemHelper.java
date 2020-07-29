package com.glisco.funcraft6.items;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ItemHelper {

    public static boolean doItemSanityChecks(ItemStack item, boolean checkForDisplayName, boolean checkForMeta) {
        if (item == null) return false;
        if (item.getType().equals(Material.AIR)) return false;

        if (checkForDisplayName) {
            if (!item.hasItemMeta()) return false;
            if (!item.getItemMeta().hasDisplayName()) return false;
        }

        if (checkForMeta) {
            if (!item.hasItemMeta()) return false;
        }

        return true;
    }

    public static boolean checkForItemStack(ItemStack toCompare, ItemStack item) {
        if (!doItemSanityChecks(toCompare, false, false)) return false;

        return toCompare.equals(item);
    }

    public static boolean checkForMaterial(ItemStack item, Material material) {
        if (!doItemSanityChecks(item, false, false)) return false;

        return item.getType().equals(material);
    }

    public static boolean checkForDisplayName(ItemStack item, String name) {
        if (!doItemSanityChecks(item, true, false)) return false;

        return item.getItemMeta().getDisplayName().equals(name);
    }
}
