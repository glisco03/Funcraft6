package com.glisco.funcraft6.brewing;

import com.glisco.funcraft6.utils.GlobalVars;
import org.bukkit.Material;
import org.bukkit.inventory.BrewerInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;

public class BrewingHelper {

    private static ArrayList<BrewingRecipe> validRecipes;
    static JavaPlugin p;

    public BrewingHelper(JavaPlugin pl) {
        p = pl;
        validRecipes = new ArrayList<>();
    }

    public static void registerRecipe(BrewingRecipe recipe) {
        validRecipes.add(recipe);
    }

    public static void validateRecipe(BrewerInventory brewer) {
        for (BrewingRecipe recipe : validRecipes) {
            if (brewer.getIngredient().getType().equals(recipe.getIngredient()) && Arrays.asList(brewer.getStorageContents()).contains(recipe.getInputPotions())) {
                if (!GlobalVars.runningStands.containsKey(brewer.getHolder().getBlock())) {
                    BrewClock clock = new BrewClock(brewer.getHolder().getBlock(), recipe, p);
                    GlobalVars.runningStands.put(brewer.getHolder().getBlock(), clock);
                }
                return;
            }
        }
    }

    public static ArrayList<Material> getValidIngredients() {
        ArrayList<Material> validIngredients = new ArrayList<>();
        for (BrewingRecipe recipe : validRecipes) {
            validIngredients.add(recipe.getIngredient());
        }
        return validIngredients;
    }

    public static ArrayList<ItemStack> getValidInputPotions() {
        ArrayList<ItemStack> validInputPotions = new ArrayList<>();
        for (BrewingRecipe recipe : validRecipes) {
            validInputPotions.add(recipe.getInputPotions());
        }
        return validInputPotions;
    }
}
