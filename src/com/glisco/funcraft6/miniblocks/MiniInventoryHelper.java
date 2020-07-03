package com.glisco.funcraft6.miniblocks;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Merchant;
import org.bukkit.inventory.MerchantRecipe;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class MiniInventoryHelper {

    public static void openMerchantInventory(Player p, Class<?> blocks) {
        Merchant inv = Bukkit.createMerchant("§b" + blocks.getSimpleName().replace("_", " ") + " Miniblocks");
        List<MerchantRecipe> recipes = new ArrayList<>();
        for (Field f : blocks.getFields()) {
            ItemStack ingredient = new ItemStack(Material.valueOf(f.getName()));
            ItemStack result = null;
            try {
                result = (ItemStack) f.get(blocks);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            switch (f.getName()) {
                case "HONEY_BOTTLE":
                    result.setAmount(1);
                    break;
                case "SWEET_BERRY":
                    result.setAmount(1);
                    break;
                case "APPLE":
                    result.setAmount(1);
                    break;
                case "WATER_BUCKET":
                    result.setAmount(1);
                    break;
                case "LAVA_BUCKET":
                    result.setAmount(1);
                    break;
                case "MILK_BUCKET":
                    result.setAmount(1);
                    break;
                default:
                    result.setAmount(8);
            }
            MerchantRecipe recipe = new MerchantRecipe(result, 666);
            List<ItemStack> ingredients = new ArrayList<>();
            ingredients.add(new ItemStack(Material.EMERALD));
            ingredients.add(ingredient);
            recipe.setIngredients(ingredients);
            recipes.add(recipe);
        }
        inv.setRecipes(recipes);
        p.openMerchant(inv, true);
    }
}
