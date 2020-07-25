package com.glisco.funcraft6.utils;

import com.glisco.funcraft6.Main;
import com.glisco.funcraft6.enchantments.EnchantmentHelper;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import java.util.UUID;

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
    public static ItemStack DRAGON_WINGS;
    public static ItemStack XP_TOME;
    public static ItemStack EXCAVATOR_PICKAXE;

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
        EnchantmentHelper.addCustomEnchant(AGILE_SWORD, "Pretty Hot I", 1);

        DRAGON_WINGS = new ItemStack(Material.ELYTRA);
        ItemMeta DRAGON_WINGS_META = DRAGON_WINGS.getItemMeta();
        DRAGON_WINGS_META.setDisplayName("§dDraconic Wings");
        DRAGON_WINGS_META.addAttributeModifier(Attribute.GENERIC_ARMOR, new AttributeModifier(UUID.randomUUID(), "minecraft:generic_armor", -20d, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.CHEST));
        DRAGON_WINGS_META.addAttributeModifier(Attribute.GENERIC_MAX_HEALTH, new AttributeModifier(UUID.randomUUID(), "minecraft:generic_max_health", -10d, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.CHEST));
        DRAGON_WINGS.setItemMeta(DRAGON_WINGS_META);

        ShapedRecipe DRAGON_WINGS_RECIPE = new ShapedRecipe(Main.key("DRAGON_WINGS_RECIPE"), DRAGON_WINGS).shape(" h ", "ses", " c ");
        DRAGON_WINGS_RECIPE.setIngredient('h', Material.DRAGON_HEAD);
        DRAGON_WINGS_RECIPE.setIngredient('s', Material.NETHER_STAR);
        DRAGON_WINGS_RECIPE.setIngredient('e', Material.ELYTRA);
        DRAGON_WINGS_RECIPE.setIngredient('c', Material.DRAGON_EGG);
        Bukkit.addRecipe(DRAGON_WINGS_RECIPE);

        XP_TOME = new ItemStack(Material.ENCHANTED_BOOK);
        ItemMeta XP_TOME_META = XP_TOME.getItemMeta();
        XP_TOME_META.setDisplayName("§r§eXP Tome");
        XP_TOME_META.setLore(ItemHelper.createSingleLineLore("§r§70/1395XP Stored"));
        XP_TOME.setItemMeta(XP_TOME_META);

        ShapedRecipe XP_TOME_RECIPE = new ShapedRecipe(Main.key("XP_TOME_RECIPE"), XP_TOME).shape(" e ", "ebe", " e ");
        XP_TOME_RECIPE.setIngredient('e', Material.ENDER_PEARL);
        XP_TOME_RECIPE.setIngredient('b', Material.BOOK);
        Bukkit.addRecipe(XP_TOME_RECIPE);

        EXCAVATOR_PICKAXE = new ItemStack(Material.NETHERITE_PICKAXE);
        ItemMeta EXCAVATOR_PICKAXE_META = EXCAVATOR_PICKAXE.getItemMeta();
        EXCAVATOR_PICKAXE_META.setDisplayName("§8Pickaxe of the Excavator");
        EXCAVATOR_PICKAXE.setItemMeta(EXCAVATOR_PICKAXE_META);
        //TODO add a fking recipe
    }
}
