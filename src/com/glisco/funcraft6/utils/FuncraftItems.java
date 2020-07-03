package com.glisco.funcraft6.utils;

import com.glisco.funcraft6.enchantments.EnchantmentHelper;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

public class FuncraftItems {

    public static ItemStack RECALL_POTION;
    public static ItemStack UNBOUND_WARP_POTION;
    public static ItemStack ACCEPTANCE_POTION;
    public static ItemStack COFFEE;
    public static ItemStack REGEN_POTION;
    public static ItemStack HASTE_POTION;
    public static ItemStack HASTE_II_POTION;
    public static ItemStack AWKWARD_POTION;
    public static ItemStack WATER_POTION;
    public static ItemStack AGILE_SWORD;

    public FuncraftItems() {
        RECALL_POTION = ItemHelper.createCustomPotion(Color.fromBGR(255, 255, 122), "§bRecall Potion", null, "§7§oBasically suicide, but safer");
        UNBOUND_WARP_POTION = ItemHelper.createCustomPotion(Color.fromBGR(243, 247, 230), "§7Unbound Warp Potion", null, "§7§oSocialising is hard");
        ACCEPTANCE_POTION = ItemHelper.createCustomPotion(Color.fromBGR(84, 251, 84), "§aPotion of Acceptance", new PotionEffect(PotionEffectType.LUCK, 300, 0), (String) null);

        COFFEE = new ItemStack(Material.POTION);
        PotionMeta COFFEE_META = (PotionMeta) COFFEE.getItemMeta();
        COFFEE_META.setColor(Color.fromBGR(19, 69, 139));
        COFFEE_META.setDisplayName("§fCoffee");
        COFFEE_META.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
        COFFEE.setItemMeta(COFFEE_META);

        WATER_POTION = ItemHelper.createPotion(PotionType.WATER, false, false);
        AWKWARD_POTION = ItemHelper.createPotion(PotionType.AWKWARD, false, false);
        REGEN_POTION = ItemHelper.createPotion(PotionType.REGEN, false, true);

        HASTE_POTION = ItemHelper.createCustomPotion(Color.YELLOW, "§rPotion of Haste", new PotionEffect(PotionEffectType.FAST_DIGGING, 9600, 0), (String) null);
        HASTE_II_POTION = ItemHelper.createCustomPotion(Color.YELLOW, "§rPotion of Haste", new PotionEffect(PotionEffectType.FAST_DIGGING, 2400, 1), (String) null);

        AGILE_SWORD = new ItemStack(Material.ENCHANTED_BOOK);
        EnchantmentHelper.addCustomEnchant(AGILE_SWORD, "Air Hopper I", 1);
    }
}
