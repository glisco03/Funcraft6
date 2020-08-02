package com.glisco.funcraft6.items;

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
    public static ItemStack EXCAVATOR_PICKAXE;
    public static ItemStack HARDENING_CRYSTAL;

    public FuncraftItems() {
        RECALL_POTION = ItemFactory.createCustomPotion(Color.fromBGR(255, 255, 122), "§bRecall Potion", null, "recall_potion", "§7§oBasically suicide, but safer");
        UNBOUND_WARP_POTION = ItemFactory.createCustomPotion(Color.fromBGR(243, 247, 230), "§7Unbound Warp Potion", null, "unbound_warp_potion", "§7§oSocialising is hard");
        ACCEPTANCE_POTION = ItemFactory.createCustomPotion(Color.fromBGR(84, 251, 84), "§aPotion of Acceptance", new PotionEffect(PotionEffectType.LUCK, 300, 0), "acceptance_potion", (String) null);

        COFFEE = new ItemStack(Material.POTION);
        PotionMeta COFFEE_META = (PotionMeta) COFFEE.getItemMeta();
        COFFEE_META.setColor(Color.fromBGR(19, 69, 139));
        COFFEE_META.setDisplayName("§fCoffee");
        COFFEE_META.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
        COFFEE.setItemMeta(COFFEE_META);
        ItemFactory.addCustomItemID(COFFEE, "coffee");

        WATER_POTION = ItemFactory.createPotion(PotionType.WATER, false, false);
        AWKWARD_POTION = ItemFactory.createPotion(PotionType.AWKWARD, false, false);
        REGEN_POTION = ItemFactory.createPotion(PotionType.REGEN, false, true);

        HASTE_POTION = ItemFactory.createCustomPotion(Color.YELLOW, "§rPotion of Haste", new PotionEffect(PotionEffectType.FAST_DIGGING, 9600, 0), "haste_potion", (String) null);
        HASTE_II_POTION = ItemFactory.createCustomPotion(Color.YELLOW, "§rPotion of Haste", new PotionEffect(PotionEffectType.FAST_DIGGING, 2400, 1), "haste_ii_potion", (String) null);

        AGILE_SWORD = new ItemStack(Material.ENCHANTED_BOOK);
        EnchantmentHelper.addCustomEnchant(AGILE_SWORD, "Lifesteal I", 1);

        DRAGON_WINGS = new ItemStack(Material.ELYTRA);
        ItemMeta DRAGON_WINGS_META = DRAGON_WINGS.getItemMeta();
        DRAGON_WINGS_META.setDisplayName("§dDraconic Wings");
        DRAGON_WINGS_META.addAttributeModifier(Attribute.GENERIC_ARMOR, new AttributeModifier(UUID.randomUUID(), "minecraft:generic_armor", -20d, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.CHEST));
        DRAGON_WINGS_META.addAttributeModifier(Attribute.GENERIC_MAX_HEALTH, new AttributeModifier(UUID.randomUUID(), "minecraft:generic_max_health", -10d, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlot.CHEST));
        DRAGON_WINGS.setItemMeta(DRAGON_WINGS_META);
        ItemFactory.addCustomItemID(DRAGON_WINGS, "dragon_wings");

        ShapedRecipe DRAGON_WINGS_RECIPE = new ShapedRecipe(Main.key("DRAGON_WINGS_RECIPE"), DRAGON_WINGS).shape(" h ", "ses", " c ");
        DRAGON_WINGS_RECIPE.setIngredient('h', Material.DRAGON_HEAD);
        DRAGON_WINGS_RECIPE.setIngredient('s', Material.NETHER_STAR);
        DRAGON_WINGS_RECIPE.setIngredient('e', Material.ELYTRA);
        DRAGON_WINGS_RECIPE.setIngredient('c', Material.DRAGON_EGG);
        Bukkit.addRecipe(DRAGON_WINGS_RECIPE);

        XP_TOME = new ItemStack(Material.KNOWLEDGE_BOOK);
        ItemMeta XP_TOME_META = XP_TOME.getItemMeta();
        XP_TOME_META.setDisplayName("§r§eXP Tome");
        XP_TOME_META.setLore(ItemFactory.createSingleLineLore("§r§70/1395XP Stored"));
        XP_TOME.setItemMeta(XP_TOME_META);
        ItemFactory.addCustomItemID(XP_TOME, "xptome");

        ShapedRecipe XP_TOME_RECIPE = new ShapedRecipe(Main.key("XP_TOME_RECIPE"), XP_TOME).shape(" e ", "ebe", " e ");
        XP_TOME_RECIPE.setIngredient('e', Material.ENDER_PEARL);
        XP_TOME_RECIPE.setIngredient('b', Material.BOOK);
        Bukkit.addRecipe(XP_TOME_RECIPE);

        EXCAVATOR_PICKAXE = new ItemStack(Material.NETHERITE_PICKAXE);
        ItemMeta EXCAVATOR_PICKAXE_META = EXCAVATOR_PICKAXE.getItemMeta();
        EXCAVATOR_PICKAXE_META.setDisplayName("§8Pickaxe of the Excavator");
        EXCAVATOR_PICKAXE.setItemMeta(EXCAVATOR_PICKAXE_META);
        ItemFactory.addCustomItemID(EXCAVATOR_PICKAXE, "excavator_pickaxe");

        ShapedRecipe EXCAVATOR_PICKAXE_RECIPE = new ShapedRecipe(Main.key("EXCAVATOR_PICKAXE_RECIPE"), EXCAVATOR_PICKAXE).shape("cad", "epo", "ngs");
        EXCAVATOR_PICKAXE_RECIPE.setIngredient('c', Material.COBBLESTONE);
        EXCAVATOR_PICKAXE_RECIPE.setIngredient('a', Material.NETHER_STAR);
        EXCAVATOR_PICKAXE_RECIPE.setIngredient('d', Material.DIRT);
        EXCAVATOR_PICKAXE_RECIPE.setIngredient('e', Material.END_STONE);
        EXCAVATOR_PICKAXE_RECIPE.setIngredient('p', Material.NETHERITE_PICKAXE);
        EXCAVATOR_PICKAXE_RECIPE.setIngredient('o', Material.OBSIDIAN);
        EXCAVATOR_PICKAXE_RECIPE.setIngredient('n', Material.NETHERRACK);
        EXCAVATOR_PICKAXE_RECIPE.setIngredient('g', Material.DRAGON_EGG);
        EXCAVATOR_PICKAXE_RECIPE.setIngredient('s', Material.STONE);
        Bukkit.addRecipe(EXCAVATOR_PICKAXE_RECIPE);

        HARDENING_CRYSTAL = new ItemStack(Material.NETHER_STAR);
        ItemMeta HARDENING_CRYSTAL_META = HARDENING_CRYSTAL.getItemMeta();
        HARDENING_CRYSTAL_META.setDisplayName("§bHardening Crystal");
        HARDENING_CRYSTAL_META.setLore(ItemFactory.createSingleLineLore("§r§7Apply to any item to make it unbreakable"));
        HARDENING_CRYSTAL.setItemMeta(HARDENING_CRYSTAL_META);
        ItemFactory.addCustomItemID(HARDENING_CRYSTAL, "hardening_crystal");

        ShapedRecipe HARDENING_CRYSTAL_RECIPE = new ShapedRecipe(Main.key("HARDENING_CRYSTAL_RECIPE"), HARDENING_CRYSTAL).shape("ono", "dad", "sds");
        HARDENING_CRYSTAL_RECIPE.setIngredient('a', Material.NETHER_STAR);
        HARDENING_CRYSTAL_RECIPE.setIngredient('n', Material.NETHERITE_INGOT);
        HARDENING_CRYSTAL_RECIPE.setIngredient('d', Material.DIAMOND_BLOCK);
        HARDENING_CRYSTAL_RECIPE.setIngredient('s', Material.SHULKER_SHELL);
        HARDENING_CRYSTAL_RECIPE.setIngredient('o', Material.OBSIDIAN);
        Bukkit.addRecipe(HARDENING_CRYSTAL_RECIPE);

        RECALL_TOTEM = new ItemStack(Material.TOTEM_OF_UNDYING);
        RECALL_TOTEM.addUnsafeEnchantment(Main.glowEnchant, 1);
        ItemMeta RECALL_TOTEM_META = RECALL_TOTEM.getItemMeta();
        RECALL_TOTEM_META.setDisplayName("§eRecalling Totem of Undying");
        RECALL_TOTEM.setItemMeta(RECALL_TOTEM_META);
        ItemFactory.addCustomItemID(RECALL_TOTEM, "recall_totem");

        ShapedRecipe NAME_TAG_RECIPE = new ShapedRecipe(Main.key("NAME_TAG_RECIPE"), new ItemStack(Material.NAME_TAG)).shape(" ps", "pbp", "lp ");
        NAME_TAG_RECIPE.setIngredient('p', Material.PAPER);
        NAME_TAG_RECIPE.setIngredient('b', Material.SLIME_BALL);
        NAME_TAG_RECIPE.setIngredient('s', Material.STRING);
        NAME_TAG_RECIPE.setIngredient('l', Material.LEATHER);
        Bukkit.addRecipe(NAME_TAG_RECIPE);

    }
}
