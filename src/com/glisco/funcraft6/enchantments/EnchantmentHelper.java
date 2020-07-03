package com.glisco.funcraft6.enchantments;

import com.glisco.funcraft6.Main;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;

public class EnchantmentHelper {

    private static ArrayList<CustomEnchantment> enchantments;
    private static HashMap<String, Integer> romanNumerals;

    public EnchantmentHelper() {
        enchantments = new ArrayList<>();
        romanNumerals = new HashMap<>();
        romanNumerals.put("I", 1);
        romanNumerals.put("II", 2);
        romanNumerals.put("III", 3);
        romanNumerals.put("IV", 4);
        romanNumerals.put("V", 5);
        romanNumerals.put("VI", 6);
    }

    public static void registerEnchantment(String name, int maxLevel, Material... validTypes) {
        enchantments.add(new CustomEnchantment(name, maxLevel, validTypes));
    }

    public static CustomEnchantment getEnchatmentFromLore(String lore) {
        for (CustomEnchantment c : enchantments) {
            if (c.isValidLore(lore)) {
                return c;
            }
        }
        return null;
    }

    public static ItemStack addCustomEnchant(ItemStack item, String enchantment, int level) {
        CustomEnchantment c = getEnchatmentFromLore(enchantment);

        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setLore(c.getLore(itemMeta.getLore(), level));
        item.setItemMeta(itemMeta);
        if(item.getEnchantments().isEmpty()){
            enchant(item, Main.glowEnchant, 1);
        }
        return item;
    }

    public static int getLevelFromLore(String lore) {
        String[] level = lore.split(" ");
        for(String s : level){
            if(romanNumerals.keySet().contains(s)){
                return romanNumerals.get(s);
            }
        }
        return -1;
    }

    public static ItemStack combineItems(ItemStack item1, ItemStack item2, ItemStack oldResult) {
        ItemStack result;
        if(oldResult.getType().equals(Material.AIR)){
           result = item1.clone();
        } else {
            result = oldResult.clone();
        }
        HashMap<CustomEnchantment, Integer> enchantments1 = new HashMap<>();
        HashMap<CustomEnchantment, Integer> enchantments2 = new HashMap<>();
        if (item1.getItemMeta().hasLore()) {
            for (String s : item1.getItemMeta().getLore()) {
                enchantments1.put(getEnchatmentFromLore(s), getLevelFromLore(s));
            }
        }
        if (item2.getItemMeta().hasLore()) {
            for (String s : item2.getItemMeta().getLore()) {
                enchantments2.put(getEnchatmentFromLore(s), getLevelFromLore(s));
            }
        }
        if (enchantments1.isEmpty() && enchantments2.isEmpty()) {
            return null;
        }
        if (!enchantments1.isEmpty() && enchantments2.isEmpty()) {
            return null;
        }
        if (enchantments1.isEmpty() && !enchantments2.isEmpty()) {
            ItemMeta resultMeta = result.getItemMeta();
            for (CustomEnchantment c : enchantments2.keySet()) {
                if(!c.getValidItemTypes().contains(item1.getType()) && item1.getType() != Material.ENCHANTED_BOOK){
                    continue;
                }
                resultMeta.setLore(c.getLore(resultMeta.getLore(), enchantments2.get(c)));
            }
            if(!resultMeta.hasLore()){
                return null;
            }
            result.setItemMeta(resultMeta);
            if (result.getEnchantments().isEmpty()) {
                enchant(result, Main.glowEnchant, 1);
            }
            return result;
        }
        if (!enchantments1.isEmpty() && !enchantments2.isEmpty()) {
            if(!item1.getType().equals(item2.getType()) && !item2.getType().equals(Material.ENCHANTED_BOOK)){
                return null;
            }
            ItemMeta resultMeta = result.getItemMeta();
            for (CustomEnchantment c1 : enchantments1.keySet()) {
                for (CustomEnchantment c2 : enchantments2.keySet()) {
                    if(!c2.getValidItemTypes().contains(item1.getType())){
                        continue;
                    }
                    if (c1 == c2) {
                        resultMeta.setLore(c1.getLeveledLore(resultMeta.getLore(), enchantments1.get(c1), enchantments2.get(c2)));
                    } else {
                        resultMeta.setLore(c2.getLore(resultMeta.getLore(), enchantments2.get(c2)));
                    }
                }
            }
            result.setItemMeta(resultMeta);
            if (result.getEnchantments().isEmpty()) {
                enchant(result, Main.glowEnchant, 1);
            }
            return result;
        }

        return result;
    }

    public static void enchant(ItemStack item, Enchantment enchantment, int level) {
        if (item.getType().equals(Material.ENCHANTED_BOOK)) {
            EnchantmentStorageMeta meta = (EnchantmentStorageMeta) item.getItemMeta();
            meta.addStoredEnchant(enchantment, level, false);
            item.setItemMeta(meta);
        } else {
            item.addUnsafeEnchantment(enchantment, level);
        }
    }
}
