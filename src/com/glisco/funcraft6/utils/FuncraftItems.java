package com.glisco.funcraft6.utils;

import com.glisco.funcraft6.Main;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import java.util.ArrayList;
import java.util.List;

public class FuncraftItems {

    public static ItemStack RECALL_POTION;
    public static ItemStack REGEN_POTION;
    public static ItemStack HASTE_POTION;
    public static ItemStack HASTE_II_POTION;
    public static ItemStack AWKWARD_POTION;
    public static ItemStack AGILE_SWORD;

    public FuncraftItems() {
        RECALL_POTION = new ItemStack(Material.POTION);
        PotionMeta RECALL_POTION_META = (PotionMeta) RECALL_POTION.getItemMeta();
        RECALL_POTION_META.setColor(Color.fromBGR(255, 255, 122));
        RECALL_POTION_META.setDisplayName("§bRecall Potion");
        RECALL_POTION_META.addEnchant(Enchantment.DEPTH_STRIDER, 1, false);
        RECALL_POTION_META.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        RECALL_POTION_META.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
        List<String> recallLore = new ArrayList<>();
        recallLore.add("§7§oBasically suicide, but safer");
        RECALL_POTION_META.setLore(recallLore);
        RECALL_POTION.setItemMeta(RECALL_POTION_META);

        AWKWARD_POTION = new ItemStack(Material.POTION);
        PotionMeta AWKWARD_POTION_META = (PotionMeta) AWKWARD_POTION.getItemMeta();
        PotionData AWKWARD_META = new PotionData(PotionType.AWKWARD, false, false);
        AWKWARD_POTION_META.setBasePotionData(AWKWARD_META);
        AWKWARD_POTION.setItemMeta(AWKWARD_POTION_META);

        REGEN_POTION = new ItemStack(Material.POTION);
        PotionMeta REGEN_POTION_META = (PotionMeta) REGEN_POTION.getItemMeta();
        PotionData REGEN_DATA = new PotionData(PotionType.REGEN, false, true);
        REGEN_POTION_META.setBasePotionData(REGEN_DATA);
        REGEN_POTION.setItemMeta(REGEN_POTION_META);

        HASTE_POTION = new ItemStack(Material.POTION);
        PotionMeta HASTE_POTION_META = (PotionMeta) HASTE_POTION.getItemMeta();
        HASTE_POTION_META.setColor(Color.YELLOW);
        HASTE_POTION_META.setDisplayName("§rPotion of Haste");
        HASTE_POTION_META.addCustomEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 9600, 0), false);
        HASTE_POTION.setItemMeta(HASTE_POTION_META);

        HASTE_II_POTION = new ItemStack(Material.POTION);
        PotionMeta HASTE_II_POTION_META = (PotionMeta) HASTE_II_POTION.getItemMeta();
        HASTE_II_POTION_META.setColor(Color.YELLOW);
        HASTE_II_POTION_META.setDisplayName("§rPotion of Haste");
        HASTE_II_POTION_META.addCustomEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 2400, 1), false);
        HASTE_II_POTION.setItemMeta(HASTE_II_POTION_META);

        AGILE_SWORD = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta AGILE_SWORD_META = AGILE_SWORD.getItemMeta();
        List<String> swordLore = new ArrayList<>();
        swordLore.add("§7Agility III");
        AGILE_SWORD_META.setLore(swordLore);
        AGILE_SWORD.setItemMeta(AGILE_SWORD_META);
        AGILE_SWORD.addUnsafeEnchantment(Main.glowEnchant, 1);
    }
}
