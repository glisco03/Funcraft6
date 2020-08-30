package com.glisco.funcraft6.items;

import com.glisco.funcraft6.Main;
import com.glisco.funcraft6.enchantments.EnchantmentHelper;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import java.util.HashMap;

public class FuncraftItems {

    public static ItemStack RECALL_POTION;
    public static ItemStack RECALL_TOTEM;
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
    public static ItemStack EXCAVATOR_HAMMER;
    public static ItemStack HARDENING_CRYSTAL;
    public static ItemStack FERTILIZER;

    public static ItemStack ENDER_QUEEN_PEARL;
    public static ItemStack QUEEN_SCALE;
    public static ItemStack DRAGON_SWORD;
    public static ItemStack DRAGON_CHESTPLATE;
    public static ItemStack DRAGON_HELMET;
    public static ItemStack DRAGON_BOOTS;
    public static ItemStack DRAGON_LEGGINGS;

    public static HashMap<ItemStack[], ItemStack> NBTrecipes;

    public FuncraftItems() {
        RECALL_POTION = ItemFactory.createCustomPotion(Color.fromBGR(255, 255, 122), "§bRecall Potion", null, "recall_potion", "§7§oBasically suicide, but safer");
        UNBOUND_WARP_POTION = ItemFactory.createCustomPotion(Color.fromBGR(243, 247, 230), "§7Unbound Warp Potion", null, "unbound_warp_potion", "§7§oSocialising is hard");
        ACCEPTANCE_POTION = ItemFactory.createCustomPotion(Color.fromBGR(84, 251, 84), "§aPotion of Acceptance", new PotionEffect(PotionEffectType.LUCK, 300, 0), "acceptance_potion", (String) null);

        COFFEE = ItemFactory.createCustomItem(Material.POTION, "§fCoffee", "coffee", false);
        PotionMeta COFFEE_META = (PotionMeta) COFFEE.getItemMeta();
        COFFEE_META.setColor(Color.fromBGR(19, 69, 139));
        COFFEE_META.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
        COFFEE.setItemMeta(COFFEE_META);

        WATER_POTION = ItemFactory.createPotion(PotionType.WATER, false, false);
        AWKWARD_POTION = ItemFactory.createPotion(PotionType.AWKWARD, false, false);
        REGEN_POTION = ItemFactory.createPotion(PotionType.REGEN, false, true);

        HASTE_POTION = ItemFactory.createCustomPotion(Color.YELLOW, "§rPotion of Haste", new PotionEffect(PotionEffectType.FAST_DIGGING, 9600, 0), "haste_potion", (String) null);
        HASTE_II_POTION = ItemFactory.createCustomPotion(Color.YELLOW, "§rPotion of Haste", new PotionEffect(PotionEffectType.FAST_DIGGING, 2400, 1), "haste_ii_potion", (String) null);

        AGILE_SWORD = new ItemStack(Material.ENCHANTED_BOOK);
        EnchantmentHelper.addCustomEnchant(AGILE_SWORD, "Lifesteal I", 1);

        DRAGON_WINGS = ItemFactory.createCustomItem(Material.ELYTRA, "§dDraconic Wings", "dragon_wings", false);
        ItemFactory.addAttributeModifier(DRAGON_WINGS, Attribute.GENERIC_ARMOR, -20d, EquipmentSlot.CHEST);
        ItemFactory.addAttributeModifier(DRAGON_WINGS, Attribute.GENERIC_MAX_HEALTH, -10d, EquipmentSlot.CHEST);

        ShapedRecipe DRAGON_WINGS_RECIPE = new ShapedRecipe(Main.key("DRAGON_WINGS_RECIPE"), DRAGON_WINGS).shape(" h ", "ses", " c ");
        DRAGON_WINGS_RECIPE.setIngredient('h', Material.DRAGON_HEAD);
        DRAGON_WINGS_RECIPE.setIngredient('s', Material.NETHER_STAR);
        DRAGON_WINGS_RECIPE.setIngredient('e', Material.ELYTRA);
        DRAGON_WINGS_RECIPE.setIngredient('c', Material.DRAGON_EGG);
        Bukkit.addRecipe(DRAGON_WINGS_RECIPE);

        XP_TOME = ItemFactory.createCustomItem(Material.CARROT_ON_A_STICK, "§eXP Tome", "xptome", false);
        ItemFactory.addLoreLine(XP_TOME, "§r§70/1395XP Stored");
        ItemFactory.setModelData(XP_TOME, 1);
        ItemFactory.setDamage(XP_TOME, 25);

        ShapedRecipe XP_TOME_RECIPE = new ShapedRecipe(Main.key("XP_TOME_RECIPE"), XP_TOME).shape(" e ", "ebe", " e ");
        XP_TOME_RECIPE.setIngredient('e', Material.ENDER_PEARL);
        XP_TOME_RECIPE.setIngredient('b', Material.BOOK);
        Bukkit.addRecipe(XP_TOME_RECIPE);

        EXCAVATOR_HAMMER = ItemFactory.createCustomItem(Material.NETHERITE_PICKAXE, "§8Hammer of the Excavator", "excavator_hammer", false);
        ItemFactory.setModelData(EXCAVATOR_HAMMER, 1);

        ShapedRecipe EXCAVATOR_HAMMER_RECIPE = new ShapedRecipe(Main.key("EXCAVATOR_HAMMER_RECIPE"), EXCAVATOR_HAMMER).shape("nnn", "nen", " s ");
        EXCAVATOR_HAMMER_RECIPE.setIngredient('n', Material.NETHERITE_INGOT);
        EXCAVATOR_HAMMER_RECIPE.setIngredient('e', Material.DRAGON_EGG);
        EXCAVATOR_HAMMER_RECIPE.setIngredient('s', Material.STICK);
        Bukkit.addRecipe(EXCAVATOR_HAMMER_RECIPE);

        HARDENING_CRYSTAL = ItemFactory.createCustomItem(Material.NETHER_STAR, "§bHardening Crystal", "hardening_crystal", false);
        ItemFactory.addLoreLine(HARDENING_CRYSTAL, "§7Apply to any item to make it unbreakable");

        ShapedRecipe HARDENING_CRYSTAL_RECIPE = new ShapedRecipe(Main.key("HARDENING_CRYSTAL_RECIPE"), HARDENING_CRYSTAL).shape("ono", "dad", "sds");
        HARDENING_CRYSTAL_RECIPE.setIngredient('a', Material.NETHER_STAR);
        HARDENING_CRYSTAL_RECIPE.setIngredient('n', Material.NETHERITE_INGOT);
        HARDENING_CRYSTAL_RECIPE.setIngredient('d', Material.DIAMOND_BLOCK);
        HARDENING_CRYSTAL_RECIPE.setIngredient('s', Material.SHULKER_SHELL);
        HARDENING_CRYSTAL_RECIPE.setIngredient('o', Material.OBSIDIAN);
        Bukkit.addRecipe(HARDENING_CRYSTAL_RECIPE);

        RECALL_TOTEM = ItemFactory.createCustomItem(Material.TOTEM_OF_UNDYING, "§eRecalling Totem of Undying", "recall_totem", true);
        ItemFactory.setModelData(RECALL_TOTEM, 1);

        ENDER_QUEEN_PEARL = ItemFactory.createCustomItem(Material.ENDER_PEARL, "§5Ender Queen Pearl", "queen_pearl", true);
        ItemFactory.addLoreLine(ENDER_QUEEN_PEARL, "§7Infinite ender pearl");
        ItemFactory.addLoreLine(ENDER_QUEEN_PEARL, "§7that does not deal damage");

        QUEEN_SCALE = ItemFactory.createCustomItem(Material.PRISMARINE_SHARD, "§5Ender Queen Scale", "queen_scale", true);
        ItemFactory.setModelData(QUEEN_SCALE, 1);

        DRAGON_SWORD = ItemFactory.createCustomItem(Material.IRON_SWORD, "§5Majestic Reaping Sword", "dragon_sword", true);
        ItemFactory.addLoreLine(DRAGON_SWORD, "§7Forged using the powers of a dragon");
        ItemFactory.addLoreLine(DRAGON_SWORD, "§7queen, this sword allows one to");
        ItemFactory.addLoreLine(DRAGON_SWORD, "§7extract a special gift from endermen");
        ItemFactory.setModelData(DRAGON_SWORD, 1);

        DRAGON_HELMET = ItemFactory.createCustomItem(Material.IRON_HELMET, "§5Dragon Helmet", "dragon_helmet", true);
        ItemFactory.addAttributeModifier(DRAGON_HELMET, Attribute.GENERIC_ARMOR, 4, EquipmentSlot.HEAD);
        ItemFactory.addAttributeModifier(DRAGON_HELMET, Attribute.GENERIC_ARMOR_TOUGHNESS, 4, EquipmentSlot.HEAD);
        ItemFactory.addAttributeModifier(DRAGON_HELMET, Attribute.GENERIC_KNOCKBACK_RESISTANCE, 0.2, EquipmentSlot.HEAD);
        ItemFactory.addPersistentData(DRAGON_HELMET, "night_vision", PersistentDataType.INTEGER, 0);
        ItemFactory.setModelData(DRAGON_HELMET, 1);

        DRAGON_CHESTPLATE = ItemFactory.createCustomItem(Material.IRON_CHESTPLATE, "§5Dragon Chestplate", "dragon_chestplate", true);
        ItemFactory.addAttributeModifier(DRAGON_CHESTPLATE, Attribute.GENERIC_ARMOR, 10, EquipmentSlot.CHEST);
        ItemFactory.addAttributeModifier(DRAGON_CHESTPLATE, Attribute.GENERIC_ARMOR_TOUGHNESS, 4, EquipmentSlot.CHEST);
        ItemFactory.addAttributeModifier(DRAGON_CHESTPLATE, Attribute.GENERIC_KNOCKBACK_RESISTANCE, 0.2, EquipmentSlot.CHEST);
        ItemFactory.addPersistentData(DRAGON_CHESTPLATE, "flight_speed", PersistentDataType.INTEGER, 1);
        ItemFactory.setModelData(DRAGON_CHESTPLATE, 1);

        DRAGON_LEGGINGS = ItemFactory.createCustomItem(Material.IRON_LEGGINGS, "§5Dragon Leggings", "dragon_leggings", true);
        ItemFactory.addAttributeModifier(DRAGON_LEGGINGS, Attribute.GENERIC_ARMOR, 8, EquipmentSlot.LEGS);
        ItemFactory.addAttributeModifier(DRAGON_LEGGINGS, Attribute.GENERIC_ARMOR_TOUGHNESS, 4, EquipmentSlot.LEGS);
        ItemFactory.addAttributeModifier(DRAGON_LEGGINGS, Attribute.GENERIC_KNOCKBACK_RESISTANCE, 0.2, EquipmentSlot.LEGS);
        ItemFactory.setModelData(DRAGON_LEGGINGS, 1);

        DRAGON_BOOTS = ItemFactory.createCustomItem(Material.IRON_BOOTS, "§5Dragon Boots", "dragon_boots", true);
        ItemFactory.addAttributeModifier(DRAGON_BOOTS, Attribute.GENERIC_ARMOR, 4, EquipmentSlot.FEET);
        ItemFactory.addAttributeModifier(DRAGON_BOOTS, Attribute.GENERIC_ARMOR_TOUGHNESS, 4, EquipmentSlot.FEET);
        ItemFactory.addAttributeModifier(DRAGON_BOOTS, Attribute.GENERIC_KNOCKBACK_RESISTANCE, 0.2, EquipmentSlot.FEET);
        ItemFactory.setModelData(DRAGON_BOOTS, 1);

        FERTILIZER = ItemFactory.createCustomItem(Material.BONE_MEAL, "§eFertilizer", "fertilizer", true);
        FERTILIZER.setAmount(8);

        ShapedRecipe NAME_TAG_RECIPE = new ShapedRecipe(Main.key("NAME_TAG_RECIPE"), new ItemStack(Material.NAME_TAG)).shape(" ps", "pbp", "lp ");
        NAME_TAG_RECIPE.setIngredient('p', Material.PAPER);
        NAME_TAG_RECIPE.setIngredient('b', Material.SLIME_BALL);
        NAME_TAG_RECIPE.setIngredient('s', Material.STRING);
        NAME_TAG_RECIPE.setIngredient('l', Material.LEATHER);
        Bukkit.addRecipe(NAME_TAG_RECIPE);

        NBTrecipes = new HashMap<>();
        NBTrecipes.put(new ItemStack[]{FuncraftItems.QUEEN_SCALE, FuncraftItems.QUEEN_SCALE, FuncraftItems.QUEEN_SCALE, FuncraftItems.QUEEN_SCALE, null, FuncraftItems.QUEEN_SCALE, null, null, null}, DRAGON_HELMET);
        NBTrecipes.put(new ItemStack[]{FuncraftItems.QUEEN_SCALE, FuncraftItems.QUEEN_SCALE, FuncraftItems.QUEEN_SCALE, FuncraftItems.QUEEN_SCALE, null, FuncraftItems.QUEEN_SCALE, FuncraftItems.QUEEN_SCALE, null, FuncraftItems.QUEEN_SCALE}, DRAGON_LEGGINGS);
        NBTrecipes.put(new ItemStack[]{FuncraftItems.QUEEN_SCALE, null, FuncraftItems.QUEEN_SCALE, FuncraftItems.QUEEN_SCALE, FuncraftItems.QUEEN_SCALE, FuncraftItems.QUEEN_SCALE, FuncraftItems.QUEEN_SCALE, FuncraftItems.QUEEN_SCALE, FuncraftItems.QUEEN_SCALE}, DRAGON_CHESTPLATE);
        NBTrecipes.put(new ItemStack[]{null, null, null, FuncraftItems.QUEEN_SCALE, null, FuncraftItems.QUEEN_SCALE, FuncraftItems.QUEEN_SCALE, null, FuncraftItems.QUEEN_SCALE}, DRAGON_BOOTS);

    }
}
