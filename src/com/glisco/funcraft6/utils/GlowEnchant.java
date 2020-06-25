package com.glisco.funcraft6.utils;

import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.inventory.ItemStack;

public class GlowEnchant extends Enchantment {

    public GlowEnchant(NamespacedKey key) {
        super(key);
    }

    @Override
    public boolean canEnchantItem(ItemStack item) {
        return true; //can it enchant items?
    }

    @Override
    public boolean conflictsWith(Enchantment other) {
        return false; //will it conflict? In this case, nope
    }

    @Override
    public EnchantmentTarget getItemTarget() {
        return null;//you can define a target here, I just setted it to null...
    }

    @Override
    public int getMaxLevel() {
        return 3; //the maxmimum level.
    }

    @Override
    public String getName() {
        return "Agility"; //the name
    }

    @Override
    public boolean isTreasure() {
        return true;
    }

    @Override
    public boolean isCursed() {
        return false;
    }

    @Override
    public int getStartLevel() {
        return 1; //the start level of your enchantment
    }

}