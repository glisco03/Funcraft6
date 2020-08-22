package com.glisco.funcraft6.items;

import com.glisco.funcraft6.Main;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CompassMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionType;

import java.util.*;

public class ItemFactory {

    public static ItemStack createCustomPotion(Color color, String name, PotionEffect effect, String itemID, String... lore) {
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
        if (lore[0] != null) {
            List<String> loreList = new ArrayList<>(Arrays.asList(lore));
            potionMeta.setLore(loreList);
        }
        potionMeta.getPersistentDataContainer().set(Main.key("funcraft_itemid"), PersistentDataType.STRING, itemID);
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

    public static ItemStack createTeleportEye(Location l, Player p) {
        ItemStack eye = new ItemStack(Material.ENDER_EYE);
        ItemMeta eyeMeta = eye.getItemMeta();

        PersistentDataContainer eyeData = eyeMeta.getPersistentDataContainer();
        eyeData.set(Main.key("owningPlayer"), PersistentDataType.STRING, p.getUniqueId().toString());
        eyeData.set(Main.key("funcraft_itemid"), PersistentDataType.STRING, "dragoneye");

        int[] location = new int[]{l.getBlockX(), (l.getBlockY() + 1), l.getBlockZ()};
        eyeData.set(Main.key("location"), PersistentDataType.INTEGER_ARRAY, location);
        eyeData.set(Main.key("world"), PersistentDataType.STRING, l.getWorld().getName());

        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add("§7Bound to: §b" + l.getBlockX() + " " + l.getBlockY() + " " + l.getBlockZ());
        lore.add("§7In: §b" + l.getWorld().getName());
        lore.add("");
        lore.add("§7Right-click to teleport yourself (uses 5 levels)");
        lore.add("");
        lore.add("§7Sneak-right-click to teleport all");
        lore.add("§7entities within 3 blocks (uses 30 levels)");
        eyeMeta.setLore(lore);
        eyeMeta.setDisplayName("§cDragon's Eye");

        eye.setItemMeta(eyeMeta);
        eye.addUnsafeEnchantment(Main.glowEnchant, 1);

        return eye;
    }

    public static void setCustomItemID(ItemStack toModify, String itemID) {
        ItemMeta meta = toModify.getItemMeta();
        PersistentDataContainer itemData = meta.getPersistentDataContainer();
        itemData.set(Main.key("funcraft_itemid"), PersistentDataType.STRING, itemID);
        toModify.setItemMeta(meta);
    }

    public static ItemStack createPlayerCompass(Location l, String playerName) {
        ItemStack compass = new ItemStack(Material.COMPASS);
        CompassMeta meta = (CompassMeta) compass.getItemMeta();
        meta.setLodestone(l);
        meta.setLodestoneTracked(false);
        meta.setDisplayName("§rLocked Compass");
        List<String> lore = new ArrayList<>();
        lore.add("§7" + l.getBlockX() + " " + l.getBlockY() + " " + l.getBlockZ() + " [" + l.getWorld().getName() + "]");
        lore.add("");
        lore.add("§7Created for: §b" + playerName);
        meta.setLore(lore);
        compass.setItemMeta(meta);
        setCustomItemID(compass, "player_compass");
        return compass;
    }

    public static void addAttributeModifier(ItemStack toModify, Attribute attribute, double amount, AttributeModifier.Operation operation, EquipmentSlot slot) {
        ItemMeta meta = toModify.getItemMeta();
        meta.addAttributeModifier(attribute, new AttributeModifier(UUID.randomUUID(), attribute.name(), amount, operation, slot));
        toModify.setItemMeta(meta);
    }

    public static void addAttributeModifier(ItemStack toModify, Attribute attribute, double amount, EquipmentSlot slot) {
        ItemMeta meta = toModify.getItemMeta();
        meta.addAttributeModifier(attribute, new AttributeModifier(UUID.randomUUID(), attribute.name(), amount, AttributeModifier.Operation.ADD_NUMBER, slot));
        toModify.setItemMeta(meta);
    }

    public static void setDisplayName(ItemStack toModify, String name) {
        ItemMeta meta = toModify.getItemMeta();
        meta.setDisplayName(name);
        toModify.setItemMeta(meta);
    }

    public static void addGlow(ItemStack toModify) {
        toModify.addUnsafeEnchantment(Main.glowEnchant, 1);
    }

    public static ItemStack createCustomItem(Material material, String name, String customID, boolean glowing) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        item.setItemMeta(meta);
        setCustomItemID(item, customID);
        if (glowing) {
            item.addUnsafeEnchantment(Main.glowEnchant, 1);
        }
        return item;
    }

    public static void addLoreLine(ItemStack toModify, String loreLine) {
        ItemMeta meta = toModify.getItemMeta();
        List<String> lore = meta.getLore();
        if (lore == null) {
            lore = new ArrayList<>();
        }
        lore.add(loreLine);
        meta.setLore(lore);
        toModify.setItemMeta(meta);
    }

    public static void addPersistentData(ItemStack toModify, String key, PersistentDataType type, Object data) {
        ItemMeta meta = toModify.getItemMeta();
        meta.getPersistentDataContainer().set(Main.key(key), type, data);
        toModify.setItemMeta(meta);
    }

    public static void setModelData(ItemStack toModify, Integer data) {
        ItemMeta meta = toModify.getItemMeta();
        meta.setCustomModelData(data);
        toModify.setItemMeta(meta);
    }

    public static List<String> createSingleLineLore(String lore) {
        return new ArrayList<>(Collections.singleton(lore));
    }
}
