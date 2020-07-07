package com.glisco.funcraft6.utils;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ItemHelper {

    public static ItemStack createCustomPotion(Color color, String name, PotionEffect effect, String... lore) {
        ItemStack potion = new ItemStack(Material.POTION);
        PotionMeta potionMeta = (PotionMeta) potion.getItemMeta();
        potionMeta.setColor(color);
        potionMeta.setDisplayName(name);
        if (effect == null) {
            potionMeta.addEnchant(Enchantment.DEPTH_STRIDER, 1, false);
            potionMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_POTION_EFFECTS);
        } else {
            potionMeta.addCustomEffect(effect, false);
        }
        if (lore != null) {
            List<String> loreList = new ArrayList<>(Arrays.asList(lore));
            potionMeta.setLore(loreList);
        }
        potion.setItemMeta(potionMeta);
        return potion;
    }

    public static ItemStack createPotion(PotionType potionType, Boolean extended, Boolean upgraded) {
        ItemStack potion = new ItemStack(Material.POTION);
        PotionMeta potionMeta = (PotionMeta) potion.getItemMeta();
        PotionData potionData = new PotionData(potionType, extended, upgraded);
        potionMeta.setBasePotionData(potionData);
        potion.setItemMeta(potionMeta);
        return potion;
    }

    public static List<String> createSingleLineLore(String lore){
        return new ArrayList<>(Collections.singleton(lore));
    }
}
