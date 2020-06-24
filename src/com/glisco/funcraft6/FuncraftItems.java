package com.glisco.funcraft6;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

public class FuncraftItems {

    public static ItemStack RECALL_POTION;
    public static ItemStack REGEN_POTION;

    @SuppressWarnings("deprecation")
    public FuncraftItems() {
        RECALL_POTION = new ItemStack(Material.POTION);
        PotionMeta RECALL_POTION_META = (PotionMeta) RECALL_POTION.getItemMeta();
        RECALL_POTION_META.setColor(Color.fromBGR(255, 255, 122));
        RECALL_POTION_META.setDisplayName("§bRecall Potion");
        RECALL_POTION_META.addEnchant(Enchantment.DEPTH_STRIDER, 1, false);
        RECALL_POTION_META.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        RECALL_POTION.setItemMeta(RECALL_POTION_META);

        REGEN_POTION = new ItemStack(Material.POTION);
        PotionMeta REGEN_POTION_META = (PotionMeta) REGEN_POTION.getItemMeta();
        PotionData REGEN_DATA = new PotionData(PotionType.REGEN, false, true);
        REGEN_POTION_META.setBasePotionData(REGEN_DATA);
        REGEN_POTION.setItemMeta(REGEN_POTION_META);
    }
}
