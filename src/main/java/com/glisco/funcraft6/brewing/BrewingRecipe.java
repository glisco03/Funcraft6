package com.glisco.funcraft6.brewing;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class BrewingRecipe {

    private final Material ingredient;
    private final ItemStack inputPotions;
    private final ItemStack outputPotions;

    public BrewingRecipe(Material input, ItemStack inPotions, ItemStack outPotions) {
        ingredient = input;
        inputPotions = inPotions;
        outputPotions = outPotions;
    }

    public Material getIngredient() {
        return ingredient;
    }

    public ItemStack getInputPotions() {
        return inputPotions;
    }

    public ItemStack getOutputPotions() {
        return outputPotions;
    }
}
